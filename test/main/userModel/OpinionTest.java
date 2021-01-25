package main.userModel;

import main.recipeModel.Recipe;
import org.junit.Test;

public class OpinionTest {
    public Opinion createOpinion(String comment, int score) {
        return new Opinion(comment, score, new User("test"), new Recipe());
    }

    @Test
    public void setOpinionText() {
        Opinion testOpinion = createOpinion("test", 1);
        String newText = "newText";
        testOpinion.setOpinionText(newText);
        assert testOpinion.getOpinionText().equals(newText);
    }

    @Test
    public void setScore() {
        Opinion testOpinion = createOpinion("test", 1);
        int newScore = 9;
        testOpinion.setScore(newScore);
        assert testOpinion.getScore() == newScore;
    }
}