package com.qsystem.testclient;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

/**
 * 数据查询窗口 - Swing版本
 * 基于qsclient的DataQueryWindow2改编
 */
public class DataQueryWindow extends JDialog {
    private static final Logger logger = Logger.getLogger(DataQueryWindow.class);
    
    private JTextField textFieldQuery;
    private JTree treeView;
    private DefaultTreeModel treeModel;
    private JButton buttonQuery;
    private ObjectMapper objectMapper = new ObjectMapper();
    
    // TreeView节点数据类
    public static class TreeViewNodeItem {
        public String name;
        public List<TreeViewNodeItem> children;
        
        public TreeViewNodeItem() {
            children = new ArrayList<>();
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public DataQueryWindow(JFrame parent) {
        super(parent, "数据查询", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initializeUI();
        displayAllData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 顶部查询面板
        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        queryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel labelQuery = new JLabel("查询关键词:");
        textFieldQuery = new JTextField(20);
        buttonQuery = new JButton("查询");
        
        queryPanel.add(labelQuery);
        queryPanel.add(textFieldQuery);
        queryPanel.add(buttonQuery);
        
        // 树形视图
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(root);
        treeView = new JTree(treeModel);
        treeView.setRootVisible(false);
        treeView.setShowsRootHandles(true);
        
        JScrollPane scrollPane = new JScrollPane(treeView);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        add(queryPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // 事件处理
        buttonQuery.addActionListener(e -> handleQuery());
        textFieldQuery.addActionListener(e -> handleQuery());
        
        // 双击事件
        treeView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleTreeViewDoubleClick();
                }
            }
        });
    }
    
