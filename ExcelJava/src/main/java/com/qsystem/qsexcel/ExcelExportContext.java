package com.qsystem.qsexcel;

import com.qsystem.qsexcel.excelutil.ExcelHelper;
import java.util.HashMap;
import java.util.Map;

/**
 * Excel导出上下文管理器
 * 用于管理单个导出会话中的ExcelHelper实例，确保sheet按插入顺序排列
 */
public class ExcelExportContext {
    private static final Map<String, ExcelHelper> helperCache = new HashMap<>();
    
    /**
     * 获取或创建ExcelHelper实例
     * 同一个文件名会返回同一个实例
     */
    public static synchronized ExcelHelper getExcelHelper(String fileName) {
        return helperCache.computeIfAbsent(fileName, ExcelHelper::new);
    }
    
    /**
     * 完成导出并清理缓存
     */
    public static synchronized void completeExport(String fileName, boolean revert) {
        ExcelHelper helper = helperCache.get(fileName);
        if (helper != null) {
            // 在保存之前倒序排列sheet
            if(revert){
                helper.reverseSheetOrder();
            }
            helper.Complete();
            helperCache.remove(fileName);
        }
    }
    
    /**
     * 清理所有缓存
     */
    public static synchronized void clearAll() {
        for (ExcelHelper helper : helperCache.values()) {
            try {
                helper.Complete();
            } catch (Exception e) {
                // 忽略错误，继续清理
            }
        }
        helperCache.clear();
    }
    
    /**
     * 检查是否存在未完成的导出
     */
    public static synchronized boolean hasActiveExports() {
        return !helperCache.isEmpty();
    }
}