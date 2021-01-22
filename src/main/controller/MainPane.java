package main.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import main.DatabaseConnection;
import main.Main;
import main.recipeModel.Recipe;
import main.userModel.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainPane extends BasicPaneActions {
    private String query;
    private String orderBy = "rcp.name";
    public User activeUser;
    @FXML private ImageView logo;
    @FXML private Button loginButton;
    @FXML private MenuButton settingsButton;
    @FXML private Button myRecipesButton;
    @FXML private Button socialButton;
    @FXML private Button basketButton;
    @FXML private ImageView recipePic;
    @FXML private ImageView socialPic;
    @FXML private ImageView basketPic;
    @FXML private ImageView settingsPic;
    @FXML private ImageView helpPic;
    @FXML private ImageView searchPic;
    @FXML public TilePane tilePain;
    @FXML public TextField search;
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label passwordError;
    @FXML private ContextMenu searchContext;
    @FXML private Menu unitSystemMenu;

    public MainPane(User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML
    void initialize() throws SQLException, IOException {
        setRecipeTiles(DatabaseConnection.search(this.activeUser));
        setUnitSystemMenu(DatabaseConnection.getUnitSystems());
        if (DatabaseConnection.isThemeLight()) {
            logo.setImage(new Image("icons/berryLogo.png"));
            recipePic.setImage(new Image("icons/berryRecipe.png"));
            socialPic.setImage(new Image("icons/berryGroup.png"));
            basketPic.setImage(new Image("icons/berryBasket.png"));
            settingsPic.setImage(new Image("icons/berryCog.png"));
            helpPic.setImage(new Image("icons/berryHelp.png"));
            searchPic.setImage(new Image("icons/berrySearch.png"));
        }
        setButtonActivity();
        Platform.runLater(() -> loginButton.requestFocus());
        search.focusedProperty().addListener((ov, oldV, newV) -> {
            if (newV) {
                searchContext.show(search, Side.BOTTOM, 0, 0);
            }
            else {
                searchContext.hide();
            }
        });
    }

    public void setButtonActivity() {
        if (activeUser != null) {
            loginButton.setText("Sign out");
            settingsButton.getItems().get(0).setVisible(true);
            settingsButton.getItems().get(1).setVisible(true);
            settingsButton.getItems().get(3).setVisible(true);
            socialButton.setDisable(false);
            myRecipesButton.setDisable(false);
            basketButton.setDisable(false);
        }
        else {
            loginButton.setText("Sign in");
            settingsButton.getItems().get(0).setVisible(false);
            settingsButton.getItems().get(1).setVisible(false);
            settingsButton.getItems().get(3).setVisible(false);
            socialButton.setDisable(true);
            myRecipesButton.setDisable(true);
            basketButton.setDisable(true);
        }
    }

    private String[] split_search(String arg) {
        int argPosStart = query.indexOf(arg);
        query = query.replaceFirst(arg, "");
        String result = query.substring(argPosStart);
        int argPosEnd = result.indexOf(" ");
        if (argPosEnd == -1) {
            query = query.replaceFirst("[ ]?" + query.substring(argPosStart), "");
        } else {
            result = result.substring(0, argPosEnd);
            query = query.replaceFirst("[ ]?" + query.substring(argPosStart, argPosStart + argPosEnd + 1), "");
        }
        return result.split(",");
    }

    private String multiple_search(String[] args, String query, boolean isNumber) {
        String result = "";
        if (isNumber) {
            result = result + " and (" + query+" " + args[args.length - 1];
            for (int i = args.length - 2; i >= 0; i--)
                result = result + "or " + query + " " + args[i];
        }
        else {
            result = result + " and (lower(" + query + ") like lower('%" + args[args.length - 1] + "%')";
            for (int i = args.length - 2; i >= 0; i--)
                result = result + " or lower(" + query + ") like lower('%" + args[i] + "%')";
        }
        result = result + ")";
        return result;
    }

    @FXML
    public void search(ActionEvent ae) throws SQLException, IOException {
        query = search.getText();
        System.out.println(query);
        String args = "";
        List<Integer> groupID = null;
        if (query.contains(":")) {
            while (query.contains("with:")) {
                String[] tempList = split_search("with:");
                args = args + multiple_search(tempList, "ing.ingredient_name", false);
            }
            if (query.contains("user:")) {
                String[] tempList = split_search("user:");
                args = args + multiple_search(tempList, "rcp.owner_name", false);
            }
            if (query.contains("maxcost:")) {
                String[] tempList = split_search("maxcost:");
                args = args + multiple_search(tempList, "rcp.cost <", true);
            }
            if (query.contains("mincost:")) {
                String[] tempList = split_search("mincost:");
                args = args + multiple_search(tempList, "rcp.cost >", true);
            }
            if (query.contains("maxtime:")) {
                String[] tempList = split_search("maxtime:");
                args = args + multiple_search(tempList, "rcp.preparation_time <", true);
            }
            if (query.contains("mintime:")) {
                String[] tempList = split_search("mintime:");
                args = args + multiple_search(tempList, "rcp.preparation_time >", true);
            }
            if (query.contains("maxrating:")) {
                String[] tempList = split_search("maxrating:");
                args = args + multiple_search(tempList, "CALC_RATING(rcp.RECIPE_ID) <", true);
            }
            if (query.contains("minrating:")) {
                String[] tempList = split_search("minrating:");
                args = args + multiple_search(tempList, "CALC_RATING(rcp.RECIPE_ID) >", true);
            }
            if (query.contains("group:")) {
                String[] tempList = split_search("group:");
                groupID = DatabaseConnection.getGroupByName(tempList);
            }
        }
        query = "lower(rcp.name) like lower('%" + query + "%')" + args + " order by " + orderBy;
        setRecipeTiles(DatabaseConnection.search(this.activeUser, query, groupID));
    }

    private void setRecipeTiles(List<Recipe> recipeList) {
        tilePain.getChildren().clear();
        if (recipeList == null)
            return;
        List<GridPane> panelist = new ArrayList<>();
        for (Recipe recipe: recipeList) {
            GridPane tempPane = new GridPane();
            for (int i = 0; i < 3; i++) {
                tempPane.getRowConstraints().add(new RowConstraints(32));
            }
            for (int i = 0; i < 6; i++) {
                ColumnConstraints columnConstraints = new ColumnConstraints(32);
                columnConstraints.setHalignment(HPos.CENTER);
                tempPane.getColumnConstraints().add(columnConstraints);
            }
            tempPane.setPrefSize(192, 96);
            Button tempButton = new Button(recipe.getName());
            tempButton.setWrapText(true);
            tempButton.setTextAlignment(TextAlignment.CENTER);
            tempButton.setPrefSize(192, 64);
            int tempInt = recipe.getId();
            tempButton.setOnMouseClicked(mouseEvent -> {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    FXMLLoader loader = loadFXML(new RecipePane(DatabaseConnection.getSelectedRecipe(tempInt), activeUser, new MainPane(activeUser)), "/resources/recipePage.fxml");
                    changeScene(basketButton, loader);
                } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                    FXMLLoader loader = loadFXML(new RecipePane(DatabaseConnection.getSelectedRecipe(tempInt), activeUser, null), "/resources/recipePage.fxml");
                    changeScene(null, loader);
                }
        });
            tempPane.add(tempButton, 0, 0, 6, 2);
            String tempString = recipe.getAvgRate();
            if (tempString == null)
                tempString = "N/A";
            else
                tempString = tempString + "/10";
            tempPane.add(new ImageView(new Image("icons/star.png")), 0, 2, 1, 1);
            tempPane.add(new Label(tempString), 1, 2, 1, 1);
            tempString = Integer.toString(recipe.getPrepareTime());
            if (tempString.equals("0"))
                tempString = "N/A";
            tempPane.add(new ImageView(new Image("icons/time.png")), 2, 2, 1, 1);
            tempPane.add(new Label(tempString), 3, 2, 1, 1);
            tempString = Integer.toString((int) recipe.getCost());
            if (tempString.equals("0"))
                tempString = "N/A";
            tempPane.add(new ImageView(new Image("icons/coin.png")), 4, 2, 1, 1);
            tempPane.add(new Label(tempString), 5, 2, 1, 1);
            panelist.add(tempPane);
        }
        tilePain.getChildren().addAll(panelist);
    }

    private void setUnitSystemMenu(List<String> unitSystemNames) {
        List<MenuItem> itemList = new ArrayList<>();
        for (String s: unitSystemNames) {
            MenuItem tempItem = new MenuItem(s);
            tempItem.setOnAction(e -> activeUser.setDefaultUnitSystem(tempItem.getText()));
            itemList.add(tempItem);
        }
        unitSystemMenu.getItems().clear();
        unitSystemMenu.getItems().addAll(itemList);
    }

    @FXML
    private void onMyRecipesAction(MouseEvent mouseEvent) {
        mouseEvent.consume();
        changeScene(myRecipesButton, loadFXML(new RecipeAdminPane(activeUser), "/recipeAdminPage.fxml"));
    }

    @FXML
    private void onSocialButtonClick(MouseEvent mouseEvent) {
        mouseEvent.consume();
        changeScene(myRecipesButton, loadFXML(new UserAdminPane(activeUser), "/userAdminPage.fxml"));
    }

    @FXML
    private void onBasketButtonClick(MouseEvent mouseEvent) throws IOException, SQLException {
        mouseEvent.consume();
        FXMLLoader loader = loadFXML(new ShoppingListPane(activeUser, new MainPane(activeUser)), "/shoppingListPage.fxml");
        changeScene(basketButton, loader);
    }

    @FXML
    private void onSignInButtonClick(MouseEvent mouseEvent) throws IOException, SQLException {
        // create a new Window with sign in
        mouseEvent.consume();
        if (activeUser != null) {
            // log user out
            DatabaseConnection.saveUser(activeUser);
            activeUser = null;
            Main.activeUser = null;
            setButtonActivity();
        }
        else {
            FXMLLoader loader = loadFXML(new LogInWindow(this), "/resources/logInWindow.fxml");

            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            scene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
            if (DatabaseConnection.isThemeLight())
                stage.getIcons().add(new Image("icons/berryLogo.png"));
            else
                stage.getIcons().add(new Image("icons/raspLogo.png"));
            stage.setTitle("Sign in");
            stage.setScene(scene);
            stage.setMaxWidth(180);
            stage.setMaxHeight(200);
            stage.showAndWait();
        }
    }

    @FXML
    private void onHelpButtonClick(MouseEvent mouseEvent) {
        mouseEvent.consume();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("How to use search");
        alert.setGraphic(null);
        alert.setContentText("Typing normal text in the search field will search for any title that contains these" +
                "words.\nYou can also use a function to filter the search result.\nSyntax for such is:\n[function1]:" +
                "[arg1],[arg2],[arg3] [function2]:[arg1]\nfor example: \"ciasto with:masło,mleko maxcost:100\"");
        alert.showAndWait();
    }

    @FXML
    private void onSearchButtonClick(MouseEvent mouseEvent) throws IOException, SQLException {
        mouseEvent.consume();
        search(new ActionEvent());
    }

    @FXML
    private void onPasswordChangeClick(ActionEvent actionEvent) throws IOException, SQLException {
        passwordError.setText(DatabaseConnection.setPassword(activeUser.getUsername(), newPasswordField.getText(), oldPasswordField.getText()));
    }

    @FXML
    private void onDeleteAccountRequest(ActionEvent actionEvent) throws IOException, SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete account");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText("Are you sure?\nYou will not be able to recover your account");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            DatabaseConnection.deleteUser(activeUser.getUsername());
            activeUser = null;
            Main.activeUser = null;
            setButtonActivity();
        }
    }

    @FXML
    private void onThemeLightSelection() {
        DatabaseConnection.theme = "light";
        resetTheme();
    }

    @FXML
    private void onThemeDarkSelection() {
        DatabaseConnection.theme = "dark";
        resetTheme();
    }

    @FXML
    private void onThemeWinterSelection() {
        DatabaseConnection.theme = "winter";
        resetTheme();
    }

    @FXML
    private void onThemeSpringSelection() {
        DatabaseConnection.theme = "spring";
        resetTheme();
    }

    @FXML
    private void onNameOrderSelection() {
        orderBy = "rcp.name";
        System.out.println(orderBy);
    }

    @FXML
    private void onCostOrderSelection() {
        orderBy = "rcp.cost";
        System.out.println(orderBy);
    }

    @FXML
    private void onTimeOrderSelection() {
        orderBy = "rcp.preparation_time";
        System.out.println(orderBy);
    }

    @FXML
    private void onRatingOrderSelection() {
        orderBy = "rating desc";
        System.out.println(orderBy);
    }

    @FXML private void onSearchWith() { search.setText(search.getText() + " with:"); }
    @FXML private void onSearchUser() { search.setText(search.getText() + " user:"); }
    @FXML private void onSearchMaxcost() { search.setText(search.getText() + " maxcost:"); }
    @FXML private void onSearchMincost() { search.setText(search.getText() + " mincost:"); }
    @FXML private void onSearchMaxtime() { search.setText(search.getText() + " maxtime:"); }
    @FXML private void onSearchMintime() { search.setText(search.getText() + " mintime:"); }
    @FXML private void onSearchMaxrating() { search.setText(search.getText() + " maxrating:"); }
    @FXML private void onSearchMinrating() { search.setText(search.getText() + " minrating:"); }
    @FXML private void onSearchGroup() { search.setText(search.getText() + " group:"); }

    private void resetTheme() {
        logo.getScene().getStylesheets().remove(0);
        ((Stage) logo.getScene().getWindow()).getIcons().remove(0);
        logo.getScene().getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
        if (DatabaseConnection.isThemeLight()) {
            logo.setImage(new Image("icons/berryLogo.png"));
            recipePic.setImage(new Image("icons/berryRecipe.png"));
            socialPic.setImage(new Image("icons/berryGroup.png"));
            basketPic.setImage(new Image("icons/berryBasket.png"));
            settingsPic.setImage(new Image("icons/berryCog.png"));
            helpPic.setImage(new Image("icons/berryHelp.png"));
            searchPic.setImage(new Image("icons/berrySearch.png"));
        }
        else {
            logo.setImage(new Image("icons/raspLogo.png"));
            recipePic.setImage(new Image("icons/raspRecipe.png"));
            socialPic.setImage(new Image("icons/raspGroup.png"));
            basketPic.setImage(new Image("icons/raspBasket.png"));
            settingsPic.setImage(new Image("icons/raspCog.png"));
            helpPic.setImage(new Image("icons/raspHelp.png"));
            searchPic.setImage(new Image("icons/raspSearch.png"));
        }
        ((Stage) logo.getScene().getWindow()).getIcons().add(logo.getImage());
    }
}
