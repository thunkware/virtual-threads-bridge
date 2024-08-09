package io.github.thunkware.vt.bridge;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.lang3.JavaVersion.JAVA_20;
import static org.apache.commons.lang3.SystemUtils.isJavaVersionAtMost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

class ExecutorTool21Test {

    private ExecutorService executorService;
    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        assumeThat(isJavaVersionAtMost(JAVA_20)).isFalse();
    }

    @Test
    void testNewVirtualThreadPerTaskExecutor() {
        assertThat(ExecutorTool.hasVirtualThreads()).isTrue();

        CountDownLatch latch = new CountDownLatch(1);
        executor = ExecutorTool.newVirtualThreadPerTaskExecutor();
        executor.submit(() -> {
            assertThat(ThreadTool.isVirtual()).isTrue();
            latch.countDown();
        });
        verifyLatchZero(latch);
        assertThat(latch.getCount()).isZero();
    }

    private static void verifyLatchZero(CountDownLatch latch) {
        assertThatNoException().isThrownBy(() -> {
            boolean reachedZero = latch.await(1, TimeUnit.SECONDS);
            assertThat(reachedZero).isTrue();
        });
    }

    @Test
    void testNewVirtualThreadPerTaskExecutorShutdownNow() throws InterruptedException {
        assertThat(ExecutorTool.hasVirtualThreads()).isTrue();

        CountDownLatch latch = new CountDownLatch(1);
        executor = ExecutorTool.newVirtualThreadPerTaskExecutor();
        assertThat(executor.isShutdown()).isFalse();
        assertThat(executor.isTerminated()).isFalse();

        executor.submit(() -> latch.await(60, TimeUnit.SECONDS));
        assertThat(latch.await(1, TimeUnit.SECONDS)).isFalse();
        assertThat(latch.getCount()).isOne();

        List<Runnable> tasks = executor.shutdownNow();
        assertThat(tasks).isEmpty();
        assertThat(latch.getCount()).isOne();

        assertThat(executor.isShutdown()).isTrue();
        assertThat(executor.isTerminated()).isTrue();
    }

    @Test
    void testNewVirtualThreadPerTaskExecutorAwait() throws InterruptedException {
        assertThat(ExecutorTool.hasVirtualThreads()).isTrue();

        CountDownLatch latch = new CountDownLatch(1);
        executor = ExecutorTool.newVirtualThreadPerTaskExecutor();
        assertThat(executor.isShutdown()).isFalse();
        assertThat(executor.isTerminated()).isFalse();

        executor.submit(() -> latch.await(3, TimeUnit.SECONDS));

        StopWatch stopWatch = StopWatch.createStarted();
        assertThat(executor.awaitTermination(4, TimeUnit.SECONDS)).isFalse();
        stopWatch.stop();

        assertThat(stopWatch.getTime(TimeUnit.MILLISECONDS)).isBetween(2500L, 4500L);
    }

    @Test
    void testNewThreadPerTaskExecutor() throws InterruptedException {
        executor = ExecutorTool.newThreadPerTaskExecutor(Thread::new);

        CountDownLatch latch = new CountDownLatch(1);
        assertThat(executor.isShutdown()).isFalse();
        assertThat(executor.isTerminated()).isFalse();

        executor.submit(() -> latch.await(3, TimeUnit.SECONDS));

        StopWatch stopWatch = StopWatch.createStarted();
        assertThat(executor.awaitTermination(4, TimeUnit.SECONDS)).isFalse();
        stopWatch.stop();

        assertThat(stopWatch.getTime(TimeUnit.MILLISECONDS)).isBetween(2500L, 4500L);
    }

    @Test
    void testNewSemaphoreVirtualExecutorRunnable() throws InterruptedException {
        executor = ExecutorTool.newSemaphoreVirtualExecutor(2);
        assertThat(executor.isShutdown()).isFalse();
        assertThat(executor.isTerminated()).isFalse();

        Semaphore semaphore = new Semaphore(3);
        AtomicInteger completedCount = new AtomicInteger();

        List<Future<?>> futures = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            Runnable runnable = () -> {
                try {
                    semaphore.acquire();
                    if (semaphore.availablePermits() < 1) {
                        throw new IllegalStateException();
                    }
                    completedCount.incrementAndGet();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    semaphore.release();
                }
            };

            Future<?> future;
            if (random.nextBoolean()) {
                // runnable
                future = executor.submit(runnable);
            } else {
                // callable
                future = executor.submit(() -> {
                    runnable.run();
                    return null;
                });
            }
            futures.add(future);
        }

        for (Future<?> future : futures) {
            assertThatNoException().isThrownBy(future::get);
        }
        assertThat(completedCount.get()).isEqualTo(100);
        executor.shutdown();
        assertThat(executor.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    void testNewSemaphoreVirtualExecutorWithAquireTimeout() throws InterruptedException {

        executor = ExecutorTool.newSemaphoreVirtualExecutor(1, Duration.ofMillis(100));
        assertThat(executor.isShutdown()).isFalse();
        assertThat(executor.isTerminated()).isFalse();

        CountDownLatch firstTaskStartsExecutionLatch = new CountDownLatch(1);
        CountDownLatch waitingLatch = new CountDownLatch(1);

        executor.submit(() -> {
            firstTaskStartsExecutionLatch.countDown();
            return waitingLatch.await(10, TimeUnit.SECONDS);
        });

        // Before executing the second task, waits until the first task has been started
        assertThat(firstTaskStartsExecutionLatch.await(10, TimeUnit.SECONDS)).isTrue();
        Future<Boolean> timeoutTask = executor.submit(() -> waitingLatch.await(10, TimeUnit.SECONDS));

        assertThatThrownBy(() -> timeoutTask.get(500, TimeUnit.MILLISECONDS)).isInstanceOf(ExecutionException.class)
                .hasRootCauseInstanceOf(TimeoutException.class);

        waitingLatch.countDown();
        executor.shutdown();
        assertThat(executor.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
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
            ThreadTool.getConfig().setThreadCustomizer(thread -> {});
        }
    }

    private void verifyThreadNamePrefix(ExecutorService executor) {
        CountDownLatch latch = new CountDownLatch(2);
        Future<?> future1 = executor.submit(() -> {
            assertThat(ThreadTool.isVirtual()).isTrue();
            assertThat(Thread.currentThread().getName()).isEqualTo("my-thread-0");
            latch.countDown();
        });
        assertThatNoException().isThrownBy(() -> future1.get(1, TimeUnit.SECONDS));

        Future<?> future2 = executor.submit(() -> {
            assertThat(ThreadTool.isVirtual()).isTrue();
            assertThat(Thread.currentThread().getName()).isEqualTo("my-thread-1");
            latch.countDown();
        });
        assertThatNoException().isThrownBy(() -> future2.get(1, TimeUnit.SECONDS));
        verifyLatchZero(latch);
    }
}
