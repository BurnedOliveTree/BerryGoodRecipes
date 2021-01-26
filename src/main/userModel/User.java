package main.userModel;

import main.DatabaseConnection;
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
    private final List<String> newFollowed = new LinkedList<>();
    private final List<String> deletedFollowed = new LinkedList<>();
    private List<Group> userGroups;
    private List<Ingredient> shoppingList;
    private String defaultUnitSystem = null;
    public ArrayList<String> units = new ArrayList<>();

    // constructor

    public User(String username) {
        this.username = username;
        this.userRecipes = new ArrayList<>();
        this.favorites = new LinkedList<>();
        this.followed = new LinkedList<>();
        this.userGroups = new LinkedList<>();
        this.shoppingList = new ArrayList<>();
    }

    public User(String argUsername, List<Recipe> userRecipes, List<Recipe> favorites, List<String> followed, ArrayList<Ingredient> shoppingList, List<Group> userGroups, ArrayList<String> units) {
        username = argUsername;
        this.userRecipes = userRecipes;
        this.favorites = favorites;
        this.followed = followed;
        this.shoppingList = shoppingList;
        this.userGroups = userGroups;
        this.units = units;
    }

    // operations on attributes

    // insertion

    public void addUserRecipe(Recipe recipe) {
        userRecipes.add(recipe);
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

    public void addToShoppingList(Ingredient ingredient) {
        ingredient.setShoppingListStatus(Status.added);
        shoppingList.add(ingredient);
    }

    // deletion

    public void removeFavorite(Recipe oldFavRecipe) {
        favorites.stream().filter(r -> r.getId().equals(oldFavRecipe.getId())).findAny().ifPresent(foundRecipe -> foundRecipe.setFavoriteStatus(Status.deleted));
    }

    public void removeFromShoppingList(Ingredient ingredient) {
        Ingredient delIngredient = getIngredientFromShoppingList(ingredient);
        delIngredient.setShoppingListStatus(Status.deleted);
    }

    public void removeSameNamedFromSL(String name, String unit){
        for (Ingredient ing : shoppingList){
            if (ing.getName().equals(name) && ing.getUnit().equals(unit)){
                ing.setShoppingListStatus(Status.deleted);
            }
        }
    }

    public void removeShoppingList() {
        for (Ingredient ingredient: shoppingList)
            ingredient.setShoppingListStatus(Status.deleted);
    }

    public void deleteUserRecipe(Recipe recipe) {
        if (userRecipes.contains(recipe)) {
            userRecipes.remove(recipe);
        }
    }

    // check

    public boolean checkIfIngredientInShoppingList(Ingredient ingredient) {
        return shoppingList.contains(ingredient);
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

    // others

    public Map<String, Ingredient> showShoppingList() throws IOException, SQLException {
        // prepare ingredient from shopping list to show
        Map<String, Ingredient> showMap = new HashMap<>();
        int i = 0;
        for (Ingredient ingredient: shoppingList) {
            if (ingredient.getShoppingListStatus() != Status.deleted) {
                i++;
                Ingredient shopIngredient = showMap.get(ingredient.getName());
                if (shopIngredient != null){
                    if (ingredient.getUnit().equals("piece") && shopIngredient.getUnit().equals("piece")){ // seperate sum for piece
                        shopIngredient.setQuantity(ingredient.getQuantity() + shopIngredient.getQuantity());
                    }
                    else if (ingredient.getUnit().equals("piece") && !shopIngredient.getUnit().equals("piece")){ //seperate case when there is already that ingredient in shopping list, and we want to add another in unit piece
                        showMap.put(ingredient.getName() + Integer.toString(i), new Ingredient(ingredient.getId(), ingredient.getQuantity(), ingredient.getUnit(), ingredient.getName()));
                    }
                    else if (!ingredient.getUnit().equals("piece") && shopIngredient.getUnit().equals("piece")){ // separate case when there is already that ingredient in shopping list in unit piece and user wants to ad another i diffrent unit
                        showMap.put(ingredient.getName() + Integer.toString(i), new Ingredient(ingredient.getId(), ingredient.getQuantity(), ingredient.getUnit(), ingredient.getName()));
                    }
                    else { // when ingredient is in shoppping list already, an both are not in unit piece
                        double quantity = DatabaseConnection.convertUnit(ingredient.getQuantity(), ingredient.getUnit(), "gram") + DatabaseConnection.convertUnit(shopIngredient.getQuantity(), shopIngredient.getUnit(), "gram");
                        shopIngredient.setQuantity(quantity);
                        shopIngredient.setUnit("gram");
                    }
                }
                else {
                    showMap.put(ingredient.getName(), new Ingredient(ingredient.getId(), ingredient.getQuantity(), ingredient.getUnit(), ingredient.getName()));
                }
            }
        }
        return showMap;
    }

    public void followUser(String newFollowedUser) {
        if (!followed.contains(newFollowedUser)) {
            followed.add(newFollowedUser);
            newFollowed.add(newFollowedUser);
            deletedFollowed.remove(newFollowedUser);
        }
    }

    public void unfollowUser(String oldFollowedUser) {
        followed.remove(oldFollowedUser);
        newFollowed.remove(oldFollowedUser);
        deletedFollowed.add(oldFollowedUser);
    }

    public Boolean qualifiesToAdd(String name){
        for (Ingredient ing : shoppingList){
            if (ing.getName().equals(name) && !ing.getUnit().equals("piece")){
                return true;
            }
        }
        return false;
    }

    public void editQuantityInShopping(String name, Double q) throws IOException, SQLException {
        for (Ingredient ing : shoppingList){
            if (ing.getName().equals(name)  && !ing.getUnit().equals("piece")){
                Double newQ = DatabaseConnection.convertUnit(q, "gram", ing.getUnit());
                ing.setQuantity(ing.getQuantity() + newQ);
                ing.setShoppingListStatus(Status.edited);
                return;
            }
        }
    }

    // getters

    public List<String> getFollowed() {
        return followed;
    }

    public String getUsername() {
        return username;
    }

    public List<Recipe> getUserRecipes() {
        return userRecipes;
    }

    public ArrayList<String> getUnits() {
        return units;
    }

    public List<Group> getUserGroups() {
        return userGroups;
    }

    public List<Recipe> getAllFavorites() {
        return favorites;
    }

    public List<Recipe> getFavorites() {
        List<Recipe> trulyFavorite = new LinkedList<>();
        for (Recipe favorite : favorites) {
            if (favorite.getFavoriteStatus() != Status.deleted && favorite.getFavoriteStatus() != Status.none)
                trulyFavorite.add(favorite);
        }
        return trulyFavorite;
    }

    public String getDefaultUnitSystem() {
        return defaultUnitSystem;
    }

    public Status getIngredientStatus(Ingredient ingredient) {
        if (shoppingList.contains(ingredient)) {
            Ingredient foundIngredient = getIngredientFromShoppingList(ingredient);
            assert foundIngredient != null;
            return foundIngredient.getShoppingListStatus();
        }
        else
            return null;
    }

    public Ingredient getIngredientFromShoppingList(Ingredient ingredient) {
        return shoppingList.stream().filter(lookingIngredient -> lookingIngredient.equals(ingredient)).findAny().orElse(null);
    }

    public Ingredient getIngredientFromShoppingListWithID(int id){
        return shoppingList.stream().filter(lookingIngredient -> lookingIngredient.getId().equals(id)).findAny().orElse(null);
    }

    public List<String> getNewFollowed() {
        return newFollowed;
    }

    public List<String> getDeletedFollowed() {
        return deletedFollowed;
    }

    // setter

    public void setShoppingList(List<Ingredient> shoppingList) {
        this.shoppingList = shoppingList;
    }

    public void setUserGroups(List<Group> groups) {
        this.userGroups = groups;
    }

    public void setDefaultUnitSystem(String unitSystem) {
        defaultUnitSystem = unitSystem;
    }

    public List<Ingredient> getShoppingList() {
        return shoppingList;
    }
}
