package main;

import javafx.application.Platform;
import javafx.scene.image.Image;
import main.controller.MainPane;
import main.userModel.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

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
        loader.setControllerFactory(param -> controller);
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
        primaryStage.getIcons().add(new Image("icons/raspLogo.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
        // zapisanie listy zakupów i ulubionych do bazy danych
        primaryStage.setOnCloseRequest(e -> {
            try {
                DatabaseConnection.saveUser(activeUser);
            } catch (SQLException | IOException err) {
                err.printStackTrace();
            }
            try {
                Properties prop = new Properties();
                prop.load(new FileInputStream("src/resources/app.config"));
                prop.setProperty("app.theme", DatabaseConnection.theme);
                prop.store(new FileOutputStream("src/resources/app.config"), null);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
