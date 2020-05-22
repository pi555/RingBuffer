package test;

import eu.menzani.ringbuffer.PrefilledOverwritingRingBuffer;
import eu.menzani.ringbuffer.PrefilledRingBuffer;

public class PrefilledManyReadersTest extends RingBufferTest {
    public static final PrefilledOverwritingRingBuffer<Event> RING_BUFFER =
            PrefilledRingBuffer.<Event>withCapacity(NOT_ONE_TO_ONE_SIZE)
                    .fillWith(FILLER)
                    .manyReaders()
                    .oneWriter()
                    .build();

    public static void main(String[] args) {
        new PrefilledManyReadersTest().runBenchmark();
    }

    @Override
    protected int getRepeatTimes() {
        return 34;
    }

    @Override
    long getSum() {
        return ONE_TO_MANY_SUM;
    }

    @Override
    long testSum() {
        PrefilledOverwritingWriter.startAsync(TOTAL_ELEMENTS, RING_BUFFER);
        return Reader.runGroupAsync(RING_BUFFER);
    }
}
