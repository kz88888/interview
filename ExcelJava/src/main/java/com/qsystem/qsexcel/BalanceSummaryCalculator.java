package com.qsystem.qsexcel;

import com.qsystem.clientlib.BalanceList;
import com.qsystem.clientlib.LnOnBalanceList;
import com.qsystem.clientlib.ReturnWithTimeSpan;
import com.qsystem.clientlib.Benchmark;
import com.qsystem.clientlib.PortfolioSimulationConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap.SimpleEntry;

/**
 * 余额摘要计算器
 * 对应C# QSystem.Excel.BalanceSummaryCalculator
 */
public class BalanceSummaryCalculator {
    private static final Logger Log = LogManager.getLogger("QuantProject");
    private static int _sharpeRatioTimeSpan = GetSharpeRatioTimeSpan();
    private static PortfolioSimulationConfig _config = null;

    /**
     * 获取夏普比率时间跨度
     */
    private static int GetSharpeRatioTimeSpan() {
        return _config != null ? _config.SharpeRatioTimeSpan : 252;
    }

    /**
     * 获取夏普余额列表
     * 对应C# GetSharpBalanceList方法
     */
    public static SortedMap<LocalDateTime, Double> GetSharpBalanceList(SortedMap<LocalDateTime, Double> balances, LocalDateTime date, double[] startBalance) {
        SortedMap<LocalDateTime, Double> balanceList = new TreeMap<>();
        LocalDateTime curDate = date.minusDays(_sharpeRatioTimeSpan);
        boolean first = true;
        
        while (curDate.compareTo(date) <= 0) {
            if (balances.containsKey(curDate)) {
                balanceList.put(curDate, balances.get(curDate));
                if (first) {
                    startBalance[0] = balances.get(curDate);
                    first = false;
                }
            }
            curDate = curDate.plusDays(1);
        }
        return balanceList;
    }

    /**
     * 获取平均排序列表
     * 对应C# GetAverageSortedList方法
     */
    static SortedMap<LocalDateTime, Double> GetAverageSortedList(int averageCount, SortedMap<LocalDateTime, Double> valueList) {
        SortedMap<LocalDateTime, Double> averageSortedList = new TreeMap<>();
        if (valueList != null && valueList.size() > averageCount) {
            List<LocalDateTime> keys = new ArrayList<>(valueList.keySet());
            for (int indx = 0; indx <= (valueList.size() - averageCount); ++indx) {
                double average = GetAverageValue(valueList, indx, averageCount);
                averageSortedList.put(keys.get(indx + averageCount - 1), average);
            }
        }
        return averageSortedList;
    }

    /**
     * 获取平均值
     * 对应C# GetAverageValue方法
     */
    public static double GetAverageValue(SortedMap<LocalDateTime, Double> valueList, int startIndex, int count) {
        double average = 0;
        double sum = 0;
        List<Double> values = new ArrayList<>(valueList.values());
        for (int indx = startIndex; indx <= (startIndex + count - 1); ++indx) {
            sum += values.get(indx);
        }
        average = sum * 1.0 / count;
        return average;
    }

    /**
     * 获取最佳最差
     * 对应C# GetBestWorst方法
     */
    static void GetBestWorst(ReturnWithTimeSpan returnResult, SimpleEntry<Double, LocalDateTime> ReturnWithTimeSpan, SortedMap<LocalDateTime, Double> monthlySP500Adj, int monthInterval) {
        returnResult.beginTime = ReturnWithTimeSpan.getValue();
        returnResult.endTime = ReturnWithTimeSpan.getValue().plusMonths(monthInterval - 1);
        returnResult.value = ReturnWithTimeSpan.getKey();
    }

    /**
     * 计算余额
     * 对应C# CalculateBalances方法
     */
    static LnOnBalanceList CalculateBalances(Map<LocalDateTime, Double> balances) {
        LnOnBalanceList balanceLnReturnList = new LnOnBalanceList();

        double formerBalance = 0;
        boolean isFirstValue = true;
        for (LocalDateTime dt : balances.keySet()) {
            double balance = balances.get(dt);

            if (isFirstValue) {
                isFirstValue = false;
                balanceLnReturnList.put(dt, Double.NaN);
            } else {
                double balanceLnReturn = Math.log(balance / formerBalance);
                balanceLnReturnList.put(dt, balanceLnReturn);
            }
            formerBalance = balance;
        }

        return balanceLnReturnList;
    }

