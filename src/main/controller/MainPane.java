package main.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
    public User activeUser;
    @FXML
    public ImageView logo;
    public Button loginButton;
    public Button myRecipesButton;
    public Button socialButton;
    public Button basketButton;
    public ImageView recipePic;
    public ImageView socialPic;
    public ImageView basketPic;
    public ImageView settingsPic;
    public TilePane tilePain;
    public TextField search;

    public MainPane(User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML
    void initialize() throws SQLException {
        DatabaseConnection.fillResults(this, tilePain);
        if (DatabaseConnection.theme.equals("light") || DatabaseConnection.theme.equals("winter")) {
            logo.setImage(new Image("icons/berryLogo.png"));
            recipePic.setImage(new Image("icons/berryRecipe.png"));
            socialPic.setImage(new Image("icons/berryGroup.png"));
            basketPic.setImage(new Image("icons/berryBasket.png"));
            settingsPic.setImage(new Image("icons/berryCog.png"));
        }
        setButtonActivity();
    }

    public void setButtonActivity() {
        if (activeUser != null) {
            loginButton.setText("Sign out");
            socialButton.setDisable(false);
            myRecipesButton.setDisable(false);
            basketButton.setDisable(false);
        }
        else {
            loginButton.setText("Sign in");
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
    public void search(ActionEvent ae) throws SQLException {
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
        query = "lower(rcp.name) like lower('%" + query + "%')" + args;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/shoppingListPage.fxml"));
        MainPane returnPane = new MainPane(activeUser);
        ShoppingListPane controller = new ShoppingListPane(activeUser, returnPane);
        loader.setController(controller);
        changeScene(basketButton, loader);
    }

    public void onRecipeClick(Button button, int RecipeID){
        try {
            FXMLLoader loader =  new FXMLLoader(getClass().getResource("/resources/recipePage.fxml"));
            loader.setController(new RecipePane(DatabaseConnection.getSelectedRecipe(RecipeID), activeUser));
            changeScene(button, loader, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/logInWindow.fxml"));
            loader.setController(new LogInWindow(this));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/resources/" + DatabaseConnection.theme + ".css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Sign In");
            stage.setScene(scene);
            stage.show();
        }
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
