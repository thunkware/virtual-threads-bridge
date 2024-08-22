package io.github.thunkware.vt.bridge;

/**
 * Class to implement a NamedRunnable that sets
 * the thread name, executes the Runnable, then
 * resets the thread name
 */
public class ThreadNameRunnable implements Runnable {

    private final String threadName;
    private final Runnable task;

    /**
     * Constructor
     *
     * @param threadName the thread name
     * @param task the Runnable
     */
    public ThreadNameRunnable(String threadName, Runnable task) {
        if (threadName == null) {
            throw new NullPointerException("name cannot be null");
        }

        if (task == null) {
            throw new NullPointerException("task cannot be null");
        }

        this.threadName = threadName;
        this.task = task;
    }

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        String originalThreadName = currentThread.getName();

        try {
            currentThread.setName(threadName);
            task.run();
        } finally {
            currentThread.setName(originalThreadName);
        }
    }
}
