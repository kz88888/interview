package com.qsystem.clientlib;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.UUID;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.qsystem.clientlib.JsonModel.TradeGroup;
import com.qsystem.clientlib.JsonModel.TradeConfirmation;
import com.qsystem.clientlib.JsonModel.Order;
import com.qsystem.clientlib.JsonModel.RiskMeasurement;
import com.qsystem.clientlib.JsonModel.OrderCategory;


/**
 * 投资组合类
 */
public class QPortfolio extends QPortfolioBase {
    // 枚举
    public enum OrderOperator {
        Buy,
        Sell,
        Null
    }

    // 函数式接口（委托）
    @FunctionalInterface
    public interface QPortfolioProcessOptionExpiratinSuccess {
        void onSuccess(int clientId, TradeConfirmation optionTrade, TradeConfirmation stockTrade);
    }

    public SortedMap<String, HedgeGroup> HedgeGroupDictionary = new TreeMap<>();
    public QPortfolioProcessOptionExpiratinSuccess _qFProcessOptionExprationSuccess;
    public UUID PortfolioHedgingGuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
    
    public static class HedgeGroup {
        public String symbol;
        public List<String> OptonSymbolList = new ArrayList<>();
        public List<Integer> HedgeOpenList = new ArrayList<>();
        public boolean isHedging = false;
    }
    
    public QPortfolio() {
        Orders = new TradeList();
        Exposures = new TreeMap<>();
        GrossExposures = new TreeMap<>();
        BalancesDaily = new BalanceDailyList();
        BalancesHiFrequency = new BalanceList();
        PositionHistory = new PositionHistoryList();
        Summary = new QPortfolioSummary();
    }
    
    /**
     * 检查JToken的键是否为长日期时间格式
     */
    private static boolean isKeyLongDT(JsonNode jToken) {
        boolean isLongDateTime = false;
        for (JsonNode balanceJToken : jToken) {
            JsonNode first = balanceJToken;
            if (first != null) {
                String strTime = first.fieldNames().next();
                if (strTime.length() > 8) {
                    isLongDateTime = true;
                }
            }
        }
        return isLongDateTime;
    }
    
    /**
     * 检查JEnumerable的键是否为长日期时间格式
     */
    private static boolean isKeyLongDT(Iterable<JsonNode> sourceTokens) {
        boolean isLongDateTime = false;
        for (JsonNode balanceJToken : sourceTokens) {
            JsonNode first = balanceJToken;
            if (first != null) {
                String strTime = first.fieldNames().next();
                if (strTime.length() > 8) {
                    isLongDateTime = true;
                }
            }
        }
        return isLongDateTime;
    }
    
