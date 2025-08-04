package com.qsystem.clientlib;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 策略基类
 */
public class StrategyBase {
    public String name;
    public String id;
    public double marginLeft;
    public boolean isChecked = false;
    public boolean isVisible = true;
    public boolean showLogButton = false;
    public boolean showReportButton = false;
    public boolean showDeleteButton = false;
    public boolean showCSVButton = false;
    public List<StrategyInfo> subordinates;
    public PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    public StrategyBase() {
        this.showLogButton = false;
        this.showReportButton = false;
        this.showDeleteButton = false;
        this.showCSVButton = false;
        this.id = UUID.randomUUID().toString();
        this.subordinates = new ArrayList<>();
    }
    
    protected void OnPropertyChanged(String propertyName) {
        propertyChangeSupport.firePropertyChange(propertyName, null, null);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
} 