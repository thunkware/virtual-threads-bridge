package io.github.thunkware;

import java.lang.reflect.Constructor;

class ThreadProviderFactory {
    private static final Boolean isJava21 = isJava21();
    private static ThreadProvider threadProvider;

    // to avoid ugly ExceptionInInitializerError on error, init in a method, not static initializer
    static synchronized ThreadProvider getThreadProvider() {
        if (threadProvider != null) {
            return threadProvider;
        }

        threadProvider = createThreadProvider();
        return threadProvider;
    }

    static boolean isJava21() {
        if (isJava21 != null) {
            return isJava21;
        }
        try {
            Class.forName("java.lang.Thread$Builder$OfPlatform");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    static ThreadProvider createThreadProvider() {
        try {
            return createThreadProvider(isJava21);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create ThreadProvider", e);
        }
    }

    private static ThreadProvider createThreadProvider(boolean isJava21) throws Exception {
        if (isJava21) {
            // even though we setup multi-release jar, for easier development, use reflection to create. Some IDEs
            // complains if there are two classes with the same fq name  (even though those are in different release dirs)
            Class<?> clazz = Class.forName("io.github.thunkware.ThreadProvider21");
            Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
            return (ThreadProvider) constructor.newInstance();
        } else {
            return new ThreadProvider8();
        }
    }

}
