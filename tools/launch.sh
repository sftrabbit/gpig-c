#!/usr/bin/env bash

echo -n "Starting the test application (b.jar)... "
java -jar b.jar > /dev/null 2>&1 & 
BJAR_PID=$!
echo "pid=$BJAR_PID"

sleep 1

echo -n "Starting the GPIG-C core (Core.jar)... "
(while true; do sleep 10000; done) | java -jar Core.jar > /dev/null 2>&1 &
CORE_PID=$!
echo "pid=$CORE_PID"

sleep 1

echo -n "Starting the data emitter (TestAppEmitter.jar)... "
(while true; do sleep 10000; done) | java -jar TestAppEmitter.jar > /dev/null 2>&1 &
EMIT_PID=$!
echo "pid=$EMIT_PID"

echo "Press enter to stop all components"
read

echo "Stopping the data emitter (TestAppEmitter.jar)..."
kill -9 $EMIT_PID

echo "Stopping the GPIG-C core (Core.jar)..."
kill -9 $CORE_PID

echo "Stopping the test application (b.jar)..."
kill -9 $BJAR_PID

echo "Done"
