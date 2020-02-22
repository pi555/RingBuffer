package eu.menzani.ringbuffer;

import eu.menzani.ringbuffer.wait.BusyWaitStrategy;

class VolatileRingBuffer<T> extends RingBufferBase<T> {
    private final BusyWaitStrategy readBusyWaitStrategy;

    private int readPosition;
    private final LazyVolatileInteger writePosition = new LazyVolatileInteger();

    private int newWritePosition;

    VolatileRingBuffer(RingBufferBuilder<?> builder) {
        super(builder);
        readBusyWaitStrategy = builder.getReadBusyWaitStrategy();
    }

    @Override
    public T put() {
        int writePosition = this.writePosition.getFromSameThread();
        newWritePosition = incrementWritePosition(writePosition);
        return (T) buffer[writePosition];
    }

    @Override
    public void commit() {
        writePosition.set(newWritePosition);
    }

    @Override
    public void put(T element) {
        int writePosition = this.writePosition.getFromSameThread();
        buffer[writePosition] = element;
        this.writePosition.set(incrementWritePosition(writePosition));
    }

    @Override
    public T take() {
        int readPosition = this.readPosition;
        readBusyWaitStrategy.reset();
        while (writePosition.get() == readPosition) {
            readBusyWaitStrategy.tick();
        }
        if (readPosition == capacityMinusOne) {
            this.readPosition = 0;
        } else {
            this.readPosition++;
        }
        Object element = buffer[readPosition];
        if (gc) {
            buffer[readPosition] = null;
        }
        return (T) element;
    }

    @Override
    int getReadPosition() {
        return readPosition;
    }

    @Override
    int getWritePosition() {
        return writePosition.get();
    }
}
