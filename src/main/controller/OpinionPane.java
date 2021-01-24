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
import main.userModel.Opinion;
import main.userModel.User;

import java.io.IOException;
import java.sql.SQLException;

public class OpinionPane extends BasicPaneActions {
    private Opinion opinion;
    private final User activeUser;
    private final Recipe recipe;
    private final BasicPaneActions returnPane;
    ObservableList<String> scoreList = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    @FXML private Button okButton;
    @FXML private Button exitButton;
    @FXML private ImageView exitPic;
    public Label scoreLabel;
    @FXML private TextField commentTextField;
    @FXML private Label opinionLabel;
    @FXML private ListView opinionView;
    @FXML private Button deleteButton;
    @FXML private Button reportButton;
    @FXML private Label reportLabel;
    @FXML private ChoiceBox scoreBox;

    public OpinionPane(Recipe recipe, User activeUser, BasicPaneActions returnPane) {
        this.recipe = recipe;
        this.activeUser = activeUser;
        this.returnPane = returnPane;
    };

    @FXML
    private void initialize() throws SQLException, IOException {
        if (DatabaseConnection.isThemeLight()) {
            exitPic.setImage(new Image("icons/berryExit.png"));
        }
        scoreBox.setItems(scoreList);
        okButton.setDisable(true);
        reportButton.setDisable(true);
        deleteButton.setDisable(true);
        DatabaseConnection.fillOpinions(recipe, opinionView);
        reportButton.setOnAction(e->{
            try {
                DatabaseConnection.reportOpinion(opinionView, activeUser.getUsername(), reportLabel, getOpinionAuthor(), recipe.getId());
            } catch (SQLException | IOException err) {
                err.printStackTrace();
            }
        });
        deleteButton.setOnAction(e->{
            try {
                DatabaseConnection.deleteOpinion(recipe, activeUser.getUsername(), opinionView);
            } catch (IOException | SQLException err) {
                err.printStackTrace();
            }
        });
    }


    private String getOpinionAuthor() {
        String opinion = (String) opinionView.getSelectionModel().getSelectedItem();
        String username = "";
        int i = 0;
        while (opinion.charAt(i) != ' '){
            username += opinion.charAt(i);
            i++;
        }
        return username;
    }

    @FXML private void opinionViewOnAction() {
        reportLabel.setText("");
        if (activeUser != null){
            if (getOpinionAuthor().equals(activeUser.getUsername())){
                deleteButton.setDisable(false);
                reportButton.setDisable(true);
            }
            else {
                deleteButton.setDisable(true);
                reportButton.setDisable(false); }
        }
    }

    @FXML private void okButtonActivity() {
        if (activeUser != null && !activeUser.getUsername().equals(recipe.getAuthor())){
            okButton.setDisable(false);
        }
    }

    @FXML private void okButtonAction() throws SQLException, IOException {
        String comment = commentTextField.getText();
        if (comment.equals(null)) {
            comment = " ";
        }
        int score = Integer.parseInt(scoreBox.getValue().toString());
        opinion = new Opinion(comment, score, activeUser, recipe);
        DatabaseConnection.createOpinion(opinion, opinionLabel, opinionView);
    }

    @FXML private void exitAction() {
        FXMLLoader loader = loadFXML(returnPane, "/resources/recipePage.fxml");
        changeScene(exitButton, loader);
    }
}