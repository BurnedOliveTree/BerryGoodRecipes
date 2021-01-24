package main.controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;

public class RecipePane  extends BasicPaneActions {
    private final Recipe recipe;
    private final User activeUser;
    private final BasicPaneActions returnPane;

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
    @FXML private ToggleButton deleteButton;
    @FXML private ToggleButton saveButton;
    @FXML private HBox saveDeleteBox;

    public RecipePane(Recipe recipe, User activeUser, BasicPaneActions returnPane) {
        this.recipe = recipe;
        this.activeUser = activeUser;
        this.returnPane = returnPane;
    }

    @FXML
    void initialize() throws IOException, SQLException {
        if (DatabaseConnection.isThemeLight()) {
            ScalePic.setImage(new Image("icons/berryScale.png"));
            ShoppingPic.setImage(new Image("icons/berryBasket.png"));
            TimePic.setImage(new Image("icons/berryStoper.png"));
            ExitPic.setImage(new Image("icons/berryExit.png"));
        }

        Text text = new Text(this.recipe.getPrepareMethod());
        text.setFont(Font.font("System", FontPosture.REGULAR, 13));
        descText.getChildren().add(text);
        ingredientListView = new ListView();
        setIngredListView(this.recipe.getIngredientList(), Boolean.FALSE);
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
        Files.createDirectories(Paths.get("./savedRecipes/"));
        if(Files.exists(Paths.get(getRecipeFileDirectory()))) {
            setSavedRecipe();
        } else {
            setSaveRecipe();
        }

        // options for logged in users
        if (activeUser == null) {
            likeButton.setDisable(true);
            shoppingListButton.setDisable(true);
            ingredientListView.setPrefHeight(this.recipe.getIngredientList().size() * 26);
        } else {
            ingredientListView.setPrefHeight(this.recipe.getIngredientList().size() * 29);
            if (activeUser.checkIfRecipeFavorite(this.recipe)) {
                LikePic.setImage(new Image("icons/favoriteClicked.png"));
            }
            setContextMenu(ingredientListView, createDeleteFromShoppingListItem(), createAddToShoppingListItem(), createChangeUnit());
        }

        Platform.runLater(() -> {
            commentButton.setPrefWidth(propertyBox.getWidth());
        });
    }

