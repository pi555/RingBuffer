package test.object;

class OneToOneBatchTest extends OneToOneTest {
    public static void main(String[] args) {
        new OneToOneBatchTest().runBenchmark();
    }

    @Override
    protected long testSum() {
        Writer.runAsync(NUM_ITERATIONS, RING_BUFFER);
        return BatchReader.runAsync(NUM_ITERATIONS, BATCH_SIZE, RING_BUFFER);
    }
}
