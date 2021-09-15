package BerryGoodRecipes.controller;

import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import BerryGoodRecipes.DatabaseConnection;

import java.io.IOException;

public class LoadingPane extends Preloader {
    private Stage stage;
    @FXML public static Label statusLabel;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader loader;
        if (DatabaseConnection.isThemeLight()) {
            stage.getIcons().add(new Image("icons/berryLogo.png"));
            loader = new FXMLLoader(this.getClass().getResource("/berryLoadingPage.fxml"));
        }
        else {
            stage.getIcons().add(new Image("icons/raspLogo.png"));
            loader = new FXMLLoader(this.getClass().getResource("/raspLoadingPage.fxml"));
        }
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/"+DatabaseConnection.theme+".css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }
}