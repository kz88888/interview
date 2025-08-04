package com.qsystem.clientlib;

import java.time.LocalDateTime;
import com.qsystem.clientlib.JsonModel.OptionType;

public class OptionPositionExtention extends PositionExtention {
    public LocalDateTime OptionExpDate;
    public double OptionStrike;
    public OptionType PutOrCall;
    public String OptionSymbol;
} 