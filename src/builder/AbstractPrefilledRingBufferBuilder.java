package eu.menzani.ringbuffer.builder;

import java.util.function.Supplier;

abstract class AbstractPrefilledRingBufferBuilder<T> extends RingBufferBuilder<T> {
    private final Supplier<? extends T> filler;

    AbstractPrefilledRingBufferBuilder(int capacity, Supplier<? extends T> filler) {
        super(capacity);
        this.filler = filler;
    }

    AbstractPrefilledRingBufferBuilder(AbstractPrefilledRingBufferBuilder<T> builder) {
        super(builder.capacity);
        filler = builder.filler;
        oneWriter = builder.oneWriter;
        oneReader = builder.oneReader;
        type = builder.type;
        writeBusyWaitStrategy = builder.writeBusyWaitStrategy;
        readBusyWaitStrategy = builder.readBusyWaitStrategy;
        memoryOrder = builder.memoryOrder;
        copyClass = builder.copyClass;
    }

    @Override
    public T[] getBuffer() {
        T[] buffer = super.getBuffer();
        for (int i = 0; i < capacity; i++) {
            buffer[i] = filler.get();
        }
        return buffer;
    }

    public T getDummyElement() {
        return filler.get();
    }
}
