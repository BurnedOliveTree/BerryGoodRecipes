package main.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.stage.Stage;
import main.DatabaseConnection;
import main.recipeModel.Recipe;
import main.userModel.Opinion;
import main.userModel.User;

import java.io.IOException;
import java.sql.SQLException;


public class OpinionPane {
    private Opinion opinion;
    private final User activeUser;
    private final Recipe recipe;
    ObservableList<String> scoreList = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    @FXML
    public Button okButton;
    public Button exitButton;
    public Label scoreLabel;
    public TextField commentTextField;
    public Label opinionLabel;
    public ListView opinionView;
    public Button editButton;
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
    private void initialize() throws SQLException {
        scoreBox.setItems(scoreList);
        exitButton.setOnAction( e->{ exitAction("/resources/recipePage.fxml"); });
        okButton.setDisable(true);
        reportButton.setDisable(true);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        scoreBox.setOnAction(e->okButtonActivity());
        okButton.setOnAction(e-> {
            try {
                okButtonAction();
            } catch (SQLException throwables) {
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
                editButton.setDisable(false);
                reportButton.setDisable(true);
            }
            else {
                deleteButton.setDisable(true);
                editButton.setDisable(true);
                reportButton.setDisable(false); }
        }
    }

    private void okButtonActivity(){
        if (activeUser != null){
            okButton.setDisable(false);
        }
    }

    private void okButtonAction() throws SQLException {
        String comment = commentTextField.getText();
        if (comment.equals(null)){comment = " ";};
        int score = Integer.parseInt(scoreBox.getValue().toString());
        opinion = new Opinion(comment, score, activeUser, recipe);
        DatabaseConnection.createOpinion(opinion, opinionLabel, opinionView);
    }

    private void  exitAction(String namePath) {
        try {
            FXMLLoader loader =  new FXMLLoader(getClass().getResource(namePath));
            RecipePane controller = new RecipePane(this.recipe, activeUser);
            loader.setController(controller);
            Parent recipePage = loader.load();
            Scene recipePageScene = new Scene(recipePage);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            recipePageScene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
            stage.setScene(recipePageScene);
            stage.show();
        } catch (IOException e) {
            System.err.println(String.format("Error: %s", e.getMessage()));}
    }


}