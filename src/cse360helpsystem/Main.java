package cse360helpsystem;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage arg0) throws Exception {
		arg0.setTitle("CSE360 Help System");
		GridPane grid = new GridPane();
		
		// Labels
		Label label = new Label("Login Page");
		Label userName = new Label("Username: ");
		Label userPass = new Label("Password: ");
		
		// Text Fields
		TextField userText = new TextField();

		
		TextField passText = new TextField();
		
		// Buttons
		Button login = new Button("Login");
		
		
		// Adding to VBox
		grid.add(label,2,0,1,1);
		grid.add(userName,1,1,1,1);
		grid.add(userPass,1,2,1,1);
		grid.add(userText,2,1,1,1);
		grid.add(passText,2,2,1,1);
		grid.add(login, 2,3,1,1);

		
		Scene scene = new Scene(grid, 640, 480);
		arg0.setScene(scene);
		
		arg0.show();
	}


public static void main(String[] args) {
		Application.launch(args);
	}
}
