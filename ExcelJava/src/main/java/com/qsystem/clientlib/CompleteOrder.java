package com.qsystem.clientlib;

import com.qsystem.clientlib.JsonModel.TradeConfirmation;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

/**
 * 完整订单类
 */
public class CompleteOrder {
    public UUID TradeGroupID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public String TradeGroupIDStr;
    public String ParentIDStr;
    public double OrderProfit = Double.NaN;
    public double PercentageWinLoss = Double.NaN;
    public List<TradeConfirmation> CompleteTradeList = new ArrayList<>();
    public List<TradeConfirmation> CompleteOrderStockList = null;
    public List<TradeConfirmation> CompleteOrderOptionList = null;
    public boolean Validate = true;
} 