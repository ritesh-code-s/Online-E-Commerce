package com.shopsphere.servlet;

import com.shopsphere.dao.OrderDAO;
import com.shopsphere.model.Order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String orderIdParameter = request.getParameter("id");

        response.setContentType("text/html");

        if (orderIdParameter == null || orderIdParameter.isEmpty()) {

            response.getWriter().println(
                    "<h2>Order ID is required.</h2>"
            );

            return;
        }

        try {

            int orderId = Integer.parseInt(orderIdParameter);

            Order order = orderDAO.findById(orderId);

            if (order == null) {

                response.getWriter().println(
                        "<h2>Order not found.</h2>"
                );

                return;
            }

            response.getWriter().println(
                    "<h2>Order Details</h2>"
            );

            response.getWriter().println(
                    "<p>Order ID: " + order.getId() + "</p>"
            );

            response.getWriter().println(
                    "<p>User ID: " + order.getUserId() + "</p>"
            );

            response.getWriter().println(
                    "<p>Total Amount: ₹"
                            + order.getTotalAmount()
                            + "</p>"
            );

            response.getWriter().println(
                    "<p>Order Date: "
                            + order.getOrderDate()
                            + "</p>"
            );

            response.getWriter().println(
                    "<p>Status: "
                            + order.getStatus()
                            + "</p>"
            );

        } catch (NumberFormatException e) {

            response.getWriter().println(
                    "<h2>Invalid Order ID.</h2>"
            );

            response.getWriter().println(
                    "<p>Order ID must be a number.</p>"
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String orderIdParameter = request.getParameter("orderId");
        String status = request.getParameter("status");

        response.setContentType("text/html");

        try {

            int orderId = Integer.parseInt(orderIdParameter);

            orderDAO.updateStatus(orderId, status);

            response.getWriter().println(
                    "<h2>Order status updated successfully!</h2>"
            );

            response.getWriter().println(
                    "<p>Order ID: " + orderId + "</p>"
            );

            response.getWriter().println(
                    "<p>New Status: " + status + "</p>"
            );

        } catch (NumberFormatException e) {

            response.getWriter().println(
                    "<h2>Invalid Order ID.</h2>"
            );
        }
    }
}