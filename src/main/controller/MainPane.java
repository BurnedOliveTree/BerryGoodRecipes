package main.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import main.Core;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import main.recipeModel.Unit;
import main.userModel.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainPane {
    @FXML
    public Button recipeLink;
    public Pane recipePane;
    public ImageView logo;

    @FXML
    void initialize() {
        recipeLink.setText("Placki");
        if (Core.theme.equals("lightTheme")) {
            try {
                logo.setImage(new Image(new FileInputStream("src/resources/berryLogo.png")));
            } catch (FileNotFoundException e) {
                System.err.printf("Error: %s%n", e.getMessage());
            }
        }
    }

    @FXML
    public void onClickButton() {
        // change main Stage Scene to recipe Scene
        try {
            FXMLLoader loader =  new FXMLLoader(getClass().getResource("/resources/recipePage.fxml"));
            RecipePane controller = new RecipePane(new Recipe("Placki", new User(3, "Karolina", "1234"), "Zrób farsz i nagrzej patelnie", true, new ArrayList<>(){{add(new Ingredient(200, new Unit(), "Twaróg"));}}));
            loader.setController(controller);
            Parent recipePage = loader.load();
            Scene recipePageScene = new Scene(recipePage);
            Stage stage = (Stage) recipeLink.getScene().getWindow();
            recipePageScene.getStylesheets().add(getClass().getResource("/resources/"+Core.theme+".css").toExternalForm());
            stage.setScene(recipePageScene);
            stage.show();
        } catch (IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }

    // TODO these buttons should only show up when Core.activeUser = null, else they should disappear

    @FXML
    public void onLogInButtonClick(MouseEvent mouseEvent) {
        // create a new Window with log in
        try {
            mouseEvent.consume();
            Scene scene = new Scene(new FXMLLoader(getClass().getResource("/resources/logInWindow.fxml")).load());
            scene.getStylesheets().add(getClass().getResource("/resources/"+Core.theme+".css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }

    @FXML
    public void onRegisterButtonClick(MouseEvent mouseEvent) {
        // create a new Window with log in
        try {
            mouseEvent.consume();
            Scene scene = new Scene(new FXMLLoader(getClass().getResource("/resources/logInWindow.fxml")).load());
            scene.getStylesheets().add(getClass().getResource("/resources/"+Core.theme+".css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Register");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }
}
