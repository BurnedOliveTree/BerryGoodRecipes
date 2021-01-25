package main.userModel;

import main.recipeModel.Recipe;

public class Opinion {
    private final User user;
    private final Recipe recipe;
    private int score;
    private String opinionText;

    public Opinion(String comment, int score, User user, Recipe recipe){
        this.opinionText = comment;
        this.score = score;
        this.user = user;
        this.recipe = recipe;
    }
    public String getUsername()
    {
        return user.getUsername();
    }

    public int getScore() {
        return score;
    }

    public String getOpinionText() {
        return opinionText;
    }

    public Recipe getRecipe(){
        return recipe;
    }

    public void setOpinionText(String text) {
        this.opinionText = text;
    } // for editing opinion

    public void setScore(int score) {
        this.score = score;
    } // for editing score

}