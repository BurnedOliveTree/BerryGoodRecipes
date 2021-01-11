package main.controller;

import javafx.application.Platform;
import main.DatabaseConnection;
import main.userModel.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.TilePane;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;

public class UserAdminPane extends BasicPaneActions {
    private final User activeUser;
    @FXML private Button exitButton;
    @FXML private ImageView exitPic;
    @FXML private TilePane tilePane;
    @FXML private ListView<String> followedList;

    public UserAdminPane(User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML
    void initialize() throws SQLException, IOException {
        if (DatabaseConnection.isThemeLight()) {
            exitPic.setImage(new Image("icons/berryExit.png"));
        }
        DatabaseConnection.getGroups(this, tilePane, activeUser);
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

    public void getGroupRecipes(String groupName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/mainPage.fxml"));
            MainPane controller = new MainPane(activeUser);
            loader.setControllerFactory(param -> controller);
            changeScene(exitButton, loader);

            controller.search.setText("group:"+groupName);
            controller.search(new ActionEvent());
        } catch (SQLException | IOException err) {
            err.printStackTrace();
        }
    }

    @FXML
    private void onExitButtonAction() {
        FXMLLoader loader = loadFXML(new MainPane(activeUser), "/resources/mainPage.fxml");
        changeScene(exitButton, loader);
    }
}
