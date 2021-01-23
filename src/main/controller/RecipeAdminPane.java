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
import main.userModel.User;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipeAdminPane extends BasicPaneActions {
    private final User activeUser;
    private List<String> accessibility = new ArrayList<String>();

    @FXML private ScrollBar scrollIngredient;
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
    @FXML private ScrollPane scrollInfo;
    @FXML private ChoiceBox<String> accessibilityBox;


    public RecipeAdminPane( User activeUser) {
        this.activeUser = activeUser;
        accessibility.add("public");
        accessibility.add("private");
        accessibility.addAll(activeUser.getUserGroups());
    }

    @FXML
    private void initialize() {
        if (DatabaseConnection.isThemeLight()) {
            exitPic.setImage(new Image("icons/berryExit.png"));
        }
        setMyRecipesTable();
        setFavTable();
        favTable.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2) {
                Recipe recipe = favTable.getSelectionModel().getSelectedItem();
                ShowRecipe(recipe);
            }
        });
        myRecipesTable.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() == 2) {
                Recipe recipe = myRecipesTable.getSelectionModel().getSelectedItem();
                ShowRecipe(recipe);
            }
        });

        accessibilityBox.setItems(FXCollections.observableArrayList(accessibility));
        accessibilityBox.getSelectionModel().select(0);
        setContextMenu(favTable, createDeleteFavItem());
        setContextMenu(myRecipesTable, createDeleteMyRecipeItem());

        titleField.setPromptText("Recipe Title");
