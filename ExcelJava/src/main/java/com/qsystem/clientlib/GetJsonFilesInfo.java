package com.qsystem.clientlib;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.time.format.DateTimeFormatter;

/**
 * 获取JSON文件信息类
 */
public class GetJsonFilesInfo {
    
    public static DataTable GetParamsInfo(String folderPathName, Consumer<String> func) {
        DataTable paramsDataTable = new DataTable();
        // 构建所有.json文件的列表
        func.accept("正在导入参数排列组合......0");
        
        // 获取.json文件信息列表
        File folder = new File(folderPathName);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.endsWith(".json"));
        
        List<StrategyParams> strategyParamsList = new ArrayList<>();
        List<String> fileNamesList = new ArrayList<>();
        
        if (jsonFiles != null) {
            for (File fileInfo : jsonFiles) {
                // 获取除.benchmark外的json文件名
                if (!fileInfo.getName().contains("benchmark")) {
                    String fileNameWithoutExt = fileInfo.getName().replace(".json", "");
                    // 获取文件名列表
                    fileNamesList.add(fileNameWithoutExt);
                    // 从.json文件名获取参数列表
                    StrategyParams strategyParams = new StrategyParams();
                    strategyParams.name = fileNameWithoutExt;
                    strategyParams.paramsPair = StrategyParams.restoreParams(fileNameWithoutExt);
                    strategyParamsList.add(strategyParams);
                }
            }
        }
        
        func.accept("正在导入参数排列组合......1");
        if (strategyParamsList.isEmpty()) {
            return paramsDataTable;
        }
        
        List<String> paramsNameList = new ArrayList<>(strategyParamsList.get(0).paramsPair.keySet());
        List<String> columnsOfParamsDT = new ArrayList<>();
        columnsOfParamsDT.addAll(paramsNameList);
        columnsOfParamsDT.add(0, "name");
        
        // 创建参数数据表
        for (String columnName : columnsOfParamsDT) {
            paramsDataTable.addColumn(columnName);
        }
        
        func.accept("正在导入参数排列组合......2");
        
        // 从strategyParamsList设置数据表
        for (int i = 0; i < fileNamesList.size(); i++) {
            DataTable.DataRow dataRow = paramsDataTable.newRow();
            dataRow.setItem(0, strategyParamsList.get(i).name);
            
            // 根据参数名添加参数值
            for (String paramsName : paramsNameList) {
                String value = strategyParamsList.get(i).paramsPair.get(paramsName);
                dataRow.setItem(paramsName, value);
            }
            
            // 添加行
            paramsDataTable.addRow(dataRow);
        }
        
