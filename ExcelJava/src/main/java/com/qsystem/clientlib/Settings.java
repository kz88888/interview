package com.qsystem.clientlib;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * 设置类 - 对应C#版本的Settings类
 * 使用单例模式，确保整个应用程序中只有一个Settings实例
 */
public class Settings {
    
    public static Settings instance;
    public Properties properties;
    
    // 私有构造函数，防止外部实例化
    private Settings() {
        properties = new Properties();
        loadSettings();
    }
    
    // 获取单例实例
    public static synchronized Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
    
    private void loadSettings() {
        try {
            // 尝试从classpath加载配置文件
            InputStream input = getClass().getClassLoader().getResourceAsStream("app.config");
            if (input == null) {
                // 如果classpath中没有，尝试从文件系统加载
                input = new FileInputStream("src/main/resources/app.config");
            }
            
            parseAppConfig(input);
            input.close();
        } catch (IOException e) {
            System.err.println("Error loading app.config: " + e.getMessage());
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
    
    private void parseAppConfig(InputStream input) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(input);
            doc.getDocumentElement().normalize();
            
            // 查找appSettings节点
            NodeList appSettingsList = doc.getElementsByTagName("appSettings");
            if (appSettingsList.getLength() > 0) {
                Element appSettings = (Element) appSettingsList.item(0);
                NodeList addNodes = appSettings.getElementsByTagName("add");
                
                for (int i = 0; i < addNodes.getLength(); i++) {
                    Element addElement = (Element) addNodes.item(i);
                    String key = addElement.getAttribute("key");
                    String value = addElement.getAttribute("value");
                    if (key != null && !key.isEmpty()) {
                        properties.setProperty(key, value != null ? value : "");
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Error parsing app.config: " + e.getMessage(), e);
        }
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
} 