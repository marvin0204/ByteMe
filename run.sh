#!/bin/bash
javac -d out $(find src/main/java -name "*.java")
java -cp out main.Main

