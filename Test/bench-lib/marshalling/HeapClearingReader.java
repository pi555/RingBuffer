package bench.marshalling;

import bench.BenchmarkThreadGroup;
import eu.menzani.benchmark.Profiler;
import org.ringbuffer.marshalling.HeapClearingRingBuffer;

import static eu.menzani.struct.HeapOffsets.INT;

class HeapClearingReader extends HeapReader {
    static long runGroupAsync(HeapClearingRingBuffer ringBuffer, Profiler profiler) {
        BenchmarkThreadGroup group = new BenchmarkThreadGroup(numIterations -> new HeapClearingReader(numIterations, ringBuffer));
        group.start(null);
        group.waitForCompletion(profiler);
        return group.getReaderSum();
    }

    static long runAsync(int numIterations, HeapClearingRingBuffer ringBuffer, Profiler profiler) {
        HeapClearingReader reader = new HeapClearingReader(numIterations, ringBuffer);
        reader.startNow(null);
        reader.waitForCompletion(profiler);
        return reader.getSum();
    }

    private HeapClearingReader(int numIterations, HeapClearingRingBuffer ringBuffer) {
        super(numIterations, ringBuffer);
    }

    @Override
    long collect() {
        HeapClearingRingBuffer ringBuffer = getHeapClearingRingBuffer();
        long sum = 0L;
        for (int numIterations = getNumIterations(); numIterations > 0; numIterations--) {
            sum += ringBuffer.readInt(ringBuffer.take(INT));
        }
        return sum;
    }
}
