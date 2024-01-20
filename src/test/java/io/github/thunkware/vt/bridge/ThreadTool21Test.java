package io.github.thunkware.vt.bridge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.JavaVersion.JAVA_20;
import static org.apache.commons.lang3.SystemUtils.isJavaVersionAtMost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assumptions.assumeThat;

class ThreadTool21Test {

    @BeforeEach
    void setUp() {
        assumeThat(isJavaVersionAtMost(JAVA_20)).isFalse();
    }

    @Test
    void test() {
        CountDownLatch latch0 = new CountDownLatch(1);
        Thread thread = ThreadTool.startVirtualThread(() -> {
            try {
                latch0.await();
            } catch (InterruptedException e) {
            }
        });

        assertThat(ThreadTool.hasVirtualThreads()).isTrue();
        assertThat(ThreadTool.isVirtual()).isFalse();
        assertThat(ThreadTool.isVirtual(thread))
                .isTrue();
        latch0.countDown();
        thread.interrupt();

        CountDownLatch latch = new CountDownLatch(1);
        ThreadTool.startVirtualThread(() -> {
            assertThat(ThreadTool.isVirtual()).isTrue();
            latch.countDown();
        });
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
        assertThat(ThreadTool.isVirtual(thread)).isTrue();
    }
}
