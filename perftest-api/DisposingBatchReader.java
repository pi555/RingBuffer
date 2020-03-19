package perftest;

import eu.menzani.ringbuffer.RingBuffer;
import eu.menzani.ringbuffer.java.Array;
import eu.menzani.ringbuffer.java.MutableLong;

class DisposingBatchReader extends BatchReader {
    DisposingBatchReader(int numIterations, RingBuffer<Event> ringBuffer) {
        super(numIterations, ringBuffer);
    }

    @Override
    void collect(MutableLong sum) {
        int numIterations = getNumIterations();
        RingBuffer<Event> ringBuffer = getRingBuffer();
        Array<Event> buffer = newReadBuffer();
        for (int i = 0; i < numIterations; i++) {
            ringBuffer.take(buffer);
            for (Event event : buffer) {
                sum.add(event.getData());
            }
            ringBuffer.dispose();
        }
    }
}
