package test.object;

class ManyToManyBatchTest extends ManyToManyTest {
    public static void main(String[] args) {
        new ManyToManyBatchTest().runBenchmark();
    }

    @Override
    protected long testSum() {
        Writer.runGroupAsync(RING_BUFFER);
        return BatchReader.runGroupAsync(BATCH_SIZE, RING_BUFFER);
    }
}
