package io.github.thunkware.vt.bridge;

import static org.apache.commons.lang3.JavaVersion.JAVA_20;
import static org.apache.commons.lang3.SystemUtils.isJavaVersionAtMost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void testNewSempahoreVirtualExecutorWithAquireTimeout() throws InterruptedException {

        ExecutorService executor = ExecutorTool.newSempahoreVirtualExecutor(1, Duration.ofMillis(100));
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
}
