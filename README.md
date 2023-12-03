# virtual-threads-backport

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.thunkware/virtual-threads-backport/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.thunkware/virtual-threads-backport)
[![Javadoc](https://javadoc.io/badge2/io.github.thunkware/virtual-threads-backport/javadoc.svg)](https://javadoc.io/doc/io.github.thunkware/virtual-threads-backport)

This library is a partial backport of Java 21's Virtual Threads API to Java 8+. Obviously only the API, not the implementation, is backported.

It allows you to more easily write code that is compatible with both Java 21 virtual threads and pre-Java 21 platform threads. For example, you might write code like this:

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

## Usage

Add the library to maven pom.xml (or the equivalent in your build system):

```xml
<dependency>
    <groupId>io.github.thunkware</groupId>
    <artifactId>virtual-threads-backport</artifactId>
    <version>0.0.1</version>
</dependency>
```

## License
This library is Open Source software released under the [MIT license](https://opensource.org/licenses/MIT).
