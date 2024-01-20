package io.github.thunkware.vt.bridge;

import io.github.thunkware.vt.bridge.ThreadTool.Builder.OfPlatform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static io.github.thunkware.vt.bridge.ThreadFeature.INHERIT_INHERITABLE_THREAD_LOCALS;
import static io.github.thunkware.vt.bridge.ThreadFeature.OF_VIRTUAL;
import static io.github.thunkware.vt.bridge.ThreadFeature.START_VIRTUAL_THREAD;
import static io.github.thunkware.vt.bridge.ThreadFeature.UNSTARTED_VIRTUAL_THREAD;
import static io.github.thunkware.vt.bridge.ThreadProvider.getThreadProvider;
import static org.apache.commons.lang3.JavaVersion.JAVA_20;
import static org.apache.commons.lang3.SystemUtils.isJavaVersionAtMost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assumptions.assumeThat;

class ThreadTool8Test {

    @BeforeEach
    void setUp() {
        assumeThat(isJavaVersionAtMost(JAVA_20)).isTrue();
    }

    @AfterEach
    void tearDown() {
        getThreadProvider().getConfig().reset();
    }

    @Test
    void test() {
        assertThat(ThreadTool.hasVirtualThreads()).isFalse();
        assertThat(ThreadTool.isVirtual()).isFalse();
        assertThat(ThreadTool.isVirtual(Thread.currentThread()))
                .isFalse();

        CountDownLatch latch = new CountDownLatch(1);
        ThreadTool.startVirtualThread(latch::countDown);
        assertThatNoException().isThrownBy(() -> latch.await(1, TimeUnit.SECONDS));
        assertThat(latch.getCount()).isZero();

        CountDownLatch latch2 = new CountDownLatch(1);
        Runnable task = () -> {
            try {
                latch2.await(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
        };
        assertThat(ThreadTool.unstartedVirtualThread(task).isAlive()).isFalse();
    }

    @Test
    void testOfPlatform() {
        ThreadFactory factory = ThreadTool.ofPlatform()
                .daemon(true)
                .name("foo")
                .inheritInheritableThreadLocals(true)
                .inheritInheritableThreadLocals(false)
                .factory();
        Thread thread = factory.newThread(Thread::yield);
        assertThat(thread.isDaemon()).isTrue();
        assertThat(thread.getName()).isEqualTo("foo");
        assertThat(ThreadTool.isVirtual(thread)).isFalse();
    }

    @Test
    void testOfVirtual() {
        ThreadFactory factory = ThreadTool.ofVirtual()
                .name("foo")
                .factory();
        Thread thread = factory.newThread(Thread::yield);
        assertThat(thread.isDaemon()).isTrue();
        assertThat(thread.getName()).isEqualTo("foo");
        assertThat(ThreadTool.isVirtual(thread)).isFalse();
    }

    @Test
    void testConfigStartVirtualThread() {
        getThreadProvider().getConfig().throwExceptionWhen(START_VIRTUAL_THREAD);
        assertThatExceptionOfType(IncompatibilityException.class).isThrownBy(() -> ThreadTool.startVirtualThread(Thread::yield));
    }

    @Test
    void testConfigUnstartedVirtualThread() {
        getThreadProvider().getConfig().throwExceptionWhen(UNSTARTED_VIRTUAL_THREAD);
        assertThatExceptionOfType(IncompatibilityException.class).isThrownBy(() -> ThreadTool.unstartedVirtualThread(Thread::yield));
    }

    @Test
    void testConfigOfVirtual() {
        getThreadProvider().getConfig().throwExceptionWhen(OF_VIRTUAL);
        assertThatExceptionOfType(IncompatibilityException.class).isThrownBy(() -> ThreadTool.ofVirtual());
    }

    @Test
    void testConfigInheritTrue() {
        getThreadProvider().getConfig().throwExceptionWhen(INHERIT_INHERITABLE_THREAD_LOCALS);
        OfPlatform ofPlatform = ThreadTool.ofPlatform();
        assertThatNoException().isThrownBy(() -> ofPlatform.inheritInheritableThreadLocals(true));
    }

    @Test
    void testConfigInheritFalse() {
        getThreadProvider().getConfig().throwExceptionWhen(INHERIT_INHERITABLE_THREAD_LOCALS);
        OfPlatform ofPlatform = ThreadTool.ofPlatform();
        assertThatExceptionOfType(IncompatibilityException.class).isThrownBy(() -> ofPlatform.inheritInheritableThreadLocals(false));
    }
}
