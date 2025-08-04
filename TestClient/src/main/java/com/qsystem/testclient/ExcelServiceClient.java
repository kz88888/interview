package com.qsystem.testclient;

import com.qsystem.clientlib.excel.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 * Excel服务客户端 - 用于与Excel gRPC服务通信
 */
public class ExcelServiceClient {
    private static final Logger logger = Logger.getLogger(ExcelServiceClient.class);
    private final ManagedChannel channel;
    private final ExcelGeneratorServiceGrpc.ExcelGeneratorServiceBlockingStub blockingStub;
    
    public ExcelServiceClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = ExcelGeneratorServiceGrpc.newBlockingStub(channel);
        logger.info("Connected to Excel service at " + host + ":" + port);
    }
    
    /**
     * 查询数据
     * @param queryText 查询文本
     * @return 查询结果列表
     */
    public List<String> query(String queryText) throws Exception {
        List<String> results = new ArrayList<>();
        
        try {
            QueryRequest request = QueryRequest.newBuilder()
                    .setWords(queryText)
                    .build();
            
            blockingStub.query(request).forEachRemaining(response -> {
                results.add(response.getUnderlyingModelJson());
            });
            
        } catch (StatusRuntimeException e) {
            logger.error("Query failed: " + e.getStatus().getDescription(), e);
            throw new Exception("Excel服务查询失败: " + e.getStatus().getDescription(), e);
        }
        
        return results;
    }
    
    /**
     * 查询详细数据
     * @param tagName 标签名称
     * @return 数据详情列表
     */
    public List<String[]> queryData(String tagName) throws Exception {
        List<String[]> results = new ArrayList<>();
        
        try {
            QueryDataRequest request = QueryDataRequest.newBuilder()
                    .setTag(tagName)
                    .build();
            
            QueryDataResponse response = blockingStub.queryData(request);
            
            // 解析响应数据
            for (QueryDataResponse.QueryDataRow row : response.getQueryDataTableList()) {
                // 每行有timestamp和data两个字段
                String[] rowData = new String[2];
                rowData[0] = String.valueOf(row.getTimestamp());
                rowData[1] = row.getData();
                results.add(rowData);
            }
            
        } catch (StatusRuntimeException e) {
            logger.error("QueryData failed: " + e.getStatus().getDescription(), e);
            throw new Exception("Excel服务数据查询失败: " + e.getStatus().getDescription(), e);
        }
        
        return results;
    }
    
    /**
     * 生成Excel文件
     * @param jsonContent JSON内容
     * @param strategyName 策略名称
     * @return Excel文件路径
     */
    public String generateExcel(String jsonContent, String strategyName) throws Exception {
        try {
            ExcelGenerationRequest request = ExcelGenerationRequest.newBuilder()
                    .setJsonContent(jsonContent)
                    .setStrategyName(strategyName)
                    .build();
            
            ExcelGenerationResponse response = blockingStub.generateExcel(request);
            
            if (response.getSuccess()) {
                logger.info("Excel generated successfully: " + response.getExcelFilePath());
                return response.getExcelFilePath();
            } else {
                throw new Exception("Excel生成失败: " + response.getErrorMessage());
            }
            
        } catch (StatusRuntimeException e) {
            logger.error("GenerateExcel failed: " + e.getStatus().getDescription(), e);
            throw new Exception("Excel服务调用失败: " + e.getStatus().getDescription(), e);
        }
    }
    
    /**
     * 关闭客户端连接
     */
    public void shutdown() {
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            logger.info("Excel service client shutdown completed");
        } catch (InterruptedException e) {
            logger.error("Shutdown interrupted", e);
        }
    }
}