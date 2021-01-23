package main.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import main.DatabaseConnection;
import main.recipeModel.Ingredient;
import main.userModel.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ShoppingListPane extends BasicPaneActions {
    private final User activeUser;
    private final BasicPaneActions returnPane;
    private final List<String> groups;
    private List<Ingredient> ingredientList = new ArrayList<>();
    private String showFirst;

    @FXML private Button exitButton;
    @FXML private ImageView exitPic;
    @FXML private ListView<String> shoppingList;
    @FXML private MenuButton shareMenu;
    @FXML private ChoiceBox<String> otherListsMenu;
    @FXML private MenuButton addIngredient;

    public ShoppingListPane(User activeUser, BasicPaneActions returnPane, String showFirst) {
        this.activeUser = activeUser;
        this.returnPane = returnPane;
        this.groups =  activeUser.getUserGroups();
        if (!this.groups.contains("User"))
            this.groups.add("User");
        this.showFirst = showFirst;
    }
    public ShoppingListPane(User activeUser, BasicPaneActions returnPane) {
        this.activeUser = activeUser;
        this.returnPane = returnPane;
        this.groups =  activeUser.getUserGroups();
        if (!this.groups.contains("User"))
            this.groups.add("User");
        this.showFirst = "User";
    }


    @FXML
    void initialize() throws IOException, SQLException {
        if (DatabaseConnection.isThemeLight()) {
            exitPic.setImage(new Image("icons/berryExit.png"));
        }
        showShoppingList(showFirst);
        Platform.runLater(()->{
            setShareMenu(showFirst);
            // if user in group shopping list he cannot share list
            shareMenu.managedProperty().bind(shareMenu.visibleProperty());
            addIngredient.managedProperty().bind(addIngredient.visibleProperty());
            setOtherListsMenu();
            setAddIngredient();
            setContextMenu(shoppingList, createDeleteIngredientItem());
        });

    }

    private void setAddIngredient() {
        // set add ingredient option
        CustomMenuItem customMenuItem = new CustomMenuItem();
        VBox newIngredient = new VBox();
        TextField quantity = new TextField();
        ChoiceBox unit = new ChoiceBox();
        unit.getItems().addAll(FXCollections.observableArrayList(activeUser.getUnits()));
        TextField name = new TextField();
        Button addButton = new Button();
        quantity.setPromptText("Qty");
        name.setPromptText("Name");
        addButton.setText("Add Ingredient");
        quantity.setStyle("-fx-text-box-border: transparent");
        name.setStyle("-fx-text-box-border: transparent");
        quantity.setAlignment(Pos.CENTER_RIGHT);
        name.setAlignment(Pos.CENTER_RIGHT);
        newIngredient.setPrefWidth(addIngredient.getPrefWidth() - 10);
        unit.setPrefWidth(newIngredient.getPrefWidth());
        addButton.setPrefWidth(newIngredient.getPrefWidth());
        addButton.setOnAction(actionEvent -> {
            if (!quantity.getText().equals("") && !name.getText().equals("")) {
                Ingredient ingredient = null;
                try {
                    ingredient = new Ingredient(null, DatabaseConnection.convertUnit(Double.parseDouble(quantity.getText()), unit.getSelectionModel().getSelectedItem().toString(), "gram"),"gram", name.getText());
                } catch (IOException | SQLException err) {
                    err.printStackTrace();
                }
                ingredient.setShoppingListStatus(Status.added);
                if (activeUser.isNameInShoppingList(ingredient.getName())){
                    try {
                        activeUser.editQuantityInShopping(ingredient.getName(), ingredient.getQuantity());
                    } catch (IOException | SQLException err) {
                        err.printStackTrace();
                    }
                }
                else {
                    activeUser.addToShoppingList(ingredient);
                }
                quantity.clear();
                name.clear();
                try {
                    showShoppingList("User");
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        newIngredient.getChildren().addAll(quantity,unit, name, addButton);
        customMenuItem.setContent(newIngredient);
        customMenuItem.setHideOnClick(false);
        addIngredient.getItems().add(customMenuItem);
    }

    private void setShareMenu(String currentList) {
        shareMenu.getItems().clear();
        if (currentList.equals("User")) {
            // only in user shopping list user can share list
            shareMenu.setVisible(true);
            addIngredient.setVisible(true);
            for (String groupName : groups) {
                if (!groupName.equals("User")) {
                    MenuItem menuItem = new MenuItem(groupName);
                    menuItem.setOnAction(e -> {
                        try {
                            DatabaseConnection.shareList(activeUser, groupName);
                            activeUser.getShoppingList().clear();
                            shoppingList.getItems().clear();
                            shoppingList.refresh();

                        } catch (IOException | SQLException ioException) {
                            ioException.printStackTrace();
                        }
                    });
                    shareMenu.getItems().add(menuItem);
                }
            }
        } else {
            shareMenu.setVisible(false);
            addIngredient.setVisible(false);
        }
    }


    private void setOtherListsMenu() {
        otherListsMenu.setItems(FXCollections.observableArrayList(groups));
        otherListsMenu.getSelectionModel().select(groups.indexOf(showFirst));
        otherListsMenu.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            setShareMenu(groups.get(t1.intValue()));
            try {
                showShoppingList(groups.get(t1.intValue()));
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private Boolean ingredientInList(String ingredientName, ArrayList<String> usedIngredients) {
        for (String usedIng : usedIngredients) {
            if (usedIng.equals(ingredientName)) return true;
        }
        return false;
    }

    private Ingredient addSameIngredients(Ingredient ingredient) throws IOException, SQLException {
        Double newQuantity = 0.0;
        if (!ingredient.getUnit().equals("piece")) {
            for (Ingredient nextIngredient : activeUser.showShoppingList().values()) {
                if (nextIngredient.getName().equals(ingredient.getName())) {
                    System.out.println("!");
                    newQuantity += DatabaseConnection.convertUnit(nextIngredient.getQuantity(), nextIngredient.getUnit(), "gram");
                }
            }
            return new Ingredient(0, newQuantity, "gram", ingredient.getName());
        }
        else{
            for (Ingredient nextIngredient : activeUser.showShoppingList().values()) {
                if (nextIngredient.getName().equals(ingredient.getName())) {
                    newQuantity += nextIngredient.getQuantity();
                }
            }
            return new Ingredient(0, newQuantity, "piece", ingredient.getName());
        }
    }

    private void showShoppingList(String currentList) throws IOException, SQLException {
        shoppingList.getItems().clear();
        ingredientList.clear();

        if (currentList.equals("User")) {
            for (Ingredient ingredient : activeUser.showShoppingList().values()) {
                ingredientList.add(ingredient);
                if (ingredient.getUnit().equals("piece")) {
                    shoppingList.getItems().add(String.format((ingredient.getQuantity() % 1 == 0) ? "%1.0f %s %s" : "%1.2f %s %s", ingredient.getQuantity(), "piece", ingredient.getName()));
                } else {
                    Ingredient temp = new Ingredient(0, DatabaseConnection.convertUnit(ingredient.getQuantity(), ingredient.getUnit(), "gram"), "gram", ingredient.getName());
                    shoppingList.getItems().add(String.format((temp.getQuantity() % 1 == 0) ? "%1.0f %s %s" : "%1.2f %s %s", temp.getQuantity(), "gram", temp.getName()));
                }
            }
        } else {
            Map<Ingredient, String> ShoppingList = DatabaseConnection.getGroupShoppingList(activeUser, currentList);
            assert ShoppingList != null;
            for (Map.Entry<Ingredient, String> entry : ShoppingList.entrySet()) {
                String author = entry.getValue();
                Ingredient ingredient = entry.getKey();
                ingredientList.add(ingredient);
                if (ingredient.getUnit().equals("piece")) {
                    shoppingList.getItems().add(String.format((ingredient.getQuantity() % 1 == 0) ? "%1.0f %s %s\t%s" : "%1.2f %s %s\t%s", ingredient.getQuantity(), "piece", ingredient.getName(), author));
                } else {
                    Ingredient temp = new Ingredient(0, DatabaseConnection.convertUnit(ingredient.getQuantity(), ingredient.getUnit(), "gram"), "gram", ingredient.getName());
                    shoppingList.getItems().add(String.format((temp.getQuantity() % 1 == 0) ? "%1.0f %s %s\t%s" : "%1.2f %s %s\t%s", temp.getQuantity(), "gram", temp.getName(), author));
                }
            }
        }
        shoppingList.refresh();
    }

    private MenuItem createDeleteIngredientItem(){
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(actionEvent -> {
            if (otherListsMenu.getValue().equals("User")) {
                activeUser.removeFromShoppingList(ingredientList.get(shoppingList.getSelectionModel().getSelectedIndex()));
            } else {
                try {
                    DatabaseConnection.deleteIngredientFromGroupShoppingList(activeUser, otherListsMenu.getValue(), ingredientList.get(shoppingList.getSelectionModel().getSelectedIndex()));
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
            ingredientList.remove(shoppingList.getSelectionModel().getSelectedIndex());
            shoppingList.getItems().remove(shoppingList.getSelectionModel().getSelectedIndex());
            shoppingList.refresh();
        });
        return delete;
    }

    @FXML
    public void clearShoppingList() throws IOException, SQLException {
        if (otherListsMenu.getValue().equals("User")) {
            activeUser.removeShoppingList();
        } else {
            DatabaseConnection.deleteGroupShoppingList(activeUser, otherListsMenu.getValue());
        }
        ingredientList.clear();
        shoppingList.getItems().clear();
        shoppingList.refresh();
    }

    @FXML   // return to proper window
    public void onExitButtonAction(){
        String path = "";
        if (Pattern.compile("MainPane").matcher(returnPane.getClass().getName()).find())
            path = "/resources/mainPage.fxml";
        else if (Pattern.compile("RecipePane").matcher(returnPane.getClass().getName()).find())
            path = "/resources/recipePage.fxml";
        else if (Pattern.compile("UserAdminPane").matcher(returnPane.getClass().getName()).find())
            path = "/resources/userAdminPage.fxml";
        if (!path.equals("")) {
            FXMLLoader loader = loadFXML(returnPane, path);
            changeScene(exitButton, loader);
        }
    }

}