package com.qsystem.exceljava.grpc;

import com.qsystem.clientlib.QPortfolio;
import com.qsystem.qsexcel.QPortfolioBaseExporter;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.qsystem.clientlib.Settings;

// TagFramework imports
import TagFramework.Metadata.*;
import TagFramework.Model.Bar;
import TagFramework.Model.OptionCore;
import TagFramework.Model.OptionGreeks;
import TagFramework.TagLayer.*;
import TagOperationBackTest.TagOperators;
import com.qsystem.exceljava.model.TreeViewNodeItem;

/**
 * Excel生成gRPC服务实现
 */
public class ExcelGeneratorServiceImpl extends ExcelGeneratorServiceGrpc.ExcelGeneratorServiceImplBase {
    private static final Logger logger = LogManager.getLogger(ExcelGeneratorServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String outputBaseDirectory;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    
    public ExcelGeneratorServiceImpl() {
        // 从配置文件读取输出目录，如果没有配置则使用默认值
        Settings settings = Settings.getInstance();
        String configuredDir = settings.getProperty("excel_output_directory", "/Users/testuser/ExcelOutput/");
        
        // 如果配置的目录包含testuser，替换为当前用户的主目录
        if (configuredDir.contains("/Users/testuser/")) {
            String userHome = System.getProperty("user.home");
            configuredDir = configuredDir.replace("/Users/testuser", userHome);
            logger.info("Adjusted output directory to use current user home: " + configuredDir);
        }
        
        this.outputBaseDirectory = configuredDir;
        
        // 确保输出目录存在
        try {
            Path outputPath = Paths.get(outputBaseDirectory);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
                logger.info("Created Excel output directory: " + outputBaseDirectory);
            } else {
                logger.info("Using existing Excel output directory: " + outputBaseDirectory);
            }
        } catch (Exception e) {
            logger.error("Failed to create output directory: " + outputBaseDirectory + ", will use current directory", e);
            // 如果无法创建配置的目录，使用当前目录下的ExcelOutput
            this.outputBaseDirectory = "ExcelOutput";
            try {
                Files.createDirectories(Paths.get(this.outputBaseDirectory));
                logger.info("Using fallback output directory: " + this.outputBaseDirectory);
            } catch (Exception ex) {
                logger.error("Failed to create fallback directory", ex);
            }
        }
    }
    
