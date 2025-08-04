package com.qsystem.clientlib;

import java.util.Comparator;

/**
 * CompleteTradeList比较器类
 */
public class CompleteTradeListCompare implements Comparator<CompleteOrder> {
    @Override
    public int compare(CompleteOrder completeOrder1, CompleteOrder completeOrder2) {
        if (completeOrder1 == null || completeOrder2 == null)
            return 0;
        if (completeOrder1.CompleteTradeList.size() < 1 || completeOrder2.CompleteTradeList.size() < 1) {
            return 0;
        }
        int rc = completeOrder1.CompleteTradeList.get(0).getTimeStamp()
                .compareTo(completeOrder2.CompleteTradeList.get(0).getTimeStamp());
        if (rc == 0) {
            rc = completeOrder1.CompleteTradeList.get(0).getSymbol()
                    .compareTo(completeOrder2.CompleteTradeList.get(0).getSymbol());
        }
        return rc;
    }
} 