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
    #echo -Xminf=10
    #echo -XX:GCTimeRatio=1
    #echo -XX:-UseAdaptiveSizePolicy
    #echo -XX:+PrintTenuringDistribution
    #echo -XX:+PrintCompilation
    #echo -XX:G1ConfidencePercent=100#
    #echo -XX:G1GCPercent=100

    if [ "$HTTPGCTEST_COLLECTOR" = "g1" ]
    then
        echo -XX:+UseG1GC
        echo -XX:MaxGCPauseMillis=10
        echo -XX:GCPauseIntervalMillis=15
        echo -XX:+G1ParallelRSetUpdatingEnabled
        echo -XX:+G1ParallelRSetScanningEnabled
    elif [ "$HTTPGCTEST_COLLECTOR" = "cms" ]
    then
        echo -XX:+UseConcMarkSweepGC
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
    echo -Xmx4G
    echo -Xss256k
}

$JAVA_HOME/bin/java $(jvmopts) -jar httpgctest-standalone.jar
