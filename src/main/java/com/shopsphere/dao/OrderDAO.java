package com.shopsphere.dao;

import com.shopsphere.model.Order;

public class OrderDAO {

    // Save order
    public void saveOrder(Order order) {

        // Database will be connected later.
        System.out.println(
                "Order saved successfully. Order ID: " + order.getId()
        );
    }

    // Find order by ID
    public Order findById(int id) {

        // Temporary data.
        // MySQL database will be used later.

        if (id <= 0) {
            return null;
        }

        return new Order(
                id,
                1,
                999.99,
                "2026-10-08",
                "PLACED"
        );
    }

    // Update order status
    public void updateStatus(int orderId, String status) {

        // Database operation will be added later.
        System.out.println(
                "Order " + orderId + " status updated to: " + status
        );
    }
}