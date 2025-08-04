package com.qsystem.clientlib;

public class PositionEx {
    public Position position;
    public PositionExtention positionExtention;

    @Override
    public String toString() {
        return "Symbol : " + (position != null ? position.Symbol : "null") + " Size: " + (position != null ? position.Quantity : "null");
    }
} 