    // inner class which extends ListCell with additional button - for adding ingredient to shopping list - option for logged users
    static class ButtonCell extends ListCell<Ingredient> {
        private HBox box = new HBox();
        private Pane pane = new Pane();
        private Label label = new Label("(empty)");
        public Ingredient selectedIngredient;
        private User activeUser;
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
                if (activeUser.checkIfIngredientInShoppingList(selectedIngredient)) {
                    Ingredient ingredient = activeUser.getIngredientFromShoppingList(selectedIngredient);
                    Status status = ingredient.getShoppingListStatus();
                    if (status == Status.deleted) {
                        ingredient.setShoppingListStatus(Status.added);
                        view.setImage(new Image("icons/minus.png"));
                    } else if (status == Status.added || status == Status.loaded) {
                        ingredient.setShoppingListStatus(Status.deleted);
                        view.setImage(new Image("icons/plus.png"));
                    }
                } else {
                    selectedIngredient.setShoppingListStatus(Status.added);
                    activeUser.addToShoppingList(selectedIngredient);
                    view.setImage(new Image("icons/minus.png"));
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
                label.setText(String.format((ingredient.getQuantity() % 1 == 0)?" %1.0f %s %s":" %1.2f %s %s",  ingredient.getQuantity(), ingredient.getUnit(), ingredient.getName()));
                Status status = activeUser.getIngredientStatus(selectedIngredient);
                if (status == Status.deleted || status == Status.none || status == null) {
                    view.setImage(new Image("icons/plus.png"));
                }
                else{
                    view.setImage(new Image("icons/minus.png"));
                }

                setGraphic(box);
            }
        }
    }

    // format properly listView - with button or without
    private void setIngredListView(ArrayList<Ingredient> ingredientsList, Boolean changedSingleUnit) throws IOException, SQLException {
        ingredientListView.getItems().clear();

        if (activeUser != null){
            if (activeUser.getDefaultUnitSystem() == null || activeUser.getDefaultUnitSystem().equals("Default") || changedSingleUnit) {
                ingredientListView.getItems().addAll(ingredientsList);
                ingredientListView.setCellFactory(ingredientListView -> new ButtonCell(activeUser));
                return;
            }
            String bestUnit = "";
            ArrayList<Ingredient> goodIngredients;
            ArrayList<Ingredient> recipeIngredients = ingredientsList;
            for (Ingredient ing : recipeIngredients) {
                if (ing.getUnit().equals("piece")) {
                    ingredientListView.getItems().add(ing);
                } else {
                    bestUnit = DatabaseConnection.getBestUnit(activeUser.getDefaultUnitSystem(), ing.getUnit(), ing.getQuantity());
                    ingredientListView.getItems().add(new Ingredient(ing.getId(), DatabaseConnection.convertUnit(ing.getQuantity(), ing.getUnit(), bestUnit), bestUnit, ing.getName()));
                }
                ingredientListView.setCellFactory(ingredientListView -> new ButtonCell(activeUser));
            }
        }
        else {
            for (Ingredient ingredient : ingredientsList) {
                ingredientListView.getItems().add(String.format((ingredient.getQuantity() % 1 == 0)?"%1.0f %s %s":"%1.2f %s %s", ingredient.getQuantity(), ingredient.getUnit(), ingredient.getName()));
            }
        }
    }

    // return Item for MenuContext which delete selected item in ingredient List, User can delete several ingredients at once
    public MenuItem createDeleteFromShoppingListItem() {
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(actionEvent -> {
            ObservableList<Ingredient> ingredients = ingredientListView.getSelectionModel().getSelectedItems();
            for (Ingredient selectedIngredient :ingredients){
                if (activeUser.checkIfIngredientInShoppingList(selectedIngredient)) {
                    Ingredient ingredient = activeUser.getIngredientFromShoppingList(selectedIngredient);
                    Status status = ingredient.getShoppingListStatus();
                    if (status == Status.added || status == Status.loaded) {
                        ingredient.setShoppingListStatus(Status.deleted);
                    }
                }
            }
            ingredientListView.refresh();
        });
        return delete;
    }

    // return Item for MenuContext which delete selected item in ingredient List, User can add several ingredients at once
    public MenuItem createAddToShoppingListItem() {
        MenuItem add = new MenuItem("Add");
        add.setOnAction(actionEvent -> {
            ObservableList<Integer> ingredients = ingredientListView.getSelectionModel().getSelectedIndices();
            for (Integer selectedIngredient :ingredients){
                if (activeUser.checkIfIngredientInShoppingList(this.recipe.getIngredientList().get(selectedIngredient))) {
                    Ingredient ingredient = activeUser.getIngredientFromShoppingList(this.recipe.getIngredientList().get(selectedIngredient));
                    Status status = ingredient.getShoppingListStatus();
                    if (status == Status.deleted) {
                        ingredient.setShoppingListStatus(Status.added);
                    }
                } else {
                    System.out.println(this.recipe.getIngredientList().get(selectedIngredient).getQuantity());
                    this.recipe.getIngredientList().get(selectedIngredient).setShoppingListStatus(Status.added);
                    activeUser.addToShoppingList(this.recipe.getIngredientList().get(selectedIngredient));
                }
            }
            ingredientListView.refresh();
        });
        return add;
    }

    public Menu createChangeUnit() {
        Menu change = new Menu("Change unit");
        change.getItems().clear();
        for (String item : activeUser.getUnits()){
            MenuItem temp = new MenuItem(item);
            change.getItems().add(temp);
            temp.setOnAction(e -> {
                try {
                    changeUnit(temp.getText(), ingredientListView.getSelectionModel().getSelectedItems(), ingredientListView.getItems());
                } catch (IOException | SQLException err) {
                    err.printStackTrace();
                }
            });
        }

        return change;
    }

    private void changeUnit(String newUnit, ObservableList<Ingredient> selIngredients, ObservableList<Ingredient> allIngredients) throws IOException, SQLException {
        ArrayList<Ingredient> newList = new ArrayList<>();
        for (Ingredient ingredient : allIngredients){
            if (selIngredients.contains(ingredient) && !ingredient.getUnit().equals("piece")){
                String oldUnit = ingredient.getUnit();
                Double oldQuantity = ingredient.getQuantity();
                Double newQuantity = DatabaseConnection.convertUnit(oldQuantity, oldUnit, newUnit);
                Ingredient tempIn = new Ingredient(ingredient.getId(), newQuantity, newUnit, ingredient.getName());
                newList.add(tempIn);
            }

            else {
                newList.add(ingredient);
            }
        }
        setIngredListView(newList, Boolean.TRUE);

    }

    private void setPortionAreaProperty(){
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
                } catch (SQLException | IOException err) {
                    err.printStackTrace();
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

    private void changeIngredListViewScale(Double numPortions) throws IOException, SQLException {
        double currNumPortions = this.recipe.getPortionNumber();
        if (!numPortions.equals(currNumPortions)) {
            double scale = numPortions / currNumPortions;
            this.recipe.setPortionNumber(numPortions);
            this.recipe.scaleIngredientList(scale);
            setIngredListView(this.recipe.getIngredientList(), Boolean.FALSE);
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
    private void saveRecipe() {
        setSavedRecipe();
        recipe.saveToFile( getRecipeFileDirectory());
    }

    private void setSavedRecipe(){
        deleteButton.setStyle("-fx-background-radius: 50;");
        deleteButton.setText("");
        saveButton.setStyle("-fx-background-color: transparent;");
        saveButton.setPrefWidth(50);
        deleteButton.setPrefWidth(35);
        saveDeleteBox.setStyle("-fx-background-color: -fx-accent;-fx-background-radius: 50;");
        saveButton.setText("Saved");
    }

    private void setSaveRecipe(){
        saveButton.setStyle("-fx-background-radius: 50;");
        saveButton.setText("");
        saveButton.setPrefWidth(35);
        deleteButton.setPrefWidth(50);
        deleteButton.setStyle("-fx-background-color: transparent;");
        saveDeleteBox.setStyle("-fx-background-color: transparent;-fx-background-radius: 50;-fx-border-radius: 50; -fx-border-width: 0.2;-fx-border-color: grey;");
        deleteButton.setText("Save");
    }

    @FXML
    private void deleteRecipe() {
        setSaveRecipe();
        recipe.deleteFile( getRecipeFileDirectory());
    }

    private String getRecipeFileDirectory() {
        return "./savedRecipes/" + recipe.getName() +"-"+ recipe.getAuthor() + ".txt";
    }

    @FXML
    private void onCommentButtonAction() {
        FXMLLoader loader = loadFXML(new OpinionPane(this.recipe, activeUser, this), "/resources/opinionPage.fxml");
        changeScene(commentButton, loader);
    }
    @FXML
    private void onScaleButtonAction() {
        FXMLLoader loader = loadFXML(new ScalePane(this.recipe, activeUser, this), "/resources/scalePage.fxml");
        changeScene(scaleButton, loader);
    }
    @FXML
    private void onExitButtonAction() {
        if (returnPane != null) {
            FXMLLoader loader = loadFXML(returnPane, "/resources/mainPage.fxml");
            changeScene(exitButton, loader);
        } else {
            exitButton.getScene().getWindow().hide();
        }
    }

    @FXML
    private void onShoppingListButtonAction() {
        FXMLLoader loader = loadFXML(new ShoppingListPane(activeUser, new RecipePane(new Recipe(this.recipe), this.activeUser, this.returnPane)), "/resources/shoppingListPage.fxml");
        changeScene(shoppingListButton, loader);
    }

    @FXML
    private void onTimeButtonAction() {
        FXMLLoader loader = loadFXML(new TimerPane(), "/resources/timerPage.fxml");
        changeScene(loader, 0, 200);
    }

}