package main.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import main.DatabaseConnection;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import main.userModel.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RecipePane {
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
    void initialize() {
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
        exitButton.setOnAction( e->{ onAction(exitButton, "/resources/mainPage.fxml"); });
        scaleButton.setOnAction(e->{onAction(scaleButton, "/resources/scalePage.fxml");});
        shoppingListButton.setOnAction( e->{ onAction(shoppingListButton, "/resources/shoppingListPage.fxml"); });
        timeButton.setOnAction( e->{ onAction(timeButton, "/resources/timepiecePage.fxml"); });
        likeButton.setOnAction( e->{ onAction(likeButton, null); });
        commentButton.setOnAction( e->{ onAction(commentButton, "/resources/opinionPage.fxml"); });
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

    private void onAction(Button button, String namePath) {
        if (button.getId().equals("likeButton"))
            try {
                LikePic.setImage(new Image(new FileInputStream("src/resources/favoriteClicked.png")));
            } catch ( FileNotFoundException e) {
                System.err.println(String.format("Error: %s", e.getMessage()));
            }
        else if (button.getId().equals("commentButton")){
            try {
                FXMLLoader loader =  new FXMLLoader(getClass().getResource(namePath));
                OpinionPane controller = new OpinionPane(this.recipe, activeUser);
                loader.setController(controller);
                Parent mainPage = loader.load();
                Scene mainPageScene = new Scene(mainPage);
                Stage stage = (Stage) button.getScene().getWindow();
                mainPageScene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
                stage.setScene(mainPageScene);
                stage.show();
            } catch (IOException e) {
                System.err.println(String.format("Error: %s", e.getMessage()));}

        }
        else if (button.getId().equals("scaleButton")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(namePath));
                ScalePane controller = new ScalePane(this.recipe, activeUser);
                loader.setController(controller);
                Parent mainPage = loader.load();
                Scene mainPageScene = new Scene(mainPage);
                Stage stage = (Stage) button.getScene().getWindow();
                mainPageScene.getStylesheets().add(getClass().getResource("/resources/" + DatabaseConnection.theme + ".css").toExternalForm());
                stage.setScene(mainPageScene);
                stage.show();
            } catch (IOException e) {
                System.err.println(String.format("Error: %s", e.getMessage()));
            }
        }
        else if (button.getId().equals("exitButton")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(namePath));
                MainPane controller = new MainPane(activeUser);
                loader.setController(controller);
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) button.getScene().getWindow();
                scene.getStylesheets().add(getClass().getResource("/resources/" + DatabaseConnection.theme + ".css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                System.err.println(String.format("Error: %s", e.getMessage()));
            }
        }
        else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(namePath));
                Scene mainPageScene = new Scene(loader.load());
                Stage stage = (Stage) button.getScene().getWindow();
                mainPageScene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
                stage.setScene(mainPageScene);
                stage.show();
            } catch (IOException e) {
                System.err.println(String.format("Error: %s", e.getMessage()));}
        }

    }

}