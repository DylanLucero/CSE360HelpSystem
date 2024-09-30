package cse360helpsystem;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
	Stage window;
	public Scene loginSc, studentSc, instructorSc, 
				roleSc, adminSc;
	
	@Override
	public void start(Stage loginPage) throws Exception {
		window = loginPage;
		loginPage.setTitle("CSE360 Help System");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		
		// Labels
		Label label = new Label("Login Page");
		Label userName = new Label("Username: ");
		Label userPass = new Label("Password: ");
		
		// Text Fields
		TextField userText = new TextField();
		TextField passText = new TextField();
		
		// Buttons
		Button login = new Button("Login");
		login.setOnAction(e->{
			System.out.println("login success");
			window.setScene(roleWindow());
		
		});

		
		// Adding to VBox
		grid.add(label,2,0,1,1);
		grid.add(userName,1,1,1,1);
		grid.add(userPass,1,2,1,1);
		grid.add(userText,2,1,1,1);
		grid.add(passText,2,2,1,1);
		grid.add(login, 2,3,1,1);

		
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
		
		
		
		sButton.setOnAction(e->{
			window.setScene(studentWindow());
		});
		
		aButton.setOnAction(e->{
			window.setScene(adminWindow());
		});
		iButton.setOnAction(e->{
			window.setScene(instructorWindow());
		});
		
		role.getItems().addAll(sButton, iButton, aButton);
		StackPane sPane = new StackPane();
		sPane.getChildren().add(role);
		studentSc = new Scene(sPane, 640, 480);
		return studentSc;
	}

	/* Student Window
	 * ----------------------------------------
	 * Phase 1 requires 
	 * - A logout button √
	*/
	public Scene studentWindow(){
		Label label = new Label("Student Page");
		Button logout = new Button("Logout");
		
		
		logout.setAlignment(Pos.TOP_LEFT);
		logout.setOnAction(e->{
			window.setScene(loginSc);
		});
		
		
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		gPane.add(label, 1,1,1,1);
		gPane.add(logout,1,2,1,1);
		studentSc = new Scene(gPane, 640, 480);
		return studentSc;
	}
	
	
	/* Instructor Window
	 * --------------------------------------
	 * Phase 1 requires
	 * - A logout button √
	*/
	public Scene instructorWindow(){
		Label label = new Label("Instructor Page");
		Button logout = new Button("Logout");
		
		
		logout.setAlignment(Pos.TOP_LEFT);
		logout.setOnAction(e->{
			window.setScene(loginSc);
		});
		
		
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		gPane.add(label, 1,1,1,1);
		gPane.add(logout,1,2,1,1);
		instructorSc = new Scene(gPane, 640, 480);
		return instructorSc;
	}
	
	
	/* Admin Window 
	 * --------------------------------------
	 * Phase 1 requires
	 * - Invite to join, generate an OTP
	 * - Reset User Account
	 * - Delete User Account
	 * - List known Accounts
	 * - Add or Remove Roles
//	 * - Logout √ 
	*/
	public Scene adminWindow(){
		Label label = new Label("Admin Page");
		Button logout = new Button("Logout");
		
		
		logout.setAlignment(Pos.TOP_LEFT);
		logout.setOnAction(e->{
			window.setScene(loginSc);
		});
		
		
		GridPane gPane = new GridPane();
		gPane.setAlignment(Pos.CENTER);
		gPane.add(label, 1,1,1,1);
		gPane.add(logout,1,2,1,1);
		
		adminSc = new Scene(gPane, 640, 480);
		return adminSc;
	}

public static void main(String[] args) {
		Application.launch(args);
	}
}
