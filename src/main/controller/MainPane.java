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

import main.DatabaseConnection;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import main.recipeModel.Unit;
import main.userModel.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainPane {
    @FXML
    public Button recipeLink;
    public Pane recipePane;
    public ImageView logo;

    @FXML
    void initialize() {
        recipeLink.setText("Placki");
        if (DatabaseConnection.theme.equals("lightTheme") || DatabaseConnection.theme.equals("winter")) {
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

            DatabaseConnection connection = new DatabaseConnection();
            Recipe recipe = connection.getRecipe(1);

//            RecipePane controller = new RecipePane(new Recipe(1,"Placki", new User("Karolina", "1234"), "Zrób farsz i nagrzej patelnie", 0, "2020-01-01", 10, 20, 4,  new ArrayList<>(){{add(new Ingredient(200, new Unit(), "Twaróg"));}}));
            RecipePane controller = new RecipePane(recipe);
            loader.setController(controller);
            Parent recipePage = loader.load();
            Scene recipePageScene = new Scene(recipePage);
            Stage stage = (Stage) recipeLink.getScene().getWindow();
            recipePageScene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
            stage.setScene(recipePageScene);
            stage.show();
        } catch (IOException | SQLException e) {
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
            scene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
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
            scene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Register");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }
}
