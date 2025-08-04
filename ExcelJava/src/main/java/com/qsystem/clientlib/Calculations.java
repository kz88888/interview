package com.qsystem.clientlib;

import java.util.List;
import java.util.Queue;

/**
 * 简单计算类
 */
public class Calculations {
    
    /**
     * 计算Ln值
     */
    public static double Calculate_LN(double currentAdjClose, double formerAdjClose) {
        return Math.log(currentAdjClose / formerAdjClose);
    }
    
    public static double Calculate_Flr(double currentAdjClose, double nextAdjClose) {
        return nextAdjClose / currentAdjClose - 1;
    }
    
    /**
     * 从Ln值列表计算波动率
     */
    public static double Calculate_Volatility(Queue<Double> lnValueList) {
        double powSum = 0;
        double sum = 0;
        double sumPow = 0;
        
        double currentValue = 0;
        Object[] valueArray = lnValueList.toArray();
        int n = valueArray.length;
        
        for (int i = 0; i < n; i++) {
            currentValue = Double.parseDouble(valueArray[i].toString());
            powSum += Math.pow(currentValue, 2);
            sum += currentValue;
        }
        
        sumPow = Math.pow(sum, 2);
        
        double tempValue = n * powSum - sumPow;
        tempValue = tempValue / (n * (n - 1));
        
        return Math.sqrt(tempValue);
    }
    
    /**
     * 计算标准化波动率
     */
    public static double Calculate_NormalizedVolatility(Queue<Double> volatilityValueList) {
        double standardDeviation = Calculate_Volatility(volatilityValueList);
        
        Object[] valueArray = volatilityValueList.toArray();
        double sum = 0;
        for (int i = 0; i < valueArray.length; i++) {
            sum += Double.parseDouble(valueArray[i].toString());
        }
        double average = sum / valueArray.length;
        double lastValue = Double.parseDouble(valueArray[valueArray.length - 1].toString());
        
        return (lastValue - average) / standardDeviation;
    }
    
    public static double CalculateAverage(Queue<Double> valueList) {
        double sum = 0;
        for (Double value : valueList) {
            sum += value;
        }
        
        return sum / valueList.size();
    }
    
    public static double CalculateVAR(List<Double> valueList) {
        double powSum = 0;
        double sum = 0;
        int count = 0;
        
        for (Double value : valueList) {
            if (!Double.isNaN(value)) {
                powSum += Math.pow(value, 2);
                sum += value;
                count++;
            }
        }
        
        double varValue = (count * powSum - Math.pow(sum, 2)) / (count * (count - 1));
        
        return varValue;
    }
    
    private static double CalculateAverage(List<Double> valueList) {
        double sum = 0;
        int count = 0;
        
        for (Double value : valueList) {
            if (!Double.isNaN(value)) {
                sum += value;
                count++;
            }
        }
        
        return sum / count;
    }
    
    public static double CalculateCOVAR(List<Double> xValueList, List<Double> yValueList) {
        double xAverage = CalculateAverage(xValueList);
        double yAverage = CalculateAverage(yValueList);
        
        double sum = 0;
        double count = 0;
        
        for (int i = 0; i < xValueList.size(); i++) {
            if (!Double.isNaN(xValueList.get(i)) && !Double.isNaN(yValueList.get(i))) {
                count++;
                sum += (xValueList.get(i) - xAverage) * (yValueList.get(i) - yAverage);
            }
        }
        
        return sum / count;
    }
    
    public static double GetSigma(List<Double> valueList) {
        double powSum = 0;
        double sum = 0;
        double sumPow = 0;
        
        double currentValue = 0;
        int n = valueList.size();
        int uselessValueCount = 0;
        
        for (int i = 0; i < n; i++) {
            if (!Double.isNaN(valueList.get(i))) {
                currentValue = valueList.get(i);
                powSum += Math.pow(currentValue, 2);
                sum += currentValue;
            } else {
                uselessValueCount++;
            }
        }
        
        n = n - uselessValueCount;
        sumPow = Math.pow(sum, 2);
        
        double tempValue = n * powSum - sumPow;
        tempValue = tempValue / (n * (n - 1));
        
        return Math.sqrt(tempValue);
    }
    
    public static double GetAverage(List<Double> flrValueList) {
        double sum = 0;
        int count = 0;
        
        for (Double value : flrValueList) {
            if (!Double.isNaN(value)) {
                sum += value;
                count++;
            }
        }
        
        return sum / count;
    }
} 