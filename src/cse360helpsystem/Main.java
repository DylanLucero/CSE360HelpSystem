package cse360helpsystem;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage arg0) throws Exception {
		arg0.setTitle("CSE360 Help System");
		VBox vbox = new VBox();
		
		// Labels
		Label label = new Label("Login Page");
		
		// Text Fields
		TextField userText = new TextField("Username");
		TextField passText = new TextField("Password");

		
		// Buttons
		Button login = new Button("Login");
		

		

		// Adding to VBox
		vbox.getChildren().add(label);
		vbox.getChildren().add(userText);
		vbox.getChildren().add(passText);
		vbox.getChildren().add(login);

		
		Scene scene = new Scene(vbox, 640, 480);
		arg0.setScene(scene);
		
		arg0.show();
	}


public static void main(String[] args) {
		Application.launch(args);
	}
}