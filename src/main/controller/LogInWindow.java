package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.Core;
import main.userModel.User;

import java.sql.*;

public class LogInWindow {
    public TextField usernameField;
    public TextField passwordField;
    private String username;
    private String password;

    public LogInWindow() {}

    @FXML
    void initialize() {
    }

    @FXML
    private void getData(MouseEvent event) {
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

    public void login() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@//ora4.ii.pw.edu.pl:1521/pdb1.ii.pw.edu.pl", Core.databaseLogin, Core.databasePassword);

            try (Statement stmt = conn.createStatement()) {
                // check if such a username exists in the database
                ResultSet queryResult = stmt.executeQuery("select * from USERS where NAME = '"+username+"'");
                if (queryResult.next()) {
                    // check if password is correct
                    String gotID = queryResult.getString("USER_ID");
                    String gotPassword = queryResult.getString("PASSWORD");
                    if (gotPassword.equals(password)) {
                        // everything is correct, create a user
                        String name = queryResult.getString("NAME");
                        // TODO add all the other columns in the future
                        Core.activeUser = new User(Integer.parseInt(gotID), username, password);
                        System.out.println("Successfully logged in!");
                    } else {
                        System.out.println("Incorrect password!");
                    }
                }
                else {
                    System.out.println("User with such a username does not exist in the database!");
                }
            } catch (SQLException e) {
                throw new Error("Problem", e);
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void register() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@//ora4.ii.pw.edu.pl:1521/pdb1.ii.pw.edu.pl", Core.databaseLogin, Core.databasePassword);

            try (Statement stmt = conn.createStatement()) {
                if (!stmt.execute("insert into USERS values(null, '"+username+"', '"+password+"')")) {
                    System.out.println("Successfully created an account!");
                }
                else {
                    System.out.println("Creating the account failed, somehow");
                }
            } catch (SQLException e) {
                throw new Error("Problem", e);
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }
}
