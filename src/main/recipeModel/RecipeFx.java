package main.recipeModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import javafx.event.EventHandler;
import java.io.IOException;

public class RecipeFx {
    private Recipe recipe;
    @FXML
    public Button timepieceButton;
    public Button scaleButton;
    public TextField numPortionField;
    public ListView ingredientListView;
    public Label titleLabel;
    public Label timeLabel;
    public Label costLabel;
    public Button shoppingListButton;
    public Button exitButton;
    public Label descLabel;

    public RecipeFx(Recipe recipe) {
        this.recipe = recipe;
    }

    @FXML
    void initialize() {
        descLabel.setText(this.recipe.getPrepareMethod());
        titleLabel.setText(this.recipe.getName());
        timeLabel.setText(String.valueOf(this.recipe.getPrepareTime()));
        costLabel.setText(String.valueOf(this.recipe.getCost()));
        numPortionField.setText(String.valueOf(this.recipe.getPortionNumber()));
        
        EventHandler<ActionEvent> handler = new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Parent mainPage = FXMLLoader.load(getClass().getResource("/resources/mainPage.fxml"));
                    Scene mainPageScene = new Scene(mainPage);
                    Stage stage = (Stage) exitButton.getScene().getWindow();
                    mainPageScene.getStylesheets().add(getClass().getResource("/resources/darkTheme.css").toExternalForm());
                    stage.setScene(mainPageScene);
                    stage.show();
                } catch (IOException e) {
                    System.err.println(String.format("Error: %s", e.getMessage()));
                }
            }
        };
        exitButton.addEventHandler(ActionEvent.ACTION, handler);
    }

}