package cse360helpsystem;
//fix

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.sql.SQLException;


public class Main extends Application {
	Stage window;
	
	private static DatabaseHelper databaseHelper;

	static {
	    try {
	        databaseHelper = new DatabaseHelper();
	    } catch (Exception e) {
	        e.printStackTrace();
	        // Optionally, terminate the application if the database cannot be initialized
	        throw new RuntimeException("Failed to initialize DatabaseHelper", e);
	    }
	}
	
    public void setDatabaseHelper(DatabaseHelper databaseHelper) {
        Main.databaseHelper = databaseHelper;
    }	
	public Scene loginSc, studentSc, instructorSc, roleSc, adminSc, setupSc, 
			establishSc, establishAdminSc, createArticleSc, updateArticleSc, 
			removeArticleSc, restoreSc, helpSc, searchSc, viewHelpArticleSc,
			removeHelpArticleSc,displayArticleSc, displayHelpArticleSc, numInput;
	
	public 	Alert a = new Alert(AlertType.NONE);

	
	
	
	// Login Page
	@Override
	public void start(Stage loginPage) throws Exception {
		window = loginPage;
		final SimpleBooleanProperty firstSelection = new SimpleBooleanProperty(true);
		
		// Needs if-else that checks if database is empty. If database is empty, we create an admin else we go to the normal login page.
			
			databaseHelper.connectToDatabase();  // Connect to the database
			databaseHelper.connectToSecondaryDatabase();
			databaseHelper.connectToHelpArticleDatabase();
			
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
		        else if (databaseHelper.doesUserExist(userText.getText())) {
		            // No role assigned, go to roleWindow
		            System.out.println("Login Success - No Role Assigned");
		            window.setScene(roleSelect()); // Redirect to role selection window
		        }
		        else {
		        	//System.out.println("Login Error");
	        		window.setScene(studentWindow());	

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
		Button help = new Button("Help");
		Button viewHelp = new Button("View Help Article");
		Button search = new Button("Search");
		Button viewArticle = new Button("View Article");

		
		//databaseHelper.deleteHelpArticle();
		
		
		logout.setOnAction(e->{
			window.setScene(loginSc);
		});
		
		help.setOnAction(e->{
			window.setScene(helpWindow());
		});
		
		search.setOnAction(e->{
			window.setScene(searchWindow());
		});
		
		viewHelp.setOnAction(e->{
			window.setScene(displayHelpArticleWindow());
		});
		viewArticle.setOnAction(e->{
			try {
				window.setScene(displayArticleWindow());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		

		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		
		// Adding to Grid
		gPane.add(label, 1,0,1,1);
		gPane.add(help, 1,1,1,1);
		gPane.add(search, 1, 2);
		gPane.add(viewHelp, 1,3);
		gPane.add(viewArticle, 1,4);
		gPane.add(logout,1,5,1,1);
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
			try {
				databaseHelper.accessArticle(1);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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

		TextField otp = new TextField();

		Button restore = new Button("Restore");
		Button backup = new Button("Backup");
		Button backupHelp = new Button("Backup Help Article");
		Button restoreHelp = new Button("Restore Help Article");
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
		Button removeHelpArticle = new Button("Remove Help Article");
		
		
		
		// Admin no longer as the right to edit articles
		
//		updateArticle.setOnAction(e->{
//			// Enter Article ID
//			window.setScene(updateArticleWindow());
//		});
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
			window.setScene(listUsersWindow());
		});
		
		createArticle.setOnAction(e->{
			window.setScene(createArticleWindow());
		});
		
		//Removing an Article
		removeArticle.setOnAction(e->{
			// Enter Article ID
			window.setScene(removeArticleWindow());
		});
		removeHelpArticle.setOnAction(e->{
			window.setScene(removeHelpArticleWindow());
		});

		 

	viewArticle.setOnAction(e->{
		try {
			window.setScene(displayArticleWindow());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		
		// Help Srticle Buttons
		gPane.add(removeHelpArticle, 0, 3);
		gPane.add(backupHelp, 0, 4);
		gPane.add(restoreHelp, 0, 5);
		gPane.setVgap(10);

		adminSc = new Scene(gPane, 640, 480);
		return adminSc;
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
	

	
	public Scene roleWindow(){
        Label title = new Label("Role Selection");
        MenuButton role = new MenuButton("Click to Select Role");
        MenuItem sButton = new MenuItem("Student");
        MenuItem iButton = new MenuItem("Instructor");
        MenuItem aButton = new MenuItem("Admin");



        // Button Actions for Menu
        sButton.setOnAction(e->{ 
            window.setScene(studentWindow());
            System.out.println("Student View");
            });

        aButton.setOnAction(e->{ 
            window.setScene(adminWindow());
            System.out.println("Admin View");
        });

        iButton.setOnAction(e->{ 
            window.setScene(instructorWindow());
            System.out.println("Instructor View");
        });

        // Adding to Grid
        role.getItems().addAll(sButton, iButton, aButton);
        GridPane gPane = new GridPane();
        gPane.setAlignment(Pos.CENTER);
        gPane.add(role, 3,2,1,1);
        gPane.add(title,3,1,1,1);
        gPane.setVgap(10);

        studentSc = new Scene(gPane, 640, 480);
        return studentSc;
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
		
		cancel.setOnAction(e->{
			window.setScene(adminWindow());
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
				databaseHelper.createArticle(createGroup.getText(), createTitle.getText(),createHeader.getText(), createAuthors.getText(), createAbstract.getText(), createKeywords.getText(), createBody.getText(), createReferences.getText());
				System.out.println("Success");
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
		gPane.add(createKeywords,1,6,1,1);
		gPane.add(createAuthors, 2, 6);
		gPane.add(createReferences,3,6,1,1);
		gPane.add(create,1,7,1,1);
		gPane.add(back,2,7,1,1);
		
		createArticleSc = new Scene(gPane,640,480);
		return createArticleSc;
	}
	public Scene roleSelect() {
        // Create buttons
        Button send = new Button("Send");
        Button back = new Button("Back");

        // Create dropdown options
        ObservableList<String> options = FXCollections.observableArrayList(
            "Student",
            "Instructor"
        );

        // Dropdown for selecting roles
        final ComboBox<String> role = new ComboBox<>(FXCollections.observableArrayList());
        final ComboBox<String> select = new ComboBox<>(options);

        // Set prompts
        select.setPromptText("Question Type");
        role.setPromptText("Role");

        // Send button logic
        send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String selectedOption = select.getValue();
                if (selectedOption != null) {
                    role.setValue(selectedOption); // Add the selected role to the role dropdown
                    System.out.println("Selected Role: " + selectedOption); // For debugging
                } else {
                    System.out.println("No role selected!");
                }
            }
        });

        // Back button logic
        back.setOnAction(e -> {
            if (studentSc != null) {
                window.setScene(studentSc); // Return to the previous screen
            } else {
                System.out.println("No previous scene set!");
            }
        });

        // GridPane layout
        GridPane gPane = new GridPane();
        gPane.setHgap(10);
        gPane.setVgap(10);

        // Add components to GridPane
        gPane.add(new Label("Select Role:"), 0, 0);
        gPane.add(select, 1, 0);
        gPane.add(new Label("Assigned Role:"), 0, 1);
        gPane.add(role, 1, 1);
        gPane.add(back, 0, 2);
        gPane.add(send, 1, 2);

        // Return the scene
        return new Scene(gPane, 400, 200);
    }

	
	public Scene restoreWindow() {
		Button removeAll = new Button("Remove All");
		Button mergeAll = new Button("Merge All");
		Button goBack = new Button("Go Back");
		
		Text text = new Text("What would you like to do?");
		
		TextField itemID = new TextField();
		
		GridPane gPane = new GridPane();
		
		
		// Add logic to remove the thingies
		removeAll.setOnAction(e->{
			try {
				databaseHelper.restore(itemID.getText(), false);
				System.out.println("Successfully Removed");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		
		// Add logic for merging the thingies
		mergeAll.setOnAction(e->{
			System.out.println("Successfully Merged");
			try {
				databaseHelper.restore(itemID.getText(), true);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
	public Scene updateArticleWindow() {
		final SimpleBooleanProperty firstSelection = new SimpleBooleanProperty(true);

		TextField articleID = new TextField();
		TextField group = new TextField();
		TextField title = new TextField();
		TextField author = new TextField();
		TextField abstractText = new TextField();
		TextField keywords = new TextField();
		TextField references = new TextField();

		TextArea articleBody= new TextArea();
		//articleBody.setPrefHeight(400);

		articleID.setPromptText("Enter Article ID");
		articleBody.setPromptText("Enter new Body");
		group.setPromptText("Enter Group");
		title.setPromptText("Enter Title");
		author.setPromptText("Set Author");
		abstractText.setPromptText("Abstract Text");
		keywords.setPromptText("Keywords");
		references.setPromptText("References");
		Button updateArticle = new Button("Update");
		Button cancel = new Button("Cancel");
	
		GridPane gPane = new GridPane();
		

	
		articleID.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
            if(newValue && firstSelection.get()){
            	gPane.requestFocus();
            	firstSelection.setValue(false);
            }
        });
		
		updateArticle.setOnAction(e->{
			if(articleID.getText() == "" ||articleBody.getText() == "" || group.getText() == "" || author.getText() == "" || title.getText() == "" ||
					abstractText.getText() == "" || keywords.getText() == "" || references.getText() == "") 
			{
				a.setAlertType(AlertType.WARNING);
				a.setContentText("Fields left empty.");
				a.show();
				return;
			}
			try {
		        String idText = articleID.getText();
	        	long id = Long.parseLong(idText);
	        	//long id, String groupString, String titleString, String authorsString, String abstractTextString, String keywordsString, String bodyString, String referencesString
				databaseHelper.updateArticle(id, group.getText(), title.getText(), author.getText(), abstractText.getText(), keywords.getText(), articleBody.getText(), references.getText());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		cancel.setOnAction(e->{
			window.setScene(adminWindow());
		});
		
		gPane.add(articleID,3,1,1,1);
		gPane.add(group,2,1,1,1);
		gPane.add(title, 1,1,1,1);
		gPane.add(author, 3,3,1,1);
		gPane.add(author, 1,2,1,1);
		gPane.add(abstractText,2,3,1,1);
		gPane.add(keywords, 2,2,1,1);
		gPane.add(references, 3,2,1,1);
		gPane.add(articleBody, 1,2,1,1);
		gPane.add(updateArticle,1,3,1,1);
		gPane.add(cancel,1,4,1,1);
		
		updateArticleSc = new Scene(gPane, 640, 480);
		return updateArticleSc;
	}
	
	public Scene helpWindow() {		
		Button send = new Button("Send");
		Button back = new Button("Go Back");
		TextField title = new TextField();
		title.setPromptText("Title");
		
		TextArea prompt = new TextArea();
		prompt.setPromptText("Enter your question");
	
		ObservableList<String> options = 
			    FXCollections.observableArrayList(
			        "Generic",
			        "Specific"
			    );
		ObservableList<String> levelOptions = 
			    FXCollections.observableArrayList(
			        "Beginner",
			        "Intermediate",
			        "Advanced",
			        "Expert"
			    );

			final ComboBox<String> level = new ComboBox<String>(levelOptions);
			level.setPromptText("Select Level");
			
			final ComboBox<String> select = new ComboBox<String>(options);
			select.setPromptText("Question Type");
			

	        send.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent e) {
	                if (select.getValue().toString() == "Generic" && 
	                    !select.getValue().toString().isEmpty()){
	                	if(prompt.getText() == null || prompt.getText() == "") {
	                		a.setAlertType(AlertType.WARNING);
	                		a.setContentText("Body Left Blank!");
	                		a.show();
	                		return;
	                	}try {
							databaseHelper.createHelpArticle(title.getText(),select.getValue().toString(), level.getValue().toString(), prompt.getText());
							System.out.println("Success");
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	                        System.out.println("Your message was successfully sent");   
	                        a.setAlertType(AlertType.INFORMATION);
	                        a.setContentText("Message Sent Successfully");
	                        a.show();
	                        prompt.clear();
//	                        text.clear();
	                }
	                else if(select.getValue().toString() == "Specific" && 
		                    !select.getValue().toString().isEmpty()){
		                	if(prompt.getText() == null || prompt.getText() == "") {
		                		a.setAlertType(AlertType.WARNING);
		                		a.setContentText("Body Left Blank!");
		                		a.show();
		                		return;
		                	}
		                		try {
									databaseHelper.createHelpArticle( title.getText(),select.getValue().toString(), level.getValue().toString(), prompt.getText());
									System.out.println("Success");
								} catch (SQLException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
		                        System.out.println("Your message was successfully sent");   
		                        a.setAlertType(AlertType.INFORMATION);
		                        a.setContentText("Message Sent Successfully");
		                        a.show();
		                        prompt.clear();
	                }
	                else {
	                    System.out.println("You have not selected a recipient!"); 
	                }
	            }
	        });
	        
	        back.setOnAction(e->{
	        	window.setScene(studentSc);
	        });
			
		
		GridPane gPane = new GridPane();
		
		
		gPane.add(select,0,0);
		gPane.add(title, 1, 0);
		gPane.add(level, 2, 0);
		gPane.add(prompt, 0, 1);
		gPane.add(back, 0, 2);
		gPane.add(send, 1, 2);
		
		helpSc = new Scene(gPane, 640,480);
		return helpSc;
	}
	
	public Scene listUsersWindow() {
		Label result = new Label("");
				
		ObservableList<String> options = 
			    FXCollections.observableArrayList(
			        "Admin",
			        "Instructor",
			        "Viewing Special Decrypted Bodies"
			    );
		final ComboBox<String> level = new ComboBox<String>(options);
		level.setPromptText("Privilige Groups");
		
		ObservableList<String> role = 
			    FXCollections.observableArrayList(
			        "Admin",
			        "Instructor",
			        "Student"
			    );
		final ComboBox<String> roles = new ComboBox<String>(role);
		roles.setPromptText("Role");
		
		Button searchButton = new Button("Search");
		Button cancel = new Button("Cancel");
		
		
		searchButton.setOnAction(e->{
			String selectedLevel = level.getValue();
			String selectedRole = roles.getValue();
		    if (selectedLevel == null || selectedRole == null) {
		        result.setText("Please select both privilege group and role.");
		    }
		    else {
			String levelChoice = switch (selectedLevel) {
				case "Admin" -> "admin";
				case "Instructor" -> "instructor";
				case "Viewing Special Decrypted Bodies" -> "view";
				default -> null;	
				};
			String roleChoice = switch (selectedRole) {
				case "Admin" -> "admin";
				case "Instructor" -> "instructor";
				case "Student" -> "student";
				default -> null;	
				};
				try {
					databaseHelper.listUsers(levelChoice, roleChoice);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
			}
		    }
			
		});
		
		cancel.setOnAction(e->{
			window.setScene(adminWindow());
		});
		
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		
		gPane.add(result, 0, 1);
		gPane.add(searchButton, 2, 1);
		gPane.add(level, 1, 0);
		gPane.add(roles, 0, 0);
		gPane.add(cancel, 1,1);
		
		searchSc = new Scene(gPane, 640,480);
		return searchSc;
	}
	public Scene searchWindow() {
		TextField search = new TextField("");
		search.setPromptText("Search");
		Label result = new Label("");
				
		ObservableList<String> options = 
			    FXCollections.observableArrayList(
			        "Beginner",
			        "Intermediate",
			        "Advanced",
			        "Expert"
			    );
			final ComboBox<String> level = new ComboBox<String>(options);
			level.setPromptText("Level");
		
		
		Button searchButton = new Button("Search");
		Button cancel = new Button("Cancel");
		
		searchButton.setOnAction(e->{
			result.setText(databaseHelper.getHelpTableTitle(1));
			
		});
		
		cancel.setOnAction(e->{
			window.setScene(studentWindow());
		});
		
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		
		gPane.add(search, 0, 0);
		gPane.add(result, 0, 1);
		gPane.add(searchButton, 2, 1);
		gPane.add(level, 1, 0);
		gPane.add(cancel, 1,1);
		
		searchSc = new Scene(gPane, 640,480);
		return searchSc;
	}
	
	public Scene viewHelpArticleWindow() {
		Button search = new Button("Search");
		Button back = new Button("Back");
		
		TextField id = new TextField();
		id.setPromptText("Enter ID");
		
		
		back.setOnAction(e->{
			window.setScene(studentWindow());
		});
		
		search.setOnAction(e->{
			try {
				window.setScene(displayHelpArticleWindow());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		
		GridPane gPane = new GridPane();
		
		gPane.add(search, 0, 2);
		gPane.add(back, 1,2);
		gPane.add(id, 0, 1);
		
		viewHelpArticleSc = new Scene(gPane, 640, 480);
		return viewHelpArticleSc;
	}
	
	public Scene removeHelpArticleWindow() {
		Button remove = new Button("Remove");
		Button back = new Button("Back");
		
		TextField id = new TextField();
		id.setPromptText("Enter ID");
		
		
		back.setOnAction(e->{
			window.setScene(adminWindow());
		});
		
		remove.setOnAction(e->{
			try {
				databaseHelper.deleteHelpArticle(id.getText());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		
		GridPane gPane = new GridPane();
		
		gPane.add(remove, 0, 2);
		gPane.add(back, 1,2);
		gPane.add(id, 0, 1);
		
		removeHelpArticleSc = new Scene(gPane, 640, 480);
		return removeHelpArticleSc;
		
	}
	
	public Scene displayHelpArticleWindow() {
		TextField title = new TextField();
		title.setEditable(false);
		TextField author = new TextField();
		author.setEditable(false);
		TextArea body = new TextArea();
		body.setEditable(false);
		Button back = new Button("Back");
		
		
		title.setText(databaseHelper.getHelpTableLevel(1));
		author.setText(databaseHelper.getHelpTableType(1));
		body.setText(databaseHelper.getHelpTableBody(1));
		
		back.setOnAction(e->{
			window.setScene(studentWindow());
		});

		
		GridPane gPane = new GridPane();
		
		gPane.add(title, 1, 0);
		gPane.add(author, 2, 1);
		gPane.add(body,1,1);
		gPane.add(back, 2, 2);
		
		displayArticleSc = new Scene(gPane, 640, 480);
		return displayArticleSc;
	}

	
	public Scene displayArticleWindow() throws SQLException {
		TextField title = new TextField();
		title.setEditable(false);
		TextField author = new TextField();
		author.setEditable(false);
		TextArea body = new TextArea();
		body.setEditable(false);
		Button back = new Button("Back");
		
		
		title.setText(databaseHelper.getTableTitle(1));
		author.setText(databaseHelper.getTableAuthor(1));
		body.setText(databaseHelper.getTableBody(1));
		
		back.setOnAction(e->{
			window.setScene(adminWindow());
		});

		
		GridPane gPane = new GridPane();
		
		gPane.add(title, 1, 0);
		gPane.add(author, 2, 1);
		gPane.add(body,1,1);
		gPane.add(back, 2, 2);
		
		displayArticleSc = new Scene(gPane, 640, 480);
		return displayArticleSc;
	}
	
	

public static void main(String[] args) {
	Application.launch(args);
	

	}
}
