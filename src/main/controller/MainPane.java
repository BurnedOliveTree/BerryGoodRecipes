package main.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPane {
    @FXML
    public Button recipeLink;
    public Pane recipePane;

    @FXML
    void initialize() {recipeLink.setText("Placki");}
    // Change Scene to RecipeScene
    @FXML
    public void onClickButton(ActionEvent actionEvent) throws IOException {
        Parent recipePage = FXMLLoader.load(getClass().getResource("/resources/recipePage.fxml"));
        Scene recipePageScene = new Scene(recipePage);
        Stage stage = (Stage) recipeLink.getScene().getWindow();
        recipePageScene.getStylesheets().add(getClass().getResource("/resources/darkTheme.css").toExternalForm());
        stage.setScene(recipePageScene);
        stage.show();
    }

}
