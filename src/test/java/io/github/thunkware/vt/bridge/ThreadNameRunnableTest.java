package io.github.thunkware.vt.bridge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.UUID;
import org.junit.jupiter.api.Test;

public class ThreadNameRunnableTest {

    private final String[] validThreadNames = new String[] {
            "",
            " ",
            UUID.randomUUID().toString()
    };

    @Test
    public void testValidThreadNames() {
        Thread thread = Thread.currentThread();
        String originalThreadName = thread.getName();

        for (String validThreadName : validThreadNames) {
            new ThreadNameRunnable(
                    validThreadName,
                    () -> {
                        String threadName = Thread.currentThread().getName();
                        assertThat(threadName).isNotNull();
                        assertThat(threadName).isEqualTo(validThreadName);
                    })
                    .run();

            assertThat(thread.getName()).isEqualTo(originalThreadName);
        }
    }

    @Test
    public void testInvalidArguments() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new ThreadNameRunnable("test", null));

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new ThreadNameRunnable(null, () -> {}));

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new ThreadNameRunnable(null, null));
    }
}
