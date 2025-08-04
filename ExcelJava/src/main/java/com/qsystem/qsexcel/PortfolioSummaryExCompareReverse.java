package com.qsystem.qsexcel;

import java.util.Comparator;

/**
 * 投资组合摘要比较器（反向排序）
 */
public class PortfolioSummaryExCompareReverse implements Comparator<PortfolioSummaryEx> {
    @Override
    public int compare(PortfolioSummaryEx summary1, PortfolioSummaryEx summary2) {
        if (summary1 == null || summary2 == null) {
            return 0;
        }
        if (summary1.SharpRatio == summary2.SharpRatio) {
            return 0;
        } else if (summary1.SharpRatio > summary2.SharpRatio) {
            return -1;
        } else if (summary1.SharpRatio < summary2.SharpRatio) {
            return 1;
        }
        return 0;
    }
} 