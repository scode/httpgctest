#!/bin/sh

# Assumes a built uber-jar exists in current directory, and that JAVA_HOME is set.

set -e

jvmopts () {
    if [ -z "$HTTPGCTEST_COLLECTOR" ]
    then
        HTTPGCTEST_COLLECTOR="g1"
    fi

    echo -XX:+UnlockExperimentalVMOptions
    echo -XX:+UnlockDiagnosticVMOptions

    #echo -Xmaxf=50
    #echo -Xminf=30
    #echo -XX:GCTimeRatio=1
    #echo -XX:-UseAdaptiveSizePolicy
    #echo -XX:+PrintTenuringDistribution
    #echo -XX:+PrintCompilation
    #echo -XX:G1ConfidencePercent=100#
    #echo -XX:G1GCPercent=100
    #echo -XX:G1YoungGenSize=50m
    #echo -XX:NewSize=15m
    #echo -XX:MaxNewSize=15m
    #echo -XX:+PrintTenuringDistribution
    #echo -XX:+PrintHeapAtGC
    #echo -XX:+PrintHeapAtGCExtended

    if [ "$HTTPGCTEST_COLLECTOR" = "g1" ]
    then
        echo -XX:+UseG1GC
        echo -XX:MaxGCPauseMillis=50
        echo -XX:GCPauseIntervalMillis=75
        #echo -XX:G1RSetSparseRegionEntries=500
        #echo -XX:+G1PrintParCleanupStats
        #echo -XX:G1PolicyVerbose=1
        #echo -XX:+PrintGCDetails
        #echo -XX:G1PrintRegionLivenessInfo=5000
        #echo -XX:+G1ParallelRSetUpdatingEnabled
        #echo -XX:+G1ParallelRSetScanningEnabled
    elif [ "$HTTPGCTEST_COLLECTOR" = "cms" ]
    then
        echo -XX:+UseConcMarkSweepGC
        #echo -XX:+CMSIncrementalMode
        echo -XX:+PrintGCDetails
    elif [ "$HTTPGCTEST_COLLECTOR" = "throughput" ]
    then
        echo >/dev/null #prevent syntax error
    else
        echo "unknown collector: $HTTPGCTEST_COLLECTOR" >&2
        exit 1
    fi

    echo -XX:+CITime
    echo -Djava.net.preferIPv4Stack=true
    echo -XX:+PrintGC
    echo -XX:+PrintGCTimeStamps
    echo -XX:+PrintCommandLineFlags

    if ! [ -z "$HTTPGCTEST_LOGGC" ]
    then
        echo -Xloggc:$HTTPGCTEST_LOGGC
    fi
    echo -Xms50M
    echo -Xmx2G
    echo -Xss256k
}

$JAVA_HOME/bin/java $(jvmopts) -jar target/httpgctest-1.0-SNAPSHOT.jar
