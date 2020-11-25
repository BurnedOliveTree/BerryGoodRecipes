package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.geometry.Insets;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import main.controller.MainPane;

import java.io.IOException;

public class Main extends Application {
    public void FXMLogin(Stage stage) throws IOException {
        Parent loginWindow = FXMLLoader.load(getClass().getResource("../resources/login.fxml"));
        Scene scene = new Scene(loginWindow);
        scene.getStylesheets().add(getClass().getResource("../resources/darkTheme.css").toExternalForm());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
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
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("BerryGood Recipes");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/resources/mainPage.fxml"));
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(getClass().getResource("../resources/darkTheme.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();


        MainPane controller = loader.getController();


        FXMLogin(new Stage());
    }
    public static void main(String[] args) {
        launch(args);
    }
}
