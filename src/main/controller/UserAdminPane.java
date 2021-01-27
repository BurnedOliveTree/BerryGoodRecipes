package main.controller;

import main.DatabaseConnection;
import main.userModel.Group;
import main.userModel.User;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.TilePane;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserAdminPane extends BasicPaneActions {
    private final User activeUser;
    @FXML private Button exitButton;
    @FXML private ImageView groupPic;
    @FXML private ImageView refreshPic;
    @FXML private ImageView exitPic;
    @FXML private TilePane tilePane;
    @FXML private TextField newGroupName;
    @FXML private ListView<String> followedList;

    public UserAdminPane(User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML void initialize() {
        if (DatabaseConnection.isThemeLight()) {
            groupPic.setImage(new Image("icons/berryGroup.png"));
            refreshPic.setImage(new Image("icons/berryRefresh.png"));
            exitPic.setImage(new Image("icons/berryExit.png"));
        }
        Platform.runLater(() -> {
            try {
                refreshWindow();
            } catch (IOException | SQLException err) {
                err.printStackTrace();
            }
        });
    }

    @FXML private void onRefreshButtonClick() throws IOException, SQLException {
        refreshWindow();
    }

    public void refreshWindow() throws IOException, SQLException {
        // refresh window to check for new groups or followed users
        activeUser.setUserGroups(DatabaseConnection.getGroups(activeUser.getUsername()));
        Platform.runLater(() -> {
            setGroupTiles();
            refreshFollowedList();
        });
    }

    private void setGroupTiles() {
        // create group tiles from a list of raw group data
        List<MenuButton> panelist = new ArrayList<>();
        for (Group group : activeUser.getUserGroups()) {
            int groupID = group.getID();
            String groupName = group.getName();
            List<String> participants = group.getParticipants();

            MenuButton tempButton = new MenuButton(groupName);
            tempButton.setWrapText(true);
            tempButton.setTextAlignment(TextAlignment.CENTER);
            tempButton.setAlignment(Pos.CENTER);
            tempButton.setPrefSize(192, 64);

            MenuItem menuItem = new MenuItem("Show shopping list");
            menuItem.setOnAction(e -> {
                FXMLLoader loader = loadFXML(new ShoppingListPane(activeUser, this, group), "/resources/shoppingListPage.fxml");
                changeScene(exitButton, loader);
            });
            tempButton.getItems().add(menuItem);

            menuItem = new MenuItem("Show recipes");
            menuItem.setOnAction(e -> getGroupRecipes(groupName));
            tempButton.getItems().add(menuItem);

            Menu followMenu = new Menu("Follow user");
            for (String s : participants) {
                menuItem = new MenuItem(s);
                if (activeUser.getFollowed().contains(s))
                    menuItem.setDisable(true);
                MenuItem finalMenuItem = menuItem;
                menuItem.setOnAction(e -> {
                    activeUser.followUser(s);
                    refreshFollowedList();
                    finalMenuItem.setDisable(true);
                });
                followMenu.getItems().add(menuItem);
            }
            tempButton.getItems().add(followMenu);

            tempButton.getItems().add(new SeparatorMenuItem());

            Menu kickMenu = new Menu("Kick user");
            for (String s : participants) {
                menuItem = new MenuItem(s);
                menuItem.setOnAction(e -> {
                    Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION, "Kick " + s + " from " + groupName, null,
                            "Are you sure?");
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            DatabaseConnection.kickUser(s, groupID);
                            refreshWindow();
                        } catch (IOException | SQLException err) {
                            err.printStackTrace();
                        }
                    }
                });
                kickMenu.getItems().add(menuItem);
            }
            tempButton.getItems().add(kickMenu);
            menuItem = new MenuItem("Delete group");
            menuItem.setOnAction(e -> {
                Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION, "Delete " + groupName, null,
                        "Are you sure?\nYou will not be able to recover your group");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        DatabaseConnection.deleteGroup(groupID);
                        refreshWindow();
                    } catch (IOException | SQLException err) {
                        err.printStackTrace();
                    }
                }
            });
            tempButton.getItems().add(menuItem);
            panelist.add(tempButton);
        }
        tilePane.getChildren().clear();
        tilePane.getChildren().addAll(panelist);
    }

    @FXML private void onAddGroupButtonClick() throws IOException, SQLException {
        if (!(newGroupName.getText().length() > DatabaseConnection.shortTextFieldLength)) {
            DatabaseConnection.addGroup(newGroupName.getText(), activeUser.getUsername());
            refreshWindow();
        }
    }

    @FXML void onUserPressed(MouseEvent mouseEvent) throws SQLException, IOException {
        if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2) {
            String username = followedList.getSelectionModel().getSelectedItem();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/mainPage.fxml"));
            MainPane controller = new MainPane(activeUser);
            loader.setControllerFactory(param -> controller);
            changeScene(exitButton, loader);

            controller.search.setText("user:"+username);
            controller.search();
        }
    }

    public void getGroupRecipes(String groupName) {
        // show recipes of a given group
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/mainPage.fxml"));
            MainPane controller = new MainPane(activeUser);
            loader.setControllerFactory(param -> controller);
            changeScene(exitButton, loader);

            controller.search.setText("group:"+groupName);
            controller.search();
        } catch (SQLException | IOException err) {
            err.printStackTrace();
        }
    }

    private void setContextMenu() {
        MenuItem unfollowMenuItem = new MenuItem("Unfollow");
        unfollowMenuItem.setOnAction(actionEvent -> {
            String username = followedList.getSelectionModel().getSelectedItem();
            if (username != null) {
                activeUser.unfollowUser(username);
                refreshFollowedList();
            }
        });
        Menu menu = new Menu("Invite");
        List<MenuItem> menuItemList = new ArrayList<>();
        for (Group group: activeUser.getUserGroups()) {
            MenuItem tempMenuItem = new MenuItem(group.getName());
            tempMenuItem.setOnAction(actionEvent -> {
                String username = followedList.getSelectionModel().getSelectedItem();
                if (username != null) {
                    if (group.getParticipants().contains(username))
                        tempMenuItem.setDisable(true);
                    else {
                        try {
                            DatabaseConnection.invite(username, group.getID());
                            tempMenuItem.setDisable(true);
                            refreshWindow();
                        } catch (IOException | SQLException err) { err.printStackTrace(); }
                    }
                }
            });
            menuItemList.add(tempMenuItem);
        }
        menu.getItems().addAll(menuItemList);
        super.setContextMenu(followedList, unfollowMenuItem, menu);
    }

    public void refreshFollowedList() {
        // refresh list of followed users
        followedList.getItems().clear();
        followedList.getItems().addAll(activeUser.getFollowed());
        setContextMenu();
    }

    @FXML private void onExitButtonAction() {
        FXMLLoader loader = loadFXML(new MainPane(activeUser), "/resources/mainPage.fxml");
        changeScene(exitButton, loader);
    }
}
