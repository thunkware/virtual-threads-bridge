package io.github.thunkware;

import io.github.thunkware.ThreadTool.Builder.OfPlatform;
import io.github.thunkware.ThreadTool.Builder.OfVirtual;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Defines static methods to create platform and virtual thread builders.
 */
class ThreadBuilders8 {
    private ThreadBuilders8() { }

    /**
     * Base class for Thread.Builder implementations.
     */
    private static class BaseThreadBuilder {
        private String name;
        private long counter;
        private UncaughtExceptionHandler uhe;

        String name() {
            return name;
        }

        long counter() {
            return counter;
        }

        UncaughtExceptionHandler uncaughtExceptionHandler() {
            return uhe;
        }

        String nextThreadName() {
            if (name != null && counter >= 0) {
                return name + (counter++);
            } else {
                return name;
            }
        }

        void setName(String name) {
            this.name = Objects.requireNonNull(name);
            this.counter = -1;
        }

        void setName(String prefix, long start) {
            Objects.requireNonNull(prefix);
            if (start < 0)
                throw new IllegalArgumentException("'start' is negative");
            this.name = prefix;
            this.counter = start;
        }

        void setUncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
            this.uhe = Objects.requireNonNull(ueh);
        }
    }

    /**
     * ThreadBuilder.OfPlatform implementation.
     */
    static final class PlatformThreadBuilder
            extends BaseThreadBuilder implements OfPlatform {
        private ThreadProviderConfig config;
        private ThreadGroup group;
        private boolean daemon;
        private boolean daemonChanged;
        private int priority;
        private long stackSize;

        PlatformThreadBuilder(ThreadProviderConfig config) {
            this.config = config;
        }

        @Override
        String nextThreadName() {
            String name = super.nextThreadName();
            return (name != null) ? name : ThreadTool.genThreadName();
        }

        @Override
        public OfPlatform name(String name) {
            setName(name);
            return this;
        }

        @Override
        public OfPlatform name(String prefix, long start) {
            setName(prefix, start);
            return this;
        }

        @Override
        public OfPlatform inheritInheritableThreadLocals(boolean inherit) {
            if (!inherit) {
                config.enforceCompatibilityPolicy(ThreadFeature.INHERIT_INHERITABLE_THREAD_LOCALS);
            }
            return this;
        }

        @Override
        public OfPlatform uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
            setUncaughtExceptionHandler(ueh);
            return this;
        }

        @Override
        public OfPlatform group(ThreadGroup group) {
            this.group = Objects.requireNonNull(group);
            return this;
        }

        @Override
        public OfPlatform daemon(boolean on) {
            daemon = on;
            daemonChanged = true;
            return this;
        }

        @Override
        public OfPlatform priority(int priority) {
            if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY)
                throw new IllegalArgumentException();
            this.priority = priority;
            return this;
        }

        @Override
        public OfPlatform stackSize(long stackSize) {
            if (stackSize < 0L)
                throw new IllegalArgumentException();
            this.stackSize = stackSize;
            return this;
        }

        @Override
        public Thread unstarted(Runnable task) {
            Objects.requireNonNull(task);
            String name = nextThreadName();
            Thread thread = new Thread(group, task, name, stackSize);
            if (daemonChanged)
                thread.setDaemon(daemon);
            if (priority != 0)
                thread.setPriority(priority);
            UncaughtExceptionHandler uhe = uncaughtExceptionHandler();
            if (uhe != null)
                thread.setUncaughtExceptionHandler(uhe);
            return thread;
        }

        @Override
        public Thread start(Runnable task) {
            Thread thread = unstarted(task);
            thread.start();
            return thread;
        }

        @Override
        public ThreadFactory factory() {
            return new PlatformThreadFactory(group, name(), counter(),
                    daemonChanged, daemon, priority, stackSize, uncaughtExceptionHandler());
        }

    }

    /**
     * ThreadBuilder.OfVirtual implementation.
     */
    static final class VirtualThreadBuilder
            extends BaseThreadBuilder implements OfVirtual {

        @Override
        public OfVirtual name(String name) {
            setName(name);
            return this;
        }

        @Override
        public OfVirtual name(String prefix, long start) {
            setName(prefix, start);
            return this;
        }

        @Override
        public OfVirtual inheritInheritableThreadLocals(boolean inherit) {
            return this;
        }

        @Override
        public OfVirtual uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
            setUncaughtExceptionHandler(ueh);
            return this;
        }

        @Override
        public Thread unstarted(Runnable task) {
            Objects.requireNonNull(task);
            Thread thread = new Thread(task, nextThreadName());
            UncaughtExceptionHandler uhe = uncaughtExceptionHandler();
            if (uhe != null)
                thread.setUncaughtExceptionHandler(uhe);
            return thread;
        }

        @Override
        public Thread start(Runnable task) {
            Thread thread = unstarted(task);
            thread.start();
            return thread;
        }

        @Override
        public ThreadFactory factory() {
            return new VirtualThreadFactory(name(), counter(), uncaughtExceptionHandler());
        }
    }

    /**
     * Base ThreadFactory implementation.
     */
    private abstract static class BaseThreadFactory implements ThreadFactory {
        private final String name;
        private final UncaughtExceptionHandler uhe;

        private final boolean hasCounter;
        private AtomicLong count = new AtomicLong();

        BaseThreadFactory(String name,
                          long start,
                          UncaughtExceptionHandler uhe)  {
            this.name = name;
            if (name != null && start >= 0) {
                this.hasCounter = true;
                this.count.set(start);
            } else {
                this.hasCounter = false;
            }
            this.uhe = uhe;
        }

        UncaughtExceptionHandler uncaughtExceptionHandler() {
            return uhe;
        }

        String nextThreadName() {
            if (hasCounter) {
                return name + count.getAndIncrement();
            } else {
                return name;
            }
        }
    }

    /**
     * ThreadFactory for platform threads.
     */
    private static class PlatformThreadFactory extends BaseThreadFactory {
        private final ThreadGroup group;
        private final boolean daemonChanged;
        private final boolean daemon;
        private final int priority;
        private final long stackSize;

        PlatformThreadFactory(ThreadGroup group,
                              String name,
                              long start,
                              boolean daemonChanged,
                              boolean daemon,
                              int priority,
                              long stackSize,
                              UncaughtExceptionHandler uhe) {
            super(name, start, uhe);
            this.group = group;
            this.daemonChanged = daemonChanged;
            this.daemon = daemon;
            this.priority = priority;
            this.stackSize = stackSize;
        }

        @Override
        String nextThreadName() {
            String name = super.nextThreadName();
            return (name != null) ? name : ThreadTool.genThreadName();
        }

        @Override
        public Thread newThread(Runnable task) {
            Objects.requireNonNull(task);
            String name = nextThreadName();
            Thread thread = new Thread(group, task, name, stackSize);
            if (daemonChanged)
                thread.setDaemon(daemon);
            if (priority != 0)
                thread.setPriority(priority);
            UncaughtExceptionHandler uhe = uncaughtExceptionHandler();
            if (uhe != null)
                thread.setUncaughtExceptionHandler(uhe);
            return thread;
        }
    }

    /**
     * ThreadFactory for virtual threads.
     */
    private static class VirtualThreadFactory extends BaseThreadFactory {

        VirtualThreadFactory(String name, long start, UncaughtExceptionHandler uhe) {
            super(name, start, uhe);
        }

        @Override
        public Thread newThread(Runnable task) {
            Objects.requireNonNull(task);
            String name = nextThreadName();
            Thread thread = new Thread(task, name);
            thread.setDaemon(true);
            UncaughtExceptionHandler uhe = uncaughtExceptionHandler();
            if (uhe != null)
                thread.setUncaughtExceptionHandler(uhe);
            return thread;
        }
    }

}