    /**
     * 获取总敞口列表
     */
    private static SortedMap<LocalDateTime, Double> GetGrossExposureseList(Iterable<JsonNode> sourceTokens, 
            SortedMap<LocalDateTime, Double> dicList, LocalDateTime startTime, LocalDateTime endTime) {
        for (JsonNode balanceJToken : sourceTokens) {
            JsonNode first = balanceJToken;
            if (first != null) {
                String strTime = first.fieldNames().next();
                LocalDateTime dt;
                if (strTime.length() > 8) {
                    dt = ConvertTime(strTime.substring(0, 8));
                    if (Integer.parseInt(strTime.substring(8, 14)) == 150000) {
                        if (startTime.isBefore(dt) && dt.isBefore(endTime)) {
                            try {
                                ObjectMapper mapper = new ObjectMapper();
                                RiskMeasurement riskMeasurement = mapper.treeToValue(first.get(strTime), RiskMeasurement.class);
                                dicList.put(dt, riskMeasurement.grossExposure);
                            } catch (Exception e) {
                                System.err.println("Error processing gross exposure data for time: " + strTime + ", Error: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return dicList;
    }
    
    /**
     * 获取敞口列表
     */
    private static SortedMap<LocalDateTime, Double> GetExposuresList(Iterable<JsonNode> sourceTokens, 
            SortedMap<LocalDateTime, Double> dicList, LocalDateTime startTime, LocalDateTime endTime) {
        for (JsonNode balanceJToken : sourceTokens) {
            JsonNode first = balanceJToken;
            if (first != null) {
                String strTime = first.fieldNames().next();
                LocalDateTime dt;
                if (strTime.length() > 8) {
                    dt = ConvertTime(strTime.substring(0, 8));
                    if (Integer.parseInt(strTime.substring(8, 14)) == 150000) {
                        if (startTime.isBefore(dt) && dt.isBefore(endTime)) {
                            try {
                                ObjectMapper mapper = new ObjectMapper();
                                RiskMeasurement riskMeasurement = mapper.treeToValue(first.get(strTime), RiskMeasurement.class);
                                dicList.put(dt, riskMeasurement.exposure);
                            } catch (Exception e) {
                                System.err.println("Error processing exposure data for time: " + strTime + ", Error: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (strTime.length() == 8) {
                    dt = ConvertTime(strTime);
                    if (startTime.isBefore(dt) && dt.isBefore(endTime)) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            RiskMeasurement riskMeasurement = mapper.treeToValue(first.get(strTime), RiskMeasurement.class);
                            dicList.put(dt, riskMeasurement.exposure);
                        } catch (Exception e) {
                            System.err.println("Error processing exposure data for time: " + strTime + ", Error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return dicList;
    }
    
    /**
     * 转换时间字符串为LocalDateTime
     */
    private static LocalDateTime ConvertTime(String str_time) {
        int year = Integer.parseInt(str_time.substring(0, 4));
        int month = Integer.parseInt(str_time.substring(4, 6));
        int day = Integer.parseInt(str_time.substring(6, 8));
        return LocalDateTime.of(year, month, day, 0, 0);
    }
    
    /**
     * 使用旧基线恢复投资组合
     */
    public static void RestorePortfolioUsingOldBaseline(String localPath, String strategyName, QPortfolio qPortfolioBase) {
        qPortfolioBase._config = new PortfolioSimulationConfig();
        
        String JsonPathName = localPath + strategyName + ".json";
        
        String shortName = "";
        
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(JsonPathName)));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode o = mapper.readTree(jsonContent);
            
            qPortfolioBase.BalancesHiFrequency = JsonUtil.GetBalanceList(o.get("balances"), 
                qPortfolioBase._config.BackTestingStart, qPortfolioBase._config.BackTestingEnd, 0);
            MakeList(o.get("exposures"), qPortfolioBase.Exposures, 
                qPortfolioBase._config.BackTestingStart, qPortfolioBase._config.BackTestingEnd);
            MakeList(o.get("gross_exposures"), qPortfolioBase.GrossExposures, 
                qPortfolioBase._config.BackTestingStart, qPortfolioBase._config.BackTestingEnd);
            qPortfolioBase.BalancesDaily = JsonUtil.GetDailyBalances(qPortfolioBase.BalancesHiFrequency);
            
            qPortfolioBase.PositionHistory = JsonUtil.GetPositionHistory(o.get("positions"));
            qPortfolioBase.Orders = JsonUtil.GetOrders(o.get("tradings"));
            
            // 处理交易确认
            for (JsonNode balanceJToken : o.get("tradings")) {
                JsonNode first = balanceJToken;
                if (first != null) {
                    String strTime = first.fieldNames().next();
                    int year = Integer.parseInt(strTime.substring(0, 4));
                    int month = Integer.parseInt(strTime.substring(4, 6));
                    int day = Integer.parseInt(strTime.substring(6, 8));
                    int nTime = first.get(strTime).get("nTime").asInt();
                    LocalDateTime dt = LocalDateTime.of(year, month, day, nTime / 10000, (nTime % 10000) / 100, nTime % 100);
                    TradeConfirmation tradeConfirmation = JsonUtil.GetTradeConfrmationFromJToken(first, dt);
                    
                    qPortfolioBase.TotalTransactionExpenses += tradeConfirmation.TransactionExpenses;
                    
                    CompleteOrder completeOrder = qPortfolioBase.CompleteTradeList.stream()
                        .filter(t -> t.TradeGroupIDStr.equals(tradeConfirmation.TradeGroupIDStr))
                        .findFirst()
                        .orElse(null);
                    
                    if (completeOrder == null) {
                        completeOrder = new CompleteOrder();
                        completeOrder.TradeGroupIDStr = tradeConfirmation.TradeGroupIDStr;
                        completeOrder.ParentIDStr = tradeConfirmation.ParentIDStr;
                        qPortfolioBase.CompleteTradeList.add(completeOrder);
                    }
                    
                    completeOrder.CompleteTradeList.add(tradeConfirmation); //bug:加入的tradeConfirmation的transactionExpense为0.
                    
                    try {
                        qPortfolioBase.OrderDictionary.put(tradeConfirmation.OrderId, tradeConfirmation);
                    } catch (Exception e) {
                        System.err.println("Error adding trade confirmation to order dictionary, OrderId: " + tradeConfirmation.OrderId + ", Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                    qPortfolioBase.AddTradeConfirmation(qPortfolioBase.OrderDictionary.get(tradeConfirmation.OrderId));
                }
            }
            
            qPortfolioBase._config.BackTestingStart = qPortfolioBase.BalancesDaily.firstKey().toLocalDate().atStartOfDay();
            qPortfolioBase._config.BackTestingEnd = qPortfolioBase.BalancesDaily.lastKey().toLocalDate().atStartOfDay();
            qPortfolioBase.Summary.BeginDate = qPortfolioBase._config.BackTestingStart;
            qPortfolioBase.Summary.EndDate = qPortfolioBase._config.BackTestingEnd;
            qPortfolioBase.Summary.TotalTransactionExpenses = qPortfolioBase.TotalTransactionExpenses;
            
            // 对应C#中的 JProperty j = (JProperty)o["positions"].Children().First().First;
            JsonNode firstPosition = o.get("positions").iterator().next();
            String firstPositionKey = firstPosition.fieldNames().next();
            
            qPortfolioBase.InitialCash = qPortfolioBase._config.InitialCash = qPortfolioBase.BalancesHiFrequency.firstEntry().getValue();
            
            GetBenchMarkFromJson(localPath, qPortfolioBase);
            
            SortedMap<LocalDateTime, Double> balancesDailyWithouTime = Calculator.GetBalanceDailyWithoutTime(qPortfolioBase.BalancesDaily);
            qPortfolioBase.LnOnBalances = Calculator.CalculateBalances(balancesDailyWithouTime);
            
            Calculator.CalculateCoreSummary(qPortfolioBase, balancesDailyWithouTime, qPortfolioBase.Benchmark);
            Calculator.CalculateBalanceSummary(qPortfolioBase.InitialCash, qPortfolioBase.Summary, balancesDailyWithouTime, 
                qPortfolioBase.Benchmark, qPortfolioBase.BenchmarkList, qPortfolioBase._config.BackTestingStart, 
                qPortfolioBase._config.BackTestingEnd, Calculator.GetYearInterval(qPortfolioBase));
            Calculator.SummarizeAllData(qPortfolioBase);
            Calculator.CalculateSummary(qPortfolioBase);
            
        } catch (IOException e) {
            System.err.println("Error reading portfolio JSON file: " + JsonPathName + ", Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 恢复投资组合
     */
    public static void RestorePortfolio(String localPath, String strategyName, QPortfolio qPortfolioBase) {
        qPortfolioBase._config = new PortfolioSimulationConfig();
        
        String JsonPathName = localPath + strategyName + ".json";
        
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(JsonPathName)));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode o = mapper.readTree(jsonContent);
            
            boolean isRMLongDT = isKeyLongDT(o.get("RiskMeasurement"));
            qPortfolioBase.BalancesHiFrequency = JsonUtil.GetBalanceList(o.get("RiskMeasurement"), 
                qPortfolioBase._config.BackTestingStart, qPortfolioBase._config.BackTestingEnd, 1);
            qPortfolioBase.Exposures = GetExposuresList(o.get("RiskMeasurement"), qPortfolioBase.Exposures, 
                qPortfolioBase._config.BackTestingStart, qPortfolioBase._config.BackTestingEnd);
            qPortfolioBase.GrossExposures = GetGrossExposureseList(o.get("RiskMeasurement"), qPortfolioBase.GrossExposures, 
                qPortfolioBase._config.BackTestingStart, qPortfolioBase._config.BackTestingEnd);
            
            qPortfolioBase.BalancesDaily = JsonUtil.GetDailyBalances(qPortfolioBase.BalancesHiFrequency);
            qPortfolioBase.PositionHistory = JsonUtil.GetPositionHistory(o.get("positions"));
            qPortfolioBase.Orders = JsonUtil.GetOrders(o.get("tradings"));
            
            // 处理交易确认
            for (JsonNode balanceJToken : o.get("tradings")) {
                JsonNode first = balanceJToken;
                if (first != null) {
                    String strTime = first.fieldNames().next();
                    int year = Integer.parseInt(strTime.substring(0, 4));
                    int month = Integer.parseInt(strTime.substring(4, 6));
                    int day = Integer.parseInt(strTime.substring(6, 8));
                    int nTime = first.get(strTime).get("nTime").asInt();
                    LocalDateTime dt = LocalDateTime.of(year, month, day, nTime / 10000, (nTime % 10000) / 100, nTime % 100);
                    TradeConfirmation tradeConfirmation = JsonUtil.GetTradeConfrmationFromJToken(first, dt);
                    
                    qPortfolioBase.TotalTransactionExpenses += tradeConfirmation.TransactionExpenses;
                    
                    CompleteOrder completeOrder = qPortfolioBase.CompleteTradeList.stream()
                        .filter(t -> t.TradeGroupIDStr.equals(tradeConfirmation.TradeGroupIDStr))
                        .findFirst()
                        .orElse(null);
                    
                    if (completeOrder == null) {
                        completeOrder = new CompleteOrder();
                        completeOrder.TradeGroupIDStr = tradeConfirmation.TradeGroupIDStr;
                        completeOrder.ParentIDStr = tradeConfirmation.ParentIDStr;
                        qPortfolioBase.CompleteTradeList.add(completeOrder);
                    }
                    
                    completeOrder.CompleteTradeList.add(tradeConfirmation); //bug:加入的tradeConfirmation的transactionExpense为0.
                    
                    try {
                        qPortfolioBase.OrderDictionary.put(tradeConfirmation.OrderId, tradeConfirmation);
                    } catch (Exception e) {
                        System.err.println("Error adding trade confirmation to order dictionary, OrderId: " + tradeConfirmation.OrderId + ", Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                    qPortfolioBase.AddTradeConfirmation(qPortfolioBase.OrderDictionary.get(tradeConfirmation.OrderId));
                }
            }
            
            // 处理交易组列表
            qPortfolioBase.TradeGroupList = JsonUtil.GetTradeGroups(o.get("tradeGroupList"));
            
            // 处理风险测量列表
            for (JsonNode balanceJToken : o.get("RiskMeasurement")) {
                JsonNode first = balanceJToken;
                if (first != null) {
                    String strTime = first.fieldNames().next();
                    LocalDateTime dt;
                    if (strTime.length() > 8) {
                        dt = ConvertTime(strTime.substring(0, 8))
                            .plusHours(Integer.parseInt(strTime.substring(8, 10)))
                            .plusMinutes(Integer.parseInt(strTime.substring(10, 12)))
                            .plusSeconds(Integer.parseInt(strTime.substring(12, 14)));
                    } else {
                        dt = ConvertTime(strTime);
                    }
                    
                    try {
                        ObjectMapper riskMapper = new ObjectMapper();
                        RiskMeasurement riskMeasurement = riskMapper.treeToValue(first.get(strTime), RiskMeasurement.class);
                        qPortfolioBase.RiskMeasurementList.put(dt, riskMeasurement);
                    } catch (Exception e) {
                        System.err.println("Error processing risk measurement data for time: " + strTime + ", Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            
            qPortfolioBase._config.BackTestingStart = qPortfolioBase.BalancesDaily.firstKey().toLocalDate().atStartOfDay();
            qPortfolioBase._config.BackTestingEnd = qPortfolioBase.BalancesDaily.lastKey().toLocalDate().atStartOfDay();
            qPortfolioBase.Summary.BeginDate = qPortfolioBase._config.BackTestingStart;
            qPortfolioBase.Summary.EndDate = qPortfolioBase._config.BackTestingEnd;
            qPortfolioBase.Summary.TotalTransactionExpenses = qPortfolioBase.TotalTransactionExpenses;
            
            qPortfolioBase.InitialCash = qPortfolioBase._config.InitialCash = qPortfolioBase.BalancesHiFrequency.firstEntry().getValue();
            
            GetBenchMarkFromRiskMeasurement(o.get("RiskMeasurement"), qPortfolioBase);
            
            SortedMap<LocalDateTime, Double> balancesDailyWithouTime = Calculator.GetBalanceDailyWithoutTime(qPortfolioBase.BalancesDaily);
            qPortfolioBase.LnOnBalances = Calculator.CalculateBalances(balancesDailyWithouTime);
            Calculator.CalculateCoreSummary(qPortfolioBase, balancesDailyWithouTime, qPortfolioBase.Benchmark);
            Calculator.CalculateBalanceSummary(qPortfolioBase.InitialCash, qPortfolioBase.Summary, balancesDailyWithouTime, 
                qPortfolioBase.Benchmark, qPortfolioBase.BenchmarkList, qPortfolioBase._config.BackTestingStart, 
                qPortfolioBase._config.BackTestingEnd, Calculator.GetYearInterval(qPortfolioBase));
            Calculator.SummarizeAllData(qPortfolioBase);
            Calculator.CalculateSummary(qPortfolioBase);
            
        } catch (IOException e) {
            System.err.println("Error reading portfolio JSON file: " + JsonPathName + ", Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 从风险测量中获取基准
     */
    private static void GetBenchMarkFromRiskMeasurement(Iterable<JsonNode> sourceTokens, QPortfolio qPortfolioBase) {
        SortedMap<LocalDateTime, Double> benchMarkDaily = new TreeMap<>();
        qPortfolioBase.BenchmarkList = new ArrayList<>();
        
        if (isKeyLongDT(sourceTokens)) {
            SortedMap<LocalDateTime, Double> benchMarkHiFrequency = JsonUtil.GetBenchMarkList(sourceTokens, 
                qPortfolioBase._config.BackTestingStart, qPortfolioBase._config.BackTestingEnd.plusHours(21), 1);
            benchMarkDaily = JsonUtil.GetBenchMarkDaily(benchMarkHiFrequency);
            buildBenchmark("510050", qPortfolioBase, benchMarkDaily);
        } else {
            benchMarkDaily = JsonUtil.GetBenchMarkList(sourceTokens, 
                qPortfolioBase._config.BackTestingStart, qPortfolioBase._config.BackTestingEnd, 1);
            buildBenchmark("SPY", qPortfolioBase, benchMarkDaily);
        }
    }
    
    /**
     * 构建基准
     */
    private static void buildBenchmark(String symbol, QPortfolio qPortfolioBase, SortedMap<LocalDateTime, Double> benchMarkDaily) {
        Benchmark benchmark = new Benchmark(symbol, qPortfolioBase._config.BackTestingStart, qPortfolioBase._config.BackTestingEnd);
        qPortfolioBase.BenchmarkList.add(benchmark);
        qPortfolioBase._config.BenchmarkSymbolList.add("510050");
        benchmark.benchmarkValues = benchMarkDaily;
        Calculator.GetBenchmarkBeginEnd(benchmark);
        benchmark.lnReturnList = Calculator.GetReturns(benchmark);
        qPortfolioBase.Benchmark = qPortfolioBase.BenchmarkList.get(0);
    }
    
    /**
     * 从JSON文件获取基准
     */
    private static void GetBenchMarkFromJson(String localPath, QPortfolio qPortfolioBase) {
        String BenchmarkPathName = localPath + "benchmark.json";
        
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(BenchmarkPathName)));
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode benchmarkArray = (ArrayNode) mapper.readTree(jsonContent);
            
            qPortfolioBase.BenchmarkList = new ArrayList<>();
            for (JsonNode so : benchmarkArray) {
                Benchmark benchmark = new Benchmark(so.get("name").asText(), 
                    qPortfolioBase._config.BackTestingStart, qPortfolioBase._config.BackTestingEnd);
                qPortfolioBase.BenchmarkList.add(benchmark);
                qPortfolioBase._config.BenchmarkSymbolList.add(so.get("name").asText());
                
                benchmark.benchmarkValues = new TreeMap<>();
                MakeList(so.get("balances"), benchmark.benchmarkValues, 
                    qPortfolioBase._config.BackTestingStart, qPortfolioBase._config.BackTestingEnd);
                Calculator.GetBenchmarkBeginEnd(benchmark);
                benchmark.lnReturnList = Calculator.GetReturns(benchmark);
            }
            qPortfolioBase.Benchmark = qPortfolioBase.BenchmarkList.get(0);
            
        } catch (IOException e) {
            System.err.println("Error reading benchmark JSON file: " + BenchmarkPathName + ", Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建列表
     */
    private static void MakeList(Iterable<JsonNode> sourceTokens, SortedMap<LocalDateTime, Double> dicList, 
            LocalDateTime startTime, LocalDateTime endTime) {
        for (JsonNode balanceJToken : sourceTokens) {
            JsonNode first = balanceJToken;
            if (first != null) {
                String strTime = first.fieldNames().next();
                LocalDateTime dt;
                if (strTime.length() > 8) {
                    dt = ConvertTime(strTime.substring(0, 8))
                        .plusHours(Integer.parseInt(strTime.substring(8, 10)))
                        .plusMinutes(Integer.parseInt(strTime.substring(10, 12)))
                        .plusSeconds(Integer.parseInt(strTime.substring(12, 14)));
                } else {
                    dt = ConvertTime(strTime);
                }
                
                if (startTime.isBefore(dt) && dt.isBefore(endTime)) {
                    dicList.put(dt, Double.parseDouble(first.get(strTime).asText()));
                }
            }
        }
    }
    
    public int reqIDs() {
        return ++_qNextOrderId;
    }
    
    public void AddTradeGroup(TradeGroup tradeGroup, Object transactionID) {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    public void AddTradeConfirmation(TradeConfirmation trade) {
        try {
            // Check if trade parameter is null
            if (trade == null) {
                throw new IllegalArgumentException("Trade parameter cannot be null");
            }
            
            // Ensure CompleteTradeList is initialized
            if (this.CompleteTradeList == null) {
                this.CompleteTradeList = new ArrayList<>();
            }
            
            // Ensure CompletedOrdersBySymbolDictionary is initialized
            if (this.CompletedOrdersBySymbolDictionary == null) {
                this.CompletedOrdersBySymbolDictionary = new TreeMap<>();
            }
            
            CompleteOrder completeOrder = this.CompleteTradeList.stream()
                .filter(t -> t.TradeGroupID != null && trade.TradeGroupID != null && t.TradeGroupID.equals(trade.TradeGroupID))
                .findFirst()
                .orElse(null);
                
            if (completeOrder == null) {
                completeOrder = new CompleteOrder();
                completeOrder.TradeGroupID = trade.TradeGroupID;
                completeOrder.CompleteTradeList.add(trade);
                this.CompleteTradeList.add(completeOrder);
                
                if (trade.getOrderCategory() != OrderCategory.Executable_System_PortfolioHedgingOpen &&
                    trade.getOrderCategory() != OrderCategory.Executable_System_PortfolioHedgingClose) {
                    
                    if (trade.Symbol != null && this.CompletedOrdersBySymbolDictionary.containsKey(trade.Symbol)) {
                        this.CompletedOrdersBySymbolDictionary.get(trade.Symbol).add(completeOrder);
                    } else if (trade.Symbol != null) {
                        List<CompleteOrder> tmpCompleteTradeList = new ArrayList<>();
                        tmpCompleteTradeList.add(completeOrder);
                        this.CompletedOrdersBySymbolDictionary.put(trade.Symbol, tmpCompleteTradeList);
                    }
                }
            } else {
                completeOrder.CompleteTradeList.add(trade);
            }
        } catch (Exception e) {
            System.err.println("Error in AddTradeConfirmation method:");
            System.err.println("Trade details: " + (trade != null ? 
                "Symbol=" + trade.Symbol + ", TradeGroupID=" + trade.TradeGroupID + ", OrderCategory=" + trade.getOrderCategory() : "null"));
            System.err.println("CompleteTradeList size: " + (this.CompleteTradeList != null ? this.CompleteTradeList.size() : "null"));
            System.err.println("CompletedOrdersBySymbolDictionary size: " + (this.CompletedOrdersBySymbolDictionary != null ? this.CompletedOrdersBySymbolDictionary.size() : "null"));
            System.err.println("Exception message: " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            throw new RuntimeException("AddTradeConfirmation failed", e);
        }
    }
    
    public double GetBlances(LocalDateTime dt) {
        if (BalancesDaily.containsKey(dt.toLocalDate())) {
            return BalancesDaily.get(dt.toLocalDate());
        }
        
        LocalDateTime balancedt = dt;
        while (!BalancesDaily.containsKey(balancedt.toLocalDate()) && balancedt.isAfter(LocalDateTime.of(1900, 1, 1, 0, 0))) {
            balancedt = balancedt.minusDays(1);
            if (BalancesDaily.containsKey(balancedt.toLocalDate())) {
                return BalancesDaily.get(balancedt.toLocalDate());
            }
        }
        throw new RuntimeException("Cannot find balance for the date requested.");
    }
    
    public void AddTradeConfiramtion(TradeConfirmation newTrade, LocalDateTime dt) {
        if (this.Orders.containsKey(dt)) {
            try {
                if (this.Orders.get(dt).containsKey(newTrade.Symbol)) {
                    SortedMap<String, List<TradeConfirmation>> OrderDic = this.Orders.get(dt);
                    OrderDic.get(newTrade.Symbol).add(newTrade);
                } else {
                    List<TradeConfirmation> TradeList = new ArrayList<>();
                    TradeList.add(newTrade);
                    this.Orders.get(dt).put(newTrade.Symbol, TradeList);
                }
            } catch (Exception e) {
                System.err.println("Error in AddTradeConfiramtion method, Symbol: " + newTrade.Symbol + ", Error: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            SortedMap<String, List<TradeConfirmation>> Order = new TreeMap<>();
            List<TradeConfirmation> TradeList = new ArrayList<>();
            TradeList.add(newTrade);
            Order.put(newTrade.Symbol, TradeList);
            this.Orders.put(dt, Order);
        }
    }
    
    public List<Order> ReqOpenOrders(LocalDateTime begin, LocalDateTime end) {
        List<Order> reqOrdes = new ArrayList<>();
        return reqOrdes;
    }
    
    public SortedMap<LocalDateTime, SortedMap<String, List<PositionEx>>> ReqPositions(LocalDateTime begin, LocalDateTime end) {
        LocalDateTime cur = begin;
        SortedMap<LocalDateTime, SortedMap<String, List<PositionEx>>> reqPositionList = new TreeMap<>();
        
        while (!cur.isAfter(end)) {
            if (PositionHistory.containsKey(cur)) {
                reqPositionList.put(cur, PositionHistory.get(cur));
            }
            cur = cur.plusDays(1);
        }
        return reqPositionList;
    }
} 