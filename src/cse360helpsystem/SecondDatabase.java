package cse360helpsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <p> AnotherDatabase class </p>
 * 
 *
 *
 * <p>Description: A Java application that can restore and store files of articles </p>
 * 
 * @author Olivia Pratt
 * 
 * @version 1.0
 */

class SecondDatabase {

    // JDBC driver name and database URL 
    static final String JDBC_DRIVER = "org.h2.Driver";   
    static final String DB_URL = "jdbc:h2:~/secondDatabase";  

    // Database credentials 
    static final String USER = "sa"; 
    static final String PASS = ""; 

    private static Connection connection = null;
    private Statement statement = null; 

    /*
     * This is the another database method
     */
	public SecondDatabase() throws Exception {
		
	}
	/*
	 * This method connects to database
	 */
    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement(); 
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }
    /**
     * This method creates the file table
     */
    public void createTables() throws SQLException {
    	String fileTable = "CREATE TABLE IF NOT EXISTS fileTable ("
    			+ "id INT AUTO_INCREMENT PRIMARY KEY, "
    			+ "filename VARCHAR(255), " //store filename
    			+ "filedata BLOB" //store data
    			+ ");";
        statement.execute(fileTable); // Execute the table creation

    }
    /**
     * This method store the filename and data into file table
     * 
     * @param file name and encrypted file data as byte[]
     * 
     */
    public static void storeFileAsBlob(String filename, byte[] fileData) throws SQLException {
        String sql = "INSERT INTO fileTable (filename, filedata) VALUES (?, ?)";
       //send sql statement to insert filedata into table
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, filename); //set filename
            pstmt.setBytes(2, fileData); //set data 
            try {
                pstmt.executeUpdate(); //send update
            } catch (SQLException e) {
                // Handle unique constraint violation
                if (e.getErrorCode() == 23505) { // Unique constraint violation code for H2
                    System.out.println("File with this name already exists. Consider updating it.");
                  
                } else {
                    throw e; 
                }
            }
        }
    }
/**
 * This method retrieves a file from the table 
 * 
 * @param name of file 
 * @return encrypted byte string
 */
    public static FileRecord retrieveFileAsBlob(String filename) throws SQLException {
        String sql = "SELECT filename, filedata FROM fileTable WHERE filename = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, filename);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String retrievedFilename = rs.getString("filename");
                byte[] fileData = rs.getBytes("filedata");
                return new FileRecord(retrievedFilename, fileData);
            } else {
            	System.out.println("File not found.");
            	return null;
            }
        }
    }
    /*
     * Method to list file table contents
     */
    public static void listFileTableContents() throws SQLException {
        String tableName = "fileTable"; // Specify the table name
        System.out.println("Table: " + tableName);
        listTableContents(tableName);
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


    public void closeConnection() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
                System.out.println("Database connections closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }	

}