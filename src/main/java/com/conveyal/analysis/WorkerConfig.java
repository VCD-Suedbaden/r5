package com.conveyal.analysis;

import com.conveyal.analysis.components.TaskScheduler;
import com.conveyal.file.FileStorage;
import com.conveyal.gtfs.GTFSCache;
import com.conveyal.r5.analyst.cluster.AnalysisWorker;
import com.conveyal.r5.streets.OSMCache;

import java.util.Properties;

/** Loads config information for an analysis worker and exposes it to the worker's Components and HttpControllers. */
public class WorkerConfig extends ConfigBase implements
        TaskScheduler.Config,
        OSMCache.Config,
        GTFSCache.Config,
        FileStorage.Config,
        AnalysisWorker.Config
{

    public static final String WORKER_CONFIG_FILE = "worker.conf";

    private final boolean workOffline;
    private final String  cacheDirectory;
    private final String  graphsBucket;
    private final String  awsRegion; // This shouldn't be needed on recent AWS SDKs, eventually eliminate it.
    private final String  baseBucket;
    private final boolean testTaskRedelivery;
    private final String  brokerAddress;
    private final String  brokerPort;
    private final String  pointsetsBucket;
    private final boolean autoShutdown;
    private final boolean listenForSinglePoint;
    private final String  initialGraphId;
    private final int     lightThreads;
    private final int     heavyThreads;

    private WorkerConfig (String filename) {
        this(propsFromFile(filename));
    }

    private WorkerConfig (Properties props) {
        super(props);
        workOffline = boolProp("work-offline");
        cacheDirectory = strProp("cache-dir");
        graphsBucket = workOffline ? null : strProp("graphs-bucket");
        awsRegion = workOffline ? null : strProp("aws-region");
        baseBucket = strProp("base-bucket");
        testTaskRedelivery = boolProp("test-task-redelivery");
        brokerAddress = strProp("broker-address");
        brokerPort = strProp("broker-port");
        pointsetsBucket = strProp("pointsets-bucket");
        autoShutdown = boolProp("auto-shutdown");
        listenForSinglePoint = boolProp("listen-for-single-point");
        initialGraphId = strProp("initial-graph-id");
        lightThreads = intProp("light-threads");
        heavyThreads = intProp("heavy-threads");
        exitIfErrors();
    }

    // Implementations of Component and HttpController Config interfaces
    // Note that one method can implement several Config interfaces at once.

    // TaskScheduler Config Interface

    @Override public int lightThreads () { return lightThreads; }
    @Override public int heavyThreads () { return heavyThreads; }

    // OSM abd GTFS cache config (eventually revise for consistent naming of method and variable)

    @Override public String bundleBucket() { return graphsBucket; }

    // AnalysisWorker Configuration Interface

    @Override public boolean workOffline()     { return workOffline; }
    @Override public String  cacheDirectory()  { return cacheDirectory; }
    @Override public String  graphsBucket()    { return graphsBucket; }
    @Override public String  awsRegion()       { return awsRegion; }
    @Override public String  baseBucket()      { return baseBucket; }
    @Override public String  brokerAddress()   { return brokerAddress; }
    @Override public String  brokerPort()      { return brokerPort; }
    @Override public String  pointsetsBucket() { return pointsetsBucket; }
    @Override public boolean autoShutdown()    { return autoShutdown; }
    @Override public String  initialGraphId()  { return initialGraphId; }
    @Override public boolean testTaskRedelivery()   { return testTaskRedelivery; }
    @Override public boolean listenForSinglePoint() { return listenForSinglePoint; }

    // Public factory methods - use these to construct WorkerConfig objects for readability

    public static WorkerConfig fromDefaultFile () {
        return new WorkerConfig(WORKER_CONFIG_FILE);
    }

    public static WorkerConfig fromProperties (Properties properties) {
        return new WorkerConfig(properties);
    }

}
