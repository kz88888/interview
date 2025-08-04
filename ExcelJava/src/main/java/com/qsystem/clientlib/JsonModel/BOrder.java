package com.qsystem.clientlib.JsonModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BOrder {
    public BInstrument instrument;
    @JsonProperty("ordertype")
    public String orderType;
    public Double price;
    @JsonProperty("nDate")
    public int nDate;
    @JsonProperty("nTime")
    public int nTime;
    public int quantity;
    public Double multiplier;
    @JsonProperty("orderID")
    public String orderID;
    @JsonProperty("tradeGroupID")
    public String tradeGroupID;
    @JsonProperty("parentID")
    public String parentID;
    public double transactionExpenses;
    public String note;

    // Getters and Setters
    public BInstrument getInstrument() { return instrument; }
    public void setInstrument(BInstrument instrument) { this.instrument = instrument; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    @JsonProperty("nDate")
    public int getNDate() { return nDate; }
    @JsonProperty("nDate")
    public void setNDate(int nDate) { this.nDate = nDate; }
    @JsonProperty("nTime")
    public int getNTime() { return nTime; }
    @JsonProperty("nTime")
    public void setNTime(int nTime) { this.nTime = nTime; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Double getMultiplier() { return multiplier; }
    public void setMultiplier(Double multiplier) { this.multiplier = multiplier; }
    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }
    public String getTradeGroupID() { return tradeGroupID; }
    public void setTradeGroupID(String tradeGroupID) { this.tradeGroupID = tradeGroupID; }
    public String getParentID() { return parentID; }
    public void setParentID(String parentID) { this.parentID = parentID; }
    public double getTransactionExpenses() { return transactionExpenses; }
    public void setTransactionExpenses(double transactionExpenses) { this.transactionExpenses = transactionExpenses; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
} 