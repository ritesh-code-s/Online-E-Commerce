package com.shopsphere.servlet;

import com.shopsphere.dao.OrderDAO;
import com.shopsphere.dao.ProductDAO;
import com.shopsphere.model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private ProductDAO productDAO;
    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        response.getWriter().println(
                "<h1>Admin Dashboard</h1>"
        );

        response.getWriter().println(
                "<h2>ShopSphere Administration</h2>"
        );

        response.getWriter().println(
                "<p>Admin panel is working successfully.</p>"
        );

        response.getWriter().println(
                "<h3>Available Admin Operations</h3>"
        );

        response.getWriter().println(
                "<ul>"
                + "<li>Manage Products</li>"
                + "<li>Add Products</li>"
                + "<li>View Orders</li>"
                + "<li>Update Order Status</li>"
                + "</ul>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        response.setContentType("text/html");

        if ("addProduct".equalsIgnoreCase(action)) {

            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String priceParameter = request.getParameter("price");
            String stockParameter = request.getParameter("stock");
            String category = request.getParameter("category");
            String image = request.getParameter("image");

            try {

                double price = Double.parseDouble(priceParameter);
                int stock = Integer.parseInt(stockParameter);

                Product product = new Product(
                        0,
                        name,
                        description,
                        price,
                        stock,
                        category,
                        image
                );

                productDAO.saveProduct(product);

                response.getWriter().println(
                        "<h2>Product added successfully!</h2>"
                );

                response.getWriter().println(
                        "<p>Product Name: " + name + "</p>"
                );

                response.getWriter().println(
                        "<p>Price: ₹" + price + "</p>"
                );

                response.getWriter().println(
                        "<p>Stock: " + stock + "</p>"
                );

            } catch (NumberFormatException e) {

                response.getWriter().println(
                        "<h2>Invalid product information.</h2>"
                );

                response.getWriter().println(
                        "<p>Price must be a number and stock must be an integer.</p>"
                );
            }

        } else if ("updateOrder".equalsIgnoreCase(action)) {

            String orderIdParameter =
                    request.getParameter("orderId");

            String status =
                    request.getParameter("status");

            try {

                int orderId =
                        Integer.parseInt(orderIdParameter);

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

                response.getWriter().println(
                        "<p>Order ID must be a number.</p>"
                );
            }

        } else {

            response.getWriter().println(
                    "<h2>Invalid admin action.</h2>"
            );
        }
    }
}