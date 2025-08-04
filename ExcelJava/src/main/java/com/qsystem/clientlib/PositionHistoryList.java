package com.qsystem.clientlib;

import java.time.LocalDateTime;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 持仓历史列表
 */
public class PositionHistoryList extends TreeMap<LocalDateTime, SortedMap<String, List<PositionEx>>> {
    // 继承TreeMap，保持与C#版本一致的功能
} 