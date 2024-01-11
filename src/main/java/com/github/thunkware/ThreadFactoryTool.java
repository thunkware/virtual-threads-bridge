package com.github.thunkware;


import static com.github.thunkware.ThreadFactoryBuilderProvider.ThreadFactoryBuilderFactory.threadFactoryBuilderProvider;

import com.github.thunkware.ThreadFactoryBuilderProvider.PlatformThreadFactoryBuilder;
import com.github.thunkware.ThreadFactoryBuilderProvider.VirtualThreadFactoryBuilder;

/**
 * Utility for working with Thread Factory API from Java 21 in Java 8+
 */
public class ThreadFactoryTool {

  /**
   * On Java 8+, throws an UnsupportedOperationExcption <p> On Java 21+, creates a new VirtualThreadFactoryBuilder
   * 
   * @return a new VirtualThreadFactoryBuilder
   */
  public static VirtualThreadFactoryBuilder ofVirtual() {
    return threadFactoryBuilderProvider.ofVirtual();
  }

  /**
   * On Java 8+, throws an UnsupportedOperationExcption <p> On Java 21+, creates a new PlatformThreadFactoryBuilder
   * 
   * @return a new VirtualThreadFactoryBuilder
   */
  public static PlatformThreadFactoryBuilder ofPlatform() {
    return threadFactoryBuilderProvider.ofPlatform();
  }

  private ThreadFactoryTool() {
    throw new AssertionError();
  }

}
