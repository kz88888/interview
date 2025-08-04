package com.qsystem.clientlib.JsonModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RiskMeasurement {
    public double balance;
    public double benchMark;
    public double exposure;
    public double grossExposure;
    public double deltaDollar;
    public double vega;
    public double theta;
    public double margin;

    // Getters and Setters
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public double getBenchMark() { return benchMark; }
    public void setBenchMark(double benchMark) { this.benchMark = benchMark; }
    public double getExposure() { return exposure; }
    public void setExposure(double exposure) { this.exposure = exposure; }
    public double getGrossExposure() { return grossExposure; }
    public void setGrossExposure(double grossExposure) { this.grossExposure = grossExposure; }
    public double getDeltaDollar() { return deltaDollar; }
    public void setDeltaDollar(double deltaDollar) { this.deltaDollar = deltaDollar; }
    public double getVega() { return vega; }
    public void setVega(double vega) { this.vega = vega; }
    public double getTheta() { return theta; }
    public void setTheta(double theta) { this.theta = theta; }
    public double getMargin() { return margin; }
    public void setMargin(double margin) { this.margin = margin; }

    public String getRMMember(String title) {
        if (balance == 0 && benchMark == 0 && exposure == 0 && grossExposure == 0 && 
            deltaDollar == 0 && vega == 0 && theta == 0 && margin == 0)
            return "DNA";
        
        String get_title = title.toUpperCase();
        if (get_title.equals("BALANCE")) {
            return String.valueOf(balance);
        }
        if (get_title.equals("BENCHMARK")) {
            return String.valueOf(benchMark);
        }
        if (get_title.equals("EXPOSURE")) {
            return String.valueOf(exposure);
        }
        if (get_title.equals("GROSSEXPOSURE")) {
            return String.valueOf(grossExposure);
        }
        if (get_title.equals("DELTADOLLAR")) {
            return String.valueOf(deltaDollar);
        }
        if (get_title.equals("VEGA")) {
            return String.valueOf(vega);
        }
        else if (get_title.equals("THETA")) {
            return String.valueOf(theta);
        }
        else if (get_title.equals("MARGIN")) {
            return String.valueOf(margin);
        }
        else {
            return "";
        }
    }
} 