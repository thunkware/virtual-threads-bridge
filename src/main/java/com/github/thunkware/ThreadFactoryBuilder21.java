package com.github.thunkware;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.concurrent.ThreadFactory;

import com.github.thunkware.ThreadFactoryBuilders.PlatformThreadFactoryBuilder;
import com.github.thunkware.ThreadFactoryBuilders.ThreadFactoryBuilder;
import com.github.thunkware.ThreadFactoryBuilders.VirtualThreadFactoryBuilder;

public class ThreadFactoryBuilder21 implements ThreadFactoryBuilder {

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

  static class VirtualThreadFactoryBuilder21 implements VirtualThreadFactoryBuilder {

    private static final MethodHandle ofVirtual;

    // private static final MethodHandle name;

    private static final MethodHandle factory;

    static {

      final Lookup publicLookup = MethodHandles.publicLookup();

      ofVirtual = invoke(() -> {
        final Class<?> ofVirtualClass = Class.forName("java.lang.Thread$Builder$OfVirtual");
        final MethodType methodType = MethodType.methodType(ofVirtualClass);
        return publicLookup.findStatic(Thread.class, "ofVirtual", methodType);
      });

      // name = invoke(() -> {
      //
      // final Class<?> ofVirtualClass = Class.forName("java.lang.Thread$Builder$OfVirtual");
      //
      // return publicLookup.findGetter(ofVirtualClass, "name", String.class);
      // });

      factory = invoke(() -> {
        final Class<?> ofVirtualClass = Class.forName("java.lang.Thread$Builder$OfVirtual");
        final MethodType methodType = MethodType.methodType(ThreadFactory.class);
        return publicLookup.findVirtual(ofVirtualClass, "factory", methodType);
      });
    }

    private final Object builder;

    VirtualThreadFactoryBuilder21() {
      builder = ThreadFactoryBuilder21.invoke(ofVirtual::invoke);
    }

    @Override
    public VirtualThreadFactoryBuilder name(String name) {
      throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public VirtualThreadFactoryBuilder inheritInheritableThreadLocals(boolean inherit) {
      throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public VirtualThreadFactoryBuilder uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
      throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ThreadFactory factory() {
      return ThreadFactoryBuilder21.invoke(() -> {
        return (ThreadFactory) factory.invoke(builder);
      });
    }

  }

  static class PlatformThreadFactoryBuilder21 implements PlatformThreadFactoryBuilder {

    @Override
    public PlatformThreadFactoryBuilder name(String name) {
      throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PlatformThreadFactoryBuilder inheritInheritableThreadLocals(boolean inherit) {
      throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PlatformThreadFactoryBuilder uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
      throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ThreadFactory factory() {
      throw new UnsupportedOperationException("Not implemented yet");
    }

  }

}
