package com.qsystem.clientlib.JsonModel;

import java.util.SortedMap;
import java.util.TreeMap;

public class TagDescriptor {
    public String TagNameA;
    public int StartDate;
    public int EndDate;
    public int expectedStartDate;
    public int expectedEndDate;
    public long DataPointCount;
    public String[] keywords;

    public int multiplier;
    public SortedMap<String, Integer> IndexPaths; // key is page ID (20160104_20160123), value is optionIndex.
} 