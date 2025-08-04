package com.qsystem.qsexcel;

import com.qsystem.qsexcel.excelutil.ExcelHelper;

/**
 * Excel收益线图导出器
 * 对应C# QSystem.Excel.ExcelExporterReturnLineChart
 */
public class ExcelExporterReturnLineChart {
    private static ExcelHelper _excel = null;

    /**
     * 构建日收益线图
     * 对应C# BuildDailyReturnLineChart方法
     */
    public static void BuildDailyReturnLineChart(String fileName, double meanValue, int rowCount) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        String dataSheetName = SimulatorSystemStrings.QPortfolioBaseReturnDistributions;
        String chartSheetName = SimulatorSystemStrings.QPortfolioBaseReturnDistributions;

        _excel.CreateHistogramChart(dataSheetName, chartSheetName, 500, 501, 300, 10, 1, meanValue, rowCount, "Daily Return Distribution", "Return", "Frequency");
        // Complete() will be called by ExcelExportContext
    }

    /**
     * 构建交易组收益线图
     * 对应C# BuildTradeGroupReturnLineChart方法
     */
    public static void BuildTradeGroupReturnLineChart(String fileName, double meanValue, int rowCount) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        String dataSheetName = SimulatorSystemStrings.TradeGroupReturnDistributions;
        String chartSheetName = SimulatorSystemStrings.TradeGroupReturnDistributions;

        _excel.CreateHistogramChart(dataSheetName, chartSheetName, 500, 501, 300, 10, 1, meanValue, rowCount, "Trade Group Return Distribution", "Return", "Frequency");
        // Complete() will be called by ExcelExportContext
    }
} 