package BerryGoodRecipes.recipeModel;

import org.junit.Before;
import org.junit.Test;

public class IngredientTest {
    private Ingredient ingredient;

    @Before
    public void createIngredient() {
        this.ingredient = new Ingredient(1, 5.0, "gram", "mąka");
    }

    @Test
    public void equalsTest() {
        assert this.ingredient.equals(new Ingredient(this.ingredient));
        assert this.ingredient.equals(new Ingredient(null, 5.0, "gram", "mąka"));
        assert !this.ingredient.equals(new Ingredient(2, 5.0, "gram", "mąka wrocławska"));
    }


}
