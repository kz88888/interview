package com.qsystem.qsexcel.excelutil;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xddf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.util.Units;

import java.util.List;
import java.util.ArrayList;

/**
 * Excel图表帮助类 - 使用Apache POI的XDDF API创建图表
 * 提供与C#版本类似的图表创建功能
 */
public class ExcelChartHelper {
    
    private final ExcelHelper excelHelper;
    
    public ExcelChartHelper(ExcelHelper excelHelper) {
        this.excelHelper = excelHelper;
    }
    
    /**
     * 创建线图 - 对应C# CreateLineChart
     */
    public void CreateLineChart(boolean isLogScale, double initCapital, String dataSheetName, 
                               String chartSheetName, int startColIndex, int endColIndex, 
                               int left, int top, int dataSeriesCount, String titleName, 
                               String xlAxisName, String ylAxisName, String ySecondAxisTitle, 
                               int width, int height) {
        try {
            // 设置数据工作表
            excelHelper.SetCurrentSheet(dataSheetName);
            XSSFSheet dataSheet = (XSSFSheet) excelHelper.getCurrentSheet();
            
            // 获取数据范围 - 需要找到实际的数据结束行
            int startRowIndex = 0; // POI使用0-based索引
            int endRowIndex = dataSheet.getLastRowNum();
            
            // 查找实际的数据结束行（排除空白行）
            // 检查日期列是否有数据
            for (int i = endRowIndex; i >= startRowIndex; i--) {
                Row row = dataSheet.getRow(i);
                if (row != null) {
                    Cell cell = row.getCell(startColIndex - 1); // 日期列，转换为0-based
                    if (cell != null && cell.getCellType() != CellType.BLANK) {
                        endRowIndex = i;
                        break;
                    }
                }
            }
            
            // 创建或获取图表工作表
            excelHelper.SetCurrentSheet(chartSheetName);
            XSSFSheet chartSheet = (XSSFSheet) excelHelper.getCurrentSheet();
            
            // 创建绘图区域
            XSSFDrawing drawing = chartSheet.createDrawingPatriarch();
            
            // 定义图表位置（以单元格为单位）
            // 转换像素位置到单元格锚点
            int col1 = left / 64;  // 大约每列64像素
            int row1 = top / 20;   // 大约每行20像素
            int col2 = col1 + (width / 64);
            int row2 = row1 + (height / 20);
            
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, col1, row1, col2, row2);
            
            // 创建图表
            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText(titleName);
            chart.setTitleOverlay(false);
            
            // 创建图例
            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);
            
            // 创建分类轴（X轴）
            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxis.setTitle(xlAxisName);
            
            // 创建值轴（Y轴）
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setTitle(ylAxisName);
            
            // 设置对数刻度
            if (isLogScale) {
                leftAxis.setLogBase(10.0);
                leftAxis.setMinimum(initCapital / 10);
                leftAxis.setCrosses(AxisCrosses.MIN);
            }
            
