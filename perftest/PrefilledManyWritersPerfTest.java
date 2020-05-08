package test;

class PrefilledManyWritersPerfTest extends PrefilledManyWritersTest {
    public static void main(String[] args) {
        new PrefilledManyWritersPerfTest().run();
    }

    @Override
    long testSum() {
        PrefilledWriter.runGroupAsync(RING_BUFFER);
        return Reader.runAsync(TOTAL_ELEMENTS, RING_BUFFER);
    }
}
