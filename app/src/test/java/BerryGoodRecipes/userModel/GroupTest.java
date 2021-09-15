package BerryGoodRecipes.userModel;

import org.junit.Test;

public class GroupTest {
    @Test
    public void testEquals() {
        Group groupOne = new Group(1, "test1");
        Group groupTwo = new Group(1, "test2");
        assert groupOne.equals(groupTwo);
    }

    @Test
    public void testToString() {
        String groupName = "test";
        Group group = new Group(1, groupName);
        assert group.getName().equals(group.toString());
    }
}