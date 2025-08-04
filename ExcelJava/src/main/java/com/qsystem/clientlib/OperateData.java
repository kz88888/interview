package com.qsystem.clientlib;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 数据操作类
 */
public class OperateData {
    
    /**
     * 流式读取.csv文件
     * @param filePath 文件路径
     * @return 数据表
     */
    public static DataTable Csv2DataTable(String filePath) {
        DataTable dt = new DataTable();
        
        try (FileInputStream fs = new FileInputStream(filePath);
             InputStreamReader isr = new InputStreamReader(fs, Charset.defaultCharset());
             BufferedReader br = new BufferedReader(isr)) {
            
            String strLine;
            String[] aryLine;
            int columnCount = 0;
            boolean isFirst = true;
            
            // 逐行读取CSV中的数据
            while ((strLine = br.readLine()) != null) {
                aryLine = strLine.split(",");
                if (isFirst) {
                    isFirst = false;
                    columnCount = aryLine.length;
                    for (int i = 0; i < columnCount; i++) {
                        String columnName = aryLine[i];
                        if (i == 0) {
                            dt.addColumn(columnName);
                        } else {
                            String newColumnName = columnName + "B";
                            dt.addColumn(newColumnName);
                        }
                    }
                } else {
                    DataTable.DataRow dr = dt.newRow();
                    for (int j = 0; j < columnCount; j++) {
                        dr.setItem(j, aryLine[j]);
                    }
                    dt.addRow(dr);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return dt;
    }
    
    /**
     * 合并两个数据表
     * @param dt1 数据表1
     * @param dt2 数据表2
     * @param key 键列名
     * @return 合并后的数据表
     */
    public static DataTable MergeDataTables(DataTable dt1, DataTable dt2, String key) {
        DataTable dt3 = dt1.clone();
        
        // 添加dt2的列（除了键列）
        for (int i = 0; i < dt2.getColumnCount(); i++) {
            String columnName = dt2.getColumn(i).getColumnName();
            if (!columnName.equalsIgnoreCase(key)) {
                dt3.addColumn(columnName);
            }
        }
        
        // 排序
        List<DataTable.DataRow> sortedDt1 = sort_desc(dt1, key);
        List<DataTable.DataRow> sortedDt2 = sort_desc(dt2, key);
        
        for (int i = 0; i < sortedDt1.size() && i < sortedDt2.size(); i++) {
            DataTable.DataRow row1 = sortedDt1.get(i);
            DataTable.DataRow row2 = sortedDt2.get(i);
            
            if (row1.getItem(key).toString().equals(row2.getItem(key).toString())) {
                DataTable.DataRow dataRow = dt3.newRow();
                
                // 复制dt1的数据
                for (int j = 0; j < dt1.getColumnCount(); j++) {
                    String columnName = dt1.getColumn(j).getColumnName();
                    dataRow.setItem(columnName, row1.getItem(columnName));
                }
                
                // 复制dt2的数据
                for (int k = 0; k < dt2.getColumnCount(); k++) {
                    String columnName = dt2.getColumn(k).getColumnName();
                    dataRow.setItem(columnName, row2.getItem(columnName));
                }
                
                dt3.addRow(dataRow);
            }
        }
        
        return dt3;
    }
    
    /**
     * 用于比较的数据表合并
     * @param dt1 数据表1
     * @param dt2 数据表2
     * @param key 键列名
     * @return 合并后的数据表
     */
    public static DataTable MergeDataTablesForCompare(DataTable dt1, DataTable dt2, String key) {
        DataTable dt3 = dt1.clone();
        
        // 添加dt2的列（除了键列）
        for (int i = 0; i < dt2.getColumnCount(); i++) {
            String columnName = dt2.getColumn(i).getColumnName();
            if (!columnName.equalsIgnoreCase(key)) {
                dt3.addColumn(columnName);
            }
        }
        
        // 排序
        List<DataTable.DataRow> sortedDt1 = sort_desc(dt1, key);
        List<DataTable.DataRow> sortedDt2 = sort_desc(dt2, key);
        
        for (int i = 0; i < sortedDt1.size() && i < sortedDt2.size(); i++) {
            DataTable.DataRow row1 = sortedDt1.get(i);
            DataTable.DataRow row2 = sortedDt2.get(i);
            
            if (row1.getItem(key).toString().equals(row2.getItem(key).toString())) {
                DataTable.DataRow dataRow = dt3.newRow();
                
                // 复制dt1的数据
                for (int j = 0; j < dt1.getColumnCount(); j++) {
                    String columnName = dt1.getColumn(j).getColumnName();
                    dataRow.setItem(columnName, row1.getItem(columnName));
                }
                
                // 复制dt2的数据
                for (int k = 0; k < dt2.getColumnCount(); k++) {
                    String columnName = dt2.getColumn(k).getColumnName();
                    dataRow.setItem(columnName, row2.getItem(columnName));
                }
                
                dt3.addRow(dataRow);
            }
        }
        
        return dt3;
    }
    
    /**
     * 按行合并数据表
     * @param dt1 数据表1
     * @param dt2 数据表2
     * @return 合并后的数据表
     */
    public static DataTable MergeDTbyRow(DataTable dt1, DataTable dt2) {
        DataTable dt = dt2;
        List<String> columnNames = new ArrayList<>();
        
        // 添加新列到数据表dt（如果不存在）
        for (DataTable.DataColumn col : dt1.getColumns()) {
            columnNames.add(col.getColumnName());
            // 测试列是否存在，如果不存在则添加
            if (!dt.containsColumn(col.getColumnName())) {
                dt.addColumn(col.getColumnName());
            }
        }
        
        // 从dt1复制行到dt
        for (DataTable.DataRow row : dt1.getRows()) {
            DataTable.DataRow newRow = dt.newRow();
            // 从dt1添加数据到dt
            for (int i = 0; i < columnNames.size(); i++) {
                newRow.setItem(columnNames.get(i), row.getItem(columnNames.get(i)));
            }
            dt.addRow(newRow);
        }
        
        dt.addRow(dt.newRow()); // 添加空行
        
        return dt;
    }
    
    /**
     * 排序数据表
     * @param dt1 数据表
     * @param key 键列名
     * @return 排序后的数据表
     */
    private static List<DataTable.DataRow> sort_desc(DataTable dt1, String key) {
        List<DataTable.DataRow> result = new ArrayList<>(dt1.getRows());
        
        result.sort((row1, row2) -> {
            Object val1 = row1.getItem(key);
            Object val2 = row2.getItem(key);
            
            if (val1 == null && val2 == null) return 0;
            if (val1 == null) return 1;
            if (val2 == null) return -1;
            
            return val2.toString().compareTo(val1.toString()); // 降序
        });
        
        return result;
    }
    
    /**
     * 数据表转CSV文件
     * @param dt 数据表
     * @param filePath 文件路径
     */
    public static void DataTable2CSV(DataTable dt, String filePath) {
        try (FileOutputStream fs = new FileOutputStream(filePath);
             OutputStreamWriter osw = new OutputStreamWriter(fs, Charset.defaultCharset());
             BufferedWriter bw = new BufferedWriter(osw)) {
            
            // 写入表头
            StringBuilder data = new StringBuilder();
            for (int i = 0; i < dt.getColumnCount(); i++) {
                data.append(dt.getColumn(i).getColumnName());
                if (i < dt.getColumnCount() - 1) {
                    data.append(",");
                }
            }
            bw.write(data.toString());
            bw.newLine();
            
            // 写入表体
            for (int i = 0; i < dt.getRowCount(); i++) {
                data = new StringBuilder();
                DataTable.DataRow row = dt.getRow(i);
                for (int j = 0; j < dt.getColumnCount(); j++) {
                    Object value = row.getItem(j);
                    data.append(value != null ? value.toString() : "");
                    if (j < dt.getColumnCount() - 1) {
                        data.append(",");
                    }
                }
                bw.write(data.toString());
                bw.newLine();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 