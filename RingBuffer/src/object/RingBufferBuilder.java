package org.ringbuffer.object;

import org.ringbuffer.wait.BusyWaitStrategy;

public final class RingBufferBuilder<T> extends ObjectRingBufferBuilder<T> {
    private boolean gcEnabled;

    RingBufferBuilder(int capacity) {
        super(capacity);
    }

    @Override
    public RingBufferBuilder<T> oneWriter() {
        super.oneWriter0();
        return this;
    }

    @Override
    public RingBufferBuilder<T> manyWriters() {
        super.manyWriters0();
        return this;
    }

    @Override
    public RingBufferBuilder<T> oneReader() {
        super.oneReader0();
        return this;
    }

    @Override
    public RingBufferBuilder<T> manyReaders() {
        super.manyReaders0();
        return this;
    }

    @Override
    public RingBufferBuilder<T> blocking() {
        super.blocking0();
        return this;
    }

    @Override
    public RingBufferBuilder<T> blocking(BusyWaitStrategy busyWaitStrategy) {
        super.blocking0(busyWaitStrategy);
        return this;
    }

    @Override
    public RingBufferBuilder<T> discarding() {
        super.discarding0();
        return this;
    }

    /**
     * Do not support the {@code null} element.
     */
    @Override
    public LockfreeRingBufferBuilder<T> lockfree() {
        super.lockfree0();
        return new LockfreeRingBufferBuilder<>(this);
    }

    @Override
    public RingBufferBuilder<T> waitingWith(BusyWaitStrategy busyWaitStrategy) {
        super.waitingWith0(busyWaitStrategy);
        return this;
    }

    @Override
    public RingBufferBuilder<T> copyClass() {
        super.copyClass0();
        return this;
    }

    public RingBufferBuilder<T> withGC() {
        gcEnabled = true;
        return this;
    }

    @Override
    protected ObjectRingBuffer<T> create(RingBufferConcurrency concurrency, RingBufferType type) {
        switch (concurrency) {
            case VOLATILE:
                switch (type) {
                    case CLEARING:
                        if (gcEnabled) {
                            if (copyClass) {
                                return instantiateCopy(VolatileGCRingBuffer.class);
                            }
                            return new VolatileGCRingBuffer<>(this);
                        }
                        if (copyClass) {
                            return instantiateCopy(VolatileRingBuffer.class);
                        }
                        return new VolatileRingBuffer<>(this);
                    case BLOCKING:
                        if (gcEnabled) {
                            if (copyClass) {
                                return instantiateCopy(VolatileBlockingGCRingBuffer.class);
                            }
                            return new VolatileBlockingGCRingBuffer<>(this);
                        }
                        if (copyClass) {
                            return instantiateCopy(VolatileBlockingRingBuffer.class);
                        }
                        return new VolatileBlockingRingBuffer<>(this);
                    case DISCARDING:
                        if (gcEnabled) {
                            if (copyClass) {
                                return instantiateCopy(VolatileDiscardingGCRingBuffer.class);
                            }
                            return new VolatileDiscardingGCRingBuffer<>(this);
                        }
                        if (copyClass) {
                            return instantiateCopy(VolatileDiscardingRingBuffer.class);
                        }
                        return new VolatileDiscardingRingBuffer<>(this);
                }
            case ATOMIC_READ:
                switch (type) {
                    case CLEARING:
                        if (gcEnabled) {
                            if (copyClass) {
                                return instantiateCopy(AtomicReadGCRingBuffer.class);
                            }
                            return new AtomicReadGCRingBuffer<>(this);
                        }
                        if (copyClass) {
                            return instantiateCopy(AtomicReadRingBuffer.class);
                        }
                        return new AtomicReadRingBuffer<>(this);
                    case BLOCKING:
                        if (gcEnabled) {
                            if (copyClass) {
                                return instantiateCopy(AtomicReadBlockingGCRingBuffer.class);
                            }
                            return new AtomicReadBlockingGCRingBuffer<>(this);
                        }
                        if (copyClass) {
                            return instantiateCopy(AtomicReadBlockingRingBuffer.class);
                        }
                        return new AtomicReadBlockingRingBuffer<>(this);
                    case DISCARDING:
                        if (gcEnabled) {
                            if (copyClass) {
                                return instantiateCopy(AtomicReadDiscardingGCRingBuffer.class);
                            }
                            return new AtomicReadDiscardingGCRingBuffer<>(this);
                        }
                        if (copyClass) {
                            return instantiateCopy(AtomicReadDiscardingRingBuffer.class);
                        }
                        return new AtomicReadDiscardingRingBuffer<>(this);
                }
            case ATOMIC_WRITE:
                switch (type) {
                    case CLEARING:
                        if (gcEnabled) {
                            if (copyClass) {
                                return instantiateCopy(AtomicWriteGCRingBuffer.class);
                            }
                            return new AtomicWriteGCRingBuffer<>(this);
                        }
                        if (copyClass) {
                            return instantiateCopy(AtomicWriteRingBuffer.class);
                        }
                        return new AtomicWriteRingBuffer<>(this);
                    case BLOCKING:
                        if (gcEnabled) {
                            if (copyClass) {
                                return instantiateCopy(AtomicWriteBlockingGCRingBuffer.class);
                            }
                            return new AtomicWriteBlockingGCRingBuffer<>(this);
                        }
                        if (copyClass) {
                            return instantiateCopy(AtomicWriteBlockingRingBuffer.class);
                        }
                        return new AtomicWriteBlockingRingBuffer<>(this);
                    case DISCARDING:
                        if (gcEnabled) {
                            if (copyClass) {
                                return instantiateCopy(AtomicWriteDiscardingGCRingBuffer.class);
                            }
                            return new AtomicWriteDiscardingGCRingBuffer<>(this);
                        }
                        if (copyClass) {
                            return instantiateCopy(AtomicWriteDiscardingRingBuffer.class);
                        }
                        return new AtomicWriteDiscardingRingBuffer<>(this);
                }
            case CONCURRENT:
                switch (type) {
                    case CLEARING:
                        if (gcEnabled) {
                            if (copyClass) {
                                return instantiateCopy(ConcurrentGCRingBuffer.class);
                            }
                            return new ConcurrentGCRingBuffer<>(this);
                        }
                        if (copyClass) {
                            return instantiateCopy(ConcurrentRingBuffer.class);
                        }
                        return new ConcurrentRingBuffer<>(this);
                    case BLOCKING:
                        if (gcEnabled) {
                            if (copyClass) {
                                return instantiateCopy(ConcurrentBlockingGCRingBuffer.class);
                            }
                            return new ConcurrentBlockingGCRingBuffer<>(this);
                        }
                        if (copyClass) {
                            return instantiateCopy(ConcurrentBlockingRingBuffer.class);
                        }
                        return new ConcurrentBlockingRingBuffer<>(this);
                    case DISCARDING:
                        if (gcEnabled) {
                            if (copyClass) {
                                return instantiateCopy(ConcurrentDiscardingGCRingBuffer.class);
                            }
                            return new ConcurrentDiscardingGCRingBuffer<>(this);
                        }
                        if (copyClass) {
                            return instantiateCopy(ConcurrentDiscardingRingBuffer.class);
                        }
                        return new ConcurrentDiscardingRingBuffer<>(this);
                }
        }
        throw new AssertionError();
    }

    @Override
    public RingBuffer<T> build() {
        return (RingBuffer<T>) super.build();
    }
}
