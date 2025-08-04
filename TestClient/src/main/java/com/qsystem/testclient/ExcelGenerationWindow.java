package com.qsystem.testclient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import org.apache.log4j.Logger;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Excel生成窗口 - 读取JSON文件并生成Excel分析文件
 */
public class ExcelGenerationWindow extends JDialog {
    private static final Logger logger = Logger.getLogger(ExcelGenerationWindow.class);
    
    private JTextField textFieldJsonPath;
    private JTextField textFieldStrategyName;
    private JTextArea textAreaServerPath;
    private JProgressBar progressBar;
    private JButton buttonSelectFile;
    private JButton buttonGenerate;
    
    public ExcelGenerationWindow(JFrame parent) {
        super(parent, "生成Excel分析文件", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // JSON文件选择区域
        JPanel filePanel = new JPanel(new BorderLayout(10, 0));
        filePanel.setBorder(BorderFactory.createTitledBorder("选择JSON文件"));
        filePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        textFieldJsonPath = new JTextField();
        textFieldJsonPath.setEditable(false);
        buttonSelectFile = new JButton("浏览...");
        buttonSelectFile.addActionListener(e -> selectJsonFile());
        
        filePanel.add(textFieldJsonPath, BorderLayout.CENTER);
        filePanel.add(buttonSelectFile, BorderLayout.EAST);
        
        // 策略名称输入区域
        JPanel strategyPanel = new JPanel(new BorderLayout(10, 0));
        strategyPanel.setBorder(BorderFactory.createTitledBorder("策略名称"));
        strategyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        textFieldStrategyName = new JTextField();
        strategyPanel.add(new JLabel("策略名称:"), BorderLayout.WEST);
        strategyPanel.add(textFieldStrategyName, BorderLayout.CENTER);
        
        // 服务器路径显示区域
        JPanel serverPathPanel = new JPanel(new BorderLayout());
        serverPathPanel.setBorder(BorderFactory.createTitledBorder("服务器生成路径"));
        
        textAreaServerPath = new JTextArea(3, 40);
        textAreaServerPath.setEditable(false);
        textAreaServerPath.setLineWrap(true);
        textAreaServerPath.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textAreaServerPath);
        serverPathPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 进度条
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("");
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonGenerate = new JButton("生成Excel");
        buttonGenerate.addActionListener(e -> generateExcel());
        buttonGenerate.setEnabled(false);
        
        JButton buttonClose = new JButton("关闭");
        buttonClose.addActionListener(e -> dispose());
        
        buttonPanel.add(buttonGenerate);
        buttonPanel.add(buttonClose);
        
        // 添加组件到主面板
        mainPanel.add(filePanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(strategyPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(serverPathPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(progressBar);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 监听文本变化
        textFieldJsonPath.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { checkEnableGenerate(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { checkEnableGenerate(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkEnableGenerate(); }
        });
        
        textFieldStrategyName.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { checkEnableGenerate(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { checkEnableGenerate(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkEnableGenerate(); }
        });
    }
    
    private void selectJsonFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            textFieldJsonPath.setText(selectedFile.getAbsolutePath());
            
            // 自动填充策略名称（使用文件名，去掉.json后缀）
            String fileName = selectedFile.getName();
            if (fileName.toLowerCase().endsWith(".json")) {
                fileName = fileName.substring(0, fileName.length() - 5);
            }
            textFieldStrategyName.setText(fileName);
        }
    }
    
    private void checkEnableGenerate() {
        boolean enable = !textFieldJsonPath.getText().trim().isEmpty() && 
                        !textFieldStrategyName.getText().trim().isEmpty();
        buttonGenerate.setEnabled(enable);
    }
    
    private void generateExcel() {
        String jsonPath = textFieldJsonPath.getText().trim();
        String strategyName = textFieldStrategyName.getText().trim();
        
        if (jsonPath.isEmpty() || strategyName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择JSON文件并输入策略名称", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 禁用按钮，防止重复点击
        buttonGenerate.setEnabled(false);
        buttonSelectFile.setEnabled(false);
        
        // 在后台线程执行
        SwingWorker<String, String> worker = new SwingWorker<String, String>() {
            @Override
            protected String doInBackground() throws Exception {
                try {
                    // Step 1: 读取JSON文件
                    publish("正在读取JSON文件...");
                    String jsonContent = new String(Files.readAllBytes(Paths.get(jsonPath)));
                    
                    // Step 2: 调用gRPC生成Excel
                    publish("正在生成Excel...");
                    ExcelServiceClient client = new ExcelServiceClient("localhost", 50051);
                    String serverExcelPath = null;
                    
                    try {
                        serverExcelPath = client.generateExcel(jsonContent, strategyName);
                        publish("Excel生成成功: " + serverExcelPath);
                    } finally {
                        client.shutdown();
                    }
                    
                    // Step 3: 复制Excel文件到本地
                    publish("正在复制Excel文件到本地...");
                    File jsonFile = new File(jsonPath);
                    String localExcelPath = jsonFile.getParent() + File.separator + strategyName + ".xlsx";
                    
                    // 复制文件
                    Files.copy(
                        new File(serverExcelPath).toPath(),
                        new File(localExcelPath).toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    );
                    
                    publish("Excel文件已保存到: " + localExcelPath);
                    
                    // Step 4: 打开Excel文件
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(new File(localExcelPath));
                        publish("Excel文件已打开");
                    }
                    
                    return serverExcelPath;
                    
                } catch (Exception e) {
                    logger.error("Generate Excel failed", e);
                    throw e;
                }
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    progressBar.setString(message);
                    if (message.startsWith("Excel生成成功:") || message.startsWith("Excel文件已保存到:")) {
                        textAreaServerPath.append(message + "\n");
                    }
                }
            }
            
            @Override
            protected void done() {
                try {
                    String serverPath = get();
                    progressBar.setString("完成");
                    JOptionPane.showMessageDialog(ExcelGenerationWindow.this, 
                        "Excel文件生成并打开成功！\n服务器路径: " + serverPath, 
                        "成功", 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    progressBar.setString("生成失败");
                    String errorMsg = e.getMessage();
                    if (e.getCause() != null) {
                        errorMsg = e.getCause().getMessage();
                    }
                    JOptionPane.showMessageDialog(ExcelGenerationWindow.this, 
                        "生成失败: " + errorMsg, 
                        "错误", 
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    // 重新启用按钮
                    buttonGenerate.setEnabled(true);
                    buttonSelectFile.setEnabled(true);
                    checkEnableGenerate();
                }
            }
        };
        
        worker.execute();
    }
}