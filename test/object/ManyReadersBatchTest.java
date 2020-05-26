package test.object;

import test.Profiler;

class ManyReadersBatchTest extends ManyReadersTest {
    public static void main(String[] args) {
        new ManyReadersBatchTest().runBenchmark();
    }

    @Override
    protected long testSum() {
        Profiler profiler = new Profiler(this, TOTAL_ELEMENTS);
        Writer.runAsync(TOTAL_ELEMENTS, RING_BUFFER, profiler);
        return BatchReader.runGroupAsync(BATCH_SIZE, RING_BUFFER, profiler);
    }
}
