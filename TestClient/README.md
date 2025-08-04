# TestClient Standalone

A standalone Java Swing application for querying data and generating Excel analysis files via gRPC.

## Features

1. **Data Query** - Query data from ExcelJava server and display in a tree view
2. **Excel Generation** - Read local JSON files and generate Excel reports through the ExcelJava server

## Requirements

- Java 8 or higher
- Maven 3.6 or higher
- ExcelJava server running on localhost:50051

## Building

```bash
mvn clean package
```

## Running

### Option 1: Using the run script
```bash
cd /Users/admin/code/qs/TestClient
./run.sh
```

If you encounter a "bad interpreter" error, fix it with:
```bash
chmod +x run.sh
```

### Option 2: Build and run manually
```bash
cd /Users/admin/code/qs/TestClient
mvn clean package
java -jar target/TestClient-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Option 3: Using Maven exec plugin
```bash
cd /Users/admin/code/qs/TestClient
mvn clean compile exec:java -Dexec.mainClass="com.qsystem.testclient.TestClientApp"
```

### Option 4: Run with dependencies in classpath
```bash
cd /Users/admin/code/qs/TestClient
mvn clean package
java -cp "target/TestClient-1.0-SNAPSHOT.jar:target/lib/*" com.qsystem.testclient.TestClientApp
```

## Configuration

The application connects to the Excel service at `localhost:50051` by default. To change this, modify the connection settings in the source code.

## Project Structure

```
TestClient/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/qsystem/
│   │   │       ├── testclient/      # Main application code
│   │   │       └── clientlib/excel/ # gRPC generated classes
│   │   ├── proto/
│   │   │   └── excel_service.proto  # gRPC service definition
│   │   └── resources/
│   │       └── log4j.properties     # Logging configuration
├── pom.xml
├── run.sh
└── README.md
```

## Usage

1. Launch the application
2. Choose between two options:
   - **查询数据 (Query Data)**: Browse and query data from the server
   - **生成Excel分析文件 (Generate Excel Analysis)**: Select a JSON file to generate Excel reports

## Logging

Logs are written to the `logs/` directory.