package com.qsystem.clientlib;

import java.time.LocalDateTime;
import java.util.UUID;
import com.qsystem.clientlib.JsonModel.OrderSymbolType;
import com.qsystem.clientlib.JsonModel.ExpiredOptionType;
import com.qsystem.clientlib.JsonModel.OrderCategory;
import com.qsystem.clientlib.PortfolioSimulationConfig.OptionOrderType;
import com.qsystem.clientlib.PortfolioSimulationConfig.StockOrderType;

public abstract class IOrder {
    public String Symbol;
    public String UnderlyingSymbol;
    public OrderSymbolType SymbolType;
    public String OrderType;
    public double Price;
    public double Quantity;
    public LocalDateTime TimeStamp;
    public UUID TradeGroupID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public ExpiredOptionType OrderOptionType;
    public double TransactionExpenses = 0;
    public LocalDateTime OrderTime = LocalDateTime.MIN;
    public LocalDateTime ExpectedExecutionTime = LocalDateTime.MIN;
    public String OrderId;
    public boolean UpdatePirce = true;
    public OptionOrderType OptionOrderType;
    public StockOrderType StockOrderType;
    public OrderDiection OrderDiection;
    public OrderStatus OrderStatus;
    public String OrderStatusMessage = "";
    public OrderCategory OrderCategory;
} 