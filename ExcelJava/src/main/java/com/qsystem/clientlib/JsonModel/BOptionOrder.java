package com.qsystem.clientlib.JsonModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BOptionOrder extends BOrder {
    public Double underlyingPrice;
    public String underlyingSymbol;

    public Double getUnderlyingPrice() { return underlyingPrice; }
    public void setUnderlyingPrice(Double underlyingPrice) { this.underlyingPrice = underlyingPrice; }
    public String getUnderlyingSymbol() { return underlyingSymbol; }
    public void setUnderlyingSymbol(String underlyingSymbol) { this.underlyingSymbol = underlyingSymbol; }

    @Override
    public Double getMultiplier() {
        return super.getMultiplier();
    }
    @Override
    public void setMultiplier(Double multiplier) {
        super.setMultiplier(multiplier);
    }
} 