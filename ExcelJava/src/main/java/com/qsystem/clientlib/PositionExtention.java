package com.qsystem.clientlib;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionExtention {
    public double PositionValue;
    public double PositionSize;
    @JsonProperty("adj_close")
    public double AdjClose;
    public double Close;
    public double Ask;
    public double Bid;
    public double TheoValue;
    public boolean validate = true;
    public UUID SignalID = UUID.randomUUID();
    @JsonProperty("underlying_price")
    public double Underlying;
    @JsonProperty("beta_delta_dollar")
    public double BetaDeltaDollar;
    @JsonProperty("maintenance_margin")
    public double MaintenanceMargin;
    @JsonProperty("thetaDollar")
    public double ThetaDollar;
    @JsonProperty("vegaDollar")
    public double VegaDollar;
} 