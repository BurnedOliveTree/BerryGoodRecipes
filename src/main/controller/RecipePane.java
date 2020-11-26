package main.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;

import java.io.IOException;

public class RecipePane {
    private Recipe recipe;
    @FXML
    public TextField numPortionField;
    public ListView<String> ingredientListView;
    public Label titleLabel;
    public Label timeLabel;
    public Label costLabel;
    public Label descLabel;
    public Button shoppingListButton;
    public Button exitButton;
    public Button timepieceButton;
    public Button scaleButton;
    public Button favoriteButton;
    public Button commentButton;

    public RecipePane(Recipe recipe) {
        this.recipe = recipe;
    }

    @FXML
    void initialize() {
        descLabel.setText(this.recipe.getPrepareMethod());
        titleLabel.setText(this.recipe.getName());
        timeLabel.setText(String.valueOf(this.recipe.getPrepareTime()));
        costLabel.setText(String.valueOf(this.recipe.getCost()));
        numPortionField.setText(String.valueOf(this.recipe.getPortionNumber()));
//        exitButton.setOnAction( new EventHandler<>() { @Override public void handle(ActionEvent event) { onAction(exitButton, "/resources/mainPage.fxml"); }});
        exitButton.setOnAction( e->{ onAction(exitButton, "/resources/mainPage.fxml"); });
        scaleButton.setOnAction( e->{ onAction(scaleButton, "/resources/scalePage.fxml"); });
        shoppingListButton.setOnAction( e->{ onAction(shoppingListButton, "/resources/shoppingListPage.fxml"); });
        timepieceButton.setOnAction( e->{ onAction(timepieceButton, "/resources/timepiecePage.fxml"); });
        favoriteButton.setOnAction( e->{ onAction(favoriteButton, "/resources/favoritePage.fxml"); });
        commentButton.setOnAction( e->{ onAction(favoriteButton, "/resources/opinionPage.fxml"); });
        for (Ingredient ingredient: this.recipe.getIngredientList()) {
        ingredientListView.getItems().add(String.format("%d %s %s", ingredient.getQuantity(), ingredient.getUnit(), ingredient.getName()));
        ingredientListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
    }

    private void  onAction(Button button, String namePath) {
        try {
            Parent mainPage = FXMLLoader.load(getClass().getResource(namePath));
            Scene mainPageScene = new Scene(mainPage);
            Stage stage = (Stage) button.getScene().getWindow();
            mainPageScene.getStylesheets().add(getClass().getResource("/resources/darkTheme.css").toExternalForm());
            stage.setScene(mainPageScene);
            stage.show();
        } catch (IOException e) {
            System.err.println(String.format("Error: %s", e.getMessage()));}
    }

}