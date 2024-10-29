package cse360helpsystem;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
			createTableUsers();  // Create the necessary tables if they don't exist
			createTableArticles();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	/*
	 * Need to call this method in main!! to connect to second database 
	 */
	public void connectToSecondaryDatabase() throws Exception{
		SecondDatabase dbHelper = new SecondDatabase();
		 try {
		        dbHelper.connectToDatabase(); 
		        System.out.println("Connecting to database 1");
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	}
    // Create the cse360users table
    private void createTableUsers() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "username VARCHAR(100) UNIQUE, "
                + "email VARCHAR(100) UNIQUE, "
                + "password VARCHAR(100), "
                + "role VARCHAR(50), "
                + "first_name VARCHAR(50), "
                + "last_name VARCHAR(50), "
                + "preferred_name VARCHAR(50),"
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
	
/*
 * Article table 
 */

    private void createTableArticles() throws SQLException {
    	String createTableSQL = "CREATE TABLE IF NOT EXISTS articleList ("
    			+ "id INT AUTO_INCREMENT PRIMARY KEY, "
    			+ "articleGroup VARCHAR(255), "
				+ "title VARCHAR(255), " //article title
				+ "authors VARCHAR(255), " //authors of the article 
				+ "abstract VARCHAR(255), " //abstract of the article 
				+ "keywords VARCHAR(255), " //keywords related to the article
				+ "body VARCHAR(255), " //body of the article
				+ "references VARCHAR(255)" //references related to the article 
				+ ");";
		statement.execute(createTableSQL); //execute table creation
    }
	/*
	 * 
	 * 
	 * Secondary Database interaction
	 */
	
	public void backup(String filename) throws Exception {
		//get to string
		
		FileRecord fileRecord = createNewFileRecord(filename);
		SecondDatabase.storeFileAsBlob(fileRecord);
		System.out.println("System Sucessfully Backed Up");
	}
/**
 * Restore encrypted file to table 
 * @param filename
 * @throws Exception
 */
	public void restore(String filename) throws Exception {
	    FileRecord fileRecord = SecondDatabase.retrieveFileAsBlob(filename);
	    // Check if filedata is null 
	    if (fileRecord == null || fileRecord.getFileData() == null) {
	        System.out.println("Cannot restore: file data is null.");
	        return;
	    }
	    insertDataFromFileRecord(fileRecord);
	    System.out.println("Successful Merge");
	    
	}
	
	/*
	 * Creates a new instance of FileRecord 
	 */
	public FileRecord createNewFileRecord(String filename) throws SQLException{
		String query = "SELECT * FROM articleList";
        ResultSet resultSet = statement.executeQuery(query);
        
		StringBuilder sb = new StringBuilder();
        
        // Convert ResultSet to a String
        while (resultSet.next()) {
            sb.append("Group: ").append(resultSet.getString("group")).append(", ")
              .append("Title: ").append(resultSet.getString("title")).append(", ")
              .append("Authors: ").append(resultSet.getString("authors")).append(", ")
              .append("Abstract: ").append(resultSet.getString("abstract")).append(", ")
              .append("Keywords: ").append(resultSet.getString("keywords")).append(", ")
              .append("Body: ").append(resultSet.getString("body")).append(", ")
              .append("References: ").append(resultSet.getString("references")).append("\n");
        }
        
        // Compress the string data
        String filedata = sb.toString();
		FileRecord fileRecord = new FileRecord(filename, filedata);
		return fileRecord;
		
	}
	
	public void insertDataFromFileRecord(FileRecord myFile) throws IOException, SQLException {
	    // Convert byte array to String using UTF-8 encoding
	    // Clear the table
	    String truncateSQL = "TRUNCATE TABLE articleList"; //truncating table data
	    try (Statement statement = connection.createStatement()) {
	        statement.executeUpdate(truncateSQL);
	    }
		
		String dataString = new String(myFile.getFileData());
	    
	    // Split the data into lines
	    String[] lines = dataString.split("\n");

        // Prepare SQL statement
        String insertSQL = "INSERT INTO articleList (articleGroup, title, authors, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            for (String line : lines) {
                // Split each line into fields (assuming CSV format)
                String[] fields = line.split(", ");
                
                // Ensure there are enough fields
                if (fields.length == 7) {
                    preparedStatement.setString(1, fields[0].split(": ")[1]); // Group
                    preparedStatement.setString(2, fields[1].split(": ")[1]); // Title
                    preparedStatement.setString(3, fields[2].split(": ")[1]); // Authors
                    preparedStatement.setString(4, fields[3].split(": ")[1]); // Abstract
                    preparedStatement.setString(5, fields[4].split(": ")[1]); // Keywords
                    preparedStatement.setString(6, fields[5].split(": ")[1]); // Body
                    preparedStatement.setString(7, fields[6].split(": ")[1]); // References
                    
                    // Execute the insert
                    preparedStatement.executeUpdate();
                }
            }
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