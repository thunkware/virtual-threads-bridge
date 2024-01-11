package com.github.thunkware;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

/**
 * Defines static methods to create platform and virtual thread factories builders.
 */
public interface ThreadFactoryBuilder {

  /**
   * Creates a new VirtualThreadFactoryBuilder
   * 
   * @return new instance of VirtualThreadFactoryBuilder
   */
  VirtualThreadFactoryBuilder ofVirtual();

  /**
   * Creates a new instance of PlatformThreadFactoryBuilder
   * 
   * @return new instance of PlatformThreadFactoryBuilder
   */
  PlatformThreadFactoryBuilder ofPlatform();

  interface BaseThreadFactoryBuilder<S extends BaseThreadFactoryBuilder<S>> {

    /**
     * Sets the thread name.
     * 
     * @param name thread name
     * @return this builder
     */
    S name(String name);

    /**
     * Sets the thread name to be the concatenation of a string prefix and the string representation of a counter value. The counter's
     * initial value is {@code start}. It is incremented after a {@code Thread} is created with this builder so that the next thread is
     * named with the new counter value. A {@code ThreadFactory} created with this builder is seeded with the current value of the counter.
     * The {@code
     * ThreadFactory} increments its copy of the counter after {@link ThreadFactory#newThread(Runnable) newThread} is used to create a
     * {@code Thread}.
     *
     * @apiNote The following example creates a builder that is invoked twice to start two threads named "{@code worker-0}" and
     *          "{@code worker-1}". {@snippet : Thread.Builder builder = Thread.ofPlatform().name("worker-", 0); Thread t1 =
     *          builder.start(task1); // name "worker-0" Thread t2 = builder.start(task2); // name "worker-1" }
     *
     * @param prefix thread name prefix
     * @param start the starting value of the counter
     * @return this builder
     * @throws IllegalArgumentException if start is negative
     */
    S name(String prefix, long start);

    /**
     * Sets whether the thread inherits the initial values of {@linkplain InheritableThreadLocal inheritable-thread-local} variables from
     * the constructing thread. The default is to inherit.
     *
     * @param inherit {@code true} to inherit, {@code false} to not inherit
     * @return this builder
     */
    S inheritInheritableThreadLocals(boolean inherit);

    /**
     * Sets the uncaught exception handler.
     *
     * @param ueh uncaught exception handler
     * @return this builder
     */
    S uncaughtExceptionHandler(UncaughtExceptionHandler ueh);

    /**
     * Creates the new ThreadFactory defined at the builder.
     * 
     * @return the new ThreadFactory defined at the builder
     */
    ThreadFactory factory();

  }

  interface VirtualThreadFactoryBuilder extends BaseThreadFactoryBuilder<VirtualThreadFactoryBuilder> {

  }

  interface PlatformThreadFactoryBuilder extends BaseThreadFactoryBuilder<PlatformThreadFactoryBuilder> {

    /**
     * Sets the thread group.
     * 
     * @param group the thread group
     * @return this builder
     */
    PlatformThreadFactoryBuilder group(ThreadGroup group);

    /**
     * Sets the daemon status.
     * 
     * @param on {@code true} to create daemon threads
     * @return this builder
     */
    PlatformThreadFactoryBuilder daemon(boolean on);

    /**
     * Sets the daemon status to {@code true}.
     * 
     * @implSpec The default implementation invokes {@linkplain #daemon(boolean)} with a value of {@code true}.
     * @return this builder
     */
    default PlatformThreadFactoryBuilder daemon() {
      return daemon(true);
    }

    /**
     * Sets the thread priority.
     * 
     * @param priority priority
     * @return this builder
     * @throws IllegalArgumentException if the priority is less than {@link Thread#MIN_PRIORITY} or greater than {@link Thread#MAX_PRIORITY}
     */
    PlatformThreadFactoryBuilder priority(int priority);

    /**
     * Sets the desired stack size.
     *
     * <p> The stack size is the approximate number of bytes of address space that the Java virtual machine is to allocate for the thread's
     * stack. The effect is highly platform dependent and the Java virtual machine is free to treat the {@code stackSize} parameter as a
     * "suggestion". If the value is unreasonably low for the platform then a platform specific minimum may be used. If the value is
     * unreasonably high then a platform specific maximum may be used. A value of zero is always ignored.
     *
     * @param stackSize the desired stack size
     * @return this builder
     * @throws IllegalArgumentException if the stack size is negative
     */
    PlatformThreadFactoryBuilder stackSize(long stackSize);

  }

  static class ThreadFactoryBuilderFactory {
    static final ThreadFactoryBuilder threadFactoryBuilder;

    static {
      boolean isJava21;
      try {
        Class.forName("java.lang.Thread$Builder$OfPlatform");
        isJava21 = true;
      } catch (ClassNotFoundException e) {
        isJava21 = false;
      }

      threadFactoryBuilder = isJava21 ? new ThreadFactoryBuilder21() : new ThreadFactoryBuilder8();
    }

    private ThreadFactoryBuilderFactory() {

    }
  }

}
