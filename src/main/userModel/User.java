package main.userModel;

import main.controller.Status;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;

import java.util.*;

public class User {
    private final String username;
    private List<Recipe> userRecipes;
    private List<Recipe> favorites;
    private List<User> followed;
    private List<Recipe> newFavorites;
    private List<Recipe> deletedFavorites;
    private Map<Integer, Ingredient> shoppingList;
    private String defaultUnitSystem;

    public User(String argUsername, List<Recipe> userRecipes, List<Recipe> favorites) {
        username = argUsername;
        this.userRecipes = userRecipes;
        this.favorites = favorites;
        followed = new LinkedList<>();
        shoppingList = new HashMap<Integer, Ingredient>();
//        defaultUnitSystem = argUnitSystem;
        newFavorites = new LinkedList<>();
        deletedFavorites = new LinkedList<>();
    }

    public User(String argUsername, List<Recipe> userRecipes, List<Recipe> favorites, Map<Integer, Ingredient> shoppingList) {
        username = argUsername;
        this.userRecipes = userRecipes;
        this.favorites = favorites;
        followed = new LinkedList<>();
        this.shoppingList = shoppingList;
//        defaultUnitSystem = argUnitSystem;
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
    public List<Recipe> getNewFavorites() {return newFavorites;}
    public List<Recipe> getDeletedFavorites() {return deletedFavorites;}
    public String getUsername() {return username;}
    public boolean checkIfRecipeFavorite(Recipe recipe) { return favorites.stream().anyMatch(r -> r.getId().equals(recipe.getId())); }
    public List<Recipe> getFavorites() {return favorites;}
    public void addToShoppingList(Ingredient ingredient) {shoppingList.put(ingredient.getId(), ingredient);}
    public void removeFromShoppingList(int ingredientId) {shoppingList.remove(ingredientId);}
    public boolean checkIfIngredientInShoppingList(int ingredientId) {
        if (shoppingList.containsKey(ingredientId)) {
            return shoppingList.get(ingredientId).getShoppingListStatus() != Status.deleted;
        } else
            return false;
    }
    public void setDefaultUnitSystem(String unitSystem) { defaultUnitSystem = unitSystem; System.out.println(unitSystem); }
    public Map<String, Ingredient> showShoppingList() {
        Map<String, Ingredient> showMap = new HashMap<>();
        for (Map.Entry<Integer, Ingredient> entry : shoppingList.entrySet()) {
            //@TODO zamiana składników na jednostke domyślną podczas dodawania
            Ingredient ingredient = entry.getValue();
            if (ingredient.getShoppingListStatus() != Status.deleted) {
                Ingredient shopIngredient = showMap.get(ingredient.getName());
                if (shopIngredient != null && shopIngredient.getUnit().getName().equals(ingredient.getUnit().getName()) ){
                    double quantity =  ingredient.getQuantity() + shopIngredient.getQuantity();
                    shopIngredient.setQuantity(quantity);
                }
                else {
                    showMap.put(ingredient.getName(), ingredient);
                }
            }
        }
        return showMap;
        }
    public Map<Integer, Ingredient> getShoppingList() {return shoppingList;}

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
