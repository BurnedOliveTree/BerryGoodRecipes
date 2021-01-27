package main.userModel;

import main.recipeModel.Recipe;
import org.junit.Before;
import org.junit.Test;

public class UserTest {
    private User user;

    @Before
    public void createRecipe()  {
        this.user =  new User("test");
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
        User followedUser = createUser(followedUsername);
        user.followUser(followedUser.getUsername());
        assert user.getFollowed().contains(followedUsername);
        assert user.getNewFollowed().contains(followedUsername);
        user.unfollowUser(followedUser.getUsername());
        assert !user.getFollowed().contains(followedUsername);
        assert user.getDeletedFollowed().contains(followedUsername);
    }

    @Test
    public void shoppingList() {
        assert true;
    }
}