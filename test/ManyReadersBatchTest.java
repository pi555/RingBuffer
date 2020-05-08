package test;

class ManyReadersBatchTest extends ManyReadersTest {
    public static void main(String[] args) {
        new ManyReadersBatchTest().run();
    }

    @Override
    long testSum() {
        Writer writer = Writer.startAsync(TOTAL_ELEMENTS, RING_BUFFER);
        long sum = BatchReader.runGroupAsync(BATCH_SIZE, RING_BUFFER);
        writer.reportPerformance();
        return sum;
    }
}
