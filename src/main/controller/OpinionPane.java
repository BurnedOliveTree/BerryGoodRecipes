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
    ObservableList<String> scoreList = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    @FXML
    public Button okButton;
    public Button exitButton;
    @FXML private ImageView exitPic;
    public Label scoreLabel;
    public TextField commentTextField;
    public Label opinionLabel;
    public ListView opinionView;
    public Button deleteButton;
    public Button reportButton;
    public Label reportLabel;
    @FXML
    public ChoiceBox scoreBox;

    public OpinionPane(Recipe recipe, User activeUser) {
        this.recipe = recipe;
        this.activeUser = activeUser;
    };

    @FXML
    private void initialize() throws SQLException, IOException {
        if (DatabaseConnection.isThemeLight()) {
            exitPic.setImage(new Image("icons/berryExit.png"));
        }
        scoreBox.setItems(scoreList);
        exitButton.setOnAction( e-> exitAction());
        okButton.setDisable(true);
        reportButton.setDisable(true);
        deleteButton.setDisable(true);
        scoreBox.setOnAction(e->okButtonActivity());
        okButton.setOnAction(e-> {
            try {
                okButtonAction();
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
        });
        opinionView.setOnMousePressed(e-> {
            try {
                opinionViewOnAction();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        DatabaseConnection.fillOpinions(recipe, opinionView);
        reportButton.setOnAction(e->{
            try {
                DatabaseConnection.reportOpinion(opinionView, activeUser.getUsername(), reportLabel, getOpinionAuthor(), recipe.getId());
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
        });
        deleteButton.setOnAction(e->{
            try {
                DatabaseConnection.deleteOpinion(recipe, activeUser.getUsername(), opinionView);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }


    private String getOpinionAuthor(){
        String opinion = (String) opinionView.getSelectionModel().getSelectedItem();
        String username = "";
        int i = 0;
        while (opinion.charAt(i) != ' '){
            username += opinion.charAt(i);
            i++;
        }
        return username;
    }

    private void opinionViewOnAction() throws SQLException {
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

    private void okButtonActivity(){
        if (activeUser != null && !activeUser.getUsername().equals(recipe.getAuthor())){
            okButton.setDisable(false);
        }
    }



    private void okButtonAction() throws SQLException, IOException {
        String comment = commentTextField.getText();
        if (comment.equals(null)){comment = " ";};
        int score = Integer.parseInt(scoreBox.getValue().toString());
        opinion = new Opinion(comment, score, activeUser, recipe);
        DatabaseConnection.createOpinion(opinion, opinionLabel, opinionView);
    }

    private void exitAction() {
        FXMLLoader loader = loadFXML(new RecipePane(this.recipe, activeUser), "/resources/recipePage.fxml");
        changeScene(exitButton, loader);
    }


}