package io.github.thunkware.vt.bridge;

import java.util.function.Supplier;

/**
 * Builder for creating threads, executors or objects conditional on virtual threads
 */
public class ConditionalBuilder<T> {

    private boolean condition;
    private Supplier<T> positiveSupplier;

    public static <T> ConditionalBuilder<T> conditionalOn(boolean condition) {
        return new ConditionalBuilder<>(condition);
    }

    ConditionalBuilder(boolean condition) {
        this.condition = condition;
    }

    public ConditionalBuilder<T> ifTrue(Supplier<T> positiveSupplier) {
        Supplier<T> exceptionSupplier = () -> {
            throw new IllegalStateException();
        };
        this.positiveSupplier = condition ? positiveSupplier : exceptionSupplier;
        return this;
    }

    public T orElseGet(Supplier<T> negativeSupplier) {
        return condition ? positiveSupplier.get() : negativeSupplier.get();
    }
}
