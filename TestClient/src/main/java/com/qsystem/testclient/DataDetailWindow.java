package com.qsystem.testclient;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * 数据详情窗口 - 显示查询的详细数据
 */
public class DataDetailWindow extends JDialog {
    private static final Logger logger = Logger.getLogger(DataDetailWindow.class);
    
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private String tagName;
    private List<String[]> dataList;
    
    public DataDetailWindow(JDialog parent, String tagName, List<String[]> dataList) {
        super(parent, "数据详情2 - " + tagName, true);
        this.tagName = tagName;
        this.dataList = dataList;
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initializeUI();
        loadData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Tag: " + tagName);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titlePanel.add(titleLabel);
        
        // 根据数据类型确定列名
        String[] columnNames = getColumnNames();
        
        // 数据表格
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 禁止编辑
            }
        };
        
        dataTable = new JTable(tableModel);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.getTableHeader().setReorderingAllowed(false);
        
        // 设置列宽
        if (columnNames.length == 2) {
            dataTable.getColumnModel().getColumn(0).setPreferredWidth(200);
            dataTable.getColumnModel().getColumn(1).setPreferredWidth(600);
        } else if (columnNames.length == 6) {
            dataTable.getColumnModel().getColumn(0).setPreferredWidth(180);
            for (int i = 1; i < 6; i++) {
                dataTable.getColumnModel().getColumn(i).setPreferredWidth(124);
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        // 底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        JButton exportButton = new JButton("导出数据");
        exportButton.addActionListener(e -> exportData(tagName));
        
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);
        
        // 状态栏
        JLabel statusLabel = new JLabel("共 " + dataList.size() + " 条记录");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.PAGE_END);
    }
    
    private String[] getColumnNames() {
        // 解析tag名称来确定数据类型
        String[] parts = tagName.split("_");
        if (parts.length > 0) {
            String tagNameB = parts[parts.length - 1];
            String[] tagNameBStrs = tagNameB.split("\\.");
            
            if (tagNameBStrs.length == 3) {
                String dataType = tagNameBStrs[2].toUpperCase();
                switch (dataType) {
                    case "BAR":
                        return new String[]{"Timestamp", "open", "high", "low", "close", "Volume"};
                    case "OC":
                        return new String[]{"Timestamp", "Ask", "Bid", "Last", "Open_interest", "Volume"};
                    case "OG":
                        return new String[]{"Timestamp", "Delta", "Gamma", "IV", "Theta", "Vega"};
                    default:
                        return new String[]{"Timestamp", "Value"};
                }
            }
        }
        return new String[]{"Timestamp", "Value"};
    }
    
    private void loadData() {
        if (dataList != null && !dataList.isEmpty()) {
            // 解析tag名称来确定数据类型
            String[] parts = tagName.split("_");
            if (parts.length > 0) {
                String tagNameB = parts[parts.length - 1];
                String[] tagNameBStrs = tagNameB.split("\\.");
                
                if (tagNameBStrs.length == 3) {
                    String dataType = tagNameBStrs[2].toUpperCase();
                    switch (dataType) {
                        case "BAR":
                            loadBarData();
                            break;
                        case "OC":
                            loadOCData();
                            break;
                        case "OG":
                            loadOGData();
                            break;
                        default:
                            loadIntData();
                            break;
                    }
                } else {
                    loadIntData();
                }
            } else {
                loadIntData();
            }
        }
        
        // 更新状态栏
        SwingUtilities.invokeLater(() -> {
            Component[] components = getContentPane().getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel && ((JLabel)comp).getText().startsWith("共")) {
                    ((JLabel)comp).setText("共 " + tableModel.getRowCount() + " 条记录");
                    break;
                }
            }
        });
    }
    
    private void loadBarData() {
        for (String[] row : dataList) {
            if (row.length >= 2) {
                String[] cols = row[1].split(",");
                if (cols.length >= 5) {
                    // 提取时间部分 (最后一个元素)
                    String timeStr = cols.length > 5 ? cols[5].trim() : "";
                    String strTS = row[0] + " " + timeStr;
                    String[] data = {strTS, 
                                   cols[0].trim(), 
                                   cols[1].trim(), 
                                   cols[2].trim(), 
                                   cols[3].trim(), 
                                   cols[4].trim()};
                    tableModel.addRow(data);
                }
            }
        }
    }
    
    private void loadOCData() {
        for (String[] row : dataList) {
            if (row.length >= 2) {
                String[] cols = row[1].split(",");
                if (cols.length >= 5) {
                    // 提取时间部分 (最后一个元素)
                    String timeStr = cols.length > 5 ? cols[5].trim() : "";
                    String strTS = row[0] + " " + timeStr;
                    String[] data = {strTS, 
                                   cols[0].trim(), 
                                   cols[1].trim(), 
                                   cols[2].trim(), 
                                   cols[3].trim(), 
                                   cols[4].trim()};
                    tableModel.addRow(data);
                }
            }
        }
    }
    
    private void loadOGData() {
        for (String[] row : dataList) {
            if (row.length >= 2) {
                String[] cols = row[1].split(",");
                if (cols.length >= 5) {
                    // 提取时间部分 (最后一个元素)
                    String timeStr = cols.length > 5 ? cols[5].trim() : "";
                    String strTS = row[0] + " " + timeStr;
                    String[] data = {strTS, 
                                   cols[0].trim(), 
                                   cols[1].trim(), 
                                   cols[2].trim(), 
                                   cols[3].trim(), 
                                   cols[4].trim()};
                    tableModel.addRow(data);
                }
            }
        }
    }
    
    private void loadIntData() {
        for (String[] row : dataList) {
            if (row.length >= 2) {
                String[] cols = row[1].split(",");
                if (cols.length >= 2) {
                    String strTS = row[0] + " " + cols[1].trim();
                    String[] data = {strTS, cols[0].trim()};
                    tableModel.addRow(data);
                } else if (cols.length >= 1) {
                    // 如果只有一个值，时间戳单独显示
                    String[] data = {row[0], cols[0].trim()};
                    tableModel.addRow(data);
                }
            }
        }
    }
    
    private void exportData(String tagName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File(tagName.replace("/", "_") + ".csv"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new java.io.File(file.getAbsolutePath() + ".csv");
            }
            
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                // 写入列标题
                int columnCount = tableModel.getColumnCount();
                StringBuilder headerLine = new StringBuilder();
                for (int i = 0; i < columnCount; i++) {
                    if (i > 0) headerLine.append(",");
                    headerLine.append(tableModel.getColumnName(i));
                }
                writer.println(headerLine.toString());
                
                // 写入数据
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    StringBuilder dataLine = new StringBuilder();
                    for (int col = 0; col < columnCount; col++) {
                        if (col > 0) dataLine.append(",");
                        Object value = tableModel.getValueAt(row, col);
                        dataLine.append(value != null ? value.toString() : "");
                    }
                    writer.println(dataLine.toString());
                }
                
                JOptionPane.showMessageDialog(this, 
                    "数据已导出到: " + file.getAbsolutePath(), 
                    "导出成功", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                logger.error("Export data failed", ex);
                JOptionPane.showMessageDialog(this, 
                    "导出失败: " + ex.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}