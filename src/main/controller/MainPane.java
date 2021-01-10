package main.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import main.DatabaseConnection;
import main.Main;
import main.userModel.User;

import java.io.IOException;
import java.sql.SQLException;

public class MainPane extends OrdinaryButtonAction {
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
    @FXML private TilePane tilePain;
    @FXML public TextField search;
    @FXML private ContextMenu searchContext;

    public MainPane(User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML
    void initialize() throws SQLException, IOException {
        DatabaseConnection.fillResults(this, tilePain);
        if (DatabaseConnection.theme.equals("light") || DatabaseConnection.theme.equals("winter")) {
            logo.setImage(new Image("icons/berryLogo.png"));
            recipePic.setImage(new Image("icons/berryRecipe.png"));
            socialPic.setImage(new Image("icons/berryGroup.png"));
            basketPic.setImage(new Image("icons/berryBasket.png"));
            settingsPic.setImage(new Image("icons/berryCog.png"));
        }
        setButtonActivity();

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
            settingsButton.getItems().get(0).setDisable(false);
            settingsButton.getItems().get(1).setDisable(false);
            socialButton.setDisable(false);
            myRecipesButton.setDisable(false);
            basketButton.setDisable(false);
        }
        else {
            loginButton.setText("Sign in");
            settingsButton.getItems().get(0).setDisable(true);
            settingsButton.getItems().get(1).setDisable(true);
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
                result = result + "or lower(" + query + ") like lower('%" + args[i] + "%')";
        }
        result = result + ")";
        return result;
    }

    @FXML
    public void search(ActionEvent ae) throws SQLException, IOException {
        query = search.getText();
        System.out.println(query);
        String args = "";
        if (query.contains(":")) {
            if (query.contains("with:")) {
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
        }
        query = "lower(rcp.name) like lower('%" + query + "%')" + args + " order by " + orderBy;
        System.out.println(query);
        DatabaseConnection.fillResults(this, tilePain, query);
    }

    @FXML
    public void onMyRecipesAction(MouseEvent mouseEvent) {
        mouseEvent.consume();
        changeScene(myRecipesButton, loadFXML(new RecipeAdminPane(activeUser), "/RecipeAdminPage.fxml"));
    }

    @FXML
    public void onSocialButtonClick(MouseEvent mouseEvent) {
        mouseEvent.consume();
        changeScene(myRecipesButton, loadFXML(new UserAdminPane(activeUser), "/userAdminPage.fxml"));
    }

    @FXML
    public void onBasketButtonClick(MouseEvent mouseEvent) {
        mouseEvent.consume();
        FXMLLoader loader = loadFXML(new ShoppingListPane(activeUser, new MainPane(activeUser)), "/shoppingListPage.fxml");
        changeScene(basketButton, loader);
    }

    public void onRecipeClick(Button button, int RecipeID) {
        FXMLLoader loader = loadFXML(new RecipePane(DatabaseConnection.getSelectedRecipe(RecipeID), activeUser), "/resources/recipePage.fxml");
        changeScene(button, loader, true);
    }

    @FXML
    public void onSignInButtonClick(MouseEvent mouseEvent) throws IOException {
        // create a new Window with sign in
        mouseEvent.consume();
        if (activeUser != null) {
            // log user out
            activeUser = null;
            Main.activeUser = null;
            setButtonActivity();
        }
        else {
            FXMLLoader loader = loadFXML(new LogInWindow(this), "/resources/logInWindow.fxml");
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/resources/" + DatabaseConnection.theme + ".css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Sign In");
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    public void onHelpButtonClick(MouseEvent mouseEvent) {
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
    public void onThemeLightSelection() {
        DatabaseConnection.theme = "light";
        resetTheme();
    }

    @FXML
    public void onThemeDarkSelection() {
        DatabaseConnection.theme = "dark";
        resetTheme();
    }

    @FXML
    public void onThemeWinterSelection() {
        DatabaseConnection.theme = "winter";
        resetTheme();
    }

    @FXML
    public void onThemeSpringSelection() {
        DatabaseConnection.theme = "spring";
        resetTheme();
    }

    @FXML
    public void onNameOrderSelection() {
        orderBy = "rcp.name";
        System.out.println(orderBy);
    }

    @FXML
    public void onCostOrderSelection() {
        orderBy = "rcp.cost";
        System.out.println(orderBy);
    }

    @FXML
    public void onTimeOrderSelection() {
        orderBy = "rcp.preparation_time";
        System.out.println(orderBy);
    }

    @FXML
    public void onRatingOrderSelection() {
        orderBy = "rating desc";
        System.out.println(orderBy);
    }

    public void resetTheme() {
        logo.getScene().getStylesheets().remove(0);
        logo.getScene().getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
        if (DatabaseConnection.theme.equals("light") || DatabaseConnection.theme.equals("winter")) {
            logo.setImage(new Image("icons/berryLogo.png"));
            recipePic.setImage(new Image("icons/berryRecipe.png"));
            socialPic.setImage(new Image("icons/berryGroup.png"));
            basketPic.setImage(new Image("icons/berryBasket.png"));
            settingsPic.setImage(new Image("icons/berryCog.png"));
        }
        else {
            logo.setImage(new Image("icons/raspLogo.png"));
            recipePic.setImage(new Image("icons/raspRecipe.png"));
            socialPic.setImage(new Image("icons/raspGroup.png"));
            basketPic.setImage(new Image("icons/raspBasket.png"));
            settingsPic.setImage(new Image("icons/raspCog.png"));
        }
    }
}
