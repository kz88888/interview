package com.qsystem.qsexcel;

import com.qsystem.clientlib.QPortfolioBase;
import com.qsystem.clientlib.ReturnDistribution;
import com.qsystem.qsexcel.excelutil.ExcelHelper;
import java.util.*;

/**
 * Excel交易组收益导出器
 * 对应C# QSystem.Excel.ExcelExporterTradeGroupReturn
 */
public class ExcelExporterTradeGroupReturn {
    private static ExcelHelper _excel = null;

    /**
     * 导出交易组收益
     * 对应C# ExportTradeGroupReturn方法
     */
    public static void ExportTradeGroupReturn(QPortfolioBase QPortfolioBaseInfo, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        int rowIndex = 0;
        int colIndex = 0;
        _excel.CreateNewWorkSheet(SimulatorSystemStrings.TradeGroupReturnDistributions);

        ReturnDistribution TradeGroupReturnDistribution = QPortfolioBaseInfo.Summary.TradeGroupReturnDistribution;
        SortedMap<Double, Integer> distributionDictionary = QPortfolioBaseInfo.Summary.TradeGroupReturnDistribution.DistributionDictionary;

        int spreadCount = QPortfolioBaseInfo.Summary.TradeGroupReturnDistribution.SpreadCount;
        double range = QPortfolioBaseInfo.Summary.TradeGroupReturnDistribution.Range;
        Object[][] valueArray = new Object[spreadCount + 1][2];

        // region Title
        valueArray[rowIndex][colIndex++] = "Value";
        valueArray[rowIndex][colIndex] = "Count";
        // endregion

        List<Double> keys = new ArrayList<>(distributionDictionary.keySet());
        for (int tmpIndx = 0; tmpIndx < distributionDictionary.size(); ++tmpIndx) {
            colIndex = 0;
            rowIndex++;
            double lower = keys.get(tmpIndx);
            double upper = 0;
            if (tmpIndx != distributionDictionary.size() - 1) {
                upper = keys.get(tmpIndx + 1);
            } else {
                upper = lower + range;
            }
            double avg = (lower + upper) / 2;
            valueArray[rowIndex][colIndex++] = String.format("%.3f", avg);
            valueArray[rowIndex][colIndex++] = distributionDictionary.get(keys.get(tmpIndx));
        }

        _excel.SetRangValue(1, 500, spreadCount + 1, 501, valueArray);

        rowIndex = 1;
        colIndex = 1;

        _excel.SetRangValue(rowIndex, colIndex, "Groups", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(TradeGroupReturnDistribution.OrderNumber));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Period with Positive Returns (win)", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(TradeGroupReturnDistribution.NumberOfOrderWin));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Period with Negative (lose)", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(TradeGroupReturnDistribution.NumberOfOrderLose));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Win ratio", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", TradeGroupReturnDistribution.WinRatio * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Expected Return per TradeGroup (95%)", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.6f%%", TradeGroupReturnDistribution.WinLossRangeAverage * 100) + "(" + String.format("%.6f%%", TradeGroupReturnDistribution.WinLossRangeLower * 100) + "," + String.format("%.6f%%", TradeGroupReturnDistribution.WinLossRangeUpper * 100) + ")");

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Standard Deviation", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(TradeGroupReturnDistribution.StandardDeviation));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Point < -0.05", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(TradeGroupReturnDistribution.NumberOfSampleCount));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Point < -0.025", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(TradeGroupReturnDistribution.NumberOfSampleCount0025));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Point < -0.01", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(TradeGroupReturnDistribution.NumberOfSampleCount001));

        // rowIndex++;
        // _excel.SetRangValue(rowIndex, colIndex, "Mean Value", 25);
        // _excel.SetRangValue(rowIndex, colIndex + 1, TradeGroupReturnDistribution.MeanValue.toString());

        // Complete() will be called by ExcelExportContext
    }
} 