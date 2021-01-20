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
import main.recipeModel.Unit;
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

    @FXML private Button exitButton;
    @FXML private ImageView exitPic;
    @FXML private ListView<String> shoppingList;
    @FXML private MenuButton shareMenu;
    @FXML private ChoiceBox<String> otherListsMenu;
    @FXML private MenuButton addIngredient;

    public ShoppingListPane(User activeUser, BasicPaneActions returnPane) {
        this.activeUser = activeUser;
        this.returnPane = returnPane;
        this.groups =  activeUser.getUserGroups();
        if (!this.groups.contains("User"))
            this.groups.add("User");
    }

    @FXML
    void initialize() throws IOException, SQLException {
        if (DatabaseConnection.isThemeLight()) {
            exitPic.setImage(new Image("icons/berryExit.png"));
        }
        showShoppingList("User");
        Platform.runLater(()->{
            setShareMenu("User");
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
        MenuButton unit = new MenuButton();
        TextField name = new TextField();
        Button addButton = new Button();
        quantity.setPromptText("Qty");
        name.setPromptText("Name");
        unit.setText("Unit");
        addButton.setText("Add Ingredient");
        quantity.setStyle("-fx-text-box-border: transparent");
        name.setStyle("-fx-text-box-border: transparent");
        quantity.setAlignment(Pos.CENTER_RIGHT);
        name.setAlignment(Pos.CENTER_RIGHT);
        unit.setAlignment(Pos.CENTER_RIGHT);
        newIngredient.setPrefWidth(addIngredient.getPrefWidth() - 10);
        unit.setPrefWidth(newIngredient.getPrefWidth());
        addButton.setPrefWidth(newIngredient.getPrefWidth());
        addButton.setOnAction(actionEvent -> {
            // @TODO other shopping list
            if (!quantity.getText().equals("") && !name.getText().equals("")) {
                // @TODO available unit show
                Ingredient ingredient = new Ingredient(null, Double.parseDouble(quantity.getText()), new Unit("gram"), name.getText());
                ingredient.setShoppingListStatus(Status.added);
                activeUser.addToShoppingList(ingredient);
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
        otherListsMenu.getSelectionModel().select(groups.size()-1);
        otherListsMenu.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            setShareMenu(groups.get(t1.intValue()));
            try {
                showShoppingList(groups.get(t1.intValue()));
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void showShoppingList(String currentList) throws IOException, SQLException {
        shoppingList.getItems().clear();
        ingredientList.clear();
        if (currentList.equals("User")) {
            for (Ingredient ingredient : activeUser.showShoppingList().values()) {
                ingredientList.add(ingredient);
                shoppingList.getItems().add(String.format((ingredient.getQuantity() % 1 == 0) ? "%1.0f %s %s" : "%1.2f %s %s", ingredient.getQuantity(), ingredient.getUnit().getName(), ingredient.getName()));
            }
        } else {
            Map<Ingredient, String> ShoppingList = DatabaseConnection.getGroupShoppingList(activeUser, currentList);
            assert ShoppingList != null;
            for (Map.Entry<Ingredient, String> entry : ShoppingList.entrySet()) {
                String author = entry.getValue();
                Ingredient ingredient = entry.getKey();
                ingredientList.add(ingredient);
                shoppingList.getItems().add(String.format((ingredient.getQuantity() % 1 == 0) ? "%1.0f %s %s\t%s" : "%1.2f %s %s\t%s", ingredient.getQuantity(), ingredient.getUnit().getName(), ingredient.getName(), author));
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
            shoppingList.getItems().remove(shoppingList.getSelectionModel().getSelectedIndex());
            shoppingList.refresh();
        });
        return delete;
    }



    @FXML   // return to proper window
    public void onExitButtonAction(){
        String path;
        if (Pattern.compile("MainPane").matcher(returnPane.getClass().getName()).find())
            path = "/resources/mainPage.fxml";
        else
            path = "/resources/recipePage.fxml";
        FXMLLoader loader = loadFXML(returnPane, path);
        changeScene(exitButton, loader);
    }

}
