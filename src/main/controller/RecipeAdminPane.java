package main.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import main.recipeModel.Recipe;
import main.userModel.User;

import java.sql.SQLException;
import java.util.Optional;

public class RecipeAdminPane extends OrdinaryButtonAction {
    private final User activeUser;

    @FXML
    private ScrollBar scrollIngredient;
    @FXML
    private TableView<Recipe> myRecipesTable;
    @FXML
    private Button exitButton;
    @FXML
    private TableView<Recipe> favTable;
    @FXML
    private GridPane ingredientPane;
    @FXML
    private TextField titleLabel;
    @FXML
    private TextField portionLabel;
    @FXML
    private TextField costLabel;
    @FXML
    private TextField hrsLabel;
    @FXML
    private TextField minsLabel;
    @FXML
    private TextArea descriptionArea ;

    public RecipeAdminPane( User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML
    private void initialize() {
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
        ingredientPane.getRowConstraints().clear();
        for (int i = 0; i < 3; i++) {
            addIngredient();
        }

    }

    @FXML
    private void addIngredient() {
        TextField quantity = new TextField("Qty");
        SplitMenuButton unit = new SplitMenuButton();
        unit.setPrefWidth(ingredientPane.getColumnConstraints().get(1).getPrefWidth());
        TextField name = new TextField("Name");
        quantity.setStyle("-fx-text-box-border: transparent");
        name.setStyle("-fx-text-box-border: transparent");
        ingredientPane.addRow(ingredientPane.getRowCount()+1, quantity, unit, name);
    }

    @FXML
    private void saveRecipe() {

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

    private void ShowRecipe(Recipe recipe) throws SQLException {
        FXMLLoader loader = loadFXML(new RecipePane(recipe, activeUser), "/resources/recipePage.fxml");
        changeScene(exitButton, loader, true);
        favTable.refresh();
    }

    @FXML
    private void onExitButtonAction() {
        FXMLLoader loader = loadFXML(new MainPane(activeUser), "/resources/mainPage.fxml");
        changeScene(exitButton, loader);
    }

    // return Item for MenuContext which delete selected item in Favorite List
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

    // return Item for MenuContext which delete selected item in MyRecipeItem with warning
    public MenuItem createDeleteMyRecipeItem() {
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(actionEvent -> {
            Recipe recipe = myRecipesTable.getSelectionModel().getSelectedItem();
            if (recipe != null) {
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
