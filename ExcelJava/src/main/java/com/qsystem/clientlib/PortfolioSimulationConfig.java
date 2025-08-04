package com.qsystem.clientlib;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.time.LocalDateTime;

/**
 * 投资组合模拟配置类
 */
public class PortfolioSimulationConfig {
    
    public enum SimulatorMode {
        ExecuteStrategySaveToTag,
        LoadSignalsFromTagNoMerge,
        LoadSignalsFromTagMerge,
        EmailSignals,
        IncrementalModeMerge,
        EmailTrades,
        BackTestMerge,
        BackTestNoMerge,
        ForwardTest,
        None
    }
    
    public enum SamePriorityBehavior {
        New,
        Old
    }
    
    public enum PortfolioPolicy {
        BySignalStrength,
        NewReplaceOld,
        NoDuplicateOrders,
        AllowDuplicateOrders
    }
    
    public enum OptionExpirationPolicy {
        AcceptExceciseAssignment,
        ClosePositionOnExpFriday
    }
    
    public enum StockValuationPrice {
        Open,
        High,
        Low,
        Close,
        AdjClose
    }
    
    public enum StockOrderType {
        close,
        adjClose,
        open,
        market,
        limit
    }
    
    public enum OptionOrderType {
        market,
        limit
    }
    
    public enum SimulatedOrderExpectedExecutionTime {
        current,
        next
    }
    
    public enum SimulatedOrderQuoteSource {
        current_close,
        current_adjClose,
        next_close,
        next_adjClose,
        next_open,
        next_market
    }
    
    public enum BrokerType {
        QBroker,
        QBrokerNetPipeClient,
        QBrokerHTTPClient
    }
    
    public enum PortfolioPresentationFormat {
        XML,
        Excel,
        None
    }
    
    public enum PortfolioPresentationSummaryFormat {
        ONESHEET,
        MULTIPLESHEET,
        None
    }
    
    public enum HedgingInstruments {
        NULL,
        CALL,
        PUT,
        ShortOption,
        LongOption,
        STOCK,
        SYMBOL,
        OTHER
    }
    
    public enum HedgingFrequency {
        NULL,
        Daily,
        WeeklyMonday,
        WeeklyTuesday,
        WeeklyWednesday,
        WeeklyThursday,
        WeeklyFriday,
        MonthlyFirstDay,
        MonthlyLastDay,
        OnPositionChange,
        OncePerExpirationPerSymbol
    }
    
    public enum ExecuteHedgingOrder {
        current,
        next
    }
    
    public enum MeasureBy {
        DollarExposure
    }
    
    public enum HedgingLevel {
        NULL,
        Position,
        Portfolio
    }
    
    public static class HedgingConfig {
        @JsonProperty
        public HedgingLevel Level;
        @JsonProperty
        public MeasureBy measureBy;
        @JsonProperty
        public double ratio = 0;
        @JsonProperty
        public HedgingInstruments HedgingInstruments;
        @JsonProperty
        public String HedgingInstrumentsSymbol = "";
        @JsonProperty
        public double HedgingThreshold = Double.NaN;
        @JsonProperty
        public double HedgingTarget = Double.NaN;
        @JsonProperty
        public HedgingFrequency HedgingFrequency;
        
        public HedgingConfig() {
            this.Level = HedgingLevel.Portfolio;
            this.measureBy = MeasureBy.DollarExposure;
            this.HedgingInstruments = HedgingInstruments.NULL;
            this.HedgingFrequency = HedgingFrequency.OncePerExpirationPerSymbol;
        }
    }
    
