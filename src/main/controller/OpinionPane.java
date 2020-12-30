package main.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.stage.Stage;
import main.DatabaseConnection;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import main.recipeModel.Unit;
import main.userModel.Opinion;
import main.userModel.User;

import java.io.IOException;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


public class OpinionPane {
    private Opinion opinion;
    ObservableList<String> scoreList = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    private Recipe recipe;
    @FXML
    public Button okButton;
    public Button exitButton;
    public Label scoreLabel;
    public TextField commentTextField;
    @FXML
    public ChoiceBox scoreBox;

    public OpinionPane(Recipe recipe) {
        this.recipe = recipe;
    };

    @FXML
    private void initialize(){
        scoreBox.setItems(scoreList);
        exitButton.setOnAction( e->{ onAction(exitButton, "/resources/mainPage.fxml"); });

    }
    private void  onAction(Button button, String namePath) {
        try {
            FXMLLoader loader =  new FXMLLoader(getClass().getResource("/resources/recipePage.fxml"));

//            RecipePane controller = new RecipePane(new Recipe(1,"Placki", new User("Karolina", "1234"), "Zrób farsz i nagrzej patelnie", 0, "2020-01-01", 10, 20, 4,  new ArrayList<>(){{add(new Ingredient(200, new Unit(), "Twaróg"));}}));
            RecipePane controller = new RecipePane(this.recipe);
            loader.setController(controller);
            Parent recipePage = loader.load();
            Scene recipePageScene = new Scene(recipePage);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            recipePageScene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
            stage.setScene(recipePageScene);
            stage.show();
        } catch (IOException e) {
            System.err.println(String.format("Error: %s", e.getMessage()));}
    }


}
