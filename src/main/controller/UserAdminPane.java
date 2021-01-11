package main.controller;

import javafx.scene.control.MenuItem;
import main.DatabaseConnection;
import main.recipeModel.Recipe;
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

    @FXML void initialize() throws SQLException, IOException {
        if (DatabaseConnection.isThemeLight()) {
            exitPic.setImage(new Image("icons/berryExit.png"));
        }
        refreshWindow();
        MenuItem menuItem = new MenuItem("Unfollow");
        menuItem.setOnAction(actionEvent -> {
            String username = followedList.getSelectionModel().getSelectedItem();
            if (username != null) {
                activeUser.unfollowUser(username);
                refreshFollowedList();
            }
        });
        setContextMenu(followedList, menuItem);
    }

    @FXML private void onRefreshButtonClick() throws IOException, SQLException {
        refreshWindow();
    }

    private void refreshWindow() throws IOException, SQLException {
        DatabaseConnection.getGroups(this, tilePane, activeUser);
        refreshFollowedList();
    }

    @FXML void onUserPressed(MouseEvent mouseEvent) throws SQLException, IOException {
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

    public void refreshFollowedList() {
        followedList.getItems().clear();
        followedList.getItems().addAll(activeUser.getFollowed());
    }

    @FXML private void onExitButtonAction() {
        FXMLLoader loader = loadFXML(new MainPane(activeUser), "/resources/mainPage.fxml");
        changeScene(exitButton, loader);
    }
}
