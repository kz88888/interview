package com.qsystem.qsexcel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 摘要类
 */
public class Summary {
    public String StrategyName;
    public String StrategyDescription;
    public LocalDateTime BeginDate;
    public LocalDateTime EndDate;
    public double StrategyReturn;
    public double StrategyReturnInsampleStart;
    public double StrategyReturnOutOfSample;
    public double SharpRatio;
    public double YearInterval;
    public double AnnualizedReturn;
    public double AverageExposure;
    public int OrderNumber;
    public int WinOrderNumber;
    public int LoseOrderNumber;
    public double WinLoseNumberRatio;
    public List<String> TagList = new ArrayList<>();
    public double Beta;
    public double Alpha;

    public List<Double> ThresholdList_OneGroup = new ArrayList<>();

    // public void Init(QPortfolioBase port)
    // {
    //     this.BeginDate = port.Summary.BeginDate;
    //     this.EndDate = port.Summary.EndDate;
    // }
} 