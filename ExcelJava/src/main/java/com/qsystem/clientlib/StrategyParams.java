package com.qsystem.clientlib;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;

/**
 * 策略参数类
 */
public class StrategyParams {
    public String name;
    public SortedMap<String, String> paramsPair = new TreeMap<>();
    
    public static SortedMap<String, String> restoreParams(String fileName) {
        SortedMap<String, String> paramsPair = new TreeMap<>();
        if (fileName == null) {
            throw new IllegalArgumentException("fileName cannot be null");
        }
        
        String[] fileNameSplit = fileName.split("\\.");
        int indexParams = fileNameSplit.length - 1;
        if (indexParams != 0) {
            // 文件名中参数组合不为空
            String paramsTotal = fileNameSplit[indexParams];
            
            char[] numberSeparator = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
            
            // 按数字分隔符分割，移除空条目
            List<String> paramsNameList = new ArrayList<>();
            String[] tempNames = paramsTotal.split("[0123456789-]");
            for (String name : tempNames) {
                if (!name.isEmpty()) {
                    paramsNameList.add(name);
                }
            }
            
            // 按参数名分割，移除空条目
            List<String> paramsValueList = new ArrayList<>();
            String tempTotal = paramsTotal;
            for (String paramName : paramsNameList) {
                String[] parts = tempTotal.split(paramName, 2);
                if (parts.length > 1) {
                    // 提取参数名后面的数字部分
                    String remaining = parts[1];
                    StringBuilder value = new StringBuilder();
                    for (char c : remaining.toCharArray()) {
                        if (Character.isDigit(c) || c == '-') {
                            value.append(c);
                        } else {
                            break;
                        }
                    }
                    paramsValueList.add(value.toString());
                    tempTotal = remaining.substring(value.length());
                } else {
                    paramsValueList.add("");
                }
            }
            
            // 添加参数对
            for (int i = 0; i < paramsNameList.size() && i < paramsValueList.size(); i++) {
                paramsPair.put(paramsNameList.get(i), paramsValueList.get(i));
            }
        }
        
        return paramsPair;
    }
} 