package main.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import main.DatabaseConnection;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import main.userModel.Group;
import main.userModel.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipeAdminPane extends BasicPaneActions {
    private final User activeUser;
    private final List<Group> accessibility = new ArrayList<>();

    @FXML private TableView<Recipe> myRecipesTable;
    @FXML private Button exitButton;
    @FXML private ImageView exitPic;
    @FXML private TableView<Recipe> favTable;
    @FXML private GridPane ingredientPane;
    @FXML private TextField titleField;
    @FXML private TextField portionField;
    @FXML private TextField costField;
    @FXML private TextField hrsField;
    @FXML private TextField minsField;
    @FXML private TextArea descriptionArea;
    @FXML private ChoiceBox<Group> accessibilityBox;

    public RecipeAdminPane(User activeUser) {
        this.activeUser = activeUser;
        try {
            this.activeUser.setUserGroups(DatabaseConnection.getGroups(activeUser.getUsername()));
        } catch (SQLException | IOException err) { err.printStackTrace(); }
        // for creating new recipe
        accessibility.add(new Group(0, "public"));
        accessibility.add(new Group(null, "private"));
        accessibility.addAll(activeUser.getUserGroups());
    }

    @FXML
    private void initialize() {
        if (DatabaseConnection.isThemeLight()) {
            exitPic.setImage(new Image("icons/berryExit.png"));
        }
        // settings for 'favorite' tab
        setFavTable();

        // settings for 'my recipes' tab
        setMyRecipesTable();

        // settings for 'add recipe' tab
        accessibilityBox.setItems(FXCollections.observableArrayList(accessibility));
        accessibilityBox.getSelectionModel().select(0);
        titleField.setPromptText("Recipe Title");
        hrsField.setPromptText("Hrs");
        minsField.setPromptText("Mins");
        ingredientPane.getRowConstraints().clear();
        for (int i = 0; i < 3; i++) {
            addIngredient();
        }

    }

    // user recipe tab

    private void setMyRecipesTable() {
        // creating a table with user recipes
        myRecipesTable.getItems().clear();
        TableColumn<Recipe, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Recipe, String> groupNameColumn = new TableColumn<>("Group name");
        groupNameColumn.setCellValueFactory(new PropertyValueFactory<>("groupName"));
        TableColumn<Recipe, String> dateAddedColumn = new TableColumn<>("Date added");
        dateAddedColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
        myRecipesTable.setItems(FXCollections.observableArrayList(activeUser.getUserRecipes()));
        myRecipesTable.getColumns().addAll(nameColumn, groupNameColumn, dateAddedColumn);
        // for double click show recipe
        myRecipesTable.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2) {
                Recipe recipe = myRecipesTable.getSelectionModel().getSelectedItem();
                ShowRecipe(recipe);
            }
        });
        // set context menu which provides the ability to remove recipes
        setContextMenu(myRecipesTable, createDeleteMyRecipeItem());
    }

    public MenuItem createDeleteMyRecipeItem() {
        // return Item for MenuContext which delete selected item in MyRecipeItem with warning
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(actionEvent -> {
            Recipe recipe = myRecipesTable.getSelectionModel().getSelectedItem();
            if (recipe != null) {
                Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION, "Delete recipe", null,
                        "You are now deleting your recipe.\n Are you sure?");
                if (result.isPresent() && result.get() == ButtonType.OK){
                    try {
                        DatabaseConnection.deleteRecipe(activeUser, recipe);
                        activeUser.deleteUserRecipe(recipe);
                        myRecipesTable.getItems().remove(recipe);
                        myRecipesTable.refresh();
                        if (favTable.getItems().contains(recipe)) {
                            favTable.getItems().remove(recipe);
                            favTable.refresh();
                        }
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return delete;
    }

    // user favorite tab

    private void setFavTable() {
        // creating a table with user favorite recipes
        favTable.getItems().clear();
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
        ObservableList<Recipe> FavList = FXCollections.observableList(activeUser.getFavorites());
        favTable.setItems(FavList);
        // for double click show recipe
        favTable.getColumns().addAll(nameColumn, authorColumn, dateAddedColumn, costColumn, timeColumn);
        favTable.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2) {
                Recipe recipe = favTable.getSelectionModel().getSelectedItem();
                ShowRecipe(recipe);
            }
        });
        // set context menu which provides the ability to remove recipe from favorites
        setContextMenu(favTable, createDeleteFavItem());
    }

    public MenuItem createDeleteFavItem() {
        // return Item for MenuContext which delete selected item in Favorite List
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

    // user and favorite tab

    private void ShowRecipe(Recipe recipe) {
        // show recipe after double click on user recipe table or favorite table
        FXMLLoader loader = loadFXML(new RecipePane(recipe, activeUser, null), "/resources/recipePage.fxml");
        changeScene(loader);
        if (activeUser.checkIfFavContainsRecipe(recipe)) {
            if (!activeUser.checkIfRecipeFavorite(recipe)) {
                favTable.getItems().remove(recipe);
            } else {
                favTable.getItems().add(recipe);
            }
        }
    }

    // add new recipe tab

    @FXML
    private void addIngredient() {
        // supports the button located in 'add recipe' tab
        // which dynamically adds a new field where the recipe ingredient can be entered
        TextField quantity = new TextField();
        ObservableList<String> units = FXCollections.observableArrayList(activeUser.getUnits());
        ChoiceBox<String> unit = new ChoiceBox<>();
        unit.setPrefWidth(ingredientPane.getColumnConstraints().get(1).getPrefWidth());
        unit.setItems(FXCollections.observableArrayList(units));
        unit.getItems().add("piece");
        TextField name = new TextField();
        quantity.setPromptText("Qty");
        name.setPromptText("Name");
        quantity.setStyle("-fx-text-box-border: transparent");
        name.setStyle("-fx-text-box-border: transparent");
        ingredientPane.addRow(ingredientPane.getRowCount(), quantity, unit, name);
    }

    @FXML
    private void saveRecipe() throws IOException, SQLException {
        // save new recipe created in the add recipe tab
        Group group = accessibilityBox.getSelectionModel().getSelectedItem();
        ArrayList<Ingredient> ingredientList = getIngredientList();
        String warning = checkCorrectness(getIngredientList());
        if (warning.equals("") && ingredientList != null){
            Integer publicity = null;
            if (!group.getName().equals("private")){
                publicity = group.getID();
            }
            Integer preparationTime = getTimePreparationInMinutes();
            Double cost = getCost();
            Double portions = getPortions();
            if (preparationTime != null && cost != null && portions != null){
                Recipe recipe = new Recipe(null, titleField.getText(), activeUser.getUsername(), descriptionArea.getText(), publicity, preparationTime, cost, portions, ingredientList);
                int recipeId  = DatabaseConnection.addRecipe(recipe, activeUser);
                recipe.setId(recipeId);
                clearRecipe();
                activeUser.addUserRecipe(recipe);
                myRecipesTable.getItems().add(recipe);
                myRecipesTable.refresh();
            } else {
                if (preparationTime == null)
                    warning += "Preparation time is too long";
                if (cost == null)
                    warning += "Cost is too big";
                if (portions == null)
                    warning += "Number of portions is too big";
                showWarning(warning);
            }
        } else if (ingredientList != null){
            showWarning(warning);
        }
    }

    private ArrayList<Ingredient> getIngredientList() {
        String warning = "";
        // create ingredient object after 'save' button clicked from correctly filled in fields
        ArrayList<Ingredient> ingredientList = new ArrayList<>();
        for (int i = 0; i < ingredientPane.getRowCount(); i++){
            TextField quantityField = (TextField) ingredientPane.getChildren().get(i*3);
            ChoiceBox<String> units = (ChoiceBox<String>) ingredientPane.getChildren().get(i*3+1);
            TextField nameField = (TextField) ingredientPane.getChildren().get(i*3+2);
            String quantityStr = quantityField.getText();
            String unit = units.getSelectionModel().getSelectedItem();
            String name = nameField.getText();
            if (quantityStr != null && unit != null && name != null && DatabaseConnection.checkDatabaseReduction(quantityStr)){
                if (!quantityStr.equals("") && quantityStr.matches("\\d+(\\.\\d+)?") && !unit.equals("") && !name.equals("") && name.length() < DatabaseConnection.shortTextFieldLength){
                    Double quantity = Double.parseDouble(quantityStr);
                    Ingredient ingredient = new Ingredient(null, quantity, unit, name);
                    ingredientList.add(ingredient);
                }
                else {
                    if (name.length() > DatabaseConnection.shortTextFieldLength) {
                        nameField.clear();
                        warning += "Name of ingredient is too long.";
                    } else if (!DatabaseConnection.checkDatabaseReduction(quantityStr)){
                        warning += "Ingredient quantity is too big";
                    }
                }
            }
        }
        if (warning.length() != 0) {
            showWarning(warning);
            ingredientList = null;
        }
        return ingredientList;
    }

    private Double getCost(){
        // get cost from field, and parse it
        if (costField.getText().equals(""))
            return 0.0;
        else {
            if (!DatabaseConnection.checkDatabaseReduction(costField.getText()))
                return null;
            return Double.parseDouble(costField.getText());
        }
    }

    private Double getPortions() {
        // get number of portions from field, and parse it
        if (portionField.getText().equals(""))
            return 1.0;
        else{
            if (!DatabaseConnection.checkDatabaseReduction(portionField.getText()))
                return null;
            return Double.parseDouble(portionField.getText());
        }
    }

    private Integer getTimePreparationInMinutes() {
        // get time from fields, parse them and converts to minutes
        if (hrsField.getText().equals("") && minsField.getText().equals(""))
            return 0;
        else {
            int mins = 0;
            if (!minsField.getText().equals("")) {
                try {
                    mins += Integer.parseInt(minsField.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            if (!hrsField.getText().equals("")) {
                try {
                    mins += 60 * Integer.parseInt(hrsField.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return mins;
        }
    }



    @FXML
    private void clearRecipe() {
        // clears the fields in the 'add recipe' tab after successfully creating a new recipe
        titleField.clear();
        portionField.clear();
        costField.clear();
        accessibilityBox.getSelectionModel().select(0);
        hrsField.clear();
        minsField.clear();
        descriptionArea.clear();
        ingredientPane.getChildren().clear();
        for (int i = 0; i < 3; i++) {
            addIngredient();
        }
    }

    private String checkCorrectness(ArrayList<Ingredient> ingredientList) {
        // validates the fields after clicking the 'save' button in the 'add recipe' tab
        String warning = "";
        if (titleField.getText().equals(""))
            warning += "Please enter title\n";
        if (titleField.getText().length() > DatabaseConnection.mediumTextFieldLength)
            warning += "Please enter shorter title\n";
        if (descriptionArea.getText().equals(""))
            warning += "Please enter description.\n";
        if (descriptionArea.getText().length() > DatabaseConnection.longTextFieldLength)
            warning += "Please enter shorter description.\n";
        if (ingredientList.size() == 0)
            warning += "Please add 1 or more ingredient\n";
        if (checkIfNotDouble(costField.getText()) && !costField.getText().equals("")) {
            warning += "Please enter correct cost value\n";
            costField.clear();
        }
        if (checkIfNotDouble(portionField.getText()) && !portionField.getText().equals("")){
            warning += "Please enter correct number of portions value\n";
            portionField.clear();
        }
        if (checkIfNotInteger(hrsField.getText()) && !hrsField.getText().equals("")) {
            warning += "Please enter correct hours value\n";
            hrsField.clear();
        }
        if (checkIfNotInteger(minsField.getText()) && !minsField.getText().equals("")) {
            warning += "Please enter correct hours value\n";
            minsField.clear();
        }
        return warning;
    }

    private boolean checkIfNotDouble(String match){ return !match.matches("\\d+(\\.\\d+)?"); }

    private boolean checkIfNotInteger(String match) {
        return !match.matches("\\d+");
    }

    private void showWarning(String warning) {
        // show warning if wrong or not enough recipe
        showAlert(Alert.AlertType.WARNING, "Not enough information!", null, warning);
    }

    @FXML
    private void onExitButtonAction() {
        // return to home page
        FXMLLoader loader = loadFXML(new MainPane(activeUser), "/resources/mainPage.fxml");
        changeScene(exitButton, loader);
    }

}
