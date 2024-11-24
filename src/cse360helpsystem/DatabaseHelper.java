package cse360helpsystem;

import java.io.IOException;
import java.sql.*;
import java.util.Base64;
import java.util.Random;

import org.bouncycastle.util.Arrays;
import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;



class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/CSE360HelpSystem";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private static Connection connection = null;
	private Statement statement = null; 
	private String lastVerifiedRole;
	
	
private EncryptionHelper encryptionHelper;
	
	public DatabaseHelper() throws Exception {
		encryptionHelper = new EncryptionHelper();
	}
	
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			dropTable("cse360users"); 	// Enabled to remove all data from database. Comment out if you want to use the database
			dropTable("articleList");
			dropTable("helparticletable");
			createTableUsers();
			createTableArticles();// Create the necessary tables if they don't exist
			createHelpArticleTable();
			createOTPTable();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	/*
	 * Need to call this method in main!! to connect to second database 
	 */
	public void connectToSecondaryDatabase() throws Exception{
		SecondDatabase dbHelper = new SecondDatabase();
		SpecialAccess specialAccess = new SpecialAccess();
		 try {
		        dbHelper.connectToDatabase(); 
	            specialAccess.connectToSpecialAccessDatabase();
		        System.out.println("Connecting to databases 2 and 3");
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	}
	
	public void connectToHelpArticleDatabase() throws Exception {
		HelpArticleDatabase dbAHelper = new HelpArticleDatabase();
		dbAHelper.connectToDatabase();
        System.out.println("Connecting to Help Article Database");
	}
	
	private static void listTableContents(String tableName) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Get metadata for the result set to fetch column names
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();

            // Print column names
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rsMetaData.getColumnName(i) + "\t");
            }
            System.out.println();

            // Print row data
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving contents of table " + tableName + ": " + e.getMessage());
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
    
    private void createHelpArticleTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS helparticletable ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
        		+ "article_title VARCHAR(50), "
                + "article_type VARCHAR(50), " 
                + "article_level VARCHAR(50), " // Fixed typo 'articlel_level' to 'article_level'
                + "article_body VARCHAR(255) UNIQUE" // Removed the semicolon here
                + ");"; // Closing the CREATE TABLE statement properly
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Table created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }   
    
    private void createOTPTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS otpRecords ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "  // Correct syntax for auto-increment in H2
                + "otp VARCHAR(10), "
                + "role VARCHAR(50)"
                + ");";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("OTPTable created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveOTPToDatabase(String otp, String role) {
        String insertSQL = "INSERT INTO otpRecords (otp, role) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, otp);  // Set OTP value
            pstmt.setString(2, role);  // Set role value
            pstmt.executeUpdate();  // Execute the insert statement to store OTP in database
            System.out.println("OTP for role " + role + " saved to database successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save OTP to the database", e);
        }
    }
    public String getLastVerifiedRole() {
	    return lastVerifiedRole;
	}
	public boolean verifyOTP(String enteredOTP) throws SQLException {
	    String query = "SELECT otp, role FROM otpRecords ORDER BY created_at DESC LIMIT 1";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                String storedOTP = rs.getString("otp");
	                if (enteredOTP.equals(storedOTP)) {
	                    lastVerifiedRole = rs.getString("role"); // Save the role for later use
	                    return true; // OTP is valid
	                }
	            }
	        }
	    }
	    lastVerifiedRole = null; // Clear the role if OTP is invalid
	    return false; // OTP is invalid
	}
	public static Random rand = new Random();
	public static String stringOTP;

	// Generating an OTP
	public String generateOTP(String role) {
		int userOTP = rand.nextInt(100,9999);
		if(userOTP<1000) {
			stringOTP = "0" + userOTP;
		} else {
            stringOTP = Integer.toString(userOTP);
        }

        // Save the OTP to the database
        saveOTPToDatabase(stringOTP, role);

        return stringOTP;
    }
    public String getHelpTableBody(int id) {
        String getContents = "SELECT article_body FROM helparticletable WHERE id = ?";
        String result = null; // Initialize the result variable to return

        try (PreparedStatement stmt = connection.prepareStatement(getContents)) {
            // Set the id parameter
            stmt.setInt(1, id);
            
            // Execute the query and get the result set
            ResultSet rs = stmt.executeQuery();
            
            // Process the result set
            if (rs.next()) {
                // Get the 'body' column from the result set
                result = rs.getString("article_body");
            } else {
                result = "No content found for the given ID.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Error fetching content.";
        }

        return result;
    }
    
    public String getHelpTableTitle(int id) {
        String getContents = "SELECT article_title FROM helparticletable WHERE id = ?";
        String result = null; // Initialize the result variable to return

        try (PreparedStatement stmt = connection.prepareStatement(getContents)) {
            // Set the id parameter
            stmt.setInt(1, id);
            
            // Execute the query and get the result set
            ResultSet rs = stmt.executeQuery();
            
            // Process the result set
            if (rs.next()) {
                // Get the 'body' column from the result set
                result = rs.getString("article_title");
            } else {
                result = "No content found for the given ID.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Error fetching content.";
        }

        return result;
    }
    
    public String getHelpTableLevel(int id) {
        String getContents = "SELECT article_level FROM helparticletable WHERE id = ?";
        String result = null; // Initialize the result variable to return

        try (PreparedStatement stmt = connection.prepareStatement(getContents)) {
            // Set the id parameter
            stmt.setInt(1, id);
            
            // Execute the query and get the result set
            ResultSet rs = stmt.executeQuery();
            
            // Process the result set
            if (rs.next()) {
                // Get the 'body' column from the result set
                result = rs.getString("article_level");
            } else {
                result = "No content found for the given ID.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Error fetching content.";
        }

        return result;
    }
    public String getHelpTableType(int id) {
        String getContents = "SELECT article_type FROM helparticletable WHERE id = ?";
        String result = null; // Initialize the result variable to return

        try (PreparedStatement stmt = connection.prepareStatement(getContents)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                result = rs.getString("article_type");
            } else {
                result = "No content found for the given ID.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Error fetching content.";
        }

        return result;
    }

    public String getTableBody(int id) {
        String getContents = "SELECT body FROM articleList WHERE id = ?";
        String result = null; // Initialize the result variable to return

        try (PreparedStatement stmt = connection.prepareStatement(getContents)) {
            // Set the id parameter
            stmt.setInt(1, id);
            
            // Execute the query and get the result set
            ResultSet rs = stmt.executeQuery();
            
            // Process the result set
            if (rs.next()) {
                // Get the 'body' column from the result set
                result = rs.getString("body");
            } else {
                result = "No content found for the given ID.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Error fetching content.";
        }

        return result;
    }
    public String getTableAuthor(int id) {
        String getContents = "SELECT authors FROM articleList WHERE id = ?";
        String result = null; // Initialize the result variable to return

        try (PreparedStatement stmt = connection.prepareStatement(getContents)) {
            // Set the id parameter
            stmt.setInt(1, id);
            
            // Execute the query and get the result set
            ResultSet rs = stmt.executeQuery();
            
            // Process the result set
            if (rs.next()) {
                // Get the 'body' column from the result set
                result = rs.getString("authors");
            } else {
                result = "No content found for the given ID.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Error fetching content.";
        }

        return result;
    }
    public String getTableTitle(int id) {
        String getContents = "SELECT title FROM articleList WHERE id = ?";
        String result = null; // Initialize the result variable to return

        try (PreparedStatement stmt = connection.prepareStatement(getContents)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                result = rs.getString("title");
            } else {
                result = "No content found for the given ID.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Error fetching content.";
        }

        return result;
    }
    public String getTableType(int id) {
        String getContents = "SELECT * FROM articleList WHERE id = ?";
        String result = null; // Initialize the result variable to return

        try (PreparedStatement stmt = connection.prepareStatement(getContents)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                result = rs.getString("Title");
            } else {
                result = "No content found for the given ID.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result = "Error fetching content.";
        }

        return result;
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
	            // Store the result of rs.next() to avoid moving the cursor multiple times
	            boolean loginSuccessful = rs.next();
	            if (loginSuccessful) {
	                loadUserAccess(username);  // Load user access info if login is successful
	            }
	            return loginSuccessful;  // Return whether the login was successful
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
            System.out.println("Role: " + resultSet.getString("role"));
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
		boolean updateResult = true;
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
	 * All things to do with access checking! WOW this is so fun
	 */
	
	public static void loadUserAccess(String user) {
		
	}
	

/*
 * Article table 
 */

    private void createTableArticles() throws SQLException {
    	String createTableSQL = "CREATE TABLE IF NOT EXISTS articleList ("
    			+ "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
    			+ "articleGroup VARCHAR(255), " 
				+ "title VARCHAR(255)," //title of the article
				+ "header VARCHAR(255), " //header of the article
				+ "authors VARCHAR(255), " //authors of the article 
				+ "abstract VARCHAR(255), " //abstract of the article 
				+ "keywords VARCHAR(255), " //keywords related to the article
				+ "body VARCHAR(255), " //body of the article
				+ "references VARCHAR(255)" //references related to the article 
				+ ");";
		statement.execute(createTableSQL); //execute table creation
    }
    
	public void createArticle(String groupString, String titleString, String headerString, String authorsString, String abstractTextString, String keywordsString, String bodyString, String referencesString) throws Exception {
		
		//prepare sql statement for inserting a new article 
		String insertArticle = "INSERT INTO articleList (articleGroup, title, authors, header, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
			pstmt.setString(1, groupString);
			pstmt.setString(2, titleString); //set title
			pstmt.setString(3, headerString);
			pstmt.setString(4, authorsString); //set authors
			pstmt.setString(5, abstractTextString); //set abstract
			pstmt.setString(6, keywordsString); //set keywords
			pstmt.setString(7, bodyString); //set encrypted body
			pstmt.setString(8, referencesString); //set references 
			pstmt.executeUpdate(); //execute the insert statement 
		}
	}
	
	public void createHelpArticle(String title, String type, String level, String body) throws SQLException {
		String insertArticle = "INSERT INTO helparticletable (article_title, article_type, article_level, article_body) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
			pstmt.setString(1, title);
			pstmt.setString(2, type);
			pstmt.setString(3, level); //set title
			pstmt.setString(4, body);
			pstmt.executeUpdate(); //execute the insert statement 

		}
	}
	
	public void accessHelpArticle(String id) throws Exception{
	    // SQL query to retrieve an article by its ID

		String sql = "SELECT * FROM helparticletable WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)){
			pstmt.setLong(1, Integer.valueOf(id));
		    // Prepare the statement to execute the query

			try (ResultSet rs = pstmt.executeQuery()){
				if (rs.next()) {
	                // Retrieve article fields from the result set

					String type = rs.getString("article_type");
					String level = rs.getString("article_level");
	                String body = rs.getString("article_body");


	                System.out.println("+---------------------+");
	                System.out.println("|      Article        |");
	                System.out.println("+---------------------+");
	                System.out.printf("| Level: %-20s 	|\n", type);
	                System.out.printf("| Type: %-20s 		|\n", level);
	                System.out.println("| Body: ");
	                System.out.println("| " + body); // Indent each line of the body
	                // Print footer
	                System.out.println("+---------------------+");
	                }
				}
			}
		}
	
	public void deleteHelpArticle(String id) throws Exception {
	    String sql = "UPDATE helparticletable SET article_type = NULL, article_level = NULL, article_body = NULL WHERE id = ?"; // SQL update statement
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setLong(1, Integer.valueOf(id)); //set ID for deletion 
			int rowsAffected = pstmt.executeUpdate(); //execute the delete statement 
			//check if any rows were affected (i.e. if the article was found and deleted)
			if (rowsAffected > 0) {
                System.out.println("Article fields set to NULL successfully.");
            } else {
                System.out.println("No article found with the given ID.");
            }
		} catch (SQLException e) { //debugging check 
			e.printStackTrace();
			throw e;
		}
	}
	
	public void accessArticle(long ID) throws Exception {
	    // SQL query to retrieve an article by its ID

		String sql = "SELECT * FROM articleList WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)){
			pstmt.setLong(1, ID);
		    // Prepare the statement to execute the query

			try (ResultSet rs = pstmt.executeQuery()){
				if (rs.next()) {
	                // Retrieve article fields from the result set

					String group = rs.getString("articleGroup");
					String title = rs.getString("title");
	                String authors = rs.getString("authors");
	                String abstractText = rs.getString("abstract");
	                String keywords = rs.getString("keywords");
	                String body = rs.getString("body");
	                String references = rs.getString("references");
	        		// Encryption for the body of the article unless specifically given access
	        		
	        		String encryptedBody = Base64.getEncoder().encodeToString(
	        				encryptionHelper.encrypt(body.getBytes(), EncryptionUtils.getInitializationVector(body.toCharArray()))
	        		);
	        		
	                
	                //decrypt data with title as iv 
	                
	                // Print the article information in a formatted manner

	                System.out.println("+---------------------+");
	                System.out.println("|      Article        |");
	                System.out.println("+---------------------+");
	                System.out.printf("| Group(s): %-20s |\n", group);

	                System.out.printf("| Title: %-20s |\n", title);
	                
	                // Print authors
	                System.out.printf("| Authors: %-17s |\n", authors);
	                
	                // Print abstract
	                System.out.printf("| Abstract: %-15s |\n", abstractText);
	                
	                // Print keywords
	                System.out.printf("| Keywords: %-14s |\n", keywords);
	                
	                // Print body
	                System.out.println("| Body: ");
	                System.out.println("| " + encryptedBody); // Indent each line of the body
	                
	                // Print references
	                System.out.printf("| References: %-13s |\n", references);
	                
	                // Print footer
	                System.out.println("+---------------------+");
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void updateArticle(long id, String groupString, String titleString, String authorsString, String abstractTextString, String keywordsString, String bodyString, String referencesString) throws SQLException {
	    String updateArticle = "UPDATE articleList SET articleGroup = ?, title = ?, authors = ?, abstract = ?, keywords = ?, body = ?, references = ? WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(updateArticle)) {
	        pstmt.setString(1, groupString); 
	        pstmt.setString(2, titleString); // Set title
	        pstmt.setString(3, authorsString); // Set authors
	        pstmt.setString(4, abstractTextString); // Set abstract
	        pstmt.setString(5, keywordsString); // Set keywords
	        pstmt.setString(6, bodyString); // Set body
	        pstmt.setString(7, referencesString); // Set references
	        pstmt.setLong(8, id); // Set the ID of the article to update
	        pstmt.executeUpdate();
	        
	}
	}
	public void updateHelpArticle(String id, String level, String type, String body) throws SQLException {
	    String updateArticle = "UPDATE helparticletable SET article_type = ?, article_level = ?, article_body = ? WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(updateArticle)) {
	        pstmt.setString(2, type); 
	        pstmt.setString(3, level); // Set title
	        pstmt.setString(4, body);
	        
	        pstmt.setLong(1, Integer.valueOf(id)); // Set the ID of the article to update
	        pstmt.executeUpdate();
	        
	}
	}
	public void displayList(String group) throws Exception{
		String sql = "";
		PreparedStatement stmt = null; //create statement 
		if (group.equals("None")) {
			sql = "SELECT * FROM articleList"; 
			stmt = connection.prepareStatement(sql);
		}
		else {
			String[] parsed_group = group.split(", ");
	        StringBuilder placeholders = new StringBuilder();
	        for (int i = 0; i < parsed_group.length; i++) {
	            placeholders.append("?");
	            if (i < parsed_group.length - 1) {
	                placeholders.append(", "); // Add a comma for separation
	            }
	        }
	        sql = "SELECT * FROM articleList WHERE `group` IN (" + placeholders.toString() + ")";
	        stmt = connection.prepareStatement(sql);
	        for (int i = 0; i < parsed_group.length; i++) {
	            stmt.setString(i + 1, parsed_group[i]); 
	        }

		}
		ResultSet rs = stmt.executeQuery(sql);  //execute query to retrieve all articles
		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); //get article ID
			String  title = rs.getString("title");  //get article title
			String authors = rs.getString("authors");  //get authors of article  
		
			// Display values 
			System.out.println("ID: " + id); 
			System.out.println("Title: " + title); 
			System.out.println("Authors: " + authors); 

		} 
	}
	
	public FileRecord createNewFileRecord(String filename, String group) throws SQLException{
		String query;
		String filedata = "";
		if (group.equals("None")) {
			query = "SELECT * FROM articleList";
			filedata = extract(query, "None");
		}
		else {
			String[] parsed_group = group.split(", ");
	        StringBuilder placeholders = new StringBuilder();
	        for (int i = 0; i < parsed_group.length; i++) {
	            placeholders.append("?");
	            if (i < parsed_group.length - 1) {
	                placeholders.append(", "); // Add a comma for separation
	            }
	        }
	        	query = "SELECT * FROM articleList WHERE `group` IN (" + placeholders.toString() + ")";
				filedata = extract(query, group) + filedata;
			}
		FileRecord fileRecord = new FileRecord(filename, filedata);
		return fileRecord;
		
	}
	public void deleteArticle(long id) throws Exception {
	    String sql = "UPDATE articleList SET articleGroup = NULL, title = NULL, authors = NULL, header = NULL, abstract = NULL, keywords = NULL, body = NULL, references = NULL WHERE id = ?"; // SQL update statement
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setLong(1, id); //set ID for deletion 
			int rowsAffected = pstmt.executeUpdate(); //execute the delete statement 
			//check if any rows were affected (i.e. if the article was found and deleted)
			if (rowsAffected > 0) {
                System.out.println("Article fields set to NULL successfully.");
            } else {
                System.out.println("No article found with the given ID.");
            }
		} catch (SQLException e) { //debugging check 
			e.printStackTrace();
			throw e;
		}
	}
	/*
	/*
	 * 
	 * 
	 * Secondary Database interaction
	 */
	
	public void backup(String filename, String group) throws Exception {
		//get to string
		FileRecord fileRecord = createNewFileRecord(filename, group);
		SecondDatabase.storeFileAsBlob(fileRecord);
		System.out.println("System Sucessfully Backed Up");
	}
