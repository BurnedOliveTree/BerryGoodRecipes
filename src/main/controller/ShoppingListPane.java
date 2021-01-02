package main.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import main.recipeModel.Ingredient;
import main.userModel.User;

public class ShoppingListPane extends OrdinaryButtonAction {
    private final User activeUser;
    private final Object returnPane;

    @FXML
    public Button exitButton;
    public ListView<String> shoppingList;

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

    @FXML
    public void onExitButtonAction(){
        FXMLLoader loader;
        if (returnPane.getClass().getName() == "MainPane")
            loader = new FXMLLoader(getClass().getResource("/resources/mainPage.fxml"));
        else
            loader = new FXMLLoader(getClass().getResource("/resources/recipePage.fxml"));
        loader.setController(returnPane);
        changeScene(exitButton, loader);
    }
}
