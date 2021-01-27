package main.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import main.DatabaseConnection;
import main.recipeModel.Recipe;
import main.userModel.User;

import java.io.IOException;
import java.sql.SQLException;

public class OpinionPane extends BasicPaneActions {
    private final User activeUser;
    private final Recipe recipe;
    private final BasicPaneActions returnPane;
    ObservableList<String> scoreList = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    @FXML private Button okButton;
    @FXML private Button exitButton;
    @FXML private ImageView exitPic;
    @FXML private TextField commentTextField;
    @FXML private Label opinionLabel;
    @FXML private ListView<String> opinionView;
    @FXML private Button deleteButton;
    @FXML private Button reportButton;
    @FXML private Label reportLabel;
    @FXML private ChoiceBox<String> scoreBox;

    public OpinionPane(Recipe recipe, User activeUser, BasicPaneActions returnPane) {
        this.recipe = recipe;
        this.activeUser = activeUser;
        this.returnPane = returnPane;
    }

    @FXML private void initialize() throws SQLException, IOException {
        if (DatabaseConnection.isThemeLight()) {
            exitPic.setImage(new Image("icons/berryExit.png"));
        }
        scoreBox.setItems(scoreList); // available scores
        okButton.setDisable(true);  // ok button available only when user is logged in, not an author of recipe and had selected score
        reportButton.setDisable(true); // available when user selected different user opinion
        deleteButton.setDisable(true); // available when user selected their own opinion
        DatabaseConnection.fillOpinions(recipe, opinionView);
        reportButton.setOnAction(e -> {
            try {
                DatabaseConnection.reportOpinion(activeUser.getUsername(), reportLabel, getOpinionAuthor(), recipe.getId());
            } catch (SQLException | IOException err) {
                err.printStackTrace();
            }
        });
        deleteButton.setOnAction(e -> {
            try {
                DatabaseConnection.deleteOpinion(recipe, activeUser.getUsername(), opinionView);
            } catch (IOException | SQLException err) {
                err.printStackTrace();
            }
        });
    }


    private String getOpinionAuthor() {
        // Gets opinion author from opinion string,
        String opinion = opinionView.getSelectionModel().getSelectedItem();
        String username = "";
        int i = 0;
        while (opinion.charAt(i) != ' '){
            username += opinion.charAt(i);
            i++;
        }
        return username;
    }

    @FXML private void opinionViewOnAction() {
        // sets delete and report buttons activity, catches error when sb selects empty space
        try {
            reportLabel.setText("");
            if (activeUser != null) {
                if (getOpinionAuthor().equals(activeUser.getUsername())) {
                    deleteButton.setDisable(false);
                    reportButton.setDisable(true);
                } else {
                    deleteButton.setDisable(true);
                    reportButton.setDisable(false);
                }
            }
        } catch (RuntimeException err) {}
    }

    @FXML private void okButtonActivity() {
        // if user is logged in, and is not an author of recipe ok button is able
        if (activeUser != null && !activeUser.getUsername().equals(recipe.getAuthor())){
            okButton.setDisable(false);
        }
    }

    @FXML private void okButtonAction() throws SQLException, IOException {
        // saves and displays added opinion
        String comment = commentTextField.getText();
        int score = Integer.parseInt(scoreBox.getValue());
        DatabaseConnection.createOpinion(activeUser.getUsername(), recipe.getId(),score, comment ,opinionLabel, opinionView);
    }

    @FXML private void exitAction() {
        FXMLLoader loader = loadFXML(returnPane, "/resources/recipePage.fxml");
        changeScene(exitButton, loader);
    }
}