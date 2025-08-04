package com.qsystem.clientlib.JsonModel;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeConfirmation extends ITradeConfirmation {
    public double Underlying;
    public double Close;
    public double LastClose;
    public double Multiplier;
    public double OrderProfit = 0.0;

    public TradeConfirmation() {
        super();
    }

    public TradeConfirmation(TradeConfirmation Order) {
        super();
        setSymbol(Order.getSymbol());
        setUnderlyingSymbol(Order.getUnderlyingSymbol());
        setOrderType(Order.getOrderType());
        setPrice(Order.getPrice());
        setQuantity(Order.getQuantity());
        setTimeStamp(Order.getTimeStamp());
        setOrderTime(Order.getOrderTime());
        setTradeGroupIDStr(Order.getTradeGroupIDStr());
        setParentIDStr(Order.getParentIDStr());
        setOrderId(Order.getOrderId());
        this.Underlying = Order.Underlying;
        setNote(Order.getNote());
        this.Close = Order.Close;
        this.LastClose = Order.LastClose;
        this.Multiplier = Order.Multiplier;
    }

    public TradeConfirmation(Order order) {
        super();
        setSymbol(order.getSymbol());
        setUnderlyingSymbol(order.getUnderlyingSymbol());
        setOrderType(order.getOrderType());
        setPrice(order.getPrice());
        setQuantity(order.getQuantity());
        setOrderId(order.getOrderId());
        setTimeStamp(order.getTimeStamp());
        setOrderTime(order.getOrderTime());
        setTradeGroupID(order.getTradeGroupID());
        setOrderId(order.getOrderId());
    }

    public TradeConfirmation(double price, double quantity) {
        super();
        setPrice(price);
        setQuantity(quantity);
        setTradeGroupID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    @Override
    public String toString() {
        return "OrderTime:" + getOrderTime() + ",OrderType:" + getOrderType() + 
               ",Symbol : " + getSymbol() + ",Price:" + getPrice() + 
               ", Quantity: " + getQuantity() + ",Note :" + getNote();
    }

    public double GetWorth() {
        if (getSymbolType() == OrderSymbolType.Option) {
            return (-1) * getPrice() * getQuantity() * Multiplier;
        }
        return (-1) * getPrice() * getQuantity();
    }

    public String OrderGuidString() {
        String guidStr = getTradeGroupID().toString();
        if (guidStr.length() > 12) {
            guidStr = guidStr.substring(guidStr.length() - 12);
        }
        return guidStr;
    }

    public String GetOrderMember(String title) {
        String get_title = title.toUpperCase();
        if (get_title.equals("SYMBOL")) {
            if (getSymbolType() == OrderSymbolType.Stock) {
                return getSymbol();
            }
            if (getSymbolType() == OrderSymbolType.Option) {
                return getUnderlyingSymbol();
            }
            return "";
        }
        else if (get_title.equals("OPTIONSYMBOL")) {
            if (getSymbolType() == OrderSymbolType.Stock) {
                return "";
            }
            if (getSymbolType() == OrderSymbolType.Option) {
                return getSymbol();
            }
            return "";
        }
        else if (get_title.equals("TIMESTAMP")) {
            return getTimeStamp().toLocalDate().toString();
        }
        else if (get_title.equals("ORDERTIME")) {
            return getOrderTime().toLocalTime().toString();
        }
        else if (get_title.equals("ORDERTYPE")) {
            return getOrderType();
        }
        else if (get_title.equals("PRICE")) {
            return String.format("%.4f", getPrice());
        }
        else if (get_title.equals("QUANTITY")) {
            return String.format("%.4f", getQuantity());
        }
        else if (get_title.equals("ORDERID")) {
            return getOrderId();
        }
        else if (get_title.equals("TRADEGROUPID")) {
            return OrderGuidString();
        }
        else if (get_title.equals("TRANSACTIONEXPENSES")) {
            return String.format("%.4f", getTransactionExpenses());
        }
        else if (get_title.equals("ORDERCATEGORY")) {
            return getOrderCategory().toString();
        }
        else if (get_title.equals("ORDERSTATUS")) {
            return getOrderStatus().toString();
        }
        else if (get_title.equals("NOTE")) {
            return getNote();
        }
        else if (get_title.equals("UNDERLYING")) {
            return String.valueOf(Underlying);
        }
        else {
            return "";
        }
    }

    @Override
    public String ID() {
        return getSymbol();
    }

    // Getters and Setters for properties (matching C# pattern)
    public double getUnderlying() { return Underlying; }
    public void setUnderlying(double underlying) { Underlying = underlying; }
    public double getClose() { return Close; }
    public void setClose(double close) { Close = close; }
    public double getLastClose() { return LastClose; }
    public void setLastClose(double lastClose) { LastClose = lastClose; }
    public double getMultiplier() { return Multiplier; }
    public void setMultiplier(double multiplier) { Multiplier = multiplier; }
    public double getOrderProfit() { return OrderProfit; }
    public void setOrderProfit(double orderProfit) { OrderProfit = orderProfit; }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class OptionTradeConfirmation extends TradeConfirmation {
    public LocalDateTime OptionExpDate;
    public double OptionStrike;
    public OptionType PutOrCall;
    public String OptionSymbol;

    public OptionTradeConfirmation() {
        super();
    }

    public OptionTradeConfirmation(OptionTradeConfirmation optionTradeConfirmation) {
        super(optionTradeConfirmation);
        this.OptionExpDate = optionTradeConfirmation.OptionExpDate;
        this.OptionStrike = optionTradeConfirmation.OptionStrike;
        this.PutOrCall = optionTradeConfirmation.PutOrCall;
        this.OptionSymbol = optionTradeConfirmation.OptionSymbol;
    }

    @Override
    public String ID() {
        return OptionSymbol;
    }

    // Getters and Setters
    public LocalDateTime getOptionExpDate() { return OptionExpDate; }
    public void setOptionExpDate(LocalDateTime optionExpDate) { OptionExpDate = optionExpDate; }
    public double getOptionStrike() { return OptionStrike; }
    public void setOptionStrike(double optionStrike) { OptionStrike = optionStrike; }
    public OptionType getPutOrCall() { return PutOrCall; }
    public void setPutOrCall(OptionType putOrCall) { PutOrCall = putOrCall; }
    public String getOptionSymbol() { return OptionSymbol; }
    public void setOptionSymbol(String optionSymbol) { OptionSymbol = optionSymbol; }
} 