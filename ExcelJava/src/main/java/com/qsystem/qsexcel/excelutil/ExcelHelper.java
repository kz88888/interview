package com.qsystem.qsexcel.excelutil;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Collections;

/**
 * Excel帮助类 - 优化版本，更贴近C#实现逻辑
 * 主要改进：
 * 1. 统一使用1-based索引，内部转换为0-based
 * 2. 支持高效的二维数组批量写入
 * 3. 保持与C#版本一致的方法签名和行为
 */
public class ExcelHelper {
    
    // 私有变量
    private String _fileName = "";
    private Workbook workbook = null;
    private Sheet workSheet = null;  // 对应C#的workSheet
    private boolean nullSheetHaveHidden = false;
    private final Object excelLock = new Object();
    private ExcelChartHelper chartHelper = null;  // 图表帮助器
    
    // 属性
    private int rowCount = 0;
    private int colCount = 0;
    
    /**
     * 当前Worksheet已使用的行数
     */
    public int getRowCount() {
        if (rowCount == 0) {
            GetUsedRowsCount();
        }
        return rowCount;
    }
    
    /**
     * 当前Worksheet已使用的列数
     */
    public int getColCount() {
        if (colCount == 0) {
            GetUsedColsCount();
        }
        return colCount;
    }
    
    /**
     * 构造函数
     */
    public ExcelHelper(String fileName) {
        this._fileName = fileName;
        Initiate();
        try {
            this.chartHelper = new ExcelChartHelper(this);
        } catch (Exception e) {
            System.err.println("创建图表帮助器时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 初始化
     */
    private void Initiate() {
        try {
            File file = new File(_fileName);
            if (file.exists()) {
                LoadObjectFromFile(_fileName);
            } else {
                InitNewObject();
            }
        } catch (Exception e) {
            Complete();
        }
    }
    
    /**
     * 初始化新对象
     */
    private void InitNewObject() {
        try {
            workbook = new XSSFWorkbook();
            // POI自动创建一个默认的sheet，我们需要处理它
            if (workbook.getNumberOfSheets() == 0) {
                workSheet = workbook.createSheet("Sheet1");
            } else {
                workSheet = workbook.getSheetAt(0);
            }
        } catch (Exception e) {
            Complete();
        }
    }
    
    /**
     * 从文件加载对象
     */
    private void LoadObjectFromFile(String fileName) {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            workbook = new XSSFWorkbook(fis);
            workSheet = workbook.getNumberOfSheets() > 0 ? 
                       workbook.getSheetAt(0) : workbook.createSheet("Sheet1");
        } catch (Exception ex) {
            // 异常处理
        }
    }
    
    /**
     * 创建新工作表
     */
    public boolean CreateNewWorkSheet(String workSheetName) {
        try {
            synchronized (excelLock) {
                // 如果是第一个sheet调用，且存在默认的Sheet1，则重命名它
                if (workbook.getNumberOfSheets() == 1) {
                    Sheet firstSheet = workbook.getSheetAt(0);
                    String firstSheetName = firstSheet.getSheetName();
                    // 检查是否是默认的空sheet
                    if ((firstSheetName.equals("Sheet1") || firstSheetName.equals("Sheet") || firstSheetName.equals("Sheet0")) 
                        && firstSheet.getLastRowNum() <= 0) {
                        // 重命名第一个sheet而不是创建新的
                        workbook.setSheetName(0, workSheetName);
                        workSheet = firstSheet;
                        return true;
                    }
                }
                
                // 创建新的sheet
                workSheet = workbook.createSheet(workSheetName);
                
                // 隐藏空的Sheet1（如果存在）
                if (!nullSheetHaveHidden && workbook.getNumberOfSheets() > 1) {
                    HideNullSheet();
                }
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 设置当前工作表
     */
    public void SetCurrentSheet(String workSheetName) {
        if (workSheetName == null || workSheetName.isEmpty()) {
            workSheet = workbook.getSheetAt(workbook.getNumberOfSheets() - 1);
        } else {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet.getSheetName().equals(workSheetName)) {
                    workSheet = sheet;
                    break;
                }
            }
        }
    }
    
    /**
     * 隐藏空工作表
     */
    public void HideNullSheet() {
        try {
            if (workbook.getNumberOfSheets() > 1) {
                String workSheetName = "Sheet1";
                Sheet sheet1 = workbook.getSheet(workSheetName);
                if (sheet1 != null) {
                    int sheetIndex = workbook.getSheetIndex(sheet1);
                    if (!workbook.isSheetHidden(sheetIndex)) {
                        workbook.setSheetHidden(sheetIndex, true);
                        nullSheetHaveHidden = true;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 获取所有工作表名
     */
    public List<String> GetALLSheetName() {
        List<String> sheetNameList = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheetNameList.add(workbook.getSheetName(i));
        }
        return sheetNameList;
    }
    
    /**
     * 创建新工作表并复制数据
     */
    public boolean CreateNewWorkSheetAndCopy(String newWorkSheetName, int copyDataColumnCount) {
        try {
            String formerSheetName = workSheet.getSheetName();
            
            workSheet = workbook.createSheet(newWorkSheetName);
            
            CopyData(formerSheetName, copyDataColumnCount);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 复制数据 - 与C#逻辑一致，使用最后一个sheet作为源
     */
    private void CopyData(String formerSheetName, int copyDataColumnCount) {
        // C#版本：Worksheet formerSheet = (Worksheet)workBook.Sheets.get_Item(workBook.Sheets.Count);
        Sheet formerSheet = workbook.getSheetAt(workbook.getNumberOfSheets() - 2); // -2因为新sheet已创建
        
        if (formerSheet == null) return;
        
        // 获取源数据范围
        int lastRow = formerSheet.getLastRowNum();
        
        // 批量复制数据
        for (int rowIdx = 0; rowIdx <= lastRow; rowIdx++) {
            Row sourceRow = formerSheet.getRow(rowIdx);
            if (sourceRow != null) {
                Row destRow = workSheet.createRow(rowIdx);
                for (int colIdx = 0; colIdx < copyDataColumnCount; colIdx++) {
                    Cell sourceCell = sourceRow.getCell(colIdx);
                    if (sourceCell != null) {
                        Cell destCell = destRow.createCell(colIdx);
                        copyCell(sourceCell, destCell);
                    }
                }
            }
        }
    }
    
    /**
     * 设置单元格值 - 使用1-based索引，与C#保持一致
     */
    public boolean SetRangValue(int rowIndex, int colIndex, Object value) {
        try {
            // 转换为0-based索引
            Row row = workSheet.getRow(rowIndex - 1);
            if (row == null) {
                row = workSheet.createRow(rowIndex - 1);
            }
            
            Cell cell = row.getCell(colIndex - 1);
            if (cell == null) {
                cell = row.createCell(colIndex - 1);
            }
            
            setCellValue(cell, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 设置单元格值（带宽度）
     */
    public boolean SetRangValue(int rowIndex, int colIndex, Object value, int width) {
        try {
            boolean result = SetRangValue(rowIndex, colIndex, value);
            if (result) {
                workSheet.setColumnWidth(colIndex - 1, width * 256);
            }
            return result;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 设置粗体单元格值
     */
    public boolean SetRangBoldValue(int rowIndex, int colIndex, Object value, int width) {
        try {
            Row row = workSheet.getRow(rowIndex - 1);
            if (row == null) {
                row = workSheet.createRow(rowIndex - 1);
            }
            
            Cell cell = row.getCell(colIndex - 1);
            if (cell == null) {
                cell = row.createCell(colIndex - 1);
            }
            
            // 创建粗体样式
            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);
            
            setCellValue(cell, value);
            cell.setCellStyle(boldStyle);
            
            if (width > 0) {
                workSheet.setColumnWidth(colIndex - 1, width * 256);
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 设置单元格值（带标题样式）
     */
    public boolean SetRangValue(int rowIndex, int colIndex, Object value, boolean isTitle) {
        try {
            boolean result = SetRangValue(rowIndex, colIndex, value, 18);
            if (result && isTitle) {
                Row row = workSheet.getRow(rowIndex - 1);
                Cell cell = row.getCell(colIndex - 1);
                
                CellStyle titleStyle = workbook.createCellStyle();
                Font titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleStyle.setFont(titleFont);
                
                cell.setCellStyle(titleStyle);
            }
            return result;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 设置区域值 - 核心方法，支持二维数组批量写入
     * 与C#版本保持一致，支持直接写入二维数组
     */
    public boolean SetRangValue(int startRowIndex, int startColIndex, int endRowIndex, int endColIndex, Object value) {
        try {
            synchronized (excelLock) {
                if (value instanceof Object[][]) {
                    // 处理二维数组 - 批量写入
                    Object[][] arrayValue = (Object[][]) value;
                    
                    // 确保有足够的行
                    for (int i = 0; i < arrayValue.length && (startRowIndex + i - 1) <= (endRowIndex - 1); i++) {
                        Row row = workSheet.getRow(startRowIndex + i - 1);
                        if (row == null) {
                            row = workSheet.createRow(startRowIndex + i - 1);
                        }
                        
                        // 批量设置单元格值
                        if (arrayValue[i] != null) {
                            for (int j = 0; j < arrayValue[i].length && (startColIndex + j - 1) <= (endColIndex - 1); j++) {
                                Cell cell = row.getCell(startColIndex + j - 1);
                                if (cell == null) {
                                    cell = row.createCell(startColIndex + j - 1);
                                }
                                setCellValue(cell, arrayValue[i][j]);
                            }
                        }
                    }
                } else {
                    // 非数组值，填充整个区域
                    for (int rowIdx = startRowIndex; rowIdx <= endRowIndex; rowIdx++) {
                        Row row = workSheet.getRow(rowIdx - 1);
                        if (row == null) {
                            row = workSheet.createRow(rowIdx - 1);
                        }
                        
                        for (int colIdx = startColIndex; colIdx <= endColIndex; colIdx++) {
                            Cell cell = row.getCell(colIdx - 1);
                            if (cell == null) {
                                cell = row.createCell(colIdx - 1);
                            }
                            setCellValue(cell, value);
                        }
                    }
                }
                
                // 自动调整列宽
                for (int col = startColIndex; col <= endColIndex; col++) {
                    workSheet.autoSizeColumn(col - 1);
                }
                
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取单元格值 - 使用1-based索引
     */
    public Object GetRangValue(int rowIndex, int colIndex) {
        try {
            Row row = workSheet.getRow(rowIndex - 1);
            if (row != null) {
                Cell cell = row.getCell(colIndex - 1);
                if (cell != null) {
                    return getCellValue(cell);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取列范围值
     */
    public List<Object> GetColumnRangeValues(int startRowIndex, int columnIndex) {
        int endRowIndex = this.getRowCount();
        
        List<Object> columnValueList = new ArrayList<>();
        for (int rowIdx = startRowIndex; rowIdx <= endRowIndex; rowIdx++) {
            Row row = workSheet.getRow(rowIdx - 1);
            if (row != null) {
                Cell cell = row.getCell(columnIndex - 1);
                if (cell != null) {
                    columnValueList.add(getCellValue(cell));
                }
            }
        }
        
        return columnValueList;
    }
    
    /**
     * 获取范围值（二维数组）
     */
    public Object[][] GetRangValue(int startRowIndex, int startColumnIndex, int endRowIndex, int endColumnIndex) {
        int rows = endRowIndex - startRowIndex + 1;
        int cols = endColumnIndex - startColumnIndex + 1;
        Object[][] result = new Object[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            Row row = workSheet.getRow(startRowIndex + i - 1);
            if (row != null) {
                for (int j = 0; j < cols; j++) {
                    Cell cell = row.getCell(startColumnIndex + j - 1);
                    result[i][j] = (cell != null) ? getCellValue(cell) : null;
                }
            }
        }
        
        return result;
    }
    
    /**
     * 设置区域背景色
     */
    public boolean SetRangeBackground(int sRow, int sCol, int eRow, int eCol, int colorIndex) {
        try {
            CellStyle colorStyle = workbook.createCellStyle();
            colorStyle.setFillForegroundColor((short) colorIndex);
            colorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            for (int rowIdx = sRow; rowIdx <= eRow; rowIdx++) {
                Row row = workSheet.getRow(rowIdx - 1);
                if (row == null) {
                    row = workSheet.createRow(rowIdx - 1);
                }
                
                for (int colIdx = sCol; colIdx <= eCol; colIdx++) {
                    Cell cell = row.getCell(colIdx - 1);
                    if (cell == null) {
                        cell = row.createCell(colIdx - 1);
                    }
                    cell.setCellStyle(colorStyle);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 设置边框线
     */
    public boolean SetBorderLine(int sRow, int sCol, int eRow, int eCol) {
        try {
            CellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderTop(BorderStyle.THICK);
            borderStyle.setBorderBottom(BorderStyle.THICK);
            borderStyle.setBorderLeft(BorderStyle.THICK);
            borderStyle.setBorderRight(BorderStyle.THICK);
            
            // 应用到整个区域
            for (int rowIdx = sRow; rowIdx <= eRow; rowIdx++) {
                Row row = workSheet.getRow(rowIdx - 1);
                if (row == null) {
                    row = workSheet.createRow(rowIdx - 1);
                }
                
                for (int colIdx = sCol; colIdx <= eCol; colIdx++) {
                    Cell cell = row.getCell(colIdx - 1);
                    if (cell == null) {
                        cell = row.createCell(colIdx - 1);
                    }
                    cell.setCellStyle(borderStyle);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 完成操作并保存文件
     */
    public boolean Complete() {
        synchronized (excelLock) {
            try {
                if (workbook == null) {
                    Initiate();
                }
                
                File file = new File(_fileName);
                
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
                
                ReleaseObject();
                return true;
            } catch (Exception ex) {
                return false;
            } finally {
                StopExcel();
            }
        }
    }
    
    /**
     * 释放对象
     */
    private void ReleaseObject() {
        synchronized (excelLock) {
            // POI不需要像COM那样释放对象，但可以清理引用
            workSheet = null;
            workbook = null;
            System.gc();
        }
    }
    
    /**
     * 停止Excel
     */
    public void StopExcel() {
        try {
            if (workbook != null) {
                workbook.close();
                workbook = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * 获取已使用的行数
     */
    private void GetUsedRowsCount() {
        this.rowCount = workSheet.getLastRowNum() + 1;
    }
    
    /**
     * 获取已使用的列数
     */
    private void GetUsedColsCount() {
        int maxCol = 0;
        for (int i = 0; i <= workSheet.getLastRowNum(); i++) {
            Row row = workSheet.getRow(i);
            if (row != null && row.getLastCellNum() > maxCol) {
                maxCol = row.getLastCellNum();
            }
        }
        this.colCount = maxCol;
    }
    
    // 辅助方法
    private void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }
    
    private Object getCellValue(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue();
                }
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return null;
            default:
                return null;
        }
    }
    
    private void copyCell(Cell source, Cell target) {
        if (source == null) return;
        
        switch (source.getCellType()) {
            case STRING:
                target.setCellValue(source.getStringCellValue());
                break;
            case NUMERIC:
                target.setCellValue(source.getNumericCellValue());
                break;
            case BOOLEAN:
                target.setCellValue(source.getBooleanCellValue());
                break;
            case FORMULA:
                target.setCellFormula(source.getCellFormula());
                break;
            case BLANK:
                target.setBlank();
                break;
        }
        
        // 复制样式
        if (source.getCellStyle() != null) {
            target.setCellStyle(source.getCellStyle());
        }
    }
    
    // 获取当前工作表（供ChartHelper使用）
    public Sheet getCurrentSheet() {
        return workSheet;
    }
    
    /**
     * 重新排序所有sheet，按照指定的顺序
     * @param sheetNames sheet名称列表，按期望的顺序排列
     */
    public void reorderSheets(List<String> sheetNames) {
        synchronized (excelLock) {
            if (workbook == null || sheetNames == null || sheetNames.isEmpty()) {
                return;
            }
            
            // 将每个sheet移动到正确的位置
            for (int i = 0; i < sheetNames.size(); i++) {
                String sheetName = sheetNames.get(i);
                int currentIndex = workbook.getSheetIndex(sheetName);
                if (currentIndex >= 0 && currentIndex != i) {
                    workbook.setSheetOrder(sheetName, i);
                }
            }
        }
    }
    
    /**
     * 获取所有sheet名称（按当前顺序）
     */
    public List<String> getAllSheetNames() {
        List<String> names = new ArrayList<>();
        synchronized (excelLock) {
            if (workbook != null) {
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    names.add(workbook.getSheetName(i));
                }
            }
        }
        return names;
    }
    
    /**
     * 倒序排列所有sheet（后创建的在前面）
     */
    public void reverseSheetOrder() {
        synchronized (excelLock) {
            if (workbook == null || workbook.getNumberOfSheets() <= 1) {
                return;
            }
            
            // 获取所有sheet名称
            List<String> sheetNames = getAllSheetNames();
            
            // 倒序排列
            Collections.reverse(sheetNames);
            
            // 重新排序
            reorderSheets(sheetNames);
        }
    }
    
    // 图表相关方法 - 使用ExcelChartHelper实现
    public void CreateLineChart(int startRowIndex, int startColIndex, int endRowIndex, int endColIndex) {
        if (chartHelper != null) {
            chartHelper.CreateSimpleLineChart(startRowIndex, startColIndex, endRowIndex, endColIndex);
        }
    }
    
    public void CreateLineChart(boolean isLogScale, double initCapital, String dataSheetName, 
                               String chartSheetName, int startColIndex, int endColIndex, 
                               int left, int top, int dataSeriesCount, String titleName, 
                               String xlAxisName, String ylAxisName, String ySecondAxisTitle, 
                               int width, int height) {
        if (chartHelper != null) {
            try {
                chartHelper.CreateLineChart(isLogScale, initCapital, dataSheetName, chartSheetName,
                    startColIndex, endColIndex, left, top, dataSeriesCount, titleName,
                    xlAxisName, ylAxisName, ySecondAxisTitle, width, height);
            } catch (Exception e) {
                System.err.println("创建线图失败: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("图表帮助器未初始化，无法创建线图");
        }
    }
    
    public void CreateHistogramChart(String dataSheetName, String chartSheetName, 
                                   int startColIndex, int endColIndex, int left, int top, 
                                   int dataSeriesCount, double meanValue, int rowCount, 
                                   String titleName, String xlAxisName, String ylAxisName) {
        if (chartHelper != null) {
            try {
                chartHelper.CreateHistogramChart(dataSheetName, chartSheetName,
                    startColIndex, endColIndex, left, top, dataSeriesCount,
                    meanValue, rowCount, titleName, xlAxisName, ylAxisName);
            } catch (Exception e) {
                System.err.println("创建直方图失败: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("图表帮助器未初始化，无法创建直方图");
        }
    }
}