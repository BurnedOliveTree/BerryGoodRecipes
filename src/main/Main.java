package main;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import main.controller.MainPane;
import main.userModel.User;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static User activeUser = null;

    @Override
    public void start(Stage primaryStage) throws IOException {
        new DatabaseConnection();

        primaryStage.setTitle("BerryGood Recipes");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/resources/mainPage.fxml"));
        MainPane controller = new MainPane(activeUser);
        loader.setController(controller);
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {

                Platform.exit();
                System.exit(0);
            }
        });



//        MainPane controller = loader.getController();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
