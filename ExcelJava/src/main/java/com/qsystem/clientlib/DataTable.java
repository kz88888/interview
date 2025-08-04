package com.qsystem.clientlib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * DataTable类 - C# DataTable的Java等效实现
 */
public class DataTable {
    public List<DataColumn> columns;
    public List<DataRow> rows;
    
    public DataTable() {
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
    }
    
    public DataTable clone() {
        DataTable newTable = new DataTable();
        // 复制列
        for (DataColumn col : this.columns) {
            newTable.columns.add(new DataColumn(col.getColumnName()));
        }
        return newTable;
    }
    
    public void addColumn(DataColumn column) {
        this.columns.add(column);
    }
    
    public void addColumn(String columnName) {
        this.columns.add(new DataColumn(columnName));
    }
    
    public DataRow newRow() {
        DataRow row = new DataRow(this);
        return row;
    }
    
    public void addRow(DataRow row) {
        this.rows.add(row);
    }
    
    public List<DataColumn> getColumns() {
        return columns;
    }
    
    public List<DataRow> getRows() {
        return rows;
    }
    
    public int getColumnCount() {
        return columns.size();
    }
    
    public int getRowCount() {
        return rows.size();
    }
    
    public DataColumn getColumn(int index) {
        return columns.get(index);
    }
    
    public DataColumn getColumn(String columnName) {
        for (DataColumn col : columns) {
            if (col.getColumnName().equalsIgnoreCase(columnName)) {
                return col;
            }
        }
        return null;
    }
    
    public boolean containsColumn(String columnName) {
        return getColumn(columnName) != null;
    }
    
    public DataRow getRow(int index) {
        return rows.get(index);
    }
    
    public List<DataRow> select(String filter, String sort) {
        List<DataRow> result = new ArrayList<>(rows);
        
        // 排序
        if (sort != null && !sort.isEmpty()) {
            String[] sortParts = sort.split(" ");
            String columnName = sortParts[0];
            boolean descending = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc");
            
            final int columnIndex = getColumnIndex(columnName);
            if (columnIndex >= 0) {
                result.sort((row1, row2) -> {
                    Object val1 = row1.getItem(columnIndex);
                    Object val2 = row2.getItem(columnIndex);
                    
                    if (val1 == null && val2 == null) return 0;
                    if (val1 == null) return descending ? 1 : -1;
                    if (val2 == null) return descending ? -1 : 1;
                    
                    int comparison = val1.toString().compareTo(val2.toString());
                    return descending ? -comparison : comparison;
                });
            }
        }
        
        return result;
    }
    
    private int getColumnIndex(String columnName) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getColumnName().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }
    
    public static class DataColumn {
        public String columnName;
        
        public DataColumn(String columnName) {
            this.columnName = columnName;
        }
        
        public String getColumnName() {
            return columnName;
        }
        
        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }
    }
    
    public static class DataRow {
        public DataTable table;
        public Map<String, Object> data;
        
        public DataRow(DataTable table) {
            this.table = table;
            this.data = new HashMap<>();
        }
        
        public void setItem(String columnName, Object value) {
            data.put(columnName, value);
        }
        
        public void setItem(int index, Object value) {
            if (index >= 0 && index < table.getColumnCount()) {
                String columnName = table.getColumn(index).getColumnName();
                data.put(columnName, value);
            }
        }
        
        public Object getItem(String columnName) {
            return data.get(columnName);
        }
        
        public Object getItem(int index) {
            if (index >= 0 && index < table.getColumnCount()) {
                String columnName = table.getColumn(index).getColumnName();
                return data.get(columnName);
            }
            return null;
        }
        
        public Object[] getItemArray() {
            Object[] array = new Object[table.getColumnCount()];
            for (int i = 0; i < table.getColumnCount(); i++) {
                array[i] = getItem(i);
            }
            return array;
        }
        
        public DataTable getTable() {
            return table;
        }
    }
} 