/**
 * Restore encrypted file to table 
 * @param filename
 * @throws Exception
 */
	public void restore(String filename, boolean merge) throws Exception {
	    FileRecord fileRecord = SecondDatabase.retrieveFileAsBlob(filename);
	    // Check if filedata is null 
	    if (fileRecord == null || fileRecord.getFileData() == null) {
	        System.out.println("Cannot restore: file data is null.");
	        return;
	    }
	    	manageDataWrite(fileRecord, merge);
	}

	/*
	 * Creates a new instance of FileRecord 
	 */

	
	public String extract(String query, String groupName) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setString(2, groupName);

		if(groupName.equals("None")) {
			 preparedStatement = connection.prepareStatement(query);
	    }
		
		ResultSet resultSet = preparedStatement.executeQuery();

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
        return filedata;
	}
	
	public void manageDataWrite(FileRecord myFile, boolean merge) throws IOException, SQLException {
	    // Convert byte array to String using UTF-8 encoding
	    // Clear the table
	if (merge) {

		System.out.print("Merging data");

        // Prepare SQL statement
		
	}
	else {
	    String truncateSQL = "TRUNCATE TABLE articleList"; //truncating table data
	    try (Statement statement = connection.createStatement()) {
	        statement.executeUpdate(truncateSQL);
	    }

        // Prepare SQL statement

	}
    String insertSQL = "INSERT INTO articleList (articleGroup, title, authors, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?, ?)";
    dataWrite(insertSQL, myFile);

	
	}
	public void dataWrite(String query, FileRecord myFile) throws SQLException {
		String dataString = new String(myFile.getFileData());
	    
	    // Split the data into lines
	    String[] lines = dataString.split("\n");

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
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