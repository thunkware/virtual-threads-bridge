package io.github.thunkware;

import io.github.thunkware.ThreadTool.Builder.OfPlatform;
import io.github.thunkware.ThreadTool.Builder.OfVirtual;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static io.github.thunkware.ThreadFeature.HAS_VIRTUAL_THREADS;
import static io.github.thunkware.ThreadFeature.IS_VIRTUAL;
import static io.github.thunkware.ThreadFeature.NEW_THREAD_PER_TASK_EXECUTOR;
import static io.github.thunkware.ThreadFeature.NEW_VIRTUAL_THREAD_PER_TASK_EXECUTOR;
import static io.github.thunkware.ThreadFeature.OF_PLATFORM;
import static io.github.thunkware.ThreadFeature.OF_VIRTUAL;
import static io.github.thunkware.ThreadFeature.START_VIRTUAL_THREAD;
import static io.github.thunkware.ThreadFeature.UNSTARTED_VIRTUAL_THREAD;

final class ThreadProvider8 implements ThreadProvider {

    private final ThreadProviderConfig config = new ThreadProviderConfig();

    @Override
    public ThreadProviderConfig getConfig() {
        return config;
    }

    @Override
    public boolean hasVirtualThreads() {
        config.enforceCompatibilityPolicy(HAS_VIRTUAL_THREADS);
        return false;
    }

    @Override
    public boolean isVirtual(final Thread thread) {
        config.enforceCompatibilityPolicy(IS_VIRTUAL);
        return false;
    }

    @Override
    public Thread startVirtualThread(final Runnable task) {
        config.enforceCompatibilityPolicy(START_VIRTUAL_THREAD);

        Thread thread = unstartedVirtualThread(task);
        thread.start();
        return thread;
    }

    @Override
    public Thread unstartedVirtualThread(Runnable task) {
        config.enforceCompatibilityPolicy(UNSTARTED_VIRTUAL_THREAD);
        return new Thread(task);
    }

    @Override
    public ExecutorService newThreadPerTaskExecutor(final ThreadFactory threadFactory) {
        config.enforceCompatibilityPolicy(NEW_THREAD_PER_TASK_EXECUTOR);
        return new ThreadPerTaskExecutor(threadFactory);
    }

    @Override
    public ExecutorService newVirtualThreadPerTaskExecutor() {
        config.enforceCompatibilityPolicy(NEW_VIRTUAL_THREAD_PER_TASK_EXECUTOR);

        return newThreadPerTaskExecutor(task -> {
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            return thread;
        });
    }

    @Override
    public OfPlatform ofPlatform() {
        config.enforceCompatibilityPolicy(OF_PLATFORM);
        return new ThreadBuilders8.PlatformThreadBuilder(config);
    }

    @Override
    public OfVirtual ofVirtual() {
        config.enforceCompatibilityPolicy(OF_VIRTUAL);
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
                try {
                    command.run();
                } finally {
                    threads.remove(Thread.currentThread());
                }
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
