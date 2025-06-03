package io.github.thunkware.vt.bridge;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class ConditionalBuilderTest {

    @Test
    void conditionalOnSafeVirtualThreads() {
        try (final MockedStatic<ThreadTool> mocked = mockStatic(ThreadTool.class)) {
            mocked.when(() -> ThreadTool.ifSafeVirtualThreads(any()))
                  .thenCallRealMethod();
            mocked.when(ThreadTool::hasSafeVirtualThreads)
                  .thenReturn(true);

            final int result = ThreadTool.ifSafeVirtualThreads(() -> 1)
                                         .orElseGet(() -> 2);
            assertThat(result).isEqualTo(1);
        }
    }

    @Test
    void conditionalOnVirtualThreads() {
        try (final MockedStatic<ThreadTool> mocked = mockStatic(ThreadTool.class)) {
            mocked.when(() -> ExecutorTool.ifVirtualThreads(any()))
                  .thenCallRealMethod();

            final int result = ExecutorTool.ifVirtualThreads(() -> 1)
                                           .orElseGet(() -> 2);
            assertThat(result).isEqualTo(2);
        }
    }

}
