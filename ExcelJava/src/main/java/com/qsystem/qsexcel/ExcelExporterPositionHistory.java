package com.qsystem.qsexcel;

import com.qsystem.clientlib.PositionEx;
import com.qsystem.clientlib.PositionHistoryList;
import com.qsystem.clientlib.BalanceList;
import com.qsystem.clientlib.BalanceDailyList;
import com.qsystem.clientlib.Calculator;
import com.qsystem.clientlib.PositionCompare;
import com.qsystem.clientlib.JsonModel.OrderSymbolType;
import com.qsystem.qsexcel.excelutil.ExcelHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * Excel持仓历史导出器
 * 对应C# QSystem.Excel.ExcelExporterHistoryPositions
 */
public class ExcelExporterPositionHistory {
    private static ExcelHelper _excel = null;
    private static Logger Log = LogManager.getLogger("QBroker");

    /**
     * 导出投资组合历史持仓
     * 对应C# ExportPortPositionHistory方法
     */
    public static void ExportPortPositionHistory(PositionHistoryList positions, BalanceList Balances, BalanceDailyList BalancesDailyWithTime, String fileName, String sheetname) {
        SortedMap<LocalDateTime, Double> BalancesDaily = Calculator.GetBalanceDailyWithoutTime(BalancesDailyWithTime);

        SortedMap<Integer, Object[][]> valuelist = new TreeMap<>();
        _excel = ExcelExportContext.getExcelHelper(fileName);
        int rowIndex = 1;
        _excel.CreateNewWorkSheet(sheetname); //sheet name = position history
        Object[][] valueArray = new Object[1][21]; //新增两列容纳theta和vega

        int currentValueColIndex = 0;
        int colCount = 0;

        valueArray[0][currentValueColIndex++] = "Date";
        valueArray[0][currentValueColIndex++] = "TimeStamp";

        valueArray[0][currentValueColIndex++] = "Symbol";
        valueArray[0][currentValueColIndex++] = "Quantity";
        valueArray[0][currentValueColIndex++] = "PriceEntered";

        valueArray[0][currentValueColIndex++] = "Close";
        valueArray[0][currentValueColIndex++] = "AdjClose";
        valueArray[0][currentValueColIndex++] = "Position Value";
        valueArray[0][currentValueColIndex++] = "Position Size";
        valueArray[0][currentValueColIndex++] = "Ask";
        valueArray[0][currentValueColIndex++] = "Bid";
        valueArray[0][currentValueColIndex++] = "TheoValue";
        valueArray[0][currentValueColIndex++] = "Delta";
        //新增Theta和Vega
        valueArray[0][currentValueColIndex++] = "Theta";
        valueArray[0][currentValueColIndex++] = "Vega";
        valueArray[0][currentValueColIndex++] = "Underlying Close";
        valueArray[0][currentValueColIndex++] = "Beta Delta Dollar";
        valueArray[0][currentValueColIndex++] = "Theta Dollar";
        valueArray[0][currentValueColIndex++] = "Vega Dollar";
        valueArray[0][currentValueColIndex++] = "Maintenance Margins";
        valueArray[0][currentValueColIndex++] = "";

        colCount = currentValueColIndex;

        _excel.SetRangValue(1, 1, 1, colCount, valueArray);

        rowIndex++;

        for (Map.Entry<LocalDateTime, SortedMap<String, List<PositionEx>>> position : positions.entrySet()) {
            double AllTotalDeltaBasedPositionSize = 0;
            double AllTotalAbsDeltaBasedPositionSize = 0;
            double AllTotalBetaDeltaDollar = 0;
            double AllTotalThetaDollar = 0;
            double AllTotalVegaDollar = 0;
            double AllTotalMaintenanceMargin = 0;
            LocalDate curDate = position.getKey().toLocalDate();
            boolean hasMatchingDate = BalancesDaily.keySet().stream()
                    .anyMatch(dateTime -> dateTime.toLocalDate().equals(curDate));
            if (!hasMatchingDate) {
                continue;
            }

            double sumPositionValue = 0;
            int count = 0;
            SortedMap<String, List<PositionEx>> positionGroup = position.getValue();
            for (List<PositionEx> ps : positionGroup.values()) {
                count += ps.size();
            }
            count += positionGroup.size() * 2;
            valueArray = new Object[count + 5][colCount];
            valueArray[0][0] = position.getKey().toLocalDate().toString();

            int currentValueRowIndex = 0;
            rowIndex++;
            int rowIndexKey = rowIndex;
            int validCount = 0;

            for (List<PositionEx> ps : positionGroup.values()) {
                ps.sort(new PositionCompare());
                boolean flag = true;
                // Find balance entry with matching date
                LocalDate positionDate = position.getKey().toLocalDate();
                Optional<Map.Entry<LocalDateTime, Double>> matchingBalance = Balances.entrySet().stream()
                        .filter(entry -> entry.getKey().toLocalDate().equals(positionDate))
                        .findFirst();
                
                if (!matchingBalance.isPresent()) {
                    valueArray[currentValueRowIndex][currentValueColIndex++] = "Data NOT AVAILABVLE";
                    continue;
                }
                double totalDeltaPositionBasedSize = 0;
                valueArray[0][1] = ps.get(0).position.TimeStamp.toLocalTime().toString();
                for (PositionEx p : ps) {
                    if (p.position.Quantity == 0) {
                        continue;
                    }
                    currentValueColIndex = 2;
                    if (p.position.SymbolType == OrderSymbolType.Option) {
                        valueArray[currentValueRowIndex][currentValueColIndex++] = p.position.ID();
                    } else {
                        if (!p.position.Symbol.equals("$CASH")) {
                            valueArray[currentValueRowIndex][currentValueColIndex++] = p.position.Symbol;
                        } else {
                            valueArray[currentValueRowIndex][currentValueColIndex++] = p.position.Symbol;
                        }
                    }

                    valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(p.position.Quantity);
                    valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(p.position.PriceEntered);

                    double PositionValue;
                    if (!p.position.Symbol.equals("$CASH")) {
                        double closePrice = p.positionExtention.Close;
                        double adjClose = p.positionExtention.AdjClose;

                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(closePrice);
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(adjClose);

                        PositionValue = p.positionExtention.PositionValue;
                        sumPositionValue += PositionValue;
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(PositionValue);
                    } else {
                        PositionValue = p.positionExtention.PositionValue;
                        sumPositionValue += PositionValue;
                        currentValueColIndex++;
                        currentValueColIndex++;
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(PositionValue);
                    }
                    double PositionSize = PositionValue / Balances.get(position.getKey());
                    valueArray[currentValueRowIndex][currentValueColIndex++] = String.format("%.2f%%", PositionSize * 100);

                    if (p.position.SymbolType == OrderSymbolType.Option) {
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(p.positionExtention.Ask);
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(p.positionExtention.Bid);
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(p.positionExtention.TheoValue);

                        double deltaBasedPosition = Double.NaN;
                        double delta = Double.NaN;
                        double theta = Double.NaN;
                        double vega = Double.NaN;
                        deltaBasedPosition = p.position.getDeltaBasedPositionSize();
                        delta = p.position.getDelta();
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(delta);
                        //excel表中新增theta和vega
                        theta = p.position.getTheta();
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(theta);
                        vega = p.position.getVega();
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(vega);
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(p.positionExtention.Underlying);
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(p.positionExtention.BetaDeltaDollar);
                        //20200114新增thetaDollar和VegaDollar
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(p.positionExtention.ThetaDollar);
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(p.positionExtention.VegaDollar);
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(p.positionExtention.MaintenanceMargin);
                    } else {
                        currentValueColIndex++;
                        currentValueColIndex++;
                        currentValueColIndex++;
                        currentValueColIndex++;
                        currentValueColIndex++;

                        //因新增theta和vega，在此处空出两列令到后面的值能对齐
                        currentValueColIndex++;
                        currentValueColIndex++;
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(p.positionExtention.BetaDeltaDollar);
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(p.positionExtention.ThetaDollar);
                        valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(p.positionExtention.VegaDollar);
                        double deltaBasedPosition = p.position.getDeltaBasedPositionSize();
                        //删除列 valueArray[currentValueRowIndex][currentValueColIndex++] = String.format("%.2f%%", deltaBasedPosition);
                    }
                    totalDeltaPositionBasedSize += p.position.getDeltaBasedPositionSize();
                    AllTotalBetaDeltaDollar += p.positionExtention.BetaDeltaDollar;
                    //新增两个统计AllTotalThetaDollar和AllTotalVegaDollar
                    AllTotalThetaDollar += p.positionExtention.ThetaDollar;
                    AllTotalVegaDollar += p.positionExtention.VegaDollar;
                    AllTotalMaintenanceMargin += p.positionExtention.MaintenanceMargin;
                    AllTotalAbsDeltaBasedPositionSize += Math.abs(p.position.getDeltaBasedPositionSize());
                    currentValueRowIndex++;

                    validCount++;
                }
                currentValueRowIndex++;
                validCount++;
                AllTotalDeltaBasedPositionSize += totalDeltaPositionBasedSize;
            }
            try {
                //20200114不显示
                //valueArray[currentValueRowIndex][currentValueColIndex - 1] = String.format("%.2f%%", AllTotalAbsDeltaBasedPositionSize);
                //valueArray[currentValueRowIndex][currentValueColIndex] = String.format("%.2f%%", AllTotalDeltaBasedPositionSize);
                currentValueRowIndex++;
                currentValueColIndex = 2;
                valueArray[currentValueRowIndex][currentValueColIndex++] = "Balance";
                // Find balance entry with matching date for display
                LocalDate posDate = position.getKey().toLocalDate();
                Optional<Map.Entry<LocalDateTime, Double>> balanceEntry = Balances.entrySet().stream()
                        .filter(entry -> entry.getKey().toLocalDate().equals(posDate))
                        .findFirst();
                
                if (balanceEntry.isPresent()) {
                    valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(balanceEntry.get().getValue());
                    currentValueColIndex++;
                    currentValueColIndex++;
                    currentValueColIndex++;
                    currentValueColIndex++;
                    valueArray[currentValueRowIndex][currentValueColIndex++] = String.valueOf(sumPositionValue);
                    //2020.01.13 因为新增了两个参数，此处加多两列对齐
                    valueArray[currentValueRowIndex][currentValueColIndex + 7] = AllTotalBetaDeltaDollar;
                    valueArray[currentValueRowIndex][currentValueColIndex + 8] = AllTotalThetaDollar;
                    valueArray[currentValueRowIndex][currentValueColIndex + 9] = AllTotalVegaDollar;
                    valueArray[currentValueRowIndex][currentValueColIndex + 10] = AllTotalMaintenanceMargin;

                    currentValueRowIndex++;
                    validCount++;
                    validCount++;
                    double tmp = 0;

                    tmp = sumPositionValue / Balances.get(position.getKey());
                }

                currentValueRowIndex++;
                validCount++;
                validCount++;

                rowIndex = rowIndex + validCount;
                rowIndexKey = rowIndex;
                valuelist.put(rowIndexKey, valueArray);
            } catch (Exception ex) {
                Log.error(ex.getMessage());
            }
        }
        int ikk = 0;
        for (int k : valuelist.keySet()) {
            Object[][] oneshot = valuelist.get(k);
            ikk += oneshot.length;
        }

        Object[][] valueArrayFull = new Object[ikk][21];
        try {
            int index = 0;
            int k = 0;
            for (Object[][] oneshot : valuelist.values()) {
                if (k > 3888 && k % 100 == 0) {
                    Log.info("LOOP IS:" + k);
                    Log.info("INDEX IS:" + index);
                    Log.info("ONE SHOT SIZE IS:" + oneshot.length);
                    Log.info("VALUE ARRAY SIZE IS:" + rowIndex + 10000);
                }

                copyArrayData(oneshot, valueArrayFull, index);
                index += oneshot.length - 2;
                k++;
            }
            _excel.SetRangValue(2, 1, valueArrayFull.length , colCount, valueArrayFull);
            // Complete() will be called by ExcelExportContext
        } catch (Exception e) {
            Log.error(e.getMessage());
            Log.error(e.getStackTrace());
        }
    }

    /**
     * 复制数组数据
     * 对应C# copyArrayData方法
     */
    public static void copyArrayData(Object[][] source, Object[][] dest, int index) {
        try {
            int rowindex = index;
            for (int j = 0; j < source.length ; j++) {
                for (int i = 0; i < 21; i++) {
                    dest[rowindex][i] = source[j][i];
                }
                rowindex++;
            }
        } catch (Exception e) {
            Log.error(e.getMessage());
            Log.error(e.getStackTrace());
        }
    }
} 