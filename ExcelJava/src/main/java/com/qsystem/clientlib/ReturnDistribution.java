package com.qsystem.clientlib;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;

/**
 * 收益分布类
 */
public class ReturnDistribution {
    public int OrderNumber;
    public int NumberOfOrderWin;
    public int NumberOfOrderLose;
    public double WinRatio = Double.NaN;
    public double WinLossRangeUpper;
    public double WinLossRangeLower;
    public double WinLossRangeAverage;
    public int ConsecutiveWinsMaxNumber;
    public double ConsecutiveWinsMaxNumberProfit;
    public int ConsecutiveWinsMaxProfitNumber;
    public double ConsecutiveWinsMaxProfit;
    public int ConsecutiveLossesMaxNumber;
    public double ConsecutiveLossesMaxNumberProfit;
    public int ConsecutiveLossesMaxProfitNumber;
    public double ConsecutiveLossesMaxProfit;
    
    public List<Double> returnDistributionList = new ArrayList<>();
    public SortedMap<Double, Integer> DistributionDictionary = new TreeMap<>();
    public double StandardDeviation = 0;
    public int SpreadCount = 100;
    public double Range = Double.NaN;
    public List<CompleteOrder> CompleteTradeList = null;
    public double MaxDown = Double.NaN;
    public int NumberOfSampleCount = 0;
    public int NumberOfSampleCount0025 = 0;
    public int NumberOfSampleCount001 = 0;
} 