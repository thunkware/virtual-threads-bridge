package io.github.thunkware;

import io.github.thunkware.ThreadTool.Builder.OfPlatform;
import io.github.thunkware.ThreadTool.Builder.OfVirtual;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

final class ThreadProvider21 implements ThreadProvider {

    private final ThreadProviderConfig config = new ThreadProviderConfig();

    @Override
    public ThreadProviderConfig getConfig() {
        return config;
    }

    @Override
    public boolean hasVirtualThreads() {
        return true;
    }

    @Override
    public final boolean isVirtual(final Thread thread) {
        return thread.isVirtual();
    }

    @Override
    public Thread startVirtualThread(final Runnable task) {
        return Thread.startVirtualThread(task);
    }

    @Override
    public Thread unstartedVirtualThread(Runnable task) {
        return Thread.ofVirtual().unstarted(task);
    }

    @Override
    public ExecutorService newThreadPerTaskExecutor(ThreadFactory threadFactory) {
        return Executors.newThreadPerTaskExecutor(threadFactory);
    }

    @Override
    public ExecutorService newVirtualThreadPerTaskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    public OfPlatform ofPlatform() {
        return new ThreadBuilders21.PlatformThreadBuilder();
    }

    @Override
    public OfVirtual ofVirtual() {
        return new ThreadBuilders21.VirtualThreadBuilder();
    }

}
