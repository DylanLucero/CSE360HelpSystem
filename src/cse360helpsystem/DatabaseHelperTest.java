import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.sql.Statement;

class DatabaseHelperTest {

	public static DatabaseHelper dbHelper = new DatabaseHelper();
	SecondDatabase sDBHelper = new SecondDatabase();
	SpecialAccess specialAccess = new SpecialAccess();
/*
	@Test
	void Test() throws SQLException{
		dbHelper.connectToDatabase();
	}
 */
	


	
	//    private void dropTable(String tableName)
	@Test
	void dropTableTest() throws SQLException{
		dbHelper.connectToDatabase();
		dbHelper.createTableUsers();
		dbHelper.dropTable("cse360users");
	}
	

	//	public void createArticle(String groupString, String titleString, String headerString, String authorsString, String abstractTextString, String keywordsString, String bodyString, String referencesString) throws Exception {
	@Test
	void createArticlesTest() throws SQLException{
		dbHelper.connectToDatabase();
		dbHelper.createTableArticles();
		
		String group = "A";
		String title = "This is Title";
		String header = "This is Header";
		String authors = "These are authors";
		String abstr = "This is abstract";
		String keyword = "keyword";
		String body = "This is body";
		String reference = "This is reference";
		
		dbHelper.createArticles(group,title,header,authors,abstr,keyword,body,reference);
		dbHelper.accessArticle(1);
	}
	
	//	public void updateArticle(long id, String groupString, String titleString, String authorsString, String abstractTextString, String keywordsString, String bodyString, String referencesString) throws SQLException {
	@Test
	void updateArticleTest() throws SQLException{
		dbHelper.connectToDatabase();
		dbHelper.createTableArticles();
		
		String group = "A";
		String title = "This is Title";
		String header = "This is Header";
		String authors = "These are authors";
		String abstr = "This is abstract";
		String keyword = "keyword";
		String body = "This is body";
		String reference = "This is reference";
		
		dbHelper.createArticles(group,title,header,authors,abstr,keyword,body,reference);
		
		body = "This is the new body";
		dbHelper.updateArticle(1,group,title,author,abstr,keyword,body,reference);
	}
	
	//	public void deleteArticle(long id) throws Exception {
	@Test
	void deleteArticleTest() throws SQLException{
		dbHelper.connectToDatabase();
		dbHelper.createTableArticles();
		
		String group = "A";
		String title = "This is Title";
		String header = "This is Header";
		String authors = "These are authors";
		String abstr = "This is abstract";
		String keyword = "keyword";
		String body = "This is body";
		String reference = "This is reference";
		
		dbHelper.createArticles(group,title,header,authors,abstr,keyword,body,reference);
		dbHelper.deleteArticle(1);
	}

	//	public void createHelpArticle(String type, String level, String body) throws SQLException {
	@Test
	void createHelpArticleTest() throws SQLException{
		dbHelper.connectToDatabase();
		dbHelper.createHelpArticleTable();
		
		String type = "A",
				level = "Advance",
				body = "This is body";
		
		dbHelper.createHelpArticle(type,level,body);
		dbHelper.accessHelpArticle("1");
	}
	
	//	public void updateHelpArticle(String id, String level, String type, String body) throws SQLException {
	@Test
	void updateHelpArticleTest() throws SQLException{
		dbHelper.connectToDatabase();
		dbHelper.createHelpArticleTable();
		
		String type = "A",
				level = "Advance",
				body = "This is body";
		
		dbHelper.createHelpArticle(type,level,body);
		
		body = "This is the new body";
		dbHelper.updateHelpArticle("1",level,type,body);
	}
	
	//	public void deleteHelpArticle(String id) throws Exception {
	@Test
	void deleteHelpArticleTest() throws SQLException{
		dbHelper.connectToDatabase();
		dbHelper.createHelpArticleTable();
		
		String type = "A",
				level = "Advance",
				body = "This is body";
		
		dbHelper.createHelpArticle(type,level,body);
		
		dbHelper.deleteHelpArticle("1");
	}
	
	//test related to blob
	//	public FileRecord createNewFileRecord(String filename, String group) throws SQLException{
	@Test
	void createFileRecordTest() throws SQLException{
		dbHelper.connectToDatabase();
		String fileName = "This_is_Name",
				group = "A";
		assertNotNull(dbHelper.createNewFileRecord(fileName,group));
	}
	
	//	public String extract(String query, String groupName) throws SQLException {
	@Test
	void extractTest() throws SQLException{
		dbHelper.connectToDatabase();
		dbHelper.connectToDatabase();
		String fileName = "This_is_Name",
				group = "A";
		dbHelper.createNewFileRecord(fileName,group);
		
		assertNotNull(dbHelper.extract(fileName,group));
	}
	
	//test relates to backup and encryption
	//	public void backup(String filename, String group) throws Exception {
	@Test
	void backupTest() throws SQLException{
		dbHelper.connectToDatabase();
		sDBHelper.connectToDatabase();
		
		String fileName = "This_is_Name",
				group = "A";
		
		sDBHelper.backup(fileName,group);
	}
	
	//	public void restore(String filename, boolean merge) throws Exception {
	void restoreTest() throws SQLException{
		dbHelper.connectToDatabase();
		sDBHelper.connectToDatabase();
		
		String fileName = "This_is_Name",
				group = "A";
		
		sDBHelper.backup(fileName,group);
		
		sDBHelper.restore(fileName,true);
	}
	
	
}
