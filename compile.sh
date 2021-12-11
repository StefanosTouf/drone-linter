#!/usr/bin/env bash

if [ -z "$GRAALVM_HOME" ]; then
    echo 'Please set GRAALVM_HOME'
    exit 1
fi

# Ensure Graal native-image program is installed
"$GRAALVM_HOME/bin/gu" install native-image

"$GRAALVM_HOME/bin/native-image" \
    -jar target/drone-clojure-config-0.1.0-SNAPSHOT-standalone.jar \
    -H:Name=drone-linter \
    -H:+ReportExceptionStackTraces \
    --initialize-at-build-time  \
    --verbose \
    --no-fallback \
    --no-server \
    "-J-Xmx3g"
