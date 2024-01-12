package com.github.thunkware;

import static org.apache.commons.lang3.JavaVersion.JAVA_20;
import static org.apache.commons.lang3.SystemUtils.isJavaVersionAtMost;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ThreadFactoryTool21Test {

  @BeforeEach
  void setUp() {
    assumeThat(isJavaVersionAtMost(JAVA_20)).isFalse();
  }

  @Test
  void testOfVirtual() {

    UncaughtExceptionHandler ueh = (t, e) -> {
    };

    ThreadFactory factory = ThreadFactoryTool.ofVirtual()
        .name("name")
        .inheritInheritableThreadLocals(true)
        .uncaughtExceptionHandler(ueh)
        .factory();

    assertThat(factory).isNotNull();

    Thread thread = factory.newThread(() -> {

    });

    assertThat(ThreadTool.isVirtual(thread)).isTrue();

    assertThat(thread.getName()).isEqualTo("name");
    assertThat(thread.getUncaughtExceptionHandler()).isSameAs(ueh);

  }

}