    private void displayAllData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // 显示查询进度
                JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);
                JDialog progressDialog = new JDialog(this, "查询中", true);
                progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                progressDialog.add(progressBar);
                progressDialog.setSize(200, 60);
                progressDialog.setLocationRelativeTo(this);
                
                SwingWorker<List<String>, Void> worker = new SwingWorker<List<String>, Void>() {
                    @Override
                    protected List<String> doInBackground() throws Exception {
                        // 连接Excel服务查询
                        ExcelServiceClient client = new ExcelServiceClient("localhost", 50051);
                        try {
                            return client.query("ALL");
                        } finally {
                            client.shutdown();
                        }
                    }
                    
                    @Override
                    protected void done() {
                        progressDialog.dispose();
                        try {
                            List<String> results = get();
                            if (results != null && !results.isEmpty()) {
                                showTreeView(results);
                            } else {
                                JOptionPane.showMessageDialog(DataQueryWindow.this, 
                                    "什么也没找到", "提示", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (Exception ex) {
                            logger.error("Query failed", ex);
                            JOptionPane.showMessageDialog(DataQueryWindow.this, 
                                "查询失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                
                worker.execute();
                progressDialog.setVisible(true);
                
            } catch (Exception ex) {
                logger.error("Display all data failed", ex);
                JOptionPane.showMessageDialog(this, 
                    "查询失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void handleQuery() {
        String queryText = textFieldQuery.getText().trim();
        if (queryText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入查询关键词", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                // 显示查询进度
                JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);
                JDialog progressDialog = new JDialog(this, "查询中", true);
                progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                progressDialog.add(progressBar);
                progressDialog.setSize(200, 60);
                progressDialog.setLocationRelativeTo(this);
                
                SwingWorker<List<String>, Void> worker = new SwingWorker<List<String>, Void>() {
                    @Override
                    protected List<String> doInBackground() throws Exception {
                        // 连接Excel服务查询
                        ExcelServiceClient client = new ExcelServiceClient("localhost", 50051);
                        try {
                            return client.query(queryText.toUpperCase());
                        } finally {
                            client.shutdown();
                        }
                    }
                    
                    @Override
                    protected void done() {
                        progressDialog.dispose();
                        try {
                            List<String> results = get();
                            if (results != null && !results.isEmpty()) {
                                showTreeView(results);
                            } else {
                                JOptionPane.showMessageDialog(DataQueryWindow.this, 
                                    "什么也没找到", "提示", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (Exception ex) {
                            logger.error("Query failed", ex);
                            JOptionPane.showMessageDialog(DataQueryWindow.this, 
                                "查询失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                
                worker.execute();
                progressDialog.setVisible(true);
                
            } catch (Exception ex) {
                logger.error("Handle query failed", ex);
                JOptionPane.showMessageDialog(this, 
                    "查询失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void showTreeView(List<String> nodeItemList) {
        if (nodeItemList == null || nodeItemList.isEmpty()) {
            return;
        }
        
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        rootNode.removeAllChildren();
        
        for (String nodeItemStr : nodeItemList) {
            try {
                TreeViewNodeItem nodeItem = objectMapper.readValue(
                    nodeItemStr.replace("_", "__"), 
                    TreeViewNodeItem.class
                );
                DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(nodeItem);
                
                // 如果有子项，递归添加子节点
                if (nodeItem.children != null && !nodeItem.children.isEmpty()) {
                    addChildNodes(treeNode, nodeItem.children);
                }
                
                rootNode.add(treeNode);
            } catch (Exception e) {
                logger.error("Failed to parse node item", e);
            }
        }
        
        treeModel.reload();
        
        // 保持树形视图折叠状态，不自动展开
    }
    
    private void addChildNodes(DefaultMutableTreeNode parentNode, List<TreeViewNodeItem> children) {
        for (TreeViewNodeItem child : children) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
            
            // 递归处理子节点的子节点
            if (child.children != null && !child.children.isEmpty()) {
                addChildNodes(childNode, child.children);
            }
            
            parentNode.add(childNode);
        }
    }
    
    private void handleTreeViewDoubleClick() {
        TreePath selectedPath = treeView.getSelectionPath();
        if (selectedPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            Object userObject = selectedNode.getUserObject();
            
            if (userObject instanceof TreeViewNodeItem) {
                TreeViewNodeItem nodeItem = (TreeViewNodeItem) userObject;
                
                // 检查是否为叶子节点
                if (nodeItem.children == null || nodeItem.children.isEmpty()) {
                    String labelContent = nodeItem.name;
                    
                    SwingUtilities.invokeLater(() -> {
                        try {
                            // 显示查询进度
                            JProgressBar progressBar = new JProgressBar();
                            progressBar.setIndeterminate(true);
                            JDialog progressDialog = new JDialog(this, "查询详细数据", true);
                            progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                            progressDialog.add(progressBar);
                            progressDialog.setSize(200, 60);
                            progressDialog.setLocationRelativeTo(this);
                            
                            SwingWorker<List<String[]>, Void> worker = new SwingWorker<List<String[]>, Void>() {
                                @Override
                                protected List<String[]> doInBackground() throws Exception {
                                    // 使用Excel服务查询详细数据
                                    ExcelServiceClient client = new ExcelServiceClient("localhost", 50051);
                                    try {
                                        return client.queryData(
                                            labelContent.replace("__", "_").replace("*", "")
                                        );
                                    } finally {
                                        client.shutdown();
                                    }
                                }
                                
                                @Override
                                protected void done() {
                                    progressDialog.dispose();
                                    try {
                                        List<String[]> results = get();
                                        DataDetailWindow detailWindow = new DataDetailWindow(
                                            DataQueryWindow.this,
                                            labelContent.replace("*", ""), 
                                            results
                                        );
                                        detailWindow.setVisible(true);
                                    } catch (Exception ex) {
                                        logger.error("Query data failed", ex);
                                        JOptionPane.showMessageDialog(DataQueryWindow.this, 
                                            "查询数据失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            };
                            
                            worker.execute();
                            progressDialog.setVisible(true);
                            
                        } catch (Exception ex) {
                            logger.error("Handle tree double click failed", ex);
                            JOptionPane.showMessageDialog(this, 
                                "查询失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } else {
                    // 如果有子项，展开或折叠节点
                    if (treeView.isExpanded(selectedPath)) {
                        treeView.collapsePath(selectedPath);
                    } else {
                        treeView.expandPath(selectedPath);
                    }
                }
            }
        }
    }
}