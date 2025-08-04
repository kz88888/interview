package com.qsystem.qsexcel;

import com.qsystem.clientlib.BalanceList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;

/**
 * 摘要列表类
 */
public class SummaryList {
    private static final Logger Log = LogManager.getLogger("QBroker");
    private static int FirstSharpRatioBalancesLineNum = 6;
    public static int BalancesLineChartNum;
    
    public SortedMap<String, PortfolioSummaryEx> SummaryListDictionary = new TreeMap<>();
    public SortedMap<java.time.LocalDateTime, SortedMap<String, Double>> BalancesDictionary = new TreeMap<>();
    public SortedMap<java.time.LocalDateTime, SortedMap<String, Double>> BalancesLineChartDictionary = new TreeMap<>();

    /**
     * 生成余额字典
     */
    public void GenerateBalanceDictionary() {
        List<PortfolioSummaryEx> tmpSummaryList = new ArrayList<>();
        for (String key : SummaryListDictionary.keySet()) {
            PortfolioSummaryEx summary = SummaryListDictionary.get(key);
            summary.PortfolioSummaryExKey = key;
            tmpSummaryList.add(summary);
            BalanceList Balances = summary.Balances;
            for (java.time.LocalDateTime date : Balances.keySet()) {
                if (!BalancesDictionary.containsKey(date)) {
                    SortedMap<String, Double> tmpDic = new TreeMap<>();
                    BalancesDictionary.put(date, tmpDic);
                    tmpDic = new TreeMap<>();
                    BalancesLineChartDictionary.put(date, tmpDic);
                }
            }
        }

        tmpSummaryList.sort(new PortfolioSummaryExCompareReverse());
        List<PortfolioSummaryEx> balanceLineSummaryList = new ArrayList<>();
        
        if (tmpSummaryList.size() > FirstSharpRatioBalancesLineNum) {
            for (int indx = 0; indx < FirstSharpRatioBalancesLineNum; ++indx) {
                balanceLineSummaryList.add(tmpSummaryList.get(indx));
            }
            BalancesLineChartNum = FirstSharpRatioBalancesLineNum;
        } else {
            balanceLineSummaryList = tmpSummaryList;
            BalancesLineChartNum = tmpSummaryList.size();
        }

        for (java.time.LocalDateTime date : BalancesDictionary.keySet()) {
            for (String key : SummaryListDictionary.keySet()) {
                double price = GetBalance(SummaryListDictionary.get(key).Balances, date);
                BalancesDictionary.get(date).put(key, price);
            }

            for (PortfolioSummaryEx tmpSummary : balanceLineSummaryList) {
                double price = GetBalance(tmpSummary.Balances, date);
                BalancesLineChartDictionary.get(date).put(tmpSummary.PortfolioSummaryExKey, price);
            }
        }
    }

    /**
     * 获取余额
     */
    double GetBalance(BalanceList balanceList, java.time.LocalDateTime date) {
        double price = Double.NaN;

        if (balanceList.containsKey(date)) {
            price = balanceList.get(date);
        } else {
            java.time.LocalDateTime start = balanceList.keySet().iterator().next();
            java.time.LocalDateTime end = balanceList.keySet().stream().reduce((first, second) -> second).orElse(start);
            
            if (date.compareTo(start) >= 0 && date.compareTo(end) <= 0) {
                // 时间在范围内
                boolean find = false;
                java.time.LocalDateTime cur = date;
                while (cur.compareTo(start) >= 0) {
                    if (balanceList.containsKey(cur)) {
                        find = true;
                        price = balanceList.get(cur);
                        break;
                    }
                    cur = cur.plusDays(1);
                }

                if (!find) {
                    cur = date;
                    while (cur.compareTo(end) <= 0) {
                        if (balanceList.containsKey(cur)) {
                            find = true;
                            price = balanceList.get(cur);
                            break;
                        }
                        cur = cur.minusDays(1);
                    }
                }
            } else if (date.compareTo(start) < 0) {
                // 小于开始时间
                price = balanceList.values().iterator().next();
            } else if (date.compareTo(end) > 0) {
                // 大于结束时间
                price = balanceList.values().stream().reduce((first, second) -> second).orElse(0.0);
            }
        }
        return price;
    }

