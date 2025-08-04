package com.qsystem.clientlib;

import java.util.Comparator;
import com.qsystem.clientlib.JsonModel.OrderCategory;

public class OrderQueueCompare implements Comparator<Order> {
    @Override
    public int compare(Order order1, Order order2) {
        if (order1 == null) {
            return 1;
        }
        if (order2 == null) {
            return -1;
        }
        if (order1.OrderCategory == OrderCategory.Executable_Client) {
            return -1;
        } else if (order2.OrderCategory == OrderCategory.Executable_Client) {
            return 1;
        }
        int rc = order1.ExpectedExecutionTime.compareTo(order2.ExpectedExecutionTime);
        if (rc == 0) {
            rc = order1.OrderTime.compareTo(order2.OrderTime);
        }
        return rc;
    }
} 