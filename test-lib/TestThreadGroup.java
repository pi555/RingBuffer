package test;

public class TestThreadGroup {
    private final AbstractTestThread[] testThreads;

    public TestThreadGroup(TestThreadFactory testThreadFactory) {
        testThreads = new AbstractTestThread[AbstractRingBufferTest.CONCURRENCY];
        for (int i = 0; i < testThreads.length; i++) {
            testThreads[i] = testThreadFactory.newInstance(AbstractRingBufferTest.NUM_ITERATIONS);
        }
    }

    public void start() {
        for (AbstractTestThread testThread : testThreads) {
            testThread.start();
        }
        for (AbstractTestThread testThread : testThreads) {
            testThread.waitUntilReady();
        }
        for (AbstractTestThread testThread : testThreads) {
            testThread.commenceExecution();
        }
    }

    public void waitForCompletion() {
        for (AbstractTestThread testThread : testThreads) {
            testThread.waitForCompletion();
        }
    }

    public long getReaderSum() {
        long sum = 0L;
        for (AbstractTestThread testThread : testThreads) {
            sum += ((AbstractReader) testThread).getSum();
        }
        return sum;
    }

    public interface TestThreadFactory {
        AbstractTestThread newInstance(int numIterations);
    }
}