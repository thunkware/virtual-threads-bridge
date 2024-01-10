package com.github.thunkware;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

/**
 * Defines static methods to create platform and virtual thread factories builders.
 */
public class ThreadFactoryBuilders {

  private ThreadFactoryBuilders() {

  }

  /**
   * Creates a Thread Factory builder for virtual threads
   */
  public static VirtualThreadFactoryBuilder ofVirtual() {
    return ThreadFactoryBuilderFactory.threadFactoryBuilder.ofVirtual();
  }

  /**
   * Creates a Thread Factory builder for platform threads
   */
  public static PlatformThreadFactoryBuilder ofPlatform() {
    return ThreadFactoryBuilderFactory.threadFactoryBuilder.ofPlatform();
  }

  interface ThreadFactoryBuilder {

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

  }

  interface BaseThreadFactoryBuilder<S extends BaseThreadFactoryBuilder<S>> {

    S name(String name);

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

  // abstract static class BaseThreadFactoryBuilder<S extends BaseThreadFactoryBuilder<S>> {
  // private String name;
  //
  // private boolean inheritInheritableThreadLocals;
  //
  // private UncaughtExceptionHandler uncaughtExceptionHandler;
  //
  // public S name(String name) {
  // this.name = name;
  //
  // return this.self();
  // }
  //
  // /**
  // * Sets whether the thread inherits the initial values of {@linkplain InheritableThreadLocal inheritable-thread-local} variables from
  // * the constructing thread. The default is to inherit.
  // *
  // * @param inherit {@code true} to inherit, {@code false} to not inherit
  // * @return this builder
  // */
  // public S inheritInheritableThreadLocals(boolean inherit) {
  // this.inheritInheritableThreadLocals = inherit;
  //
  // return this.self();
  // }
  //
  // /**
  // * Sets the uncaught exception handler.
  // *
  // * @param ueh uncaught exception handler
  // * @return this builder
  // */
  // S uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
  //
  // this.uncaughtExceptionHandler = ueh;
  //
  // return this.self();
  // }
  //
  // @SuppressWarnings("unchecked")
  // private S self() {
  // return (S) this;
  // }
  //
  // /**
  // * Returns a {@code ThreadFactory} to create threads from the current state of the builder. The returned thread factory is safe for use
  // * by multiple concurrent threads.
  // *
  // * @return a thread factory to create threads
  // */
  // public abstract ThreadFactory factory();
  //
  // }
  //
  // /**
  // * Builder to create ThreadFactory objects for virtual threads.
  // */
  // public static class VirtualThreadFactoryBuilder extends BaseThreadFactoryBuilder<VirtualThreadFactoryBuilder> {
  //
  // @Override
  // public ThreadFactory factory() {
  // throw new UnsupportedOperationException();
  // }
  //
  // }
  //
  // /**
  // * Builder to create ThreadFactory objects for platform threads.
  // */
  // public static class PlatformThreadFactoryBuilder extends BaseThreadFactoryBuilder<PlatformThreadFactoryBuilder> {
  //
  // private ThreadGroup group;
  //
  // private boolean daemon;
  //
  // private int priority;
  //
  // private long stackSize;
  //
  // /**
  // * Sets the thread group.
  // *
  // * @param group the thread group
  // * @return this builder
  // */
  // PlatformThreadFactoryBuilder group(ThreadGroup group) {
  //
  // this.group = group;
  //
  // return this;
  // }
  //
  // /**
  // * Sets the daemon status.
  // *
  // * @param on {@code true} to create daemon threads
  // * @return this builder
  // */
  // PlatformThreadFactoryBuilder daemon(boolean on) {
  // this.daemon = on;
  //
  // return this;
  // }
  //
  // /**
  // * Sets the daemon status to {@code true}.
  // *
  // * @implSpec The default implementation invokes {@linkplain #daemon(boolean)} with a value of {@code true}.
  // * @return this builder
  // */
  // PlatformThreadFactoryBuilder daemon() {
  // return daemon(true);
  // }
  //
  // /**
  // * Sets the thread priority.
  // *
  // * @param priority priority
  // * @return this builder
  // * @throws IllegalArgumentException if the priority is less than {@link Thread#MIN_PRIORITY} or greater than {@link Thread#MAX_PRIORITY}
  // */
  // PlatformThreadFactoryBuilder priority(int priority) {
  // this.priority = priority;
  //
  // return this;
  // }
  //
  // /**
  // * Sets the desired stack size.
  // *
  // * <p> The stack size is the approximate number of bytes of address space that the Java virtual machine is to allocate for the thread's
  // * stack. The effect is highly platform dependent and the Java virtual machine is free to treat the {@code stackSize} parameter as a
  // * "suggestion". If the value is unreasonably low for the platform then a platform specific minimum may be used. If the value is
  // * unreasonably high then a platform specific maximum may be used. A value of zero is always ignored.
  // *
  // * @param stackSize the desired stack size
  // * @return this builder
  // * @throws IllegalArgumentException if the stack size is negative
  // */
  // PlatformThreadFactoryBuilder stackSize(long stackSize) {
  // this.stackSize = stackSize;
  //
  // return this;
  // }
  //
  // @Override
  // public ThreadFactory factory() {
  // throw new ThreadFactory() {
  //
  // @Override
  // public Thread newThread(Runnable r) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  // };
  // }

  // }
}
