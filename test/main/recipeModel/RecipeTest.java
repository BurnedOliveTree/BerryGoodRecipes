package main.recipeModel;

import main.recipeModel.Recipe;
import org.junit.Test;

public class RecipeTest {
    public Recipe createRecipe(int id, String name, String author)  {
        return new Recipe(id, name, author);
    }

}
