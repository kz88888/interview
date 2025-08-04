package com.qsystem.exceljava;

import com.qsystem.exceljava.grpc.ExcelGeneratorServiceGrpc;
import com.qsystem.exceljava.grpc.ExcelGeneratorServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

// 数据加载相关导入
import Config.SystemConfig;
import Module.Loader.TFLoader;
import TagFramework.TagLayer.TagLayer;
import TagOperationBackTest.TagOperators;

/**
 * Excel gRPC服务器
 * 提供基于gRPC协议的Excel生成和数据查询服务
 */
public class ExcelGrpcServer {
    private static final Logger logger = LogManager.getLogger(ExcelGrpcServer.class);
    
    private Server server;
    private final int port;
    
    public ExcelGrpcServer(int port) {
        this.port = port;
    }
    
    /**
     * 启动gRPC服务器
     */
    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
            .addService(new ExcelGeneratorServiceImpl())
            .maxInboundMessageSize(100 * 1024 * 1024)  // 100MB max message size
            .build()
            .start();
        
        logger.info("Excel gRPC Server started on port " + port);
        
        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server...");
            try {
                ExcelGrpcServer.this.stop();
            } catch (InterruptedException e) {
                logger.error("Error during shutdown", e);
            }
            logger.info("Server shut down");
        }));
    }
    
    /**
     * 停止服务器
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
    
    /**
     * 阻塞等待服务器关闭
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
    
    /**
     * 初始化数据层
     */
    private static void initializeDataLayer() {
        try {
            logger.info("Initializing data layer...");
            
            // 加载系统配置
            SystemConfig systemConfig = SystemConfig.getInstance();
            
            // 初始化TagLayer
            TagLayer.createInstance(systemConfig.getTagFrameworkConfig()._DataPath);
            logger.info("TagLayer initialized with data path: " + systemConfig.getTagFrameworkConfig()._DataPath);
            
            // 加载数据
            TFLoader.LoadData(systemConfig.getTagFrameworkConfig());
            logger.info("Data loading completed");
            
            // 创建TagOperators
            TagOperators.CreateTagOperators(systemConfig);
            logger.info("TagOperators created");
            
        } catch (Exception e) {
            logger.error("Failed to initialize data layer", e);
            throw new RuntimeException("Data layer initialization failed", e);
        }
    }
    
    /**
     * 主函数
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // 首先初始化数据层
        try {
            initializeDataLayer();
        } catch (Exception e) {
            logger.error("Failed to initialize data layer, exiting", e);
            System.exit(1);
        }
        
        // 从SystemConfig读取端口，优先级：命令行参数 > 环境变量 > SystemConfig配置 > 默认值
        SystemConfig systemConfig = SystemConfig.getInstance();
        int port = systemConfig.getPort(); // 从system_config.xml读取端口
        
        // 从命令行参数获取端口（最高优先级）
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.error("Invalid port number: " + args[0]);
                System.err.println("Usage: java -jar exceljava.jar [port]");
                System.exit(1);
            }
        }
        
        // 从环境变量读取端口（次高优先级）
        String envPort = System.getenv("GRPC_PORT");
        if (envPort != null && args.length == 0) {
            try {
                port = Integer.parseInt(envPort);
            } catch (NumberFormatException e) {
                logger.error("Invalid GRPC_PORT environment variable: " + envPort);
            }
        }
        
        // 启动服务器
        final ExcelGrpcServer server = new ExcelGrpcServer(port);
        server.start();
        server.blockUntilShutdown();
    }
}