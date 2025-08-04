package com.qsystem.clientlib.JsonModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OptionDataDescriptor extends TagDescriptor {
    public static String convention = "EXP.STRIKE.P/C.[A]"; //A is added only if not unique anymore.
    @JsonProperty("ExpirationDate")
    public String ExpirationDate;
    @JsonProperty("IDCode")
    public String IDCode; //csv
    @JsonProperty("Strike")
    public String Strike;
    @JsonProperty("OldStrike")
    public String OldStrike;
    @JsonProperty("PutOrCall")
    public String PutOrCall;
    @JsonProperty("IsOld")
    public boolean IsOld;
    @JsonProperty("IsAdjusted")
    public boolean IsAdjusted; //rename
    @JsonProperty("multiplier")
    public Double multiplier;

    public int getExpirationInt() {
        int expriration = Integer.parseInt(ExpirationDate);
        return expriration;
    }

    public boolean is_call() {
        return PutOrCall.toUpperCase().equals("C");
    }

    public String getOptionShortStr() {
        String shortStr = ExpirationDate + "." + Strike;
        if (IsAdjusted == true)
            shortStr += "A";
        shortStr += "." + PutOrCall;
        return shortStr;
    }

    // Getters and Setters
    public String getExpirationDate() { return ExpirationDate; }
    public void setExpirationDate(String expirationDate) { ExpirationDate = expirationDate; }
    public String getIDCode() { return IDCode; }
    public void setIDCode(String idCode) { IDCode = idCode; }
    public String getStrike() { return Strike; }
    public void setStrike(String strike) { Strike = strike; }
    public String getOldStrike() { return OldStrike; }
    public void setOldStrike(String oldStrike) { OldStrike = oldStrike; }
    public String getPutOrCall() { return PutOrCall; }
    public void setPutOrCall(String putOrCall) { PutOrCall = putOrCall; }
    public boolean isIsOld() { return IsOld; }
    public void setIsOld(boolean isOld) { IsOld = isOld; }
    public boolean isIsAdjusted() { return IsAdjusted; }
    public void setIsAdjusted(boolean isAdjusted) { IsAdjusted = isAdjusted; }
    public Double getMultiplier() { return multiplier; }
    public void setMultiplier(Double multiplier) { this.multiplier = multiplier; }
} 