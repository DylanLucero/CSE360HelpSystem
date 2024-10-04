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
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Scanner;

public class Main extends Application {
	Stage window;

	private static final Scanner scnr = new Scanner(System.in);
	
	public Scene loginSc, studentSc, instructorSc, roleSc, adminSc, setupSc, establishSc;
	public 	Alert a = new Alert(AlertType.NONE);

	
	
	
	// Login Page
	@Override
	public void start(Stage loginPage) throws Exception {
		window = loginPage;
		final SimpleBooleanProperty firstSelection = new SimpleBooleanProperty(true);
		
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
			if(userText.getText() == "" || passText.getText() == "") {
				a.setAlertType(AlertType.WARNING);
				a.setContentText("Fields left empty.");
				a.show();
				userText.clear();
				passText.clear();
				return;
			}
			
			
			System.out.println("Username: " + userText.getText());
			System.out.println("Password: " + passText.getText());
			
	
			userText.clear();
			passText.clear();
			//System.out.println("Login Success");
			window.setScene(finishSetupWindow());
		
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
	
	
	/* Role Selection Window 
	 * ---------------------------------------------
	 * This needs to trigger ONLY if the account has
	 * more than one role, otherwise go straight
	 * to the accounts role screen
	 * We'll add the logic for verification
	*/
	public Scene roleWindow(){
		Label title = new Label("Role Selection");
		MenuButton role = new MenuButton("Click to Select Role");
		MenuItem sButton = new MenuItem("Student");
		MenuItem iButton = new MenuItem("Instructor");
		MenuItem aButton = new MenuItem("Admin");
		
		// Button Actions for Menu
		sButton.setOnAction(e->{ 
			window.setScene(studentWindow());
			//System.out.println("Student View");
			});
		
		aButton.setOnAction(e->{ 
			window.setScene(adminWindow());
			//System.out.println("Admin View");
		});
		
		iButton.setOnAction(e->{ 
			window.setScene(instructorWindow());
			//System.out.println("Instructor View");
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
		TextField user = new TextField();
		user.setPromptText("Username");
		TextField pass = new TextField();
		pass.setPromptText("Password");
		
		
		create.setOnAction(e->{
			window.setScene(loginSc);
		});
		
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		
		
		// Adding to Grid
		gPane.add(label, 2,0,1,1);
		gPane.add(user, 2,1,1,1);
		gPane.add(pass, 2,2,1,1);
		gPane.add(create,2,3,1,1);
		gPane.setVgap(10);

		
		establishSc = new Scene(gPane, 640, 480);
		return establishSc;
	}
	
	

	
	/* Finish Setting Up Account Window
	 * ---------------------------------------
	 * 
	 * 
	 * */
	public Scene finishSetupWindow(){
		final SimpleBooleanProperty firstSelection = new SimpleBooleanProperty(true);
		Label label = new Label("Finish Setting Up Your Account");
		Button advance = new Button("Continue");
		TextField email = new TextField();
		email.setPromptText("Email Address");
		TextField firstName = new TextField();
		firstName.setPromptText("First Name");
		TextField middleName = new TextField();
		middleName.setPromptText("Middle Name");
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
			
			System.out.println("First Name: " + firstName.getText());
			System.out.println("Middle Name: " + middleName.getText());
			System.out.println("Last Name: "+ lastName.getText());
			System.out.println("Prefered Name: " + prefName.getText());

			// If role = student, redirect to student
			// If role = admin, redirect to admin
			// If role = instructor, redirect to instructor
			// If multiple roles, redirect to role selection
			
			window.setScene(roleWindow());
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
}
