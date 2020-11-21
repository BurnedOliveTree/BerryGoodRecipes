package main;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("BerryGood Recipes");
        Text text1 = new Text(300, 230, "Miłego dnia :D");
        text1.setFill(Color.rgb(240, 240, 240));
        text1.setFont(Font.font(java.awt.Font.SERIF, 20));
        Group root2 = new Group(text1);
        primaryStage.setScene(new Scene(root2, 640, 480, Color.rgb(16, 16, 16)));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
        //System.out.println(javafx.scene.text.Font.getFamilies());
    }
}
