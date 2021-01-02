package main.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import main.DatabaseConnection;
import main.recipeModel.Recipe;
import main.userModel.User;
import javafx.scene.input.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;


public class RecipeAdminPane extends OrdinaryButtonAction {
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
        favTable.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2) {
                Recipe recipe = favTable.getSelectionModel().getSelectedItem();
                try {
                    ShowRecipe(recipe);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        myRecipesTable.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2) {
                Recipe recipe = myRecipesTable.getSelectionModel().getSelectedItem();
                try {
                    ShowRecipe(recipe);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        setContentMenu(favTable, createDeleteFavItem());
        setContentMenu(myRecipesTable, createDeleteMyRecipeItem());

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/recipePage.fxml"));
        loader.setController(new RecipePane(recipe, activeUser));
        try {
            Parent mainPage = loader.load();
            Scene mainPageScene = new Scene(mainPage);
            Stage stage = new Stage();
            mainPageScene.getStylesheets().add(getClass().getResource("/resources/" + DatabaseConnection.theme + ".css").toExternalForm());
            stage.setScene(mainPageScene);
            stage.showAndWait();
            favTable.refresh();
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

    public MenuItem createDeleteFavItem() {
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(actionEvent -> {
            Recipe recipe = favTable.getSelectionModel().getSelectedItem();
            if (recipe != null) {
                activeUser.removeFavorite(recipe);
                favTable.refresh();
            }
        });
        return delete;
    }

    public MenuItem createDeleteMyRecipeItem() {
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(actionEvent -> {
            Recipe recipe = myRecipesTable.getSelectionModel().getSelectedItem();
            if (recipe != null) {
                // @TODO zapytaj czy na pewno chce usunac
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Recipe");
                alert.setHeaderText(null);
                alert.setGraphic(null);
                alert.setContentText("You are now deleting your recipe.\n Are you sure?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){

                    // @TODO usun przepis
                    myRecipesTable.refresh();
                }
            }
        });
        return delete;
    }



}
