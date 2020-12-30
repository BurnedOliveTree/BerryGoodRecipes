package main.controller;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.DatabaseConnection;
import main.recipeModel.Recipe;
import main.userModel.User;


import java.io.IOException;
import java.util.List;

public class RecipeAdminPane implements OrdinaryButtonAction {
    private User activeUser;
    @FXML
    TableView<Recipe> myRecipesTable;

    public RecipeAdminPane( User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML
    void initialize() {
        setMyRecipesTable();
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

    @Override
    public void onExitAction(Button button) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/mainPage.fxml"));
        MainPane controller = new MainPane(activeUser);
        loader.setController(controller);
        changeScene(button, loader);
    }


}
