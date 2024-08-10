package io.github.thunkware.vt.bridge;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Executor that limits concurrency to a number of semaphore permits
 *
 * @since 0.05
 */
public class SemaphoreExecutor implements ExecutorService {

    private final ExecutorService delegate;
    private final Semaphore semaphore;
    private final SemaphoreAquireStrategy acquireStrategy;

    public SemaphoreExecutor(ExecutorService delegate, int permits) {
        this(delegate, new Semaphore(permits, true));
    }

    public SemaphoreExecutor(ExecutorService delegate, Semaphore semaphore) {
        this.delegate = delegate;
        this.semaphore = semaphore;
        this.acquireStrategy = semaphore::acquire;
    }

    public SemaphoreExecutor(ExecutorService delegate, int permits, Duration acquireTimeout) {
        this(delegate, new Semaphore(permits, true), acquireTimeout);
    }

    public SemaphoreExecutor(ExecutorService delegate, Semaphore semaphore, Duration acquireTimeout) {
        this.delegate = delegate;
        this.semaphore = semaphore;
        this.acquireStrategy = () -> this.semaphoreTryAquireStrategy(acquireTimeout);
    }

    private <T> List<Callable<T>> toSemaphoreCallables(Collection<? extends Callable<T>> callables) {
        return callables.stream()
                .map(this::toSemaphoreCallable)
                .collect(Collectors.toList());
    }

    private <T> Callable<T> toSemaphoreCallable(Callable<T> callable) {
        return () -> {
            acquireStrategy.call();
            try {
                return callable.call();
            } finally {
                semaphore.release();
            }
        };
    }

    private Runnable toSemaphoreRunnable(Runnable command) {
        return () -> {
            acquireStrategy.call();
            try {
                command.run();
            } finally {
                semaphore.release();
            }
        };
    }

    private void semaphoreTryAquireStrategy(Duration acquireTimeout) throws InterruptedException, TimeoutException {
        boolean isAcquired = semaphore.tryAcquire(acquireTimeout.toNanos(), TimeUnit.NANOSECONDS);

        if (!isAcquired) {
            throw new TimeoutException(String.format("Permit not acquired before the acquireTimeout %s", acquireTimeout));
        }
    }

    @Override
    public void execute(Runnable command) {
        delegate.execute(toSemaphoreRunnable(command));
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return delegate.submit(toSemaphoreCallable(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return delegate.submit(toSemaphoreRunnable(task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return delegate.submit(toSemaphoreRunnable(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return delegate.invokeAll(toSemaphoreCallables(tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return delegate.invokeAll(toSemaphoreCallables(tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return delegate.invokeAny(toSemaphoreCallables(tasks));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.invokeAny(toSemaphoreCallables(tasks), timeout, unit);
    }

    @FunctionalInterface
    private interface SemaphoreAquireStrategy {
        void acquire() throws InterruptedException, TimeoutException;

        default void call() {
            try {
                acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException();
            } catch (TimeoutException e) {
                throw new IllegalStateException(e);
            }
        }
    }

}