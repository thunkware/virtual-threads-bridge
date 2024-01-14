package com.github.thunkware;

import com.github.thunkware.ThreadTool.Builder;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

interface ThreadProvider {
    public boolean hasVirtualThreads();

    public boolean isVirtual(Thread thread);

    public Thread startVirtualThread(Runnable task);

    public Thread unstartedVirtualThread(Runnable task);

    public ExecutorService newThreadPerTaskExecutor(ThreadFactory threadFactory);

    public ExecutorService newVirtualThreadPerTaskExecutor();

    public Builder.OfPlatform ofPlatform();

    public Builder.OfVirtual ofVirtual();

    static class ThreadProviderFactory {
        private static ThreadProvider threadProvider;

        // to avoid ugly ExceptionInInitializerError on error, init in a method, not static initializer
        static synchronized ThreadProvider getThreadProvider() {
            if (threadProvider != null) {
                return threadProvider;
            }

            boolean isJava21;
            try {
                Class.forName("java.lang.Thread$Builder$OfPlatform");
                isJava21 = true;
            } catch (ClassNotFoundException e) {
                isJava21 = false;
            }

            try {
                threadProvider = createThreadProvider(isJava21);
            } catch (Exception e) {
                throw new IllegalStateException("Cannot create ThreadProvider", e);
            }

            return threadProvider;
        }

        private static ThreadProvider createThreadProvider(boolean isJava21) throws Exception {
            if (isJava21) {
                // even though we setup multi-release jar, for easier development, use reflection to create. Some IDEs
                // complains if there are two classes with the same fq name  (even though in different release dirs)
                Class<?> clazz = Class.forName("com.github.thunkware.ThreadProvider21");
                Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
                return (ThreadProvider) constructor.newInstance();
            } else {
                return new ThreadProvider8();
            }
        }

    }

}
