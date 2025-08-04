package com.qsystem.clientlib;

import java.util.List;
import java.util.ArrayList;


/**
 * 对冲组类
 */
public class HedgeGroup {
    public String symbol;
    public List<String> OptonSymbolList = new ArrayList<>();
    public List<Integer> HedgeOpenList = new ArrayList<>();
    public boolean isHedging = false;
} 