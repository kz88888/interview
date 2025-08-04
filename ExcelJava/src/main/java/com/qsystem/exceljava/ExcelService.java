package com.qsystem.exceljava;

import com.qsystem.clientlib.QPortfolio;
import com.qsystem.qsexcel.QPortfolioBaseExporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * Excel生成服务
 * 提供JSON到Excel的转换功能
 */
public class ExcelService {
    private static final Logger logger = LogManager.getLogger(ExcelService.class);
    
    /**
     * 将JSON文件转换为Excel文件
     * @param jsonFilePath JSON文件路径
     * @param outputDirectory 输出目录
     * @return 生成的Excel文件路径
     */
    public String convertJsonToExcel(String jsonFilePath, String outputDirectory) throws Exception {
        return convertJsonToExcel(jsonFilePath, outputDirectory, null);
    }
    
    /**
     * 将JSON文件转换为Excel文件（带进度回调）
     * @param jsonFilePath JSON文件路径
     * @param outputDirectory 输出目录
     * @param progressCallback 进度回调函数
     * @return 生成的Excel文件路径
     */
    public String convertJsonToExcel(String jsonFilePath, String outputDirectory, Consumer<String> progressCallback) throws Exception {
        logger.info("Starting JSON to Excel conversion");
        logger.info("JSON file: " + jsonFilePath);
        logger.info("Output directory: " + outputDirectory);
        
        // 验证输入文件
        File jsonFile = new File(jsonFilePath);
        if (!jsonFile.exists()) {
            throw new IllegalArgumentException("JSON file does not exist: " + jsonFilePath);
        }
        
        // 确保输出目录存在
        Path outputPath = Paths.get(outputDirectory);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
            logger.info("Created output directory: " + outputDirectory);
        }
        
        // 获取策略名称（从文件名推断）
        String fileName = jsonFile.getName();
        String strategyName = fileName.endsWith(".json") 
            ? fileName.substring(0, fileName.length() - 5) 
            : fileName;
        
        // 如果是zip文件，需要先解压
        if (jsonFilePath.endsWith(".zip")) {
            if (progressCallback != null) {
                progressCallback.accept("Unzipping file...");
            }
            com.qsystem.clientlib.ZipUtil.unZip(outputDirectory);
            strategyName = fileName.substring(0, fileName.length() - 4);
        }
        
        // 构建QPortfolio对象
        QPortfolio qPortfolio = new QPortfolio();
        
        try {
            if (progressCallback != null) {
                progressCallback.accept("Loading portfolio data...");
            }
            
            // 确保outputDirectory以分隔符结尾
            String normalizedOutputDir = outputDirectory;
            if (!normalizedOutputDir.endsWith(File.separator)) {
                normalizedOutputDir += File.separator;
            }
            
            // 恢复Portfolio数据
            QPortfolio.RestorePortfolio(normalizedOutputDir, strategyName, qPortfolio);
            
            // 生成Excel文件名
            String excelFileName = outputDirectory + File.separator + strategyName + ".xlsx";
            File excelFile = new File(excelFileName);
            
            // 如果文件已存在，删除旧文件
            if (excelFile.exists()) {
                excelFile.delete();
                logger.info("Deleted existing Excel file");
            }
            
            if (progressCallback != null) {
                progressCallback.accept("Generating Excel file...");
            }
            
            // 导出到Excel
            QPortfolioBaseExporter.ExportDebugInfoToExcel(excelFileName, qPortfolio, progressCallback);
            QPortfolioBaseExporter.ExportToExcel(excelFileName, qPortfolio, progressCallback);
            
            if (progressCallback != null) {
                progressCallback.accept("Excel generation completed");
            }
            
            logger.info("Excel file generated successfully: " + excelFileName);
            return excelFileName;
            
        } catch (Exception e) {
            logger.error("Failed to generate Excel file", e);
            throw new Exception("Failed to generate Excel file: " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量转换多个JSON文件
     * @param jsonFiles JSON文件路径数组
     * @param outputDirectory 输出目录
     * @return 生成的Excel文件路径数组
     */
    public String[] convertMultipleJsonToExcel(String[] jsonFiles, String outputDirectory) throws Exception {
        String[] results = new String[jsonFiles.length];
        
        for (int i = 0; i < jsonFiles.length; i++) {
            try {
                results[i] = convertJsonToExcel(jsonFiles[i], outputDirectory);
            } catch (Exception e) {
                logger.error("Failed to convert file: " + jsonFiles[i], e);
                results[i] = null;
            }
        }
        
        return results;
    }
}