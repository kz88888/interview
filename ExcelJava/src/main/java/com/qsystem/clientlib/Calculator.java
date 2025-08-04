package com.qsystem.clientlib;

import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.qsystem.clientlib.JsonModel.*;

/**
 * Calculator类 - 从C#版本转换
 * 包含投资组合计算的核心功能
 */
public class Calculator {
    
    private static final Logger Log = LoggerFactory.getLogger(Calculator.class);
    public static int _sharpeRatioTimeSpan = 30;
    
    /**
     * 计算余额摘要
     * 对应C#: public static void CalculateBalanceSummary(double InitialCash, QPortfolioSummary summary, 
     *          SortedList<DateTime, double> BalancesDaily, Benchmark benchmark, List<Benchmark> benchmarkList, 
     *          DateTime startDate, DateTime endDate, double yearInterval)
     */
    public static void CalculateBalanceSummary(double InitialCash, QPortfolioSummary summary, 
                                              SortedMap<LocalDateTime, Double> BalancesDaily, 
                                              Benchmark benchmark, List<Benchmark> benchmarkList, 
                                              LocalDateTime startDate, LocalDateTime endDate, 
                                              double yearInterval) {
        
        SortedMap<LocalDateTime, Double> lnReturnList = benchmark.getReturns();
        // SortedList<DateTime, double> BalancesDaily = ExcelHelper.GetBalanceDailyWithoutTime(BalancesDailyWithTime);
        
        SortedMap<LocalDateTime, Double> benchmarkValues = benchmark.benchmarkValues;
        // Calc SharpRatio if simulator series is larger than 1 year 
        long tpDays = java.time.Duration.between(startDate, endDate).toDays();
        SortedMap<LocalDateTime, Double> sharpRatioSortedList = null;
        SortedMap<LocalDateTime, Double> benchmarkSharpRatioSortedList = null;
        
        if (tpDays > _sharpeRatioTimeSpan) {
            sharpRatioSortedList = new TreeMap<>();
            benchmarkSharpRatioSortedList = new TreeMap<>();
            double lastSharpRatio = Double.NaN;
            double lastBenchmarkSharpRatio = Double.NaN;
            LocalDateTime curDate = startDate.plusDays(_sharpeRatioTimeSpan);
            
            while (endDate.compareTo(curDate) > 0) {
                long tp = java.time.Duration.between(startDate, curDate).toDays();
                double tmpYearInterval = _sharpeRatioTimeSpan / 365.25;
                double tmpStartBalance = Double.NaN;
                
                double[] tmpStartBalanceRef = new double[] { tmpStartBalance };
                SortedMap<LocalDateTime, Double> tmpBalanceList = GetSharpBalanceList(BalancesDaily, curDate, tmpStartBalanceRef);
                tmpStartBalance = tmpStartBalanceRef[0];
                
                if (tmpBalanceList.size() > 0) {
                    Double[] values = tmpBalanceList.values().toArray(new Double[0]);
                    double tmpStrategyReturn = values[values.length - 1] / tmpStartBalance - 1;
                    double tmpSharpRatio = CalculateSharpRatio(tmpBalanceList, tmpStrategyReturn, tmpYearInterval);
                    
                    if (tmpSharpRatio == 0.0 && IsBusinessDay(curDate)) {
                        tmpSharpRatio = lastSharpRatio;
                    }
                    
                    sharpRatioSortedList.put(curDate, tmpSharpRatio);
                    lastSharpRatio = tmpSharpRatio;
                }
                
                double[] tmpBenchmarkStartBalanceRef = new double[] { tmpStartBalance };
                SortedMap<LocalDateTime, Double> tmpBenchmarkList = GetSharpBalanceList(benchmarkValues, curDate, tmpBenchmarkStartBalanceRef);
                tmpStartBalance = tmpBenchmarkStartBalanceRef[0];
                
                if (tmpBenchmarkList.size() > 0) {
                    Double[] values = tmpBenchmarkList.values().toArray(new Double[0]);
                    double tmpBenchmarkReturn = values[values.length - 1] / tmpStartBalance - 1;
                    double tmpBenchmarkSharpRatio = CalculateSharpRatio(tmpBenchmarkList, tmpBenchmarkReturn, tmpYearInterval);
                    benchmarkSharpRatioSortedList.put(curDate, tmpBenchmarkSharpRatio);
                    
                    if (tmpBenchmarkSharpRatio == 0.0 && IsBusinessDay(curDate)) {
                        tmpBenchmarkSharpRatio = lastSharpRatio;
                    }
                    
                    lastBenchmarkSharpRatio = tmpBenchmarkSharpRatio;
                }
                
                curDate = curDate.plusDays(1);
            }
        }
        
        SortedMap<LocalDateTime, Double> averageSharpRatioSortedList = new TreeMap<>();
        SortedMap<LocalDateTime, Double> averageBenchmarkSharpRatioSortedList = new TreeMap<>();
        int averageCount = 30;
        averageSharpRatioSortedList = GetAverageSortedList(averageCount, sharpRatioSortedList);
        averageBenchmarkSharpRatioSortedList = GetAverageSortedList(averageCount, benchmarkSharpRatioSortedList);
        
        double benchmarkSharpRatio = CalculateSharpRatio(benchmarkValues, benchmark.getBenchmarkReturn(), yearInterval);
        
        benchmark.SharpRatio = benchmarkSharpRatio;
        
        // 使用包装类来模拟C#的out参数
        Entry<Double, Integer> bestOneYearReturn = null;
        Entry<Double, Integer> worstOneYearReturn = null;
        
        Holder<Entry<Double, Integer>> bestOneYearReturnHolder = new Holder<>();
        Holder<Entry<Double, Integer>> worstOneYearReturnHolder = new Holder<>();
        
        boolean calcMaxReturn = CalculateMaxReturn(BalancesDaily, 1, bestOneYearReturnHolder, worstOneYearReturnHolder);
        bestOneYearReturn = bestOneYearReturnHolder.value;
        worstOneYearReturn = worstOneYearReturnHolder.value;
        
        if (!calcMaxReturn) {
            // 出错
        }
        
        Entry<Double, Integer> bestThreeYearReturn = null;
        Entry<Double, Integer> worstThreeYearReturn = null;
        
        Holder<Entry<Double, Integer>> bestThreeYearReturnHolder = new Holder<>();
        Holder<Entry<Double, Integer>> worstThreeYearReturnHolder = new Holder<>();
        
        calcMaxReturn = CalculateMaxReturn(BalancesDaily, 3, bestThreeYearReturnHolder, worstThreeYearReturnHolder);
        bestThreeYearReturn = bestThreeYearReturnHolder.value;
        worstThreeYearReturn = worstThreeYearReturnHolder.value;
        
        if (!calcMaxReturn) {
            // 出错
        }
        
        // BestWorstMovingWindow
        SortedMap<LocalDateTime, Double> monthlyBalance = CalculateMonthlyBalance(BalancesDaily);
        
        Entry<Double, LocalDateTime> best3MonthReturn = null;
        Entry<Double, LocalDateTime> worst3MonthReturn = null;
        Holder<Entry<Double, LocalDateTime>> best3MonthReturnHolder = new Holder<>();
        Holder<Entry<Double, LocalDateTime>> worst3MonthReturnHolder = new Holder<>();
        CalculateMaxMonthlyReturn(monthlyBalance, 3, best3MonthReturnHolder, worst3MonthReturnHolder);
        best3MonthReturn = best3MonthReturnHolder.value;
        worst3MonthReturn = worst3MonthReturnHolder.value;
        
        Entry<Double, LocalDateTime> best1YearReturn = null;
        Entry<Double, LocalDateTime> worst1YearReturn = null;
        Holder<Entry<Double, LocalDateTime>> best1YearReturnHolder = new Holder<>();
        Holder<Entry<Double, LocalDateTime>> worst1YearReturnHolder = new Holder<>();
        CalculateMaxMonthlyReturn(monthlyBalance, 12, best1YearReturnHolder, worst1YearReturnHolder);
        best1YearReturn = best1YearReturnHolder.value;
        worst1YearReturn = worst1YearReturnHolder.value;
        
        Entry<Double, LocalDateTime> best3YearReturn = null;
        Entry<Double, LocalDateTime> worst3YearReturn = null;
        Holder<Entry<Double, LocalDateTime>> best3YearReturnHolder = new Holder<>();
        Holder<Entry<Double, LocalDateTime>> worst3YearReturnHolder = new Holder<>();
        CalculateMaxMonthlyReturn(monthlyBalance, 36, best3YearReturnHolder, worst3YearReturnHolder);
        best3YearReturn = best3YearReturnHolder.value;
        worst3YearReturn = worst3YearReturnHolder.value;
        
        // CalculateMax(benchmark, startDate, endDate);
        // CalculateSharpRatio(benchmark, yearInterval);
        // CalculateMaxByTime(benchmark, best3MonthReturn, worst3MonthReturn, best1YearReturn, worst1YearReturn, best3YearReturn, worst3YearReturn);
        
        for (Benchmark b : benchmarkList) {
            CalculateMax(b, startDate, endDate);
            CalculateMaxByTime(b, best3MonthReturn, worst3MonthReturn, best1YearReturn, worst1YearReturn, best3YearReturn, worst3YearReturn);
            CalculateSharpRatio(b, yearInterval);
        }
        
        // output to portfolio.Summary
        
        benchmark.setBenchmarkReturn(benchmark.getBenchmarkReturn());
        
        GetBestWorst(summary.Best3MonthReturn, best3MonthReturn, monthlyBalance, 3);
        GetBestWorst(summary.Worst3MonthReturn, worst3MonthReturn, monthlyBalance, 3);
        GetBestWorst(summary.Best1YearReturn, best1YearReturn, monthlyBalance, 12);
        GetBestWorst(summary.Worst1YearReturn, worst1YearReturn, monthlyBalance, 12);
        GetBestWorst(summary.Best3YearReturn, best3YearReturn, monthlyBalance, 36);
        GetBestWorst(summary.Worst3YearReturn, worst3YearReturn, monthlyBalance, 36);
        
        summary.sharpRatioSortedList = averageSharpRatioSortedList;
        summary.benchmarkSharpRatioSortedList = averageBenchmarkSharpRatioSortedList;
    }
    
