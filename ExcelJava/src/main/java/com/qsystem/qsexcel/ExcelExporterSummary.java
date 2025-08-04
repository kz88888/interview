package com.qsystem.qsexcel;

import com.qsystem.clientlib.QPortfolioBase;
import com.qsystem.clientlib.QPortfolioSummary;
import com.qsystem.clientlib.ReturnWithTimeSpan;
import com.qsystem.qsexcel.excelutil.ExcelHelper;
import java.time.format.DateTimeFormatter;

/**
 * Excel摘要导出器
 */
public class ExcelExporterSummary {
    private static ExcelHelper _excel = null;

    /**
     * 导出投资组合摘要
     */
    public static void ExportPortSummary(QPortfolioBase port, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        int rowIndex = 1;
        int colIndex = 1;
        _excel.CreateNewWorkSheet(SimulatorSystemStrings.SummarySheetName);

        QPortfolioSummary summary = port.Summary;

        _excel.SetRangValue(rowIndex, colIndex, "Begin Date", 45);
        _excel.SetRangValue(rowIndex, colIndex + 1, summary.BeginDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "End Date");
        _excel.SetRangValue(rowIndex, colIndex + 1, summary.EndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Begin Portfolio Value", 45);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(summary.InitialBalance));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "End Portfolio Value");
        double lastBalance = port.BalancesDaily.values().stream().reduce((first, second) -> second).orElse(0.0);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(lastBalance));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Year Interval");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(summary.YearInterval));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Data Points Tested");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(summary.DataPointTested));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Strategy Return");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", summary.StrategyReturn * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Sharp Ratio");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", summary.SharpRatio * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Annualized Return");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", summary.YearPortfolioRatio * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Account Max Drawn Down");
        ReturnWithTimeSpan ele = summary.AccountMaxDrawnDown;
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", ele.value * 100));
        _excel.SetRangValue(rowIndex, colIndex + 2, 
                           ele.beginTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + 
                           " to " + 
                           ele.endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Average Exposure");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(summary.AverageExposure));
                        
        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Beta");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(summary.Beta));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Alpha");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", summary.Alpha * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Annualize Alpha");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", summary.AnnualizeAlpha * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Benchmark Return");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", port.Benchmark.getBenchmarkReturn() * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Benchmark Sharp Ratio");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", port.Benchmark.SharpRatio * 100));

        rowIndex++;
        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Delta");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", summary.delta * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Vega");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", summary.vega * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Theta");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", summary.theta * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Maintenance Margins");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", summary.margins * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Total Transaction Expenses");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.4f", summary.TotalTransactionExpenses));

        rowIndex++;
        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Total Net Profit");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(summary.TotalNetProfit));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Profit Factor");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(summary.ProfitFactor));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Gross Profit");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(summary.GrossProfit));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Gross Loss");
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(summary.GrossLoss));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Consecutive Wins", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(summary.ConsecutiveWinsMaxNumber) + "(" + String.valueOf(summary.ConsecutiveWinsMaxNumberProfit) + ")");

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Consecutive Wins Profit", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(summary.ConsecutiveWinsMaxProfit) + "(" + summary.ConsecutiveWinsMaxProfitNumber + ")");

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Consecutive Losses", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(summary.ConsecutiveLossesMaxNumber) + "(" + String.valueOf(summary.ConsecutiveLossesMaxNumberProfit) + ")");

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Consecutive Losses Profit", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(summary.ConsecutiveLossesMaxProfit) + "(" + String.valueOf(summary.ConsecutiveLossesMaxProfitNumber) + ")");

        rowIndex++;
        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "*See benchmark work sheet for details");
        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "**See return distribution (Order) page for details,, return numbers do not included PNL from portfolio level hedging.");

        // Complete() will be called by ExcelExportContext
    }
} 