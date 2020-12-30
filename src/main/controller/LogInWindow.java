package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.DatabaseConnection;
import main.Main;

import java.sql.*;

public class LogInWindow {
    private final MainPane mainPane;
    public TextField usernameField;
    public TextField passwordField;
    private String username;
    private String password;

    public LogInWindow(MainPane mainPane) {
        this.mainPane = mainPane;
    }

    @FXML
    void initialize() {
    }

    @FXML
    private void getData(MouseEvent event) throws SQLException {
        event.consume();
        System.out.println("Hello "+usernameField.getText()+", your password is "+passwordField.getText());
        username = usernameField.getText();
        password = passwordField.getText();
        Stage stage = (Stage) usernameField.getScene().getWindow();
        if (stage.getTitle().equals("Register")) {
            register();
        }
        else {
            login();
        }
        stage.close();
    }

    public void login() throws SQLException {
        mainPane.activeUser = DatabaseConnection.login(username, password);
    }

    public void register() throws SQLException {
        mainPane.activeUser = DatabaseConnection.register(username, password);
    }
}
