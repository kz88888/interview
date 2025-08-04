#!/bin/bash

# Change to the script directory
cd "$(dirname "$0")"

# Check if JAR exists
if [ ! -f "target/TestClient-1.0-SNAPSHOT-jar-with-dependencies.jar" ]; then
    echo "JAR file not found. Building project..."
    mvn clean package
fi

# Create logs directory if it doesn't exist
mkdir -p logs

# Run the TestClient application
java -jar target/TestClient-1.0-SNAPSHOT-jar-with-dependencies.jar