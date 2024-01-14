package com.github.thunkware;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.thunkware.ThreadProvider.ThreadProviderFactory.getThreadProvider;

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
        return getThreadProvider().hasVirtualThreads();
    }

    /**
     * Returns {@code true} if the thread is a virtual thread. A virtual thread
     * is scheduled by the Java virtual machine rather than the operating system.
     *
     * @param thread Thread
     * @return {@code true} if the thread is a virtual thread
     */
    public static final boolean isVirtual(Thread thread) {
        return getThreadProvider().isVirtual(thread);
    }

    /**
     * Returns {@code true} if this current thread is a virtual thread. A virtual thread
     * is scheduled by the Java virtual machine rather than the operating system.
     *
     * @return {@code true} if this thread is a virtual thread
     */
    public static final boolean isVirtual() {
        return getThreadProvider().isVirtual(Thread.currentThread());
    }

    /**
     * On Java 8+, creates a platform thread to execute a task and schedules it to execute. <p>
     * On Java 21+, creates a virtual thread to execute a task and schedules it to execute.
     *
     * @param task the object to run when the thread executes
     * @return a new, and started, thread
     */
    public static Thread startVirtualThread(Runnable task) {
        return getThreadProvider().startVirtualThread(task);
    }

    /**
     * On Java 8+, creates a platform thread to execute a task. <p>
     * On Java 21+, creates a virtual thread to execute a task.
     *
     * @param task the object to run when the thread executes
     * @return a new, and unstarted, thread
     */
    public static Thread unstartedVirtualThread(Runnable task) {
        return getThreadProvider().unstartedVirtualThread(task);
    }

    /**
     * Returns a builder for creating a platform {@code Thread} or {@code ThreadFactory}
     * that creates platform threads.
     *
     * @return A builder for creating {@code Thread} or {@code ThreadFactory} objects.
     */
    public static Builder.OfPlatform ofPlatform() {
        return getThreadProvider().ofPlatform();
    }

    /**
     * On Java 8+, returns a builder for creating a platform {@code Thread} or {@code ThreadFactory}
     * that creates platform threads. <p>
     * On Java 21+, returns a builder for creating a platform {@code Thread} or {@code ThreadFactory}
     * that creates virtual threads. <p>
     *
     * @return A builder for creating {@code Thread} or {@code ThreadFactory} objects.
     */
    public static Builder.OfVirtual ofVirtual() {
        return getThreadProvider().ofVirtual();
    }

    /**
     * A builder for {@link Thread} and {@link ThreadFactory} objects.
     */
    public interface Builder {

        /**
         * Sets the thread name.
         * @param name thread name
         * @return this builder
         */
        Builder name(String name);

        /**
         * Sets the thread name to be the concatenation of a string prefix and
         * the string representation of a counter value.
         *
         * @param prefix thread name prefix
         * @param start the starting value of the counter
         * @return this builder
         * @throws IllegalArgumentException if start is negative
         */
        Builder name(String prefix, long start);

        /**
         * On Java 8+, this method is a no-op. <p>
         * On Java 21+, sets whether the thread inherits the initial values of {@linkplain
         * InheritableThreadLocal inheritable-thread-local} variables from the
         * constructing thread. The default is to inherit.
         *
         * @param inherit {@code true} to inherit, {@code false} to not inherit
         * @return this builder
         */
        Builder inheritInheritableThreadLocals(boolean inherit);

        /**
         * Sets the uncaught exception handler.
         * @param ueh uncaught exception handler
         * @return this builder
         */
        Builder uncaughtExceptionHandler(UncaughtExceptionHandler ueh);

        /**
         * Creates a new {@code Thread} from the current state of the builder to
         * run the given task.
         *
         * @param task the object to run when the thread executes
         * @return a new unstarted Thread
         */
        Thread unstarted(Runnable task);

        /**
         * Creates a new {@code Thread} from the current state of the builder and
         * schedules it to execute.
         *
         * @param task the object to run when the thread executes
         * @return a new started Thread
         */
        Thread start(Runnable task);

        /**
         * Returns a {@code ThreadFactory} to create threads from the current
         * state of the builder. The returned thread factory is safe for use by
         * multiple concurrent threads.
         *
         * @return a thread factory to create threads
         */
        ThreadFactory factory();

        /**
         * A builder for creating a platform {@link Thread} or {@link ThreadFactory}
         * that creates platform threads.
         */
        interface OfPlatform extends Builder {

            @Override OfPlatform name(String name);

            /**
             * @throws IllegalArgumentException {@inheritDoc}
             */
            @Override OfPlatform name(String prefix, long start);

            @Override OfPlatform inheritInheritableThreadLocals(boolean inherit);
            @Override OfPlatform uncaughtExceptionHandler(UncaughtExceptionHandler ueh);

            /**
             * Sets the thread group.
             * @param group the thread group
             * @return this builder
             */
            OfPlatform group(ThreadGroup group);

            /**
             * Sets the daemon status.
             * @param on {@code true} to create daemon threads
             * @return this builder
             */
            OfPlatform daemon(boolean on);

            /**
             * Sets the daemon status to {@code true}.
             * @return this builder
             */
            default OfPlatform daemon() {
                return daemon(true);
            }

            /**
             * Sets the thread priority.
             * @param priority priority
             * @return this builder
             * @throws IllegalArgumentException if the priority is less than
             *        {@link Thread#MIN_PRIORITY} or greater than {@link Thread#MAX_PRIORITY}
             */
            OfPlatform priority(int priority);

            /**
             * Sets the desired stack size.
             *
             * <p> The stack size is the approximate number of bytes of address space
             * that the Java virtual machine is to allocate for the thread's stack. The
             * effect is highly platform dependent and the Java virtual machine is free
             * to treat the {@code stackSize} parameter as a "suggestion". If the value
             * is unreasonably low for the platform then a platform specific minimum
             * may be used. If the value is unreasonably high then a platform specific
             * maximum may be used. A value of zero is always ignored.
             *
             * @param stackSize the desired stack size
             * @return this builder
             * @throws IllegalArgumentException if the stack size is negative
             */
            OfPlatform stackSize(long stackSize);
        }

        /**
         * A builder for creating a virtual {@link Thread} or {@link ThreadFactory}
         * that creates virtual threads.
         */
        interface OfVirtual extends Builder {

            @Override OfVirtual name(String name);

            /**
             * @throws IllegalArgumentException {@inheritDoc}
             */
            @Override OfVirtual name(String prefix, long start);

            @Override OfVirtual inheritInheritableThreadLocals(boolean inherit);
            @Override OfVirtual uncaughtExceptionHandler(UncaughtExceptionHandler ueh);
        }
    }
    
    private static final AtomicInteger threadNumber = new AtomicInteger();

    static String genThreadName() {
        return "Thread-" + threadNumber.getAndIncrement();
    }
    
    private ThreadTool() {
        throw new AssertionError();
    }
}