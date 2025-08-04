package com.qsystem.clientlib;

import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 收益分布扩展类
 */
public class ReturnDistributionEx {
    
    public static void CalcReturnDistributionAllData(ReturnDistribution returnDistribution, List<Double> valueList) {
        Collections.sort(valueList);
        List<Double> valueListLessThan1 = new java.util.ArrayList<>();
        List<Double> valueListMoreThan1 = new java.util.ArrayList<>();
        double s = 0;
        
        for (int i = 0; i < valueList.size(); ++i) {
            double value = valueList.get(i);
            s += (value - returnDistribution.WinLossRangeAverage) * (value - returnDistribution.WinLossRangeAverage);
            
            if (value < -1) {
                // valueListLessThan1.add(value);
                // valueList.remove(i);
            }
            if (value > 1) {
                // valueListMoreThan1.add(value);
                // valueList.remove(i);
            }
        }
        
        returnDistribution.StandardDeviation = Math.sqrt(s / (valueList.size() - 1));
        
        if (valueListLessThan1.size() > 0) {
            returnDistribution.DistributionDictionary.put(-1.0, valueListLessThan1.size());
        }
        
        if (valueListMoreThan1.size() > 0) {
            returnDistribution.DistributionDictionary.put(1.0, valueListMoreThan1.size());
        }
        
        returnDistribution.returnDistributionList = valueList;
        returnDistribution.SpreadCount = GetSpreadCount(valueList);
        returnDistribution.Range = GetRange(valueList, returnDistribution.SpreadCount);
        GetDistributionDictionary(returnDistribution, returnDistribution.returnDistributionList, returnDistribution.SpreadCount);
        
        CalcMaxDownAndNumberOfSample(returnDistribution.returnDistributionList, returnDistribution);
        CalcNumberOfSample2(returnDistribution.returnDistributionList, returnDistribution);
        CalcNumberOfSample3(returnDistribution.returnDistributionList, returnDistribution);
    }
    
    public static void CalcMaxDownAndNumberOfSample(List<Double> returnList, ReturnDistribution returnDistribution) {
        for (Double value : returnList) {
            if (value < -0.05) {
                returnDistribution.NumberOfSampleCount++;
            }
            
            if (value < 0) {
                if (Double.isNaN(returnDistribution.MaxDown)) {
                    returnDistribution.MaxDown = value;
                } else {
                    if (returnDistribution.MaxDown > value) {
                        returnDistribution.MaxDown = value;
                    }
                }
            }
        }
    }
    
    public static void CalcNumberOfSample2(List<Double> returnList, ReturnDistribution returnDistribution) {
        for (Double value : returnList) {
            if (value < -0.025) {
                returnDistribution.NumberOfSampleCount0025++;
            }
        }
    }
    
    public static void CalcNumberOfSample3(List<Double> returnList, ReturnDistribution returnDistribution) {
        for (Double value : returnList) {
            if (value < -0.01) {
                returnDistribution.NumberOfSampleCount001++;
            }
        }
    }
    
    public static void CalcWinsAndLosses(ReturnDistribution returnDistribution, List<Double> profitValueList, boolean flag) {
        int orderNumber = profitValueList.size();
        int numberOfOrderLose = 0;
        for (Double value : profitValueList) {
            if (value < 0) {
                numberOfOrderLose++;
            }
        }
        returnDistribution.OrderNumber = orderNumber;
        returnDistribution.NumberOfOrderLose = numberOfOrderLose;
        returnDistribution.NumberOfOrderWin = orderNumber - numberOfOrderLose;
        if (orderNumber > 0) {
            returnDistribution.WinRatio = (double) returnDistribution.NumberOfOrderWin / orderNumber;
        } else {
            returnDistribution.WinRatio = 0.0;
        }
    }
    
    public static double GetRange(List<Double> valueList, int spreadCount) {
        double max = valueList.get(valueList.size() - 1);
        double min = valueList.get(0);
        double range = (max - min) / spreadCount;
        return range;
    }
    
