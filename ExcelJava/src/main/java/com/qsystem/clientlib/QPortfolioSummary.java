package com.qsystem.clientlib;

import java.time.LocalDateTime;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;

/**
 * 投资组合摘要类
 */
public class QPortfolioSummary {
    
    public QPortfolioSummary() {
        AccountMaxDrawnDown = new ReturnWithTimeSpan();
        Best3MonthReturn = new ReturnWithTimeSpan();
        Worst3MonthReturn = new ReturnWithTimeSpan();
        Best1YearReturn = new ReturnWithTimeSpan();
        Worst1YearReturn = new ReturnWithTimeSpan();
        Best3YearReturn = new ReturnWithTimeSpan();
        Worst3YearReturn = new ReturnWithTimeSpan();
    }
    
    // Properties
    public String StrategyName;
    public String StrategyDescription;
    public double StrategyReturnInsampleStart;
    public double StrategyReturnOutOfSample;
    public double AnnualizedReturn;
    public int WinOrderNumber;
    public int LoseOrderNumber;
    public double WinLoseNumberRatio;
    public List<String> TagList = new ArrayList<>();
    public List<Double> ThresholdList_OneGroup = new ArrayList<>();
    
    public SortedMap<LocalDateTime, Double> sharpRatioSortedList = null;
    public SortedMap<LocalDateTime, Double> benchmarkSharpRatioSortedList = null;
    
    public LocalDateTime BeginDate;
    public LocalDateTime EndDate;
    public LocalDateTime lastEndDate;
    public double AverageExposure;
    public int OrderNumber;
    public int NumberOfOrderWin;
    public int NumberOfOrderLose;
    
    public double WinLossRangeUpper;
    public double WinLossRangeLower;
    public double WinLossRangeAverage;
    
    public int AverageReturnPerOrder;
    public double WinRatio;
    public double Beta;
    public double Alpha;
    public double AnnualizeAlpha;
    public double SharpRatio;
    public double StrategyReturn;
    public ReturnWithTimeSpan AccountMaxDrawnDown;
    public double StrategyReturnInSampleStart;
    public double StrategyReturnOutofSample;
    public double YearInterval;
    public double YearPortfolioRatio;
    public ReturnWithTimeSpan Best3MonthReturn;
    public ReturnWithTimeSpan Worst3MonthReturn;
    public ReturnWithTimeSpan Best1YearReturn;
    public ReturnWithTimeSpan Worst1YearReturn;
    public ReturnWithTimeSpan Best3YearReturn;
    public ReturnWithTimeSpan Worst3YearReturn;
    public int DataPointTested;
    public double InitialBalance;
    public double TotalNetProfit;
    public double ProfitFactor;
    public double GrossProfit;
    public double GrossLoss;
    public int ConsecutiveWinsMaxNumber;
    public double ConsecutiveWinsMaxNumberProfit;
    public int ConsecutiveWinsMaxProfitNumber;
    public double ConsecutiveWinsMaxProfit;
    public int ConsecutiveLossesMaxNumber;
    public double ConsecutiveLossesMaxNumberProfit;
    public int ConsecutiveLossesMaxProfitNumber;
    public double ConsecutiveLossesMaxProfit;
    public double NetNorminalExposure;
    public double MaxGrossExposure;
    
    // 20200202新增
    public double delta;
    public double vega;
    public double theta;
    public double margins;
    
    public ReturnDistribution OrderReturnDistribution = new ReturnDistribution();
    public ReturnDistribution QPortfolioBaseReturnDistribution = new ReturnDistribution();
    public ReturnDistribution LongOrderReturnDistribution = new ReturnDistribution();
    public ReturnDistribution ShortOrderReturnDistribution = new ReturnDistribution();
    // 20200202新增
    public ReturnDistribution TradeGroupReturnDistribution = new ReturnDistribution();
    
    public double TotalTransactionExpenses = 0;
} 