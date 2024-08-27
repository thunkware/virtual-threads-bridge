package io.github.thunkware.vt.bridge;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ThreadNameCallableTest {

    private final String[] validThreadNames = new String[] {
            "",
            " ",
            UUID.randomUUID().toString()
    };

    @Test
    public void testValidThreadNames() throws Exception {
        Thread thread = Thread.currentThread();
        String originalThreadName = thread.getName();

        for (String validThreadName : validThreadNames) {
            String uuid = UUID.randomUUID().toString();
            String result = new ThreadNameCallable<>(
                    validThreadName,
                    () -> {
                        String threadName = Thread.currentThread().getName();
                        assertThat(threadName).isNotNull();
                        assertThat(threadName).isEqualTo(validThreadName);
                        return uuid;
                    })
                    .call();

            assertThat(result).isEqualTo(uuid);
            assertThat(thread.getName()).isEqualTo(originalThreadName);
        }
    }

    @Test
    public void testInvalidArguments() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new ThreadNameCallable<Void>("test", null));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new ThreadNameCallable<>(null, () -> "string"));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new ThreadNameCallable<Void>(null, null));
    }
}
