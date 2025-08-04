package com.qsystem.clientlib;

import java.time.LocalDateTime;
import java.util.UUID;
import com.qsystem.clientlib.JsonModel.OrderSymbolType;

public class Order extends IOrder {
    public Order() {}
    public Order(Order order) {
        this.Symbol = order.Symbol;
        this.OrderType = order.OrderType;
        this.Price = order.Price;
        this.Quantity = order.Quantity;
        this.TimeStamp = order.TimeStamp;
        this.OrderTime = order.OrderTime;
        this.TradeGroupID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
    public Order(double price, double quantity) {
        this.Price = price;
        this.Quantity = quantity;
        this.TradeGroupID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
    public double GetWorth() {
        if (SymbolType == OrderSymbolType.Option) {
            return (-1) * Price * Quantity * 100;
        }
        return (-1) * Price * Quantity;
    }
    public String OrderGuidString() {
        String guidStr = TradeGroupID.toString();
        if (guidStr.length() > 12) {
            guidStr = guidStr.substring(guidStr.length() - 12);
        }
        return guidStr;
    }
    public String GetOrderMember(String title) {
        String get_title = title.toUpperCase();
        if (get_title.equals("SYMBOL")) {
            return Symbol;
        } else if (get_title.equals("TIMESTAMP")) {
            return TimeStamp.toLocalDate().toString();
        } else if (get_title.equals("ORDERTYPE")) {
            return OrderType;
        } else if (get_title.equals("PRICE")) {
            return String.valueOf(Price);
        } else if (get_title.equals("QUANTITY")) {
            return String.format("%.4f", Quantity);
        } else if (get_title.equals("TRADEGROUPID")) {
            return OrderGuidString();
        } else if (get_title.equals("ORDERID")) {
            return OrderId;
        } else if (get_title.equals("TRANSACTIONEXPENSES")) {
            return String.valueOf(TransactionExpenses);
        }
        return "";
    }
    public String ID() {
        return Symbol;
    }
} 