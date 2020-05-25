package test.object;

class ManyReadersBlockingBatchPerfTest extends ManyReadersBlockingPerfTest {
    public static void main(String[] args) {
        new ManyReadersBlockingBatchPerfTest().runBenchmark();
    }

    @Override
    protected long testSum() {
        Writer.runAsync(TOTAL_ELEMENTS, RING_BUFFER);
        return BatchReader.runGroupAsync(BATCH_SIZE, RING_BUFFER);
    }
}