package io.github.thunkware;


/**
 * {@link ThreadProvider} method or feature that can be configured with a {@link CompatibilityPolicy}
 */
public enum ThreadFeature {
    HAS_VIRTUAL_THREADS,
    IS_VIRTUAL,
    START_VIRTUAL_THREAD,
    UNSTARTED_VIRTUAL_THREAD,
    NEW_THREAD_PER_TASK_EXECUTOR,
    NEW_VIRTUAL_THREAD_PER_TASK_EXECUTOR,
    OF_PLATFORM,
    OF_VIRTUAL,
    INHERIT_INHERITABLE_THREAD_LOCALS,
}
