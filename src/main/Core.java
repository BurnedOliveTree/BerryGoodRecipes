package main;

import main.recipeModel.Recipe;
import main.userModel.User;

import java.util.LinkedList;
import java.util.List;

public class Core {
    private List<User> users;
    private List<Recipe> recipes;
    // TODO private List<Ingredient> ingredients;
    // TODO private List<Unit> units;

    public Core() {
        users = new LinkedList<>();
        recipes = new LinkedList<>();
    }
    public void addAccount(int argID, String argUsername, String argPassword) {
        users.add(new User(argID, argUsername, argPassword));
    }
    public void deleteAccount(User account) {
        users.remove(account);
    }
    public void addRecipe() {
        recipes.add(new Recipe()); // TODO
    }
    public void deleteRecipe(Recipe recipe) {
        recipes.remove(recipe);
    }
}
