package main.controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;

import main.DatabaseConnection;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import main.userModel.User;

import java.io.IOException;

public class RecipePane  extends OrdinaryButtonAction{
    private final Recipe recipe;
    private final User activeUser;
    @FXML private TextFlow descText;
    @FXML private ImageView ExitPic;
    @FXML private ImageView ScalePic;
    @FXML private ImageView ShoppingPic;
    @FXML private ImageView LikePic;
    @FXML private ImageView TimePic;
    @FXML private Label titleLabel;
    @FXML private Label costLabel;
    @FXML private Label authorLabel;
    @FXML private Label dateAddedLabel;
    @FXML private Label timePrepLabel;
    @FXML private Spinner<Integer> portionArea;
    @FXML private Pane ingredientPane;
    @FXML private ListView ingredientListView;
    @FXML private Button exitButton;
    @FXML private Button shoppingListButton;
    @FXML private Button likeButton;
    @FXML private Button timeButton;
    @FXML private Button commentButton;
    @FXML private Button scaleButton;
    @FXML private VBox propertyBox;

    public RecipePane(Recipe recipe, User activeUser) {
        this.recipe = recipe;
        this.activeUser = activeUser;
    }

    @FXML
    void initialize() {
        if (DatabaseConnection.theme.equals("light") || DatabaseConnection.theme.equals("winter")) {
            ScalePic.setImage(new Image("icons/berryScale.png"));
            ShoppingPic.setImage(new Image("icons/berryBasket.png"));
            TimePic.setImage(new Image("icons/berryStoper.png"));
            ExitPic.setImage(new Image("icons/berryExit.png"));
        }

        Text text = new Text(this.recipe.getPrepareMethod());
        text.setFont(Font.font("System", FontPosture.REGULAR, 13));
        descText.getChildren().add(text);
        ingredientListView = new ListView();
        setIngredListView();
        ingredientPane.getChildren().add(ingredientListView);
        ingredientListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        titleLabel.setText(this.recipe.getName());
        titleLabel.setWrapText(true);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        authorLabel.setText("Author: " + this.recipe.getAuthor());
        dateAddedLabel.setText("Date added: " + this.recipe.getDateAdded());
        setPortionAreaProperty();

        if (this.recipe.getCost() == 0) {
            costLabel.setText("Cost: Unknown");
        } else {
            costLabel.setText("Cost: " + this.recipe.getCost());
        }

        if (this.recipe.getPrepareTime() == 0) {
            timePrepLabel.setText("Preparation time: Unknown");
        } else {
            timePrepLabel.setText("Preparation time: " + this.recipe.getPrepareTime());
        }

        // options for logged in users
        if (activeUser == null) {
            likeButton.setDisable(true);
            shoppingListButton.setDisable(true);
            ingredientListView.setMaxHeight(this.recipe.getIngredientList().size() * 26);
        } else {
            ingredientListView.setMaxHeight(this.recipe.getIngredientList().size() * 29);
            if (activeUser.checkIfRecipeFavorite(this.recipe)) {
                LikePic.setImage(new Image("icons/favoriteClicked.png"));
            }
        }

        Platform.runLater(() -> {
            commentButton.setPrefWidth(propertyBox.getWidth());
        });

    }
    // inner class which extends ListCell with additional button - for adding ingredient to shopping list - option for logged users
    static class ButtonCell extends ListCell<Ingredient> {
        HBox box = new HBox();
        Pane pane = new Pane();
        Label label = new Label("(empty)");
        Ingredient selectedIngredient;
        User activeUser;
        private ImageView view;

        public ButtonCell(User activeUser) {
            super();
            view = new ImageView(new Image("icons/plus.png"));
            view.setFitHeight(20);
            view.setFitWidth(20);
            box.getChildren().addAll(view, label, pane);
            HBox.setHgrow(pane, Priority.ALWAYS);
            this.activeUser = activeUser;
            view.setOnMouseClicked(mouseEvent -> {
                if (!activeUser.checkIfIngredientInShoppingList(selectedIngredient.getId())) {
                    view.setImage(new Image("icons/minus.png"));
                    activeUser.addToShoppingList(selectedIngredient);
                } else {
                    view.setImage(new Image("icons/plus.png"));
                    activeUser.removeFromShoppingList(selectedIngredient.getId());
                }
            });
        }