    /**
     * 计算贝塔值
     * 对应C# CalculateBeta方法
     */
    public static double CalculateBeta(SortedMap<LocalDateTime, SimpleDouble> lnReturnList, LnOnBalanceList LnOnBalances) {
        List<Double> balanceReturnValueList = new ArrayList<>();
        List<Double> returnValueList = new ArrayList<>();

        for (LocalDateTime date : LnOnBalances.keySet()) {
            try {
                LocalDateTime key = date;

                if (lnReturnList.containsKey(key)) {
                    balanceReturnValueList.add(LnOnBalances.get(date));
                    returnValueList.add(lnReturnList.get(key).Data);
                } else {
                    if (lnReturnList.containsKey(key.minusDays(1))) {
                        balanceReturnValueList.add(LnOnBalances.get(key.minusDays(1)));
                        returnValueList.add(lnReturnList.get(key.minusDays(1)).Data);
                    } else {
                        Log.error("date " + date.toString() + " is not in benchmark returns list");
                    }
                }
            } catch (Exception ex) {
                Log.debug(ex.getMessage());
            }
        }

        double varValue = CalculateVAR(returnValueList);
        double covarValue = CalculateCOVAR(returnValueList, balanceReturnValueList);

        double beta = covarValue / varValue;
        return beta;
    }

    /**
     * 计算夏普比率
     * 对应C# CalculateSharpRatio方法
     */
    public static double CalculateSharpRatio(SortedMap<LocalDateTime, Double> balanceList, double strategyReturn, double yearInterval) {
        Queue<Double> valueQueue = new LinkedList<>();
        SortedMap<LocalDateTime, Double> lnReturnList = new TreeMap<>();

        if (balanceList != null && balanceList.size() > 1) {
            LocalDateTime dt = balanceList.keySet().iterator().next();
            LocalDateTime prevDT = dt;
            List<LocalDateTime> keys = new ArrayList<>(balanceList.keySet());
            for (int i = 1; i < keys.size(); i++) {
                prevDT = dt;
                dt = keys.get(i);
                double lnreturnClose = Math.log(balanceList.get(dt) / balanceList.get(prevDT));
                lnReturnList.put(dt, lnreturnClose);
            }
        }

        for (double value : lnReturnList.values()) {
            if (!Double.isNaN(value)) {
                valueQueue.offer(value);
            }
        }

        double volatility = Calculate_Volatility(valueQueue);
        double annualReturn = Math.pow(strategyReturn + 1, 1 / yearInterval) - 1;
        double annualExcessReturn = 0;
        if (annualReturn != 0)
            annualExcessReturn = annualReturn - 0.00529; //0.0529 = average fed fund rate from 1954 to today.
        double annualVol = volatility * Math.pow(252, 0.5);

        if (annualVol <= 0.0)
            return 0;
        return annualExcessReturn / annualVol;
    }

    /**
     * 计算年化投资组合比率
     * 对应C# CalculateYearPortfolioRatio方法
     */
    public static double CalculateYearPortfolioRatio(double strategyReturn, double years) {
        return Math.pow(strategyReturn, 1.0 / years) - 1;
    }

    /**
     * 计算部分策略收益
     * 对应C# CalculatePartStrategyReturn方法
     */
    public static double CalculatePartStrategyReturn(Map<LocalDateTime, Double> Balances, LocalDateTime startDate, LocalDateTime endDate) {
        List<LocalDateTime> dates = new ArrayList<>(Balances.keySet());
        Collections.sort(dates);
        
        double startBalance = dates.stream()
            .filter(t -> t.compareTo(startDate) >= 0)
            .findFirst()
            .map(Balances::get)
            .orElse(0.0);
            
        double endBalance = dates.stream()
            .filter(t -> t.compareTo(endDate) <= 0)
            .max(LocalDateTime::compareTo)
            .map(Balances::get)
            .orElse(0.0);

        return endBalance / startBalance - 1;
    }

