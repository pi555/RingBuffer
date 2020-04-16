package test.wait;

import eu.menzani.ringbuffer.wait.ArrayMultiStepBusyWaitStrategy;
import eu.menzani.ringbuffer.wait.BusyWaitStrategy;

public class TwoStepMultiStepTest extends MultiStepBusyWaitStrategyTest {
    public static void main(String[] args) {
        new TwoStepMultiStepTest().runBenchmark();
    }

    @Override
    BusyWaitStrategy getStrategy() {
        return ArrayMultiStepBusyWaitStrategy.endWith(SECOND)
                .after(FIRST, STEP_TICKS)
                .build();
    }

    @Override
    public int getNumSteps() {
        return 2;
    }
}
