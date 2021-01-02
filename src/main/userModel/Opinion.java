package main.userModel;


import main.recipeModel.Recipe;

public class Opinion {
    User user;
    int score;
    Recipe recipe;
    String opinionText;

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

    private void setOpinionText(String text)  {this.opinionText = text;} // do edytowania komentarzy

    private void setScore(int score) {this.score = score;} // do edytowania oceny

}