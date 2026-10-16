#!/usr/bin/env bash
set -eu

test "${JITPACK:-}" = true
: "${ANDROID_HOME:?}"
"$JAVA_HOME/bin/javac" -version | grep -E '^javac 21[.]'

# Gradle downloads missing SDK packages using the exported accepted SDK license.
mkdir -p "$ANDROID_HOME/licenses"
cat .jitpack/android-sdk-license >> "$ANDROID_HOME/licenses/android-sdk-license"

# Use the selected Java 21 instead of downloading the upstream daemon's JBR.
mv gradle/gradle-daemon-jvm.properties gradle/gradle-daemon-jvm.properties.jitpack-backup
