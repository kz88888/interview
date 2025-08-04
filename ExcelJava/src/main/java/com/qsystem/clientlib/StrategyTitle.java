package com.qsystem.clientlib;

/**
 * 策略标题类
 */
public class StrategyTitle extends StrategyBase {
    public String status;
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        String oldValue = this.status;
        this.status = status;
        OnPropertyChanged("Status");
    }
} 