package cse360helpsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cse360helpsystem";  // Update to your DB URL
    private static final String DB_USER = "sa";  // Update to your DB username
    private static final String DB_PASSWORD = "";  // Update to your DB password

    // Establish a database connection
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Insert new user into the database
    public boolean insertUser(String username, String password, String role) throws SQLException {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        }
    }

    // Check if user exists and password matches
    public String authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT role FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet resultSet = stmt.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getString("role");  // Return role if found
            } else {
                return null;  // No matching user found
            }
        }
    }
}
