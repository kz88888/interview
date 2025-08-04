package com.qsystem.qsexcel;

import com.qsystem.qsexcel.excelutil.ExcelHelper;

/**
 * Excel交易分布线图导出器
 * 对应C# QSystem.Excel.ExcelExporterTradeDistributionsLineChart
 */
public class ExcelExporterTradeDistributionsLineChart {
    private static ExcelHelper _excel = null;

    /**
     * 构建交易分布线图
     * 对应C# BuildOrderDistributionsLineChart方法
     */
    public static void BuildOrderDistributionsLineChart(String fileName, double meanValue, int rowCount) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        String dataSheetName = SimulatorSystemStrings.CompleteTradeReturnDistributions;
        String chartSheetName = SimulatorSystemStrings.CompleteTradeReturnDistributions;

        _excel.CreateHistogramChart(dataSheetName, chartSheetName, 500, 501, 300, 10, 1, meanValue, rowCount, "Trade Return Distribution", "Return", "Frequency");
        // Complete() will be called by ExcelExportContext
    }

    /**
     * 构建多头交易分布线图
     * 对应C# BuildLongOrderDistributionsLineChart方法
     */
    public static void BuildLongOrderDistributionsLineChart(String fileName, double meanValue, int rowCount) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        String dataSheetName = SimulatorSystemStrings.LongCompleteTradeReturnDistributions;
        String chartSheetName = SimulatorSystemStrings.LongCompleteTradeReturnDistributions;

        _excel.CreateHistogramChart(dataSheetName, chartSheetName, 500, 501, 300, 10, 1, meanValue, rowCount, "Long Trade Return Distribution", "Return", "Frequency");
        // Complete() will be called by ExcelExportContext
    }

    /**
     * 构建空头交易分布线图
     * 对应C# BuildShortOrderDistributionsLineChart方法
     */
    public static void BuildShortOrderDistributionsLineChart(String fileName, double meanValue, int rowCount) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        String dataSheetName = SimulatorSystemStrings.ShortCompleteTradeReturnDistributions;
        String chartSheetName = SimulatorSystemStrings.ShortCompleteTradeReturnDistributions;

        _excel.CreateHistogramChart(dataSheetName, chartSheetName, 500, 501, 300, 10, 1, meanValue, rowCount, "Short Trade Return Distribution", "Return", "Frequency");
        // Complete() will be called by ExcelExportContext
    }
} 