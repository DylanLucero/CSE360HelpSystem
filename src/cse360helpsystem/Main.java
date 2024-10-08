package cse360helpsystem;
//fix

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.h2.engine.User;

public class Main extends Application {
	Stage window;
	

	private static DatabaseHelper databaseHelper = new DatabaseHelper();
    public void setDatabaseHelper(DatabaseHelper databaseHelper) {
        Main.databaseHelper = databaseHelper;
    }	
	public Scene loginSc, studentSc, instructorSc, roleSc, adminSc, setupSc, establishSc, establishAdminSc;
	public 	Alert a = new Alert(AlertType.NONE);

	
	
	
	// Login Page
	@Override
	public void start(Stage loginPage) throws Exception {
		window = loginPage;
		final SimpleBooleanProperty firstSelection = new SimpleBooleanProperty(true);
		
		// Needs if-else that checks if database is empty. If database is empty, we create an admin else we go to the normal login page.
			
			databaseHelper.connectToDatabase();  // Connect to the database
			if(databaseHelper.isDatabaseEmpty()) {
				// System.println("Database is empty, creating admin");
				window.setScene(establishAdminWindow());
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

		    try {
		        boolean loginSuccess = databaseHelper.login(userText.getText(), passText.getText(), "Student"); // Replace "role" with the actual role you want to check
		        boolean loginSuccessAdmin = databaseHelper.adminLogin(userText.getText(), passText.getText(), "Admin");
		        if (loginSuccessAdmin){
		        	System.out.println("Login Success Admin");
		            window.setScene(finishSetupWindow(userText.getText(), passText.getText()));
		        } else {
		        if (loginSuccess) {
		            System.out.println("Login Success Student/Instructor");
		        }
		            if (!databaseHelper.setupComplete(userText.getText())) {
		                System.out.println("Account setup incomplete, moving to setup page.");
		            window.setScene(finishSetupWindow(userText.getText(), passText.getText()));
		        } else
		        {
			        a.setAlertType(AlertType.ERROR);
			        a.setContentText("Login error try again.");
			        a.show();
		        }
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
		Button logout = new Button("Logout");
		
		logout.setOnAction(e->{
			window.setScene(loginSc);
		});
		
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		
		// Adding to Grid
		gPane.add(label, 1,1,1,1);
		gPane.add(logout,1,2,1,1);
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
		Button inviteUser = new Button("Invite User");
		Button resetUser = new Button("Reset User Account");
		Button deleteUser = new Button("Delete User Account");
		Button listUsers = new Button("List Known Users");
		Button addRemoveRoles = new Button("Add/Remove Roles"); // Thinking a checkbox system of some sort
		Button logout = new Button("Logout");
		
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
		
		
		// Add or Remove Roles - Add or remove roles from user accounts
		
		// Pane Mumbo Jumbo
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		
		// Adding to Grid
		gPane.add(label, 1,0,2,1);
		gPane.add(inviteUser,1,2,1,1);
		gPane.add(otp,3,2,2,1);
		gPane.add(resetUser,1,3,2,1);
		gPane.add(deleteUser,1,4,2,1);
		gPane.add(listUsers,1,5,2,1);
		gPane.add(addRemoveRoles,1,6,2,1);
		gPane.add(logout,1,7,2,1);
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
	
	

public static void main(String[] args) {
	Application.launch(args);
	

	}
private static void adminFlow() throws SQLException {

}
}
