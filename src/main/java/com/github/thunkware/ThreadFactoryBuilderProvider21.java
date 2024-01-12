package com.github.thunkware;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.concurrent.ThreadFactory;


/**
 * {@link ThreadFactoryBuilderProvider} to create ThreadFactoryBuilder with Java 21+
 */
public class ThreadFactoryBuilderProvider21 implements ThreadFactoryBuilderProvider {

  @Override
  public VirtualThreadFactoryBuilder ofVirtual() {
    return new VirtualThreadFactoryBuilder21();
  }

  private static <T> T invoke(final ThrowingCallable<T> callable) {
    try {
      return callable.call();
    } catch (final Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  @FunctionalInterface
  private static interface ThrowingCallable<V> {
    V call() throws Throwable;
  }

  /**
   * {@link VirtualThreadFactoryBuilder } for Java 21+
   */
  static class VirtualThreadFactoryBuilder21 implements VirtualThreadFactoryBuilder {

    private static final MethodHandle ofVirtual;

    private static final MethodHandle name;

    private static final MethodHandle nameWithCounter;

    private static final MethodHandle inheritInheritableThreadLocals;

    private static final MethodHandle uncaughtExceptionHandler;

    private static final MethodHandle factory;

    static {

      final Lookup publicLookup = MethodHandles.publicLookup();

      final Class<?> ofVirtualClass = invoke(() -> Class.forName("java.lang.Thread$Builder$OfVirtual"));

      ofVirtual = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofVirtualClass);
        return publicLookup.findStatic(Thread.class, "ofVirtual", methodType);
      });

      name = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofVirtualClass, String.class);
        return publicLookup.findVirtual(ofVirtualClass, "name", methodType);
      });

      nameWithCounter = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofVirtualClass, String.class, long.class);
        return publicLookup.findVirtual(ofVirtualClass, "name", methodType);
      });

      inheritInheritableThreadLocals = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofVirtualClass, boolean.class);
        return publicLookup.findVirtual(ofVirtualClass, "inheritInheritableThreadLocals", methodType);
      });

      uncaughtExceptionHandler = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofVirtualClass, UncaughtExceptionHandler.class);
        return publicLookup.findVirtual(ofVirtualClass, "uncaughtExceptionHandler", methodType);
      });

      factory = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ThreadFactory.class);
        return publicLookup.findVirtual(ofVirtualClass, "factory", methodType);
      });

    }

    private Object builder;

    VirtualThreadFactoryBuilder21() {
      builder = ThreadFactoryBuilderProvider21.invoke(ofVirtual::invoke);
    }

    @Override
    public VirtualThreadFactoryBuilder21 name(String nameStr) {
      builder = ThreadFactoryBuilderProvider21.invoke(() -> name.invoke(builder, nameStr));
      return this;
    }

    @Override
    public VirtualThreadFactoryBuilder21 name(String prefix, long start) {
      builder = ThreadFactoryBuilderProvider21.invoke(() -> nameWithCounter.invoke(builder, prefix, start));
      return this;
    }

    @Override
    public VirtualThreadFactoryBuilder21 inheritInheritableThreadLocals(boolean inherit) {
      builder = ThreadFactoryBuilderProvider21.invoke(() -> inheritInheritableThreadLocals.invoke(builder, inherit));
      return this;
    }

    @Override
    public VirtualThreadFactoryBuilder21 uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
      builder = ThreadFactoryBuilderProvider21.invoke(() -> uncaughtExceptionHandler.invoke(builder, ueh));
      return this;
    }

    @Override
    public ThreadFactory factory() {
      return ThreadFactoryBuilderProvider21.invoke(() -> (ThreadFactory) factory.invoke(builder));
    }

  }

}
