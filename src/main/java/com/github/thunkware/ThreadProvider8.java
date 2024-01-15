package com.github.thunkware;

import com.github.thunkware.ThreadTool.Builder.OfPlatform;
import com.github.thunkware.ThreadTool.Builder.OfVirtual;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

final class ThreadProvider8 implements ThreadProvider {

    @Override
    public boolean hasVirtualThreads() {
        return false;
    }

    @Override
    public boolean isVirtual(final Thread thread) {
        return false;
    }

    @Override
    public Thread startVirtualThread(final Runnable task) {
        Thread thread = unstartedVirtualThread(task);
        thread.start();
        return thread;
    }

    @Override
    public Thread unstartedVirtualThread(Runnable task) {
        return new Thread(task);
    }

    @Override
    public ExecutorService newThreadPerTaskExecutor(final ThreadFactory threadFactory) {
        return new ThreadPerTaskExecutor(threadFactory);
    }

    @Override
    public ExecutorService newVirtualThreadPerTaskExecutor() {
        return newThreadPerTaskExecutor(task -> {
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            return thread;
        });
    }

    @Override
    public OfPlatform ofPlatform() {
        return new ThreadBuilders8.PlatformThreadBuilder();
    }

    @Override
    public OfVirtual ofVirtual() {
        return new ThreadBuilders8.VirtualThreadBuilder();
    }

    private static class ThreadPerTaskExecutor extends AbstractExecutorService implements ExecutorService {

        private final ThreadFactory threadFactory;
        private volatile boolean isShutdown;
        private final Set<Thread> threads = Collections.newSetFromMap(new ConcurrentHashMap<>());

        public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
        }

        @Override
        public void execute(final Runnable command) {
            if (isShutdown) {
                throw new IllegalArgumentException("Executor is shut down");
            }
            Thread thread = threadFactory.newThread(() -> {
                command.run();
                threads.remove(Thread.currentThread());
            });
            threads.add(thread);
            thread.start();
        }

        @Override
        public void shutdown() {
            isShutdown = true;
        }

        @Override
        public List<Runnable> shutdownNow() {
            shutdown();
            threads.forEach(Thread::interrupt);
            return Collections.emptyList();
        }

        @Override
        public boolean isShutdown() {
            return isShutdown;
        }

        @Override
        public boolean isTerminated() {
            return isShutdown();
        }

        @Override
        public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
            long timeoutMs = unit.toMillis(timeout);
            for (Thread thread : threads) {
                if (timeoutMs <= 0) {
                    return false;
                }

                long start = System.currentTimeMillis();
                thread.join(timeoutMs);
                long diff = System.currentTimeMillis() - start;
                timeoutMs = timeoutMs - diff;
            }
            return true;
        }

    }

}
