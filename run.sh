#!/bin/sh

# Assumes a built uber-jar exists in current directory, and that JAVA_HOME is set.

$JAVA_HOME/bin/java -XX:+PrintGC -XX:+UnlockExperimentalVMOptions -XX:+UnlockDiagnosticVMOptions -XX:+UseG1GC -XX:MaxGCPauseMillis=10 -XX:GCPauseIntervalMillis=20 -XX:+G1ParallelRSetUpdatingEnabled -XX:+G1ParallelRSetScanningEnabled -XX:+PrintGCDetails -jar httpgctest-standalone.jar
