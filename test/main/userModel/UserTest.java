package main.userModel;

import main.controller.Status;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import org.junit.Before;
import org.junit.Test;

public class UserTest {
    private User user;

    @Before
    public void createRecipe()  {
        this.user =  new User("test");
    }

    @Test
    public void addUserRecipe() {
        Recipe testRecipe = new Recipe();
        user.addUserRecipe(testRecipe);
        assert user.getUserRecipes().size() == 1;
    }

    @Test
    public void setDefaultUnitSystem() {
        String unitSystem = "metric";
        user.setDefaultUnitSystem(unitSystem);
        assert user.getDefaultUnitSystem().equals(unitSystem);
    }

    @Test
    public void addAndRemoveFavorite() {
        Recipe testRecipe = new Recipe(3, "Przepis", user.getUsername());
        user.addFavorite(testRecipe);
        assert user.checkIfRecipeFavorite(testRecipe);
        user.removeFavorite(testRecipe);
        assert !user.checkIfRecipeFavorite(testRecipe);
    }

    @Test
    public void followAndUnfollowUser() {
        String followedUsername = "followed";
        User followedUser = new User(followedUsername);
        user.followUser(followedUser.getUsername());
        assert user.getFollowed().contains(followedUsername);
        assert user.getNewFollowed().contains(followedUsername);
        user.unfollowUser(followedUser.getUsername());
        assert !user.getFollowed().contains(followedUsername);
        assert user.getDeletedFollowed().contains(followedUsername);
    }

    @Test
    public void addToShoppingListTest() {
        Ingredient ingredient = new Ingredient(1, 5.0, "gram", "mąka");
        user.addToShoppingList(ingredient);
        assert user.getShoppingList().size() == 1;
        assert ingredient.getShoppingListStatus() == Status.added;
    }

    @Test
    public void removeFromShoppingListTest() {
        Ingredient ingredient = new Ingredient(1, 5.0, "gram", "mąka");
        user.addToShoppingList(ingredient);
        assert user.getShoppingList().size() == 1;
        assert user.checkIfIngredientExistedInShoppingList(ingredient);
        user.removeFromShoppingList(ingredient);
        assert user.getShoppingList().size() == 1;
        assert user.checkIfIngredientExistedInShoppingList(ingredient);
        assert ingredient.getShoppingListStatus() == Status.deleted;
    }

}