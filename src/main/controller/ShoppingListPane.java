package main.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import main.DatabaseConnection;
import main.recipeModel.Ingredient;
import main.userModel.User;

import java.util.regex.Pattern;

public class ShoppingListPane extends BasicPaneActions {
    private final User activeUser;
    private final Object returnPane;

    @FXML private Button exitButton;
    @FXML private ImageView exitPic;
    @FXML private ListView<String> shoppingList;

    public ShoppingListPane(User activeUser, Object returnPane) {
        this.activeUser = activeUser;
        this.returnPane = returnPane;
    }

    @FXML
    void initialize() {
        if (DatabaseConnection.theme.equals("light") || DatabaseConnection.theme.equals("winter")) {
            exitPic.setImage(new Image("icons/berryExit.png"));
        }
        for (Ingredient ingredient : activeUser.showShoppingList().values()) {
            if (ingredient.getQuantity() % 1 == 0)
                shoppingList.getItems().add(String.format("%d %s %s", (int)Math.round(ingredient.getQuantity()), ingredient.getUnit().getName(), ingredient.getName()));
            else
                shoppingList.getItems().add(String.format("%f %s %s", ingredient.getQuantity(), ingredient.getUnit().getName(), ingredient.getName()));
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
