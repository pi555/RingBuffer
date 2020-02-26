package eu.menzani.ringbuffer;

public class ManyReadersBlockingTest extends RingBufferTest {
    public ManyReadersBlockingTest() {
        super(AtomicReadBlockingOrDiscardingRingBuffer.class, 17999997000000L, RingBuffer.<Event>empty(SMALL_BUFFER_SIZE)
                .manyReaders()
                .oneWriter()
                .blocking()
                .withGC());
    }

    long run() throws InterruptedException {
        TestThreadGroup readerGroup = Reader.newGroup(ringBuffer);
        Writer writer = new Writer(TOTAL_ELEMENTS, ringBuffer);
        readerGroup.reportPerformance();
        writer.reportPerformance();
        return readerGroup.getReaderSum();
    }
}
