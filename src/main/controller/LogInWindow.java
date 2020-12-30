package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.DatabaseConnection;

import java.sql.*;

public class LogInWindow {
    private final MainPane mainPane;
    private String username;
    private String password;
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
    }

    @FXML
    private void getDataLogin(MouseEvent event) throws SQLException {
        event.consume();
        System.out.println("Hello "+usernameField.getText()+", your password is "+passwordField.getText());
        username = usernameField.getText();
        password = passwordField.getText();
        Stage stage = (Stage) usernameField.getScene().getWindow();
        login();
        if (mainPane.activeUser != null) {
            mainPane.loginButton.setText("Sign out");
            stage.close();
        }
    }

    @FXML
    private void getDataRegister(MouseEvent event) throws SQLException {
        event.consume();
        username = usernameField.getText();
        password = passwordField.getText();
        Stage stage = (Stage) usernameField.getScene().getWindow();
        register();
        if (mainPane.activeUser != null) {
            mainPane.loginButton.setText("Sign out");
            stage.close();
        }
    }

    public void login() throws SQLException {
        mainPane.activeUser = DatabaseConnection.login(username, password, errMess);
    }

    public void register() throws SQLException {
        mainPane.activeUser = DatabaseConnection.register(username, password, errMess);
    }
}
