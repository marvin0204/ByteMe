#!/bin/bash

if [ "$#" -ne 3 ]; then
  echo "Usage: $0 <handle> <port> <ipcport>"
  exit 1
fi

HANDLE=$1
PORT=$2
IPCPORT=$3
WHOISPORT=4000

echo "Starte Client '$HANDLE' (UDP: $PORT, IPC: $IPCPORT)..."

# Ressourcen- & Bildverzeichnis vorbereiten
mkdir -p resources
mkdir -p images

# TOML-Konfiguration schreiben
cat > resources/config.toml <<EOF
handle = "$HANDLE"
port = $PORT
ipcport = $IPCPORT
whoisport = $WHOISPORT
autoreply = "Ich bin gerade nicht verfügbar."
imagepath = "images"
EOF

# Java-Dateien kompilieren
rm -rf out
mkdir -p out
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d out @sources.txt

# Client starten
java -cp out main.Main resources/config.toml

