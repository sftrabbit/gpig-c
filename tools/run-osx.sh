#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
java -XstartOnFirstThread -jar "$DIR/Core.jar" > /dev/null 2>&1 &
java -XstartOnFirstThread -jar "$DIR/EmitterLauncher.jar" > /dev/null 2>&1 &
