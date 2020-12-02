package main.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.stage.Stage;
import main.Core;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import main.recipeModel.Unit;
import main.userModel.Opinion;
import main.userModel.User;

import java.io.IOException;
import java.util.ArrayList;

public class OpinionPane {
    private Opinion opinion;
    ObservableList<String> scoreList = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

    @FXML
    public Button okButton;
    public Button exitButton;
    public Label scoreLabel;
    public TextField commentTextField;
    @FXML
    public ChoiceBox scoreBox;

    public OpinionPane(){};

    @FXML
    private void initialize(){
        scoreBox.setItems(scoreList);
        exitButton.setOnAction( e->{ onAction(exitButton, "/resources/mainPage.fxml"); });

    }
    private void  onAction(Button button, String namePath) {
        try {
            Parent mainPage = FXMLLoader.load(getClass().getResource(namePath));
            Scene mainPageScene = new Scene(mainPage);
            Stage stage = (Stage) button.getScene().getWindow();
            mainPageScene.getStylesheets().add(getClass().getResource("/resources/"+Core.theme+".css").toExternalForm());
            stage.setScene(mainPageScene);
            stage.show();
        } catch (IOException e) {
            System.err.println(String.format("Error: %s", e.getMessage()));}
    }


}
