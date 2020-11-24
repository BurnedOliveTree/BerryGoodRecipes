package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

public class Main extends Application {
    public void login(Stage loginStage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        final TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getText();
        GridPane.setConstraints(usernameField, 0, 0);
        grid.getChildren().add(usernameField);

        final TextField passwordField = new TextField();
        passwordField.setPromptText("Password");
        GridPane.setConstraints(passwordField, 0, 1);
        grid.getChildren().add(passwordField);

        Button login = new Button("Log in");
        GridPane.setConstraints(login, 0, 2);
        grid.getChildren().add(login);

        loginStage.setTitle("Log in");
        loginStage.setScene(new Scene(grid, 240, 120, Color.rgb(16, 16, 16)));
        loginStage.show();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("BerryGood Recipes");
        Text text1 = new Text(300, 230, "Miłego dnia :D");
        text1.setFill(Color.rgb(240, 240, 240));
        text1.setFont(Font.font(java.awt.Font.SERIF, 20));
        Group root2 = new Group(text1);
        primaryStage.setScene(new Scene(root2, 640, 480, Color.rgb(16, 16, 16)));
        primaryStage.show();

        login(new Stage());
    }
    public static void main(String[] args) {
        launch(args);
    }
}
