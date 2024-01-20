# virtual-threads-bridge

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.thunkware/virtual-threads-bridge/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.thunkware/virtual-threads-bridge)
[![Javadoc](https://javadoc.io/badge2/io.github.thunkware/virtual-threads-bridge/javadoc.svg)](https://javadoc.io/doc/io.github.thunkware/virtual-threads-bridge)

This library allows you to use Java21's Virtual Threads API in Java8+. It's as if Virtual Threads API (but not the implementation) has been backported or bridged.
You could then more easily write code that is compatible with both Java21 virtual threads and pre-Java21 platform threads.

For example, you might have your own Java8 or pre-Java21 library, and you want it to take advantage of virtual threads if your library is run on Java21 JVM. With virtual-threads-bridgem you can write code like this:

```java
ExecutorService executor = ExecutorTool.hasVirtualThreads()
    ? ExecutorTool.newVirtualThreadPerTaskExecutor()
    : Executors.newCachedThreadPool();

ExecutorService concurrencyLimitedExecutor = ExecutorTool.hasVirtualThreads()
    ? ExecutorTool.newSempahoreVirtualExecutor(10)
    : Executors.newFixedThreadPool(10);

Thread thread = ThreadTool.hasVirtualThreads()
    ? ThreadTool.unstartedVirtualThread(myRunnable)
    : new Thread(myRunnable);
```

The following Java21 APIs are bridged:

<table>
  <tr>
    <th>Java21</th>
    <th>Bridge</th>
  </tr>
  <tr>
    <td>
    <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html#isVirtual()">
    Thread.isVirtual()</a>
    </td>
    <td>
    <a href="https://github.com/thunkware/virtual-threads-bridge/blob/virtual-threads-bridge-0.0.3/src/main/java/io/github/thunkware/vt/bridge/ThreadTool.java#L41">
    ThreadTool.isVirtual(Thread)</a>
    </td>
  </tr>
  <tr>
    <td>
    <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html#ofPlatform()">
    Thread.ofPlatform()
    </a>
    </td>
    <td>
    <a href="https://github.com/thunkware/virtual-threads-bridge/blob/virtual-threads-bridge-0.0.3/src/main/java/io/github/thunkware/vt/bridge/ThreadTool.java#L83">
    ThreadTool.ofPlatform()
    </a>
    </td>
  </tr>
  <tr>
    <td>
    <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html#ofVirtual()">
    Thread.ofVirtual()
    </a>
    </td>
    <td>
    <a href="https://github.com/thunkware/virtual-threads-bridge/blob/virtual-threads-bridge-0.0.3/src/main/java/io/github/thunkware/vt/bridge/ThreadTool.java#L95">
    ThreadTool.ofVirtual()
    </a>
    </td>
  </tr>
  <tr>
    <td>
    <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html#startVirtualThread(java.lang.Runnable)">
    Thread.startVirtualThread(Runnable)
    </a>
    </td>
    <td>
    <a href="https://github.com/thunkware/virtual-threads-bridge/blob/virtual-threads-bridge-0.0.3/src/main/java/io/github/thunkware/vt/bridge/ThreadTool.java#L62">
    ThreadTool.startVirtualThread(Runnable)
    </a>
    </td>
  </tr>
  <tr>
    <td>
    <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Executors.html#newThreadPerTaskExecutor(java.util.concurrent.ThreadFactory)">
    Executors.newThreadPerTaskExecutor(ThreadFactory)
    </a>
    </td>
    <td>
    <a href="https://github.com/thunkware/virtual-threads-bridge/blob/virtual-threads-bridge-0.0.3/src/main/java/io/github/thunkware/vt/bridge/ExecutorTool.java#L37">
    ExecutorTool.newThreadPerTaskExecutor(ThreadFactory)
    </a>
    </td>
  </tr>
  <tr>
    <td>
    <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Executors.html#newVirtualThreadPerTaskExecutor()">
    Executors.newVirtualThreadPerTaskExecutor()
    </a>
    </td>
    <td>
    <a href="https://github.com/thunkware/virtual-threads-bridge/blob/virtual-threads-bridge-0.0.3/src/main/java/io/github/thunkware/vt/bridge/ExecutorTool.java#L51">
    ExecutorTool.newVirtualThreadPerTaskExecutor()
    </a>
    </td>
  </tr>
</table>



## Usage

Add the library to maven pom.xml (or the equivalent in your build system):

```xml
<dependency>
    <groupId>io.github.thunkware</groupId>
    <artifactId>virtual-threads-bridge</artifactId>
    <version>0.0.3</version>
</dependency>
```

## License
This library is Open Source software released under the [MIT license](https://opensource.org/licenses/MIT).
