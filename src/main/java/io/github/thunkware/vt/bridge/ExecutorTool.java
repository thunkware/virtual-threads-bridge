package io.github.thunkware.vt.bridge;

import java.time.Duration;
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
     * Creates an Executor that starts a new virtual Thread on Java 21 + (or new platform thread on Java 8+) for each task.
     * The number of threads created by the Executor is unbounded.
     *
     * <p> This method is equivalent to invoking
     * {@link #newThreadPerTaskExecutor(ThreadFactory)} with a thread factory
     * that creates virtual threads.
     *
     * @param threadCustomizer ThreadCustomizer to customize new unstarted threads
     * @return a new executor that creates a new virtual Thread for each task
     */
    public static ExecutorService newVirtualThreadPerTaskExecutor(ThreadCustomizer threadCustomizer) {
        return getThreadProvider().newVirtualThreadPerTaskExecutor(threadCustomizer);
    }

    /**
     * Creates an Executor that starts a new virtual Thread and limits concurrency to the number of sempahore permits
     *
     * @param permits number of sempahore permits
     * @return a new executor with limited concurrency
     * @deprecated Because of typo in method name
     */
    @Deprecated
    public static ExecutorService newSempahoreVirtualExecutor(int permits) {
        return newSemaphoreVirtualExecutor(permits);
    }

    /**
     * Creates an Executor that starts a new virtual Thread and limits concurrency to the number of semaphore permits
     *
     * @param permits number of semaphore permits
     * @return a new executor with limited concurrency
     */
    public static ExecutorService newSemaphoreVirtualExecutor(int permits) {
        ExecutorService executor = getThreadProvider().newVirtualThreadPerTaskExecutor();
        return new SemaphoreExecutor(executor, permits);
    }

    /**
     * Creates an Executor that starts a new virtual Thread and limits concurrency
     * to the number of semaphore permits.
     *
     * <p>
     * When executing a task with the created ExecutorService if one permit is
     * available, the task is executed. If no permit is available, then the executor
     * thread becomes disabled for thread scheduling purposes and lies dormant until
     * one of three things happens:
     * <ul>
     * <li>a permit becomes available</li>
     * <li>some other thread {@linkplain Thread#interrupt interrupts} the current
     * thread; or</li>
     * <li>the specified waiting time elapses, in which case a Timeout exception is thrown</li>
     * </ul>
     *
     * @param permits        number of sempahore permits
     * @param acquireTimeout time to wait for acquire a new task when no permit is
     *                       available
     * @return a new executor with limited concurrency
     * @deprecated Because of typo in method name
     */
    @Deprecated
    public static ExecutorService newSempahoreVirtualExecutor(int permits, Duration acquireTimeout) {
        return newSemaphoreVirtualExecutor(permits, acquireTimeout);
    }

    /**
     * Creates an Executor that starts a new virtual Thread and limits concurrency
     * to the number of semaphore permits.
     *
     * <p>
     * When executing a task with the created ExecutorService if one permit is
     * available, the task is executed. If no permit is available, then the executor
     * thread becomes disabled for thread scheduling purposes and lies dormant until
     * one of three things happens:
     * <ul>
     * <li>a permit becomes available</li>
     * <li>some other thread {@linkplain Thread#interrupt interrupts} the current
     * thread; or</li>
     * <li>the specified waiting time elapses, in which case a Timeout exception is thrown</li>
     * </ul>
     *
     * @param permits        number of semaphore permits
     * @param acquireTimeout time to wait for acquire a new task when no permit is
     *                       available
     * @return a new executor with limited concurrency
     */
    public static ExecutorService newSemaphoreVirtualExecutor(int permits, Duration acquireTimeout) {
        ExecutorService executor = getThreadProvider().newVirtualThreadPerTaskExecutor();
        return new SemaphoreExecutor(executor, permits, acquireTimeout);
    }


    private ExecutorTool() {
        throw new AssertionError();
    }

}