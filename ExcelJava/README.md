# ExcelJava服务

ExcelJava是一个基于gRPC的服务，提供Excel文件生成和数据查询功能。

## 功能特性

1. **Excel生成服务**: 将JSON格式的投资组合数据转换为Excel报告
2. **数据查询服务**: 
   - `query`: 查询可用的数据标签
   - `queryData`: 获取特定标签的时间序列数据

## 编译和运行

### 前置条件

- Java 21
- Maven 3.6+
- 数据文件（可选，用于数据查询功能）

### 编译

```bash
mvn clean compile
```

### 打包

```bash
mvn package
```

### 运行

```bash
# 使用默认端口 50051
java -jar target/exceljava-1.0-jar-with-dependencies.jar

# 指定端口
java -jar target/exceljava-1.0-jar-with-dependencies.jar 50052
```

## 配置

### 系统配置文件

服务启动时会读取 `system_config.xml` 文件来初始化数据层。主要配置项：

- `DataPath`: 数据文件路径
- `TagFrameworkConfig`: 数据加载配置
- `Markets`: 市场配置
- `CommissionConfig`: 佣金配置

### 本地测试配置

提供了 `system_config_local.xml` 作为本地测试配置示例。

### 服务配置

在 `src/main/resources/app.config` 中配置：

- `grpc_port`: gRPC服务端口（默认50051）
- `excel_output_directory`: Excel文件输出目录

## 数据准备

如果需要使用数据查询功能，需要准备TagFramework格式的数据文件：

1. 创建数据目录结构：
   ```
   testdata/
   └── SPY/
       └── _PVT.D.BAR/
           ├── metadata.zip
           └── data files...
   ```

2. 在 `system_config.xml` 中配置数据路径和加载规则

## API使用示例

### Excel生成

```python
import grpc
from excel_service_pb2 import ExcelGenerationRequest
from excel_service_pb2_grpc import ExcelGeneratorServiceStub

channel = grpc.insecure_channel('localhost:50051')
stub = ExcelGeneratorServiceStub(channel)

request = ExcelGenerationRequest(
    strategy_name="MyStrategy",
    json_content="{...}"  # QPortfolio JSON数据
)

response = stub.GenerateExcel(request)
print(f"Excel file: {response.excel_file_path}")
```

### 数据查询

```python
# 查询所有可用符号
request = QueryRequest(words="ALL")
for response in stub.Query(request):
    print(response.underlying_model_json)

# 查询特定符号
request = QueryRequest(words="SPY")
for response in stub.Query(request):
    print(response.underlying_model_json)

# 获取数据
request = QueryDataRequest(tag="SPY_20240101_PVT.D.BAR")
response = stub.QueryData(request)
for row in response.query_data_table:
    print(f"{row.timestamp}: {row.data}")
```

## 注意事项

1. 数据查询功能需要预先加载数据到内存，启动时间取决于数据量
2. Excel生成功能独立于数据加载，即使没有数据也可以使用
3. 生成的Excel文件保存在配置的输出目录中

## 故障排查

1. **启动失败**: 检查 `system_config.xml` 配置是否正确
2. **数据加载失败**: 检查数据路径和文件格式
3. **端口冲突**: 更改启动端口或检查端口占用
4. **内存不足**: 增加JVM内存参数 `-Xmx4g`