//        accessibilityBox.setT
        hrsField.setPromptText("Hrs");
        minsField.setPromptText("Mins");
        ingredientPane.getRowConstraints().clear();
        for (int i = 0; i < 3; i++) {
            addIngredient();
        }

    }

    @FXML
    private void addIngredient() {
        TextField quantity = new TextField();
        ObservableList<String> units = activeUser.getUnits();
        ChoiceBox<String> unit = new ChoiceBox<>();
        unit.setPrefWidth(ingredientPane.getColumnConstraints().get(1).getPrefWidth());
        unit.setItems(FXCollections.observableArrayList(units));
        TextField name = new TextField();
        quantity.setPromptText("Qty");
        name.setPromptText("Name");
        quantity.setStyle("-fx-text-box-border: transparent");
        name.setStyle("-fx-text-box-border: transparent");
        ingredientPane.addRow(ingredientPane.getRowCount(), quantity, unit, name);
    }

    @FXML
    private void saveRecipe() throws IOException, SQLException {
        String groupName = accessibilityBox.getSelectionModel().getSelectedItem();
        ArrayList<Ingredient> ingredientList = getIngredientList();
        String warining = checkCorrectness(getIngredientList());
        if (warining.equals("")){
            Integer publicity = null;
            if (!groupName.equals("private")){
                publicity = DatabaseConnection.getGroupIdWithName(groupName, activeUser);
            }
            Integer preparationTime = getTimePreparationInMins();
            Double cost = getCost();
            Double portions = getPortions();
            Recipe recipe = new Recipe(null, titleField.getText(), activeUser.getUsername(), descriptionArea.getText(), publicity, getDateAdded(), preparationTime, cost, portions, ingredientList);
            recipe.setGroupName(groupName);
            int recipeId  = DatabaseConnection.addRecipe(recipe, activeUser);
            recipe.setId(recipeId);
            clearRecipe();
            activeUser.addUserRecipe(recipe);
            myRecipesTable.getItems().add(recipe);
            myRecipesTable.refresh();
        } else {
            showWarning(warining);
        }
    }

    private String checkCorrectness(ArrayList<Ingredient> ingredientList) {
        String warining = "";
        if (titleField.getText().equals(""))
            warining += "Please enter title\n";
        if (descriptionArea.getText().equals(""))
            warining += "Please enter description.\n";
        if (ingredientList.size() == 0)
            warining += "Please add 1 or more ingredient\n";
        if (!checkIfDouble(costField.getText()) && !costField.getText().equals("")) {
            warining += "Please enter correct cost value\n";
            costField.clear();
        }
        if (!checkIfDouble(portionField.getText()) && !portionField.getText().equals("")){
            warining += "Please enter correct number of portions value\n";
            portionField.clear();
        }
        if (!checkIfInteger(hrsField.getText()) && !hrsField.getText().equals("")) {
            warining += "Please enter correct hours value\n";
            hrsField.clear();
        }
        if (!checkIfInteger(minsField.getText()) && !minsField.getText().equals("")) {
            warining += "Please enter correct hours value\n";
            minsField.clear();
        }
        return warining;
    }

    @FXML
    private void clearRecipe() {
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

    private void showWarning(String warning) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Not enough information!");
        alert.setContentText(warning);
        alert.showAndWait();
    }


    private ArrayList<Ingredient> getIngredientList() {
        ArrayList<Ingredient> ingredientList = new ArrayList<>();
        for (int i = 0; i < ingredientPane.getRowCount(); i++){
            TextField quantityField = (TextField) ingredientPane.getChildren().get(i*3);
            ChoiceBox<String> units = (ChoiceBox<String>) ingredientPane.getChildren().get(i*3+1);
            TextField nameField = (TextField) ingredientPane.getChildren().get(i*3+2);
            String quantityStr = quantityField.getText();
            String unit = units.getSelectionModel().getSelectedItem();
            String name = nameField.getText();
            if (quantityStr != null && unit != null && name != null){
                if (!quantityStr.equals("") && quantityStr.matches("\\d+(\\.\\d+)?") && !unit.equals("") && !name.equals("")){
                    Double quantity = Double.parseDouble(quantityStr);
                    Ingredient ingredient = new Ingredient(null, quantity, unit, name);
                    ingredientList.add(ingredient);
                }
            }
        }
        return ingredientList;
    }

    private Double getCost(){
        if (costField.getText().equals(""))
            return 0.0;
        else
            return Double.parseDouble(costField.getText());
    }
    private Double getPortions() {
        if (portionField.getText().equals(""))
            return 1.0;
        else
            return Double.parseDouble(portionField.getText());
    }

    private Integer getTimePreparationInMins() {
        if (hrsField.getText().equals("") && minsField.getText().equals(""))
            return 0;
        else {
            int mins = 0;
            if (!minsField.getText().equals(""))
                    mins += Integer.parseInt(minsField.getText());
            if (!hrsField.getText().equals(""))
                mins += 60 * Integer.parseInt(hrsField.getText());
            return mins;
        }
    }
    private String getDateAdded() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(dateTimeFormatter);
    }

    private boolean checkIfDouble(String match){
        return match.matches("\\d+(\\.\\d+)?");
    }

    private boolean checkIfInteger(String match) {
        return match.matches("\\d+");
    }

    private void setMyRecipesTable() {
        myRecipesTable.getItems().clear();
        TableColumn<Recipe, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Recipe, String> groupNameColumn = new TableColumn<>("Group name");
        groupNameColumn.setCellValueFactory(new PropertyValueFactory<>("groupName"));
        TableColumn<Recipe, String> dateAddedColumn = new TableColumn<>("Date added");
        dateAddedColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
        ObservableList<Recipe> RecipeList = FXCollections.observableArrayList(activeUser.getUserRecipes());
        myRecipesTable.setItems(RecipeList);
        myRecipesTable.getColumns().addAll(nameColumn, groupNameColumn, dateAddedColumn);
    }
    private void setFavTable() {
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
        favTable.getColumns().addAll(nameColumn, authorColumn, dateAddedColumn, costColumn, timeColumn);
    }

    private void ShowRecipe(Recipe recipe) {
        FXMLLoader loader = loadFXML(new RecipePane(recipe, activeUser, null), "/resources/recipePage.fxml");
        changeScene(loader);

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
                    try {
                        DatabaseConnection.deleteRecipe(activeUser, recipe);
                        activeUser.getUserRecipes().remove(recipe);
                        myRecipesTable.getItems().remove(recipe);
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                    myRecipesTable.refresh();
                }
            }
        });
        return delete;
    }
}
