package javaservlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Pothole")
public class PotholeServlet extends HttpServlet {
    private static final String URL = "jdbc:mysql://localhost:8086/navin"; // Updated URL with port 8084
    private static final String USER = "root";  // Your MySQL username
    private static final String PASSWORD = "navin@181818";  // Your MySQL password

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String latitude = request.getParameter("latitude");
        String longitude = request.getParameter("longitude");

        // Optional: Validate or sanitize inputs before using them
        if (title == null || description == null || latitude == null || longitude == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "All fields are required.");
            return;
        }

        // Convert latitude and longitude to double
        double lat = 0.0, lon = 0.0;
        try {
            lat = Double.parseDouble(latitude);
            lon = Double.parseDouble(longitude);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid latitude or longitude values.");
            return;
        }
        // SQL query to insert the pothole data into the database
        String insertQuery = "INSERT INTO potholes (title, description, latitude, longitude) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, lat);  // Set latitude as double
            preparedStatement.setDouble(4, lon); // Set longitude as double

            int rowsAffected = preparedStatement.executeUpdate();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h2>Pothole Report Submitted Successfully!</h2>");
            out.println("<p>" + rowsAffected + " row(s) inserted into the database.</p>");
            out.println("<a href='index.html'>Go Back</a>");
            out.println("</body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }
}

