package bench.marshalling;

import eu.menzani.benchmark.Profiler;
import org.ringbuffer.marshalling.HeapRingBuffer;

public class ManyWritersHeapBlockingContentionBenchmark extends RingBufferBenchmark {
    public static class Holder {
        public static final HeapRingBuffer RING_BUFFER =
                HeapRingBuffer.withCapacity(BLOCKING_SIZE)
                        .oneReader()
                        .manyWriters()
                        .blocking()
                        .build();
    }

    public static void main(String[] args) {
        new ManyWritersHeapBlockingContentionBenchmark().runBenchmark();
    }

    @Override
    protected long getSum() {
        return MANY_WRITERS_SUM;
    }

    @Override
    protected long measure() {
        Profiler profiler = createThroughputProfiler(TOTAL_ELEMENTS);
        SynchronizedHeapWriter.startGroupAsync(getRingBuffer(), profiler);
        return HeapReader.runAsync(TOTAL_ELEMENTS, getRingBuffer(), profiler);
    }

    HeapRingBuffer getRingBuffer() {
        return Holder.RING_BUFFER;
    }
}