    @JsonProperty
    public PortfolioPresentationFormat PortfolioPresentationFormat;
    @JsonProperty
    public PortfolioPresentationSummaryFormat PortfolioPresentationSummaryFormat;
    @JsonProperty
    public String StrategyDefaultDll;
    @JsonProperty
    public String ConfigLocation;
    @JsonProperty
    public boolean EnablePortfolioAudit;
    @JsonProperty
    public int SharpeRatioTimeSpan;
    @JsonProperty
    public int SharpeRatioSmoothCount;
    @JsonProperty
    public double InitialCash = 0.0;
    @JsonProperty
    public double GrossExposureMax = 0.0;
    @JsonProperty
    public double LongExposureMax = 0.0;
    @JsonProperty
    public double ShortExposureMax = 0.0;
    @JsonProperty
    public double PositionSizeLimit = 100;
    @JsonProperty
    public double MaintainceLimit = Double.NaN;
    @JsonProperty
    public List<String> BenchmarkSymbolList = new ArrayList<>();
    @JsonProperty
    public String DefaultBenchmarkSymbol = "";
    @JsonProperty
    public LocalDateTime BackTestingStart = LocalDateTime.MIN;
    @JsonProperty
    public LocalDateTime BackTestingEnd = LocalDateTime.MAX;
    @JsonProperty
    public LocalDateTime PoistionCloseDay = LocalDateTime.MAX;
    @JsonProperty
    public List<LocalDateTime> SimulationTimeSeries = new ArrayList<>();
    @JsonProperty
    public SortedSet<PortfolioPolicy> PortfolioPolicyList = new TreeSet<>();
    @JsonProperty
    public OptionExpirationPolicy OptionExpPolicy;
    @JsonProperty
    public Double DeltaBasedPositionSizeLowerLimit;
    @JsonProperty
    public StockValuationPrice StockValuationPrice;
    @JsonProperty
    public double StockPerShare = 0;
    @JsonProperty
    public double StockPerTrade = 0;
    @JsonProperty
    public double OptionPerContract = 0;
    @JsonProperty
    public double OptionPerTrade = 0;
    @JsonProperty
    public double OptionExercisePerTrade = 0;
    @JsonProperty
    public double OptionExercisePerContract = 0;
    @JsonProperty
    public double OptionAssignmentPerTrade = 0;
    @JsonProperty
    public double OptionAssignmentPerContract = 0;
    @JsonProperty
    public String ConfigLocationStr = "";
    @JsonProperty
    public StockOrderType StockOrderType;
    @JsonProperty
    public OptionOrderType OptionOrderType;
    @JsonProperty
    public BrokerType qBrokerType;
    @JsonProperty
    public SimulatedOrderQuoteSource simulatedOrderQuoteSource;
    @JsonProperty
    public SimulatedOrderExpectedExecutionTime SimulatedOrderExpectedExecutionTime;
    @JsonProperty
    public String BrokerLocation = "http://localhost:18888";
    @JsonProperty
    public String NetPipeUrl = "net.pipe://localhost/BrokerService";
    @JsonProperty
    public SimulatorMode SimulatorMode;
    @JsonProperty
    public String SignalListKey = "";
    @JsonProperty
    public int RetryCount = 3;
    @JsonProperty
    public int RetryWaitTime = 30;
    @JsonProperty
    public int RetryCloseTime = 25;
    @JsonProperty
    public ExecuteHedgingOrder ExecuteHedgingOrder;
    @JsonProperty
    public String SpecialSignalTag;
    @JsonProperty
    public String ConfigurationID;
    @JsonProperty
    public boolean RollbackPostiion = true;
    @JsonProperty
    public HedgingConfig HedgingConfig = new HedgingConfig();
    @JsonProperty
    public boolean CloseAllPositionEndSimulation = true;
    @JsonProperty
    public boolean SignalTagsPermitation = false;
    @JsonProperty
    public String SignalTagsPermitationScope = "";
    
    public PortfolioSimulationConfig() {
        this.StockValuationPrice = StockValuationPrice.AdjClose;
        this.SimulatedOrderExpectedExecutionTime = SimulatedOrderExpectedExecutionTime.current;
        this.SimulatorMode = SimulatorMode.None;
    }
} 