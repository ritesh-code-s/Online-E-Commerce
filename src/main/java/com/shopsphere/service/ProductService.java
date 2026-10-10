package com.shopsphere.service;

import com.shopsphere.exception.ProductNotFoundException;
import com.shopsphere.model.Product;

public class ProductService {

    // Get product by ID
    public Product getProductById(int id)
            throws ProductNotFoundException {

        // Temporary product data.
        // Database will be connected later.

        if (id <= 0) {
            throw new ProductNotFoundException("Product not found.");
        }

        Product product = new Product(
                id,
                "Demo Product",
                "This is a demo product.",
                999.99,
                10,
                "General",
                "default.jpg"
        );

        return product;
    }

    // Check product stock
    public boolean checkStock(Product product, int quantity) {

        return product.getStock() >= quantity;
    }
}