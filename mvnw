#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF)
# Maven Wrapper startup script
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_PROJECTBASEDIR" ] ; then
  MAVEN_PROJECTBASEDIR=$(dirname "$0")
fi

WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_LAUNCHER="org.apache.maven.wrapper.MavenWrapperMain"

WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

# Read wrapperUrl from properties file
if [ -r "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties" ]; then
  WRAPPER_URL=$(grep "wrapperUrl" "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties" | cut -d= -f2)
fi

if [ ! -e "$WRAPPER_JAR" ]; then
  echo "Downloading $WRAPPER_URL"
  if command -v curl > /dev/null; then
    curl -o "$WRAPPER_JAR" "$WRAPPER_URL" -f -L
  elif command -v wget > /dev/null; then
    wget "$WRAPPER_URL" -O "$WRAPPER_JAR"
  else
    echo "ERROR: curl or wget is required to download Maven wrapper"
    exit 1
  fi
fi

JAVA_EXE="$JAVA_HOME/bin/java"
if [ ! -x "$JAVA_EXE" ]; then
  JAVA_EXE="java"
fi

exec "$JAVA_EXE" \
  -classpath "$WRAPPER_JAR" \
  "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" \
  "$WRAPPER_LAUNCHER" "$@"