    public static int GetSpreadCount(List<Double> valueList) {
        int spreadCount = 0;
        
        if (valueList.size() > 1000) {
            spreadCount = 100;
        } else if (valueList.size() > 0) {
            spreadCount = valueList.size() / 10;
        }
        
        if (spreadCount < 10) {
            spreadCount = 10;
        } else if (spreadCount > 100) {
            spreadCount = 100;
        }
        
        return spreadCount;
    }
    
    public static void GetDistributionDictionary(ReturnDistribution returnDistribution, List<Double> valueList, int spreadCount) {
        SortedMap<Double, Integer> distributionDictionary = returnDistribution.DistributionDictionary;
        if (valueList.size() > 1) {
            double max = valueList.get(valueList.size() - 1);
            double min = valueList.get(0);
            double range = (max - min) / spreadCount;
            if (range == 0) {
                return;
            }
            for (int indx = 0; indx < spreadCount; ++indx) {
                double lowerLimitValue;
                double upperLimitValue;
                
                lowerLimitValue = min + range * indx;
                upperLimitValue = lowerLimitValue + range;
                if (indx == spreadCount - 1) {
                    upperLimitValue = max;
                }
                int tmpCount = GetDistributionCount(valueList, lowerLimitValue, upperLimitValue);
                if (!distributionDictionary.containsKey(lowerLimitValue)) {
                    distributionDictionary.put(lowerLimitValue, tmpCount);
                }
            }
        } else {
            if (valueList.size() == 1) {
                distributionDictionary.put(valueList.get(0), 1);
            }
        }
    }
    
    private static int GetDistributionCount(List<Double> valueList, double lowerLimit, double upperLimit) {
        int count = 0;
        for (Double value : valueList) {
            if (value >= lowerLimit && value < upperLimit) {
                count++;
            }
        }
        return count;
    }
    
    public static void CalclateOrderAndOrderLossNumber(List<Double> valueList, int OrderNumber, int Orderloses) {
        OrderNumber = valueList.size();
        Orderloses = 0;
        for (Double value : valueList) {
            if (value < 0) {
                Orderloses++;
            }
        }
    }

    public static List<List<Integer>> GetConsecutiveLossDateList(List<Double> valueList) {
        List<List<Integer>> allConsecutiveDateList = new java.util.ArrayList<>();
        List<Integer> consecutiveDateList = new java.util.ArrayList<>();
        
        for (int i = 0; i < valueList.size(); ++i) {
            if (Double.isNaN(valueList.get(i))) {
                continue;
            }
            
            if (valueList.get(i) < 0) {
                consecutiveDateList.add(i);
            }
            
            if (valueList.get(i) >= 0) {
                consecutiveDateList = new java.util.ArrayList<>();
            }
            
            if (consecutiveDateList.size() > 0) {
                if (!allConsecutiveDateList.contains(consecutiveDateList)) {
                    allConsecutiveDateList.add(new java.util.ArrayList<>(consecutiveDateList));
                }
            }
        }
        return allConsecutiveDateList;
    }

    public static List<List<Integer>> GetConsecutiveWinsDateList(List<Double> valueList) {
        List<List<Integer>> allConsecutiveDateList = new java.util.ArrayList<>();
        List<Integer> consecutiveDateList = new java.util.ArrayList<>();
        
        for (int i = 0; i < valueList.size(); ++i) {
            if (Double.isNaN(valueList.get(i))) {
                continue;
            }
            
            if (valueList.get(i) > 0) {
                consecutiveDateList.add(i);
            }
            
            if (valueList.get(i) <= 0) {
                consecutiveDateList = new java.util.ArrayList<>();
            }
            
            if (consecutiveDateList.size() > 0) {
                if (!allConsecutiveDateList.contains(consecutiveDateList)) {
                    allConsecutiveDateList.add(new java.util.ArrayList<>(consecutiveDateList));
                }
            }
        }
        return allConsecutiveDateList;
    }

