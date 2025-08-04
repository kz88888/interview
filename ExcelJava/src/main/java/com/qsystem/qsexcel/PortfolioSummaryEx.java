package com.qsystem.qsexcel;

import com.qsystem.clientlib.QPortfolioSummary;
import com.qsystem.clientlib.BalanceList;
import com.qsystem.clientlib.ReturnWithTimeSpan;

/**
 * 扩展投资组合摘要类
 */
public class PortfolioSummaryEx extends QPortfolioSummary {
    public double BenchmarkReturn;
    public BalanceList Balances = new BalanceList();
    public String PortfolioSummaryExKey = "";

    /**
     * 构造函数
     */
    public PortfolioSummaryEx() {
        AccountMaxDrawnDown = new ReturnWithTimeSpan();
        Best3MonthReturn = new ReturnWithTimeSpan();
        Worst3MonthReturn = new ReturnWithTimeSpan();
        Best1YearReturn = new ReturnWithTimeSpan();
        Worst1YearReturn = new ReturnWithTimeSpan();
        Best3YearReturn = new ReturnWithTimeSpan();
        Worst3YearReturn = new ReturnWithTimeSpan();
    }
 
    /**
     * 获取投资组合摘要
     */
    public static PortfolioSummaryEx GetPortfolioSummary(QPortfolioSummary summary, double BenchmarkReturn, BalanceList Balances) {
        PortfolioSummaryEx summaryEx = new PortfolioSummaryEx();
        summaryEx.BeginDate = summary.BeginDate;
        summaryEx.EndDate = summary.EndDate;
        summaryEx.AverageExposure = summary.AverageExposure;
        summaryEx.OrderNumber = summary.OrderNumber;
        summaryEx.NumberOfOrderWin = summary.NumberOfOrderWin;
        summaryEx.NumberOfOrderLose = summary.NumberOfOrderLose;
        summaryEx.WinLossRangeUpper = summary.WinLossRangeUpper;
        summaryEx.WinLossRangeLower = summary.WinLossRangeLower;
        summaryEx.WinLossRangeAverage = summary.WinLossRangeAverage;
        summaryEx.AverageReturnPerOrder = summary.AverageReturnPerOrder;
        summaryEx.WinRatio = summary.WinRatio;
        summaryEx.Beta = summary.Beta;
        summaryEx.Alpha = summary.Alpha;
        summaryEx.AnnualizeAlpha = summary.AnnualizeAlpha;
        summaryEx.SharpRatio = summary.SharpRatio;
        summaryEx.StrategyReturn = summary.StrategyReturn;
        ReturnWithTimeSpan.CopyBestWorthReturn(summary.AccountMaxDrawnDown, summaryEx.AccountMaxDrawnDown);
        summaryEx.StrategyReturnInSampleStart = summary.StrategyReturnInSampleStart;
        summaryEx.StrategyReturnOutofSample = summary.StrategyReturnOutofSample;
        summaryEx.YearInterval = summary.YearInterval;
        summaryEx.YearPortfolioRatio = summary.YearPortfolioRatio;
        ReturnWithTimeSpan.CopyBestWorthReturn(summary.Best3MonthReturn, summaryEx.Best3MonthReturn);
        ReturnWithTimeSpan.CopyBestWorthReturn(summary.Worst3MonthReturn, summaryEx.Worst3MonthReturn);
        ReturnWithTimeSpan.CopyBestWorthReturn(summary.Best1YearReturn, summaryEx.Best1YearReturn);
        ReturnWithTimeSpan.CopyBestWorthReturn(summary.Worst1YearReturn, summaryEx.Worst1YearReturn);
        ReturnWithTimeSpan.CopyBestWorthReturn(summary.Best3YearReturn, summaryEx.Best3YearReturn);
        ReturnWithTimeSpan.CopyBestWorthReturn(summary.Worst3YearReturn, summaryEx.Worst3YearReturn);
        summaryEx.DataPointTested = summary.DataPointTested;
        summaryEx.InitialBalance = summary.InitialBalance;
        summaryEx.TotalNetProfit = summary.TotalNetProfit;
        summaryEx.ProfitFactor = summary.ProfitFactor;
        summaryEx.GrossProfit = summary.GrossProfit;
        summaryEx.GrossLoss = summary.GrossLoss;
        summaryEx.ConsecutiveWinsMaxNumber = summary.ConsecutiveWinsMaxNumber;
        summaryEx.ConsecutiveWinsMaxNumberProfit = summary.ConsecutiveWinsMaxNumberProfit;
        summaryEx.ConsecutiveWinsMaxProfitNumber = summary.ConsecutiveWinsMaxProfitNumber;
        summaryEx.ConsecutiveWinsMaxProfit = summary.ConsecutiveWinsMaxProfit;
        summaryEx.ConsecutiveLossesMaxNumber = summary.ConsecutiveLossesMaxNumber;
        summaryEx.ConsecutiveLossesMaxNumberProfit = summary.ConsecutiveLossesMaxNumberProfit;
        summaryEx.ConsecutiveLossesMaxProfitNumber = summary.ConsecutiveLossesMaxProfitNumber;
        summaryEx.ConsecutiveLossesMaxProfit = summary.ConsecutiveLossesMaxProfit;
        summaryEx.NetNorminalExposure = summary.NetNorminalExposure;
        summaryEx.MaxGrossExposure = summary.MaxGrossExposure;
        summaryEx.BenchmarkReturn = BenchmarkReturn;

        for (java.time.LocalDateTime dt : Balances.keySet()) {
            summaryEx.Balances.put(dt, Balances.get(dt));
        }

        return summaryEx;
    }
} 