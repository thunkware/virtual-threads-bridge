package com.github.thunkware;


/**
 * {@link ThreadFactoryBuilderProvider} to create ThreadFactoryBuilder with Java 8+
 */
public class ThreadFactoryBuilderProvider8 implements ThreadFactoryBuilderProvider {

  @Override
  public VirtualThreadFactoryBuilder ofVirtual() {
    throw new UnsupportedOperationException("UnsupportedOperation");
  }

  @Override
  public PlatformThreadFactoryBuilder ofPlatform() {
    throw new UnsupportedOperationException("UnsupportedOperation");
  }

}
