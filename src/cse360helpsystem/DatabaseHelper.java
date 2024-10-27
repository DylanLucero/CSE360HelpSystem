package cse360helpsystem;
import java.sql.*;


class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/CSE360HelpSystem";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			//Re-enable this if you want to get rid of everything in database.
			//dropTable("cse360users");
			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
    // Create the cse360users table
    private void createTables() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "username VARCHAR(100) UNIQUE, "
                + "email VARCHAR(100) UNIQUE, "
                + "password VARCHAR(100), "
                + "role VARCHAR(50), "
                + "first_name VARCHAR(50), "
                + "last_name VARCHAR(50), "
                + "preferred_name VARCHAR(50),"
                + "abstract VARCHAR(500),"
                + "header VARCHAR(250),"
                + "keywords VARCHAR(250),"
                + "title VARCHAR(50),"
                + "references VARCHAR(500),"
                + "body VARCHAR(2500),"
                + "is_setup_complete BOOLEAN DEFAULT NULL);";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Table created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    
    // Drop the tables
    private void dropTable(String tableName) {
        String dropTableSQL = "DROP TABLE IF EXISTS " + tableName;

        try (Statement statement = connection.createStatement()) {
            statement.execute(dropTableSQL);
            System.out.println("Table " + tableName + " has been dropped.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}
	public void register(String username, String pass, String role) throws SQLException {
		String insertUser = "INSERT INTO cse360users (username, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, username);
			pstmt.setString(2, pass);
			pstmt.setString(3, role);
			pstmt.executeUpdate();
		}
	}
	public void adminRegister(String username, String pass, String role) throws SQLException {
		String insertUser = "INSERT INTO cse360users (username, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, username);
			pstmt.setString(2, pass);
			pstmt.setString(3, role);
			pstmt.executeUpdate();
		}
	}

	public boolean login(String username, String password, String role) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE username = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setString(3, role);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	public boolean adminLogin(String username, String password, String role) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE username = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setString(3,  role);;
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	public boolean doesUserExist(String username) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
    public void printUsers() throws SQLException {
        String query = "SELECT * FROM cse360users";
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            System.out.println("UserName: " + resultSet.getString("username"));
            System.out.println("First Name: " + resultSet.getString("first_name"));
           System.out.println("Last Name: " + resultSet.getString("last_name"));
           System.out.println("Preferred Name: " + resultSet.getString("preferred_name"));
        }
       }
    
	
	public void updateUserDetails(String username, String firstName, String lastName, String preferredName) throws SQLException {
		String query = "INSERT INTO cse360users (first_name, last_name, preferred_name) VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {

	        pstmt.setString(1, firstName);
	        pstmt.setString(2, lastName);
	        pstmt.setString(3, preferredName);
	        pstmt.executeUpdate();
	    }
	}
	
	public void articleData(String title, String header, String abstractContent, String body, String keywords, String references) throws SQLException {
		String insertBody = "INSERT INTO cse360users (author, title, abstractContent, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertBody)) {
			pstmt.setString(1, title);
			pstmt.setString(2, header);
			pstmt.setString(3, abstractContent);
			pstmt.setString(4, body);
			pstmt.setString(5, keywords);
			pstmt.setString(6, references);
			pstmt.executeUpdate();
		}
	}
	

	public boolean setupComplete(String username) throws SQLException {
		boolean updateResult = false;
		String query = "SELECT is_setup_complete FROM cse360users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs1;
	        rs1 = pstmt.executeQuery();
	        rs1.next();
	        if (rs1.next()) {
	        	updateResult = rs1.getBoolean("is_setup_complete");
	        }
        	return updateResult;
	    }
	}
	
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
}