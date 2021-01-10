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

import java.io.IOException;
import java.sql.SQLException;

public class UserAdminPane extends OrdinaryButtonAction {
    private final User activeUser;
    @FXML private Button exitButton;
    @FXML private TilePane tilePane;
    @FXML private ListView<String> followedList;

    public UserAdminPane(User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML
    void initialize() throws SQLException, IOException {
        DatabaseConnection.getGroups(tilePane, activeUser);
        DatabaseConnection.getFollowed(followedList, activeUser);
    }

    @FXML
    void onUserPressed(MouseEvent mouseEvent) throws SQLException, IOException {
        if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2) {
            String username = followedList.getSelectionModel().getSelectedItem();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/mainPage.fxml"));
            MainPane controller = new MainPane(activeUser);
            loader.setControllerFactory(param -> controller);
            changeScene(exitButton, loader);

            controller.search.setText("user:"+username);
            controller.search(new ActionEvent());
        }
    }

    @FXML
    private void onExitButtonAction() {
        FXMLLoader loader = loadFXML(new MainPane(activeUser), "/resources/mainPage.fxml");
        changeScene(exitButton, loader);
    }
}
