package main.recipeModel;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RecipeFx extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("BerryGood Recipes");
        Text text1 = new Text(300, 230, "Miłego dnia :D");
        text1.setFill(Color.rgb(100, 20, 0));
        text1.setFont(Font.font(java.awt.Font.SERIF, 20));
        Group root2 = new Group(text1);
        stage.setScene(new Scene(root2, 640, 480, Color.web("#111")));
        stage.show();
    }
}
