package main.userModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import main.controller.Status;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class User {
    private final String username;
    private List<Recipe> userRecipes;
    private List<Recipe> favorites;
    private List<String> followed;
    private List<String> userGroups;
    private List<String> newFollowed = new LinkedList<>();
    private List<String> deletedFollowed = new LinkedList<>();
    private List<Ingredient> shoppingList;
    private String defaultUnitSystem = null;
    public ObservableList<String> units = FXCollections.observableArrayList();
    public String getDefaultUnitSystem() {
        return defaultUnitSystem;
    }



    public User(String username) throws IOException, SQLException {
        this.username = username;
        this.userRecipes = new ArrayList<>();
        this.favorites = new LinkedList<>();
        this.followed = new LinkedList<>();
        this.shoppingList = new ArrayList<Ingredient>();
    }

    public ObservableList<String> getUnits() {
        return units;
    }

    public User(String argUsername, List<Recipe> userRecipes, List<Recipe> favorites, List<String> followed, ArrayList<Ingredient> shoppingList, List<String> userGroups, ObservableList<String> units) {
        username = argUsername;
        this.userRecipes = userRecipes;
        this.favorites = favorites;
        this.followed = followed;
        this.shoppingList = shoppingList;
        this.userGroups = userGroups;
        this.units = units;
    }

    public String getUsername() { return username; }

    public List<Recipe> getUserRecipes() { return userRecipes; }

    public List<Recipe> getAllFavorites() { return favorites; }
    public List<Recipe> getFavorites() {
        List<Recipe> trulyFavorite = new LinkedList<>();
        for (Recipe favorite : favorites) {
            if (favorite.getFavoriteStatus() != Status.deleted && favorite.getFavoriteStatus() != Status.none)
                trulyFavorite.add(favorite);
        }
        return trulyFavorite;
    }
    public void addFavorite(Recipe newFavRecipe) {
        Recipe foundRecipe = favorites.stream().filter(r -> r.getId().equals(newFavRecipe.getId())).findAny().orElse(null);
        if (foundRecipe == null){
            newFavRecipe.setFavoriteStatus(Status.added);
            favorites.add(newFavRecipe);
        } else if (foundRecipe.getFavoriteStatus() == Status.deleted){
            foundRecipe.setFavoriteStatus(Status.added);
        }
    }
    public void removeFavorite(Recipe oldFavRecipe) {
        favorites.stream().filter(r -> r.getId().equals(oldFavRecipe.getId())).findAny().ifPresent(foundRecipe -> foundRecipe.setFavoriteStatus(Status.deleted));
    }
    public boolean checkIfRecipeFavorite(Recipe recipe) {
        Recipe foundRecipe = favorites.stream().filter(r -> r.getId().equals(recipe.getId())).findAny().orElse(null);
        if (foundRecipe == null)
            return false;
        else return foundRecipe.getFavoriteStatus() == Status.added || foundRecipe.getFavoriteStatus() == Status.loaded;
    }

    public boolean checkIfFavContainsRecipe(Recipe recipe) {
        Recipe foundRecipe = favorites.stream().filter(r -> r.getId().equals(recipe.getId())).findAny().orElse(null);
        return foundRecipe != null;
    }

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
        ingredient.setShoppingListStatus(Status.added);
        shoppingList.add(ingredient);
    }

    public void removeFromShoppingList(Ingredient ingredient) {
        Ingredient delIngredient = getIngredientFromShoppingList(ingredient);
        delIngredient.setShoppingListStatus(Status.deleted);
    }

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
            assert foundIngredient != null;
            return foundIngredient.getShoppingListStatus();
        }
        else
            return null;
    }

    public Ingredient getIngredientFromShoppingList(Ingredient ingredient) {
        return shoppingList.stream().filter(lookingIngredient  -> lookingIngredient.equals(ingredient)).findAny().orElse(null);
    }

    public Ingredient getIngredientFromShoppingListWithID(int id){
        return shoppingList.stream().filter(lookingIngredient  -> lookingIngredient.getId().equals(id)).findAny().orElse(null);
    }

    public void setDefaultUnitSystem(String unitSystem) { defaultUnitSystem = unitSystem; System.out.println(unitSystem); }

    public Map<String, Ingredient> showShoppingList() {
        Map<String, Ingredient> showMap = new HashMap<>();
        for (Ingredient ingredient: shoppingList) {
            //@TODO MARIANKA zamiana składników na jednostke domyślną podczas dodawania
            if (ingredient.getShoppingListStatus() != Status.deleted) {
                Ingredient shopIngredient = showMap.get(ingredient.getName());
                if (shopIngredient != null && shopIngredient.getUnit().equals(ingredient.getUnit()) ){
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

    public void addUserRecipe(Recipe recipe) {
        userRecipes.add(recipe);
    }
}
