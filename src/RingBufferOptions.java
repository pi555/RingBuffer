package eu.menzani.ringbuffer;

import java.util.function.Supplier;

public class RingBufferOptions<T> {
    public static <T> RingBufferOptions<T> empty(int capacity) {
        RingBufferOptions<T> options = new RingBufferOptions<>();
        options.capacity = capacity;
        return options;
    }

    public static <T> RingBufferOptions<T> empty(int capacity, T dummyElement) {
        RingBufferOptions<T> options = new RingBufferOptions<>();
        options.capacity = capacity;
        options.dummyElement = dummyElement;
        return options;
    }

    public static <T> RingBufferOptions<T> prefilled(int capacity, Supplier<? extends T> filler) {
        RingBufferOptions<T> options = new RingBufferOptions<>();
        options.capacity = capacity;
        options.filler = filler;
        return options;
    }

    private int capacity;
    private Supplier<? extends T> filler;
    private T dummyElement;
    private BusyWaitStrategy writeBusyWaitStrategy;
    private BusyWaitStrategy readBusyWaitStrategy;

    public RingBufferOptions<T> withWriteBusyWaitStrategy(BusyWaitStrategy writeBusyWaitStrategy) {
        this.writeBusyWaitStrategy = writeBusyWaitStrategy;
        return this;
    }

    public RingBufferOptions<T> withReadBusyWaitStrategy(BusyWaitStrategy readBusyWaitStrategy) {
        this.readBusyWaitStrategy = readBusyWaitStrategy;
        return this;
    }

    int getCapacity() {
        if (capacity < 2) {
            throw new IllegalArgumentException("capacity must be at least 2, but is " + capacity);
        }
        return capacity;
    }

    int getCapacityMinusOne() {
        return capacity - 1;
    }

    Object[] newEmptyBuffer() {
        if (filler != null) {
            throw new IllegalArgumentException("A garbage collected ring buffer cannot be pre-filled.");
        }
        return new Object[capacity];
    }

    Object[] newBuffer() {
        Object[] buffer = new Object[capacity];
        if (filler != null) {
            for (int i = 0; i < capacity; i++) {
                buffer[i] = filler.get();
            }
        }
        return buffer;
    }

    BusyWaitStrategy getWriteBusyWaitStrategy() {
        if (writeBusyWaitStrategy == null) {
            return HintBusyWaitStrategy.INSTANCE;
        }
        return writeBusyWaitStrategy;
    }

    BusyWaitStrategy getReadBusyWaitStrategy() {
        if (readBusyWaitStrategy == null) {
            return HintBusyWaitStrategy.INSTANCE;
        }
        return readBusyWaitStrategy;
    }

    T getDummyElement() {
        if (filler != null) {
            return filler.get();
        }
        return dummyElement;
    }

    boolean isPrefilled() {
        return filler != null;
    }
}
