# virtual-threads-backport

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.thunkware/virtual-threads-backport/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.thunkware/virtual-threads-backport)
[![Javadoc](https://javadoc.io/badge2/io.github.thunkware/virtual-threads-backport/javadoc.svg)](https://javadoc.io/doc/io.github.thunkware/virtual-threads-backport)

This is a partial backport of Java21's Virtual Threads API to Java8+. Obviously only the API, not the implementation, is backported.

It allows you to more easily write code that is compatible with both Java21 virtual threads and pre-Java21 platform threads.

For example, you might have a Java8 or pre-Java21 library, and you want it to take advantage of virtual threads if run on Java21 JVM. This project allows you to write code like this:

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

The following Java21 APIs are backported:

<table>
  <tr>
    <th>Java21</th>
    <th>Backport</th>
  </tr>
  <tr>
    <td>
    <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Thread.html#isVirtual()">
    Thread.isVirtual()</a>
    </td>
    <td>
    <a href="https://github.com/thunkware/virtual-threads-backport/blob/virtual-threads-backport-0.0.2/src/main/java/io/github/thunkware/ThreadTool.java#L31">
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
    <a href="https://github.com/thunkware/virtual-threads-backport/blob/virtual-threads-backport-0.0.2/src/main/java/io/github/thunkware/ThreadTool.java#L73">
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
    <a href="https://github.com/thunkware/virtual-threads-backport/blob/virtual-threads-backport-0.0.2/src/main/java/io/github/thunkware/ThreadTool.java#L85">
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
    <a href="https://github.com/thunkware/virtual-threads-backport/blob/virtual-threads-backport-0.0.2/src/main/java/io/github/thunkware/ThreadTool.java#L52">
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
    <a href="https://github.com/thunkware/virtual-threads-backport/blob/virtual-threads-backport-0.0.2/src/main/java/io/github/thunkware/ExecutorTool.java#L37">
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
    <a href="https://github.com/thunkware/virtual-threads-backport/blob/virtual-threads-backport-0.0.2/src/main/java/io/github/thunkware/ExecutorTool.java#L51">
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
    <artifactId>virtual-threads-backport</artifactId>
    <version>0.0.2</version>
</dependency>
```

## License
This library is Open Source software released under the [MIT license](https://opensource.org/licenses/MIT).
