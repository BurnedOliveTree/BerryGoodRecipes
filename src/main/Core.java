package main;

import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import main.recipeModel.Unit;
import main.userModel.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Core {
    private List<User> users;
    private List<Recipe> recipes;
    private List<Ingredient> ingredients;
    private List<Unit> units;

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
    public void addRecipe(String name, User author, String prepareMethod, boolean accessibility,  ArrayList<Ingredient> ingredientList) {
        recipes.add(new Recipe(name, author, prepareMethod, accessibility, ingredientList));
    }
    public void deleteRecipe(Recipe recipe) {
        recipes.remove(recipe);
    }
}
