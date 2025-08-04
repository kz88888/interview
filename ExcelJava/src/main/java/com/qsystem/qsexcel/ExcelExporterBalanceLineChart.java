package com.qsystem.qsexcel;

import com.qsystem.qsexcel.excelutil.ExcelHelper;

/**
 * Excel余额线图导出器
 * 对应C# QSystem.Excel.ExcelExporterBalanceLineChart
 */
public class ExcelExporterBalanceLineChart {
    private static ExcelHelper _excel = null;

    /**
     * 构建余额线图
     * 对应C# BuildBalanceLineChart方法
     */
    public static void BuildBalanceLineChart(double strategyReturn, double initCapital, String fileName) {
        //return;
        _excel = ExcelExportContext.getExcelHelper(fileName);
        String dataSheetName = SimulatorSystemStrings.BalanceSheetName;
        String chartSheetName = SimulatorSystemStrings.SummarySheetName;
        boolean isLogScale = false;
        if (strategyReturn > 10) {
            isLogScale = true;
        }
        //_excel.CreateLineChart(isLogScale, initCapital, dataSheetName, chartSheetName, 1, 5, 400, 10, 4, "Balance & Benchmark & Sharp Ratio & Benchmark Sharp", "Date", "Balance", "Sharp Ratio");

        //2020.01.14修改删除sharp ratio和benchmark sharp的显示。
        //_excel.CreateLineChart(isLogScale, initCapital, dataSheetName, chartSheetName, 1, 5, 400, 10, 4, "Balance & Benchmark ", "Date", "Balance");
        _excel.CreateLineChart(isLogScale, initCapital, dataSheetName, chartSheetName, 1, 3, 400, 10, 2, "Balance & Benchmark ", "Date", "Balance", "Sharp Ratio", 800, 600);

        // Complete() will be called by ExcelExportContext
    }
} 