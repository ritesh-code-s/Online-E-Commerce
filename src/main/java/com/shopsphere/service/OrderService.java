package com.shopsphere.service;

import com.shopsphere.exception.InsufficientStockException;
import com.shopsphere.model.Order;
import com.shopsphere.model.Product;

public class OrderService {

    // Create a new order
    public Order createOrder(int userId, Product product, int quantity)
            throws InsufficientStockException {

        // Check stock before creating the order
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName()
            );
        }

        // Calculate total amount
        double totalAmount = product.getPrice() * quantity;

        // Temporary order data.
        // Database will be connected later.
        Order order = new Order(
                1,
                userId,
                totalAmount,
                "2026-10-08",
                "PLACED"
        );

        return order;
    }

    // Calculate order total
    public double calculateTotal(Product product, int quantity) {

        return product.getPrice() * quantity;
    }
}