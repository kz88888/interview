package com.qsystem.clientlib.JsonModel;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ITradeConfirmation {
    public String Symbol;
    public String UnderlyingSymbol;
    public OrderSymbolType SymbolType = OrderSymbolType.Stock;
    public String OrderType;
    public double Price;
    public double Quantity;
    public String Note;
    public LocalDateTime TimeStamp;
    public UUID TradeGroupID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public String TradeGroupIDStr;
    public String ParentIDStr;
    public ExpiredOptionType OrderOptionType = ExpiredOptionType.Null;
    public double TransactionExpenses = 0;
    public LocalDateTime OrderTime = LocalDateTime.MIN;
    public TradeStatus OrderStatus = TradeStatus.NULL;
    public String OrderStatusMessage = "";
    public String OrderId;
    public LocalDateTime ExecutionTime = LocalDateTime.MIN;
    public OrderCategory orderCategory = OrderCategory.Executable_Client;

    // Getters and Setters for properties (matching C# pattern)
    public String getSymbol() { return Symbol; }
    public void setSymbol(String symbol) { Symbol = symbol; }
    public String getUnderlyingSymbol() { return UnderlyingSymbol; }
    public void setUnderlyingSymbol(String underlyingSymbol) { UnderlyingSymbol = underlyingSymbol; }
    public OrderSymbolType getSymbolType() { return SymbolType; }
    public void setSymbolType(OrderSymbolType symbolType) { SymbolType = symbolType; }
    public String getOrderType() { return OrderType; }
    public void setOrderType(String orderType) { OrderType = orderType; }
    public double getPrice() { return Price; }
    public void setPrice(double price) { Price = price; }
    public double getQuantity() { return Quantity; }
    public void setQuantity(double quantity) { Quantity = quantity; }
    public String getNote() { return Note; }
    public void setNote(String note) { Note = note; }
    public LocalDateTime getTimeStamp() { return TimeStamp; }
    public void setTimeStamp(LocalDateTime timeStamp) { TimeStamp = timeStamp; }
    public UUID getTradeGroupID() { return TradeGroupID; }
    public void setTradeGroupID(UUID tradeGroupID) { TradeGroupID = tradeGroupID; }
    public String getTradeGroupIDStr() { return TradeGroupIDStr; }
    public void setTradeGroupIDStr(String tradeGroupIDStr) { TradeGroupIDStr = tradeGroupIDStr; }
    public String getParentIDStr() { return ParentIDStr; }
    public void setParentIDStr(String parentIDStr) { ParentIDStr = parentIDStr; }
    public double getTransactionExpenses() { return TransactionExpenses; }
    public void setTransactionExpenses(double transactionExpenses) { TransactionExpenses = transactionExpenses; }
    public LocalDateTime getOrderTime() { return OrderTime; }
    public void setOrderTime(LocalDateTime orderTime) { OrderTime = orderTime; }
    public TradeStatus getOrderStatus() { return OrderStatus; }
    public void setOrderStatus(TradeStatus orderStatus) { OrderStatus = orderStatus; }
    public String getOrderStatusMessage() { return OrderStatusMessage; }
    public void setOrderStatusMessage(String orderStatusMessage) { OrderStatusMessage = orderStatusMessage; }
    public String getOrderId() { return OrderId; }
    public void setOrderId(String orderId) { OrderId = orderId; }
    public LocalDateTime getExecutionTime() { return ExecutionTime; }
    public void setExecutionTime(LocalDateTime executionTime) { ExecutionTime = executionTime; }
    public OrderCategory getOrderCategory() { return orderCategory; }
    public void setOrderCategory(OrderCategory orderCategory) { this.orderCategory = orderCategory; }

    public abstract String ID();
} 