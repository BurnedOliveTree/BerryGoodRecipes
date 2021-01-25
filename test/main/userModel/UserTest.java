package main.userModel;

import main.recipeModel.Recipe;
import org.junit.Test;

public class UserTest {
    public User createUser() {
        return new User("test");
    }

    public User createUser(String username) {
        return new User(username);
    }

    @Test
    public void constructor() {
        String username = "test";
        User testUser = new User(username);
        assert testUser.getUsername().equals(username);
    }

    @Test
    public void addUserRecipe() {
        User testUser = createUser();
        Recipe testRecipe = new Recipe();
        testUser.addUserRecipe(testRecipe);
        assert testUser.getUserRecipes().size() == 1;
    }

    @Test
    public void setDefaultUnitSystem() {
        User testUser = createUser();
        String unitSystem = "metric";
        testUser.setDefaultUnitSystem(unitSystem);
        assert testUser.getDefaultUnitSystem().equals(unitSystem);
    }

    @Test
    public void addAndRemoveFavorite() {
        User testUser = createUser();
        Recipe testRecipe = new Recipe(3, "Przepis", testUser.getUsername());
        testUser.addFavorite(testRecipe);
        assert testUser.checkIfRecipeFavorite(testRecipe);
        testUser.removeFavorite(testRecipe);
        assert !testUser.checkIfRecipeFavorite(testRecipe);
    }

    @Test
    public void followAndUnfollowUser() {
        User testUser = createUser();
        String followedUsername = "followed";
        User followedUser = createUser(followedUsername);
        testUser.followUser(followedUser.getUsername());
        assert testUser.getFollowed().contains(followedUsername);
        assert testUser.getNewFollowed().contains(followedUsername);
        testUser.unfollowUser(followedUser.getUsername());
        assert !testUser.getFollowed().contains(followedUsername);
        assert testUser.getDeletedFollowed().contains(followedUsername);
    }

    @Test
    public void shoppingList() {
        // TODO
        assert true;
    }
}