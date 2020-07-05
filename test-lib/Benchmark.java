/*
 * Copyright 2020 Francesco Menzani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class Benchmark {
    private static Benchmark instance;

    public static Benchmark current() {
        return instance;
    }

    private final List<Result> results = new ArrayList<>(5);

    protected Benchmark() {
        instance = this;
    }

    Result getResult(String profilerName, Profiler.ResultFormat format) {
        for (Result result : results) {
            if (result.profilerName.equals(profilerName)) {
                return result;
            }
        }
        Result result = new Result(profilerName, format);
        results.add(result);
        return result;
    }

    protected int getWarmupRepeatTimes() {
        return getRepeatTimes();
    }

    protected abstract int getRepeatTimes();

    protected abstract int getNumIterations();

    public void runBenchmark() {
        int numIterations = getNumIterations();
        for (int i = getWarmupRepeatTimes(); i > 0; i--) {
            test(numIterations);
        }
        results.clear();
        for (int i = getRepeatTimes(); i > 0; i--) {
            test(numIterations);
        }
        for (Result result : results) {
            result.report();
        }
    }

    protected abstract void test(int i);

    static class Result {
        private static final DecimalFormat doubleFormat = new DecimalFormat("#.##");

        final String profilerName;
        private final Profiler.ResultFormat format;

        private long sum;
        private double count;
        private long minimum = Long.MAX_VALUE;
        private long maximum;

        Result(String profilerName, Profiler.ResultFormat format) {
            this.profilerName = profilerName;
            this.format = format;
        }

        synchronized void update(long value) {
            sum += value;
            count++;
            if (value < minimum) {
                minimum = value;
            }
            if (value > maximum) {
                maximum = value;
            }
        }

        void report() {
            long sum;
            double count;
            long minimum;
            long maximum;
            synchronized (this) {
                sum = this.sum;
                count = this.count;
                minimum = this.minimum;
                maximum = this.maximum;
            }
            double average = sum / count;
            String report = profilerName + ": " + formatDouble(average);
            if (count > 1D && maximum != 0L) {
                double absoluteVariance = Math.max(maximum - average, average - minimum);
                long relativeVariance = Math.round(absoluteVariance / average * 100D);
                report += " ± " + relativeVariance + '%';
            }
            System.out.println(report);
        }

        private String formatDouble(double value) {
            switch (format) {
                case TIME:
                    if (value < 2_000D) {
                        return doubleFormat.format(value) + "ns";
                    }
                    if (value < 2_000_000D) {
                        return doubleFormat.format(value / 1_000D) + "us";
                    }
                    return doubleFormat.format(value / 1_000_000D) + "ms";
                case THROUGHPUT:
                    if (value < 1_000D) {
                        return doubleFormat.format(1_000D / value) + "M msg/sec";
                    }
                    if (value < 1_000_000D) {
                        return doubleFormat.format(1_000_000D / value) + "K msg/sec";
                    }
                    return doubleFormat.format(1_000_000_000D / value) + " msg/sec";
                default:
                    throw new AssertionError();
            }
        }
    }
}
