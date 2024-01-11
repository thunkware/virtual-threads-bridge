package com.github.thunkware;


public class ThreadFactoryBuilder8 implements ThreadFactoryBuilder {

  @Override
  public VirtualThreadFactoryBuilder ofVirtual() {
    throw new UnsupportedOperationException("UnsupportedOperation");
  }

  @Override
  public PlatformThreadFactoryBuilder ofPlatform() {
    throw new UnsupportedOperationException("UnsupportedOperation");
  }

}
