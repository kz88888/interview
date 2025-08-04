package com.qsystem.clientlib;

import java.time.LocalDateTime;
import com.qsystem.clientlib.JsonModel.OptionType;

public class OptionOrder extends Order {
    public LocalDateTime OptionExpDate;
    public double OptionStrike;
    public OptionType PutOrCall;
    public String OptionSymbol;
    
    public OptionOrder() {}
    
    public OptionOrder(OptionOrder optionOrder) {
        this.OptionExpDate = optionOrder.OptionExpDate;
        this.OptionStrike = optionOrder.OptionStrike;
        this.PutOrCall = optionOrder.PutOrCall;
        this.OptionSymbol = optionOrder.OptionSymbol;
    }
    
    @Override
    public String ID() {
        return OptionSymbol;
    }
} 