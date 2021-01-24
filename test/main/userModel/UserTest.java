package main.userModel;

import main.recipeModel.Recipe;
import org.junit.Test;

public class UserTest {
    public User createUser() {
        return new User("test");
    }

    @Test
    public void constructor() {
        String username = "test";
        User testUser = new User(username);
        assert testUser.getUsername().equals(username);
    }

    @Test
    public void recipe() {
        User testUser = createUser();
        Recipe testRecipe = new Recipe();
        testUser.addUserRecipe(testRecipe);
        assert testUser.getUserRecipes().size() == 1;
    }

    @Test
    public void unitSystem() {
        User testUser = createUser();
        String unitSystem = "metric";
        testUser.setDefaultUnitSystem(unitSystem);
        assert testUser.getDefaultUnitSystem().equals(unitSystem);
    }

    @Test
    public void favorite() {
        // TODO
        assert true;
    }

    @Test
    public void followed() {
        // TODO
        assert true;
    }

    @Test
    public void shoppingList() {
        // TODO
        assert true;
    }
}