package com.qsystem.clientlib;

import java.time.LocalDateTime;
import java.util.UUID;
import com.qsystem.clientlib.JsonModel.OrderSymbolType;

/**
 * 持仓类
 * 对应C# QSystem.ClientLib.Position
 */
public class Position extends IPosition {
    
    public Position() {
        super();
    }
    
    public String ID() {
        return Symbol;
    }
    
    // Getters and Setters for properties (matching C# pattern)
    public String getSymbol() { return Symbol; }
    public void setSymbol(String symbol) { Symbol = symbol; }
    public OrderSymbolType getSymbolType() { return SymbolType; }
    public void setSymbolType(OrderSymbolType symbolType) { SymbolType = symbolType; }
    public double getPriceEntered() { return PriceEntered; }
    public void setPriceEntered(double priceEntered) { PriceEntered = priceEntered; }
    public double getQuantity() { return Quantity; }
    public void setQuantity(double quantity) { Quantity = quantity; }
    public LocalDateTime getTimeStamp() { return TimeStamp; }
    public void setTimeStamp(LocalDateTime timeStamp) { TimeStamp = timeStamp; }
    public UUID getPositionGroupId() { return PositionGroupId; }
    public void setPositionGroupId(UUID positionGroupId) { PositionGroupId = positionGroupId; }
    public PositionState getPositionState() { return PositionState; }
    public void setPositionState(PositionState positionState) { PositionState = positionState; }
    public boolean isPositionActive() { return PositionActive; }
    public void setPositionActive(boolean positionActive) { PositionActive = positionActive; }
    public double getDeltaBasedPositionSize() { return DeltaBasedPositionSize; }
    public void setDeltaBasedPositionSize(double deltaBasedPositionSize) { DeltaBasedPositionSize = deltaBasedPositionSize; }
    public double getDelta() { return Delta; }
    public void setDelta(double delta) { Delta = delta; }
    public double getTheta() { return Theta; }
    public void setTheta(double theta) { Theta = theta; }
    public double getVega() { return Vega; }
    public void setVega(double vega) { Vega = vega; }
    public PositionType getPositiveOrNegative() { return PositiveOrNegative; }
    public void setPositiveOrNegative(PositionType positiveOrNegative) { PositiveOrNegative = positiveOrNegative; }
} 