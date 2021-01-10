package main.controller;

import main.DatabaseConnection;
import main.Main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LogInWindow extends OrdinaryButtonAction {
    private final MainPane mainPane;
    @FXML
    public TextField usernameField;
    public TextField passwordField;
    public Label errMess;

    public LogInWindow(MainPane mainPane) {
        this.mainPane = mainPane;
    }

    @FXML
    void initialize() {
        errMess.setText("");
        Platform.runLater(() -> usernameField.requestFocus());
    }

    @FXML
    private void onLoginEnter(ActionEvent ae) {
        passwordField.requestFocus();
    }

    @FXML
    private void onPasswordEnter(ActionEvent ae) throws SQLException, IOException {
        login(usernameField.getText(), passwordField.getText());
    }

    @FXML
    private void getDataLogin(MouseEvent event) throws SQLException, IOException {
        event.consume();
        System.out.println("Hello "+usernameField.getText()+", your password is "+passwordField.getText());
        login(usernameField.getText(), passwordField.getText());
    }

    @FXML
    private void getDataRegister(MouseEvent event) throws SQLException, IOException {
        event.consume();
        mainPane.activeUser = DatabaseConnection.register(usernameField.getText(), passwordField.getText(), errMess);
        checkLoginStatus();
    }

    public void login(String username, String password) throws SQLException, IOException {
        mainPane.activeUser = DatabaseConnection.login(username, password, errMess);
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        Main.activeUser = mainPane.activeUser;
        if (mainPane.activeUser != null) {
            mainPane.setButtonActivity();
            ((Stage) usernameField.getScene().getWindow()).close();
        }
    }
}
