package com.qsystem.clientlib;

import java.time.LocalDateTime;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import com.qsystem.clientlib.JsonModel.TradeConfirmation;

/**
 * 交易列表
 */
public class TradeList extends TreeMap<LocalDateTime, SortedMap<String, List<TradeConfirmation>>> {
    // 继承TreeMap，保持与C#版本一致的功能
} 