    @Override
    public void generateExcel(ExcelGenerationRequest request, StreamObserver<ExcelGenerationResponse> responseObserver) {
        logger.info("Received Excel generation request for strategy: " + request.getStrategyName());
        
        try {
            // 验证请求参数
            if (request.getJsonContent() == null || request.getJsonContent().isEmpty()) {
                throw new IllegalArgumentException("JSON content cannot be empty");
            }
            
            if (request.getStrategyName() == null || request.getStrategyName().isEmpty()) {
                throw new IllegalArgumentException("Strategy name cannot be empty");
            }
            
            // 生成带时间戳的文件名
            String timestamp = dateFormat.format(new Date());
            String outputFileName = request.getStrategyName() + "_" + timestamp;
            
            // 创建输出目录（每个策略一个子目录）
            Path outputPath = Paths.get(outputBaseDirectory, outputFileName);
            Files.createDirectories(outputPath);
            String outputDirectory = outputPath.toString();
            logger.info("Created output directory: " + outputDirectory);
            
            // 创建临时JSON文件
            String tempFileName = request.getStrategyName() + ".json";
            File tempJsonFile = new File(outputDirectory, tempFileName);
            
            try {
                // 验证JSON格式
                JsonNode jsonNode = objectMapper.readTree(request.getJsonContent());
                
                // 将JSON内容写入临时文件
                try (FileWriter writer = new FileWriter(tempJsonFile)) {
                    writer.write(request.getJsonContent());
                }
                logger.info("Created temporary JSON file: " + tempJsonFile.getAbsolutePath());
                
                // 构建QPortfolio对象
                QPortfolio qPortfolio = new QPortfolio();
                
                // 确保outputDirectory以分隔符结尾
                String normalizedOutputDir = outputDirectory;
                if (!normalizedOutputDir.endsWith(File.separator)) {
                    normalizedOutputDir += File.separator;
                }
                
                // 恢复Portfolio数据
                QPortfolio.RestorePortfolio(normalizedOutputDir, request.getStrategyName(), qPortfolio);
                
                // 生成Excel文件名
                String excelFileName = outputDirectory + File.separator + request.getStrategyName() + ".xlsx";
                File excelFile = new File(excelFileName);
                
                // 如果文件已存在，删除旧文件
                if (excelFile.exists()) {
                    excelFile.delete();
                    logger.info("Deleted existing Excel file");
                }
                
                // 创建进度回调函数
                java.util.function.Consumer<String> progressCallback = (String message) -> {
                    logger.info("Excel generation progress: " + message);
                };
                
                // 导出到Excel
                QPortfolioBaseExporter.ExportDebugInfoToExcel(excelFileName, qPortfolio, progressCallback);
                QPortfolioBaseExporter.ExportToExcel(excelFileName, qPortfolio, progressCallback);
                
                // 验证Excel文件是否生成成功
                if (!excelFile.exists()) {
                    throw new RuntimeException("Excel file was not created successfully");
                }
                
                // 获取文件大小
                long fileSize = excelFile.length();
                
                logger.info("Successfully generated Excel file: " + excelFileName);
                
                // 构建响应
                ExcelGenerationResponse response = ExcelGenerationResponse.newBuilder()
                        .setSuccess(true)
                        .setExcelFilePath(excelFileName)
                        .setFileSize(fileSize)
                        .setGenerationTimestamp(System.currentTimeMillis())
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
                logger.info("JSON file saved at: " + tempJsonFile.getAbsolutePath());
                
            } finally {
                // 保留JSON文件，不删除
            }
            
        } catch (Exception e) {
            logger.error("Failed to generate Excel file", e);
            
            // 发送错误响应
            ExcelGenerationResponse errorResponse = ExcelGenerationResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage("Failed to generate Excel: " + e.getMessage())
                    .setGenerationTimestamp(System.currentTimeMillis())
                    .build();
            
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
    
    @Override
    public void query(QueryRequest request, StreamObserver<QueryResponse> responseObserver) {
        logger.info("Received query request with words: " + request.getWords());
        
        try {
            // Note: In a full implementation, user authentication would be performed here
            // For now, we proceed without authentication
            
            if (request.getWords().toUpperCase().equals("ALL")) {
                queryAllSymbols(responseObserver);
            } else {
                String symbol = request.getWords();
                queryPackagesForOneSymbol(symbol, responseObserver, true);
            }
            
        } catch (Exception e) {
            logger.error("Error in query method", e);
            responseObserver.onError(e);
        }
    }
    
    private void queryAllSymbols(StreamObserver<QueryResponse> responseObserver) {
        try {
            // Query all symbols from TagLayer
            for (String symbol : TagLayer.getInstance()._OptionCorePackages.keySet()) {
                queryPackagesForOneSymbol(symbol, responseObserver, false);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("Error querying all symbols", e);
            // If TagLayer is not initialized, return empty result
            QueryResponse response = QueryResponse.newBuilder()
                .setUnderlyingModelJson("{\"name\":\"All Symbols\",\"children\":[]}")
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
    
    private void queryPackagesForOneSymbol(String symbol, StreamObserver<QueryResponse> responseObserver, boolean isOnlyOne) {
        queryOCForOneSymbol(symbol, responseObserver);
        queryOGForOneSymbol(symbol, responseObserver);
        queryBarForOneSymbol(symbol, responseObserver);
        queryIntPackageForOneSymbol(symbol, responseObserver);
        
        if (isOnlyOne) {
            responseObserver.onCompleted();
        }
    }
    
    private void queryOCForOneSymbol(String symbol, StreamObserver<QueryResponse> responseObserver) {
        try {
            OptionCoreTagGroup optionCoreTagGroup = TagOperators.getInstance(symbol).getOptionCoreTagGroup();
            if (optionCoreTagGroup != null) {
                TreeMap<String, TreeMap<String, OptionDataDescriptor>> optionCoreTagMap = optionCoreTagGroup.getOptionTagMetaTree();
                if (!optionCoreTagMap.isEmpty()) {
                    TreeViewNodeItem treeViewNodeItem = new TreeViewNodeItem();
                    treeViewNodeItem.setName(symbol + "_" + optionCoreTagGroup.TagNameB + "(" + optionCoreTagGroup.TotalTagCount + ")");
                    
                    for (Map.Entry<String, TreeMap<String, OptionDataDescriptor>> optionTagMapEntry : optionCoreTagMap.entrySet()) {
                        TreeViewNodeItem expDateTreeViewNodeItem = new TreeViewNodeItem();
                        expDateTreeViewNodeItem.setName(optionTagMapEntry.getKey() + "(" + optionTagMapEntry.getValue().size() + ")");
                        
                        for (Map.Entry<String, OptionDataDescriptor> ocTagEntry : optionTagMapEntry.getValue().entrySet()) {
                            TreeViewNodeItem ocViewNodeItem = new TreeViewNodeItem();
                            ocViewNodeItem.setName(symbol + "_" + ocTagEntry.getValue().getTagNameA() + 
                                optionCoreTagGroup.TagNameB.substring(optionCoreTagGroup.TagNameB.indexOf("_")));
                            expDateTreeViewNodeItem.getChildren().add(ocViewNodeItem);
                        }
                        treeViewNodeItem.getChildren().add(expDateTreeViewNodeItem);
                    }
                    
                    QueryResponse.Builder queryResponseBuilder = QueryResponse.newBuilder();
                    queryResponseBuilder.setUnderlyingModelJson(objectMapper.writeValueAsString(treeViewNodeItem));
                    responseObserver.onNext(queryResponseBuilder.build());
                }
            }
        } catch (Exception e) {
            logger.debug("Error querying OC for symbol: " + symbol, e);
        }
    }
    
    private void queryOGForOneSymbol(String symbol, StreamObserver<QueryResponse> responseObserver) {
        try {
            if (TagLayer.getInstance()._OptionGreeksPackages.containsKey(symbol)) {
                TreeMap<String, OptionGreeksTagPackage> optionGreeksTagPackages = TagLayer.getInstance()._OptionGreeksPackages.get(symbol);
                
                for (String tagNameB : optionGreeksTagPackages.keySet()) {
                    OptionGreeksTagGroup optionGreeksTagGroup = (OptionGreeksTagGroup) optionGreeksTagPackages.get(tagNameB)._tagGroup;
                    
                    if (optionGreeksTagGroup != null) {
                        TreeMap<String, TreeMap<String, OptionDataDescriptor>> optionGreeksTagMap = optionGreeksTagGroup.getOptionTagMetaTree();
                        if (!optionGreeksTagMap.isEmpty()) {
                            TreeViewNodeItem treeViewNodeItem = new TreeViewNodeItem();
                            treeViewNodeItem.setName(symbol + "_" + tagNameB + "(" + optionGreeksTagGroup.TotalTagCount + ")");
                            
                            for (Map.Entry<String, TreeMap<String, OptionDataDescriptor>> optionGreeksTagMapEntry : optionGreeksTagMap.entrySet()) {
                                TreeViewNodeItem expDateTreeViewNodeItem = new TreeViewNodeItem();
                                expDateTreeViewNodeItem.setName(optionGreeksTagMapEntry.getKey() + "(" + optionGreeksTagMapEntry.getValue().size() + ")");
                                
                                for (Map.Entry<String, OptionDataDescriptor> ogTagEntry : optionGreeksTagMapEntry.getValue().entrySet()) {
                                    TreeViewNodeItem optionGreeksViewNodeItem = new TreeViewNodeItem();
                                    optionGreeksViewNodeItem.setName(symbol + "_" + ogTagEntry.getValue().getTagNameA() + "." + 
                                        optionGreeksTagGroup.idToken + optionGreeksTagGroup.TagNameB.substring(optionGreeksTagGroup.TagNameB.indexOf("_")));
                                    expDateTreeViewNodeItem.getChildren().add(optionGreeksViewNodeItem);
                                }
                                treeViewNodeItem.getChildren().add(expDateTreeViewNodeItem);
                            }
                            
                            QueryResponse.Builder queryResponseBuilder = QueryResponse.newBuilder();
                            queryResponseBuilder.setUnderlyingModelJson(objectMapper.writeValueAsString(treeViewNodeItem));
                            responseObserver.onNext(queryResponseBuilder.build());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error querying OG for symbol: " + symbol, e);
        }
    }
    
    private void queryBarForOneSymbol(String symbol, StreamObserver<QueryResponse> responseObserver) {
        try {
            BarTagGroup underlyingTagGroup = TagOperators.getInstance(symbol).getUnderlyingTagGroup();
            if (underlyingTagGroup != null) {
                TreeMap<String, TagDescriptor> underlyingTagMap = underlyingTagGroup.tagMetaTree;
                if (!underlyingTagMap.isEmpty()) {
                    TreeViewNodeItem treeViewNodeItem = new TreeViewNodeItem();
                    treeViewNodeItem.setName("Underlying_" + symbol + "(" + underlyingTagGroup.TotalTagCount + ")");
                    
                    for (Map.Entry<String, TagDescriptor> underlyingTagMapEntry : underlyingTagMap.entrySet()) {
                        TreeViewNodeItem symbolTreeViewNodeItem = new TreeViewNodeItem();
                        symbolTreeViewNodeItem.setName(underlyingTagMapEntry.getKey() + "_" + underlyingTagGroup.TagNameB);
                        treeViewNodeItem.getChildren().add(symbolTreeViewNodeItem);
                    }
                    
                    QueryResponse.Builder queryResponseBuilder = QueryResponse.newBuilder();
                    queryResponseBuilder.setUnderlyingModelJson(objectMapper.writeValueAsString(treeViewNodeItem));
                    responseObserver.onNext(queryResponseBuilder.build());
                }
            }
        } catch (Exception e) {
            logger.debug("Error querying Bar for symbol: " + symbol, e);
        }
    }
    
    private void queryIntPackageForOneSymbol(String symbol, StreamObserver<QueryResponse> responseObserver) {
        try {
            if (TagLayer.getInstance()._IntPackages.containsKey(symbol)) {
                TreeMap<String, IntTagPackage> intTagPackages = TagLayer.getInstance()._IntPackages.get(symbol);
                
                for (String tagNameB : intTagPackages.keySet()) {
                    IntTagGroup intTagGroup = (IntTagGroup) intTagPackages.get(tagNameB)._tagGroup;
                    if (intTagGroup != null) {
                        TreeMap<String, TagDescriptor> intTagMap = intTagGroup.tagMetaTree;
                        if (!intTagMap.isEmpty()) {
                            TreeViewNodeItem treeViewNodeItem = new TreeViewNodeItem();
                            treeViewNodeItem.setName(symbol + "_" + tagNameB + "(" + intTagGroup.TotalTagCount + ")");
                            
                            for (Map.Entry<String, TagDescriptor> intTagMapEntry : intTagMap.entrySet()) {
                                TreeViewNodeItem symbolTreeViewNodeItem = new TreeViewNodeItem();
                                symbolTreeViewNodeItem.setName(intTagMapEntry.getKey() + "_" + intTagGroup.TagNameB);
                                treeViewNodeItem.getChildren().add(symbolTreeViewNodeItem);
                            }
                            
                            QueryResponse.Builder queryResponseBuilder = QueryResponse.newBuilder();
                            queryResponseBuilder.setUnderlyingModelJson(objectMapper.writeValueAsString(treeViewNodeItem));
                            responseObserver.onNext(queryResponseBuilder.build());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error querying Int package for symbol: " + symbol, e);
        }
    }
    
    @Override
    public void queryData(QueryDataRequest request, StreamObserver<QueryDataResponse> responseObserver) {
        logger.info("Received queryData request for tag: " + request.getTag());
        
        try {
            // Note: In a full implementation, user authentication would be performed here
            QueryDataResponse.Builder queryResponseBuilder = QueryDataResponse.newBuilder();
            tag(request.getTag(), queryResponseBuilder);
            responseObserver.onNext(queryResponseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("Error in queryData method", e);
            responseObserver.onError(e);
        }
    }
    
    private void tag(String tagName, QueryDataResponse.Builder queryResponseBuilder) {
        String[] part = tagName.split("_");
        if (part.length != 3) {
            return;
        }
        
        String symbol = part[0];
        String[] typeTokenStrs = part[2].split("\\.");
        String type;
        
        if (typeTokenStrs.length == 3) {
            type = typeTokenStrs[2];
            switch (type.toUpperCase()) {
                case "OC":
                    getTagFromTL_data(tagName, queryResponseBuilder);
                    break;
                case "OG":
                    getTagFromTL_greeks(tagName, queryResponseBuilder);
                    break;
                case "BAR":
                    getTagFromTL_underlying(tagName, queryResponseBuilder);
                    break;
            }
        } else if (typeTokenStrs.length == 2) {
            getTagFromIntPackage(tagName, queryResponseBuilder);
        }
    }
    
    private void getTagFromIntPackage(String tagName, QueryDataResponse.Builder queryResponseBuilder) {
        try {
            SortedMap<Long, Double> doubleMap = TagQueryOperation.GetIntDataTag(tagName);
            if (doubleMap == null || doubleMap.isEmpty()) {
                return;
            }
            
            for (Map.Entry<Long, Double> entry : doubleMap.entrySet()) {
                QueryDataResponse.QueryDataRow.Builder queryDataTableBuilder = QueryDataResponse.QueryDataRow.newBuilder();
                double value = entry.getValue();
                long timeStamp = entry.getKey();
                int nDate = (int) (timeStamp / 1000000L);
                int nTime = (int) (timeStamp % 1000000L);
                
                queryDataTableBuilder.setTimestamp(nDate);
                if (!TagOperators.isNaN(value)) {
                    queryDataTableBuilder.setData(String.format("%.4f, %02d:%02d:%02d",
                        value, nTime / 10000, (nTime % 10000) / 100, nTime % 100));
                } else {
                    queryDataTableBuilder.setData(String.format("%d, %02d:%02d:%02d",
                        TagLayer.NaNInteger, nTime / 10000, (nTime % 10000) / 100, nTime % 100));
                }
                queryResponseBuilder.addQueryDataTable(queryDataTableBuilder);
            }
        } catch (Exception e) {
            logger.error("Error getting int package data for tag: " + tagName, e);
        }
    }
    
    private void getTagFromTL_data(String tagName, QueryDataResponse.Builder queryResponseBuilder) {
        try {
            SortedMap<Long, OptionCore> optionCoreMap = TagQueryOperation.GetTagFrom_data(tagName);
            if (optionCoreMap == null || optionCoreMap.isEmpty()) {
                return;
            }
            
            for (Map.Entry<Long, OptionCore> optionCoreEntry : optionCoreMap.entrySet()) {
                QueryDataResponse.QueryDataRow.Builder queryDataTableBuilder = QueryDataResponse.QueryDataRow.newBuilder();
                OptionCore optionCore = optionCoreEntry.getValue();
                long timeStamp = optionCoreEntry.getKey();
                int nDate = (int) (timeStamp / 1000000L);
                int nTime = (int) (timeStamp % 1000000L);
                
                if (optionCore != null) {
                    queryDataTableBuilder.setTimestamp(nDate);
                    queryDataTableBuilder.setData(String.format("%.4f, %.4f, %.4f, %d, %d, %02d:%02d:%02d",
                        optionCore.getAskDouble(),
                        optionCore.getBidDouble(),
                        optionCore.getLastDouble(),
                        optionCore.open_interest,
                        optionCore.volume,
                        nTime / 10000, (nTime % 10000) / 100, nTime % 100));
                    queryResponseBuilder.addQueryDataTable(queryDataTableBuilder);
                }
            }
        } catch (Exception e) {
            logger.error("Error getting option core data for tag: " + tagName, e);
        }
    }
    
    private void getTagFromTL_greeks(String tagName, QueryDataResponse.Builder queryResponseBuilder) {
        try {
            SortedMap<Long, OptionGreeks> optionGreeksMap = TagQueryOperation.GetTagFrom_greeks(tagName);
            if (optionGreeksMap == null || optionGreeksMap.isEmpty()) {
                return;
            }
            
            for (Map.Entry<Long, OptionGreeks> optionGreeksEntry : optionGreeksMap.entrySet()) {
                QueryDataResponse.QueryDataRow.Builder queryDataTableBuilder = QueryDataResponse.QueryDataRow.newBuilder();
                OptionGreeks optionGreeks = optionGreeksEntry.getValue();
                long timeStamp = optionGreeksEntry.getKey();
                int nDate = (int) (timeStamp / 1000000L);
                int nTime = (int) (timeStamp % 1000000L);
                
                if (optionGreeks != null) {
                    queryDataTableBuilder.setTimestamp(nDate);
                    queryDataTableBuilder.setData(String.format("%.8f, %.8f, %.8f, %.8f, %.8f, %02d:%02d:%02d",
                        optionGreeks.getDeltaDouble(),
                        optionGreeks.getGammaDouble(),
                        optionGreeks.getIVDouble(),
                        optionGreeks.getThetaDouble(),
                        optionGreeks.getVegaDouble(),
                        nTime / 10000, (nTime % 10000) / 100, nTime % 100));
                    queryResponseBuilder.addQueryDataTable(queryDataTableBuilder);
                }
            }
        } catch (Exception e) {
            logger.error("Error getting option greeks data for tag: " + tagName, e);
        }
    }
    
    private void getTagFromTL_underlying(String tagName, QueryDataResponse.Builder queryResponseBuilder) {
        String[] part = tagName.split("_");
        String symbol = part[0];
        SortedMap<Long, Bar> underlyingMap = TagQueryOperation.GetUnderlyingTag(tagName);
        if (underlyingMap == null || underlyingMap.isEmpty()) {
            return;
        }
        
        for (Map.Entry<Long, Bar> underlyingEntry : underlyingMap.entrySet()) {
            QueryDataResponse.QueryDataRow.Builder queryDataTableBuilder = QueryDataResponse.QueryDataRow.newBuilder();
            Bar bar = underlyingEntry.getValue();
            long timeStamp = underlyingEntry.getKey();
            int nDate = (int) (timeStamp / 1000000L);
            int nTime = (int) (timeStamp % 1000000L);
            
            if (bar != null) {
                queryDataTableBuilder.setTimestamp(nDate);
                queryDataTableBuilder.setData(String.format("%.4f, %.4f, %.4f, %.4f, %d, %02d:%02d:%02d",
                    bar.getOpenDouble(),
                    bar.getHighDouble(),
                    bar.getLowDouble(),
                    bar.getCloseDouble(),
                    bar.volume,
                    nTime / 10000, (nTime % 10000) / 100, nTime % 100));
                queryResponseBuilder.addQueryDataTable(queryDataTableBuilder);
            }
        }
    }
}