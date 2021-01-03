package main.controller;

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
import javafx.scene.text.*;

import main.DatabaseConnection;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import main.userModel.User;

public class RecipePane  extends OrdinaryButtonAction{
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
    public Spinner<Integer> portionArea;
    public ListView ingredientListView;
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
        if (DatabaseConnection.theme.equals("light") || DatabaseConnection.theme.equals("winter")) {
            ScalePic.setImage(new Image("icons/berryScale.png"));
            ShoppingPic.setImage(new Image("icons/berryBasket.png"));
            TimePic.setImage(new Image("icons/berryStoper.png"));
        }
        Text text = new Text(this.recipe.getPrepareMethod());
        text.setFont(Font.font("System", FontPosture.REGULAR, 13));
        descText.getChildren().add(text);

        titleLabel.setText(this.recipe.getName());
        titleLabel.setWrapText(true);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        if (this.recipe.getCost() == 0) {
            costLabel.setText("Cost: Unknown");
        } else {
            costLabel.setText("Cost: " + this.recipe.getCost());
        }
        setIngredListView();
        ingredientListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
            shoppingListButton.setDisable(true);
        } else if (activeUser.checkIfRecipeFavorite(this.recipe)){
            LikePic.setImage(new Image("icons/favoriteClicked.png"));
        }
        //@TODO rozmiar listview

        setPortionAreaProperty();

    }

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
            box.setHgrow(pane, Priority.ALWAYS);
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
                if (ingredient.getQuantity() % 1 == 0)
                    label.setText(String.format(" %d %s %s", (int)Math.round(ingredient.getQuantity()), ingredient.getUnit().getName(), ingredient.getName()));
                else
                    label.setText(String.format(" %f %s %s", ingredient.getQuantity(), ingredient.getUnit().getName(), ingredient.getName()));

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

    private void setIngredListView() {
        ingredientListView.getItems().clear();
        if (activeUser != null) {
            ingredientListView.getItems().addAll(this.recipe.getIngredientList());
            ingredientListView.setCellFactory(ingredientListView -> new ButtonCell(activeUser));
        } else {
            for (Ingredient ingredient: this.recipe.getIngredientList()) {
                if (ingredient.getQuantity() % 1 == 0)
                    ingredientListView.getItems().add(String.format("%d %s %s", (int)Math.round(ingredient.getQuantity()), ingredient.getUnit().getName(), ingredient.getName()));
                else
                    ingredientListView.getItems().add(String.format("%1.2f %s %s", ingredient.getQuantity(), ingredient.getUnit().getName(), ingredient.getName()));
            }

        }

    }

    void setPortionAreaProperty(){
        portionArea.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000));
        portionArea.getEditor().textProperty().set(String.format((recipe.getPortionNumber() % 1 == 0)?"%1.0f":"%1.2f", recipe.getPortionNumber()));
        portionArea.setEditable(true);
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

    void handleSpin(ObservableValue<?> observableValue, Number oldValue, Number currNumPortions) {
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
    public void onLikeButtonAction() {
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
        exitButton.getScene().getWindow().hide();
    }

    @FXML
    public void onShoppingListButtonAction(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/shoppingListPage.fxml"));
        RecipePane returnPane = new RecipePane(recipe, activeUser);
        ShoppingListPane controller = new ShoppingListPane(activeUser, returnPane);
        loader.setController(controller);
        changeScene(shoppingListButton, loader);
    }

    @FXML
    public void onTimeButtonAction(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/timerPage.fxml"));
        TimerPane controller = new TimerPane();
        loader.setController(controller);
        changeScene(timeButton, loader, true);
    }

}