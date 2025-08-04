package com.qsystem.clientlib;

import java.time.LocalDateTime;

/**
 * 带时间跨度的收益类
 */
public class ReturnWithTimeSpan {
    public double value;
    public LocalDateTime beginTime;
    public LocalDateTime endTime;
    
    public static void CopyBestWorthReturn(ReturnWithTimeSpan src, ReturnWithTimeSpan dst) {
        dst.beginTime = src.beginTime;
        dst.endTime = src.endTime;
        dst.value = src.value;
    }
} 