    /**
     * 计算最大收益（按年）
     * 对应C# CalculateMaxReturn方法（第一个重载）
     */
    public static boolean CalculateMaxReturn(Map<LocalDateTime, Double> Balances, int yearInterval, SimpleEntry<Double, Integer>[] bestReturn, SimpleEntry<Double, Integer>[] worstReturn) {
        if (Balances.size() == 0)
            throw new RuntimeException("Balance should contain at least one data point!!");

        TreeMap<Double, Integer> returnList = new TreeMap<>();

        List<LocalDateTime> dates = new ArrayList<>(Balances.keySet());
        Collections.sort(dates);
        
        if (dates.get(dates.size() - 1).compareTo(dates.get(0).plusYears(yearInterval - 1)) < 0) {
            returnList.put(Double.NaN, Integer.MIN_VALUE);
            bestReturn[0] = new SimpleEntry<>(returnList.firstEntry().getKey(), returnList.firstEntry().getValue());
            worstReturn[0] = new SimpleEntry<>(returnList.firstEntry().getKey(), returnList.firstEntry().getValue());
            return false;
        }

        boolean isFirstData = true;
        int currentYear = 0;

        double yearStartBalance = 0;
        double yearEndBalance = 0;
        double formerBalance = 0;
        for (LocalDateTime currentDate : dates) {
            if (isFirstData) {
                isFirstData = false;
                currentYear = currentDate.getYear();
                yearStartBalance = Balances.get(currentDate);
                formerBalance = yearStartBalance;
            }

            //一年结束
            if (currentYear + yearInterval - 1 < currentDate.getYear()) {
                yearEndBalance = formerBalance;
                double yearReturn = yearEndBalance / yearStartBalance - 1;

                if (!returnList.containsKey(yearReturn))
                    returnList.put(yearReturn, currentYear);

                currentYear = currentDate.getYear();
                yearStartBalance = Balances.get(currentDate);
            }

            formerBalance = Balances.get(currentDate);
        }

        if (returnList.size() > 0) {
            bestReturn[0] = new SimpleEntry<>(returnList.lastEntry().getKey(), returnList.lastEntry().getValue());
            worstReturn[0] = new SimpleEntry<>(returnList.firstEntry().getKey(), returnList.firstEntry().getValue());
            return true;
        }
        returnList.put(Double.NaN, Integer.MIN_VALUE);
        bestReturn[0] = new SimpleEntry<>(returnList.firstEntry().getKey(), returnList.firstEntry().getValue());
        worstReturn[0] = new SimpleEntry<>(returnList.firstEntry().getKey(), returnList.firstEntry().getValue());
        return false;
    }

    /**
     * 计算月余额
     * 对应C# CalculateMonthlyBalance方法
     */
    public static SortedMap<LocalDateTime, Double> CalculateMonthlyBalance(Map<LocalDateTime, Double> Balances) {
        SortedMap<LocalDateTime, Double> monthlyBalances = new TreeMap<>();
        List<LocalDateTime> dates = new ArrayList<>(Balances.keySet());
        Collections.sort(dates);
        
        LocalDateTime firstDatetime = LocalDateTime.of(dates.get(0).getYear(), dates.get(0).getMonth(), 1, 0, 0);
        monthlyBalances.put(firstDatetime, Balances.get(dates.get(0)));

        int totalDays = dates.size();
        for (int i = 1; i < totalDays; ++i) {
            if (dates.get(i - 1).getMonth() != dates.get(i).getMonth()) {
                LocalDateTime tmpDatetime = LocalDateTime.of(dates.get(i).getYear(), dates.get(i).getMonth(), 1, 0, 0);
                monthlyBalances.put(tmpDatetime, Balances.get(dates.get(i - 1)));
            }
        }
        int year = dates.get(totalDays - 1).getYear();
        int month = dates.get(totalDays - 1).getMonthValue() + 1;
        if (month == 13) {
            month = 1;
            year++;
        }
        LocalDateTime lastDatetime = LocalDateTime.of(year, month, 1, 0, 0);
        monthlyBalances.put(lastDatetime, Balances.get(dates.get(totalDays - 1)));

        return monthlyBalances;
    }

    /**
     * 查找局部最大最小值
     * 对应C# FindLocalMaxMin方法
     */
    public static SortedMap<LocalDateTime, Double> FindLocalMaxMin(Map<LocalDateTime, Double> Balances) {
        SortedMap<LocalDateTime, Double> localMaxMin = new TreeMap<>();
        List<LocalDateTime> dates = new ArrayList<>(Balances.keySet());
        Collections.sort(dates);
        
        for (int i = 0; i < dates.size(); ++i) {
            if (i == 0 || i == dates.size() - 1) {
                localMaxMin.put(dates.get(i), Balances.get(dates.get(i)));
            } else if ((Balances.get(dates.get(i)) >= Balances.get(dates.get(i - 1)) && Balances.get(dates.get(i)) > Balances.get(dates.get(i + 1))) ||
               (Balances.get(dates.get(i)) < Balances.get(dates.get(i - 1)) && Balances.get(dates.get(i)) <= Balances.get(dates.get(i + 1)))) {
                localMaxMin.put(dates.get(i), Balances.get(dates.get(i)));
            }
        }
        return localMaxMin;
    }

