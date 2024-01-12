package com.github.thunkware;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

/**
 * {@link ThreadFactoryBuilderProvider} to create ThreadFactoryBuilder with Java 8+
 */
public class ThreadFactoryBuilderProvider8 implements ThreadFactoryBuilderProvider {

  @Override
  public VirtualThreadFactoryBuilder ofVirtual() {
    return new VirtualThreadFactoryBuilder8();
  }

  /**
   * {@link VirtualThreadFactoryBuilder } for Java 8+
   */
  private static class VirtualThreadFactoryBuilder8 implements VirtualThreadFactoryBuilder {

    private String name;

    private long start = -1;

    private UncaughtExceptionHandler uncaughtExceptionHandler;

    @Override
    public VirtualThreadFactoryBuilder8 name(String name) {
      this.name = name;
      return this;
    }

    @Override
    public VirtualThreadFactoryBuilder8 name(String prefix, long start) {
      this.name = prefix;
      this.start = start;
      return this;
    }

    @Override
    public VirtualThreadFactoryBuilder8 inheritInheritableThreadLocals(boolean inherit) {
      // Cannot emulate it with java8+ should we consider to throw an exception?
      return this;
    }

    @Override
    public VirtualThreadFactoryBuilder8 uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
      this.uncaughtExceptionHandler = ueh;
      return this;
    }

    @Override
    public ThreadFactory factory() {
      return new Java8VirtualThreadInternalThreadFactory(this.name, this.start, this.uncaughtExceptionHandler);
    }
    
    private class Java8VirtualThreadInternalThreadFactory implements ThreadFactory {

      private final String name;

      private long counter = -1;

      private final UncaughtExceptionHandler uncaughtExceptionHandler;

      Java8VirtualThreadInternalThreadFactory(final String name, final long start, UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.name = name;
        this.counter = start;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
      }

      @Override
      public Thread newThread(Runnable r) {
        
        Thread thread = new Thread(r);
        thread.setDaemon(true);

        if (this.name != null) {
          thread.setName(nextThreadName());
        }

        if (this.uncaughtExceptionHandler != null) {
          thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        }

        return thread;
      }

      private String nextThreadName() {
        if (name != null && counter >= 0) {
          return name + (counter++);
        } else {
          return name;
        }
      }
    }

  }
}
