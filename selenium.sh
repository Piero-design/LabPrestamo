#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
if [ "$(uname)" = Darwin ]; then
  JAVA_HOME=$(/usr/libexec/java_home)
  export JAVA_HOME
fi
exec mvn -Pselenium verify "$@"
