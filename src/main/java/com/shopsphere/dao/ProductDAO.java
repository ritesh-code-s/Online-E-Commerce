package com.shopsphere.dao;

import com.shopsphere.model.Product;

public class ProductDAO {

    // Save product
    public void saveProduct(Product product) {

        // Database will be connected later.
        System.out.println("Product saved: " + product.getName());
    }

    // Find product by ID
    public Product findById(int id) {

        // Temporary data.
        // MySQL database will be used later.

        if (id <= 0) {
            return null;
        }

        return new Product(
                id,
                "Demo Product",
                "This is a demo product.",
                999.99,
                10,
                "General",
                "default.jpg"
        );
    }
}