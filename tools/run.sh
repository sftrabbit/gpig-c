#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
$DIR/Core.jar > /dev/null 2>&1 &
$DIR/EmitterLauncher.jar > /dev/null 2>&1 &
