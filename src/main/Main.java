package main;

import main.controller.MainPane;

import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class Main extends Application {
    public TextField usernameField;
    public TextField passwordField;

    public void loginFXML(Stage stage) throws IOException {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../resources/login.fxml")));
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
