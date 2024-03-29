package BerryGoodRecipes;

import javafx.application.Platform;
import javafx.scene.image.Image;
import BerryGoodRecipes.controller.LoadingPane;
import BerryGoodRecipes.controller.MainPane;
import BerryGoodRecipes.userModel.User;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    public static User activeUser = null;

    @Override
    public void init() {
        try {
            DatabaseConnection.setConnection();
        } catch (IOException | SQLException e) {
            Platform.exit();
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("BerryGood Recipes");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/mainPage.fxml"));
        MainPane controller = new MainPane(activeUser);
        loader.setControllerFactory(param -> controller);
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/"+DatabaseConnection.theme+".css").toExternalForm());
        if (DatabaseConnection.isThemeLight())
            primaryStage.getIcons().add(new Image("icons/berryLogo.png"));
        else
            primaryStage.getIcons().add(new Image("icons/raspLogo.png"));
        primaryStage.setScene(scene);
        primaryStage.show();

        // save shopping list and add favorites to database
        primaryStage.setOnCloseRequest(e -> {
            try {
                DatabaseConnection.saveUser(activeUser);
                Properties prop = new Properties();
                prop.load(getClass().getResourceAsStream("/app.config"));
                prop.setProperty("app.theme", DatabaseConnection.theme);
                System.out.println("Working Directory = " + System.getProperty("user.dir"));
                prop.store(new FileOutputStream("./src/main/resources/app.config"), null);
            } catch (SQLException | IOException err) {
                err.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setMinWidth(520);
        primaryStage.setMinHeight(520);
    }

    public static void main(String[] args) throws IOException, SQLException {
        new DatabaseConnection();
        System.setProperty("javafx.preloader", LoadingPane.class.getCanonicalName());
        launch(args);
    }
}
