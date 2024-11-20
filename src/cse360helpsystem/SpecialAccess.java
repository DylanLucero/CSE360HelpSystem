package cse360helpsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


class SpecialAccess{
   
	static final String JDBC_DRIVER = "org.h2.Driver";   
    static final String DB_URL = "jdbc:h2:~/secondDatabase";  

    // Database credentials 
    static final String USER = "sa"; 
    static final String PASS = ""; 

    private static Connection connection = null;
    private Statement statement = null; 
    
    public void connectToSpecialAccessDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement(); 
            createTableSpecialAccess();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }
 
	
	private void createTableSpecialAccess() {
		String createSpecialAccessArticlesSQL = "CREATE TABLE if NOT EXISTS specialAccessArticles ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "articleListId BIGINT, " //id of the article in articleList
				+ "articleGroup VARCHAR(255)"
				+ ");"; 
	    String createSpecialDecryptedAccessSQL = "CREATE TABLE IF NOT EXISTS specialAccessToDecrypted ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "role VARCHAR(50), "
	    		+ "username VARCHAR(100)"
	    		+ ");";
	    String createSpecialAdminListSQL = "CREATE TABLE IF NOT EXISTS specialAccessAdmins ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "role VARCHAR(50), "
	    		+ "username VARCHAR(100)"
                + ");";
	    try (Statement stmt = connection.createStatement()) {
	    	stmt.execute(createSpecialAccessArticlesSQL);
	        stmt.execute(createSpecialDecryptedAccessSQL);
	        stmt.execute(createSpecialAdminListSQL);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public String pullUserRole(String user) throws SQLException {
		String role = null;
		String roleRetrieval = "SELECT * FROM cse360users WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(roleRetrieval)) {
			pstmt.setString(1, roleRetrieval);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				role = rs.getString("role");
			}
		}
		return role;
	}
	
	private void addToSpecialAccessAdmins(String user) throws SQLException {
		String role = pullUserRole(user);
		String insertUser = "INSERT INTO specialAccessAdmins (role, username) VALUES (?,?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, role);
			pstmt.setString(2, user);
			pstmt.executeUpdate();
		}
	}
	
	public void listEncryptedArticles(String articleGroup) throws SQLException {
		String groupRetrieval = "SELECT * FROM specialAccessArticles WHERE articleGroup = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(groupRetrieval)){
			pstmt.setString(1, articleGroup);
			try (ResultSet rs = pstmt.executeQuery()){
				if (rs.next()) {
					rs.getLong("id");
					//FIXME something here to display them however front end works
				}
			}
		}
	}
	
	public void listRoleWithAdminAccess(String role) {
		
	}
	
	public void listRoleWithDecryptionAccess(String role) {
		
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



