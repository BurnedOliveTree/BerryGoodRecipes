package main.userModel;


public class Opinion {
    User user;
    int score;
    String opinionText;

    public Opinion(String comment, int score, User user){
        this.opinionText = comment;
        this.score = score;
        this.user = user;
    }

    private void setOpinionText(String text)  {this.opinionText = text;} // do edytowania komentarzy

    private void setScore(int score) {this.score = score;} // do edytowania oceny

}
