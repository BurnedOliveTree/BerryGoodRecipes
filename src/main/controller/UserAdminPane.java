package main.controller;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;
import main.DatabaseConnection;
import main.userModel.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
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
        for (String groupName: activeUser.getUserGroups()) {
            MenuItem tempMenuItem = new MenuItem(groupName);
            tempMenuItem.setOnAction(actionEvent -> {
                String username = followedList.getSelectionModel().getSelectedItem();
                if (username != null) {
                    try {
                        DatabaseConnection.invite(username, groupName);
                        refreshWindow();
                    } catch (IOException | SQLException err) {
                        err.printStackTrace();
                    }
                }
            });
            menuItemList.add(tempMenuItem);
        }
        menu.getItems().addAll(menuItemList);
        setContextMenu(followedList, unfollowMenuItem, menu);
    }

    @FXML private void onRefreshButtonClick() throws IOException, SQLException {
        refreshWindow();
    }

    public void refreshWindow() throws IOException, SQLException {
        setGroupTiles(DatabaseConnection.getGroups(activeUser));
        refreshFollowedList();
    }

    private void setGroupTiles(List<List<String>> listOfLists) {
        List<MenuButton> panelist = new ArrayList<>();
        for (int i=0; i<listOfLists.size(); i++) {
            int groupID = Integer.parseInt((listOfLists.get(i)).get(0));
            String groupName = listOfLists.get(i).get(1);
            listOfLists.get(i).remove(0);
            listOfLists.get(i).remove(0);

            MenuButton tempButton = new MenuButton(groupName);
            tempButton.setWrapText(true);
            tempButton.setTextAlignment(TextAlignment.CENTER);
            tempButton.setAlignment(Pos.CENTER);
            tempButton.setPrefSize(192, 64);

            MenuItem menuItem = new MenuItem("Show shopping list");
            menuItem.setOnAction(e -> System.out.println("TODO jump to shopping list"));
            tempButton.getItems().add(menuItem);

            menuItem = new MenuItem("Show recipes");
            menuItem.setOnAction(e -> getGroupRecipes(groupName));
            tempButton.getItems().add(menuItem);

            Menu followMenu = new Menu("Follow user");
            listOfLists.get(i).remove(activeUser.getUsername());
            for (String s: listOfLists.get(i)) {
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
            for (String s: listOfLists.get(i)) {
                menuItem = new MenuItem(s);
                menuItem.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Kick "+s+" from "+groupName);
                    alert.setHeaderText(null);
                    alert.setGraphic(null);
                    alert.setContentText("Are you sure?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
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
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete "+groupName);
                alert.setHeaderText(null);
                alert.setGraphic(null);
                alert.setContentText("Are you sure?\nYou will not be able to recover your group");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
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
        DatabaseConnection.addGroup(newGroupName.getText(), activeUser.getUsername());
        refreshWindow();
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
