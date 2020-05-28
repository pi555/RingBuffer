/*
 * Copyright 2020 Francesco Menzani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ringbuffer.marshalling;

import org.ringbuffer.Lock;
import org.ringbuffer.memory.Integer;
import org.ringbuffer.wait.BusyWaitStrategy;

class ConcurrentMarshallingBlockingRingBuffer implements MarshallingBlockingRingBuffer {
    private final int capacity;
    private final int capacityMinusOne;
    private final ByteArray buffer;
    private final BusyWaitStrategy readBusyWaitStrategy;
    private final BusyWaitStrategy writeBusyWaitStrategy;

    private final Lock writeLock = new Lock();
    private final Lock readLock = new Lock();

    private final Integer readPosition;
    private final Integer writePosition;

    ConcurrentMarshallingBlockingRingBuffer(MarshallingBlockingRingBufferBuilder builder) {
        capacity = builder.getCapacity();
        capacityMinusOne = builder.getCapacityMinusOne();
        buffer = builder.getBuffer();
        readBusyWaitStrategy = builder.getReadBusyWaitStrategy();
        writeBusyWaitStrategy = builder.getWriteBusyWaitStrategy();
        readPosition = builder.newCursor();
        writePosition = builder.newCursor();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public int next(int size) {
        writeLock.lock();
        int writePosition = this.writePosition.getPlain() & capacityMinusOne;
        writeBusyWaitStrategy.reset();
        while (freeSpace(writePosition) <= size) {
            writeBusyWaitStrategy.tick();
        }
        return this.writePosition.getPlain();
    }

    private int freeSpace(int writePosition) {
        int readPosition = this.readPosition.get() & capacityMinusOne;
        if (writePosition >= readPosition) {
            return capacity - (writePosition - readPosition);
        }
        return readPosition - writePosition;
    }

    @Override
    public void put(int offset) {
        writePosition.set(offset);
        writeLock.unlock();
    }

    @Override
    public int take(int size) {
        readLock.lock();
        int readPosition = this.readPosition.getPlain() & capacityMinusOne;
        readBusyWaitStrategy.reset();
        while (size(readPosition) < size) {
            readBusyWaitStrategy.tick();
        }
        return this.readPosition.getPlain();
    }

    @Override
    public void advance(int offset) {
        readPosition.set(offset);
        readLock.unlock();
    }

    @Override
    public int size() {
        return size(readPosition.get() & capacityMinusOne);
    }

    private int size(int readPosition) {
        int writePosition = this.writePosition.get() & capacityMinusOne;
        if (writePosition >= readPosition) {
            return writePosition - readPosition;
        }
        return capacity - (readPosition - writePosition);
    }

    @Override
    public boolean isEmpty() {
        return (writePosition.get() & capacityMinusOne) == (readPosition.get() & capacityMinusOne);
    }

    @Override
    public void writeByte(int offset, byte value) {
        buffer.putByte(offset & capacityMinusOne, value);
    }

    @Override
    public void writeChar(int offset, char value) {
        buffer.putChar(offset & capacityMinusOne, value);
    }

    @Override
    public void writeShort(int offset, short value) {
        buffer.putShort(offset & capacityMinusOne, value);
    }

    @Override
    public void writeInt(int offset, int value) {
        buffer.putInt(offset & capacityMinusOne, value);
    }

    @Override
    public void writeLong(int offset, long value) {
        buffer.putLong(offset & capacityMinusOne, value);
    }

    @Override
    public void writeBoolean(int offset, boolean value) {
        buffer.putBoolean(offset & capacityMinusOne, value);
    }

    @Override
    public void writeFloat(int offset, float value) {
        buffer.putFloat(offset & capacityMinusOne, value);
    }

    @Override
    public void writeDouble(int offset, double value) {
        buffer.putDouble(offset & capacityMinusOne, value);
    }

    @Override
    public byte readByte(int offset) {
        return buffer.getByte(offset & capacityMinusOne);
    }

    @Override
    public char readChar(int offset) {
        return buffer.getChar(offset & capacityMinusOne);
    }

    @Override
    public short readShort(int offset) {
        return buffer.getShort(offset & capacityMinusOne);
    }

    @Override
    public int readInt(int offset) {
        return buffer.getInt(offset & capacityMinusOne);
    }

    @Override
    public long readLong(int offset) {
        return buffer.getLong(offset & capacityMinusOne);
    }

    @Override
    public boolean readBoolean(int offset) {
        return buffer.getBoolean(offset & capacityMinusOne);
    }

    @Override
    public float readFloat(int offset) {
        return buffer.getFloat(offset & capacityMinusOne);
    }

    @Override
    public double readDouble(int offset) {
        return buffer.getDouble(offset & capacityMinusOne);
    }
}
