package com.shopsphere.servlet;

import com.shopsphere.exception.InsufficientStockException;
import com.shopsphere.model.Order;
import com.shopsphere.model.Product;
import com.shopsphere.payment.CardPayment;
import com.shopsphere.payment.Payment;
import com.shopsphere.payment.UpiPayment;
import com.shopsphere.service.OrderService;
import com.shopsphere.service.ProductService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private ProductService productService;
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        productService = new ProductService();
        orderService = new OrderService();
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String userIdParameter = request.getParameter("userId");
        String productIdParameter = request.getParameter("productId");
        String quantityParameter = request.getParameter("quantity");
        String paymentMethod = request.getParameter("paymentMethod");

        response.setContentType("text/html");

        try {

            int userId = Integer.parseInt(userIdParameter);
            int productId = Integer.parseInt(productIdParameter);
            int quantity = Integer.parseInt(quantityParameter);

            Product product = productService.getProductById(productId);

            if (!productService.checkStock(product, quantity)) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            Order order = orderService.createOrder(
                    userId,
                    product,
                    quantity
            );

            Payment payment;

            if ("UPI".equalsIgnoreCase(paymentMethod)) {
                payment = new UpiPayment();
            } else {
                payment = new CardPayment();
            }

            payment.pay(order.getTotalAmount());

            response.getWriter().println(
                    "<h2>Order placed successfully!</h2>"
            );

            response.getWriter().println(
                    "<p>Order ID: " + order.getId() + "</p>"
            );

            response.getWriter().println(
                    "<p>Total Amount: ₹"
                            + order.getTotalAmount()
                            + "</p>"
            );

            response.getWriter().println(
                    "<p>Payment Method: "
                            + paymentMethod
                            + "</p>"
            );

            response.getWriter().println(
                    "<p>Status: " + order.getStatus() + "</p>"
            );

        } catch (NumberFormatException e) {

            response.getWriter().println(
                    "<h2>Invalid checkout information.</h2>"
            );

            response.getWriter().println(
                    "<p>User ID, Product ID and Quantity "
                            + "must be numbers.</p>"
            );

        } catch (InsufficientStockException e) {

            response.getWriter().println(
                    "<h2>Checkout failed.</h2>"
            );

            response.getWriter().println(
                    "<p>" + e.getMessage() + "</p>"
            );

        } catch (Exception e) {

            response.getWriter().println(
                    "<h2>Checkout failed.</h2>"
            );

            response.getWriter().println(
                    "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}