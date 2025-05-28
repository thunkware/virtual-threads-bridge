package io.github.thunkware.vt.bridge;

import io.github.thunkware.vt.bridge.ThreadTool.Builder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import static io.github.thunkware.vt.bridge.ThreadFeature.HAS_VIRTUAL_THREADS;
import static io.github.thunkware.vt.bridge.ThreadFeature.NEW_THREAD_PER_TASK_EXECUTOR;
import static io.github.thunkware.vt.bridge.ThreadFeature.NEW_VIRTUAL_THREAD_PER_TASK_EXECUTOR;
import static io.github.thunkware.vt.bridge.ThreadFeature.OF_PLATFORM;
import static io.github.thunkware.vt.bridge.ThreadFeature.OF_VIRTUAL;
import static io.github.thunkware.vt.bridge.ThreadFeature.START_VIRTUAL_THREAD;
import static io.github.thunkware.vt.bridge.ThreadFeature.UNSTARTED_VIRTUAL_THREAD;

/**
 * Provides various Java21 Virtual Thread features in Java8+ VM 
 */
public interface ThreadProvider {

    /**
     * Get configuration for this ThreadProvider
     * @return {@link ThreadProviderConfig}
     */
    ThreadProviderConfig getConfig();

    /**
     * On Java 8+, returns false
     * On Java 21+, returns true
     *
     * @return true if the JVM supports virtual threads
     */
    @ConfigFeature(feature = HAS_VIRTUAL_THREADS)
    boolean hasVirtualThreads();

    /**
     * On Java 8+, returns false
     * On Java 24+, returns true
     *
     * @return true if the JVM supports virtual threads that can synchronize without pinning
     */
    @ConfigFeature(feature = HAS_VIRTUAL_THREADS)
    boolean hasSafeVirtualThreads();

    /**
     * Returns {@code true} if the thread is a virtual thread. A virtual thread
     * is scheduled by the Java virtual machine rather than the operating system.
     *
     * @param thread Thread
     * @return {@code true} if the thread is a virtual thread
     */
    @ConfigFeature(feature = HAS_VIRTUAL_THREADS)
    boolean isVirtual(Thread thread);

    /**
     * On Java 8+, creates a platform thread to execute a task and schedules it to execute. <p>
     * On Java 21+, creates a virtual thread to execute a task and schedules it to execute.
     *
     * @param task the object to run when the thread executes
     * @return a new, and started, thread
     */
    @ConfigFeature(feature = START_VIRTUAL_THREAD)
    Thread startVirtualThread(Runnable task);

    /**
     * On Java 8+, creates a platform thread to execute a task. <p>
     * On Java 21+, creates a virtual thread to execute a task.
     *
     * @param task the object to run when the thread executes
     * @return a new, and unstarted, thread
     */
    @ConfigFeature(feature = UNSTARTED_VIRTUAL_THREAD)
    Thread unstartedVirtualThread(Runnable task);

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
    @ConfigFeature(feature = NEW_THREAD_PER_TASK_EXECUTOR)
    ExecutorService newThreadPerTaskExecutor(ThreadFactory threadFactory);

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
    @ConfigFeature(feature = NEW_VIRTUAL_THREAD_PER_TASK_EXECUTOR)
    ExecutorService newVirtualThreadPerTaskExecutor();

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
    @ConfigFeature(feature = NEW_VIRTUAL_THREAD_PER_TASK_EXECUTOR)
    ExecutorService newVirtualThreadPerTaskExecutor(ThreadCustomizer threadCustomizer);

    /**
     * Returns a builder for creating a platform {@code Thread} or {@code ThreadFactory}
     * that creates platform threads.
     *
     * @return A builder for creating {@code Thread} or {@code ThreadFactory} objects.
     */
    @ConfigFeature(feature = OF_PLATFORM)
    Builder.OfPlatform ofPlatform();

    /**
     * On Java 8+, returns a builder for creating a platform {@code Thread} or {@code ThreadFactory}
     * that creates platform threads. <p>
     * On Java 21+, returns a builder for creating a platform {@code Thread} or {@code ThreadFactory}
     * that creates virtual threads. <p>
     *
     * @return A builder for creating {@code Thread} or {@code ThreadFactory} objects.
     */
    @ConfigFeature(feature = OF_VIRTUAL)
    Builder.OfVirtual ofVirtual();

    /**
     * Get a shared ThreadProvider instance
     * @return ThreadProvider
     */
    static ThreadProvider getThreadProvider() {
        return ThreadProviderFactory.getThreadProvider();
    }

    /**
     * Create a new ThreadProvider instance
     * @return ThreadProvider
     */
    static ThreadProvider createThreadProvider() {
        return ThreadProviderFactory.createThreadProvider();
    }
}
