package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import main.userModel.User;

public class LoadingPane extends BasicPaneActions {
    public User activeUser;
    @FXML public Label statusLabel;

    public LoadingPane(User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML private void initialize() { }
}
