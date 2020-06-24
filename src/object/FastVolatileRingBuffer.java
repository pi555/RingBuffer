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

import jdk.internal.vm.annotation.Contended;
import org.ringbuffer.AbstractRingBufferBuilder;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class FastVolatileRingBuffer<T> implements FastEmptyRingBuffer<T> {
    private static final VarHandle BUFFER = MethodHandles.arrayElementVarHandle(Object[].class);

    @Contended
    private final Object[] buffer;
    private final int capacityMinusOne;

    @Contended
    private int readPosition;
    @Contended
    private int writePosition;

    public FastVolatileRingBuffer(int capacity) {
        AbstractRingBufferBuilder.validateCapacity(capacity);
        buffer = new Object[capacity];
        capacityMinusOne = capacity - 1;
    }

    @Override
    public void put(T element) {
        BUFFER.setRelease(buffer, writePosition++ & capacityMinusOne, element);
    }

    @Override
    public T take() {
        Object element;
        int readPosition = this.readPosition++ & capacityMinusOne;
        while ((element = BUFFER.getAcquire(buffer, readPosition)) == null) {
            Thread.onSpinWait();
        }
        buffer[readPosition] = null;
        return (T) element;
    }
}
