package io.github.thunkware.vt.bridge;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.lang3.JavaVersion.JAVA_20;
import static org.apache.commons.lang3.SystemUtils.isJavaVersionAtMost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

class ExecutorTool8Test {

    @BeforeEach
    void setUp() {
        assumeThat(isJavaVersionAtMost(JAVA_20)).isTrue();
    }

    @Test
    void testNewVirtualThreadPerTaskExecutor() {
        assertThat(ExecutorTool.hasVirtualThreads()).isFalse();

        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = ExecutorTool.newVirtualThreadPerTaskExecutor();
        executor.submit(latch::countDown);
        assertThatNoException().isThrownBy(() -> latch.await(1, TimeUnit.SECONDS));
        assertThat(latch.getCount()).isZero();
    }

    @Test
    void testNewVirtualThreadPerTaskExecutorShutdownNow() {
        assertThat(ExecutorTool.hasVirtualThreads()).isFalse();

        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = ExecutorTool.newVirtualThreadPerTaskExecutor();
        assertThat(executor.isShutdown()).isFalse();
        assertThat(executor.isTerminated()).isFalse();

        executor.submit(() -> latch.await(60, TimeUnit.SECONDS));
        assertThatNoException().isThrownBy(() -> latch.await(1, TimeUnit.SECONDS));
        assertThat(latch.getCount()).isOne();

        List<Runnable> tasks = executor.shutdownNow();
        assertThat(tasks).isEmpty();
        assertThat(latch.getCount()).isOne();

        assertThat(executor.isShutdown()).isTrue();
        assertThat(executor.isTerminated()).isTrue();
    }

    @Test
    void testNewVirtualThreadPerTaskExecutorAwait() throws InterruptedException {
        assertThat(ExecutorTool.hasVirtualThreads()).isFalse();

        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = ExecutorTool.newVirtualThreadPerTaskExecutor();
        assertThat(executor.isShutdown()).isFalse();
        assertThat(executor.isTerminated()).isFalse();

        executor.submit(() -> latch.await(3, TimeUnit.SECONDS));

        StopWatch stopWatch = StopWatch.createStarted();
        executor.awaitTermination(4, TimeUnit.SECONDS);
        stopWatch.stop();

        assertThat(stopWatch.getTime(TimeUnit.MILLISECONDS)).isBetween(2500L, 4500L);
    }

    @Test
    void testNewThreadPerTaskExecutor() throws InterruptedException {
        ExecutorService executor = ExecutorTool.newThreadPerTaskExecutor(Thread::new);

        CountDownLatch latch = new CountDownLatch(1);
        assertThat(executor.isShutdown()).isFalse();
        assertThat(executor.isTerminated()).isFalse();

        executor.submit(() -> latch.await(3, TimeUnit.SECONDS));

        StopWatch stopWatch = StopWatch.createStarted();
        executor.awaitTermination(4, TimeUnit.SECONDS);
        stopWatch.stop();

        assertThat(stopWatch.getTime(TimeUnit.MILLISECONDS)).isBetween(2500L, 4500L);
    }

    @Test
    void testNewSemaphoreVirtualExecutorWithAquireTimeout() throws InterruptedException {

        ExecutorService executor = ExecutorTool.newSemaphoreVirtualExecutor(1, Duration.ofMillis(100));
        assertThat(executor.isShutdown()).isFalse();
        assertThat(executor.isTerminated()).isFalse();

        CountDownLatch firstTaskStartsExecutionLatch = new CountDownLatch(1);
        CountDownLatch waitingLatch = new CountDownLatch(1);

        executor.submit(() -> {
            firstTaskStartsExecutionLatch.countDown();
            return waitingLatch.await(10, TimeUnit.SECONDS);
        });

        // Before executing the second task, waits until the first task has been started
        firstTaskStartsExecutionLatch.await(10, TimeUnit.SECONDS);
        Future<Boolean> timeoutTask = executor.submit(() -> waitingLatch.await(10, TimeUnit.SECONDS));

        assertThatThrownBy(() -> timeoutTask.get(500, TimeUnit.MILLISECONDS)).isInstanceOf(ExecutionException.class)
                .hasRootCauseInstanceOf(TimeoutException.class);

        waitingLatch.countDown();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    void testNewVirtualThreadPerTaskExecutorThreadName() throws Exception {
        ExecutorService executor = ExecutorTool.newVirtualThreadPerTaskExecutor(
                ThreadCustomizer.withNamePrefix("my-thread-"));
        verifyThreadNamePrefix(executor);
    }

    @Test
    void testNewVirtualThreadPerTaskExecutorGlobalThreadName() throws Exception {
        ThreadTool.getConfig().setThreadCustomizer(ThreadCustomizer.withNamePrefix("my-thread-"));
        ExecutorService executor = ExecutorTool.newVirtualThreadPerTaskExecutor();
        try {
            verifyThreadNamePrefix(executor);
        } finally {
            ThreadTool.getConfig().setThreadCustomizer(thread -> {
            });
        }
    }

    @Test
    void testNewVirtualThreadPerTaskExecutorThreadFactory() {
        AtomicInteger counter = new AtomicInteger();
        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("my-thread-" + counter.getAndIncrement());
            return thread;
        };
        ExecutorService executor = ExecutorTool.newVirtualThreadPerTaskExecutor(threadFactory);
        try {
            verifyThreadNamePrefix(executor);
        } finally {
            executor.shutdown();
        }
    }

    private void verifyThreadNamePrefix(ExecutorService executor) {
        CountDownLatch latch = new CountDownLatch(2);
        Future<?> future1 = executor.submit(() -> {
            assertThat(ThreadTool.isVirtual()).isFalse();
            assertThat(Thread.currentThread().getName()).isEqualTo("my-thread-0");
            latch.countDown();
        });
        assertThatNoException().isThrownBy(() -> future1.get(1, TimeUnit.SECONDS));

        Future<?> future2 = executor.submit(() -> {
            assertThat(ThreadTool.isVirtual()).isFalse();
            assertThat(Thread.currentThread().getName()).isEqualTo("my-thread-1");
            latch.countDown();
        });
        assertThatNoException().isThrownBy(() -> future2.get(1, TimeUnit.SECONDS));
        assertThatNoException().isThrownBy(() -> latch.await(1, TimeUnit.SECONDS));
    }
}
