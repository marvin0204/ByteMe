#!/bin/bash

echo "[INFO] Discovery-Dienst wird gestartet..."

# Kompilieren (optional, falls nicht vorher gemacht)
rm -rf out
mkdir -p out
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d out @sources.txt

# Discovery-Service starten
java -cp out discovery.DiscoveryService
