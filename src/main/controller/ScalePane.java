package main.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import main.DatabaseConnection;
import main.recipeModel.Recipe;
import main.userModel.User;

import java.io.IOException;

public class ScalePane {
    private final Recipe recipe;
    private final User activeUser;
    ObservableList<String> shapeList = FXCollections.observableArrayList("Round", "Rectangular");
    public ScalePane(Recipe recipe, User activeUser) {
        this.recipe = recipe;
        this.activeUser = activeUser;
    };

    @FXML
    public Button exitButton;
    public ChoiceBox IHaveBox;
    public ChoiceBox inRecipeBox;
    @FXML
    private void initialize(){
        exitButton.setOnAction( e->{ onAction(exitButton, "/resources/recipePage.fxml"); });
        IHaveBox.setItems(shapeList);
        inRecipeBox.setItems(shapeList);
    }

    private void  onAction(Button button, String namePath) {
        try {
            FXMLLoader loader =  new FXMLLoader(getClass().getResource(namePath));
            RecipePane controller = new RecipePane(this.recipe, activeUser);
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