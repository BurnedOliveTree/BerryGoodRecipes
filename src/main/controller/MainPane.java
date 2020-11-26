package main.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import main.recipeModel.Unit;
import main.userModel.User;

import java.io.IOException;
import java.util.ArrayList;

public class MainPane {
    @FXML
    public Button recipeLink;
    public Pane recipePane;

    @FXML
    void initialize() {recipeLink.setText("Placki");}
    // Change Scene to RecipeScene
    @FXML
    public void onClickButton(ActionEvent actionEvent){
//        Stage stage = (Stage) recipeLink.getScene().getWindow();
//        stage.close();
        try {
            FXMLLoader loader =  new FXMLLoader(getClass().getResource("/resources/recipePage.fxml"));
            RecipePane controller = new RecipePane(new Recipe("Placki", new User(3, "Karolina", "1234"), "Zrób farsz i nagrzej patelnie", true, new ArrayList<Ingredient>(){{add(new Ingredient(200, new Unit(), "Twaróg"));}}));
            loader.setController(controller);
            Parent recipePage = loader.load();
            Scene recipePageScene = new Scene(recipePage);
            Stage stage = (Stage) recipeLink.getScene().getWindow();
            recipePageScene.getStylesheets().add(getClass().getResource("/resources/darkTheme.css").toExternalForm());
            stage.setScene(recipePageScene);
            stage.show();


        } catch (IOException e) {
            System.err.println(String.format("Error: %s", e.getMessage()));
        }}
}