        func.accept("正在导入参数排列组合......3");
        return paramsDataTable;
    }
    
    public static DataTable GetSummaryInfo(String folderPathName, Consumer<String> func) {
        File folder = new File(folderPathName);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.endsWith(".json"));
        
        // 从文件信息获取报告名称列表
        List<String> fileNamesList = new ArrayList<>();
        
        if (jsonFiles != null) {
            for (File fileInfo : jsonFiles) {
                // 获取除benchmark.json外的json文件名
                if (!fileInfo.getName().contains("benchmark")) {
                    String fileNameWithoutExt = fileInfo.getName().replace(".json", "");
                    // 获取json报告文件名列表
                    fileNamesList.add(fileNameWithoutExt);
                }
            }
        }
        
        SortedMap<String, String> alphaList = new TreeMap<>();
        SortedMap<String, String> annualizedReturnList = new TreeMap<>();
        SortedMap<String, String> accountMaxDrawnDownTimeList = new TreeMap<>();
        SortedMap<String, String> accountMaxDrawnDownList = new TreeMap<>();
        SortedMap<String, String> averageExposureList = new TreeMap<>();
        SortedMap<String, String> annualizeAlphaList = new TreeMap<>();
        SortedMap<String, String> betaList = new TreeMap<>();
        SortedMap<String, String> sharpeRatioList = new TreeMap<>();
        SortedMap<String, String> strategyReturnList = new TreeMap<>();
        
        int i = 1;
        for (String reportName : fileNamesList) {
            func.accept("正在合并数据......" + i + "of" + fileNamesList.size());
            i++;
            
            String folderPath = folderPathName;
            if (!folderPath.endsWith(File.separator)) {
                folderPath += File.separator;
            }
            
            try {
                QPortfolio qPortfolio = new QPortfolio();
                QPortfolio.RestorePortfolio(folderPath, reportName, qPortfolio); // 从.json文件恢复对象
                
                QPortfolioSummary summary = (QPortfolioSummary) qPortfolio.Summary;
                alphaList.put(reportName, String.format("%.2f%%", summary.Alpha * 100));
                annualizedReturnList.put(reportName, String.format("%.2f%%", summary.AnnualizedReturn * 100));
                
                ReturnWithTimeSpan ele = summary.AccountMaxDrawnDown;
                accountMaxDrawnDownList.put(reportName, String.format("%.2f%%", ele.value * 100));
                accountMaxDrawnDownTimeList.put(reportName, 
                    ele.beginTime.toLocalDate().format(DateTimeFormatter.ofPattern("M/d/yyyy")) + " to " + 
                    ele.endTime.toLocalDate().format(DateTimeFormatter.ofPattern("M/d/yyyy")));
                
                averageExposureList.put(reportName, String.valueOf(summary.AverageExposure));
                annualizeAlphaList.put(reportName, String.format("%.2f%%", summary.AnnualizeAlpha * 100));
                betaList.put(reportName, String.valueOf(summary.Beta));
                sharpeRatioList.put(reportName, String.format("%.2f%%", summary.SharpRatio * 100));
                strategyReturnList.put(reportName, String.format("%.2f%%", summary.StrategyReturn * 100));
                
                System.gc(); // 垃圾回收
            } catch (Exception e) {
                // 记录异常信息
                System.err.println("Error processing report: " + reportName + ", Error: " + e.getMessage());
                
                alphaList.put(reportName, "N/A");
                annualizedReturnList.put(reportName, "N/A");
                accountMaxDrawnDownList.put(reportName, "N/A");
                accountMaxDrawnDownTimeList.put(reportName, "N/A");
                averageExposureList.put(reportName, "N/A");
                annualizeAlphaList.put(reportName, "N/A");
                betaList.put(reportName, "N/A");
                sharpeRatioList.put(reportName, "N/A");
                strategyReturnList.put(reportName, "N/A");
            }
        }
        
        DataTable summaryInfoDT = new DataTable();
        // 表头
        summaryInfoDT.addColumn("name");
        summaryInfoDT.addColumn("sharpRatio");
        summaryInfoDT.addColumn("strategyReturn");
        summaryInfoDT.addColumn("annualizedReturn");
        summaryInfoDT.addColumn("alpha");
        summaryInfoDT.addColumn("annualizeAlpha");
        summaryInfoDT.addColumn("accountMaxDrawnDown");
        summaryInfoDT.addColumn("MaxDrawnDownTime");
        summaryInfoDT.addColumn("averageExposure");
        summaryInfoDT.addColumn("beta");
        
        // 从NameList和alphalist设置数据表
        for (String reportName : fileNamesList) {
            DataTable.DataRow dataRow = summaryInfoDT.newRow();
            dataRow.setItem(0, reportName);
            dataRow.setItem(1, sharpeRatioList.get(reportName));
            dataRow.setItem(2, strategyReturnList.get(reportName));
            dataRow.setItem(3, annualizedReturnList.get(reportName));
            dataRow.setItem(4, alphaList.get(reportName));
            dataRow.setItem(5, annualizeAlphaList.get(reportName));
            dataRow.setItem(6, accountMaxDrawnDownList.get(reportName));
            dataRow.setItem(7, accountMaxDrawnDownTimeList.get(reportName));
            dataRow.setItem(8, averageExposureList.get(reportName));
            dataRow.setItem(9, betaList.get(reportName));
            
            summaryInfoDT.addRow(dataRow);
        }
        
        return summaryInfoDT;
    }
} 