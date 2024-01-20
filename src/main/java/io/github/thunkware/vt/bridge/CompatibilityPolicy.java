package io.github.thunkware.vt.bridge;

/**
 * Policy on how to apply (in)compatibility
 */
public enum CompatibilityPolicy {

    /**
     * If Java21 feature is accessed on Java8+ VM, then make best-effort to be compatible.
     * e.g. Attempting to create virtual thread on Java8+ VM will result in a platform thread. 
     */
    BEST_EFFORT,
    
    /**
     * If Java21 feature is accessed on Java8+ VM, then throw exception.
     * e.g. Attempting to create virtual thread on Java8+ VM will throw exception.
     */
    THROW_EXCEPTION,
}
