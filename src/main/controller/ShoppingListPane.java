package main.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import main.recipeModel.Ingredient;
import main.userModel.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShoppingListPane extends OrdinaryButtonAction {
    private final User activeUser;
    private final Object returnPane;

    @FXML
    private Button exitButton;
    @FXML
    private ListView<String> shoppingList;

    public ShoppingListPane(User activeUser, Object returnPane) {
        this.activeUser = activeUser;
        this.returnPane = returnPane;
    }

    @FXML
    void initialize() {
        for (Ingredient ingredient : activeUser.showShoppingList().values()) {
            if (ingredient.getQuantity() % 1 == 0)
                shoppingList.getItems().add(String.format("%d %s %s", (int)Math.round(ingredient.getQuantity()), ingredient.getUnit().getName(), ingredient.getName()));
            else
                shoppingList.getItems().add(String.format("%f %s %s", ingredient.getQuantity(), ingredient.getUnit().getName(), ingredient.getName()));
        }
    }

    @FXML   // return to proper window
    public void onExitButtonAction(){
        FXMLLoader loader;
        if (Pattern.compile("MainPane").matcher(returnPane.getClass().getName()).find())
            loader = new FXMLLoader(getClass().getResource("/resources/mainPage.fxml"));
        else
            loader = new FXMLLoader(getClass().getResource("/resources/recipePage.fxml"));
        loader.setController(returnPane);
        changeScene(exitButton, loader);
    }
}
