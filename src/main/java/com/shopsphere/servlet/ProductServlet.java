package com.shopsphere.servlet;

import com.shopsphere.exception.ProductNotFoundException;
import com.shopsphere.model.Product;
import com.shopsphere.service.ProductService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/product")
public class ProductServlet extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() throws ServletException {
        productService = new ProductService();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String idParameter = request.getParameter("id");

        response.setContentType("text/html");

        if (idParameter == null || idParameter.isEmpty()) {
            response.getWriter().println(
                    "<h2>Product ID is required.</h2>"
            );
            return;
        }

        try {

            int id = Integer.parseInt(idParameter);

            Product product = productService.getProductById(id);

            response.getWriter().println(
                    "<h2>Product Details</h2>"
            );

            response.getWriter().println(
                    "<p>Product ID: " + product.getId() + "</p>"
            );

            response.getWriter().println(
                    "<p>Name: " + product.getName() + "</p>"
            );

            response.getWriter().println(
                    "<p>Description: " + product.getDescription() + "</p>"
            );

            response.getWriter().println(
                    "<p>Price: ₹" + product.getPrice() + "</p>"
            );

            response.getWriter().println(
                    "<p>Stock: " + product.getStock() + "</p>"
            );

            response.getWriter().println(
                    "<p>Category: " + product.getCategory() + "</p>"
            );

        } catch (ProductNotFoundException e) {

            response.getWriter().println(
                    "<h2>Product Not Found</h2>"
            );

            response.getWriter().println(
                    "<p>" + e.getMessage() + "</p>"
            );

        } catch (NumberFormatException e) {

            response.getWriter().println(
                    "<h2>Invalid Product ID</h2>"
            );

            response.getWriter().println(
                    "<p>Please enter a valid numeric product ID.</p>"
            );
        }
    }
}