package com.qsystem.clientlib.JsonModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BInstrument {
    public String symbol;
    public String underlyingSymbol;
} 