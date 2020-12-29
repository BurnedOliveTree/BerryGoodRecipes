package main.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import main.Core;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RecipePane {
    private Recipe recipe;
    @FXML
    public TextFlow descText;
    public ImageView ScalePic;
    public ImageView ShoppingPic;
    public ImageView LikePic;
    public ImageView TimePic;

    public RecipePane(Recipe recipe) {
        this.recipe = recipe;
    }

    @FXML
    void initialize() {
        if (Core.theme.equals("lightTheme") || Core.theme.equals("winter")) {
            try {
                ScalePic.setImage(new Image(new FileInputStream("src/resources/berryScale.png")));
                ShoppingPic.setImage(new Image(new FileInputStream("src/resources/berryBasket.png")));
                TimePic.setImage(new Image(new FileInputStream("src/resources/berryStoper.png")));
            } catch (FileNotFoundException e) {
                System.err.printf("Error: %s%n", e.getMessage());
            }
        }
        descText.getChildren().add(new Text("ala ma kota"));


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