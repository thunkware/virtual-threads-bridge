package com.github.thunkware;

import com.github.thunkware.ThreadFactoryBuilders.PlatformThreadFactoryBuilder;
import com.github.thunkware.ThreadFactoryBuilders.ThreadFactoryBuilder;
import com.github.thunkware.ThreadFactoryBuilders.VirtualThreadFactoryBuilder;

public class ThreadFactoryBuilder8 implements ThreadFactoryBuilder {

  @Override
  public VirtualThreadFactoryBuilder ofVirtual() {
    throw new UnsupportedOperationException("not implemented yet");
  }

  @Override
  public PlatformThreadFactoryBuilder ofPlatform() {
    throw new UnsupportedOperationException("not implemented yet");
  }

}
