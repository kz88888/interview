package com.qsystem.clientlib;

import java.util.Comparator;

public class PositionCompare implements Comparator<PositionEx> {
    @Override
    public int compare(PositionEx pos1, PositionEx pos2) {
        if (pos1.position.Symbol.equals("$CASH")) {
            return 1;
        }
        if (pos2.position.Symbol.equals("$CASH")) {
            return -1;
        }
        
        if (pos1 == null || pos2 == null) {
            return 0;
        } else if (pos1.position.Quantity == pos2.position.Quantity) {
            return 0;
        } else if (pos1.position.Quantity > pos2.position.Quantity) {
            return -1;
        } else {
            return 1;
        }
    }
} 