    /**
     * 计算核心摘要
     * 对应C#: public static void CalculateCoreSummary(QPortfolioBase qPortfolioBase, 
     *          SortedList<DateTime, double> balancesDailyWithouTime, Benchmark benchmark)
     */
    public static void CalculateCoreSummary(QPortfolioBase qPortfolioBase, 
                                           SortedMap<LocalDateTime, Double> balancesDailyWithoutTime, 
                                           Benchmark benchmark) {
        SortedMap<LocalDateTime, Double> lnReturnList = benchmark.getReturns();
        double yearInterval = Calculator.GetYearInterval(qPortfolioBase);
        double InitialCash = qPortfolioBase.InitialCash;
        LocalDateTime startDate = qPortfolioBase._config.BackTestingStart;
        LocalDateTime endDate = qPortfolioBase._config.BackTestingEnd;
        QPortfolioSummary summary = qPortfolioBase.Summary;
        // TODO
        LnOnBalanceList LnOnBalances = CalculateBalances(balancesDailyWithoutTime);
        summary.Beta = CalculateBeta(lnReturnList, LnOnBalances);
        Map<LocalDateTime, Double> balances = balancesDailyWithoutTime;
        double strategyReturn = 0;
        double strateReturnOutOfSample = 0;
        double sharpRatio = 0;
        
        if (balancesDailyWithoutTime.size() > 0) {
            LocalDateTime[] keys = balancesDailyWithoutTime.keySet().toArray(new LocalDateTime[0]);
            strategyReturn = balancesDailyWithoutTime.get(keys[keys.length - 1]) / qPortfolioBase.InitialCash - 1;
            strateReturnOutOfSample = CalculatePartStrategyReturn(balances, startDate, endDate);
            sharpRatio = CalculateSharpRatio(balancesDailyWithoutTime, strategyReturn, yearInterval);
        }
        // TODO 
        
        double yearPortfolioRatio = CalculateYearPortfolioRatio(strategyReturn + 1, yearInterval);
        
        // TODO
        summary.StrategyReturn = strategyReturn;
        GetMaxDrawDown(balances, summary.AccountMaxDrawnDown);
        summary.StrategyReturnOutofSample = strateReturnOutOfSample;
        summary.YearInterval = yearInterval;
        summary.YearPortfolioRatio = yearPortfolioRatio;
        // TODO
        // TODO
        summary.SharpRatio = sharpRatio;
        summary.AnnualizedReturn = yearPortfolioRatio;
        // TODO
        
        // TODO
        
        SortedMap<LocalDateTime, Double> exposures = qPortfolioBase.Exposures;
        
        qPortfolioBase.Summary.YearInterval = yearInterval;
        
        LocalDateTime timeStart = summary.BeginDate;
        LocalDateTime timeEnd = summary.EndDate;
        
        if (exposures.size() > 0) {
            LocalDateTime[] expKeys = exposures.keySet().toArray(new LocalDateTime[0]);
            timeStart = expKeys[0];
            timeEnd = expKeys[expKeys.length - 1];
        }
        yearInterval = qPortfolioBase.Summary.YearInterval;
        double averageExposure = AverageExposure(exposures);
        // TODO
        
        // TODO
        qPortfolioBase.Summary.delta = CaculateMaxDelta(qPortfolioBase);
        qPortfolioBase.Summary.theta = CaculateMaxTheta(qPortfolioBase);
        qPortfolioBase.Summary.vega = CaculateMaxVega(qPortfolioBase);
        qPortfolioBase.Summary.margins = CaculateMaxMargins(qPortfolioBase);
        
        double alpha = qPortfolioBase.Summary.StrategyReturn - (Math.pow((qPortfolioBase.Benchmark.BenchmarkEndValue / qPortfolioBase.Benchmark.BenchmarkBeginValue), averageExposure) - 1);
        double annualizeAlpha = CalculateYearPortfolioRatio(alpha + 1, yearInterval);
        
        // output to portfolio.Summary
        
        GetMaxDrawDown(balances, qPortfolioBase.Summary.AccountMaxDrawnDown);
        qPortfolioBase.Summary.AverageExposure = averageExposure;
        qPortfolioBase.Summary.Alpha = alpha;
        qPortfolioBase.Summary.AnnualizeAlpha = annualizeAlpha;
        // TODO
    }
    
    // 辅助类，用于模拟C#的ref和out参数
    public static class Holder<T> {
        public T value;
    }
    
    public static void GetBenchmarkBeginEnd(Benchmark benchmark) {
        benchmark.BenchmarkBeginValue = benchmark.benchmarkValues.get(benchmark.benchmarkValues.firstKey());
        benchmark.BenchmarkEndValue = benchmark.benchmarkValues.get(benchmark.benchmarkValues.lastKey());
    }
    
    static SortedMap<LocalDateTime, Double> GetAverageSortedList(int averageCount, SortedMap<LocalDateTime, Double> valueList) {
        SortedMap<LocalDateTime, Double> averageSortedList = new TreeMap<>();
        if (valueList != null && valueList.size() > averageCount) {
            LocalDateTime[] keys = valueList.keySet().toArray(new LocalDateTime[0]);
            Double[] values = valueList.values().toArray(new Double[0]);
            
            for (int indx = 0; indx <= (valueList.size() - averageCount); ++indx) {
                double average = GetAverageValue(valueList, indx, averageCount);
                averageSortedList.put(keys[indx + averageCount - 1], average);
            }
        }
        
        return averageSortedList;
    }
    
    public static double GetAverageValue(SortedMap<LocalDateTime, Double> valueList, int startIndex, int count) {
        double average = 0;
        double sum = 0;
        Double[] values = valueList.values().toArray(new Double[0]);
        for (int indx = startIndex; indx <= (startIndex + count - 1); ++indx) {
            sum += values[indx];
        }
        average = sum * 1.0 / count;
        return average;
    }
    
