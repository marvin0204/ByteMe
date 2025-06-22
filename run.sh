#!/bin/bash

# Standard-Konfigurationspfad
CONFIG_FILE="src/main/resources/config.toml"

# Falls ein Pfad übergeben wurde, diesen verwenden
if [[ ! -z "$1" ]]; then
  CONFIG_FILE="$1"
fi

# Existiert die Konfigurationsdatei?
if [[ ! -f "$CONFIG_FILE" ]]; then
  echo "Konfigurationsdatei nicht gefunden: $CONFIG_FILE"
  exit 1
fi

# Zielordner vorbereiten
rm -rf out
mkdir -p out

# Alle Java-Dateien sammeln & kompilieren mit Paketstruktur
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d out @sources.txt

# Hauptklasse starten (mit config.toml als Argument)
java -cp out main.Main "$CONFIG_FILE"
