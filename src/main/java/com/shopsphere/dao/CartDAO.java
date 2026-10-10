package com.shopsphere.dao;

import com.shopsphere.model.Cart;

public class CartDAO {

    // Add product to cart
    public void addToCart(Cart cart) {

        // Database will be connected later.
        System.out.println(
                "Product added to cart: " + cart.getProductId()
        );
    }

    // Get cart for a user
    public Cart getCartByUserId(int userId) {

        // Temporary data.
        // MySQL database will be used later.

        if (userId <= 0) {
            return null;
        }

        return new Cart(
                1,
                userId,
                1,
                1
        );
    }

    // Remove product from cart
    public void removeFromCart(int cartId) {

        // Database operation will be added later.
        System.out.println("Product removed from cart: " + cartId);
    }
}