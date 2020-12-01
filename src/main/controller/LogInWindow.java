package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LogInWindow {
    public TextField usernameField;
    public TextField passwordField;

    public LogInWindow() {}

    @FXML
    private void getData(MouseEvent event) {
        event.consume();
        System.out.println("Hello "+usernameField.getText()+", your password is "+passwordField.getText());
    }
}
