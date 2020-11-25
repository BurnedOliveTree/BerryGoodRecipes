package main;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
        Text text1 = new Text(300, 230, "Miłego dnia :D");
        text1.setFill(Color.rgb(240, 240, 240));
        text1.setFont(Font.font(java.awt.Font.SERIF, 20));
        Group root2 = new Group(text1);
        primaryStage.setScene(new Scene(root2, 640, 480, Color.web("#111")));
        primaryStage.show();

        loginFXML(new Stage());
    }
    public static void main(String[] args) {
        launch(args);
    }
}
