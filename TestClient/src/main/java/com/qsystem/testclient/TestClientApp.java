package com.qsystem.testclient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * TestClient main application
 * Provides two main functions:
 * 1. Query data from ExcelJava server
 * 2. Generate Excel analysis files
 */
public class TestClientApp {
    private static final Logger logger = Logger.getLogger(TestClientApp.class);
    private JFrame mainFrame;
    
    public TestClientApp() {
        initializeUI();
    }
    
    private void initializeUI() {
        mainFrame = new JFrame("Test Client");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
        
        // Create main panel with GridBagLayout for better control
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(20, 20, 20, 20);
        
        // Create title label
        JLabel titleLabel = new JLabel("Test Client", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.1;
        mainPanel.add(titleLabel, gbc);
        
        // Create query data button
        JButton queryDataButton = createSquareButton("查询数据", new Color(52, 152, 219));
        queryDataButton.addActionListener(this::onQueryData);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0.9;
        mainPanel.add(queryDataButton, gbc);
        
        // Create generate Excel button
        JButton generateExcelButton = createSquareButton("生成Excel分析文件", new Color(46, 204, 113));
        generateExcelButton.addActionListener(this::onGenerateExcel);
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(generateExcelButton, gbc);
        
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);
    }
    
    private JButton createSquareButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        
        // Make button appear raised
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    private void onQueryData(ActionEvent e) {
        logger.info("Opening data query window");
        SwingUtilities.invokeLater(() -> {
            DataQueryWindow queryWindow = new DataQueryWindow(mainFrame);
            queryWindow.setVisible(true);
        });
    }
    
    private void onGenerateExcel(ActionEvent e) {
        logger.info("Opening Excel generation window");
        SwingUtilities.invokeLater(() -> {
            ExcelGenerationWindow excelWindow = new ExcelGenerationWindow(mainFrame);
            excelWindow.setVisible(true);
        });
    }
    
    public static void main(String[] args) {
        // Configure logging
        try {
            PropertyConfigurator.configure(TestClientApp.class.getResourceAsStream("/log4j.properties"));
        } catch (Exception e) {
            System.err.println("Failed to configure logging: " + e.getMessage());
        }
        
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.error("Failed to set system look and feel", e);
        }
        
        // Start application
        SwingUtilities.invokeLater(() -> {
            new TestClientApp();
        });
    }
}