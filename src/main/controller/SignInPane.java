package main.controller;

import main.DatabaseConnection;
import main.Main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class SignInPane extends BasicPaneActions {
    private final MainPane mainPane;
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Label errMess;

    public SignInPane(MainPane mainPane) {
        this.mainPane = mainPane;
    }

    @FXML void initialize() {
        errMess.setText("");
        Platform.runLater(() -> usernameField.requestFocus());
    }

    @FXML private void onLoginEnter() {
        passwordField.requestFocus();
    }

    @FXML private void onPasswordEnter() throws SQLException, IOException {
        login(usernameField.getText(), passwordField.getText());
    }

    @FXML private void getDataLogin(MouseEvent event) throws SQLException, IOException {
        // sign in
        event.consume();
        login(usernameField.getText(), passwordField.getText());
    }

    @FXML private void getDataRegister(MouseEvent event) throws SQLException, IOException {
        // sign up
        event.consume();
        if (checkCredentialsLength()) {
            mainPane.activeUser = DatabaseConnection.register(usernameField.getText(), passwordField.getText(), errMess);
            checkLoginStatus();
        }
    }

    private void login(String username, String password) throws SQLException, IOException {
        // call log-in in database
        if (checkCredentialsLength()) {
            mainPane.activeUser = DatabaseConnection.login(username, password, errMess);
            checkLoginStatus();
        }
    }

    private boolean checkCredentialsLength() {
        if (usernameField.getText().length() > DatabaseConnection.shortTextFieldLength) {
            errMess.setText("Username too long!");
            return false;
        }
        if (passwordField.getText().length() > DatabaseConnection.shortTextFieldLength) {
            errMess.setText("Password too long!");
            return false;
        }
        return true;
    }

    private void checkLoginStatus() {
        // close window if user is logged in
        Main.activeUser = mainPane.activeUser;
        if (mainPane.activeUser != null) {
            mainPane.setButtonActivity();
            ((Stage) usernameField.getScene().getWindow()).close();
        }
    }
}
