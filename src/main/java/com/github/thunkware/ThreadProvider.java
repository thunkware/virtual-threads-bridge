package com.github.thunkware;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

interface ThreadProvider {
    public boolean hasVirtualThreads();

    public boolean isVirtual(Thread thread);

    public Thread startVirtualThread(Runnable task);

    public Thread unstartedVirtualThread(Runnable task);

    public ExecutorService newThreadPerTaskExecutor(ThreadFactory threadFactory);

    public ExecutorService newVirtualThreadPerTaskExecutor();

    static class ThreadProviderFactory {
        static final ThreadProvider threadProvider;

        static {
            boolean isJava21;
            try {
                Class.forName("java.lang.Thread$Builder$OfPlatform");
                isJava21 = true;
            } catch (ClassNotFoundException e) {
                isJava21 = false;
            }

            threadProvider = isJava21 ? new ThreadProvider21() : new ThreadProvider8();
        }

    }

}