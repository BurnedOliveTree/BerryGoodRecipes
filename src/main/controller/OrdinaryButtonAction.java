package main.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import main.DatabaseConnection;

import java.io.IOException;

public interface OrdinaryButtonAction {


    void onExitAction(Button button);

    default void changeScene(Button button,FXMLLoader loader) {
        try {

            Parent mainPage = loader.load();
            Scene mainPageScene = new Scene(mainPage);
            Stage stage = (Stage) button.getScene().getWindow();
            mainPageScene.getStylesheets().add(getClass().getResource("/resources/"+ DatabaseConnection.theme+".css").toExternalForm());
            stage.setScene(mainPageScene);
            stage.show();
        } catch (IOException e) {
            System.err.println(String.format("Error: %s", e.getMessage()));}
    }
}
