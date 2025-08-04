package com.qsystem.clientlib;

import java.time.LocalDateTime;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.qsystem.clientlib.JsonModel.TradeConfirmation;
import com.qsystem.clientlib.JsonModel.TradeGroup;
import com.qsystem.clientlib.JsonModel.RiskMeasurement;
import com.qsystem.clientlib.JsonModel.BOption;
import com.qsystem.clientlib.JsonModel.BOrder;
import com.qsystem.clientlib.JsonModel.BOptionOrder;
import com.qsystem.clientlib.JsonModel.OrderSymbolType;
import com.qsystem.clientlib.PositionEx;
import com.qsystem.clientlib.Position;
import com.qsystem.clientlib.PositionExtention;
import com.qsystem.clientlib.OptionPositionExtention;

/**
 * JSON工具类 - 对应C#版本的JsonUtil类
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static BalanceList GetBalanceList(Object sourceTokens, LocalDateTime startTime, LocalDateTime endTime, int type) {
        BalanceList dicList = new BalanceList();
        
        if (sourceTokens instanceof JsonNode) {
            JsonNode tokens = (JsonNode) sourceTokens;
            Iterator<JsonNode> iterator = tokens.iterator();
            
            while (iterator.hasNext()) {
                JsonNode balanceJToken = iterator.next();
                // 对应C#的 JProperty balanceJP = (JProperty)balanceJToken.First;
                // 在Java中，balanceJToken本身就是对象，我们需要获取其第一个字段
                JsonNode balanceJP = balanceJToken;
                // 对应C#的 String strTime = balanceJP.Name;
                String strTime = balanceJP.fieldNames().next();
                LocalDateTime dt;
                
                if (strTime.length() > 8) {
                    dt = ConvertTime(strTime.substring(0, 8))
                        .plusHours(Integer.parseInt(strTime.substring(8, 10)))
                        .plusMinutes(Integer.parseInt(strTime.substring(10, 12)))
                        .plusSeconds(Integer.parseInt(strTime.substring(12, 14)));
                } else {
                    dt = ConvertTime(strTime);
                }

                if (startTime.isBefore(dt) && dt.isBefore(endTime) || dt.isEqual(endTime)) {
                    try {
                        if (type == 1) {
                            RiskMeasurement riskMeasurement = objectMapper.treeToValue(balanceJP.get(strTime), RiskMeasurement.class);
                            dicList.put(dt, riskMeasurement.balance);
                        } else {
                            dicList.put(dt, Double.parseDouble(balanceJP.get(strTime).asText()));
                        }
                    } catch (Exception e) {
                        String a = e.getMessage();
                    }
                }
            }
        }
        
        return dicList;
    }
    
    public static SortedMap<LocalDateTime, Double> GetBenchMarkList(Object sourceTokens, LocalDateTime startTime, LocalDateTime endTime, int type) {
        SortedMap<LocalDateTime, Double> dicList = new TreeMap<>();
        
        if (sourceTokens instanceof JsonNode) {
            JsonNode tokens = (JsonNode) sourceTokens;
            Iterator<JsonNode> iterator = tokens.iterator();
            
            while (iterator.hasNext()) {
                JsonNode balanceJToken = iterator.next();
                // 对应C#的 JProperty balanceJP = (JProperty)balanceJToken.First;
                // 在Java中，balanceJToken本身就是对象，我们需要获取其第一个字段
                JsonNode balanceJP = balanceJToken;
                // 对应C#的 String strTime = balanceJP.Name;
                String strTime = balanceJP.fieldNames().next();
                LocalDateTime dt;
                
                if (strTime.length() > 8) {
                    dt = ConvertTime(strTime.substring(0, 8))
                        .plusHours(Integer.parseInt(strTime.substring(8, 10)))
                        .plusMinutes(Integer.parseInt(strTime.substring(10, 12)))
                        .plusSeconds(Integer.parseInt(strTime.substring(12, 14)));
                } else {
                    dt = ConvertTime(strTime);
                }

                if (startTime.isBefore(dt) && dt.isBefore(endTime) || dt.isEqual(endTime)) {
                    try {
                        if (type == 1) {
                            RiskMeasurement riskMeasurement = objectMapper.treeToValue(balanceJP.get(strTime), RiskMeasurement.class);
                            dicList.put(dt, riskMeasurement.benchMark);
                        } else {
                            dicList.put(dt, Double.parseDouble(balanceJP.get(strTime).asText()));
                        }
                    } catch (Exception e) {
                        String a = e.getMessage();
                    }
                }
            }
        }
        
        return dicList;
    }
    
    public static TradeConfirmation GetTradeConfrmationFromJToken(Object balanceJP, LocalDateTime dt) {
        TradeConfirmation tradeConfirmation = new TradeConfirmation();
        
        if (balanceJP instanceof JsonNode) {
            JsonNode balanceJPNode = (JsonNode) balanceJP;
            // 获取第一个字段名（对应C#的balanceJP.Name）
            String fieldName = balanceJPNode.fieldNames().next();
            // 获取对应的值（对应C#的balanceJP.First）
            JsonNode balanceJPValue = balanceJPNode.get(fieldName);
            JsonNode instrumentNode = balanceJPValue.get("instrument");
            
            if (instrumentNode != null) {
                try {
                    BOption option = objectMapper.treeToValue(instrumentNode, BOption.class);
                    
                    tradeConfirmation.TimeStamp = dt;
                    tradeConfirmation.OrderTime = dt;

                    if (option.expiration == 0) {
                        BOrder order = objectMapper.treeToValue(balanceJPValue, BOrder.class);
                        tradeConfirmation.Symbol = option.symbol;
                        tradeConfirmation.SymbolType = OrderSymbolType.Stock;
                        tradeConfirmation.OrderType = order.orderType;
                        tradeConfirmation.Price = order.price;
                        tradeConfirmation.Quantity = order.quantity;
                        tradeConfirmation.Underlying = order.price;
                        tradeConfirmation.Note = order.note;
                        tradeConfirmation.TradeGroupIDStr = order.tradeGroupID;
                        tradeConfirmation.OrderId = order.orderID;
                        tradeConfirmation.ParentIDStr = order.parentID;
                        tradeConfirmation.Multiplier = option.multiplier;
                        tradeConfirmation.TransactionExpenses = order.transactionExpenses;
                    } else {
                        BOptionOrder optionOrder = objectMapper.treeToValue(balanceJPValue, BOptionOrder.class);
                        
                        tradeConfirmation.UnderlyingSymbol = optionOrder.underlyingSymbol;
                        tradeConfirmation.Symbol = option.toString();
                        tradeConfirmation.SymbolType = OrderSymbolType.Option;
                        tradeConfirmation.OrderType = optionOrder.orderType;
                        tradeConfirmation.Price = optionOrder.price;
                        tradeConfirmation.Note = optionOrder.note;
                        tradeConfirmation.Quantity = optionOrder.quantity;
                        tradeConfirmation.Underlying = optionOrder.underlyingPrice;
                        tradeConfirmation.ExecutionTime = ConvertTime(String.valueOf(option.expiration));
                        tradeConfirmation.OrderId = optionOrder.orderID;
                        tradeConfirmation.TradeGroupIDStr = optionOrder.tradeGroupID;
                        tradeConfirmation.ParentIDStr = optionOrder.parentID;
                        tradeConfirmation.Multiplier = option.multiplier;
                        tradeConfirmation.TransactionExpenses = optionOrder.transactionExpenses;
                    }
                } catch (Exception e) {
                    System.err.println("=== DETAILED EXCEPTION INFORMATION ===");
                    System.err.println("Exception Type: " + e.getClass().getName());
                    System.err.println("Exception Message: " + e.getMessage());
                    System.err.println("Exception Stack Trace:");
                    e.printStackTrace(System.err);
                    
                    // Print additional details for specific exception types
                    if (e instanceof com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException) {
                        com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException upe = 
                            (com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException) e;
                        System.err.println("Unrecognized Property: " + upe.getPropertyName());
                        System.err.println("Known Properties: " + upe.getKnownPropertyIds());
                        System.err.println("Location: " + upe.getLocation());
                    }
                    
                    if (e instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException) {
                        com.fasterxml.jackson.databind.exc.MismatchedInputException mie = 
                            (com.fasterxml.jackson.databind.exc.MismatchedInputException) e;
                        System.err.println("Mismatched Input Type: " + mie.getTargetType());
                        System.err.println("Location: " + mie.getLocation());
                    }
                    
                    System.err.println("=== END EXCEPTION DETAILS ===");
                    throw new RuntimeException(e);
                }
            } 
        }
        
        return tradeConfirmation;
    }
    
    static BalanceDailyList CopyBalances(BalanceList balancesHiFrequency) {
        BalanceDailyList balanceDaily = new BalanceDailyList();
        for (LocalDateTime key : balancesHiFrequency.keySet()) {
            balanceDaily.put(key, balancesHiFrequency.get(key));
        }
        return balanceDaily;
    }
    
    public static TradeList GetOrders(Object jEnumerable) {
        TradeList orders = new TradeList();
        
        if (jEnumerable instanceof JsonNode) {
            JsonNode tokens = (JsonNode) jEnumerable;
            Iterator<JsonNode> iterator = tokens.iterator();
            
            while (iterator.hasNext()) {
                JsonNode balanceJToken = iterator.next();
                // 对应C#的 JProperty balanceJP = (JProperty)balanceJToken.First;
                // 在Java中，balanceJToken本身就是对象，我们需要获取其第一个字段
                JsonNode balanceJP = balanceJToken;
                // 对应C#的 String strTime = balanceJP.Name;
                String timeStr = balanceJP.fieldNames().next();
                
                int year = Integer.parseInt(timeStr.substring(0, 4));
                int month = Integer.parseInt(timeStr.substring(4, 6));
                int day = Integer.parseInt(timeStr.substring(6, 8));
                int nTime = balanceJP.get(timeStr).get("nTime").asInt();
                
                LocalDateTime dt = LocalDateTime.of(year, month, day, 
                    nTime / 10000, (nTime % 10000) / 100, nTime % 100);
                
                TradeConfirmation tradeConfirmation = GetTradeConfrmationFromJToken(balanceJP, dt);

                if (orders.containsKey(dt)) {
                    try {
                        SortedMap<String, List<TradeConfirmation>> orderDic = orders.get(dt);
                        if (orderDic.containsKey(tradeConfirmation.Symbol)) {
                            orderDic.get(tradeConfirmation.Symbol).add(tradeConfirmation);
                        } else {
                            List<TradeConfirmation> tradeList = new ArrayList<>();
                            tradeList.add(tradeConfirmation);
                            orderDic.put(tradeConfirmation.Symbol, tradeList);
                        }
                    } catch (Exception e) {
                        System.err.println("=== DETAILED EXCEPTION INFORMATION (GetOrders) ===");
                        System.err.println("Exception Type: " + e.getClass().getName());
                        System.err.println("Exception Message: " + e.getMessage());
                        System.err.println("Exception Stack Trace:");
                        e.printStackTrace(System.err);
                        
                        // Print additional details for specific exception types
                        if (e instanceof com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException) {
                            com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException upe = 
                                (com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException) e;
                            System.err.println("Unrecognized Property: " + upe.getPropertyName());
                            System.err.println("Known Properties: " + upe.getKnownPropertyIds());
                            System.err.println("Location: " + upe.getLocation());
                        }
                        
                        if (e instanceof com.fasterxml.jackson.databind.exc.MismatchedInputException) {
                            com.fasterxml.jackson.databind.exc.MismatchedInputException mie = 
                                (com.fasterxml.jackson.databind.exc.MismatchedInputException) e;
                            System.err.println("Mismatched Input Type: " + mie.getTargetType());
                            System.err.println("Location: " + mie.getLocation());
                        }
                        
                        if (e instanceof java.lang.NumberFormatException) {
                            java.lang.NumberFormatException nfe = (java.lang.NumberFormatException) e;
                            System.err.println("Number Format Error: " + nfe.getMessage());
                        }
                        
                        if (e instanceof java.lang.StringIndexOutOfBoundsException) {
                            java.lang.StringIndexOutOfBoundsException sioobe = (java.lang.StringIndexOutOfBoundsException) e;
                            System.err.println("String Index Error: " + sioobe.getMessage());
                        }
                        
                        System.err.println("=== END EXCEPTION DETAILS (GetOrders) ===");
                        throw new RuntimeException(e);
                    }
                } else {
                    SortedMap<String, List<TradeConfirmation>> order = new TreeMap<>();
                    List<TradeConfirmation> tradeList = new ArrayList<>();
                    tradeList.add(tradeConfirmation);
                    order.put(tradeConfirmation.Symbol, tradeList);
                    orders.put(dt, order);
                }
            }
        }
        
        return orders;
    }
    
    public static PositionHistoryList GetPositionHistory(Object jEnumerable) {
        PositionHistoryList positionHistory = new PositionHistoryList();
        
        if (jEnumerable instanceof JsonNode) {
            JsonNode tokens = (JsonNode) jEnumerable;
            Iterator<JsonNode> iterator = tokens.iterator();
            
            while (iterator.hasNext()) {
                JsonNode balanceJToken = iterator.next();
                // 对应C#的 JProperty balanceJP = (JProperty)balanceJToken.First;
                // 在Java中，balanceJToken本身就是对象，我们需要获取其第一个字段
                JsonNode balanceJP = balanceJToken;
                // 对应C#的 String strTime = balanceJP.Name;
                String strTime = balanceJP.fieldNames().next();
                LocalDateTime dt;
                
                if (strTime.length() > 8) {
                    dt = ConvertTime(strTime.substring(0, 8))
                        .plusHours(Integer.parseInt(strTime.substring(8, 10)))
                        .plusMinutes(Integer.parseInt(strTime.substring(10, 12)))
                        .plusSeconds(Integer.parseInt(strTime.substring(12, 14)));
                } else {
                    dt = ConvertTime(strTime);
                }
                
                SortedMap<String, List<PositionEx>> newPositionList = new TreeMap<>();
                // 对应C#的 balanceJToken.First.First，在Java中直接使用balanceJToken
                JsonNode positionsNode = balanceJToken.get(strTime);
                
                if (positionsNode != null) {
                    Iterator<Map.Entry<String, JsonNode>> fields = positionsNode.fields();
                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> entry = fields.next();
                        String posName = entry.getKey();
                        JsonNode posValue = entry.getValue();
                        
                        if (posName.equals("money")) {
                            List<PositionEx> positionList = new ArrayList<>();
                            PositionEx newp = new PositionEx();
                            newp.position = new Position();
                            newp.position.Symbol = posName;
                            newp.position.Quantity = posValue.asDouble();
                            newp.position.TimeStamp = dt;
                            newp.positionExtention = new PositionExtention();
                            positionList.add(newp);
                            newPositionList.put("money", positionList);
                        } else {
                            List<PositionEx> positionList = new ArrayList<>();
                            PositionEx newp = new PositionEx();
                            newp.position = new Position();
                            newp.position.Symbol = posName;
                            String[] arrayOptionName = posName.split("_");
                            
                            if (arrayOptionName.length != 1) {
                                newp.position.SymbolType = OrderSymbolType.Option;
                                newp.positionExtention = new OptionPositionExtention();
                                newp.positionExtention.Ask = posValue.get("ask").asDouble();
                                newp.positionExtention.Bid = posValue.get("bid").asDouble();
                                newp.position.Delta = posValue.get("delta").asDouble();
                                newp.position.Theta = posValue.get("theta").asDouble();
                                newp.position.Vega = posValue.get("vega").asDouble();
                                newp.positionExtention.BetaDeltaDollar = posValue.get("beta_delta_dollar").asDouble();
                                newp.positionExtention.Underlying = posValue.get("underlying_price").asDouble();
                                newp.positionExtention.ThetaDollar = posValue.get("thetaDollar").asDouble();
                                newp.positionExtention.VegaDollar = posValue.get("vegaDollar").asDouble();
                                newp.positionExtention.MaintenanceMargin = posValue.get("maintenance_margin").asDouble();
                            } else {
                                newp.position.SymbolType = OrderSymbolType.Stock;
                                newp.positionExtention = new PositionExtention();
                                newp.positionExtention.Close = posValue.get("close").asDouble();
                                newp.positionExtention.AdjClose = posValue.get("adj_close").asDouble();
                                newp.positionExtention.BetaDeltaDollar = posValue.get("beta_delta_dollar").asDouble();
                            }
                            newp.position.TimeStamp = dt;
                            newp.position.PriceEntered = posValue.get("price").asDouble();
                            newp.position.Quantity = posValue.get("quantity").asDouble();
                            positionList.add(newp);
                            newPositionList.put(posName, positionList);
                        }
                    }
                }
                
                if (positionHistory.containsKey(dt)) {
                    positionHistory.put(dt, newPositionList);
                } else {
                    positionHistory.put(dt, newPositionList);
                }
            }
        }
        
        return positionHistory;
    }
    
    public static SortedMap<String, TradeGroup> GetTradeGroups(Object jEnumerables) {
        SortedMap<String, TradeGroup> tradeGroupList = new TreeMap<>();
        
        if (jEnumerables instanceof JsonNode) {
            JsonNode tokens = (JsonNode) jEnumerables;
            Iterator<JsonNode> iterator = tokens.iterator();
            
            while (iterator.hasNext()) {
                JsonNode balanceJToken = iterator.next();
                // 对应C#的 JProperty balanceJP = (JProperty)balanceJToken.First;
                // 在Java中，balanceJToken本身就是对象，我们需要获取其第一个字段
                JsonNode balanceJP = balanceJToken;
                // 对应C#的 String strTime = balanceJP.Name;
                String fieldName = balanceJP.fieldNames().next();
                // 对应C#的 string transactionID = balanceJP.First["transactionID"].ToObject<string>();
                String transactionID = balanceJP.get(fieldName).get("transactionID").asText();
                try {
                    // 对应C#的 TradeGroup tradeGroup = balanceJP.First.ToObject<TradeGroup>();
                    TradeGroup tradeGroup = objectMapper.treeToValue(balanceJP.get(fieldName), TradeGroup.class);
                    // 对应C#的 tradeGroupList.Add(transactionID, tradeGroup);
                    tradeGroupList.put(transactionID, tradeGroup);
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        return tradeGroupList;
    }
    
    public static BalanceDailyList GetDailyBalances(BalanceList balancesHiFrequency) {
        BalanceDailyList balances = new BalanceDailyList();
        
        // 如果时分秒都为0，代表json文件中其实日期没有时分等数据，直接把balancesHiFrequency复制过去，然后返回。
        for (LocalDateTime key : balancesHiFrequency.keySet()) {
            if (key.getHour() == 0 && key.getMinute() == 0) {
                balances = CopyBalances(balancesHiFrequency);
                return balances;
            }
        }

        // 如果某一天里面 ,存在balancesHiFrequency 里面存在一个或几个值,但是呢,这一个或者几个值没有
        // 包含3:00. 那么逻辑是,把3:00 以前的,最后时间的一条balance 加上. 
        
        LocalDateTime lastestDT = balancesHiFrequency.keySet().toArray(new LocalDateTime[0])[balancesHiFrequency.size() - 1];
        
        // 两个loop .第一个 生成一个  SortedMap<LocalDateTime.Day, List<BalanceList>>
        SortedMap<LocalDateTime, BalanceList> balanceListDaily = new TreeMap<>();
        
        LocalDateTime daily = balancesHiFrequency.keySet().toArray(new LocalDateTime[0])[0].toLocalDate().atStartOfDay();
        
        for (LocalDateTime key : balancesHiFrequency.keySet()) {
            daily = key.toLocalDate().atStartOfDay();
            if (balanceListDaily.containsKey(daily)) {
                BalanceList balanceList = balanceListDaily.get(daily);
                balanceList.put(key, balancesHiFrequency.get(key));
            } else {
                BalanceList balanceList = new BalanceList();
                balanceListDaily.put(daily, balanceList);
                balanceList.put(key, balancesHiFrequency.get(key));
            }
        }

        for (LocalDateTime key : balanceListDaily.keySet()) {
            daily = key;
            BalanceList balanceList = balanceListDaily.get(daily);
            boolean contains3PM = false;
            LocalDateTime latest_key = balanceList.keySet().toArray(new LocalDateTime[0])[0];
            
            for (LocalDateTime key2 : balanceList.keySet()) {
                if (key2.getHour() == 15 && key2.getMinute() == 0 && balancesHiFrequency.get(key2) != 0) {
                    contains3PM = true;
                    balances.put(key2, balancesHiFrequency.get(key2));
                    break;
                } else {
                    if (key2.getHour() < 15 && latest_key.isBefore(key2) && balancesHiFrequency.get(key2) != 0) {
                        latest_key = key2;
                    }
                }
            }
            if (!contains3PM && balancesHiFrequency.get(latest_key) != 0) {
                balances.put(latest_key, balanceList.get(latest_key));
            }
        }
        return balances;
    }
    
    public static SortedMap<LocalDateTime, Double> GetBenchMarkDaily(SortedMap<LocalDateTime, Double> benchMarkHiFrequency) {
        // 参考GetBalances获得每天的benchMarkListDaily
        // 如果某一天里面 ,存在benchMarkHiFrequency 里面存在一个或几个值,但是呢,这一个或者几个值没有
        // 包含3:00. 那么逻辑是,把3:00 以前的,最后时间的一条balance 加上. 
        
        SortedMap<LocalDateTime, Double> benchMark = new TreeMap<>();
        LocalDateTime lastestDT = benchMarkHiFrequency.keySet().toArray(new LocalDateTime[0])[benchMarkHiFrequency.size() - 1];

        // 两个loop .第一个 生成一个  SortedMap<LocalDateTime.Day, List< SortedMap<LocalDateTime, Double>>>
        SortedMap<LocalDateTime, SortedMap<LocalDateTime, Double>> benchMarkListDaily = new TreeMap<>();

        LocalDateTime daily = benchMarkHiFrequency.keySet().toArray(new LocalDateTime[0])[0].toLocalDate().atStartOfDay();

        for (LocalDateTime key : benchMarkHiFrequency.keySet()) {
            daily = key.toLocalDate().atStartOfDay();
            if (benchMarkListDaily.containsKey(daily)) {
                SortedMap<LocalDateTime, Double> benchMarkList = benchMarkListDaily.get(daily);
                benchMarkList.put(key, benchMarkHiFrequency.get(key));
            } else {
                SortedMap<LocalDateTime, Double> benchMarkList = new TreeMap<>();
                benchMarkListDaily.put(daily, benchMarkList);
                benchMarkList.put(key, benchMarkHiFrequency.get(key));
            }
        }

        for (LocalDateTime key : benchMarkListDaily.keySet()) {
            daily = key.toLocalDate().atStartOfDay();
            SortedMap<LocalDateTime, Double> benchMarkList = benchMarkListDaily.get(daily);
            boolean contains3PM = false;
            LocalDateTime latest_day = benchMarkList.keySet().toArray(new LocalDateTime[0])[0];
            double lastAvaiableBenchMark = 0;
            
            for (LocalDateTime key2 : benchMarkList.keySet()) {
                if (benchMarkHiFrequency.get(key2) != 88888888.000 && benchMarkHiFrequency.get(key2) != 0) {
                    lastAvaiableBenchMark = benchMarkHiFrequency.get(key2);
                }

                if (key2.getHour() == 15 && key2.getMinute() == 0 && benchMarkHiFrequency.get(key2) != 0) {
                    contains3PM = true;
                    if (benchMarkHiFrequency.get(key2) != 88888888.000 && benchMarkHiFrequency.get(key2) != 0) {
                        benchMark.put(key2.toLocalDate().atStartOfDay(), benchMarkHiFrequency.get(key2));
                        break;
                    } else {
                        benchMark.put(key2.toLocalDate().atStartOfDay(), lastAvaiableBenchMark);
                        break;
                    }
                } else {
                    if (key2.getHour() < 15 && latest_day.isBefore(key2) && benchMarkHiFrequency.get(key2) != 0 && benchMarkHiFrequency.get(key2) != 88888888) {
                        latest_day = key2;
                    }
                }
            }
            if (!contains3PM && benchMarkHiFrequency.get(latest_day) != 0) {
                benchMark.put(latest_day.toLocalDate().atStartOfDay(), benchMarkHiFrequency.get(latest_day));
            }
        }
        return benchMark;
    }

    private static LocalDateTime ConvertTime(String str_time) {
        int year = Integer.parseInt(str_time.substring(0, 4));
        int month = Integer.parseInt(str_time.substring(4, 6));
        int day = Integer.parseInt(str_time.substring(6, 8));

        return LocalDateTime.of(year, month, day, 0, 0);
    }
} 