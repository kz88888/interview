package com.qsystem.clientlib;

import java.time.LocalDateTime;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;
import com.qsystem.clientlib.JsonModel.TradeConfirmation;
import com.qsystem.clientlib.JsonModel.TradeGroup;
import com.qsystem.clientlib.JsonModel.RiskMeasurement;

/**
 * 投资组合基类
 */
public class QPortfolioBase {
    
    public static class SectorIndustry {
        public String sector;
        public String industry;
    }
    
    public LocalDateTime currentDate = LocalDateTime.MIN;
    public LocalDateTime ExpectedSystemTime = LocalDateTime.MIN;
    
    public TradeList Orders;
    public SortedMap<String, TradeGroup> TradeGroupList = new TreeMap<>();
    
    public PositionHistoryList PositionHistory;
    
    public BalanceList BalancesHiFrequency;
    
    public BalanceDailyList BalancesDaily;
    
    public LnOnBalanceList LnOnBalances;
    
    public SortedMap<LocalDateTime, RiskMeasurement> RiskMeasurementList = new TreeMap<>();
    
    public SortedMap<LocalDateTime, Double> Exposures;
    
    public SortedMap<LocalDateTime, Double> GrossExposures;
    
    public SortedMap<String, TradeConfirmation> OrderDictionary = new TreeMap<>();
    
    public PortfolioSimulationConfig _config = null;
    
    public LocalDateTime InitDateTime = LocalDateTime.MIN;
    
    public double InitialCash = Double.NaN;
    
    public int _qNextOrderId = 0;
    
    public int _qClientId = 0;
    
    public double TotalTransactionExpenses = 0;
    
    public LocalDateTime lastEndDate = LocalDateTime.MIN;
    
    public QPortfolioSummary Summary = new QPortfolioSummary();
    public Benchmark Benchmark = null;
    public List<CompleteOrder> CompleteTradeList = new ArrayList<>();
    public SortedMap<String, List<CompleteOrder>> CompletedOrdersBySymbolDictionary = new TreeMap<>();
    public List<Benchmark> BenchmarkList = null;
    
    public int maxSectorCount = 0;
    public SortedMap<String, List<SectorIndustry>> sectorIndustryDictionary = new TreeMap<>();
} 