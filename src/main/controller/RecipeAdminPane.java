package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import main.recipeModel.Recipe;
import main.userModel.User;


import java.util.List;

public class RecipeAdminPane {
    private User activeUser;
    @FXML
    private ListView<String> myRecipesList;

    public RecipeAdminPane( User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML
    void initialize() {
        for (Recipe recipe: activeUser.getUserRecipes()) {
            myRecipesList.getItems().add(String.format("%s\t%s\t%s", recipe.getName(), recipe.getGroupName(), recipe.getDateAdded()));
        }
    }

}
