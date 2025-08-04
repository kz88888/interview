package com.qsystem.clientlib;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * 摘要导出器类
 */
public class SummaryExporter {
    public volatile int isCompleted;
    public int marketId;
    public CountDownLatch waitHandle;
    public boolean completedSynchronously;
    public Object asyncState;
    
    public SummaryExporter() {
        this.waitHandle = new CountDownLatch(1);
    }
    
    public void Execute(SFTP sftp, String strategyname, String localPath, String remotepath, 
                       Runnable callback, Consumer<String> func) {
        CompletableFuture.runAsync(() -> 
            ExportSummary(sftp, strategyname, localPath, remotepath, callback, func));
    }
    
    public void ExportSummary(SFTP sftp, String strategyname, String localPath, String remotepath, 
                             Runnable callback, Consumer<String> func) {
        try {
            List<String> files = sftp.getFileNameList(remotepath);
            int i = 1;
            int k = files.size();
            
            Properties props = new Properties();
            try {
                props.load(getClass().getClassLoader().getResourceAsStream("app.properties"));
            } catch (Exception e) {
                // 使用默认值
            }
            
            String ignoreSummaryCSV = props.getProperty("ignoreSummaryCSV", "false");
            
            if ("true".equals(ignoreSummaryCSV)) {
                for (String file : files) {
                    if ((!file.equals(".")) && (!file.equals(".."))) {
                        String sourceFilePath = remotepath + File.separator + file;
                        String destFilePath = new File(localPath, file).getPath();
                        File destFile = new File(destFilePath);
                        if (!destFile.exists()) {
                            sftp.Download(sourceFilePath, destFilePath);
                        }
                        i++;
                    }
                    
                    String words = "正在下载" + i + " of " + k + " 个文件......";
                    func.accept(words);
                }
                ZipUtil.unZip(localPath);
                ExportSummaryIgnoreCSV(strategyname, localPath, func);
            } else {
                for (String file : files) {
                    if ((!file.equals(".")) && (!file.equals("..")) && 
                        (!file.contains("log")) && (!file.contains(".json"))) {
                        String sourceFilePath = remotepath + File.separator + file;
                        String destFilePath = new File(localPath, file).getPath();
                        File destFile = new File(destFilePath);
                        if (!destFile.exists()) {
                            sftp.Download(sourceFilePath, destFilePath);
                            ZipUtil.unZipSingleFile(localPath);
                        }
                        i++;
                    }
                    String words = "正在下载" + i + " of " + k + " 个文件......";
                    func.accept(words);
                }
                
                // 实现数据表操作
                DataTable paramsDataTable = GetJsonFilesInfo.GetParamsInfo(localPath, func);
                func.accept("正在导入参数排列组合......");
                DataTable qPorfolioSummaryDT = GetJsonFilesInfo.GetSummaryInfo(localPath, func);
                func.accept("正在导入回测数据......");
                DataTable summaryCsvDataTable = OperateData.Csv2DataTable(localPath + "summary.csv");
                func.accept("正在合并数据......");
                DataTable tempSummaryDT = OperateData.MergeDataTables(paramsDataTable, summaryCsvDataTable, "name");
                DataTable newSummaryDT = OperateData.MergeDataTables(tempSummaryDT, qPorfolioSummaryDT, "name");
                String newFilePathName = localPath + strategyname + ".summary.csv";
                func.accept("写出......" + newFilePathName);
                OperateData.DataTable2CSV(newSummaryDT, newFilePathName);
            }
        } finally {
            isCompleted = 1;
            waitHandle.countDown();
            callback.run();
        }
    }
    
    public DataTable ExportSummaryIgnoreCSV(String strategyname, String localPath, Consumer<String> func) {
        DataTable paramsDataTable = GetJsonFilesInfo.GetParamsInfo(localPath, func);
        func.accept("正在合并数据......1");
        DataTable qPorfolioSummaryDT = GetJsonFilesInfo.GetSummaryInfo(localPath, func);
        func.accept("正在合并数据......2");
        DataTable newSummaryDT = OperateData.MergeDataTables(qPorfolioSummaryDT, paramsDataTable, "name");
        String newFilePathName = localPath + strategyname + ".summary.csv";
        func.accept("写出......" + newFilePathName);
        OperateData.DataTable2CSV(newSummaryDT, newFilePathName);
        return newSummaryDT;
    }
    
    // IAsyncResult equivalent methods
    public boolean isCompleted() {
        return (isCompleted == 1);
    }
    
    public CountDownLatch getAsyncWaitHandle() {
        return waitHandle;
    }
    
    public boolean isCompletedSynchronously() {
        return completedSynchronously;
    }
    
    public Object getAsyncState() {
        return asyncState;
    }
    
    public void setAsyncState(Object asyncState) {
        this.asyncState = asyncState;
    }
    
    public int getMarketId() {
        return marketId;
    }
    
    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }
} 