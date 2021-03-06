package bench;

import eu.menzani.lang.Check;
import eu.menzani.struct.PropertyConfiguration;
import eu.menzani.system.Platform;

import java.nio.file.Path;

public class Config {
    static final int concurrentProducersAndConsumers;
    public static final int ConcurrentPrefilledRingBufferObjectPoolBenchmark_concurrency;

    public static void init() {
    }

    static {
        PropertyConfiguration configuration = new PropertyConfiguration(Path.of("cfg", "benchmarks.properties"));
        int numberOfCores = Platform.getCPUInfo().getNumberOfCores();
        final String useAllCPUs = "USE_ALL_CPUS";
        concurrentProducersAndConsumers = configuration.getInt("concurrent-producers-and-consumers", numberOfCores / 2, useAllCPUs);
        ConcurrentPrefilledRingBufferObjectPoolBenchmark_concurrency = configuration.getInt("ConcurrentPrefilledRingBufferObjectPoolBenchmark-concurrency", numberOfCores, useAllCPUs);
        configuration.saveDefault();
        Check.notLesser(concurrentProducersAndConsumers, 2);
        Check.notLesser(ConcurrentPrefilledRingBufferObjectPoolBenchmark_concurrency, 1);
    }
}