        @Override
        protected void updateItem(Ingredient ingredient, boolean empty) {
            super.updateItem(ingredient, empty);
            if (empty) {
                selectedIngredient = null;
                setGraphic(null);
            } else {
                selectedIngredient = ingredient;
                label.setText(String.format((ingredient.getQuantity() % 1 == 0)?" %1.0f %s %s":" %1.2f %s %s",  ingredient.getQuantity(), ingredient.getUnit().getName(), ingredient.getName()));
                if (!activeUser.checkIfIngredientInShoppingList(selectedIngredient.getId())) {
                    Image image = new Image("icons/plus.png");
                    view.setImage(image);
                }
                else{
                    Image image = new Image("icons/minus.png");
                    view.setImage(image);
                }

                setGraphic(box);
            }
        }
    }

    // format properly listView - with button or without
    private void setIngredListView() {
        ingredientListView.getItems().clear();
        if (activeUser != null) {
            ingredientListView.getItems().addAll(this.recipe.getIngredientList());
            ingredientListView.setCellFactory(ingredientListView -> new ButtonCell(activeUser));
        } else {
            for (Ingredient ingredient: this.recipe.getIngredientList()) {
                ingredientListView.getItems().add(String.format((ingredient.getQuantity() % 1 == 0)?"%1.0f %s %s":"%1.2f %s %s", ingredient.getQuantity(), ingredient.getUnit().getName(), ingredient.getName()));
            }
        }

    }

    void setPortionAreaProperty(){
        portionArea.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000));
        portionArea.getEditor().textProperty().set(String.format((recipe.getPortionNumber() % 1 == 0)?"%1.0f":"%1.2f", recipe.getPortionNumber()));
        portionArea.setEditable(true);
        // change portions using input value from keyboard
        portionArea.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    double currNumPortions = Double.parseDouble(portionArea.getEditor().textProperty().get());
                    if (currNumPortions > 0){
                        changeIngredListViewScale(currNumPortions);
                        recipe.setPortionNumber(currNumPortions);
                    }
                    else
                        portionArea.getEditor().textProperty().set(String.format((recipe.getPortionNumber() % 1 == 0)?"%1.0f":"%1.2f", recipe.getPortionNumber()));
                } catch (NumberFormatException e) {
                    portionArea.getEditor().textProperty().set(String.format((recipe.getPortionNumber() % 1 == 0)?"%1.0f":"%1.2f", recipe.getPortionNumber()));
                }
            }
        });

        portionArea.valueProperty().addListener(this::handleSpin);
    }

    // for Listener in portionArea, change portions using button
    private void handleSpin(ObservableValue<?> observableValue, Number oldValue, Number currNumPortions) {
        try {
            if (currNumPortions.intValue() > 0)
                changeIngredListViewScale((double)currNumPortions.intValue());
            else
                portionArea.getEditor().textProperty().set("1");
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
    private void onLikeButtonAction() {
        if (activeUser.checkIfRecipeFavorite(recipe)) {
            LikePic.setImage(new Image("icons/favoriteUnclicked.png"));
            activeUser.removeFavorite(recipe);
        }
        else{
            LikePic.setImage(new Image("icons/favoriteClicked.png"));
            activeUser.addFavorite(recipe);
        }
    }

    @FXML
    private void onCommentButtonAction() {
        FXMLLoader loader = loadFXML(new OpinionPane(this.recipe, activeUser), "/resources/opinionPage.fxml");
        changeScene(commentButton, loader);
    }
    @FXML
    private void onScaleButtonAction() {
        FXMLLoader loader = loadFXML(new ScalePane(this.recipe, activeUser), "/resources/scalePage.fxml");
        changeScene(scaleButton, loader);
    }
    @FXML //@TODO always on another window?
    private void onExitButtonAction(){
        exitButton.getScene().getWindow().hide();
    }

    @FXML
    private void onShoppingListButtonAction(){
        FXMLLoader loader = loadFXML(new ShoppingListPane(activeUser, new RecipePane(recipe, activeUser)), "/resources/shoppingListPage.fxml");
        changeScene(shoppingListButton, loader);
    }

    @FXML
    private void onTimeButtonAction() throws IOException {
        FXMLLoader loader = loadFXML(new TimerPane(), "/resources/timerPage.fxml");
        changeScene(loader);
    }

}