    /**
     * 计算平均摘要
     */
    public static PortfolioSummaryEx CalcAverageSummay(SortedMap<String, PortfolioSummaryEx> summaryList) {
        PortfolioSummaryEx avgSummary = new PortfolioSummaryEx();

        for (PortfolioSummaryEx summary : summaryList.values()) {
            avgSummary.StrategyReturn += summary.StrategyReturn;
            avgSummary.AccountMaxDrawnDown.value += summary.AccountMaxDrawnDown.value;
            avgSummary.BenchmarkReturn += summary.BenchmarkReturn;
            avgSummary.StrategyReturnInSampleStart += summary.StrategyReturnInSampleStart;
            avgSummary.StrategyReturnOutofSample += summary.StrategyReturnOutofSample;
            avgSummary.YearInterval += summary.YearInterval;
            avgSummary.YearPortfolioRatio += summary.YearPortfolioRatio;
            avgSummary.AverageExposure += summary.AverageExposure;
            avgSummary.OrderNumber += summary.OrderNumber;
            avgSummary.NumberOfOrderWin += summary.NumberOfOrderWin;
            avgSummary.NumberOfOrderLose += summary.NumberOfOrderLose;
            avgSummary.WinRatio += summary.WinRatio;
            avgSummary.WinLossRangeLower += summary.WinLossRangeLower;
            avgSummary.WinLossRangeUpper += summary.WinLossRangeUpper;
            avgSummary.WinLossRangeAverage += summary.WinLossRangeAverage;
            avgSummary.Beta += summary.Beta;
            avgSummary.Alpha += summary.Alpha;
            avgSummary.AnnualizeAlpha += summary.AnnualizeAlpha;
            avgSummary.SharpRatio += summary.SharpRatio;

            avgSummary.DataPointTested += summary.DataPointTested;
            avgSummary.InitialBalance += summary.InitialBalance;
            avgSummary.TotalNetProfit += summary.TotalNetProfit;
            avgSummary.ProfitFactor += summary.ProfitFactor;
            avgSummary.GrossProfit += summary.GrossProfit;
            avgSummary.GrossLoss += summary.GrossLoss;
            avgSummary.ConsecutiveWinsMaxNumber += summary.ConsecutiveWinsMaxNumber;
            avgSummary.ConsecutiveWinsMaxNumberProfit += summary.ConsecutiveWinsMaxNumberProfit;
            avgSummary.ConsecutiveWinsMaxProfitNumber += summary.ConsecutiveWinsMaxProfitNumber;
            avgSummary.ConsecutiveWinsMaxProfit += summary.ConsecutiveWinsMaxProfit;
            avgSummary.ConsecutiveLossesMaxNumber += summary.ConsecutiveLossesMaxNumber;
            avgSummary.ConsecutiveLossesMaxNumberProfit += summary.ConsecutiveLossesMaxNumberProfit;
            avgSummary.ConsecutiveLossesMaxProfitNumber += summary.ConsecutiveLossesMaxProfitNumber;
            avgSummary.ConsecutiveLossesMaxProfit += summary.ConsecutiveLossesMaxProfit;

            avgSummary.NetNorminalExposure += summary.NetNorminalExposure;
            avgSummary.MaxGrossExposure += summary.MaxGrossExposure;
        }

        int count = summaryList.size();
        avgSummary.StrategyReturn /= count;
        avgSummary.AccountMaxDrawnDown.value /= count;
        avgSummary.StrategyReturnInSampleStart /= count;
        avgSummary.StrategyReturnOutofSample /= count;
        avgSummary.YearInterval /= count;
        avgSummary.YearPortfolioRatio /= count;
        avgSummary.AverageExposure /= count;
        avgSummary.OrderNumber /= count;
        avgSummary.NumberOfOrderWin /= count;
        avgSummary.NumberOfOrderLose /= count;
        avgSummary.WinRatio /= count;
        avgSummary.WinLossRangeLower /= count;
        avgSummary.WinLossRangeUpper /= count;
        avgSummary.WinLossRangeAverage /= count;
        avgSummary.Beta /= count;
        avgSummary.Alpha /= count;
        avgSummary.AnnualizeAlpha /= count;
        avgSummary.SharpRatio /= count;

        avgSummary.DataPointTested /= count;
        avgSummary.InitialBalance /= count;
        avgSummary.TotalNetProfit /= count;
        avgSummary.ProfitFactor /= count;
        avgSummary.GrossProfit /= count;
        avgSummary.GrossLoss /= count;
        avgSummary.ConsecutiveWinsMaxNumber /= count;
        avgSummary.ConsecutiveWinsMaxNumberProfit /= count;
        avgSummary.ConsecutiveWinsMaxProfitNumber /= count;
        avgSummary.ConsecutiveWinsMaxProfit /= count;
        avgSummary.ConsecutiveLossesMaxNumber /= count;
        avgSummary.ConsecutiveLossesMaxNumberProfit /= count;
        avgSummary.ConsecutiveLossesMaxProfitNumber /= count;
        avgSummary.ConsecutiveLossesMaxProfit /= count;

        avgSummary.NetNorminalExposure /= count;
        avgSummary.MaxGrossExposure /= count;

        return avgSummary;
    }
} 