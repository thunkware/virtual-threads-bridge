package io.github.thunkware.vt.bridge;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

class ThreadNamePrefixingCustomizer implements ThreadCustomizer {

    private final AtomicInteger counter = new AtomicInteger();
    private final String prefix;

    public ThreadNamePrefixingCustomizer(String prefix) {
        this.prefix = Objects.requireNonNull(prefix, "prefix is required");
    }

    @Override
    public void customize(Thread unstartedThread) {
        unstartedThread.setName(prefix + counter.getAndIncrement());
    }

}
