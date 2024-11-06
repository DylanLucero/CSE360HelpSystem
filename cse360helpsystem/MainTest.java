package cse360helpsystem;

import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;

public class MainTest {
	
	public static DatabaseHelper dbHelper = new DatabaseHelper();
	public static SecondDatabase dbHelper2 = new SecondDatabase();

    @Test
    public void testRegisterUser() throws SQLException {
    	dbHelper.connectToDatabase();  // Connect to the database

        String username = "testUser";
        String password = "password123";
        String role = "Student";

        dbHelper.register(username, password, role);
        assertTrue(dbHelper.doesUserExist(username));
    }
    @Test
    public void testLogin() throws SQLException {
    	dbHelper.connectToDatabase();  // Connect to the database

        String username = "testUser";
        String password = "password123";
        String role = "Student";

        dbHelper.login(username, password, role);
        assertFalse(dbHelper.doesUserExist(username));
    }
    @Test
    public void testAdmin() throws SQLException {
    	dbHelper.connectToDatabase();  // Connect to the database

        String username = "testUser";
        String password = "password123";
        String role = "Admin";

        dbHelper.login(username, password, role);
        assertFalse(dbHelper.doesUserExist(username));
    }
    
    //Testing of Access and Creation of article
    @Test
    public void testCreate() throws Exception{
    	dbHelper.connectToDatabase();  // Connect to the database
    	dbHelper2.connectToDatabase();

    	String articleTitle = "Test Article";
    	String group = "Testing";
    	
    	dbHelper.createNewFileRecord(articleTitle, group);
    	dbHelper.accessArticle(1);
    	
    }

    @Test
    public void testBackup() throws Exception{
    	dbHelper.connectToDatabase();  // Connect to the database
    	dbHelper2.connectToDatabase();

    	
    	String articleTitle = "Test Article";
    	String group = "Testing";
    	
    	dbHelper.createNewFileRecord(articleTitle, group);

    	dbHelper.backup("Test Article", "Testing");
    }


   }

  