    public static void CalculateConsecutiveMaxOrdersNumber(List<List<Integer>> allConsecutiveDateList, List<Double> profitValueList, int[] consecutiveNumber, double[] consecutiveProfit) {
        double maxProfit = 0;
        int maxOrderNumber = 0;
        
        for (int indx = 0; indx < allConsecutiveDateList.size(); ++indx) {
            List<Integer> tmpConsecutiveDateList = allConsecutiveDateList.get(indx);
            double profit = 0;
            int tmpOrderNumber = 0;
            tmpOrderNumber = tmpConsecutiveDateList.size();
            
            for (int dt : tmpConsecutiveDateList) {
                profit += profitValueList.get(dt);
            }
            
            if (tmpOrderNumber > maxOrderNumber) {
                maxProfit = profit;
                maxOrderNumber = tmpOrderNumber;
            }
        }
        
        consecutiveNumber[0] = maxOrderNumber;
        consecutiveProfit[0] = maxProfit;
    }

    public static void CalculateConsecutiveMaxOrdersProfit(List<List<Integer>> allConsecutiveDateList, List<Double> profitValueList, int[] consecutiveNumber, double[] consecutiveProfit, boolean flag) {
        double maxProfit = Double.NaN;
        int maxProfitOrderNumber = 0;
        int maxProfitIndex = 0;
        
        for (int indx = 0; indx < allConsecutiveDateList.size(); ++indx) {
            List<Integer> tmpConsecutiveDateList = allConsecutiveDateList.get(indx);
            double profit = 0;
            
            for (int i = 0; i < tmpConsecutiveDateList.size(); i++) {
                int dt = tmpConsecutiveDateList.get(i);
                if (flag && i == 0) {
                    profit += 0;
                } else {
                    profit += profitValueList.get(dt);
                }
            }
            
            if (Double.isNaN(maxProfit) || Math.abs(profit) > Math.abs(maxProfit)) {
                maxProfit = profit;
                maxProfitOrderNumber = tmpConsecutiveDateList.size();
                maxProfitIndex = indx;
            }
        }
        
        consecutiveNumber[0] = maxProfitOrderNumber;
        consecutiveProfit[0] = maxProfit;
    }

    public static void CalulateWinLossExpectedRange(ReturnDistribution returnDistribution, List<Double> lnValueList) {
        double winLossRangeLower = Double.NaN;
        double winLossRangeUpper = Double.NaN;
        double winLossRangeAverage = Double.NaN;
        CalulateWinLossExpectedRange(lnValueList, new double[]{winLossRangeUpper}, new double[]{winLossRangeLower}, new double[]{winLossRangeAverage});
        returnDistribution.WinLossRangeLower = winLossRangeLower;
        returnDistribution.WinLossRangeUpper = winLossRangeUpper;
        returnDistribution.WinLossRangeAverage = winLossRangeAverage;
    }
    
    public static void CalulateWinLossExpectedRange(List<Double> valueList, double[] upper, double[] lower, double[] average) {
        List<Double> gainPercent = new java.util.ArrayList<>();
        for (Double value : valueList) {
            if (Double.isNaN(value)) {
                continue;
            }
            gainPercent.add(value);
        }
        
        double tmpAverage = CalculateAverage(gainPercent);
        average[0] = tmpAverage;
        
        double[] samples = gainPercent.stream().mapToDouble(Double::doubleValue).toArray();
        double[] temp = FindRange(samples, 0.95);
        lower[0] = temp[0];
        upper[0] = temp[1];
    }
    
    public static double[] FindRange(double[] samples, double interval) {
        if (samples.length <= 2) {
            return new double[]{Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY};
        }
        
        // 简化实现：使用正态分布近似
        double mean = CalculateAverage(java.util.Arrays.stream(samples).boxed().collect(java.util.stream.Collectors.toList()));
        double sd = CalculateStandardDeviation(samples);
        double z = 1.96; // 95% 置信区间对应的z值
        double t = z * (sd / Math.sqrt(samples.length));
        
        return new double[]{mean - t, mean + t};
    }
    
    private static double CalculateAverage(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    private static double CalculateStandardDeviation(double[] values) {
        if (values.length <= 1) {
            return 0.0;
        }
        
        double mean = java.util.Arrays.stream(values).average().orElse(0.0);
        double variance = java.util.Arrays.stream(values)
                .map(x -> Math.pow(x - mean, 2))
                .average()
                .orElse(0.0);
        
        return Math.sqrt(variance);
    }
} 