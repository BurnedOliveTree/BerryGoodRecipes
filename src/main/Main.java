package main;

import javafx.application.Application;
import javafx.fxml.FXML;
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

import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent;
import java.io.IOException;

public class Main extends Application {
    public TextField usernameField;
    public TextField passwordField;

    public void loginFXML(Stage stage) throws IOException {
        Parent loginWindow = FXMLLoader.load(getClass().getResource("../resources/login.fxml"));
        Scene scene = new Scene(loginWindow);
        scene.getStylesheets().add(getClass().getResource("../resources/darkTheme.css").toExternalForm());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void getData(MouseEvent event) {
        event.consume();
        System.out.println("Hello "+usernameField.getText()+", your password is "+passwordField.getText());
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
        loginFXML(new Stage());
    }
    public static void main(String[] args) {
        launch(args);
    }
}
