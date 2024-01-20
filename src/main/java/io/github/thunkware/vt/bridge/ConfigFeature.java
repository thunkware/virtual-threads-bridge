package io.github.thunkware.vt.bridge;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Associates {@link ThreadFeature} to {@link ThreadProvider} method for documentation purposes
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface ConfigFeature {

    ThreadFeature feature();
}
