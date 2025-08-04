package com.qsystem.qsexcel.excelutil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Excel工具辅助类
 */
public class Helper {
    
    /**
     * 将字符串转换为日期时间
     */
    public static LocalDateTime ConvertToDateTime(String endDateString, boolean maybeEmpty) {
        if (endDateString == null || endDateString.isEmpty()) {
            return LocalDateTime.now();
        } else {
            return LocalDateTime.parse(endDateString);
        }
    }

    /**
     * 将Excel对象转换为日期时间
     */
    public static LocalDateTime ConvertToDateTime(Object excelDate) {
        LocalDateTime dateTime = LocalDateTime.MIN;

        try {
            if (excelDate instanceof String) {
                return LocalDateTime.parse((String) excelDate);
            } else {
                return ConvertToDateTime(Double.parseDouble(excelDate.toString()));
            }
        } catch (Exception e) {
            return ConvertToDateTime(Double.parseDouble(excelDate.toString()));
        }
    }

    /**
     * 将Excel日期数值转换为日期时间
     */
    public static LocalDateTime ConvertToDateTime(double excelDate) {
        if (excelDate < 1) {
            throw new IllegalArgumentException("Excel dates cannot be smaller than 0.");
        }
        
        LocalDateTime dateOfReference = LocalDateTime.of(1900, 1, 1, 0, 0);
        if (excelDate > 60.0) {
            excelDate = excelDate - 2;
        } else {
            excelDate = excelDate - 1;
        }
        return dateOfReference.plusDays((long) excelDate);
    }

    /**
     * 尝试解析双精度值
     */
    public static double TryParseDouble(Object value) {
        if (value == null) {
            return 0;
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 获取日期时间字符串
     */
    public static String GetDateTimeString(LocalDateTime date) {
        String month = String.format("%02d", date.getMonthValue());
        String day = String.format("%02d", date.getDayOfMonth());
        return date.getYear() + month + day;
    }

    /**
     * 获取当前列数
     */
    public static int GetCurrentColumnCount() {
        int totalCount = 10;
        return totalCount;
    }

    /**
     * 获取当前VNV列数
     */
    public static int GetCurrentVNVColumnCount() {
        int totalCount = 10;
        return totalCount;
    }

    /**
     * 获取分布图表的当前列数
     */
    public static int GetCurrentColumnCountForDistributionChart() {
        int totalCount = 10;
        return totalCount;
    }
} 