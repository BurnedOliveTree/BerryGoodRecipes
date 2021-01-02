package main.controller;

import main.DatabaseConnection;
import main.userModel.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.TilePane;
import javafx.scene.input.MouseEvent;

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
    void onUserPressed(MouseEvent mouseEvent) {
        if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2) {
            String username = followedList.getSelectionModel().getSelectedItem();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/mainPage.fxml"));
            MainPane controller = new MainPane(activeUser);
            loader.setController(controller);
            changeScene(exitButton, loader);

            controller.search.setText("user:"+username);
            controller.search(new ActionEvent());
        }
    }

    @FXML
    @Override
    public void onExitButtonAction() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/mainPage.fxml"));
        loader.setController(new MainPane(activeUser));
        changeScene(exitButton, loader);
    }
}
