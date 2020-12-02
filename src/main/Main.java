package main;

import main.controller.MainPane;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        setup();

        primaryStage.setTitle("BerryGood Recipes");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/resources/mainPage.fxml"));
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(getClass().getResource("/resources/"+Core.theme+".css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        MainPane controller = loader.getController();
    }

    private void setup() {
        DataBaseTest test = new DataBaseTest();
        test.test();
        new Core(test.theme);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
