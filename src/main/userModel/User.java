package main.userModel;

import main.controller.Status;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;

import java.nio.channels.MulticastChannel;
import java.util.*;

public class User {
    private final String username;
    private List<Recipe> userRecipes;
    private List<Recipe> favorites;
    private List<Recipe> newFavorites = new LinkedList<>();
    private List<Recipe> deletedFavorites = new LinkedList<>();
    private List<String> followed;
    private List<String> userGroups;
    private List<String> newFollowed = new LinkedList<>();
    private List<String> deletedFollowed = new LinkedList<>();
    private List<Ingredient> shoppingList;
    private String defaultUnitSystem = null;

    public String getDefaultUnitSystem() {
        return defaultUnitSystem;
    }



    public User(String username) {
        this.username = username;
        this.userRecipes = new ArrayList<>();
        this.favorites = new LinkedList<>();
        this.followed = new LinkedList<>();
        this.shoppingList = new ArrayList<Ingredient>();
    }

    public User(String argUsername, List<Recipe> userRecipes, List<Recipe> favorites, List<String> followed, ArrayList<Ingredient> shoppingList, List<String> userGroups) {
        username = argUsername;
        this.userRecipes = userRecipes;
        this.favorites = favorites;
        this.followed = followed;
        this.shoppingList = shoppingList;
        this.userGroups = userGroups;

    }

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
    public List<String> getUserGroups() { return userGroups; }
    public List<String> getNewFollowed() { return newFollowed; }
    public List<String> getDeletedFollowed() { return deletedFollowed; }

    public void addToShoppingList(Ingredient ingredient) {
        shoppingList.add(ingredient);}
    public void removeFromShoppingList(Ingredient ingredient) {
        shoppingList.remove(ingredient);}
    public void removeShoppingList() {
        for (Ingredient ingredient: shoppingList)
            ingredient.setShoppingListStatus(Status.deleted);
    }

    public boolean checkIfIngredientInShoppingList(Ingredient ingredient) {
        return shoppingList.contains(ingredient);
    }

    public void setShoppingList(List<Ingredient> shoppingList) {this.shoppingList = shoppingList;}

    public Status getIngredientStatus(Ingredient ingredient) {
        if (shoppingList.contains(ingredient)){
            Ingredient foundIngredient = shoppingList.stream().filter(lookingIngredient  -> lookingIngredient.equals(ingredient)).findAny().orElse(null);
            return foundIngredient.getShoppingListStatus();
        }
        else
            return null;
    }

    public Ingredient getIngredientFromShoppingList(Ingredient ingredient) {
        return shoppingList.stream().filter(lookingIngredient  -> lookingIngredient.equals(ingredient)).findAny().orElse(null);
    }

    public void setDefaultUnitSystem(String unitSystem) { defaultUnitSystem = unitSystem; System.out.println(unitSystem); }
    public Map<String, Ingredient> showShoppingList() {
        Map<String, Ingredient> showMap = new HashMap<>();
        for (Ingredient ingredient: shoppingList) {
            //@TODO MARIANKA zamiana składników na jednostke domyślną podczas dodawania
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
    public List<Ingredient> getShoppingList() {return shoppingList;}

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
