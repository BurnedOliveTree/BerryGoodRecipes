package main.recipeModel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class RecipeFx {
    @FXML
    public Button exitButton;
    @FXML
    void initialize() {}
    @FXML
    public void onExitButton() throws IOException {
        Parent mainPage = FXMLLoader.load(getClass().getResource("/resources/mainPage.fxml"));
        Scene mainPageScene = new Scene(mainPage);
        Stage stage = (Stage) exitButton.getScene().getWindow();
        mainPageScene.getStylesheets().add(getClass().getResource("/resources/darkTheme.css").toExternalForm());
        stage.setScene(mainPageScene);
        stage.show();
    }
}
