package com.qsystem.qsexcel;

import com.qsystem.qsexcel.excelutil.Helper;
import java.time.LocalDateTime;

/**
 * Excel数据解析帮助类
 */
public class ExcelDataParseHelper {
    
    /**
     * 解析日期时间
     */
    public static LocalDateTime ParseDateTime(Object value) {
        if (value == null) {
            return LocalDateTime.now();
        }
        
        try {
            return Helper.ConvertToDateTime(value);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
    
    /**
     * 解析双精度值
     */
    public static double ParseDouble(Object value) {
        return Helper.TryParseDouble(value);
    }
    
    /**
     * 解析字符串
     */
    public static String ParseString(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
} 