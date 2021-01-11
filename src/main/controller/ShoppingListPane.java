package main.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import main.DatabaseConnection;
import main.recipeModel.Ingredient;
import main.userModel.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ShoppingListPane extends BasicPaneActions {
    private final User activeUser;
    private final BasicPaneActions returnPane;
    private final List<String> groups;
    private String currentList = "User";

    @FXML private Button exitButton;
    @FXML private ImageView exitPic;
    @FXML private ListView<String> shoppingList;
    @FXML private MenuButton shareMenu;
    @FXML private MenuButton otherListsMenu;

    // TODO context menu dodanie opcji add ingredient to grouplist

    public ShoppingListPane(User activeUser, BasicPaneActions returnPane) throws IOException, SQLException {
        this.activeUser = activeUser;
        this.returnPane = returnPane;
        this.groups =  DatabaseConnection.getGroups(activeUser);
        this.groups.add("User");
    }

    @FXML
    void initialize() throws IOException, SQLException {
        if (DatabaseConnection.isThemeLight()) {
            exitPic.setImage(new Image("icons/berryExit.png"));
        }
        showShoppingList();
        Platform.runLater(()->{
            setShareMenu();
            setOtherListsMenu();
        });
    }

    private void setShareMenu() {
        if (currentList == "User") {
            for (String groupName : groups) {
                if (groupName != "User") {
                    MenuItem menuItem = new MenuItem(groupName);
                    menuItem.setOnAction(e -> {
                        try {
                            DatabaseConnection.shareList(activeUser, groupName);
                        } catch (IOException | SQLException ioException) {
                            ioException.printStackTrace();
                        }
                    });
                    shareMenu.getItems().add(menuItem);
                }
            }
        } else {
            shareMenu.hide();
        }
    }

    private void setOtherListsMenu() {
        for (String groupName : groups) {
            if (!groupName.equals(this.currentList)) {
                MenuItem menuItem = new MenuItem(groupName);
                menuItem.setOnAction(e -> {
                    menuItem.setText(String.format("%s", groupName));
                    currentList = groupName;
                    try {
                        showShoppingList();
                    } catch (IOException | SQLException ioException) {
                        ioException.printStackTrace();
                    }
                });
                otherListsMenu.getItems().add(menuItem);
            }
        }
    }

    private void showShoppingList() throws IOException, SQLException {
        shoppingList.getItems().clear();
        if (currentList.equals("User")) {
            for (Ingredient ingredient : activeUser.showShoppingList().values()) {
                shoppingList.getItems().add(String.format((ingredient.getQuantity() % 1 == 0)?"%1.0f %s %s":"%1.2f %s %s", ingredient.getQuantity(), ingredient.getUnit().getName(), ingredient.getName()));
            }
        } else {
            Map<Ingredient, String> ShoppingList = DatabaseConnection.getGroupShoppingList(activeUser, currentList);
            assert ShoppingList != null;
            for (Map.Entry<Ingredient, String> entry : ShoppingList.entrySet()) {
                String author = entry.getValue();
                Ingredient ingredient = entry.getKey();
                shoppingList.getItems().add(String.format((ingredient.getQuantity() % 1 == 0)?"%1.0f %s %s\t%s":"%1.2f %s %s\t%s", ingredient.getQuantity(), ingredient.getUnit().getName(), ingredient.getName(), author));
            }
        }
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
