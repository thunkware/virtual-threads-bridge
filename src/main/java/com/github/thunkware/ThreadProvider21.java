package com.github.thunkware;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

final class ThreadProvider21 implements ThreadProvider {

    private static final MethodHandle isVirtual;
    private static final MethodHandle startVirtualThread;
    private static final MethodHandle ofVirtual;
    private static final MethodHandle unstarted;
    private static final MethodHandle newThreadPerTaskExecutor;
    private static final MethodHandle newVirtualThreadPerTaskExecutor;

    static {
        final Lookup publicLookup = MethodHandles.publicLookup();
        isVirtual = invoke(() -> {
            final MethodType methodType = MethodType.methodType(boolean.class);
            return publicLookup.findVirtual(Thread.class, "isVirtual", methodType);
        });

        startVirtualThread = invoke(() -> {
            final MethodType methodType = MethodType.methodType(Thread.class, Runnable.class);
            return publicLookup.findStatic(Thread.class, "startVirtualThread", methodType);
        });

        ofVirtual = invoke(() -> {
            final Class<?> ofVirtualClass = Class.forName("java.lang.Thread$Builder$OfVirtual");
            final MethodType methodType = MethodType.methodType(ofVirtualClass);
            return publicLookup.findStatic(Thread.class, "ofVirtual", methodType);
        });

        unstarted = invoke(() -> {
            final Class<?> ofVirtualClass = Class.forName("java.lang.Thread$Builder$OfVirtual");
            final MethodType methodType = MethodType.methodType(Thread.class, Runnable.class);
            return publicLookup.findVirtual(ofVirtualClass, "unstarted", methodType);
        });

        newThreadPerTaskExecutor = invoke(() -> {
            final MethodType methodType = MethodType.methodType(ExecutorService.class, ThreadFactory.class);
            return publicLookup.findStatic(Executors.class, "newThreadPerTaskExecutor", methodType);
        });

        newVirtualThreadPerTaskExecutor = invoke(() -> {
            final MethodType methodType = MethodType.methodType(ExecutorService.class);
            return publicLookup.findStatic(Executors.class, "newVirtualThreadPerTaskExecutor", methodType);
        });

    }

    @Override
    public boolean hasVirtualThreads() {
        return true;
    }

    @Override
    public final boolean isVirtual(final Thread thread) {
        try {
            return (boolean) isVirtual.invoke(thread);
        } catch (final Throwable e) {
            throw rethrow(e);
        }
    }

    @Override
    public Thread startVirtualThread(final Runnable task) {
        return invoke(() -> (Thread) startVirtualThread.invoke(task));
    }

    @Override
    public Thread unstartedVirtualThread(Runnable task) {
        return invoke(() -> {
            Object builder = ofVirtual.invoke();
            return (Thread) unstarted.invoke(builder, task);
        });
    }

    @Override
    public ExecutorService newThreadPerTaskExecutor(ThreadFactory threadFactory) {
        return invoke(() -> (ExecutorService) newThreadPerTaskExecutor.invoke(threadFactory));
    }

    @Override
    public ExecutorService newVirtualThreadPerTaskExecutor() {
        return invoke(() -> (ExecutorService) newVirtualThreadPerTaskExecutor.invoke());
    }

    private static RuntimeException rethrow(final Throwable throwable) {
        return sneakyThrow(throwable);
    }

    @SuppressWarnings("unchecked")
    private static <R, T extends Throwable> R sneakyThrow(final Throwable throwable) throws T {
        throw (T) throwable;
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

}
