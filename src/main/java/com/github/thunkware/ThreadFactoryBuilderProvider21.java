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

  @Override
  public PlatformThreadFactoryBuilder ofPlatform() {
    return new PlatformThreadFactoryBuilder21();
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

  /**
   * {@link PlatformThreadFactoryBuilder } for Java 21+
   */
  static class PlatformThreadFactoryBuilder21 implements PlatformThreadFactoryBuilder {

    private static final MethodHandle ofPlatform;

    private static final MethodHandle name;

    private static final MethodHandle nameWithCounter;

    private static final MethodHandle inheritInheritableThreadLocals;

    private static final MethodHandle uncaughtExceptionHandler;

    private static final MethodHandle threadGroup;

    private static final MethodHandle daemon;

    private static final MethodHandle priority;

    private static final MethodHandle stackSize;

    private static final MethodHandle factory;

    static {

      final Lookup publicLookup = MethodHandles.publicLookup();

      final Class<?> ofPlatformClass = invoke(() -> Class.forName("java.lang.Thread$Builder$OfPlatform"));

      ofPlatform = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofPlatformClass);
        return publicLookup.findStatic(Thread.class, "ofPlatform", methodType);
      });

      name = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofPlatformClass, String.class);
        return publicLookup.findVirtual(ofPlatformClass, "name", methodType);
      });

      nameWithCounter = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofPlatformClass, String.class, long.class);
        return publicLookup.findVirtual(ofPlatformClass, "name", methodType);
      });

      inheritInheritableThreadLocals = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofPlatformClass, boolean.class);
        return publicLookup.findVirtual(ofPlatformClass, "inheritInheritableThreadLocals", methodType);
      });

      uncaughtExceptionHandler = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofPlatformClass, UncaughtExceptionHandler.class);
        return publicLookup.findVirtual(ofPlatformClass, "uncaughtExceptionHandler", methodType);
      });

      threadGroup = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofPlatformClass, ThreadGroup.class);
        return publicLookup.findVirtual(ofPlatformClass, "group", methodType);
      });

      daemon = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofPlatformClass, boolean.class);
        return publicLookup.findVirtual(ofPlatformClass, "daemon", methodType);
      });

      priority = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofPlatformClass, int.class);
        return publicLookup.findVirtual(ofPlatformClass, "priority", methodType);
      });

      stackSize = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ofPlatformClass, long.class);
        return publicLookup.findVirtual(ofPlatformClass, "stackSize", methodType);
      });

      factory = invoke(() -> {
        final MethodType methodType = MethodType.methodType(ThreadFactory.class);
        return publicLookup.findVirtual(ofPlatformClass, "factory", methodType);
      });

    }

    private Object builder;

    PlatformThreadFactoryBuilder21() {
      builder = ThreadFactoryBuilderProvider21.invoke(ofPlatform::invoke);
    }

    @Override
    public PlatformThreadFactoryBuilder name(String nameStr) {
      builder = ThreadFactoryBuilderProvider21.invoke(() -> name.invoke(builder, nameStr));
      return this;
    }

    @Override
    public PlatformThreadFactoryBuilder21 name(String prefix, long start) {
      builder = ThreadFactoryBuilderProvider21.invoke(() -> nameWithCounter.invoke(builder, prefix, start));
      return this;
    }

    @Override
    public PlatformThreadFactoryBuilder21 inheritInheritableThreadLocals(boolean inherit) {
      builder = ThreadFactoryBuilderProvider21.invoke(() -> inheritInheritableThreadLocals.invoke(builder, inherit));
      return this;
    }

    @Override
    public PlatformThreadFactoryBuilder21 uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
      builder = ThreadFactoryBuilderProvider21.invoke(() -> uncaughtExceptionHandler.invoke(builder, ueh));
      return this;
    }

    @Override
    public PlatformThreadFactoryBuilder21 group(ThreadGroup group) {
      builder = ThreadFactoryBuilderProvider21.invoke(() -> threadGroup.invoke(builder, group));
      return this;
    }

    @Override
    public PlatformThreadFactoryBuilder21 daemon(boolean on) {
      builder = ThreadFactoryBuilderProvider21.invoke(() -> daemon.invoke(builder, on));
      return this;
    }

    @Override
    public PlatformThreadFactoryBuilder21 priority(int priorityint) {
      builder = ThreadFactoryBuilderProvider21.invoke(() -> priority.invoke(builder, priorityint));
      return this;
    }

    @Override
    public PlatformThreadFactoryBuilder21 stackSize(long stackSizeArg) {
      builder = ThreadFactoryBuilderProvider21.invoke(() -> stackSize.invoke(builder, stackSizeArg));
      return this;
    }

    @Override
    public ThreadFactory factory() {
      return ThreadFactoryBuilderProvider21.invoke(() -> (ThreadFactory) factory.invoke(builder));
    }

  }

}
