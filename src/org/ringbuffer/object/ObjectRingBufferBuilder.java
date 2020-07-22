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

package org.ringbuffer.object;

import org.ringbuffer.RingBufferBuilder;
import org.ringbuffer.concurrent.AtomicArray;
import org.ringbuffer.concurrent.AtomicBooleanArray;
import org.ringbuffer.lock.Lock;
import org.ringbuffer.memory.Integer;
import org.ringbuffer.wait.BusyWaitStrategy;

import java.lang.invoke.MethodHandles;

abstract class ObjectRingBufferBuilder<T> extends RingBufferBuilder<ObjectRingBuffer<T>> {
    private static final MethodHandles.Lookup implLookup = MethodHandles.lookup();

    @Override
    protected MethodHandles.Lookup getImplLookup() {
        return implLookup;
    }

    private final int capacity;
    // All fields are copied in <init>(ObjectRingBufferBuilder<?>)

    ObjectRingBufferBuilder(int capacity) {
        validateCapacity(capacity);
        this.capacity = capacity;
    }

    ObjectRingBufferBuilder(ObjectRingBufferBuilder<?> builder) {
        super(builder);
        capacity = builder.capacity;
    }

    abstract ObjectRingBufferBuilder<?> discarding();

    void discarding0() {
        type = RingBufferType.DISCARDING;
    }

    @Override
    protected void withoutLocks0() {
        super.withoutLocks0();
        validateCapacityPowerOfTwo(capacity);
    }

    @Override
    protected Lock getWriteLock() {
        return super.getWriteLock();
    }

    @Override
    protected Lock getReadLock() {
        return super.getReadLock();
    }

    @Override
    protected BusyWaitStrategy getWriteBusyWaitStrategy() {
        return super.getWriteBusyWaitStrategy();
    }

    @Override
    protected BusyWaitStrategy getReadBusyWaitStrategy() {
        return super.getReadBusyWaitStrategy();
    }

    int getCapacity() {
        return capacity;
    }

    int getCapacityMinusOne() {
        return capacity - 1;
    }

    @SuppressWarnings("unchecked")
    T[] getBuffer() {
        return (T[]) new Object[capacity];
    }

    AtomicArray<T> getBufferArray() {
        return new AtomicArray<>(capacity);
    }

    Integer newCursor() {
        return memoryOrder.newInteger();
    }

    AtomicBooleanArray getWrittenPositions() {
        AtomicBooleanArray writtenPositions = new AtomicBooleanArray(capacity);
        writtenPositions.fill(true);
        return writtenPositions;
    }
}