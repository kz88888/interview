#!/bin/bash

# Set script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Set default port (can be overridden by command line argument)
PORT=${1:-50051}

# Check which config file to use
CONFIG_FILE="system_config.xml"
if [ ! -f "$CONFIG_FILE" ]; then
    echo "Error: system_config.xml not found!"
    echo "Please ensure system_config.xml exists in the project root directory."
    exit 1
fi

# Check if JAR exists, build if not
if [ ! -f "target/exceljava-1.0-jar-with-dependencies.jar" ]; then
    echo "Building ExcelJava project..."
    mvn clean package
    if [ $? -ne 0 ]; then
        echo "Build failed!"
        exit 1
    fi
fi

# Copy configuration file to target directory if it doesn't exist
if [ ! -f "target/$CONFIG_FILE" ]; then
    echo "Copying $CONFIG_FILE to target directory..."
    cp "$CONFIG_FILE" "target/"
fi

# Copy lib directory to target for system scope dependencies
if [ ! -d "target/lib" ]; then
    echo "Copying lib directory to target..."
    cp -r lib target/
fi

# Extract data path from config
DATA_PATH=$(grep -o '<DataPath>.*</DataPath>' "$CONFIG_FILE" | head -1 | sed 's/<[^>]*>//g')

echo "================================================"
echo "ExcelJava gRPC Server"
echo "================================================"
echo "Port: $PORT"
echo "Configuration: $CONFIG_FILE"
echo "Data path: $DATA_PATH"

# Check if data path exists
if [ ! -d "$DATA_PATH" ]; then
    echo ""
    echo "WARNING: Data path '$DATA_PATH' does not exist!"
    echo "The server will start but data queries may not work properly."
    echo ""
fi

echo "================================================"
echo "Press Ctrl+C to stop the server"
echo ""

# Change to target directory to ensure config file is found
cd target

# Run the server with Java 21 compatibility flags
java --add-opens java.base/java.nio=ALL-UNNAMED \
     --add-opens java.base/jdk.internal.misc=ALL-UNNAMED \
     --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
     --add-exports java.base/jdk.internal.misc=ALL-UNNAMED \
     -Dio.netty.tryReflectionSetAccessible=true \
     -Djava.security.manager=allow \
     -Dio.grpc.netty.shaded.io.netty.tryReflectionSetAccessible=true \
     -jar exceljava-1.0-jar-with-dependencies.jar $PORT