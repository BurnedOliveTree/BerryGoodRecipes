package main.userModel;

import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;

import java.util.*;

public class User {
    private final String username;
    private List<Recipe> userRecipes;
    private List<Recipe> favorites;
    private List<Recipe> newFavorites = new LinkedList<>();
    private List<Recipe> deletedFavorites = new LinkedList<>();
    private List<String> followed;
    private List<String> newFollowed = new LinkedList<>();
    private List<String> deletedFollowed = new LinkedList<>();
    private Map<Integer, Ingredient> shoppingList;
    private String defaultUnitSystem = null;

    public User(String username) {
        this.username = username;
        this.userRecipes = new ArrayList<>();
        this.favorites = new LinkedList<>();
        this.followed = new LinkedList<>();
        shoppingList = new HashMap<Integer, Ingredient>();
    }

    public User(String argUsername, List<Recipe> userRecipes, List<Recipe> favorites, List<String> followed) {
        username = argUsername;
        this.userRecipes = userRecipes;
        this.favorites = favorites;
        this.followed = followed;
        shoppingList = new HashMap<Integer, Ingredient>();
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

    public String getUsername() { return username; }

    public List<Recipe> getUserRecipes() { return userRecipes; }

    public List<Recipe> getFavorites() { return favorites; }
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
    public List<Recipe> getNewFavorites() { return newFavorites; }
    public List<Recipe> getDeletedFavorites() { return deletedFavorites; }
    public boolean checkIfRecipeFavorite(Recipe recipe) { return favorites.stream().anyMatch(r -> r.getId().equals(recipe.getId())); }

    public List<String> getFollowed() {return followed;}
    public void followUser(String newFollowedUser) {
        followed.add(newFollowedUser);
        newFollowed.add(newFollowedUser);
        deletedFollowed.remove(newFollowedUser);
    }
    public void unfollowUser(String oldFollowedUser) {
        followed.remove(oldFollowedUser);
        newFollowed.remove(oldFollowedUser);
        deletedFollowed.add(oldFollowedUser);
    }
    public List<String> getNewFollowed() { return newFollowed; }
    public List<String> getDeletedFollowed() { return deletedFollowed; }

    public void addToShoppingList(Ingredient ingredient) {shoppingList.put(ingredient.getId(), ingredient);}
    public void removeFromShoppingList(int ingredientId) {shoppingList.remove(ingredientId);}
    public boolean checkIfIngredientInShoppingList(int ingredientId) {return shoppingList.containsKey(ingredientId);}
    public void setDefaultUnitSystem(String unitSystem) { defaultUnitSystem = unitSystem; System.out.println(unitSystem); }
    public Map<String, Ingredient> showShoppingList() {
        Map<String, Ingredient> showMap = new HashMap<>();
        for (Map.Entry<Integer, Ingredient> entry : shoppingList.entrySet()) {
            //@TODO zamiana składników na jednostke domyślną podczas dodawania
            Ingredient ingredient = entry.getValue();
            Ingredient shopIngredient = showMap.get(ingredient.getName());
            if (shopIngredient != null && shopIngredient.getUnit().getName().equals(ingredient.getUnit().getName()) ){
                double quantity =  ingredient.getQuantity() + shopIngredient.getQuantity();
                shopIngredient.setQuantity(quantity);
            }
            else {
                showMap.put(ingredient.getName(), ingredient);
            }
        }
        return showMap;
        }
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
