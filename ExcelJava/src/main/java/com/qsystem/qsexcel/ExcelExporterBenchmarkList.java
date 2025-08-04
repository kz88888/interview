package com.qsystem.qsexcel;

import com.qsystem.clientlib.QPortfolioBase;
import com.qsystem.clientlib.QPortfolioSummary;
import com.qsystem.clientlib.Benchmark;
import com.qsystem.clientlib.ReturnWithTimeSpan;
import com.qsystem.qsexcel.excelutil.ExcelHelper;

import java.io.File;
import java.util.*;

/**
 * Excel基准列表导出器
 * 对应C# QSystem.Excel.ExcelExporterBenchmarkList
 */
public class ExcelExporterBenchmarkList {
    private static ExcelHelper _excel = null;
    private static final String BENCHMARK = "Benchmark";

    /**
     * 导出投资组合基准列表
     * 对应C# ExportPortBenchmarkList方法
     */
    public static void ExportPortBenchmarkList(QPortfolioBase port, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        int rowIndex = 1;
        _excel.CreateNewWorkSheet(BENCHMARK);
        int[] endIndex = new int[1];

        /*Export Default Benchmark*/
        ExportPortBenchmark(port.Benchmark, port.Summary, rowIndex, endIndex);
        rowIndex = endIndex[0];

        /*Export other benchmark list */
        if (port.BenchmarkList != null) {
            for (Benchmark benchmark : port.BenchmarkList) {
                ExportPortBenchmark(benchmark, port.Summary, rowIndex, endIndex);
                rowIndex = endIndex[0];
            }
        }

        // Complete() will be called by ExcelExportContext
    }

    /**
     * 导出投资组合基准
     * 对应C# ExportPortBenchmark方法
     */
    private static void ExportPortBenchmark(Benchmark benchmark, QPortfolioSummary summary, int startRowIndex, int[] endRowIndex) {
        final int colIndex = 1;
        int rowIndex = startRowIndex;

        _excel.SetRangBoldValue(rowIndex, colIndex, "Benchmark", 35);
        _excel.SetRangBoldValue(rowIndex, colIndex + 1, benchmark._benchmarkSymbol, 10);

        rowIndex++;
        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "QPortfolioBase Best 3 Month Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, summary.Best3MonthReturn, true, benchmark.Best3MonthPortfolioReturn);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "QPortfolioBase Worst 3 Month Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, summary.Worst3MonthReturn, true, benchmark.Worst3MonthPortfolioReturn);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "QPortfolioBase Best 1 Year Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, summary.Best1YearReturn, true, benchmark.Best1YearPortfolioReturn);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "QPortfolioBase Worst 1 Year Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, summary.Worst1YearReturn, true, benchmark.Worst1YearPortfolioReturn);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "QPortfolioBase Best 3 Year Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, summary.Best3YearReturn, true, benchmark.Best3YearPortfolioReturn);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "QPortfolioBase Worst 3 Year Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, summary.Worst3YearReturn, true, benchmark.Worst3YearPortfolioReturn);

        rowIndex++;
        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Benchmark Performance during testing period");

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, benchmark._benchmarkSymbol + " Benchmark Best 3 Month Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, benchmark.Best3MonthReturnTestingPeriod, false, 0);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, benchmark._benchmarkSymbol + " Worst 3 Month Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, benchmark.Worst3MonthReturnTestingPeriod, false, 0);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, benchmark._benchmarkSymbol + " Best 1 Year Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, benchmark.Best1YearReturnTestingPeriod, false, 0);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, benchmark._benchmarkSymbol + " Worst 1 Year Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, benchmark.Worst1YearReturnTestingPeriod, false, 0);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, benchmark._benchmarkSymbol + " Best 3 Year Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, benchmark.Best3YearReturnTestingPeriod, false, 0);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, benchmark._benchmarkSymbol + " Worst 3 Year Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, benchmark.Worst3YearReturnTestingPeriod, false, 0);

        rowIndex++;
        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Benchmark Performance during entire benchmark history");

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, benchmark._benchmarkSymbol + " Benchmark Best 3 Month Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, benchmark.Best3MonthReturn, false, 0);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, benchmark._benchmarkSymbol + " Worst 3 Month Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, benchmark.Worst3MonthReturn, false, 0);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, benchmark._benchmarkSymbol + " Best 1 Year Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, benchmark.Best1YearReturn, false, 0);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, benchmark._benchmarkSymbol + " Worst 1 Year Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, benchmark.Worst1YearReturn, false, 0);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, benchmark._benchmarkSymbol + " Best 3 Year Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, benchmark.Best3YearReturn, false, 0);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, benchmark._benchmarkSymbol + " Worst 3 Year Return");
        ExportPortBenchmarkBestWorst(rowIndex, colIndex, benchmark.Worst3YearReturn, false, 0);

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, benchmark._benchmarkSymbol + " Benchmark Sharp Ratio");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", benchmark.SharpRatio * 100));

        rowIndex++;
        rowIndex++;

        endRowIndex[0] = rowIndex;
    }

    /**
     * 导出投资组合基准最佳最差
     * 对应C# ExportPortBenchmarkBestWorst方法
     */
    private static void ExportPortBenchmarkBestWorst(int rowIndex, int colIndex, ReturnWithTimeSpan ReturnWithTimeSpan, boolean withSPR, double spr) {
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", ReturnWithTimeSpan.value * 100));
        _excel.SetRangValue(rowIndex, colIndex + 2,
                            ReturnWithTimeSpan.beginTime.getMonthValue() + "/" + ReturnWithTimeSpan.beginTime.getYear() +
                            " to " +
                            ReturnWithTimeSpan.endTime.getMonthValue() + "/" + ReturnWithTimeSpan.endTime.getYear(), 25);
        if (withSPR) {
            _excel.SetRangValue(rowIndex, colIndex + 3, "Benchmark Return During the same time", 40);
            _excel.SetRangValue(rowIndex, colIndex + 4, String.format("%.2f%%", spr * 100));
        }
    }
} 