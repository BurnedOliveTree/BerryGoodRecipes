package main.controller;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.DatabaseConnection;
import main.recipeModel.Recipe;
import main.userModel.User;

import java.io.IOException;
import java.sql.SQLException;


public class RecipeAdminPane implements OrdinaryButtonAction {
    private User activeUser;

    @FXML
    public TableView<Recipe> myRecipesTable;
    public Button exitButton;
    public TableView<Recipe> favTable;

    public RecipeAdminPane( User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML
    void initialize() {
        setMyRecipesTable();
        setFavTable();
        favTable.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2) {
                    Recipe recipe = favTable.getSelectionModel().getSelectedItem();
                    try {
                        ShowRecipe(recipe);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }


    private void setMyRecipesTable() {
        ObservableList<Recipe> RecipeList = FXCollections.observableArrayList(activeUser.getUserRecipes());
        TableColumn<Recipe, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Recipe, String> groupNameColumn = new TableColumn<>("Group name");
        groupNameColumn.setCellValueFactory(new PropertyValueFactory<>("groupName"));
        TableColumn<Recipe, String> dateAddedColumn = new TableColumn<>("Date added");
        dateAddedColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));

        myRecipesTable.setItems(RecipeList);
        myRecipesTable.getColumns().addAll(nameColumn, groupNameColumn, dateAddedColumn);
    }
    private void setFavTable() {
        ObservableList<Recipe> FavList = FXCollections.observableList(activeUser.getFavorites());
        TableColumn<Recipe,String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Recipe, String> dateAddedColumn = new TableColumn<>("Date added");
        dateAddedColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
        TableColumn<Recipe, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<Recipe, Integer>  costColumn = new TableColumn<>("Cost");
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        TableColumn<Recipe, Integer> timeColumn = new TableColumn<>("Preparation time");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("prepareTime"));
        favTable.setItems(FavList);
        favTable.getColumns().addAll(nameColumn, authorColumn, dateAddedColumn, costColumn, timeColumn);
    }

    public void ShowRecipe(Recipe recipe) throws SQLException {
        FXMLLoader loader =  new FXMLLoader(getClass().getResource("/resources/recipePage.fxml"));
        loader.setController(new RecipePane(recipe, activeUser));
        try {
            Parent mainPage = loader.load();
            Scene mainPageScene = new Scene(mainPage);
            Stage oldStage = (Stage) favTable.getScene().getWindow();
            Stage stage = new Stage();
            mainPageScene.getStylesheets().add(getClass().getResource("/resources/"+ DatabaseConnection.theme+".css").toExternalForm());
            stage.setScene(mainPageScene);
//            oldStage.hide();
            stage.showAndWait();
//            oldStage.show();
        } catch (IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }


    @Override
    @FXML
    public void onExitButtonAction() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/mainPage.fxml"));
        MainPane controller = new MainPane(activeUser);
        loader.setController(controller);
        changeScene(exitButton, loader);

    }


}
