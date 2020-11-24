package main.userModel;

import main.recipeModel.Recipe;
import main.recipeModel.Unit;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public class User {
    private final int ID;
    private String username;
    private String password;
    private List<Recipe> userRecipes;
    private Map<Recipe, String> favorites;
    private List<User> followed;
    // TODO private UnitSystem defaultUnitSystem;
    private ShoppingList shoppingList;

    public User(int argID, String argUsername, String argPassword) {
        ID = argID;
        username = argUsername;
        password = argPassword;
        userRecipes = new LinkedList<>();
        favorites = new HashMap<>();
        followed = new LinkedList<>();
        // TODO defaultUnitSystem = argUnitSystem;
        shoppingList = new ShoppingList();
    }
    public void setNewUsername(String newUsername) {
        username = newUsername;
    }
    public void setNewPassword(String newPassword) {
        password = newPassword;
    }
    public void addRecipe(Recipe newRecipe) {
        userRecipes.add(newRecipe);
    }
    public void removeRecipe(Recipe oldRecipe) {
        userRecipes.remove(oldRecipe);
    }
    public void addFavorite(Recipe newFavRecipe) {
        favorites.put(newFavRecipe, "");
    }
    public void removeFavorite(Recipe oldFavRecipe) {
        favorites.remove(oldFavRecipe);
    }
    public void followUser(User newFollowedUser) {
        followed.add(newFollowedUser);
    }
    public void unfollowUser(User oldFollowedUser) {
        followed.remove(oldFollowedUser);
    }
    public void addToShoppingList(Integer quantity, Unit unit, String ingredientName) {
        shoppingList.addToShoppingList(quantity, unit, ingredientName); // basic unit is always the same
    }
    public void removeFromShoppingList(Integer quantity, String ingredientName) {
        shoppingList.removeFromShoppingList(quantity, ingredientName);
    }
}
