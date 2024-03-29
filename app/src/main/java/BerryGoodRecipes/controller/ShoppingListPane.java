package BerryGoodRecipes.controller;

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

import BerryGoodRecipes.DatabaseConnection;
import BerryGoodRecipes.recipeModel.Ingredient;
import BerryGoodRecipes.userModel.Group;
import BerryGoodRecipes.userModel.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ShoppingListPane extends BasicPaneActions {
    private final User activeUser;
    private final BasicPaneActions returnPane;
    private final List<Group> groups;
    private List<Ingredient> ingredientList = new ArrayList<>();
    private final Group showFirst;
    private final Group privateGroup = new Group(-1, "User");

    @FXML private Button exitButton;
    @FXML private ImageView exitPic;
    @FXML private ImageView sharePic;
    @FXML private ImageView clearPic;
    @FXML private ListView<String> shoppingList;
    @FXML private MenuButton shareMenu;
    @FXML private ChoiceBox<Group> otherListsMenu;
    @FXML private MenuButton addIngredient;

    public ShoppingListPane(User activeUser, BasicPaneActions returnPane, Group showFirst) {
        this.activeUser = activeUser;
        this.returnPane = returnPane;
        this.groups =  activeUser.getUserGroups();
        if (!this.groups.contains(privateGroup))
            this.groups.add(privateGroup);
        this.showFirst = showFirst;
    }

    public ShoppingListPane(User activeUser, BasicPaneActions returnPane) {
        this.activeUser = activeUser;
        this.returnPane = returnPane;
        this.groups =  activeUser.getUserGroups();
        if (!this.groups.contains(privateGroup))
            this.groups.add(privateGroup);
        this.showFirst = privateGroup;
    }

    @FXML
    void initialize() throws IOException, SQLException {
        if (DatabaseConnection.isThemeLight()) {
            exitPic.setImage(new Image("icons/berryExit.png"));
            sharePic.setImage(new Image("icons/berryShare.png"));
            clearPic.setImage(new Image("icons/berryClear.png"));
        }
        showShoppingList(showFirst);
        Platform.runLater(()->{
            setShareMenu(showFirst);
            // if user in group shopping list he cannot share list and add custom ingredient
            shareMenu.managedProperty().bind(shareMenu.visibleProperty());
            addIngredient.managedProperty().bind(addIngredient.visibleProperty());
            setOtherListsMenu();
            setAddIngredient();
            setContextMenu(shoppingList, createDeleteIngredientItem());
        });

    }

    // shopping list options - with view changes

    private void setAddIngredient() {
        // set add custom ingredient option
        CustomMenuItem customMenuItem = new CustomMenuItem();
        VBox newIngredient = new VBox();
        newIngredient.setPrefWidth(addIngredient.getPrefWidth() - 10);

        // quantity settings
        TextField quantity = new TextField();
        quantity.setPromptText("Qty");
        quantity.setStyle("-fx-text-box-border: transparent");
        quantity.setAlignment(Pos.CENTER_RIGHT);

        //unit settings
        ChoiceBox<String> unit = new ChoiceBox<>();
        unit.getItems().addAll(FXCollections.observableArrayList(DatabaseConnection.units.getUnits()));
        unit.setPrefWidth(newIngredient.getPrefWidth());

        // name settings
        TextField name = new TextField();
        name.setPromptText("Name");
        name.setStyle("-fx-text-box-border: transparent");
        name.setAlignment(Pos.CENTER_RIGHT);

        // add button settings - with creating new ingredient
        Button addButton = new Button();
        addButton.setText("Add Ingredient");
        addButton.setPrefWidth(newIngredient.getPrefWidth());
        addButton.setOnAction(actionEvent -> {
            if (!quantity.getText().equals("") && DatabaseConnection.checkDoubleDatabaseReduction(quantity.getText()) && quantity.getText().matches("\\d+(\\.\\d+)?") && !name.getText().equals("") && !(name.getText().length() > DatabaseConnection.shortTextFieldLength)) {
                try {
                    Ingredient ingredient = new Ingredient(null, DatabaseConnection.units.convertUnit(unit.getSelectionModel().getSelectedItem(), "gram", Double.parseDouble(quantity.getText())),"gram", name.getText());
                    ingredient.setShoppingListStatus(Status.added);
                    if (activeUser.qualifiesToAdd(ingredient.getName())){
                        if(!activeUser.editQuantityInShopping(ingredient.getName(), ingredient.getQuantity())) showWarning("Too large quantity!");
                    }
                    else {
                        activeUser.addToShoppingList(ingredient);
                    }
                    quantity.clear();
                    name.clear();
                    showShoppingList(privateGroup);
                } catch (IOException | SQLException err) {
                    err.printStackTrace();
                }
            } else {
                showWarning("Wrong value!");
                quantity.clear();
                name.clear();
            }
        });
        newIngredient.getChildren().addAll(quantity,unit, name, addButton);
        customMenuItem.setContent(newIngredient);
        customMenuItem.setHideOnClick(false);
        addIngredient.getItems().add(customMenuItem);
    }

    private void setShareMenu(Group currentList) {
        // create share menu
        shareMenu.getItems().clear();
        // if current list is user shopping list
        if (currentList == privateGroup) {
            // only in user shopping list user can share list
            shareMenu.setVisible(true);
            addIngredient.setVisible(true);
            for (Group group : groups) {
                if (group.getID() != null && !group.equals(privateGroup)) {
                    MenuItem menuItem = new MenuItem(group.getName());
                    menuItem.setOnAction(e -> {
                        try {
                            // sharing a list clears the user's list
                            DatabaseConnection.shareList(activeUser, group.getID());
                            activeUser.getShoppingList().clear();
                            shoppingList.getItems().clear();
                            shoppingList.refresh();
                        } catch (IOException | SQLException err) {
                            err.printStackTrace();
                        }
                    });
                    shareMenu.getItems().add(menuItem);
                }
            }
            // if not set some functions disable
        } else {
            shareMenu.setVisible(false);
            addIngredient.setVisible(false);
        }
    }

    private void setOtherListsMenu() {
        // create menu which allows user to display group lists
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

    // show shopping list

    private void showShoppingList(Group currentList) throws IOException, SQLException {
        shoppingList.getItems().clear();
        ingredientList.clear();

        // if
        if (currentList == privateGroup) {
            showUserShoppingList();
        } else {
            showGroupShoppingList(currentList);
        }
        shoppingList.refresh();
    }

    private void showUserShoppingList() throws IOException, SQLException {
        // prepare list to show user shopping list
        for (Ingredient ingredient : activeUser.showShoppingList().values()) {
            ingredientList.add(ingredient);
            if (ingredient.getUnit().equals("piece")) {
                shoppingList.getItems().add(String.format((ingredient.getQuantity() % 1 == 0) ? "%1.0f %s %s" : "%1.2f %s %s", ingredient.getQuantity(), "piece", ingredient.getName()));
            } else {
                Ingredient temp = new Ingredient(0, DatabaseConnection.units.convertUnit(ingredient.getUnit(), "gram", ingredient.getQuantity()), "gram", ingredient.getName());
                shoppingList.getItems().add(String.format((temp.getQuantity() % 1 == 0) ? "%1.0f %s %s" : "%1.2f %s %s", temp.getQuantity(), "gram", temp.getName()));
            }
        }
    }

    private void showGroupShoppingList(Group currentList) throws IOException, SQLException {
        // prepare list to show group shopping list
        Map<Ingredient, String> ShoppingList = DatabaseConnection.getGroupShoppingList(currentList.getID());
        for (Map.Entry<Ingredient, String> entry : ShoppingList.entrySet()) {
            String author = entry.getValue();
            Ingredient ingredient = entry.getKey();
            ingredientList.add(ingredient);
            if (ingredient.getUnit().equals("piece")) {
                shoppingList.getItems().add(String.format((ingredient.getQuantity() % 1 == 0) ? "%1.0f %s %s\t%s" : "%1.2f %s %s\t%s", ingredient.getQuantity(), "piece", ingredient.getName(), author));
            } else {
                Ingredient temp = new Ingredient(0, DatabaseConnection.units.convertUnit(ingredient.getUnit(), "gram", ingredient.getQuantity()), "gram", ingredient.getName());
                shoppingList.getItems().add(String.format((temp.getQuantity() % 1 == 0) ? "%1.0f %s %s\t%s" : "%1.2f %s %s\t%s", temp.getQuantity(), "gram", temp.getName(), author));
            }
        }
    }

    // delete ingredient

    private MenuItem createDeleteIngredientItem(){
        // delete selected ingredient from shopping list
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(actionEvent -> {
            // if option selected in user shopping list
            if (otherListsMenu.getValue() == privateGroup) {
                activeUser.removeSameNamedFromShoppingList(ingredientList.get(shoppingList.getSelectionModel().getSelectedIndex()).getName(), ingredientList.get(shoppingList.getSelectionModel().getSelectedIndex()).getUnit());
            } else {
                // if option selected in group shopping list
                try {
                    DatabaseConnection.deleteIngredientFromGroupShoppingList(otherListsMenu.getValue().getID(), ingredientList.get(shoppingList.getSelectionModel().getSelectedIndex()));
                } catch (IOException | SQLException err) {
                    err.printStackTrace();
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
        // remove all ingredient from shopping list
        if (otherListsMenu.getValue().getName().equals(privateGroup.getName())) {
            activeUser.removeShoppingList();
        } else {
            DatabaseConnection.deleteGroupShoppingList(otherListsMenu.getValue().getID());
        }
        ingredientList.clear();
        shoppingList.getItems().clear();
        shoppingList.refresh();
    }

    @FXML
    public void onExitButtonAction(){
        // return to proper window
        String path = "";
        if (Pattern.compile("MainPane").matcher(returnPane.getClass().getName()).find())
            path = "/mainPage.fxml";
        else if (Pattern.compile("RecipePane").matcher(returnPane.getClass().getName()).find())
            path = "/recipePage.fxml";
        else if (Pattern.compile("UserAdminPane").matcher(returnPane.getClass().getName()).find())
            path = "/userAdminPage.fxml";
        if (!path.equals("")) {
            FXMLLoader loader = loadFXML(returnPane, path);
            changeScene(exitButton, loader);
        }
    }

}