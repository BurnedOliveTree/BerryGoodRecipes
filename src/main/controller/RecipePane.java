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
import main.userModel.Group;
import main.userModel.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    @FXML private Spinner<Double> portionArea;
    @FXML private Pane ingredientPane;
    @FXML private ListView<Ingredient> ingredientListView;
    @FXML private Button exitButton;
    @FXML private Button shoppingListButton;
    @FXML private Button likeButton;
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
        // description
        Text text = new Text(this.recipe.getPrepareMethod());

        // ingredient list
        ingredientListView = new ListView<>();
        setIngredListView(this.recipe.getIngredientList(), Boolean.FALSE);
        ingredientPane.getChildren().add(ingredientListView);
        ingredientListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // title
        titleLabel.setText(this.recipe.getName());
        titleLabel.setWrapText(true);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        // additional information
        authorLabel.setText(this.recipe.getAuthor());
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

        // save/saved button
        Files.createDirectories(Paths.get("./savedRecipes/"));
        if(Files.exists(Paths.get(getRecipeFileDirectory()))) {
            setSavedRecipe();
        } else {
            setSaveRecipe();
        }

        // options for logged in users
        if (activeUser == null) {
            ingredientListView.setPrefHeight(this.recipe.getIngredientList().size() * 27);
            likeButton.setDisable(true);
            shoppingListButton.setDisable(true);
            super.setContextMenu(authorLabel, createShowRecipesMenuItem());
        } else {
            ingredientListView.setPrefHeight(this.recipe.getIngredientList().size() * 32);
            if (activeUser.checkIfRecipeFavorite(this.recipe)) {
                LikePic.setImage(new Image("icons/favoriteClicked.png"));
            }
            super.setContextMenu(ingredientListView, createDeleteFromShoppingListItem(), createAddToShoppingListItem(), createChangeUnit());
            super.setContextMenu(authorLabel, createShowRecipesMenuItem(), createFollowMenuItem(), createInviteMenu());
        }

        Platform.runLater(() -> {
            commentButton.setPrefWidth(propertyBox.getWidth());
            descText.getChildren().add(text);
        });
    }

    // context menu bind with author label

    private MenuItem createShowRecipesMenuItem() {
        // after right pressing on mouse button on author label. Create opportunity to show all user recipes
        MenuItem menuItem = new MenuItem("Show recipes");
        menuItem.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainPage.fxml"));
                MainPane controller = new MainPane(activeUser);
                loader.setControllerFactory(param -> controller);
                changeScene(exitButton, loader);

                controller.search.setText("user:" + recipe.getAuthor());
                controller.search();
            } catch (SQLException | IOException err) {
                err.printStackTrace();
            }
        });
        return menuItem;
    }

    private MenuItem createFollowMenuItem() {
        // after right pressing on mouse button on author label. Create opportunity to follow author of recipe
        MenuItem followMenuItem = new MenuItem("Follow");
        if (activeUser.getFollowed().contains(recipe.getAuthor()))
            followMenuItem.setDisable(true);
        followMenuItem.setOnAction(actionEvent -> {
            activeUser.followUser(recipe.getAuthor());
            followMenuItem.setDisable(true);
        });
        return followMenuItem;
    }

    private Menu createInviteMenu(){
        // after right pressing on mouse button on author label. Create opportunity to invite author to one of user group
        Menu menu = new Menu("Invite");
        List<MenuItem> menuItemList = new ArrayList<>();
        for (Group group: activeUser.getUserGroups()) {
            MenuItem tempMenuItem = new MenuItem(group.getName());
            if (group.getParticipants().contains(recipe.getAuthor()))
                tempMenuItem.setDisable(true);
            tempMenuItem.setOnAction(actionEvent -> {
                try {
                    DatabaseConnection.invite(recipe.getAuthor(), group.getID());
                    tempMenuItem.setDisable(true);
                } catch (IOException | SQLException err) { err.printStackTrace(); }
            });
            menuItemList.add(tempMenuItem);
        }
        menu.getItems().addAll(menuItemList);
        return menu;
    }

    // ingredient list view

    static class ButtonCell extends ListCell<Ingredient> {
        // inner class which extends ListCell with additional button - for adding ingredient to shopping list - option for logged users
        private final HBox box = new HBox();
        private final Label label = new Label("(empty)");
        public Ingredient selectedIngredient;
        private final User activeUser;
        private ImageView view;

        public ButtonCell(User activeUser) {
            super();
            this.activeUser = activeUser;
            Pane pane = new Pane();
            HBox.setHgrow(pane, Priority.ALWAYS);
            setView();
            box.getChildren().addAll(view, label, pane);
        }

        private void setView() {
            view = new ImageView(new Image("icons/plus.png"));
            view.setFitHeight(20);
            view.setFitWidth(20);
            view.setOnMouseClicked(mouseEvent -> {
                if (activeUser.checkIfIngredientExistedInShoppingList(selectedIngredient)) {
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
            // change image +/- taking into account the content of shopping list
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

    private void setIngredListView(ArrayList<Ingredient> ingredientsList, Boolean changedSingleUnit) throws IOException, SQLException {
        // format properly listView - with button (for logged user) or without
        ingredientListView.getItems().clear();
        if (activeUser != null){
            if (activeUser.getDefaultUnitSystem() == null || activeUser.getDefaultUnitSystem().equals("Default") || changedSingleUnit) {
                ingredientListView.getItems().addAll(ingredientsList);
                ingredientListView.setCellFactory(ingredientListView -> new ButtonCell(activeUser));
                return;
            }
            // with conversion
            String bestUnit;
            for (Ingredient ing : ingredientsList) {
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
            for (Ingredient ing : ingredientsList) {
                ingredientListView.getItems().add(ing);
            }
        }
    }

    public MenuItem createDeleteFromShoppingListItem() {
        // return Item for MenuContext which delete selected item in ingredient List, User can delete several ingredients at once
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(actionEvent -> {
            ObservableList<Ingredient> ingredients = ingredientListView.getSelectionModel().getSelectedItems();
            for (Ingredient selectedIngredient :ingredients){
                if (activeUser.checkIfIngredientExistedInShoppingList(selectedIngredient)) {
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

    public MenuItem createAddToShoppingListItem() {
        // return Item for MenuContext which delete selected item in ingredient List, User can add several ingredients at once
        MenuItem add = new MenuItem("Add");
        add.setOnAction(actionEvent -> {
            ObservableList<Integer> ingredients = ingredientListView.getSelectionModel().getSelectedIndices();
            for (Integer selectedIngredient :ingredients){
                if (activeUser.checkIfIngredientExistedInShoppingList(this.recipe.getIngredientList().get(selectedIngredient))) {
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

    // changing unit in ingredient list

    public Menu createChangeUnit() {
        // give opportunity for changing unit more convenient for the user
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
        // convert unit
        ArrayList<Ingredient> newList = new ArrayList<>();
        for (Ingredient ingredient : allIngredients){
            if (selIngredients.contains(ingredient) && !ingredient.getUnit().equals("piece")){
                String oldUnit = ingredient.getUnit();
                Double oldQuantity = ingredient.getQuantity();
                Double newQuantity = DatabaseConnection.convertUnit(oldQuantity, oldUnit, newUnit);
                Ingredient tempIn = new Ingredient(ingredient.getId(), newQuantity, newUnit, ingredient.getName());
                newList.add(tempIn);
            } else {
                newList.add(ingredient);
            }
        }
        setIngredListView(newList, Boolean.TRUE);
    }

    // changing portion in ingredient list

    private void setPortionAreaProperty(){
        // set spinner for changing portion field
        portionArea.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 1000.0));
        portionArea.getEditor().textProperty().set(String.format((recipe.getPortionNumber() % 1 == 0)?"%1.0f":"%1.2f", recipe.getPortionNumber()));
        portionArea.setEditable(true);
        // change portions using input value from keyboard
        portionArea.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    double currNumPortions = Double.parseDouble(portionArea.getEditor().textProperty().get());
                    if (currNumPortions > 0){
                        recipe.setPortionNumber(currNumPortions);
                        changeIngredListViewScale(currNumPortions);

                    }
                    else {
                        portionArea.getEditor().textProperty().set(String.format((recipe.getPortionNumber() % 1 == 0) ? "%1.0f" : "%1.2f", recipe.getPortionNumber()));
                    }
                } catch (NumberFormatException e) {
                    portionArea.getEditor().textProperty().set(String.format((recipe.getPortionNumber() % 1 == 0)?"%1.0f":"%1.2f", recipe.getPortionNumber()));
                } catch (SQLException | IOException err) {
                    err.printStackTrace();
                }
            }
        });
        portionArea.valueProperty().addListener(this::handleSpin);
    }

    private void handleSpin(ObservableValue<?> observableValue, Number oldValue, Number currNumPortions) {
        // for Listener in portionArea, change portions using button
        try {
            if (currNumPortions.intValue() > 0)
                changeIngredListViewScale((double)currNumPortions.intValue());

            else
                // if an invalid value is entered
                portionArea.getEditor().textProperty().set("1");
        } catch (Exception e) {
            portionArea.getEditor().textProperty().set(oldValue.toString());
        }
    }

    private void changeIngredListViewScale(Double numPortions) throws IOException, SQLException {
        recipe.scaleIngredientList(numPortions);
        setIngredListView(this.recipe.getIngredientList(), Boolean.TRUE);
    }

    // menu box options

    @FXML
    private void onLikeButtonAction() {
        // add/delete from user favorites, change image
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
        recipe.saveToFile(getRecipeFileDirectory());
    }

    private void setSavedRecipe(){
        deleteButton.setStyle("-fx-background-radius: 50;");
        deleteButton.setText("");
        saveButton.setStyle("-fx-background-color: transparent;");
        saveButton.setPrefWidth(70);
        deleteButton.setPrefWidth(40);
        saveDeleteBox.setStyle("-fx-background-color: -fx-accent;-fx-background-radius: 50;");
        saveButton.setText("Saved");
    }

    @FXML
    private void deleteRecipe() {
        setSaveRecipe();
        recipe.deleteFile( getRecipeFileDirectory());
    }

    private void setSaveRecipe(){
        saveButton.setStyle("-fx-background-radius: 50;");
        saveButton.setText("");
        saveButton.setPrefWidth(40);
        deleteButton.setPrefWidth(70);
        deleteButton.setStyle("-fx-background-color: transparent;");
        saveDeleteBox.setStyle("-fx-background-color: transparent;-fx-background-radius: 50;-fx-border-radius: 50; -fx-border-width: 0.2;-fx-border-color: grey;");
        deleteButton.setText("Save");
    }

    private String getRecipeFileDirectory() {
        return "./savedRecipes/" + recipe.getName() +"-"+ recipe.getAuthor() + ".txt";
    }

    @FXML
    private void onCommentButtonAction() {
        FXMLLoader loader = loadFXML(new OpinionPane(this.recipe, activeUser, this), "/opinionPage.fxml");
        changeScene(commentButton, loader);
    }
    @FXML
    private void onScaleButtonAction() {
        FXMLLoader loader = loadFXML(new ScalePane(this), "/scalePage.fxml");
        changeScene(scaleButton, loader);
    }
    @FXML
    private void onExitButtonAction() {
        if (returnPane != null) {
            FXMLLoader loader = loadFXML(returnPane, "/mainPage.fxml");
            changeScene(exitButton, loader);
        } else {
            exitButton.getScene().getWindow().hide();
        }
    }
    @FXML
    private void onShoppingListButtonAction() {

        FXMLLoader loader = loadFXML(new ShoppingListPane(activeUser, new RecipePane(new Recipe(this.recipe), this.activeUser, this.returnPane)), "/shoppingListPage.fxml");
        changeScene(shoppingListButton, loader);
    }
    @FXML
    private void onTimeButtonAction() {
        FXMLLoader loader = loadFXML(new TimerPane(), "/timerPage.fxml");
        changeScene(loader, 0, 200);
    }

}