            // 设置交叉点
            leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);
            
            // 准备数据
            // startColIndex 是日期列的位置（1-based），数据列从 startColIndex+1 开始
            // 转换为0-based索引：日期列 = startColIndex - 1，第一个数据列 = startColIndex
            int dateColIndex = startColIndex - 1;  // 1-based转0-based
            
            CellRangeAddress categoriesRange = new CellRangeAddress(
                startRowIndex + 1, endRowIndex, 
                dateColIndex, dateColIndex
            );
            
            XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(
                dataSheet, categoriesRange
            );
            
            // 创建线图数据
            XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
            
            // 添加数据系列
            // 数据列从 startColIndex+1 开始（1-based）
            for (int i = 0; i < dataSeriesCount; i++) {
                // 数据列索引：startColIndex + 1 + i（1-based）
                // 转换为0-based：startColIndex + i
                int dataColIndex = startColIndex + i;  // 0-based索引
                
                CellRangeAddress valuesRange = new CellRangeAddress(
                    startRowIndex + 1, endRowIndex,
                    dataColIndex, dataColIndex
                );
                
                XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(
                    dataSheet, valuesRange
                );
                
                // 系列标题从第一行获取
                String seriesTitle = dataSheet.getRow(startRowIndex)
                    .getCell(dataColIndex).getStringCellValue();
                
                XDDFLineChartData.Series series = (XDDFLineChartData.Series) data.addSeries(categories, values);
                series.setTitle(seriesTitle, null);
                series.setSmooth(false);
                series.setMarkerStyle(MarkerStyle.NONE);
                
                // 设置线条样式和颜色
                try {
                    XDDFLineProperties lineProperties = new XDDFLineProperties();
                    lineProperties.setWidth(1.0);  // 1磅宽度
                    
                    // 设置线条颜色
                    if (i == 0) {
                        // 第一条线（Account Balance）- 蓝色
                        XDDFSolidFillProperties fillProperties = new XDDFSolidFillProperties();
                        XDDFColor color = XDDFColor.from(new byte[]{0, 0, (byte)255}); // RGB蓝色
                        fillProperties.setColor(color);
                        lineProperties.setFillProperties(fillProperties);
                    } else if (i == 1) {
                        // 第二条线（Benchmark Value）- 红色
                        XDDFSolidFillProperties fillProperties = new XDDFSolidFillProperties();
                        XDDFColor color = XDDFColor.from(new byte[]{(byte)255, 0, 0}); // RGB红色
                        fillProperties.setColor(color);
                        lineProperties.setFillProperties(fillProperties);
                    }
                    
                    series.setLineProperties(lineProperties);
                } catch (Exception e) {
                    // 如果设置线条属性失败，继续执行
                    System.err.println("无法设置线条属性: " + e.getMessage());
                }
            }
            
            // 如果有多个系列且需要第二Y轴
            if (dataSeriesCount > 2 && ySecondAxisTitle != null && !ySecondAxisTitle.isEmpty()) {
                XDDFValueAxis rightAxis = chart.createValueAxis(AxisPosition.RIGHT);
                rightAxis.setTitle(ySecondAxisTitle);
                rightAxis.setCrossBetween(AxisCrossBetween.BETWEEN);
                
                // 可以将某些系列关联到第二Y轴
                // 这需要创建新的图表数据对象
            }
            
            // 绘制图表
            chart.plot(data);
            
            // 设置X轴标签文字方向为垂直（在plot之后设置）
            try {
                // 首先创建文本属性
                bottomAxis.getOrAddTextProperties();
                
                // 设置X轴标签旋转为-90度（垂直）
                // -90度 = -5400000 (POI使用1/60000度为单位)
                chart.getCTChart().getPlotArea().getCatAxArray(0)
                    .getTxPr().getBodyPr().setRot(-5400000);
            } catch (Exception e) {
                System.err.println("设置X轴标签旋转失败: " + e.getMessage());
                // 如果失败，继续执行，不影响图表生成
            }
            
        } catch (Exception e) {
            System.err.println("创建图表时出错: " + e.getMessage());
            e.printStackTrace();
            // 记录错误但不中断执行，不抛出异常
        }
    }
    
    /**
     * 创建直方图 - 对应C# CreateHistogramChart
     */
    public void CreateHistogramChart(String dataSheetName, String chartSheetName, 
                                   int startColIndex, int endColIndex, int left, int top, 
                                   int dataSeriesCount, double meanValue, int rowCount, 
                                   String titleName, String xlAxisName, String ylAxisName) {
        try {
            // 设置数据工作表
            excelHelper.SetCurrentSheet(dataSheetName);
            XSSFSheet dataSheet = (XSSFSheet) excelHelper.getCurrentSheet();
            
            // 获取数据范围 - 注意C#使用1-based索引
            int startRowIndex = 0;  // POI使用0-based索引
            int endRowIndex = rowCount > 0 ? Math.min(rowCount - 1, dataSheet.getLastRowNum()) : dataSheet.getLastRowNum();
            
            // 调整列索引 - 从1-based转换为0-based
            int adjustedStartCol = startColIndex - 1;
            int adjustedEndCol = endColIndex - 1;
            
            // 验证数据是否存在
            if (dataSheet.getLastRowNum() < 1) {
                System.err.println("数据表 " + dataSheetName + " 没有足够的数据行");
                return;
            }
            
            // 读取数据值（模仿C#的逻辑）
            List<Double> xValueList = new ArrayList<>();
            double maxYValue = Double.MIN_VALUE;
            
            // 读取数据
            for (int rowIdx = 1; rowIdx <= endRowIndex && rowIdx <= dataSheet.getLastRowNum(); rowIdx++) {
                Row row = dataSheet.getRow(rowIdx);
                if (row == null) continue;
                
                Cell xCell = row.getCell(adjustedStartCol);
                Cell yCell = row.getCell(adjustedEndCol);
                
                if (xCell != null && yCell != null) {
                    try {
                        double xValue = xCell.getNumericCellValue();
                        double yValue = yCell.getNumericCellValue();
                        xValueList.add(xValue);
                        if (yValue > maxYValue) {
                            maxYValue = yValue;
                        }
                    } catch (Exception e) {
                        // 如果不是数字，尝试解析字符串
                        try {
                            String xStr = xCell.getStringCellValue();
                            String yStr = yCell.getStringCellValue();
                            double xValue = Double.parseDouble(xStr);
                            double yValue = Double.parseDouble(yStr);
                            xValueList.add(xValue);
                            if (yValue > maxYValue) {
                                maxYValue = yValue;
                            }
                        } catch (Exception ex) {
                            // 跳过无法解析的数据
                            continue;
                        }
                    }
                }
            }
            
            if (xValueList.isEmpty()) {
                System.err.println("没有找到有效的数据来创建直方图");
                return;
            }
            
            maxYValue = (int)(1.2 * maxYValue);
            
            // 创建或获取图表工作表
            excelHelper.SetCurrentSheet(chartSheetName);
            XSSFSheet chartSheet = (XSSFSheet) excelHelper.getCurrentSheet();
            
            // 创建绘图区域
            XSSFDrawing drawing = chartSheet.createDrawingPatriarch();
            
            // 定义图表位置
            int col1 = left / 64;
            int row1 = top / 20;
            int col2 = col1 + (700 / 64);  // 默认宽度700
            int row2 = row1 + (350 / 20);  // 默认高度350
            
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, col1, row1, col2, row2);
            
            // 创建图表
            XSSFChart chart = drawing.createChart(anchor);
            if (titleName != null && !titleName.isEmpty()) {
                chart.setTitleText(titleName);
            }
            chart.setTitleOverlay(false);
            
            // 创建图例
            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);
            
            // 创建分类轴（X轴）
            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            if (xlAxisName != null && !xlAxisName.isEmpty()) {
                bottomAxis.setTitle(xlAxisName);
            }
            
            // 创建值轴（Y轴）
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            if (ylAxisName != null && !ylAxisName.isEmpty()) {
                leftAxis.setTitle(ylAxisName);
            }
            leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);
            leftAxis.setMaximum(maxYValue);
            
            // 准备数据范围
            CellRangeAddress categoriesRange = new CellRangeAddress(
                1, endRowIndex,  // 从第2行开始（0-based索引）
                adjustedStartCol, adjustedStartCol
            );
            
            CellRangeAddress valuesRange = new CellRangeAddress(
                1, endRowIndex,
                adjustedEndCol, adjustedEndCol
            );
            
            // 创建数据源
            XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(
                dataSheet, categoriesRange
            );
            
            XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(
                dataSheet, valuesRange
            );
            
            // 创建柱状图数据
            XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            data.setBarDirection(BarDirection.COL);
            data.setVaryColors(false);
            
            // 添加数据系列
            XDDFBarChartData.Series series = (XDDFBarChartData.Series) data.addSeries(categories, values);
            series.setTitle("Frequency", null);
            
            // 如果需要添加平均值线
            if (!Double.isNaN(meanValue) && dataSeriesCount == 1) {
                // 计算平均值线的X位置
                double xPosition = 0;
                for (double value : xValueList) {
                    if (value > meanValue) {
                        break;
                    }
                    xPosition++;
                }
                
                // 创建第二个数据系列作为平均值线（使用散点图）
                XDDFValueAxis bottomValueAxis = chart.createValueAxis(AxisPosition.BOTTOM);
                bottomValueAxis.setVisible(false);
                
                XDDFScatterChartData scatterData = (XDDFScatterChartData) chart.createData(
                    ChartTypes.SCATTER, bottomValueAxis, leftAxis
                );
                
                // 创建平均值线的数据
                Double[] xData = new Double[] { xPosition, xPosition };
                Double[] yData = new Double[] { 0.0, maxYValue };
                
                XDDFNumericalDataSource<Double> xDataSource = XDDFDataSourcesFactory.fromArray(xData);
                XDDFNumericalDataSource<Double> yDataSource = XDDFDataSourcesFactory.fromArray(yData);
                
                XDDFScatterChartData.Series meanSeries = (XDDFScatterChartData.Series) 
                    scatterData.addSeries(xDataSource, yDataSource);
                meanSeries.setTitle("Mean", null);
                meanSeries.setSmooth(true);
                meanSeries.setMarkerStyle(MarkerStyle.NONE);
                
                // 绘制散点图
                chart.plot(scatterData);
            }
            
            // 绘制柱状图
            chart.plot(data);
            
            // 设置X轴标签文字方向为垂直
            try {
                // 首先创建文本属性
                bottomAxis.getOrAddTextProperties();
                
                // 设置X轴标签旋转为-90度（垂直）
                // -90度 = -5400000 (POI使用1/60000度为单位)
                chart.getCTChart().getPlotArea().getCatAxArray(0)
                    .getTxPr().getBodyPr().setRot(-5400000);
            } catch (Exception e) {
                System.err.println("设置X轴标签旋转失败: " + e.getMessage());
                // 如果失败，继续执行，不影响图表生成
            }
            
        } catch (Exception e) {
            System.err.println("创建直方图时出错: " + e.getMessage());
            e.printStackTrace();
            // 记录错误但不中断执行，不抛出异常
        }
    }
    
    /**
     * 简单的线图创建方法
     */
    public void CreateSimpleLineChart(int startRowIndex, int startColIndex, int endRowIndex, int endColIndex) {
        // 简化版本，直接在当前工作表创建图表
        try {
            XSSFSheet sheet = (XSSFSheet) excelHelper.getCurrentSheet();
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 
                endColIndex + 2, startRowIndex, 
                endColIndex + 12, startRowIndex + 20);
            
            XSSFChart chart = drawing.createChart(anchor);
            
            // 创建轴
            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setCrossBetween(AxisCrossBetween.BETWEEN);
            
            // 数据源
            CellRangeAddress categoriesRange = new CellRangeAddress(
                startRowIndex, endRowIndex - 1, 
                startColIndex - 1, startColIndex - 1
            );
            
            XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(
                sheet, categoriesRange
            );
            
            // 创建线图
            XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
            
            // 添加所有数据列作为系列
            for (int col = startColIndex; col < endColIndex; col++) {
                CellRangeAddress valuesRange = new CellRangeAddress(
                    startRowIndex, endRowIndex - 1,
                    col - 1, col - 1
                );
                
                XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(
                    sheet, valuesRange
                );
                
                XDDFLineChartData.Series series = (XDDFLineChartData.Series) data.addSeries(categories, values);
                series.setSmooth(false);
                series.setMarkerStyle(MarkerStyle.NONE);
            }
            
            chart.plot(data);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}