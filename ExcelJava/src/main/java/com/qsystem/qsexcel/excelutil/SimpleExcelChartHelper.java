package com.qsystem.qsexcel.excelutil;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

/**
 * 简化的Excel图表帮助类
 * 当完整的图表功能不可用时，提供基本的图表占位符
 */
public class SimpleExcelChartHelper {
    
    private final ExcelHelper excelHelper;
    
    public SimpleExcelChartHelper(ExcelHelper excelHelper) {
        this.excelHelper = excelHelper;
    }
    
    /**
     * 创建图表占位符和说明
     */
    public void createChartPlaceholder(String chartSheetName, String chartType, String description) {
        try {
            // 确保图表工作表存在
            excelHelper.SetCurrentSheet(chartSheetName);
            
            // 在图表位置添加说明文字
            excelHelper.SetRangValue(2, 2, "图表类型: " + chartType, true);
            excelHelper.SetRangValue(3, 2, "说明: " + description);
            excelHelper.SetRangValue(5, 2, "注意: 由于技术限制，图表需要在Excel中手动创建");
            excelHelper.SetRangValue(6, 2, "数据已准备就绪，请按以下步骤创建图表：");
            excelHelper.SetRangValue(7, 2, "1. 选择数据区域");
            excelHelper.SetRangValue(8, 2, "2. 插入 -> 图表 -> " + chartType);
            excelHelper.SetRangValue(9, 2, "3. 根据需要调整图表样式");
            
            // 添加边框以标识图表区域
            excelHelper.SetBorderLine(2, 2, 10, 8);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 准备图表数据并添加说明
     */
    public void prepareChartData(String dataSheetName, int startRow, int startCol, 
                                int endRow, int endCol, String[] headers) {
        try {
            excelHelper.SetCurrentSheet(dataSheetName);
            
            // 确保数据有标题
            if (headers != null && headers.length > 0) {
                for (int i = 0; i < headers.length; i++) {
                    excelHelper.SetRangValue(startRow, startCol + i, headers[i], true);
                }
            }
            
            // 标记数据区域
            excelHelper.SetRangeBackground(startRow, startCol, startRow, endCol, 15); // 灰色背景
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}