    /**
     * 计算最大回撤
     * 对应C# CalculateMaxDrawDown方法
     */
    public static SortedMap<LocalDateTime, Double> CalculateMaxDrawDown(SortedMap<LocalDateTime, Double> localMaxMin) {
        SortedMap<LocalDateTime, Double> maxDrawDownInfo = new TreeMap<>();
        int maxIndex = 0;
        int minIndex = localMaxMin.size() - 1;
        double maxDrawDown = 0;
        List<LocalDateTime> dates = new ArrayList<>(localMaxMin.keySet());
        Collections.sort(dates);
        
        for (int i = 0; i < localMaxMin.size() - 1; ) {
            double drawDown = 0;
            int k = i + 1;
            for (int j = i + 1; j <= localMaxMin.size() - 1; ++j) {
                drawDown = 1 - localMaxMin.get(dates.get(j)) / localMaxMin.get(dates.get(i));
                if (drawDown <= 0) {
                    k = j;
                    break;
                } else if (drawDown > maxDrawDown) {
                    maxDrawDown = drawDown;
                    maxIndex = i;
                    minIndex = j;
                    k = j + 1;
                }
            }
            i = k;
        }
        maxDrawDownInfo.put(dates.get(maxIndex), localMaxMin.get(dates.get(maxIndex)));
        maxDrawDownInfo.put(dates.get(minIndex), localMaxMin.get(dates.get(minIndex)));
        return maxDrawDownInfo;
    }

    /**
     * 计算最大收益（按月）
     * 对应C# CalculateMaxReturn方法（第二个重载）
     */
    public static boolean CalculateMaxReturn(Map<LocalDateTime, Double> Balances, int monthInterval, SimpleEntry<Double, LocalDateTime>[] bestReturn, SimpleEntry<Double, LocalDateTime>[] worstReturn, LocalDateTime begin, LocalDateTime end) {
        LocalDateTime beginDate = begin != null ? begin : LocalDateTime.MIN;
        LocalDateTime endDate = end != null ? end : LocalDateTime.MAX;

        TreeMap<Double, LocalDateTime> returnList = new TreeMap<>();

        List<LocalDateTime> dates = new ArrayList<>(Balances.keySet());
        Collections.sort(dates);

        if (dates.get(dates.size() - 1).compareTo(dates.get(0).plusMonths(monthInterval - 1)) < 0) {
            returnList.put(Double.NaN, LocalDateTime.MIN);
            bestReturn[0] = new SimpleEntry<>(returnList.firstEntry().getKey(), returnList.firstEntry().getValue());
            worstReturn[0] = new SimpleEntry<>(returnList.firstEntry().getKey(), returnList.firstEntry().getValue());
            return false;
        }

        for (int i = 0; i < dates.size() - monthInterval; ++i) {
            if (dates.get(i).compareTo(beginDate) >= 0 && dates.get(i).compareTo(endDate) <= 0
                && dates.get(i + monthInterval).compareTo(beginDate) >= 0 && dates.get(i + monthInterval).compareTo(endDate) <= 0) {
                double periodReturn = Balances.get(dates.get(i + monthInterval)) / Balances.get(dates.get(i)) - 1;
                if (!returnList.containsKey(periodReturn)) {
                    returnList.put(periodReturn, dates.get(i));
                }
            }
        }
        if (returnList.size() > 0) {
            bestReturn[0] = new SimpleEntry<>(returnList.lastEntry().getKey(), returnList.lastEntry().getValue());
            worstReturn[0] = new SimpleEntry<>(returnList.firstEntry().getKey(), returnList.firstEntry().getValue());
            return true;
        }
        returnList.put(Double.NaN, LocalDateTime.MIN);
        bestReturn[0] = new SimpleEntry<>(returnList.firstEntry().getKey(), returnList.firstEntry().getValue());
        worstReturn[0] = new SimpleEntry<>(returnList.firstEntry().getKey(), returnList.firstEntry().getValue());
        return false;
    }

    // 辅助计算方法
    private static double CalculateVAR(List<Double> values) {
        if (values.size() < 2) return 0.0;
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        return values.stream().mapToDouble(v -> Math.pow(v - mean, 2)).average().orElse(0.0);
    }

    private static double CalculateCOVAR(List<Double> x, List<Double> y) {
        if (x.size() != y.size() || x.size() < 2) return 0.0;
        double xMean = x.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double yMean = y.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        
        double sum = 0.0;
        for (int i = 0; i < x.size(); i++) {
            sum += (x.get(i) - xMean) * (y.get(i) - yMean);
        }
        return sum / (x.size() - 1);
    }

    private static double Calculate_Volatility(Queue<Double> values) {
        if (values.size() < 2) return 0.0;
        List<Double> valueList = new ArrayList<>(values);
        double mean = valueList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = valueList.stream().mapToDouble(v -> Math.pow(v - mean, 2)).average().orElse(0.0);
        return Math.sqrt(variance);
    }
} 