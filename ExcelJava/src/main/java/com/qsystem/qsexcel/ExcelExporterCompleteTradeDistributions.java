package com.qsystem.qsexcel;

import com.qsystem.clientlib.QPortfolioBase;
import com.qsystem.clientlib.QPortfolioSummary;
import com.qsystem.clientlib.ReturnDistribution;
import com.qsystem.clientlib.CompleteOrder;
import com.qsystem.clientlib.JsonModel.TradeConfirmation;
import com.qsystem.qsexcel.excelutil.ExcelHelper;
import java.util.*;

/**
 * Excel完整交易分布导出器
 * 对应C# QSystem.Excel.ExcelExporterCompleteOrderDistributions
 */
public class ExcelExporterCompleteTradeDistributions {
    private static ExcelHelper _excel = null;

    /**
     * 导出投资组合完整订单分布
     * 对应C# ExportPortCompleteOrderDistributions方法
     */
    public static void ExportPortCompleteOrderDistributions(QPortfolioBase QPortfolioBaseInfo, String fileName, String sheetName, ReturnDistribution OrderReturnDistribution) {
        _excel = ExcelExportContext.getExcelHelper(fileName);
        int rowIndex = 0;
        int colIndex = 0;
        _excel.CreateNewWorkSheet(sheetName);

        SortedMap<Double, Integer> distributionDictionary = OrderReturnDistribution.DistributionDictionary;
        int spreadCount = OrderReturnDistribution.SpreadCount;
        double range = OrderReturnDistribution.Range;
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
            if (lower == -1 || lower == 1) {
                valueArray[rowIndex][colIndex++] = String.format("%.3f", lower);
                valueArray[rowIndex][colIndex++] = distributionDictionary.get(keys.get(tmpIndx)).toString();
                continue;
            }
            double upper = 0;
            if (tmpIndx != distributionDictionary.size() - 1) {
                upper = keys.get(tmpIndx + 1);
            } else {
                upper = lower + range;
            }
            valueArray[rowIndex][colIndex++] = String.format("%.3f", lower);
            valueArray[rowIndex][colIndex++] = distributionDictionary.get(keys.get(tmpIndx)).toString();
        }

        _excel.SetRangValue(1, 500, spreadCount + 1, 501, valueArray);

        QPortfolioSummary summary = QPortfolioBaseInfo.Summary;

        rowIndex = 1;
        colIndex = 1;

        _excel.SetRangValue(rowIndex, colIndex, "Number of Orders", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(OrderReturnDistribution.OrderNumber));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Orders (win)", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(OrderReturnDistribution.NumberOfOrderWin));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Orders (lose)", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(OrderReturnDistribution.NumberOfOrderLose));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Win ratio", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", OrderReturnDistribution.WinRatio * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Expected Return per Order (95%)", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", OrderReturnDistribution.WinLossRangeAverage * 100) + "(" + String.format("%.2f%%", OrderReturnDistribution.WinLossRangeLower * 100) + "," + String.format("%.2f%%", OrderReturnDistribution.WinLossRangeUpper * 100) + ")");

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Consecutive Wins", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(OrderReturnDistribution.ConsecutiveWinsMaxNumber) + "(" + String.valueOf(OrderReturnDistribution.ConsecutiveWinsMaxNumberProfit) + ")");

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Consecutive Wins Profit", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(OrderReturnDistribution.ConsecutiveWinsMaxProfit) + "(" + String.valueOf(OrderReturnDistribution.ConsecutiveWinsMaxProfitNumber) + ")");

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Consecutive Losses", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(OrderReturnDistribution.ConsecutiveLossesMaxNumber) + "(" + String.valueOf(OrderReturnDistribution.ConsecutiveLossesMaxNumberProfit) + ")");

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Consecutive Losses Profit", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(OrderReturnDistribution.ConsecutiveLossesMaxProfit) + "(" + String.valueOf(OrderReturnDistribution.ConsecutiveLossesMaxProfitNumber) + ")");

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Standard Deviation", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(OrderReturnDistribution.StandardDeviation));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Max Down", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.format("%.2f%%", OrderReturnDistribution.MaxDown * 100));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Point < -0.05", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(OrderReturnDistribution.NumberOfSampleCount));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Point < -0.025", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(OrderReturnDistribution.NumberOfSampleCount0025));

        rowIndex++;
        _excel.SetRangValue(rowIndex, colIndex, "Number of Point < -0.01", 25);
        _excel.SetRangValue(rowIndex, colIndex + 1, String.valueOf(OrderReturnDistribution.NumberOfSampleCount001));

        if (OrderReturnDistribution.CompleteTradeList != null && OrderReturnDistribution.CompleteTradeList.size() > 0) {
            int rowCount = OrderReturnDistribution.CompleteTradeList.size() + 1;
            int colCount = 2;
            Object[][] TradeGroupIDValueArray = new Object[rowCount][colCount];
            rowIndex = 0;
            colIndex = 0;
            TradeGroupIDValueArray[rowIndex][colIndex++] = "Order ID";
            TradeGroupIDValueArray[rowIndex][colIndex] = "Win or Loss";
            rowIndex++;
            for (CompleteOrder CompleteOrder : OrderReturnDistribution.CompleteTradeList) {
                if (CompleteOrder.CompleteTradeList.size() <= 0) {
                    continue;
                }
                colIndex = 0;
                TradeConfirmation first = CompleteOrder.CompleteTradeList.get(0);
                TradeGroupIDValueArray[rowIndex][colIndex++] = first.OrderGuidString();
                TradeGroupIDValueArray[rowIndex][colIndex] = String.valueOf(CompleteOrder.PercentageWinLoss);
                rowIndex++;
            }
            _excel.SetRangValue(1, 100, rowCount, 100 + colCount - 1, TradeGroupIDValueArray);
        }

        // Complete() will be called by ExcelExportContext
    }
} 