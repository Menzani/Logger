## Features

- A clear interface enables greater extensibility.
- Configuration is done at compile-time. XML is not supported; instead, you are free to implement customization how you want it.
- Either pass `Logger` instances to constructors or use dependency injection.
  No per-class static constants referencing the logger object.
- Do work in another thread, to avoid blocking the current one
- Do work in parallel
- Thread-safety is provided out-of-the-box and required by all contracts.

## Download

JAR bundles containing the source code are available for download in the *Releases* section of this repository.

Java 8 is supported.  
Backwards compatibility is not guaranteed between major releases.

The rest of this document applies to the latest revision of version **1**.

---

## Package structure

Domain-specific classes can be found in two packages:
- `it.menzani.logger.api`
- `it.menzani.logger.impl`

The `it.menzani.logger` package contains three facilities which can be used freely:
- A `Builder<T>` interface
- Lazy initialization done well
- Profiler to measure code execution time

Also, the `it.menzani.logger.api.Cloneable<T>` interface provides a straightforward alternative to Java's cloning mechanism.

## Class hierarchy

![](Logger.png)

<sub>[UML diagram](Logger.uml)</sub>

## Tutorial

### Hello World

```java
Logger logger = new SynchronousLogger()
        .addPipeline(new Pipeline()
                .addConsumer(new ConsoleConsumer()));
logger.info("Hello, world!");
```
