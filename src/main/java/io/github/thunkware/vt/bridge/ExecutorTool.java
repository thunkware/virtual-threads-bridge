package io.github.thunkware.vt.bridge;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import static io.github.thunkware.vt.bridge.ThreadProviderFactory.getThreadProvider;

/**
 * Utility for working with Executors API from Java21 in Java8+ VM. Convenience class for {@link ThreadProvider}
 */
public class ExecutorTool {

    /**
     * On Java 8+, returns false
     * On Java 21+, returns true
     *
     * @return true if the JVM supports virtual threads
     */
    public static final boolean hasVirtualThreads() {
        return getThreadProvider().hasVirtualThreads();
    }

    /**
     * Creates an Executor that starts a new Thread for each task.
     * The number of threads created by the Executor is unbounded.
     *
     * <p> Invoking {@link Future#cancel(boolean) cancel(true)} on a {@link
     * Future Future} representing the pending result of a task submitted to
     * the Executor will {@link Thread#interrupt() interrupt} the thread
     * executing the task.
     *
     * @param threadFactory the factory to use when creating new threads
     * @return a new executor that creates a new Thread for each task
     * @throws NullPointerException if threadFactory is null
     */
    public static ExecutorService newThreadPerTaskExecutor(ThreadFactory threadFactory) {
        return getThreadProvider().newThreadPerTaskExecutor(threadFactory);
    }

    /**
     * Creates an Executor that starts a new virtual Thread on Java 21 + (or new platform thread on Java 8+) for each task.
     * The number of threads created by the Executor is unbounded.
     *
     * <p> This method is equivalent to invoking
     * {@link #newThreadPerTaskExecutor(ThreadFactory)} with a thread factory
     * that creates virtual threads.
     *
     * @return a new executor that creates a new virtual Thread for each task
     */
    public static ExecutorService newVirtualThreadPerTaskExecutor() {
        return getThreadProvider().newVirtualThreadPerTaskExecutor();
    }

    /**
     * Creates an Executor that starts a new virtual Thread and limits concurrency to the number of sempahore permits
     *
     * @param permits number of sempahore permits
     * @return a new executor with limited concurrency
     */
    public static ExecutorService newSempahoreVirtualExecutor(int permits) {
        ExecutorService executor = getThreadProvider().newVirtualThreadPerTaskExecutor();
        return new SempahoreExecutor(executor, permits);
    }

    private ExecutorTool() {
        throw new AssertionError();
    }

}