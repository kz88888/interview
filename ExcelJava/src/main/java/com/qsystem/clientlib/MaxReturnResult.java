package com.qsystem.clientlib;

import java.time.LocalDateTime;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

/**
 * 用于封装CalculateMaxReturn函数的返回结果
 * 模拟C#的out参数功能
 */
public class MaxReturnResult {
    private boolean success;
    private Entry<Double, Integer> bestYearReturn;
    private Entry<Double, Integer> worstYearReturn;
    private Entry<Double, LocalDateTime> bestMonthReturn;
    private Entry<Double, LocalDateTime> worstMonthReturn;
    
    // 年度版本构造函数
    public MaxReturnResult(boolean success, double bestReturnValue, int bestReturnYear, 
                          double worstReturnValue, int worstReturnYear) {
        this.success = success;
        this.bestYearReturn = new SimpleEntry<>(bestReturnValue, bestReturnYear);
        this.worstYearReturn = new SimpleEntry<>(worstReturnValue, worstReturnYear);
    }
    
    // 月度版本构造函数
    public MaxReturnResult(boolean success, double bestReturnValue, LocalDateTime bestReturnDate,
                          double worstReturnValue, LocalDateTime worstReturnDate) {
        this.success = success;
        this.bestMonthReturn = new SimpleEntry<>(bestReturnValue, bestReturnDate);
        this.worstMonthReturn = new SimpleEntry<>(worstReturnValue, worstReturnDate);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public Entry<Double, Integer> getBestYearReturn() {
        return bestYearReturn;
    }
    
    public Entry<Double, Integer> getWorstYearReturn() {
        return worstYearReturn;
    }
    
    public Entry<Double, LocalDateTime> getBestMonthReturn() {
        return bestMonthReturn;
    }
    
    public Entry<Double, LocalDateTime> getWorstMonthReturn() {
        return worstMonthReturn;
    }
}