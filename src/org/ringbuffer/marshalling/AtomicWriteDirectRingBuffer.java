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

import jdk.internal.vm.annotation.Contended;
import org.ringbuffer.lock.Lock;
import org.ringbuffer.memory.Long;
import org.ringbuffer.wait.BusyWaitStrategy;

class AtomicWriteDirectRingBuffer implements DirectClearingRingBuffer {
    private final long capacity;
    private final long capacityMinusOne;
    private final DirectByteArray buffer;
    private final Lock writeLock;
    private final BusyWaitStrategy readBusyWaitStrategy;

    @Contended("read")
    private long readPosition;
    private final Long writePosition;
    @Contended("read")
    private long cachedWritePosition;

    AtomicWriteDirectRingBuffer(DirectClearingRingBufferBuilder builder) {
        capacity = builder.getCapacity();
        capacityMinusOne = builder.getCapacityMinusOne();
        buffer = builder.getBuffer();
        writeLock = builder.getWriteLock();
        readBusyWaitStrategy = builder.getReadBusyWaitStrategy();
        writePosition = builder.newCursor();
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long next() {
        writeLock.lock();
        return writePosition.getPlain();
    }

    @Override
    public void put(long offset) {
        writePosition.set(offset);
        writeLock.unlock();
    }

    @Override
    public long take(long size) {
        long readPosition = this.readPosition & capacityMinusOne;
        readBusyWaitStrategy.reset();
        while (isNotFullEnoughCached(readPosition, size)) {
            readBusyWaitStrategy.tick();
        }
        readPosition = this.readPosition;
        this.readPosition += size;
        return readPosition;
    }

    private boolean isNotFullEnoughCached(long readPosition, long size) {
        if (size(readPosition, cachedWritePosition) < size) {
            cachedWritePosition = writePosition.get() & capacityMinusOne;
            return size(readPosition, cachedWritePosition) < size;
        }
        return false;
    }

    @Override
    public void advance() {
    }

    @Override
    public long size() {
        return size(readPosition & capacityMinusOne, writePosition.get() & capacityMinusOne);
    }

    private long size(long readPosition, long writePosition) {
        if (writePosition >= readPosition) {
            return writePosition - readPosition;
        }
        return capacity - (readPosition - writePosition);
    }

    @Override
    public boolean isEmpty() {
        return (writePosition.get() & capacityMinusOne) == (readPosition & capacityMinusOne);
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
}