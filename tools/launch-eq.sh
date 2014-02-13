#!/usr/bin/env bash

echo -n "Starting the GPIG-C core (Core.jar)... "
(while true; do sleep 10000; done) | java -jar Core.jar > /dev/null 2>&1 &
CORE_PID=$!
echo "pid=$CORE_PID"

sleep 1

echo -n "Starting the earthquake emitter (EarthEmitter.jar)... "
(while true; do sleep 10000; done) | LD_LIBRARY_PATH=. java -jar EarthEmitter.jar > /dev/null 2>&1 &
EMIT_PID=$!
echo "pid=$EMIT_PID"

echo "Press enter to stop all components"
read

echo "Stopping the earthquake emitter (EarthEmitter.jar)..."
kill -9 $EMIT_PID

echo "Stopping the GPIG-C core (Core.jar)..."
kill -9 $CORE_PID

echo "Done"
