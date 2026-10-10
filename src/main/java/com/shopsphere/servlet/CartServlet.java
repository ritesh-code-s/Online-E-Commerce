package com.shopsphere.servlet;

import com.shopsphere.dao.CartDAO;
import com.shopsphere.model.Cart;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private CartDAO cartDAO;

    @Override
    public void init() throws ServletException {
        cartDAO = new CartDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String userIdParameter = request.getParameter("userId");
        String productIdParameter = request.getParameter("productId");
        String quantityParameter = request.getParameter("quantity");

        response.setContentType("text/html");

        try {

            int userId = Integer.parseInt(userIdParameter);
            int productId = Integer.parseInt(productIdParameter);
            int quantity = Integer.parseInt(quantityParameter);

            Cart cart = new Cart(
                    0,
                    userId,
                    productId,
                    quantity
            );

            cartDAO.addToCart(cart);

            response.getWriter().println(
                    "<h2>Product added to cart!</h2>"
            );

            response.getWriter().println(
                    "<p>Product ID: " + productId + "</p>"
            );

            response.getWriter().println(
                    "<p>Quantity: " + quantity + "</p>"
            );

        } catch (NumberFormatException e) {

            response.getWriter().println(
                    "<h2>Invalid cart information.</h2>"
            );

            response.getWriter().println(
                    "<p>User ID, Product ID and Quantity must be numbers.</p>"
            );
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String userIdParameter = request.getParameter("userId");

        response.setContentType("text/html");

        try {

            int userId = Integer.parseInt(userIdParameter);

            Cart cart = cartDAO.getCartByUserId(userId);

            if (cart == null) {

                response.getWriter().println(
                        "<h2>Cart is empty.</h2>"
                );

                return;
            }

            response.getWriter().println(
                    "<h2>Your Cart</h2>"
            );

            response.getWriter().println(
                    "<p>Cart ID: " + cart.getId() + "</p>"
            );

            response.getWriter().println(
                    "<p>Product ID: " + cart.getProductId() + "</p>"
            );

            response.getWriter().println(
                    "<p>Quantity: " + cart.getQuantity() + "</p>"
            );

        } catch (NumberFormatException e) {

            response.getWriter().println(
                    "<h2>Invalid User ID.</h2>"
            );
        }
    }
}