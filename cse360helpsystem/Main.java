package cse360helpsystem;
//fix

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;


public class Main extends Application {
	Stage window;
	

	private static DatabaseHelper databaseHelper = new DatabaseHelper();
    public void setDatabaseHelper(DatabaseHelper databaseHelper) {
        Main.databaseHelper = databaseHelper;
    }	
	public Scene loginSc, studentSc, instructorSc, roleSc, adminSc, setupSc, establishSc, establishAdminSc, createArticleSc, updateArticleSc, removeArticleSc, restoreSc;
	public 	Alert a = new Alert(AlertType.NONE);

	
	
	
	// Login Page
	@Override
	public void start(Stage loginPage) throws Exception {
		window = loginPage;
		final SimpleBooleanProperty firstSelection = new SimpleBooleanProperty(true);
		
		// Needs if-else that checks if database is empty. If database is empty, we create an admin else we go to the normal login page.
			
			databaseHelper.connectToDatabase();  // Connect to the database
			databaseHelper.connectToSecondaryDatabase();
			if(databaseHelper.isDatabaseEmpty()) {
				// System.println("Database is empty, creating admin");
				window.setScene(establishAdminWindow());
				window.show();
			}
		
		loginPage.setTitle("CSE360 Help System");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		
		// Labels
		Label label = new Label("Login Page");
		
		// Text Fields
		TextField userText = new TextField();
		userText.setPromptText("Username");
		PasswordField passText = new PasswordField();
		passText.setPromptText("Password");
		
		// Buttons
		Button login = new Button("Login");
		Button register = new Button("Register");
		
		//Alerts
		login.setOnAction(e->{
			if(userText.getText().equals("") || passText.getText().equals("")) {
				a.setAlertType(AlertType.WARNING);
				a.setContentText("Fields left empty.");
				a.show();
				userText.clear();
				passText.clear();
				return;
		    }
				
			// Boolean that checks whether login was successful or not
			// Also redirects to finishSetupWindow if setup isn't completely done by now or else just goes to the specific window
		    try {
		        boolean loginSuccessStudent = databaseHelper.login(userText.getText(), passText.getText(), "Student"); // Replace "role" with the actual role you want to check
		        boolean loginSuccessAdmin = databaseHelper.adminLogin(userText.getText(), passText.getText(), "Admin");
		        boolean loginSuccessInstructor = databaseHelper.login(userText.getText(), passText.getText(), "Instructor");
		        if (loginSuccessAdmin){
		        	System.out.println("Login Success Admin");
		        if(!databaseHelper.setupComplete(userText.getText())) {
		            window.setScene(finishSetupWindow(userText.getText(), passText.getText()));
		        	}
		        	else {
							window.setScene(adminWindow());
		        	}
		        } else if (loginSuccessStudent) {
		            System.out.println("Login Success Student");
		          if(!databaseHelper.setupComplete(userText.getText())) {
			            window.setScene(finishSetupWindow(userText.getText(), passText.getText()));
			        	} else {
			        		window.setScene(studentWindow());	
			        	}
		        }
		        else if(loginSuccessInstructor) {
		            System.out.println("Login Success Instructor");
		            if(!databaseHelper.setupComplete(userText.getText())) {
			            window.setScene(finishSetupWindow(userText.getText(), passText.getText()));
			        	} else {
			        		window.setScene(instructorWindow());	
			        	}
		        }
		        else {
		        	a.setAlertType(AlertType.ERROR);
			        a.setContentText("Login error try again.");
			        a.show();
			        return;
		        }
		        
		        
		    } catch (SQLException ex) {
		        ex.printStackTrace();
		        a.setAlertType(AlertType.ERROR);
		        a.setContentText("Database error occurred.");
		        a.show();
		    }
		    
			System.out.println("Username: " + userText.getText());
			System.out.println("Password: " + passText.getText());
			
	
			userText.clear();
			passText.clear();
		
		});
		
		register.setOnAction(e->{
			userText.clear();
			passText.clear();
			window.setScene(establishWindow());
		});
		
		// Fix for AutoFocus
		userText.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
            if(newValue && firstSelection.get()){
            	grid.requestFocus();
            	firstSelection.setValue(false);
            }
        });
		
		// Adding to Grid
		grid.add(label,2,0,1,1);
		grid.add(userText,2,1,1,1);
		grid.add(passText,2,2,1,1);
		grid.add(register,3,4,4,5);
		grid.add(login, 2,4,4,5);
		grid.setVgap(10);
		
		loginSc = new Scene(grid, 640, 480);
		loginPage.setScene(loginSc);
		loginPage.show();
	}


	

	/* Student Window √
	 * ----------------------------------------
	 * Phase 1 requires 
	 * - A logout button √
	*/
	public Scene studentWindow(){
		Label label = new Label("Student Page");
		
		Button logout = new Button("Logout");
		
		TextField createTitle = new TextField();
		createTitle.setPromptText("Enter Title");
		TextField createBody = new TextField();
		createBody.setPromptText("Enter Article");
		
		
		
		logout.setOnAction(e->{
			window.setScene(loginSc);
		});

		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		
		// Adding to Grid
		gPane.add(label, 1,0,1,1);
		gPane.add(logout,1,2,1,1);
		gPane.setVgap(10);
		
		studentSc = new Scene(gPane, 640, 480);
		return studentSc;
	}
	
	
	
	
	/* Instructor Window √
	 * --------------------------------------
	 * Phase 1 requires
	 * - A logout button √
	*/
	public Scene instructorWindow(){
		Label label = new Label("Instructor Page");
		Label articleTitle = new Label("Article Title");
		
		//Buttons
		Button listArticles = new Button("List Article");
		Button viewArticle = new Button("View Article");
		Button createArticle = new Button("Create Article");
		Button updateArticle = new Button("Update Article");
		Button removeArticle = new Button("Remove Article");
		Button logout = new Button("Logout");
		Button restore = new Button("Restore");
		Button backup = new Button("Backup");
		Button removeAll = new Button("Remove All");
		Button mergeAll = new Button("Merge All");
		
		Text text = new Text("Would you like to remove all existing article or merge the back ups with current articles?");
		Text articleText = new Text("This is an article");
		
		TextField createTitle = new TextField();
		createTitle.setPromptText("Enter Title");
		TextField createBody = new TextField();
		createBody.setPromptText("Enter Article");
		
		
		logout.setOnAction(e->{
			window.setScene(loginSc);
		});
		
		
		removeArticle.setOnAction(e->{
			// Enter Article ID
			window.setScene(removeArticleWindow());
		});
		
		updateArticle.setOnAction(e->{
			// Enter Article ID
			window.setScene(updateArticleWindow());
		});
		
		
		createArticle.setOnAction(e->{
			window.setScene(createArticleWindow());
		});
		
		// For Viewing the article, creates a pop up
		viewArticle.setOnAction(e->{
			final Stage articleStage = new Stage();
			articleStage.initModality(Modality.NONE);
            GridPane article = new GridPane();
            
            article.add(articleTitle,2,1, 1, 1);
            article.add(articleText,1,2,1,1);
            
            Scene dialogScene = new Scene(article, 300, 200);
            articleStage.setScene(dialogScene);
            articleStage.show();
		});

		
		
		// Opens pop up Dialog so they user can select to restore or backup
		restore.setOnAction(e->{
			final Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            GridPane dialog = new GridPane();
            
            dialog.add(text,1,1, 1, 1);
            dialog.add(removeAll,1,3,1,1);
            dialog.add(mergeAll,1,4,1,1);
            
            Scene dialogScene = new Scene(dialog, 300, 200);
            dialogStage.setScene(dialogScene);
            dialogStage.show();

		});
		
		
		logout.setOnAction(e->{
			window.setScene(loginSc);
		});
		
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		
		// Adding to Grid
		gPane.add(label, 1,1,1,1);
		gPane.add(viewArticle, 1,2,1,1);
		gPane.add(updateArticle, 1,3,1,1);
		gPane.add(removeArticle, 1,4,1,1);
		gPane.add(listArticles, 1,5,1,1);
		gPane.add(createArticle, 1,6,1,1);

		gPane.add(restore, 1,7,1,1);
		gPane.add(backup,1,8,1,1);
		gPane.add(logout,1,9,1,1);
		gPane.setVgap(10);

		instructorSc = new Scene(gPane, 640, 480);
		return instructorSc;
	}
	
	
	
	
	
	
	/* Admin Window 
	 * --------------------------------------
	 * Phase 1 requires
	 * - Invite to join, generate an OTP - Need database Verification
	 * - Reset User Account - Need databse Verification
	 * - Delete User Account
	 * - List known Accounts
	 * - Add or Remove Roles
	 * - Logout √ 
	*/
	public Scene adminWindow(){
		Label label = new Label("Admin Page");
		Label articleTitle = new Label("Article Title");

		TextField otp = new TextField();

		Button restore = new Button("Restore");
		Button backup = new Button("Backup");
		Button inviteUser = new Button("Invite User");
		Button resetUser = new Button("Reset User Account");
		Button deleteUser = new Button("Delete User Account");
		Button listUsers = new Button("List Known Users");
		Button addRemoveRoles = new Button("Add/Remove Roles");
		Button logout = new Button("Logout");
		Button listArticles = new Button("List Article");
		Button viewArticle = new Button("View Article");
		Button createArticle = new Button("Create Article");
		Button updateArticle = new Button("Update Article");
		Button removeArticle = new Button("Remove Article");
		
		Text articleText = new Text("This is an article");

		updateArticle.setOnAction(e->{
			// Enter Article ID
			window.setScene(updateArticleWindow());
		});
		// Button Functions
		//Logout
		logout.setOnAction(e->{
			window.setScene(loginSc);
		});
		
		
		// Invite User - Generating an OTP to verify in database
		inviteUser.setOnAction(e->{
			otp.setText(OTP.generateOTP());
		});
		
		
		// Reset User - Generating an OTP for user to login with
		resetUser.setOnAction(e->{
			otp.setText(OTP.generateOTP());
		});
		
		
		// Delete User - Delete User account from Database
		deleteUser.setOnAction(e->{
			System.out.println("User Deleted");
		});
		
		// List Users - List user accounts from Database
		listUsers.setOnAction(e->{
			try {
				System.out.println("");
				databaseHelper.printUsers();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		});
		
		//----------------- Article Functions----------------------
		//Create Article
		
		
		//?? FIXME!! Needs a list articles function call? 
		// a backend function exists in databasehelper that intakes an optional group 
		//NOTE:: if no group is specfied to list please pass in group as "None"
		createArticle.setOnAction(e->{
			window.setScene(createArticleWindow());
		});
		
		//Removing an Article
		removeArticle.setOnAction(e->{
			// Enter Article ID
			window.setScene(removeArticleWindow());
		});

		
		// View Article
		viewArticle.setOnAction(e->{
			final Stage articleStage = new Stage();
			articleStage.initModality(Modality.APPLICATION_MODAL);
            GridPane article = new GridPane();
            
            article.add(articleTitle,1,1, 1, 1);
            article.add(articleText,1,2,1,1);
            
            Scene dialogScene = new Scene(article, 300, 200);
            articleStage.setScene(dialogScene);
            articleStage.show();
		});
		
		
		
		// Restore Button
		//Opens a pop up window, add logic for removeAll and mergeAll Buttons
		restore.setOnAction(e->{
			window.setScene(restoreWindow());
		});
		
		
		// Add or Remove Roles - Add or remove roles from user accounts
		
		// Pane Mumbo Jumbo
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		
		// Adding to Grid
		gPane.add(label, 1,0,2,1);
		gPane.add(inviteUser,1,2,1,1);
		gPane.add(otp,3,2,2,1);
		gPane.add(viewArticle, 3,3,1,1);
		gPane.add(updateArticle, 3,4,1,1);
		gPane.add(removeArticle, 3,5,1,1);
		gPane.add(listArticles, 3,6,1,1);
		gPane.add(createArticle, 3,7,1,1);

		gPane.add(resetUser,1,3,2,1);
		gPane.add(deleteUser,1,4,2,1);
		gPane.add(listUsers,1,5,2,1);
		gPane.add(addRemoveRoles,1,6,2,1);
		gPane.add(backup,1,7,2,1);
		gPane.add(restore,1,8,2,1);
		gPane.add(logout,1,9,2,1);
		gPane.setVgap(10);

		adminSc = new Scene(gPane, 640, 480);
		return adminSc;
	}
	
	//UPDATING ARTICLE WINDOW
	public Scene updateArticleWindow() {
		final SimpleBooleanProperty firstSelection = new SimpleBooleanProperty(true);

		TextArea articleID = new TextArea();
		articleID.setPromptText("Enter Article ID");
		articleID.setPrefWidth(400);
		Button update = new Button("Update");
		Button cancel = new Button("Cancel");
	
		GridPane gPane = new GridPane();
	
		articleID.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
            if(newValue && firstSelection.get()){
            	gPane.requestFocus();
            	firstSelection.setValue(false);
            }
        });
		update.setOnAction(event ->{
	        String idText = articleID.getText();
	        try {
	        	long id = Long.parseLong(idText);
	        	
	        	
	        	//
	        	//NEEDAS TO SEND TO AN ARTICLE EDIT SCENE  FIXME!!!
	        	//
	        	
	        	
	        	articleID.clear();
	        } catch (NumberFormatException e) {
	            System.out.println("Invalid Article ID. Please enter a valid number.");
	        }

		});
		
		
		gPane.add(articleID,1,1,1,1);
		gPane.add(update,1,2,1,1);
		gPane.add(cancel,2,2,1,1);
		
		updateArticleSc = new Scene(gPane, 640, 480);
		return updateArticleSc;
	}
	//REMOVING ARTICLE WINDOW
	public Scene removeArticleWindow() {
		final SimpleBooleanProperty firstSelection = new SimpleBooleanProperty(true);

		TextField articleID = new TextField();
		articleID.setPromptText("Enter Article ID");
		articleID.setPrefWidth(400);
		Button remove = new Button("Remove");
		Button cancel = new Button("Cancel");
	
		GridPane gPane = new GridPane();
	
		articleID.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
            if(newValue && firstSelection.get()){
            	gPane.requestFocus();
            	firstSelection.setValue(false);
            }
        });
		
		remove.setOnAction(event ->{
	        String idText = articleID.getText();
	        try {
	        	long id = Long.parseLong(idText);
	        	databaseHelper.deleteArticle(id);
	        	articleID.clear();
	        } catch (NumberFormatException e) {
	            System.out.println("Invalid Article ID. Please enter a valid number.");
	        } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
		
		gPane.add(articleID,1,1,1,1);
		gPane.add(remove,1,2,1,1);
		gPane.add(cancel,2,2,1,1);
		
		removeArticleSc = new Scene(gPane, 640, 480);
		return removeArticleSc;
	}
	
	//CREATING AN ARTICLE WINDOW
	public Scene createArticleWindow() {
		final SimpleBooleanProperty firstSelection = new SimpleBooleanProperty(true);

		Button create = new Button("Create");
		Button back = new Button("Back");
		
		TextField createTitle = new TextField();
		createTitle.setPromptText("Title");
		TextField createAuthors = new TextField();
		createAuthors.setPromptText("Authors");
		TextField createAbstract = new TextField();
		createAbstract.setPromptText("Abstract");
		TextField createGroup = new TextField();
		createGroup.setPromptText("Group");
		TextField createHeader = new TextField();
		createHeader.setPromptText("Header");
		TextArea createBody = new TextArea();
		createBody.setPromptText("Enter article");
		createBody.setPrefHeight(250);
		TextField createKeywords = new TextField();
		createKeywords.setPromptText("Keywords");
		TextField createReferences = new TextField();
		createReferences.setPromptText("References");
		
		back.setOnAction(e->{
			window.setScene(adminWindow());
		});
		
		GridPane gPane = new GridPane();
		
		create.setOnAction(e->{
			try {
				databaseHelper.register(createGroup.getText(), createTitle.getText(),createHeader.getText(), createAuthors.getText(), createAbstract.getText(), createKeywords.getText(), createBody.getText(), createReferences.getText());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			window.setScene(adminWindow());
		});
		
		
		
		createTitle.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
            if(newValue && firstSelection.get()){
            	gPane.requestFocus();
            	firstSelection.setValue(false);
            }
        });
		
		gPane.setAlignment(Pos.CENTER);
		gPane.add(createTitle,1,1,2,1);
		gPane.add(createHeader,1,2,2,1);
		gPane.add(createBody,1,3,2,1);
		gPane.add(createAbstract,1,4,2,1);
		gPane.add(createGroup,1,5,2,1);
		gPane.add(createKeywords,1,6,2,1);
		gPane.add(createReferences,2,6,2,1);
		gPane.add(create,1,7,1,1);
		gPane.add(back,2,7,1,1);
		
		createArticleSc = new Scene(gPane,640,480);
		return createArticleSc;
	}
	
	/* Establish Account Window
	 * -----------------------------------
	 * - Username 
	 * - Password 
	 * - Create
	 * 
	 * Needs Logic for database and textfield entries
	 * */
	public Scene establishWindow(){
		Label label = new Label("Create Account");
		Button create = new Button("Create");
		Button backButton = new Button("Back");
		backButton.setOnAction(e -> window.setScene(loginSc));
		TextField user = new TextField();
		user.setPromptText("Username");
		PasswordField pass = new PasswordField();
		pass.setPromptText("Password");
		PasswordField verifyPass = new PasswordField();
		verifyPass.setPromptText("Re-enter Password");
		
		
		create.setOnAction(e->{
			if(!pass.getText().equals(verifyPass.getText())) {
				a.setAlertType(AlertType.WARNING);
				a.setContentText("Passwords do Not match");
				a.show();
				user.clear();
				pass.clear();
				verifyPass.clear();
				return;
			}
			
			if (databaseHelper.doesUserExist(user.getText())) {
			    a.setAlertType(AlertType.WARNING);
			    a.setContentText("User already exists!");
			    a.show();
			    user.clear();
			    pass.clear();
			    verifyPass.clear();
			    return;
			}
			else {
				 String username = user.getText();      // Get the username when button is clicked
			     String password = pass.getText();      // Get the password when button is clicked
			        try {
			    		if(databaseHelper.isDatabaseEmpty()) {
			    			databaseHelper.register(username, password, "Admin");
						}
						else {
							databaseHelper.register(username, password, "Student");
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
			}
			window.setScene(loginSc);
		});
		
		
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		

		
		// Adding to Grid
		gPane.add(label, 2,0,1,1);
		gPane.add(user, 2,1,1,1);
		gPane.add(pass, 2,2,1,1);
		gPane.add(verifyPass, 2,3,1,1);
		gPane.add(create,2,5,1,1);
		gPane.add(backButton,3,5,1,1);
		gPane.setVgap(10);
		

		
		establishSc = new Scene(gPane, 640, 480);
		return establishSc;
	}
	/* Establish Account Window
	 * -----------------------------------
	 * - Username 
	 * - Password 
	 * - Create
	 * 
	 * Needs Logic for database and textfield entries
	 * */
	public Scene establishAdminWindow(){
		Label label = new Label("Create Admin Account");
		Button create = new Button("Create");
		Button backButton = new Button("Back");
		backButton.setOnAction(e -> window.setScene(loginSc));
		TextField user = new TextField();
		user.setPromptText("Username");
		PasswordField pass = new PasswordField();
		pass.setPromptText("Password");
		PasswordField verifyPass = new PasswordField();
		verifyPass.setPromptText("Re-enter Password");

		
		
		create.setOnAction(e->{
			if(!pass.getText().equals(verifyPass.getText())) {
				a.setAlertType(AlertType.WARNING);
				a.setContentText("Passwords do Not match");
				a.show();
				user.clear();
				pass.clear();
				verifyPass.clear();
				return;
			}
			else {
				 String username = user.getText();      // Get the username when button is clicked
			     String password = pass.getText();      // Get the password when button is clicked
			     String role = "Admin";
			        try {
						databaseHelper.register(username, password, role);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
			}
			window.setScene(loginSc);
		});
		
		
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		

		
		// Adding to Grid
		gPane.add(label, 2,0,1,1);
		gPane.add(user, 2,1,1,1);
		gPane.add(pass, 2,2,1,1);
		gPane.add(verifyPass, 2,3,1,1);
		gPane.add(create,2,5,1,1);
		gPane.setVgap(10);
		

		
		establishAdminSc = new Scene(gPane, 640, 480);
		return establishAdminSc;
	}
	
	public Scene establishLogin(){
		Label label = new Label("Login");
		Button create = new Button("Create");
		Button backButton = new Button("Back");
		backButton.setOnAction(e -> window.setScene(establishSc));
		TextField user = new TextField();
		user.setPromptText("Username");
		PasswordField pass = new PasswordField();
		pass.setPromptText("Password");
		PasswordField verifyPass = new PasswordField();
		verifyPass.setPromptText("Re-enter Password");

		
		
		create.setOnAction(e->{
			if(!pass.getText().equals(verifyPass.getText())) {
				a.setAlertType(AlertType.WARNING);
				a.setContentText("Passwords do Not match");
				a.show();
				user.clear();
				pass.clear();
				verifyPass.clear();
				return;
			}
			else {
				 String username = user.getText();      // Get the username when button is clicked
			     String password = pass.getText();      // Get the password when button is clicked
			     String role = "Admin";
			        try {
						databaseHelper.register(username, password, role);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
			}
			window.setScene(loginSc);
		});
		
		
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		

		
		// Adding to Grid
		gPane.add(label, 2,0,1,1);
		gPane.add(user, 2,1,1,1);
		gPane.add(pass, 2,2,1,1);
		gPane.add(verifyPass, 2,3,1,1);
		gPane.add(create,2,5,1,1);
		gPane.setVgap(10);
		

		
		establishSc = new Scene(gPane, 640, 480);
		return establishSc;
	}
	

	
	/* Finish Setting Up Account Window
	 * ---------------------------------------
	 * 
	 * 
	 * */
	public Scene finishSetupWindow(String username, String password){
		final SimpleBooleanProperty firstSelection = new SimpleBooleanProperty(true);
		Label label = new Label("Finish Setting Up Your Account");
		Button advance = new Button("Continue");
		TextField email = new TextField();
		email.setPromptText("Email Address");
		TextField firstName = new TextField();
		firstName.setPromptText("First Name");
		TextField middleName = new TextField();
		middleName.setPromptText("Middle Name (Optional)");
		TextField lastName = new TextField();
		lastName.setPromptText("Last Name");
		TextField prefName = new TextField();
		prefName.setPromptText("Prefered Name");
		/* Continue Button 
		 * ------------------------
		 * Add routing to users correct window
		 * If more than one role redirect to roleWindow()
		 * */
		
		advance.setOnAction(e->{
			
			if(firstName.getText()=="" || lastName.getText()=="" || prefName.getText()=="") {
				a.setAlertType(AlertType.WARNING);
				a.setContentText("Fields left empty.");
				a.show();
				return;
			} 

				try {
					databaseHelper.updateUserDetails(username, firstName.getText(), lastName.getText(), prefName.getText());
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}			

			// If role = student, redirect to student
			// If role = admin, redirect to admin
			// If role = instructor, redirect to instructor
			// If multiple roles, redirect to role selection
			try {
				if (databaseHelper.login(username, password, "Admin")){
					window.setScene(adminWindow());
				}
				else {
					window.setScene(studentWindow());
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//System.out.println("Logout");
		});
		
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		
		//Fix for AutoFocus
		firstName.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
            if(newValue && firstSelection.get()){
            	gPane.requestFocus();
            	firstSelection.setValue(false);
            }
        });
		
		// Adding to Grid
		gPane.add(label, 1,1,1,1);
		gPane.add(firstName,1,2,1,1);
		gPane.add(middleName,1,3,1,1);
		gPane.add(lastName, 1,4,1,1);
		gPane.add(prefName,1,5,1,1);
		gPane.add(advance,1,6,1,1);
		gPane.setVgap(10);
		
		setupSc = new Scene(gPane, 640, 480);
		return setupSc;
	}
	
	public Scene restoreWindow() {
		Button removeAll = new Button("Remove All");
		Button mergeAll = new Button("Merge All");
		Button goBack = new Button("Go Back");
		
		Text text = new Text("What would you like to do?");
		
		
		
		GridPane gPane = new GridPane();
		
		//FIXME!! need to add drop table logic for this?  
		
		
		
		// Add logic to remove the thingies
		removeAll.setOnAction(e->{
			
		});
		
		
		// Add logic for merging the thingies
		////
		/// FIXME!! Connect to databaseHelper.restore which takes the filename and a boolean 
		//value for if it needs to merge or not 
		///
		mergeAll.setOnAction(e->{
			
		});
		
		goBack.setOnAction(e->{
			window.setScene(adminWindow());
		});
		
		gPane.add(text,1,1,1,1);
		gPane.add(removeAll,1,2,1,1);
		gPane.add(mergeAll,2,2,1,1);
		gPane.add(goBack,3,2,1,1);
		
		restoreSc = new Scene(gPane, 640, 480);
		return restoreSc;
	}

public static void main(String[] args) {
	Application.launch(args);
	

	}
}
