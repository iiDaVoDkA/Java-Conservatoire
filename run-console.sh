#!/bin/bash

echo "========================================"
echo " Conservatoire Virtuel - Console App"
echo "========================================"
echo ""
echo "Starting console application..."
echo ""

mvn clean compile exec:java -Dexec.mainClass="com.music.school.ConservatoireApp"

