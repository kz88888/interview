package com.qsystem.clientlib;

import java.util.Comparator;

public class OrderCompare implements Comparator<Order> {
    @Override
    public int compare(Order order1, Order order2) {
        return order1.ExpectedExecutionTime.compareTo(order2.ExpectedExecutionTime);
    }
} 