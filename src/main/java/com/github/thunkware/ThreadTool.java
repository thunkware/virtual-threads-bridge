package com.github.thunkware;

import static com.github.thunkware.ThreadFactoryBuilder.ThreadFactoryBuilderFactory.threadFactoryBuilder;
import static com.github.thunkware.ThreadProvider.ThreadProviderFactory.threadProvider;

import com.github.thunkware.ThreadFactoryBuilder.PlatformThreadFactoryBuilder;
import com.github.thunkware.ThreadFactoryBuilder.VirtualThreadFactoryBuilder;

/**
 * Utility for working with Threads API from Java 21 in Java 8+ 
 */
public class ThreadTool {

    /**
     * On Java 8+, returns false
     * On Java 21+, returns true
     *
     * @return true if the JVM supports virtual threads
     */
    public static final boolean hasVirtualThreads() {
        return threadProvider.hasVirtualThreads();
    }

    /**
     * Returns {@code true} if the thread is a virtual thread. A virtual thread
     * is scheduled by the Java virtual machine rather than the operating system.
     *
     * @param thread Thread
     * @return {@code true} if the thread is a virtual thread
     */
    public static final boolean isVirtual(Thread thread) {
        return threadProvider.isVirtual(thread);
    }

    /**
     * Returns {@code true} if this current thread is a virtual thread. A virtual thread
     * is scheduled by the Java virtual machine rather than the operating system.
     *
     * @return {@code true} if this thread is a virtual thread
     */
    public static final boolean isVirtual() {
        return threadProvider.isVirtual(Thread.currentThread());
    }

    /**
     * On Java 8+, creates a platform thread to execute a task and schedules it to execute. <p>
     * On Java 21+, creates a virtual thread to execute a task and schedules it to execute.
     *
     * @param task the object to run when the thread executes
     * @return a new, and started, thread
     */
    public static Thread startVirtualThread(Runnable task) {
        return threadProvider.startVirtualThread(task);
    }

    /**
     * On Java 8+, creates a platform thread to execute a task. <p>
     * On Java 21+, creates a virtual thread to execute a task.
     *
     * @param task the object to run when the thread executes
     * @return a new, and unstarted, thread
     */
    public static Thread unstartedVirtualThread(Runnable task) {
        return threadProvider.unstartedVirtualThread(task);
    }
    
    /**
     * On Java 8+, throws an UnsupportedOperationExcption
     * <p>
     * On Java 21+, creates a new VirtualThreadFactoryBuilder
     * 
     * @return a new VirtualThreadFactoryBuilder
     */
    public static VirtualThreadFactoryBuilder ofVirtual() {
	return threadFactoryBuilder.ofVirtual();
    }

    /**
     * On Java 8+, throws an UnsupportedOperationExcption
     * <p>
     * On Java 21+, creates a new PlatformThreadFactoryBuilder
     * 
     * @return a new VirtualThreadFactoryBuilder
     */
    public static PlatformThreadFactoryBuilder ofPlatform() {
	return threadFactoryBuilder.ofPlatform();
    }

    private ThreadTool() {
        throw new AssertionError();
    }
}