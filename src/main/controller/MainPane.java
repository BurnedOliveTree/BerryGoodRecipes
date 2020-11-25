package main.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class MainPane {
    @FXML
    public Button recipeLink;
    public Pane recipePane;

    @FXML
    void initialize() {recipeLink.setText("Placki");}

    @FXML
    public void onClickButton(ActionEvent actionEvent) {
        System.out.println("Am");
    }

}
