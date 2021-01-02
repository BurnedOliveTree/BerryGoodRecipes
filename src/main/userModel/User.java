package main.userModel;

import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

public class User {
    private String username;
    private String password; //@TODO tego tutaj nie trzymamy Ksawery
    private List<Recipe> userRecipes;
    private List<Recipe> favorites;
    private List<User> followed;
    private List<Recipe> newFavorites;
    private List<Recipe> deletedFavorites;
    // TODO private UnitSystem defaultUnitSystem;
    private Map<Integer, Ingredient> shoppingList;

    public User(String argUsername, String argPassword, List<Recipe> userRecipes, List<Recipe> favorites) {
        username = argUsername;
        password = argPassword;
        this.userRecipes = userRecipes;
        this.favorites = favorites;
        followed = new LinkedList<>();
        // TODO defaultUnitSystem = argUnitSystem;
        shoppingList = new HashMap<Integer, Ingredient>();
        newFavorites = new LinkedList<>();
        deletedFavorites = new LinkedList<>();
    }
    public void setNewUsername(String newUsername) {
        username = newUsername;
    }
    public void setNewPassword(String newPassword) {
        password = newPassword;
    }
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
    public List<Recipe> getNewFavorites() {return newFavorites;}
    public List<Recipe> getDeletedFavorites() {return deletedFavorites;}
    public String getUsername() {return username;}
    public boolean checkIfRecipeFavorite(Recipe recipe) { return favorites.stream().anyMatch(r -> r.getId().equals(recipe.getId())); }
    public List<Recipe> getFavorites() {return favorites;}
    public void addToShoppingList(Ingredient ingredient) {shoppingList.put(ingredient.getId(), ingredient);}
    public void removeFromShoppingList(int ingredientId) {shoppingList.remove(ingredientId);}
    public boolean checkIfIngredientInShoppingList(int ingredientId) {return shoppingList.get(ingredientId) != null;}
//
//    public void saveToFile()
//    {
//        try {
//            FileWriter file = new FileWriter("shoppinglist.txt");
//            for (Ingredient ingredient: this.shoppingList){
//                file.write(ingredient.getQuantity() + ingredient.getUnit().toString() + ingredient.getName());
//            }
//            file.close();
//
//        } catch (IOException err) {
//            System.out.println("Error: ");
//            err.printStackTrace();
//    }
//    }
}
