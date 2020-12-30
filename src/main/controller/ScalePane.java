package main.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import main.DatabaseConnection;
import main.recipeModel.Recipe;

import java.io.IOException;

public class ScalePane {
    private Recipe recipe;
    public ScalePane(Recipe recipe) {
        this.recipe = recipe;
    };

    @FXML
    public Button exitButton;
    @FXML
    private void initialize(){
        exitButton.setOnAction( e->{ onAction(exitButton, "/resources/recipePage.fxml"); });
    }

    private void  onAction(Button button, String namePath) {
        try {
            FXMLLoader loader =  new FXMLLoader(getClass().getResource(namePath));
            RecipePane controller = new RecipePane(this.recipe);
            loader.setController(controller);
            Parent recipePage = loader.load();
            Scene recipePageScene = new Scene(recipePage);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            recipePageScene.getStylesheets().add(getClass().getResource("/resources/"+ DatabaseConnection.theme+".css").toExternalForm());
            stage.setScene(recipePageScene);
            stage.show();
        } catch (IOException e) {
            System.err.println(String.format("Error: %s", e.getMessage()));}
}}