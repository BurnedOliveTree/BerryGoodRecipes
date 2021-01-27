package main.recipeModel;

import main.recipeModel.Recipe;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class RecipeTest {
    private Recipe recipe;

    @Before
    public void createRecipe()  {
        this.recipe =  new Recipe(1, "Placki", "Rokarolka");
    }

    @Test
    public void fileOperationsTest() {
        recipe.saveToFile("./savedRecipes/test_recipe.txt");
        assert Files.exists(Paths.get("./savedRecipes/test_recipe.txt"));
        boolean done = recipe.deleteFile("./savedRecipes/test_recipe.txt");
        assert done;
    }

    @Test
    public void equalsTest() {
        assert recipe.equals(new Recipe(1, "Placki", "Rokarolka"));
        assert !recipe.equals(new Recipe(2, "Placki", "Rokarolka"));
    }

    @Test
    public void scaleIngredientListWithoutIngredientTest() {
        // if nothing in ingredient list
        double newNumPortions = 5;
        double portions = recipe.getPortionNumber();
        recipe.scaleIngredientList(newNumPortions);
        assert recipe.getPortionNumber() == newNumPortions;
    }
    @Test
    public void scaleIngredientListTest() {
        // if nothing in ingredient list
        ArrayList<Double> oldQuantity = new ArrayList<>();
        recipe.addIngredient(new Ingredient(null,  5.0, "gram", "mąka"));
        for (Ingredient ingredient: recipe.getIngredientList()) {
            oldQuantity.add(ingredient.getQuantity());
        }
        double newNumPortions = 5;
        double scale = recipe.scaleIngredientList(newNumPortions);
        assert recipe.getPortionNumber() == newNumPortions;
        ArrayList<Double> newQuantity = new ArrayList<>();
        for (Ingredient ingredient: recipe.getIngredientList()){
            newQuantity.add(ingredient.getQuantity());
        }

        for (int i = 0; i < recipe.getIngredientList().size(); i++) {
            assert scale * oldQuantity.get(i) == newQuantity.get(i);
        }
    }

    @Test
    public void setIngredientList() {
        ArrayList<Ingredient> ingredientList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ingredientList.add(new Ingredient(null,5.0, "gram", "mąka"));
        }
        recipe.setIngredientList(ingredientList);
        assert recipe.getIngredientList().size() == 1;
    }
}
