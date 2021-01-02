package main.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.TilePane;
import main.DatabaseConnection;
import main.userModel.User;

import javafx.fxml.FXML;

import java.sql.SQLException;

public class UserAdminPane extends OrdinaryButtonAction {
    private final User activeUser;
    @FXML
    public Button exitButton;
    public TilePane tilePane;
    public ListView<String> followedList;

    public UserAdminPane(User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML
    void initialize() {
        try {
            DatabaseConnection.getGroups(tilePane, activeUser);
            DatabaseConnection.getFollowed(followedList, activeUser);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    @Override
    public void onExitButtonAction() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/mainPage.fxml"));
        loader.setController(new MainPane(activeUser));
        changeScene(exitButton, loader);
    }
}
