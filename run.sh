#!/bin/sh

# Assumes a built uber-jar exists in current directory, and that JAVA_HOME is set.

jvmopts () {
    echo -XX:+PrintGC
    echo -XX:+UnlockExperimentalVMOptions
    echo -XX:+UnlockDiagnosticVMOptions
    echo -XX:+UseG1GC
    echo -XX:MaxGCPauseMillis=10
    echo -XX:GCPauseIntervalMillis=15
    echo -XX:+G1ParallelRSetUpdatingEnabled
    echo -XX:+G1ParallelRSetScanningEnabled
    echo -Xmx4G
    echo -XX:G1ConfidencePercent=100
    echo -Xss256k
}

$JAVA_HOME/bin/java $(jvmopts) -jar httpgctest-standalone.jar
