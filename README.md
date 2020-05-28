# RingBuffer

This library supplies ring buffer implementations that are optimized for different use cases and can be configured extensively. The following cases are covered:

- Single-Producer Single-Consumer
- Single-Producer Multiple-Consumer
- Multiple-Producer Single-Consumer
- Multiple-Producer Multiple-Consumer

**Object ring buffers** work with Java objects.

They can be prefilled, to support garbage-free operation.  
They support reading elements in batches, which improves throughput at the cost of reduced granularity.  
When full, they can either clear all elements, discard newer elements, or they can block waiting for enough space to become available.  
Only busy-waiting is supported, and the way in which it is done can be configured, so even an exception may be thrown.
If low latency is not a requirement, there are ways to busy-wait without causing excessive CPU usage.

**Marshalling ring buffers** are backed by a byte array and allow to transfer any primitive type.

Their capacity must be a power of 2. `Number.getNextPowerOfTwo()` can help.  
The byte array can reside on or off the heap. In the latter case, more than ~2GB can be allocated.

## Motivation

This library can be useful when there is a single thread executing the business logic, that has to take instructions from multiple producer threads and send results to multiple consumer threads.  
An example of this is the implementation of a trading algorithm. When latency must be low, synchronization is too expensive and so this library provides a means to communicate between threads while avoiding locks on the latency-sensitive one.

## Thread priority and affinity

First, load the native library for the current platform: `Threads.loadNativeLibrary();`
- Bind threads to specific CPU cores: `Threads.bindCurrentThreadToCPU(int)`
- Set threads priority to realtime: `Threads.setCurrentThreadPriorityToRealtime()`

## Class copying

To allow inlining of polymorphic calls, a class can be copied. This is similar to what happens with C++ templates.  
To understand when this can be useful, examine the particular implementation you are using and determine if a virtual call is polymorphic (for example this is the case when different busy-wait strategies are used in different instances of the same ring buffer).

First, add [Byte Buddy](https://bytebuddy.net/#/) to the classpath.  
Then, call `copyClass()` on the ring buffer builder.  
This functionality is available for any class by means of the `CopiedClass` class.

## Public utilities

To build a Java library for low-latency inter-thread communication, we introduced utilities.

- `Atomic*` classes expose all the features supported by `VarHandle`s while having better names.
- `Platform.current()` returns the current OS and JVM architecture.
- `Unsafe.UNSAFE` exposes the `sun.misc.Unsafe`.
- `GarbageCollectorProfiler` supports listening to GC events and logging them.
- `Assert`, `Assume` and `Ensure` allow for performant meaningful condition checking.
- `*ArrayView`s allow to view an array as `List`.

## Download

Download the [latest artifact](https://github.com/JVM-RingBuffer/RingBuffer/releases/download/v1.0/RingBuffer-1.0.jar).

**Warning.**
These features of object ring buffers have not yet been tested:
- Discarding elements when full
- `forEach()`, `contains()` and `toString()`

## Benchmarks

After building from source, you may run the benchmarks.

`test` folder: write _then_ read  
`contention-test` folder: write and read at the same time

`AbstractRingBufferTest.CONCURRENCY` is the number of threads reading and/or writing.  
You may need to adjust `AbstractTestThread.spreader` which takes care of binding each thread to its own CPU.

There is not much difference between the performance of [`ArrayBlockingQueue`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ArrayBlockingQueue.html) and the MPMC ring buffers, however the latter have more features.

## Examples

### Object ring buffers

```java
EmptyRingBuffer<Integer> producersToProcessor =
        EmptyRingBuffer.<Integer>withCapacity(5)
                .manyWriters()
                .oneReader()
                .blocking()
                .withGC()
                .build();
PrefilledClearingRingBuffer<Event> processorToConsumers =
        PrefilledRingBuffer.<Event>withCapacity(300 + 1)
                .fillWith(Event::new)
                .oneWriter()
                .manyReaders()
                .waitingWith(YieldBusyWaitStrategy.getDefault())
                .build();
Threads.loadNativeLibrary();

Runnable producer = () -> {
    for (int i = 0; i < 100; i++) {
        producersToProcessor.put(i);
    }
};
Runnable processor = () -> {
    Threads.bindCurrentThreadToCPU(5);
    Threads.setCurrentThreadPriorityToRealtime();

    for (int i = 0; i < 300; i++) {
        Integer data = producersToProcessor.take();

        int key = processorToConsumers.nextKey();
        Event event = processorToConsumers.next(key);
        event.setData(data);
        processorToConsumers.put(key);
    }
};
Runnable consumer = () -> {
    for (int i = 0; i < 100 / 5; i++) {
        processorToConsumers.takeBatch(5);
        for (int j = 0; j < 5; j++) {
            System.out.println(processorToConsumers.takePlain().getData());
        }
        processorToConsumers.advanceBatch();
    }
};

for (int i = 0; i < 3; i++) {
    new Thread(producer).start();
    new Thread(consumer).start();
}
new Thread(processor).start();
```

### Marshalling ring buffers

```java
MarshallingBlockingRingBuffer ringBuffer =
        MarshallingRingBuffer.withCapacity(Number.getNextPowerOfTwo(100))
                .oneWriter()
                .oneReader()
                .blocking(FailBusyWaitStrategy.readingTooSlow())
                .unsafe()
                .build();

int offset = ringBuffer.next(INT + CHAR);
ringBuffer.writeInt(offset, 55);
ringBuffer.writeChar(offset + INT, 'x');
ringBuffer.put(offset + INT + CHAR);

int offset = ringBuffer.take(INT + CHAR);
System.out.println(ringBuffer.readInt(offset));
System.out.println(ringBuffer.readChar(offset + INT));
ringBuffer.advance(offset + INT + CHAR);

DirectMarshallingRingBuffer ringBuffer =
        DirectMarshallingRingBuffer.withCapacity(2048)
                .manyWriters()
                .manyReaders()
                .copyClass()
                .withMemoryOrder(MemoryOrder.VOLATILE)
                .build();

long offset = ringBuffer.next();
ringBuffer.writeBoolean(offset, true); offset += BOOLEAN;
ringBuffer.writeDouble(offset, 5D); offset += DOUBLE;
ringBuffer.put(offset);

long offset = ringBuffer.take(BOOLEAN + DOUBLE);
System.out.println(ringBuffer.readBoolean(offset)); offset += BOOLEAN;
System.out.println(ringBuffer.readDouble(offset));
ringBuffer.advance();
```