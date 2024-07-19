package io.github.thunkware.vt.bridge;

import java.util.concurrent.ThreadFactory;

/**
 * Callback interface for customizing unstarted threads
 */
@FunctionalInterface
public interface ThreadCustomizer {

    void customize(Thread unstartedThread);

    /**
     * Converts this {@link ThreadCustomizer} to a {@link ThreadFactory}
     * @param delegate delegate ThreadFactory for creating a new thread
     * @return {@link ThreadFactory}
     */
    default ThreadFactory asThreadFactory(ThreadFactory delegate) {
        return runnable -> {
            Thread thread = delegate.newThread(runnable);
            this.customize(thread);
            return thread;
        };
    }

    /**
     * Create a {@link ThreadCustomizer} that prefixes thread names with give prefix and an incrementing counter value.
     * For example, "rpc-pool-" will generate thread names like "rpc-pool-0", "rpc-pool-1", etc
     * @param prefix
     * @return ThreadCustomizer
     */
    public static ThreadCustomizer withNamePrefix(String prefix) {
        return new ThreadNamePrefixingCustomizer(prefix);
    }
}
