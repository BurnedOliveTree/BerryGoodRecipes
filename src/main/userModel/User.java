package main.userModel;

import main.recipeModel.Recipe;
import main.recipeModel.Unit;

import java.util.List;
import java.util.LinkedList;

public class User {
    private String username;
    private List<Recipe> userRecipes;
    private List<Recipe> favorites;
    private List<User> followed;
    private List<Recipe> newFavorites;
    private List<Recipe> deletedFavorites;
    private String defaultUnitSystem;
    private ShoppingList shoppingList;

    public User(String argUsername, List<Recipe> userRecipes, List<Recipe> favorites) {
        username = argUsername;
        this.userRecipes = userRecipes;
        this.favorites = favorites;
        followed = new LinkedList<>();
//        defaultUnitSystem = argUnitSystem;
        shoppingList = new ShoppingList();
        newFavorites = new LinkedList<>();
        deletedFavorites = new LinkedList<>();
    }
//    public void setNewPassword(String newPassword) {
//        password = newPassword;
//    }
//    public void addRecipe(Recipe newRecipe) {
//        userRecipes.add(newRecipe);
//    }
//    public void removeRecipe(Recipe oldRecipe) {
//        userRecipes.remove(oldRecipe);
//    }
    // @TODO przenieść powyższe funkcje do BD

    public List<Recipe> getUserRecipes() {return userRecipes;}
    public void addFavorite(Recipe newFavRecipe) {
        favorites.add(newFavRecipe);
        newFavorites.add(newFavRecipe);
        deletedFavorites.remove(newFavRecipe);
    }
    public void removeFavorite(Recipe oldFavRecipe) {
        favorites.remove(oldFavRecipe);
        deletedFavorites.add(oldFavRecipe);
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
    public List<Recipe> getNewFavorites() {return newFavorites;}
    public List<Recipe> getDeletedFavorites() {return deletedFavorites;}
    public String getUsername() {return username;}
    public boolean checkIfRecipeFavorite(Recipe recipe) {
        return favorites.stream().anyMatch(r -> r.getId().equals(recipe.getId()));
    }
    public List<Recipe> getFavorites() {return favorites;}
}
