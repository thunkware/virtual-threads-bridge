package io.github.thunkware.vt.bridge;

import java.util.concurrent.Callable;

/**
 * Class to implement a NamedCallable that sets
 * the thread name, executes the Callable, then
 * resets the thread name
 */
public class ThreadNameCallable<V> implements Callable<V> {

    private final String threadName;
    private final Callable<V> callable;

    public ThreadNameCallable(String threadName, Callable<V> callable) {
        if (threadName == null) {
            throw new IllegalArgumentException("threadName cannot be null");
        }

        if (callable == null) {
            throw new IllegalArgumentException("callable cannot be null");
        }

        this.threadName = threadName;
        this.callable = callable;
    }

    @Override
    public V call() throws Exception {
        Thread currentThread = Thread.currentThread();
        String originalThreadName = currentThread.getName();

        try {
            currentThread.setName(threadName);
            return callable.call();
        } finally {
            currentThread.setName(originalThreadName);
        }
    }
}
