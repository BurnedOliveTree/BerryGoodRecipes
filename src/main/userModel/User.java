package main.userModel;

import main.recipeModel.Recipe;
import main.recipeModel.Unit;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public class User {
    private String username;
    private String password;
    private List<Recipe> userRecipes;
    private List<Integer> favorites;
    private List<User> followed;
    private List<Integer> newFavorites;
    private List<Integer> deletedFavorites;
    // TODO private UnitSystem defaultUnitSystem;
    private ShoppingList shoppingList;

    public User(String argUsername, String argPassword) {
        username = argUsername;
        password = argPassword;
        userRecipes = new LinkedList<>();
        favorites = new LinkedList<>();
        followed = new LinkedList<>();
        // TODO defaultUnitSystem = argUnitSystem;
        shoppingList = new ShoppingList();
        newFavorites = new LinkedList<>();
        deletedFavorites = new LinkedList<>();
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
    public void addFavorite(Integer newFavRecipe) {
        favorites.add(newFavRecipe);
        newFavorites.add(newFavRecipe);
        if (deletedFavorites.contains(newFavRecipe))
            deletedFavorites.remove(newFavRecipe);
    }
    public void removeFavorite(Integer oldFavRecipe) {
        favorites.remove(oldFavRecipe);
        deletedFavorites.add(oldFavRecipe);
        if (newFavorites.contains(oldFavRecipe))
            newFavorites.remove(oldFavRecipe);

    }
    public void followUser(User newFollowedUser) {
        followed.add(newFollowedUser);
    }
    public void unfollowUser(User oldFollowedUser) {
        followed.remove(oldFollowedUser);
    }
    public void addToShoppingList(Double quantity, Unit unit, String ingredientName) {
        shoppingList.addToShoppingList(quantity, unit, ingredientName); // basic unit is always the same
    }
    public void removeFromShoppingList(Integer quantity, String ingredientName) {
        shoppingList.removeFromShoppingList(quantity, ingredientName);
    }
    public List<Integer> getNewFavorites() {return newFavorites;}
    public String getUsername() {return username;}
    public boolean checkIfRecipeFavorite(int recipe_id) {
        if (favorites.contains(recipe_id))
            return true;
        else
            return false;
    };
}
