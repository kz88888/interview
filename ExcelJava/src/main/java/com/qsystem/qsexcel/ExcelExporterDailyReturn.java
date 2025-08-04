package com.qsystem.qsexcel;

import com.qsystem.clientlib.QPortfolioBase;
import com.qsystem.clientlib.ReturnDistribution;
import com.qsystem.qsexcel.excelutil.ExcelHelper;
import java.util.*;

/**
 * Excel日收益导出器
 * 对应C# QSystem.Excel.ExcelExporterDailyReturn
 */
public class ExcelExporterDailyReturn {
    private static ExcelHelper _excel = null;

    /**
     * 导出投资组合日收益
     * 对应C# ExportPortDailyReturn方法
     */
    public static void ExportPortDailyReturn(QPortfolioBase QPortfolioBaseInfo, String fileName) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        int rowIndex = 0;
        int colIndex = 0;
        _excel.CreateNewWorkSheet(SimulatorSystemStrings.QPortfolioBaseReturnDistributions);

        ReturnDistribution QPortfolioBaseReturnDistribution = QPortfolioBaseInfo.Summary.QPortfolioBaseReturnDistribution;
        SortedMap<Double, Integer> distributionDictionary = QPortfolioBaseInfo.Summary.QPortfolioBaseReturnDistribution.DistributionDictionary;

        int spreadCount = QPortfolioBaseInfo.Summary.QPortfolioBaseReturnDistribution.SpreadCount;
        double range = QPortfolioBaseInfo.Summary.QPortfolioBaseReturnDistribution.Range;
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
            valueArray[rowIndex][colIndex++] = distributionDictionary.get(keys.get(tmpIndx)).toString();
        }

        _excel.SetRangValue(1, 500, spreadCount + 1, 501, valueArray);

        rowIndex = 1;
        colIndex = 1;

        _excel.SetRangValue(rowIndex, colIndex, "Days", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(QPortfolioBaseReturnDistribution.OrderNumber));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Period with Positive Returns (win)", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(QPortfolioBaseReturnDistribution.NumberOfOrderWin));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Period with Negative (lose)", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(QPortfolioBaseReturnDistribution.NumberOfOrderLose));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Win ratio", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", QPortfolioBaseReturnDistribution.WinRatio * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Expected Return per Day (95%)", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.6f%%", QPortfolioBaseReturnDistribution.WinLossRangeAverage * 100) + "(" + String.format("%.6f%%", QPortfolioBaseReturnDistribution.WinLossRangeLower * 100) + "," + String.format("%.6f%%", QPortfolioBaseReturnDistribution.WinLossRangeUpper * 100) + ")");

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Standard Deviation", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(QPortfolioBaseReturnDistribution.StandardDeviation));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Point < -0.05", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(QPortfolioBaseReturnDistribution.NumberOfSampleCount));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Point < -0.025", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(QPortfolioBaseReturnDistribution.NumberOfSampleCount0025));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Point < -0.01", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(QPortfolioBaseReturnDistribution.NumberOfSampleCount001));

        // rowIndex++;
        // _excel.SetRangValue(rowIndex, colIndex, "Mean Value", 25);
        // _excel.SetRangValue(rowIndex, colIndex + 1, QPortfolioBaseReturnDistribution.MeanValue.toString());

        // Complete() will be called by ExcelExportContext
    }
} 