package com.qsystem.clientlib.JsonModel;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    private String Symbol;
    private String UnderlyingSymbol;
    @JsonProperty("ordertype")
    private String OrderType;
    private double Price;
    private double Quantity;
    @JsonProperty("orderID")
    private String OrderId;
    private LocalDateTime TimeStamp;
    private LocalDateTime OrderTime;
    @JsonProperty("tradeGroupID")
    private UUID TradeGroupID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    // Getters and Setters
    public String getSymbol() { return Symbol; }
    public void setSymbol(String symbol) { Symbol = symbol; }
    public String getUnderlyingSymbol() { return UnderlyingSymbol; }
    public void setUnderlyingSymbol(String underlyingSymbol) { UnderlyingSymbol = underlyingSymbol; }
    public String getOrderType() { return OrderType; }
    public void setOrderType(String orderType) { OrderType = orderType; }
    public double getPrice() { return Price; }
    public void setPrice(double price) { Price = price; }
    public double getQuantity() { return Quantity; }
    public void setQuantity(double quantity) { Quantity = quantity; }
    public String getOrderId() { return OrderId; }
    public void setOrderId(String orderId) { OrderId = orderId; }
    public LocalDateTime getTimeStamp() { return TimeStamp; }
    public void setTimeStamp(LocalDateTime timeStamp) { TimeStamp = timeStamp; }
    public LocalDateTime getOrderTime() { return OrderTime; }
    public void setOrderTime(LocalDateTime orderTime) { OrderTime = orderTime; }
    public UUID getTradeGroupID() { return TradeGroupID; }
    public void setTradeGroupID(UUID tradeGroupID) { TradeGroupID = tradeGroupID; }
} 