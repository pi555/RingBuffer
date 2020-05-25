package eu.menzani.ringbuffer.marshalling;

import eu.menzani.ringbuffer.builder.DirectMarshallingRingBufferBuilder;
import eu.menzani.ringbuffer.builder.Proxy;
import eu.menzani.ringbuffer.marshalling.array.DirectByteArray;
import eu.menzani.ringbuffer.memory.Long;
import eu.menzani.ringbuffer.wait.BusyWaitStrategy;

import static eu.menzani.ringbuffer.builder.Proxy.*;

class VolatileDirectMarshallingRingBuffer implements DirectMarshallingRingBuffer {
    private final long capacity;
    private final long capacityMinusOne;
    private final DirectByteArray buffer;
    private final BusyWaitStrategy readBusyWaitStrategy;

    private long readPosition;
    private final Long writePosition;

    VolatileDirectMarshallingRingBuffer(DirectMarshallingRingBufferBuilder builder) {
        capacity = Proxy.getCapacity(builder);
        capacityMinusOne = getCapacityMinusOne(builder);
        buffer = getBuffer(builder);
        readBusyWaitStrategy = getReadBusyWaitStrategy(builder);
        writePosition = newCursor(builder);
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long next() {
        return writePosition.getPlain();
    }

    @Override
    public void writeByte(long offset, byte value) {
        buffer.putByte(offset & capacityMinusOne, value);
    }

    @Override
    public void writeChar(long offset, char value) {
        buffer.putChar(offset & capacityMinusOne, value);
    }

    @Override
    public void writeShort(long offset, short value) {
        buffer.putShort(offset & capacityMinusOne, value);
    }

    @Override
    public void writeInt(long offset, int value) {
        buffer.putInt(offset & capacityMinusOne, value);
    }

    @Override
    public void writeLong(long offset, long value) {
        buffer.putLong(offset & capacityMinusOne, value);
    }

    @Override
    public void writeBoolean(long offset, boolean value) {
        buffer.putBoolean(offset & capacityMinusOne, value);
    }

    @Override
    public void writeFloat(long offset, float value) {
        buffer.putFloat(offset & capacityMinusOne, value);
    }

    @Override
    public void writeDouble(long offset, double value) {
        buffer.putDouble(offset & capacityMinusOne, value);
    }

    @Override
    public void put(long offset) {
        writePosition.set(offset);
    }

    @Override
    public long take(long size) {
        long readPosition = this.readPosition & capacityMinusOne;
        readBusyWaitStrategy.reset();
        while (size(readPosition) < size) {
            readBusyWaitStrategy.tick();
        }
        readPosition = this.readPosition;
        this.readPosition += size;
        return readPosition;
    }

    @Override
    public byte readByte(long offset) {
        return buffer.getByte(offset & capacityMinusOne);
    }

    @Override
    public char readChar(long offset) {
        return buffer.getChar(offset & capacityMinusOne);
    }

    @Override
    public short readShort(long offset) {
        return buffer.getShort(offset & capacityMinusOne);
    }

    @Override
    public int readInt(long offset) {
        return buffer.getInt(offset & capacityMinusOne);
    }

    @Override
    public long readLong(long offset) {
        return buffer.getLong(offset & capacityMinusOne);
    }

    @Override
    public boolean readBoolean(long offset) {
        return buffer.getBoolean(offset & capacityMinusOne);
    }

    @Override
    public float readFloat(long offset) {
        return buffer.getFloat(offset & capacityMinusOne);
    }

    @Override
    public double readDouble(long offset) {
        return buffer.getDouble(offset & capacityMinusOne);
    }

    @Override
    public void advance() {}

    @Override
    public long size() {
        return size(readPosition & capacityMinusOne);
    }

    private long size(long readPosition) {
        long writePosition = this.writePosition.get() & capacityMinusOne;
        if (writePosition <= readPosition) {
            return readPosition - writePosition;
        }
        return capacity - (writePosition - readPosition);
    }

    @Override
    public boolean isEmpty() {
        return (writePosition.get() & capacityMinusOne) == (readPosition & capacityMinusOne);
    }
}