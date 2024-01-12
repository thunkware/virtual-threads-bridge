package com.github.thunkware;


import static com.github.thunkware.ThreadFactoryBuilderProvider.ThreadFactoryBuilderFactory.threadFactoryBuilderProvider;

import com.github.thunkware.ThreadFactoryBuilderProvider.VirtualThreadFactoryBuilder;

/**
 * Utility for working with Thread Factory API from Java 21 in Java 8+
 */
public class ThreadFactoryTool {

  /**
   * Creates a new {@link VirtualThreadFactoryBuilder}. <p> On Java 8+ this builder will create a platform thread factory. <p>On Java 21+
   * this builder will create a virtual thread factory.
   * 
   * 
   * @return a new VirtualThreadFactoryBuilder
   */
  public static VirtualThreadFactoryBuilder ofVirtual() {
    return threadFactoryBuilderProvider.ofVirtual();
  }

  private ThreadFactoryTool() {
    throw new AssertionError();
  }

}