    static void GetBestWorst(ReturnWithTimeSpan returnResult, Entry<Double, LocalDateTime> ReturnWithTimeSpan, 
                                     SortedMap<LocalDateTime, Double> monthlySP500Adj, int monthInterval) {
        returnResult.beginTime = ReturnWithTimeSpan.getValue();
        returnResult.endTime = ReturnWithTimeSpan.getValue().plusMonths(monthInterval - 1);
        returnResult.value = ReturnWithTimeSpan.getKey();
    }
    
    public static void CalculateSharpRatio(Benchmark benchmark, double yearInterval) {
        benchmark.SharpRatio = CalculateSharpRatio(benchmark.benchmarkValues, benchmark.getBenchmarkReturn(), yearInterval);
    }
    
    public static boolean IsBusinessDay(LocalDateTime date) {
        // QLNet.UnitedStates unitedStates = new QLNet.UnitedStates();
        // QLNet.Date curDate = new QLNet.Date(date);
        // boolean isBusinessDay = unitedStates.isBusinessDay(curDate);
        // return isBusinessDay;
        
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        
        // 周末不是工作日
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }
        
        // TODO: 如果需要更精确的美国节假日判断，可以集成QLNet的Java版本或其他日历库
        return true;
    }
    
    public static SortedMap<LocalDateTime, Double> GetReturns(Benchmark benchmark) {
        SortedMap<LocalDateTime, Double> lnReturnList = new TreeMap<>();
        int idx = 0;
        LocalDateTime lastDay = LocalDateTime.MIN;
        for (Entry<LocalDateTime, Double> item : benchmark.benchmarkValues.entrySet()) {
            if (idx > 0) {
                double value = (item.getValue() / benchmark.benchmarkValues.get(lastDay)) - 1;
                lnReturnList.put(item.getKey(), value);
            }
            idx++;
            lastDay = item.getKey();
        }
        return lnReturnList;
    }
    
    public static void SummarizeAllData(QPortfolioBase portfolio) {
        // GenerateOrderSymbolDictionary(portfolio);
        // SummarizeCompleteOrderDictionary(portfolio);
        // SummarizePositionExHistory(portfolio);
        SummarizeReturnDistribution(portfolio);
        summarizeTradeGroupDistribution(portfolio);
        // CalcConsecutiveResults(portfolio);
        CopyDataToSummary(portfolio);
    }
    
    public static void CopyDataToSummary(QPortfolioBase portfolio) {
        portfolio.Summary.OrderNumber = portfolio.Summary.TradeGroupReturnDistribution.OrderNumber;
        portfolio.Summary.NumberOfOrderWin = portfolio.Summary.TradeGroupReturnDistribution.NumberOfOrderWin;
        portfolio.Summary.NumberOfOrderLose = portfolio.Summary.TradeGroupReturnDistribution.NumberOfOrderLose;
        portfolio.Summary.WinRatio = portfolio.Summary.TradeGroupReturnDistribution.WinRatio;
        
        portfolio.Summary.WinLossRangeLower = portfolio.Summary.TradeGroupReturnDistribution.WinLossRangeLower;
        portfolio.Summary.WinLossRangeUpper = portfolio.Summary.TradeGroupReturnDistribution.WinLossRangeUpper;
        portfolio.Summary.WinLossRangeAverage = portfolio.Summary.TradeGroupReturnDistribution.WinLossRangeAverage;
    }
    
    public static void CalculateSummary(QPortfolioBase portfolio) {
        // portfolio.Summary 
        
        SortedMap<LocalDateTime, Double> balances = GetBalanceDailyWithoutTime(portfolio.BalancesDaily);
        
        // portfolio.Summary.GrossLoss = CaculateOrderGrossLoss(portfolio.CompleteTradeList);
        // portfolio.Summary.GrossProfit = CaculateOrderGrossProfit(portfolio.CompleteTradeList);
        portfolio.Summary.GrossLoss = CaculateOrderGrossLoss(portfolio.TradeGroupList);
        portfolio.Summary.GrossProfit = CaculateOrderGrossProfit(portfolio.TradeGroupList);
        portfolio.Summary.ProfitFactor = Math.abs(portfolio.Summary.GrossLoss) > 1e-6 ? portfolio.Summary.GrossProfit / Math.abs(portfolio.Summary.GrossLoss) : 0;
        portfolio.Summary.TotalNetProfit = portfolio.BalancesDaily.lastEntry().getValue() - portfolio.BalancesHiFrequency.firstEntry().getValue();
        portfolio.Summary.InitialBalance = portfolio.InitialCash;
        portfolio.Summary.DataPointTested = portfolio.RiskMeasurementList.size();
        
        portfolio.Summary.NetNorminalExposure = CaculateMaxNetNorminalExposure(portfolio);
        portfolio.Summary.MaxGrossExposure = CaculateMaxGrossExposure(portfolio);
        // 20200202新增
        
        double strateReturnInsampleStart = 0;
        double strateReturnOutOfSample = 0;
        
        portfolio.Summary.StrategyReturnInSampleStart = strateReturnInsampleStart;
        portfolio.Summary.StrategyReturnOutofSample = strateReturnOutOfSample;
        
        portfolio.Benchmark.setBenchmarkReturn(portfolio.Benchmark.getBenchmarkReturn());
        
        portfolio.Summary.StrategyReturnInsampleStart = strateReturnInsampleStart;
        portfolio.Summary.StrategyReturnOutOfSample = strateReturnOutOfSample;
    }
    
    private static double CaculateMaxMargins(QPortfolioBase QPortfolioBaseInfo) {
        double maxMargin = 0;
        double marginPerMin = 0;
        SortedMap<LocalDateTime, RiskMeasurement> riskMeasurementList = QPortfolioBaseInfo.RiskMeasurementList;
        for (Entry<LocalDateTime, RiskMeasurement> riskMeasurementPair : riskMeasurementList.entrySet()) {
            
            LocalDateTime curDate = riskMeasurementPair.getKey();
            RiskMeasurement riskMeasurement = riskMeasurementPair.getValue();
            
            double totalMargin = riskMeasurement.margin;
            double balance = riskMeasurement.balance;
            if (balance != 0) {
                marginPerMin = totalMargin / balance;
                if (Math.abs(marginPerMin) > Math.abs(maxMargin)) {
                    maxMargin = marginPerMin;
                }
            }
        }
        return maxMargin;
    }
    
    private static double CaculateMaxVega(QPortfolioBase QPortfolioBaseInfo) {
        double maxVega = 0;
        double vegaPerMin = 0;
        SortedMap<LocalDateTime, RiskMeasurement> riskMeasurementList = QPortfolioBaseInfo.RiskMeasurementList;
        for (Entry<LocalDateTime, RiskMeasurement> riskMeasurementPair : riskMeasurementList.entrySet()) {
            
            LocalDateTime curDate = riskMeasurementPair.getKey();
            RiskMeasurement riskMeasurement = riskMeasurementPair.getValue();
            
            double totalVegaDollar = riskMeasurement.vega;
            double balance = riskMeasurement.balance;
            if (balance != 0) {
                vegaPerMin = totalVegaDollar / balance;
                if (Math.abs(vegaPerMin) > Math.abs(maxVega)) {
                    maxVega = vegaPerMin;
                }
            }
        }
        return maxVega;
    }
    
    private static double CaculateMaxTheta(QPortfolioBase QPortfolioBaseInfo) {
        double maxTheta = 0;
        double thetaPerMin = 0;
        SortedMap<LocalDateTime, RiskMeasurement> riskMeasurementList = QPortfolioBaseInfo.RiskMeasurementList;
        for (Entry<LocalDateTime, RiskMeasurement> riskMeasurementPair : riskMeasurementList.entrySet()) {
            
            LocalDateTime curDate = riskMeasurementPair.getKey();
            RiskMeasurement riskMeasurement = riskMeasurementPair.getValue();
            
            double totalTheta = riskMeasurement.theta;
            double balance = riskMeasurement.balance;
            if (balance != 0) {
                thetaPerMin = totalTheta / balance;
                if (Math.abs(thetaPerMin) > Math.abs(maxTheta)) {
                    maxTheta = thetaPerMin;
                }
            }
        }
        
        return maxTheta;
    }
    
    private static double CaculateMaxDelta(QPortfolioBase QPortfolioBaseInfo) {
        double maxDelta = 0;
        double deltaPerMin = 0;
        SortedMap<LocalDateTime, RiskMeasurement> riskMeasurementList = QPortfolioBaseInfo.RiskMeasurementList;
        for (Entry<LocalDateTime, RiskMeasurement> riskMeasurementPair : riskMeasurementList.entrySet()) {
            
            LocalDateTime curDate = riskMeasurementPair.getKey();
            RiskMeasurement riskMeasurement = riskMeasurementPair.getValue();
            
            double totalDelta = riskMeasurement.deltaDollar;
            double balance = riskMeasurement.balance;
            if (balance != 0) {
                deltaPerMin = totalDelta / balance;
                if (Math.abs(deltaPerMin) > Math.abs(maxDelta)) {
                    maxDelta = deltaPerMin;
                }
            }
        }
        
        return maxDelta;
    }
    
    public static double GetYearInterval(QPortfolioBase portfolio) {
        SortedMap<LocalDateTime, Double> exposures = portfolio.Exposures;
        LocalDateTime timeStart = portfolio.Summary.BeginDate;
        LocalDateTime timeEnd = portfolio.Summary.EndDate;
        
        if (exposures.size() > 0) {
            LocalDateTime[] keys = exposures.keySet().toArray(new LocalDateTime[0]);
            timeStart = keys[0];
            timeEnd = keys[keys.length - 1];
        }
        
        int year = 0;
        while (timeEnd.isAfter(timeStart.plusYears(1))) {
            year++;
            timeStart = timeStart.plusYears(1);
        }
        long tp = java.time.Duration.between(timeStart, timeEnd).toDays();
        double totalDays = java.time.Duration.between(timeStart, timeStart.plusYears(1).minusDays(1)).toDays();
        double yearInterval = year + tp / totalDays;
        return yearInterval;
    }
    
    public static LnOnBalanceList CalculateBalances(Map<LocalDateTime, Double> balances) {
        LnOnBalanceList balanceLnReturnList = new LnOnBalanceList();
        
        double formerBalance = 0;
        boolean isFirstValue = true;
        for (LocalDateTime dt : balances.keySet()) {
            double balance = balances.get(dt);
            
            if (isFirstValue) {
                isFirstValue = false;
                balanceLnReturnList.put(dt, Double.NaN);
            } else {
                double balanceLnReturn = Calculations.Calculate_LN(balance, formerBalance);
                balanceLnReturnList.put(dt, balanceLnReturn);
            }
            formerBalance = balance;
        }
        
        return balanceLnReturnList;
    }
    
    public static SortedMap<LocalDateTime, Double> GetSharpBalanceList(SortedMap<LocalDateTime, Double> balances, 
                                                                      LocalDateTime date, double[] startBalance) {
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
    
    public static double CalculateBeta(SortedMap<LocalDateTime, Double> lnReturnList, LnOnBalanceList LnOnBalances) {
        List<Double> balanceReturnValueList = new ArrayList<>();
        List<Double> returnValueList = new ArrayList<>();
        
        for (LocalDateTime date : LnOnBalances.keySet()) {
            try {
                LocalDateTime key = date; // .AddHours(21);
                
                if (lnReturnList.containsKey(key)) {
                    balanceReturnValueList.add(LnOnBalances.get(date));
                    returnValueList.add(lnReturnList.get(key));
                } else {
                    if (lnReturnList.containsKey(key.minusDays(1))) {
                        balanceReturnValueList.add(LnOnBalances.get(key.minusDays(1)));
                        returnValueList.add(lnReturnList.get(key.minusDays(1)));
                    }
                }
            } catch (Exception ex) {
                // Log.Debug(ex.Message);
            }
        }
        
        double varValue = Calculations.CalculateVAR(returnValueList);
        double covarValue = Calculations.CalculateCOVAR(returnValueList, balanceReturnValueList);
        
        double beta = covarValue / varValue;
        return beta;
    }
    
    public static double CalculateSharpRatio(SortedMap<LocalDateTime, Double> balanceList, double strategyReturn, double yearInterval) {
        SortedMap<LocalDateTime, Double> balanceList2 = new TreeMap<>();
        for (LocalDateTime dt3 : balanceList.keySet()) {
            balanceList2.put(dt3.toLocalDate().atStartOfDay(), balanceList.get(dt3));
        }
        
        Queue<Double> valueQueue = new LinkedList<>();
        SortedMap<LocalDateTime, Double> lnReturnList = new TreeMap<>();
        
        if (balanceList2 != null && balanceList2.size() > 0) {
            LocalDateTime dt = balanceList2.firstKey();
            LocalDateTime prevDT = dt;
            LocalDateTime lastKey = balanceList2.lastKey();
            while (dt.compareTo(lastKey) < 0) {
                prevDT = dt;
                dt = dt.plusDays(1);
                while (!balanceList2.containsKey(dt)) {
                    try {
                        dt = dt.plusDays(1);
                    } catch (Exception e) {
                        String a = e.getStackTrace().toString();
                    }
                }
                double lnreturnClose = Calculations.Calculate_LN(balanceList2.get(dt), balanceList2.get(prevDT));
                lnReturnList.put(dt, lnreturnClose);
            }
        }
        
        for (double value : lnReturnList.values()) {
            if (!Double.isNaN(value)) {
                valueQueue.add(value);
            }
        }
        
        // 如果没有足够的数据点，返回 0
        if (valueQueue.size() < 2) {
            return 0.0;
        }
        
        double volatility = Calculations.Calculate_Volatility(valueQueue);
        
        // 如果 volatility 是 NaN 或 0，返回 0
        if (Double.isNaN(volatility) || volatility == 0.0) {
            return 0.0;
        }
        
        double annualReturn = Math.pow(strategyReturn + 1, 1 / yearInterval) - 1;
        double annualExcessReturn = 0;
        if (annualReturn != 0)
            annualExcessReturn = annualReturn - 0.00529; // 0.0529 = average fed fund rate from 1954 to today. 
        // double annualExcessReturn = annualReturn - 0.00529; // 0.0529 = average fed fund rate from 1954 to today. 
        double annualVol = volatility * Math.pow(252, 0.5);
        
        if (Double.isNaN(annualVol) || annualVol <= 0.0)
            return 0;
        return annualExcessReturn / annualVol;
        // return Math.pow(strategyReturn / volatility, 1 / yearInterval);
    }
    
    public static double CalculateYearPortfolioRatio(double strategyReturn, double years) {
        return Math.pow(strategyReturn, 1.0 / years) - 1;
    }
    
    public static double CalculatePartStrategyReturn(Map<LocalDateTime, Double> BalancesDaily, LocalDateTime startDate, LocalDateTime endDate) {
        double startBalance = 0;
        double endBalance = 0;
        
        // 找到大于等于开始日期的第一个余额
        for (Entry<LocalDateTime, Double> entry : BalancesDaily.entrySet()) {
            if (entry.getKey().compareTo(startDate) >= 0) {
                startBalance = entry.getValue();
                break;
            }
        }
        
        // 找到小于等于结束日期的最后一个余额
        for (Entry<LocalDateTime, Double> entry : BalancesDaily.entrySet()) {
            if (entry.getKey().compareTo(endDate) <= 0) {
                endBalance = entry.getValue();
            }
        }
        
        return endBalance / startBalance - 1;
    }
    
    public static boolean CalculateMaxReturn(Map<LocalDateTime, Double> BalancesDaily, int yearInterval, 
                                           Holder<Entry<Double, Integer>> bestReturn, 
                                           Holder<Entry<Double, Integer>> worstReturn) {
        if (BalancesDaily.isEmpty())
            throw new RuntimeException("Balance should contain at least one data point!!");
        
        SortedMap<Double, Integer> returnList = new TreeMap<>();
        
        LocalDateTime[] dates = BalancesDaily.keySet().toArray(new LocalDateTime[0]);
        LocalDateTime firstDate = dates[0];
        LocalDateTime lastDate = dates[dates.length - 1];
        
        if (lastDate.compareTo(firstDate.plusYears(yearInterval - 1)) < 0) {
            returnList.put(Double.NaN, Integer.MIN_VALUE);
            bestReturn.value = new SimpleEntry<>(returnList.firstKey(), returnList.get(returnList.firstKey()));
            worstReturn.value = bestReturn.value;
            return false;
        }
        
        boolean isFirstData = true;
        int currentYear = 0;
        
        double yearStartBalance = 0;
        double yearEndBalance = 0;
        double formerBalance = 0;
        for (LocalDateTime currentDate : BalancesDaily.keySet()) {
            if (isFirstData) {
                isFirstData = false;
                currentYear = currentDate.getYear();
                yearStartBalance = BalancesDaily.get(currentDate);
                formerBalance = yearStartBalance;
            }
            
            // 一年结束
            if (currentYear + yearInterval - 1 < currentDate.getYear()) {
                yearEndBalance = formerBalance;
                double yearReturn = yearEndBalance / yearStartBalance - 1;
                
                if (!returnList.containsKey(yearReturn))
                    returnList.put(yearReturn, currentYear);
                
                currentYear = currentDate.getYear();
                yearStartBalance = BalancesDaily.get(currentDate);
            }
            
            formerBalance = BalancesDaily.get(currentDate);
        }
        
        if (returnList.size() > 0) {
            Double[] returns = returnList.keySet().toArray(new Double[0]);
            bestReturn.value = new SimpleEntry<>(returns[returns.length - 1], returnList.get(returns[returns.length - 1]));
            worstReturn.value = new SimpleEntry<>(returns[0], returnList.get(returns[0]));
            return true;
        }
        returnList.put(Double.NaN, Integer.MIN_VALUE);
        bestReturn.value = new SimpleEntry<>(returnList.firstKey(), returnList.get(returnList.firstKey()));
        worstReturn.value = bestReturn.value;
        return false;
    }
    
    /**
     * Converts a map with LocalDateTime keys to use only date portions (at midnight).
     * WARNING: If multiple entries exist for the same date with different times,
     * only the last entry will be preserved in the result map.
     * Consider using date-based comparison logic instead if you need to preserve all entries.
     */
    public static SortedMap<LocalDateTime, Double> GetBalanceDailyWithoutTime(SortedMap<LocalDateTime, Double> BalancesDailyWithTime) {
        SortedMap<LocalDateTime, Double> balancesDaily = new TreeMap<>();
        for (LocalDateTime dt3 : BalancesDailyWithTime.keySet()) {
            balancesDaily.put(dt3.toLocalDate().atStartOfDay(), BalancesDailyWithTime.get(dt3));
        }
        return balancesDaily;
    }
    
    public static boolean CalculateMaxMonthlyReturn(Map<LocalDateTime, Double> Balances, int monthInterval, 
                                           Holder<Entry<Double, LocalDateTime>> bestReturn, 
                                           Holder<Entry<Double, LocalDateTime>> worstReturn) {
        return CalculateMaxMonthlyReturn(Balances, monthInterval, bestReturn, worstReturn, null, null);
    }
    
    public static boolean CalculateMaxMonthlyReturn(Map<LocalDateTime, Double> Balances, int monthInterval, 
                                           Holder<Entry<Double, LocalDateTime>> bestReturn, 
                                           Holder<Entry<Double, LocalDateTime>> worstReturn, 
                                           LocalDateTime begin, LocalDateTime end) {
        LocalDateTime beginDate = begin != null ? begin : LocalDateTime.MIN;
        LocalDateTime endDate = end != null ? end : LocalDateTime.MAX;
        
        SortedMap<Double, LocalDateTime> returnList = new TreeMap<>();
        
        LocalDateTime[] dates = Balances.keySet().toArray(new LocalDateTime[0]);
        Double[] values = Balances.values().toArray(new Double[0]);
        
        if (dates.length == 0 || dates[dates.length - 1].compareTo(dates[0].plusMonths(monthInterval - 1)) < 0) {
            returnList.put(Double.NaN, LocalDateTime.MIN);
            bestReturn.value = new SimpleEntry<>(returnList.firstKey(), returnList.get(returnList.firstKey()));
            worstReturn.value = bestReturn.value;
            return false;
        }
        
        for (int i = 0; i < dates.length - monthInterval; ++i) {
            if (dates[i].compareTo(beginDate) >= 0 && dates[i].compareTo(endDate) <= 0
                && dates[i + monthInterval].compareTo(beginDate) >= 0 && dates[i + monthInterval].compareTo(endDate) <= 0) {
                double periodReturn = values[i + monthInterval] / values[i] - 1;
                if (!returnList.containsKey(periodReturn)) {
                    returnList.put(periodReturn, dates[i]);
                }
            }
        }
        
        if (returnList.size() > 0) {
            Double[] returns = returnList.keySet().toArray(new Double[0]);
            bestReturn.value = new SimpleEntry<>(returns[returns.length - 1], returnList.get(returns[returns.length - 1]));
            worstReturn.value = new SimpleEntry<>(returns[0], returnList.get(returns[0]));
            return true;
        }
        returnList.put(Double.NaN, LocalDateTime.MIN);
        bestReturn.value = new SimpleEntry<>(returnList.firstKey(), returnList.get(returnList.firstKey()));
        worstReturn.value = bestReturn.value;
        return false;
    }
    
    public static SortedMap<LocalDateTime, Double> CalculateMonthlyBalance(SortedMap<LocalDateTime, Double> BalancesDailyWithTime) {
        SortedMap<LocalDateTime, Double> Balances = GetBalanceDailyWithoutTime(BalancesDailyWithTime);
        
        SortedMap<LocalDateTime, Double> monthlyBalances = new TreeMap<>();
        try {
            LocalDateTime[] dates = Balances.keySet().toArray(new LocalDateTime[0]);
            Double[] values = Balances.values().toArray(new Double[0]);
            
            LocalDateTime firstDatetime = LocalDateTime.of(dates[0].getYear(), dates[0].getMonthValue(), 1, 0, 0);
            monthlyBalances.put(firstDatetime, values[0]);
            
            int totalDays = Balances.size();
            for (int i = 1; i < totalDays; ++i) {
                if (dates[i - 1].getMonthValue() != dates[i].getMonthValue()) {
                    LocalDateTime tmpDatetime = LocalDateTime.of(dates[i].getYear(), dates[i].getMonthValue(), 1, 0, 0);
                    monthlyBalances.put(tmpDatetime, values[i - 1]);
                }
            }
            int year = dates[totalDays - 1].getYear();
            int month = dates[totalDays - 1].getMonthValue() + 1;
            if (month == 13) {
                month = 1;
                year++;
            }
            LocalDateTime lastDatetime = LocalDateTime.of(year, month, 1, 0, 0);
            monthlyBalances.put(lastDatetime, values[totalDays - 1]);
        } catch (Exception ex) {
            Log.error(ex.toString());
        }
        return monthlyBalances;
    }
    
    public static double AverageExposure(SortedMap<LocalDateTime, Double> exposures) {
        double exposure = 0;
        
        for (LocalDateTime date : exposures.keySet()) {
            exposure += exposures.get(date);
        }
        return exposure / exposures.size();
    }
    
    // 20200202修改为按照tradeGroup计算
    public static double CaculateOrderGrossLoss(SortedMap<String, TradeGroup> tradeGroupList) {
        double profit = 0;
        
        for (int indx = 0; indx < tradeGroupList.size(); ++indx) {
            TradeGroup tradeGroup = tradeGroupList.values().toArray(new TradeGroup[0])[indx];
            double tmp_profit = tradeGroup.PNL;
            if (tmp_profit < 0) {
                // loss
                profit += tmp_profit;
            }
        }
        return profit;
    }
    
    // 20200202修改为tradeGRoup计算
    public static double CaculateOrderGrossProfit(SortedMap<String, TradeGroup> tradeGroupList) {
        double profit = 0;
        
        for (int indx = 0; indx < tradeGroupList.size(); ++indx) {
            TradeGroup tradeGroup = tradeGroupList.values().toArray(new TradeGroup[0])[indx];
            double tmp_profit = tradeGroup.PNL;
            if (tmp_profit > 0) {
                // profit
                profit += tmp_profit;
            }
        }
        return profit;
    }
    
    private static double CaculateNetNorminalExposure(QPortfolioBase port, LocalDateTime dt) {
        return port.Exposures.get(dt);
    }
    
    public static double CaculateMaxNetNorminalExposure(QPortfolioBase port) {
        double maxNetNorminalExposure = Double.MIN_VALUE;
        LocalDateTime maxDataTime = LocalDateTime.MIN;
        for (LocalDateTime dt : port.Exposures.keySet()) {
            double NetNorminalExposure = CaculateNetNorminalExposure(port, dt);
            if (maxNetNorminalExposure == Double.MIN_VALUE) {
                maxNetNorminalExposure = NetNorminalExposure;
                maxDataTime = dt;
            }
            if (NetNorminalExposure > maxNetNorminalExposure) {
                maxNetNorminalExposure = NetNorminalExposure;
                maxDataTime = dt;
            }
        }
        return maxNetNorminalExposure;
    }
    
    public static double CaculateMaxGrossExposure(QPortfolioBase port) {
        double maxGrossExposure = Double.MIN_VALUE;
        LocalDateTime maxTime = LocalDateTime.MIN;
        for (LocalDateTime dt : port.GrossExposures.keySet()) {
            double grossExposure = CaculateGrossExposure(port, dt);
            if (maxGrossExposure == Double.MIN_VALUE) {
                maxGrossExposure = grossExposure;
                maxTime = dt;
            }
            if (Math.abs(grossExposure) > Math.abs(maxGrossExposure)) {
                maxGrossExposure = grossExposure;
                maxTime = dt;
            }
        }
        return Math.abs(maxGrossExposure);
    }
    
    private static double CaculateGrossExposure(QPortfolioBase port, LocalDateTime dt) {
        return port.GrossExposures.get(dt);
    }
    
    public static void GetMaxDrawDown(Map<LocalDateTime, Double> balances, ReturnWithTimeSpan accountMaxDrawnDown) {
        SortedMap<LocalDateTime, Double> localMaxMin = FindLocalMaxMin(balances);
        SortedMap<LocalDateTime, Double> maxDrawDown = CalculateMaxDrawDown(localMaxMin);
        LocalDateTime[] dates = maxDrawDown.keySet().toArray(new LocalDateTime[0]);
        Double[] values = maxDrawDown.values().toArray(new Double[0]);
        accountMaxDrawnDown.beginTime = dates[0];
        accountMaxDrawnDown.endTime = dates[1];
        accountMaxDrawnDown.value = values[1] / values[0] - 1;
    }
    
    public static SortedMap<LocalDateTime, Double> FindLocalMaxMin(Map<LocalDateTime, Double> Balances) {
        SortedMap<LocalDateTime, Double> localMaxMin = new TreeMap<>();
        LocalDateTime[] dates = Balances.keySet().toArray(new LocalDateTime[0]);
        Double[] values = Balances.values().toArray(new Double[0]);
        
        for (int i = 0; i < dates.length; ++i) {
            if (i == 0 || i == dates.length - 1) {
                localMaxMin.put(dates[i], values[i]);
            } else if ((values[i] >= values[i - 1] && values[i] > values[i + 1]) ||
                      (values[i] < values[i - 1] && values[i] <= values[i + 1])) {
                localMaxMin.put(dates[i], values[i]);
            }
        }
        return localMaxMin;
    }
    
    public static SortedMap<LocalDateTime, Double> CalculateMaxDrawDown(SortedMap<LocalDateTime, Double> localMaxMin) {
        SortedMap<LocalDateTime, Double> maxDrawDownInfo = new TreeMap<>();
        int maxIndex = 0;
        int minIndex = localMaxMin.size() - 1;
        double maxDrawDown = 0;
        
        LocalDateTime[] dates = localMaxMin.keySet().toArray(new LocalDateTime[0]);
        Double[] values = localMaxMin.values().toArray(new Double[0]);
        
        for (int i = 0; i < localMaxMin.size() - 1;) {
            double drawDown = 0;
            int k = i + 1;
            for (int j = i + 1; j <= localMaxMin.size() - 1; ++j) {
                drawDown = 1 - values[j] / values[i];
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
        maxDrawDownInfo.put(dates[maxIndex], values[maxIndex]);
        maxDrawDownInfo.put(dates[minIndex], values[minIndex]);
        return maxDrawDownInfo;
    }
    
    public static void CalculateMax(Benchmark benchmark, LocalDateTime beginDate, LocalDateTime endDate) {
        // SortedList<DateTime, double> benchmarkAdjClose;
        benchmark.monthlyBenchmarkAdj = CalculateMonthlyBalance(benchmark.benchmarkValues);
        if (benchmark.monthlyBenchmarkAdj == null) throw new RuntimeException();
        
        Entry<Double, LocalDateTime> best3MonthBecnchmarkReturn;
        Entry<Double, LocalDateTime> worst3MonthBenchmarkReturn;
        Holder<Entry<Double, LocalDateTime>> best3MonthHolder = new Holder<>();
        Holder<Entry<Double, LocalDateTime>> worst3MonthHolder = new Holder<>();
        CalculateMaxMonthlyReturn(benchmark.monthlyBenchmarkAdj, 3, best3MonthHolder, worst3MonthHolder);
        best3MonthBecnchmarkReturn = best3MonthHolder.value;
        worst3MonthBenchmarkReturn = worst3MonthHolder.value;
        
        Entry<Double, LocalDateTime> best1YearBenchmarkReturn;
        Entry<Double, LocalDateTime> worst1YearBenchmarkReturn;
        Holder<Entry<Double, LocalDateTime>> best1YearHolder = new Holder<>();
        Holder<Entry<Double, LocalDateTime>> worst1YearHolder = new Holder<>();
        CalculateMaxMonthlyReturn(benchmark.monthlyBenchmarkAdj, 12, best1YearHolder, worst1YearHolder);
        best1YearBenchmarkReturn = best1YearHolder.value;
        worst1YearBenchmarkReturn = worst1YearHolder.value;
        
        Entry<Double, LocalDateTime> best3YearBenchmarkReturn;
        Entry<Double, LocalDateTime> worst3YearBenchmarkReturn;
        Holder<Entry<Double, LocalDateTime>> best3YearHolder = new Holder<>();
        Holder<Entry<Double, LocalDateTime>> worst3YearHolder = new Holder<>();
        CalculateMaxMonthlyReturn(benchmark.monthlyBenchmarkAdj, 36, best3YearHolder, worst3YearHolder);
        best3YearBenchmarkReturn = best3YearHolder.value;
        worst3YearBenchmarkReturn = worst3YearHolder.value;
        
        Entry<Double, LocalDateTime> best3MonthBecnchmarkReturnTestingPeriod;
        Entry<Double, LocalDateTime> worst3MonthBenchmarkReturnTestingPeriod;
        Holder<Entry<Double, LocalDateTime>> best3MonthTestHolder = new Holder<>();
        Holder<Entry<Double, LocalDateTime>> worst3MonthTestHolder = new Holder<>();
        CalculateMaxMonthlyReturn(benchmark.monthlyBenchmarkAdj, 3, best3MonthTestHolder, worst3MonthTestHolder, beginDate, endDate);
        best3MonthBecnchmarkReturnTestingPeriod = best3MonthTestHolder.value;
        worst3MonthBenchmarkReturnTestingPeriod = worst3MonthTestHolder.value;
        
        Entry<Double, LocalDateTime> best1YearBenchmarkReturnTestingPeriod;
        Entry<Double, LocalDateTime> worst1YearBenchmarkReturnTestingPeriod;
        Holder<Entry<Double, LocalDateTime>> best1YearTestHolder = new Holder<>();
        Holder<Entry<Double, LocalDateTime>> worst1YearTestHolder = new Holder<>();
        CalculateMaxMonthlyReturn(benchmark.monthlyBenchmarkAdj, 12, best1YearTestHolder, worst1YearTestHolder, beginDate, endDate);
        best1YearBenchmarkReturnTestingPeriod = best1YearTestHolder.value;
        worst1YearBenchmarkReturnTestingPeriod = worst1YearTestHolder.value;
        
        Entry<Double, LocalDateTime> best3YearBenchmarkReturnTestingPeriod;
        Entry<Double, LocalDateTime> worst3YearBenchmarkReturnTestingPeriod;
        Holder<Entry<Double, LocalDateTime>> best3YearTestHolder = new Holder<>();
        Holder<Entry<Double, LocalDateTime>> worst3YearTestHolder = new Holder<>();
        CalculateMaxMonthlyReturn(benchmark.monthlyBenchmarkAdj, 36, best3YearTestHolder, worst3YearTestHolder, beginDate, endDate);
        best3YearBenchmarkReturnTestingPeriod = best3YearTestHolder.value;
        worst3YearBenchmarkReturnTestingPeriod = worst3YearTestHolder.value;
        
        GetBestWorst(benchmark.Best3MonthReturn, best3MonthBecnchmarkReturn, benchmark.monthlyBenchmarkAdj, 3);
        GetBestWorst(benchmark.Worst3MonthReturn, worst3MonthBenchmarkReturn, benchmark.monthlyBenchmarkAdj, 3);
        GetBestWorst(benchmark.Best1YearReturn, best1YearBenchmarkReturn, benchmark.monthlyBenchmarkAdj, 12);
        GetBestWorst(benchmark.Worst1YearReturn, worst1YearBenchmarkReturn, benchmark.monthlyBenchmarkAdj, 12);
        GetBestWorst(benchmark.Best3YearReturn, best3YearBenchmarkReturn, benchmark.monthlyBenchmarkAdj, 36);
        GetBestWorst(benchmark.Worst3YearReturn, worst3YearBenchmarkReturn, benchmark.monthlyBenchmarkAdj, 36);
        
        GetBestWorst(benchmark.Best3MonthReturnTestingPeriod, best3MonthBecnchmarkReturnTestingPeriod, benchmark.monthlyBenchmarkAdj, 3);
        GetBestWorst(benchmark.Worst3MonthReturnTestingPeriod, worst3MonthBenchmarkReturnTestingPeriod, benchmark.monthlyBenchmarkAdj, 3);
        GetBestWorst(benchmark.Best1YearReturnTestingPeriod, best1YearBenchmarkReturnTestingPeriod, benchmark.monthlyBenchmarkAdj, 12);
        GetBestWorst(benchmark.Worst1YearReturnTestingPeriod, worst1YearBenchmarkReturnTestingPeriod, benchmark.monthlyBenchmarkAdj, 12);
        GetBestWorst(benchmark.Best3YearReturnTestingPeriod, best3YearBenchmarkReturnTestingPeriod, benchmark.monthlyBenchmarkAdj, 36);
        GetBestWorst(benchmark.Worst3YearReturnTestingPeriod, worst3YearBenchmarkReturnTestingPeriod, benchmark.monthlyBenchmarkAdj, 36);
    }
    
    public static double CaluculateBenchmarkRetrun(Benchmark benchmark, Entry<Double, LocalDateTime> bestReturnTime, int monthInterval) {
        LocalDateTime beginTime = bestReturnTime.getValue();
        if ((beginTime.compareTo(benchmark.monthlyBenchmarkAdj.firstKey()) > 0) && 
            (benchmark.monthlyBenchmarkAdj.lastKey().compareTo(beginTime) > 0)) {
            LocalDateTime sourceDt = bestReturnTime.getValue();
            LocalDateTime dt = bestReturnTime.getValue().plusMonths(monthInterval);
            if (benchmark.monthlyBenchmarkAdj.containsKey(dt)) {
                double tmp = benchmark.monthlyBenchmarkAdj.get(dt) /
                            benchmark.monthlyBenchmarkAdj.get(sourceDt) - 1;
                return tmp;
            }
        }
        return Double.NaN;
    }
    
    public static void CalculateMaxByTime(Benchmark benchmark, 
                                        Entry<Double, LocalDateTime> best3MonthBecnchmarkReturn, 
                                        Entry<Double, LocalDateTime> worst3MonthBenchmarkReturn,
                                        Entry<Double, LocalDateTime> best1YearBenchmarkReturn, 
                                        Entry<Double, LocalDateTime> worst1YearBenchmarkReturn,
                                        Entry<Double, LocalDateTime> best3YearBenchmarkReturn, 
                                        Entry<Double, LocalDateTime> worst3YearBenchmarkReturn) {
        benchmark.Best3MonthPortfolioReturn = CaluculateBenchmarkRetrun(benchmark, best3MonthBecnchmarkReturn, 3);
        benchmark.Worst3MonthPortfolioReturn = CaluculateBenchmarkRetrun(benchmark, worst3MonthBenchmarkReturn, 3);
        benchmark.Best1YearPortfolioReturn = CaluculateBenchmarkRetrun(benchmark, best1YearBenchmarkReturn, 12);
        benchmark.Worst1YearPortfolioReturn = CaluculateBenchmarkRetrun(benchmark, worst1YearBenchmarkReturn, 12);
        benchmark.Best3YearPortfolioReturn = CaluculateBenchmarkRetrun(benchmark, best3YearBenchmarkReturn, 36);
        benchmark.Worst3YearPortfolioReturn = CaluculateBenchmarkRetrun(benchmark, worst3YearBenchmarkReturn, 36);
    }
    
    public static double CalcProfit(CompleteOrder completeOrder) {
        double profit = 0;
        // TODO: check completetradelist 
        if (/*VerifyCompleteOrder() && */completeOrder.CompleteTradeList.size() >= 2) {
            for (TradeConfirmation Order : completeOrder.CompleteTradeList) {
                profit += Order.GetWorth();
            }
            
        } else {
            Log.warn("Calc Profit Invalid Complete Order " + completeOrder.TradeGroupID.toString());
        }
        return profit;
    }
    
    public static List<TradeConfirmation> GetCompleteOrderSymbolType(CompleteOrder completeOrder, OrderSymbolType type) {
        List<TradeConfirmation> TradeList = new ArrayList<>();
        for (TradeConfirmation Order : completeOrder.CompleteTradeList) {
            if (Order.SymbolType == type) {
                TradeList.add(Order);
            }
        }
        return TradeList;
    }
    
    static void SummarizeReturnDistribution(QPortfolioBase portfolio) {
        List<Double> lnValueList = new ArrayList<>();
        List<Double> lnProfitValueList = new ArrayList<>();
        SortedMap<LocalDateTime, Double> balancesDailyWithoutTime = GetBalanceDailyWithoutTime(portfolio.BalancesDaily);
        
        LnOnBalanceList lnOnBalances = portfolio.LnOnBalances;
        LocalDateTime[] dates = lnOnBalances.keySet().toArray(new LocalDateTime[0]);
        Double[] values = lnOnBalances.values().toArray(new Double[0]);
        
        for (int indx = 0; indx < lnOnBalances.size(); ++indx) {
            if (Double.isNaN(values[indx])) {
                continue;
            }
            lnValueList.add(values[indx]);
            if (balancesDailyWithoutTime.containsKey(dates[indx])) {
                double lastBalance = 0;
                LocalDateTime lastDay = GetLastDate(balancesDailyWithoutTime, dates[indx]);
                if (balancesDailyWithoutTime.containsKey(lastDay)) {
                    lastBalance = balancesDailyWithoutTime.get(lastDay);
                }
                lnProfitValueList.add(balancesDailyWithoutTime.get(dates[indx]) - lastBalance);
            } else {
                Log.error("There is not Balance at the day : " + dates[indx].toLocalDate().toString());
                lnProfitValueList.add(0.0);
            }
        }
        
        calculateConsequtive(lnValueList, lnProfitValueList, portfolio.Summary);
        
        summarizeDistributionInternal(portfolio.Summary.QPortfolioBaseReturnDistribution, lnValueList);
    }
    
    private static void calculateConsequtive(List<Double> valueList, List<Double> profitValueList, QPortfolioSummary summary) {
        int consecutiveNumber = 0;
        double consecutiveProfit = 0;
        List<List<Integer>> AllConsecutiveDateList = ReturnDistributionEx.GetConsecutiveLossDateList(valueList);
        
        int[] consecutiveNumberRef = new int[] { consecutiveNumber };
        double[] consecutiveProfitRef = new double[] { consecutiveProfit };
        
        ReturnDistributionEx.CalculateConsecutiveMaxOrdersNumber(AllConsecutiveDateList, profitValueList, consecutiveNumberRef, consecutiveProfitRef);
        summary.ConsecutiveLossesMaxNumber = consecutiveNumberRef[0];
        summary.ConsecutiveLossesMaxNumberProfit = consecutiveProfitRef[0];
        
        ReturnDistributionEx.CalculateConsecutiveMaxOrdersProfit(AllConsecutiveDateList, profitValueList, consecutiveNumberRef, consecutiveProfitRef, false);
        summary.ConsecutiveLossesMaxProfitNumber = consecutiveNumberRef[0];
        summary.ConsecutiveLossesMaxProfit = consecutiveProfitRef[0];
        
        AllConsecutiveDateList = ReturnDistributionEx.GetConsecutiveWinsDateList(valueList);
        
        ReturnDistributionEx.CalculateConsecutiveMaxOrdersNumber(AllConsecutiveDateList, profitValueList, consecutiveNumberRef, consecutiveProfitRef);
        summary.ConsecutiveWinsMaxNumber = consecutiveNumberRef[0];
        summary.ConsecutiveWinsMaxNumberProfit = consecutiveProfitRef[0];
        
        ReturnDistributionEx.CalculateConsecutiveMaxOrdersProfit(AllConsecutiveDateList, profitValueList, consecutiveNumberRef, consecutiveProfitRef, true);
        summary.ConsecutiveWinsMaxProfitNumber = consecutiveNumberRef[0];
        summary.ConsecutiveWinsMaxProfit = consecutiveProfitRef[0];
    }
    
    private static void summarizeTradeGroupDistribution(QPortfolioBase portfolio) {
        List<Double> tradeGroupProfitValueList = new ArrayList<>();
        
        for (TradeGroup tradeGroup : portfolio.TradeGroupList.values()) {
            double value = tradeGroup.PNL;
            if (Double.isNaN(value)) {
                continue;
            }
            
            tradeGroupProfitValueList.add(tradeGroup.PNL / portfolio.InitialCash);
        }
        if (tradeGroupProfitValueList.size() > 0) {
            summarizeDistributionInternal(portfolio.Summary.TradeGroupReturnDistribution, tradeGroupProfitValueList);
        }
    }
    
    private static void summarizeDistributionInternal(ReturnDistribution qPortfolioBaseReturnDistribution, List<Double> lnValueList) {
        ReturnDistributionEx.CalcWinsAndLosses(qPortfolioBaseReturnDistribution, lnValueList, true);
        ReturnDistributionEx.CalulateWinLossExpectedRange(qPortfolioBaseReturnDistribution, lnValueList);
        ReturnDistributionEx.CalcReturnDistributionAllData(qPortfolioBaseReturnDistribution, lnValueList);
    }
    
    static LocalDateTime GetLastDate(SortedMap<LocalDateTime, Double> BalancesDailyWithoutTime, LocalDateTime curDate) {
        LocalDateTime lastDate = LocalDateTime.MIN;
        LocalDateTime tmpDate = curDate;
        if (BalancesDailyWithoutTime.firstKey().compareTo(tmpDate) == 0) {
            lastDate = curDate;
            return lastDate;
        }
        tmpDate = tmpDate.minusDays(1);
        while (BalancesDailyWithoutTime.firstKey().compareTo(tmpDate) <= 0) {
            if (BalancesDailyWithoutTime.containsKey(tmpDate)) {
                lastDate = tmpDate;
                break;
            }
            tmpDate = tmpDate.minusDays(1);
        }
        
        return lastDate;
    }
    
    private static void GetLongCompleteTradeList(List<CompleteOrder> completeTradeList, 
                                                List<CompleteOrder> longCompleteTradeList, 
                                                List<CompleteOrder> shortCompleteTradeList) {
        for (CompleteOrder completeOrder : completeTradeList) {
            if (completeOrder.CompleteTradeList.size() == 0) {
                continue;
            }
            if (completeOrder.CompleteTradeList.get(0).getOrderCategory() == OrderCategory.Executable_System_PortfolioHedgingOpen) {
                continue;
            }
            if (completeOrder.CompleteTradeList.get(0).getQuantity() > 0) {
                longCompleteTradeList.add(completeOrder);
            }
            
            if (completeOrder.CompleteTradeList.get(0).getQuantity() < 0) {
                shortCompleteTradeList.add(completeOrder);
            }
        }
    }
    
    private static boolean ContainPortfolioHedgingOrder(CompleteOrder completeOrder) {
        if (completeOrder.CompleteTradeList.get(0).getOrderCategory() == OrderCategory.Executable_System_PortfolioHedgingOpen) {
            return true;
        }
        return false;
    }
    
    public static double CalculatePercentageWinLoss(CompleteOrder completeOrder, QPortfolioBase QPortfolioSummary) {
        double winloss = Double.NaN;
        // TODO: check completetradelist 
        if (/*VerifyCompleteOrder() &&*/ completeOrder.CompleteTradeList.size() >= 2) {
            TradeConfirmation enterOrder = completeOrder.CompleteTradeList.get(0);
            if (enterOrder != null) {
                try {
                    if (enterOrder.SymbolType == OrderSymbolType.Option) {
                        double balance = Double.NaN;
                        PositionEx Position = null;
                        winloss = completeOrder.OrderProfit / Math.abs(QPortfolioSummary._config.PositionSizeLimit * QPortfolioSummary.InitialCash);
                        // 这个找法，实际造成性能太差无法使用了。这是分钟数据。
                        // Position = FindEnterPositionByTradeId(enterOrder, QPortfolioSummary.PositionHistory);
                        // if (Position != null)
                        // {
                        //     // balance = QPortfolioSummary.Balances[Position.position.TimeStamp];
                        //     
                        // }
                        // else
                        // {
                        //     Log.Error("Can not find Order Position!!! SYMBOL " + enterOrder.Symbol + " TIMESTAMP " + enterOrder.TimeStamp.ToShortDateString());
                        // }
                    } else {
                        winloss = completeOrder.OrderProfit / Math.abs(enterOrder.GetWorth());
                    }
                    
                } catch (Exception ex) {
                    Log.error(ex.getMessage());
                }
                
            }
        } else {
            Log.warn("CalculatePercentageWinLoss Invalid Complete Order " + completeOrder.TradeGroupID.toString());
        }
        return winloss;
    }
    
    public static PositionEx FindEnterPositionByTradeId(TradeConfirmation trade, PositionHistoryList PositionHistory) {
        PositionEx Position = null;
        LocalDateTime start = trade.TimeStamp;
        while (start.compareTo(PositionHistory.lastKey()) <= 0) {
            if (PositionHistory.containsKey(start.toLocalDate())) {
                SortedMap<String, List<PositionEx>> groupPositionsBySymbol = PositionHistory.get(start.toLocalDate());
                if (groupPositionsBySymbol.containsKey(trade.Symbol)) {
                    for (PositionEx pos : groupPositionsBySymbol.get(trade.Symbol)) {
                        if (pos.position.PositionGroupId.equals(trade.TradeGroupID)) {
                            Position = pos;
                            break;
                        }
                    }
                    if (Position != null) {
                        break;
                    }
                }
            }
            start = start.plusDays(1);
        }
        return Position;
    }
}