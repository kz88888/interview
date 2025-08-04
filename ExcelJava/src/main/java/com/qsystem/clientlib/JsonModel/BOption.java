package com.qsystem.clientlib.JsonModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BOption extends BInstrument {
    @JsonProperty("_OptionDataDescriptor")
    public OptionDataDescriptor _OptionDataDescriptor;
    @JsonProperty("expiration")
    public int expiration;
    @JsonProperty("_call")
    public Boolean _call;
    @JsonProperty("strike")
    public Double strike;
    @JsonProperty("multiplier")
    public double multiplier;
    public String oldSymbol;

    public OptionDataDescriptor get_OptionDataDescriptor() { return _OptionDataDescriptor; }
    public void set_OptionDataDescriptor(OptionDataDescriptor _OptionDataDescriptor) { this._OptionDataDescriptor = _OptionDataDescriptor; }
    public int getExpiration() { return expiration; }
    public void setExpiration(int expiration) { this.expiration = expiration; }
    public Boolean get_call() { return _call; }
    public void set_call(Boolean _call) { this._call = _call; }
    public Double getStrike() { return strike; }
    public void setStrike(Double strike) { this.strike = strike; }
    public double getMultiplier() { return multiplier; }
    public void setMultiplier(Double multiplier) { this.multiplier = multiplier; }

    @Override
    public String toString() {
        return symbol + "_" + _OptionDataDescriptor.getOptionShortStr();
    }
} 