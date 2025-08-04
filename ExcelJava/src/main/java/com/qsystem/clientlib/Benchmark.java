package com.qsystem.clientlib;

import java.time.LocalDateTime;
import java.util.SortedMap;

/**
 * 基准类
 */
public class Benchmark {
    
    public String _benchmarkSymbol = "";
    public LocalDateTime _startDate = LocalDateTime.MIN;
    public LocalDateTime _endDate = LocalDateTime.MAX;
    
    public double BenchmarkBeginValue = 0;
    public double BenchmarkEndValue = 0;
    
    public double _benchmarkReturn = 0.0;
    
    public double getBenchmarkReturn() {
        if (_benchmarkReturn == 0.0) {
            _benchmarkReturn = GetBenchmarkReturn();
        }
        return _benchmarkReturn;
    }
    
    public void setBenchmarkReturn(double value) {
        _benchmarkReturn = value;
    }
    
    public double SharpRatio;
    public ReturnWithTimeSpan Best3MonthReturn;
    public ReturnWithTimeSpan Worst3MonthReturn;
    public ReturnWithTimeSpan Best1YearReturn;
    public ReturnWithTimeSpan Worst1YearReturn;
    public ReturnWithTimeSpan Best3YearReturn;
    public ReturnWithTimeSpan Worst3YearReturn;
    
    public ReturnWithTimeSpan Best3MonthReturnTestingPeriod;
    public ReturnWithTimeSpan Worst3MonthReturnTestingPeriod;
    public ReturnWithTimeSpan Best1YearReturnTestingPeriod;
    public ReturnWithTimeSpan Worst1YearReturnTestingPeriod;
    public ReturnWithTimeSpan Best3YearReturnTestingPeriod;
    public ReturnWithTimeSpan Worst3YearReturnTestingPeriod;
    
    public double Best3MonthPortfolioReturn;
    public double Worst3MonthPortfolioReturn;
    public double Best1YearPortfolioReturn;
    public double Worst1YearPortfolioReturn;
    public double Best3YearPortfolioReturn;
    public double Worst3YearPortfolioReturn;
    
    public SortedMap<LocalDateTime, Double> monthlyBenchmarkAdj = null;
    public SortedMap<LocalDateTime, Double> lnReturnList = null;
    public SortedMap<LocalDateTime, Double> benchmarkValues = null;
    
    public double GetBenchmarkReturn() {
        return BenchmarkEndValue / BenchmarkBeginValue - 1;
    }
    
    public SortedMap<LocalDateTime, Double> getReturns() {
        return lnReturnList;
    }
    
    public Benchmark() {
    }
    
    public Benchmark(String Symbol, LocalDateTime start, LocalDateTime end) {
        _benchmarkSymbol = Symbol;
        _startDate = start;
        _endDate = end;
        
        Best3MonthReturn = new ReturnWithTimeSpan();
        Worst3MonthReturn = new ReturnWithTimeSpan();
        Best1YearReturn = new ReturnWithTimeSpan();
        Worst1YearReturn = new ReturnWithTimeSpan();
        Best3YearReturn = new ReturnWithTimeSpan();
        Worst3YearReturn = new ReturnWithTimeSpan();
        Best3MonthReturnTestingPeriod = new ReturnWithTimeSpan();
        Worst3MonthReturnTestingPeriod = new ReturnWithTimeSpan();
        Best1YearReturnTestingPeriod = new ReturnWithTimeSpan();
        Worst1YearReturnTestingPeriod = new ReturnWithTimeSpan();
        Best3YearReturnTestingPeriod = new ReturnWithTimeSpan();
        Worst3YearReturnTestingPeriod = new ReturnWithTimeSpan();
    }
} 