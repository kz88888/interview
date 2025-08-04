package com.qsystem.clientlib;

import java.time.LocalDateTime;
import java.util.UUID;
import com.qsystem.clientlib.JsonModel.OrderSymbolType;

/**
 * 持仓接口
 * 对应C# QSystem.ClientLib.IPosition
 */
public abstract class IPosition {
    public String Symbol;
    public OrderSymbolType SymbolType;
    public double PriceEntered;
    public double Quantity;
    public LocalDateTime TimeStamp;
    public UUID PositionGroupId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public PositionState PositionState;
    public boolean PositionActive = false;
    public double DeltaBasedPositionSize;
    public double Delta;
    public double Theta;
    public double Vega;
    public PositionType PositiveOrNegative;
} 