package io.github.thunkware.vt.bridge;

import io.github.thunkware.vt.bridge.ThreadTool.Builder.OfPlatform;
import io.github.thunkware.vt.bridge.ThreadTool.Builder.OfVirtual;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

class ThreadBuilders21 {

    private ThreadBuilders21() {
    }

    static final class PlatformThreadBuilder implements OfPlatform {

        private final java.lang.Thread.Builder.OfPlatform delegate = Thread.ofPlatform();

        @Override
        public Thread unstarted(Runnable task) {
            return delegate.unstarted(task);
        }

        @Override
        public Thread start(Runnable task) {
            return delegate.start(task);
        }

        @Override
        public ThreadFactory factory() {
            return delegate.factory();
        }

        @Override
        public OfPlatform name(String name) {
            delegate.name(name);
            return this;
        }

        @Override
        public OfPlatform name(String prefix, long start) {
            delegate.name(prefix, start);
            return this;
        }

        @Override
        public OfPlatform inheritInheritableThreadLocals(boolean inherit) {
            delegate.inheritInheritableThreadLocals(inherit);
            return this;
        }

        @Override
        public OfPlatform uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
            delegate.uncaughtExceptionHandler(ueh);
            return this;
        }

        @Override
        public OfPlatform group(ThreadGroup group) {
            delegate.group(group);
            return this;
        }

        @Override
        public OfPlatform daemon(boolean on) {
            delegate.daemon(on);
            return this;
        }

        @Override
        public OfPlatform daemon() {
            delegate.daemon();
            return this;
        }

        @Override
        public OfPlatform priority(int priority) {
            delegate.priority(priority);
            return this;
        }

        @Override
        public OfPlatform stackSize(long stackSize) {
            delegate.stackSize(stackSize);
            return this;
        }

    }

    static final class VirtualThreadBuilder implements OfVirtual {

        private final java.lang.Thread.Builder.OfVirtual delegate = Thread.ofVirtual();

        @Override
        public Thread unstarted(Runnable task) {
            return delegate.unstarted(task);
        }

        @Override
        public Thread start(Runnable task) {
            return delegate.start(task);
        }

        @Override
        public ThreadFactory factory() {
            return delegate.factory();
        }

        @Override
        public OfVirtual name(String name) {
            delegate.name(name);
            return this;
        }

        @Override
        public OfVirtual name(String prefix, long start) {
            delegate.name(prefix, start);
            return this;
        }

        @Override
        public OfVirtual inheritInheritableThreadLocals(boolean inherit) {
            delegate.inheritInheritableThreadLocals(inherit);
            return this;
        }

        @Override
        public OfVirtual uncaughtExceptionHandler(UncaughtExceptionHandler ueh) {
            delegate.uncaughtExceptionHandler(ueh);
            return this;
        }
    }

}
