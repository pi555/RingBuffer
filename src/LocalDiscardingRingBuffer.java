package eu.menzani.ringbuffer;

import java.util.function.Consumer;

class LocalDiscardingRingBuffer<T> implements RingBuffer<T> {
    private final int capacity;
    private final int capacityMinusOne;
    private final T[] buffer;
    private final T dummyElement;

    private int readPosition;
    private int writePosition;

    LocalDiscardingRingBuffer(RingBufferBuilder<T> builder) {
        capacity = builder.getCapacity();
        capacityMinusOne = builder.getCapacityMinusOne();
        buffer = builder.getBuffer();
        dummyElement = builder.getDummyElement();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public T next() {
        int oldWritePosition = writePosition;
        if (oldWritePosition == 0) {
            writePosition = capacityMinusOne;
        } else {
            writePosition--;
        }
        if (readPosition == writePosition) {
            writePosition = oldWritePosition;
            return dummyElement;
        }
        return buffer[oldWritePosition];
    }

    @Override
    public void put() {}

    @Override
    public void put(T element) {
        int newWritePosition;
        if (writePosition == 0) {
            newWritePosition = capacityMinusOne;
        } else {
            newWritePosition = writePosition - 1;
        }
        if (readPosition != newWritePosition) {
            buffer[writePosition] = element;
            writePosition = newWritePosition;
        }
    }

    @Override
    public T take() {
        int readPosition = this.readPosition;
        if (readPosition == 0) {
            this.readPosition = capacityMinusOne;
        } else {
            this.readPosition--;
        }
        return buffer[readPosition];
    }

    @Override
    public void fill(T[] buffer) {
        if (readPosition >= buffer.length) {
            int i = readPosition;
            readPosition -= buffer.length;
            for (int j = 0; i > readPosition; i--) {
                buffer[j++] = this.buffer[i];
            }
        } else {
            fillSplit(buffer);
        }
    }

    private void fillSplit(T[] buffer) {
        int i = readPosition;
        int j = 0;
        for (; i >= 0; i--) {
            buffer[j++] = this.buffer[i];
        }
        readPosition += capacity - buffer.length;
        for (i = capacityMinusOne; i > readPosition; i--) {
            buffer[j++] = this.buffer[i];
        }
    }

    @Override
    public void forEach(Consumer<T> action) {
        if (writePosition <= readPosition) {
            for (int i = readPosition; i > writePosition; i--) {
                action.accept(buffer[i]);
            }
        } else {
            forEachSplit(action);
        }
    }

    private void forEachSplit(Consumer<T> action) {
        for (int i = readPosition; i >= 0; i--) {
            action.accept(buffer[i]);
        }
        for (int i = capacityMinusOne; i > writePosition; i--) {
            action.accept(buffer[i]);
        }
    }

    @Override
    public boolean contains(T element) {
        if (writePosition <= readPosition) {
            for (int i = readPosition; i > writePosition; i--) {
                if (buffer[i].equals(element)) {
                    return true;
                }
            }
            return false;
        }
        return containsSplit(element);
    }

    private boolean containsSplit(T element) {
        for (int i = readPosition; i >= 0; i--) {
            if (buffer[i].equals(element)) {
                return true;
            }
        }
        for (int i = capacityMinusOne; i > writePosition; i--) {
            if (buffer[i].equals(element)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        if (writePosition <= readPosition) {
            return readPosition - writePosition;
        }
        return capacity - (writePosition - readPosition);
    }

    @Override
    public boolean isEmpty() {
        return writePosition == readPosition;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder(16);
        builder.append('[');
        if (writePosition < readPosition) {
            for (int i = readPosition; i > writePosition; i--) {
                builder.append(buffer[i].toString());
                builder.append(", ");
            }
        } else {
            toStringSplit(builder);
        }
        builder.setLength(builder.length() - 2);
        builder.append(']');
        return builder.toString();
    }

    private void toStringSplit(StringBuilder builder) {
        for (int i = readPosition; i >= 0; i--) {
            builder.append(buffer[i].toString());
            builder.append(", ");
        }
        for (int i = capacityMinusOne; i > writePosition; i--) {
            builder.append(buffer[i].toString());
            builder.append(", ");
        }
    }
}
