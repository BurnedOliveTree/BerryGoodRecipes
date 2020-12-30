package main.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import main.DatabaseConnection;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import main.userModel.Opinion;
import main.userModel.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RecipePane  implements OrdinaryButtonAction{
    private final Recipe recipe;
    private final User activeUser;
    @FXML
    public TextFlow descText;
    public ImageView ScalePic;
    public ImageView ShoppingPic;
    public ImageView LikePic;
    public ImageView TimePic;
    public Label titleLabel;
    public Label costLabel;
    public Label authorLabel;
    public Label dateAddedLabel;
    public Label timePrepLabel;
    public TextArea portionArea;
    public ListView<String> ingredientListView;
    public Button exitButton;
    public Button shoppingListButton;
    public Button likeButton;
    public Button timeButton;
    public Button commentButton;
    public Button scaleButton;

    public RecipePane(Recipe recipe, User activeUser) {
        this.recipe = recipe;
        this.activeUser = activeUser;
    }

    @FXML
    void initialize() throws FileNotFoundException {
        if (DatabaseConnection.theme.equals("lightTheme") || DatabaseConnection.theme.equals("winter")) {
            try {
                ScalePic.setImage(new Image(new FileInputStream("src/resources/berryScale.png")));
                ShoppingPic.setImage(new Image(new FileInputStream("src/resources/berryBasket.png")));
                TimePic.setImage(new Image(new FileInputStream("src/resources/berryStoper.png")));
            } catch (FileNotFoundException e) {
                System.err.printf("Error: %s%n", e.getMessage());
            }
        }
        descText.getChildren().add(new Text(this.recipe.getPrepareMethod()));
        titleLabel.setText(this.recipe.getName());

        if (this.recipe.getCost() == 0) {
            costLabel.setText("Cost: Unknown");
        } else {
            costLabel.setText("Cost: " + this.recipe.getCost());
        }

        if (this.recipe.getPortionNumber() % 1 == 0)
            portionArea.setText(String.valueOf((int)this.recipe.getPortionNumber()));
        else
            portionArea.setText(String.valueOf(this.recipe.getPortionNumber()));

        setIngredListView();
        ingredientListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        String x = "Author: " + this.recipe.getAuthor();

        authorLabel.setText("Author: " + this.recipe.getAuthor());
        dateAddedLabel.setText("Date added: " + this.recipe.getDateAdded());
        if (this.recipe.getPrepareTime() == 0) {
            timePrepLabel.setText("Preparation time: Unknown");
        } else {
            timePrepLabel.setText("Preparation time: " + this.recipe.getPrepareTime());
        }


        // options for logged in users
        if (activeUser == null) {
            likeButton.setDisable(true);
        } else if (activeUser.checkIfRecipeFavorite(this.recipe.getId())){
            LikePic.setImage(new Image(new FileInputStream("src/resources/favoriteClicked.png")));
        }


        portionArea.textProperty().addListener((observableValue, s, t1) -> {
            try {
                Double currNumPortions = Double.parseDouble(t1);
                if (currNumPortions > 0)
                    changeIngredListViewScale(currNumPortions);
                else
                    portionArea.clear();
            } catch (IllegalArgumentException e) {
                portionArea.clear();
                return;
            }
        });

    }

    private void setIngredListView() {
        ingredientListView.getItems().clear();
        for (Ingredient ingredient: this.recipe.getIngredientList()) {
            if (ingredient.getQuantity() % 1 == 0)
                ingredientListView.getItems().add(String.format("%d %s %s", (int)Math.round(ingredient.getQuantity()), ingredient.getUnit().getName(), ingredient.getName()));
            else
                ingredientListView.getItems().add(String.format("%f %s %s", ingredient.getQuantity(), ingredient.getUnit().getName(), ingredient.getName()));
        }

    }

    private void changeIngredListViewScale(Double numPortions) {
        double currNumPortions = this.recipe.getPortionNumber();
        if (!numPortions.equals(currNumPortions)) {
            double scale = numPortions / currNumPortions;
            this.recipe.setPortionNumber(numPortions);
            this.recipe.scaleIngredientList(scale);
            setIngredListView();
        }
    }
    @FXML
    public void onLikeButtonAction() {
        try {
            boolean state = activeUser.checkIfRecipeFavorite(recipe.getId());
            if (activeUser.checkIfRecipeFavorite(recipe.getId())) {
                LikePic.setImage(new Image(new FileInputStream("src/resources/favoriteUnclicked.png")));
                activeUser.removeFavorite(recipe.getId());
            }
            else{
                LikePic.setImage(new Image(new FileInputStream("src/resources/favoriteClicked.png")));
                activeUser.addFavorite(recipe.getId());
            }
        } catch ( FileNotFoundException e) {
            System.err.println(String.format("Error: %s", e.getMessage()));
        }
    }

    @FXML
    public void onCommentButtonAction(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/opinionPage.fxml"));
        OpinionPane controller = new OpinionPane(this.recipe, activeUser);
        loader.setController(controller);
        changeScene(commentButton, loader);
    }
    @FXML
    public void onScaleButtonAction(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/scalePage.fxml"));
        ScalePane controller = new ScalePane(this.recipe, activeUser);
        loader.setController(controller);
        changeScene(scaleButton, loader);
    }
    @FXML
    public void onExitButtonAction(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/mainPage.fxml"));
        MainPane controller = new MainPane(activeUser);
        loader.setController(controller);
        changeScene(exitButton, loader);
    }

    @FXML
    public void onShoppingListButtonAction(){

    }

    @FXML
    public void onTimeButtonAction(){

    }

}