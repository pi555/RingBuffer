package test.object;

class PrefilledManyToManyPerfTest extends PrefilledManyToManyTest {
    public static void main(String[] args) {
        new PrefilledManyToManyPerfTest().runBenchmark();
    }

    @Override
    protected long testSum() {
        PrefilledOverwritingWriter.runGroupAsync(RING_BUFFER);
        return Reader.runGroupAsync(RING_BUFFER);
    }
}