
#!/bin/bash
javac -d out $(find src -name "*.java")
java -cp out main.CliMain
