package com.github.thunkware;

import static org.apache.commons.lang3.JavaVersion.JAVA_20;
import static org.apache.commons.lang3.SystemUtils.isJavaVersionAtMost;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ThreadFactoryBuilder8Test {

  @BeforeEach
  void setUp() {
    assumeThat(isJavaVersionAtMost(JAVA_20)).isTrue();
  }

  @Test
  void testOfVirtual() {
    assertThatThrownBy(() -> ThreadTool.ofVirtual()).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void testOfPlatform() {

    assertThatThrownBy(() -> ThreadTool.ofVirtual()).isInstanceOf(UnsupportedOperationException.class);

  }

}
