package com.github.thunkware;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

/**
 * Defines static methods to create virtual thread factories builders.
 */
public interface ThreadFactoryBuilderProvider {

  /**
   * Creates a new {@link VirtualThreadFactoryBuilder}
   * 
   * @return new instance of VirtualThreadFactoryBuilder
   */
  VirtualThreadFactoryBuilder ofVirtual();

  /**
   * Builder to create Virtual Threads Factories
   */
  interface VirtualThreadFactoryBuilder {

    /**
     * Sets the thread name.
     * 
     * @param name thread name
     * @return this builder
     */
    VirtualThreadFactoryBuilder name(String name);

    /**
     * Sets the thread name to be the concatenation of a string prefix and the string representation of a counter value. The counter's
     * initial value is {@code start}. It is incremented after a {@code Thread} is created with this builder so that the next thread is
     * named with the new counter value. A {@code ThreadFactory} created with this builder is seeded with the current value of the counter.
     * The {@code
     * ThreadFactory} increments its copy of the counter after {@link ThreadFactory#newThread(Runnable) newThread} is used to create a
     * {@code Thread}.
     *
     * @param prefix thread name prefix
     * @param start the starting value of the counter
     * @return this builder
     * @throws IllegalArgumentException if start is negative
     */
    VirtualThreadFactoryBuilder name(String prefix, long start);

    /**
     * Sets whether the thread inherits the initial values of {@linkplain InheritableThreadLocal inheritable-thread-local} variables from
     * the constructing thread. The default is to inherit.
     *
     * @param inherit {@code true} to inherit, {@code false} to not inherit
     * @return this builder
     */
    VirtualThreadFactoryBuilder inheritInheritableThreadLocals(boolean inherit);

    /**
     * Sets the uncaught exception handler.
     *
     * @param ueh uncaught exception handler
     * @return this builder
     */
    VirtualThreadFactoryBuilder uncaughtExceptionHandler(UncaughtExceptionHandler ueh);

    /**
     * Creates the new ThreadFactory defined at the builder.
     * 
     * @return the new ThreadFactory defined at the builder
     */
    ThreadFactory factory();

  }


  static class ThreadFactoryBuilderFactory {
    static final ThreadFactoryBuilderProvider threadFactoryBuilderProvider;

    static {
      boolean isJava21;
      try {
        Class.forName("java.lang.Thread$Builder$OfPlatform");
        isJava21 = true;
      } catch (ClassNotFoundException e) {
        isJava21 = false;
      }

      threadFactoryBuilderProvider = isJava21 ? new ThreadFactoryBuilderProvider21() : new ThreadFactoryBuilderProvider8();
    }

    private ThreadFactoryBuilderFactory() {

    }
  }

}
