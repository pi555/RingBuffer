package eu.menzani.ringbuffer.builder;

import eu.menzani.ringbuffer.java.Assume;
import eu.menzani.ringbuffer.java.Number;
import eu.menzani.ringbuffer.marshalling.array.DirectByteArray;
import eu.menzani.ringbuffer.marshalling.array.SafeDirectByteArray;
import eu.menzani.ringbuffer.marshalling.array.UnsafeDirectByteArray;
import eu.menzani.ringbuffer.memory.Long;

abstract class AbstractDirectMarshallingRingBufferBuilder<T> extends AbstractBaseMarshallingRingBufferBuilder<T> {
    private final long capacity;
    // All fields are copied in <init>(AbstractDirectMarshallingRingBufferBuilder<T>)

    AbstractDirectMarshallingRingBufferBuilder(long capacity) {
        Assume.notLesser(capacity, 2L);
        if (!Number.isPowerOfTwo(capacity)) {
            throw new IllegalArgumentException("capacity must be a power of 2.");
        }
        this.capacity = capacity;
    }

    AbstractDirectMarshallingRingBufferBuilder(AbstractDirectMarshallingRingBufferBuilder<?> builder) {
        super(builder);
        capacity = builder.capacity;
    }

    long getCapacity() {
        return capacity;
    }

    long getCapacityMinusOne() {
        return capacity - 1L;
    }

    DirectByteArray getBuffer() {
        if (unsafe) {
            return new UnsafeDirectByteArray(capacity);
        }
        return new SafeDirectByteArray(capacity);
    }

    Long newCursor() {
        return memoryOrder.newLong();
    }
}