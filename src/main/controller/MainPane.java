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
    public ImageView palettePic;
    public TilePane tilePain;
    public TextField search;

    public MainPane(User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML
    void initialize() throws SQLException {
        DatabaseConnection.fillResults(this, tilePain);
        if (DatabaseConnection.theme.equals("lightTheme") || DatabaseConnection.theme.equals("winter")) {
            logo.setImage(new Image("berryLogo.png"));
            recipePic.setImage(new Image("berryRecipe.png"));
            socialPic.setImage(new Image("berryGroup.png"));
            basketPic.setImage(new Image("berryBasket.png"));
            palettePic.setImage(new Image("berryPalette.png"));
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

    @FXML
    public void search(ActionEvent ae) throws SQLException {
        query = search.getText();
        System.out.println(query);
        String args = "";
        if (query.contains(":")) {
            if (query.contains("with:")) {
                String[] tempList = split_search("with:");
                args = args + " and (lower(ing.ingredient_name) like lower('%" + tempList[tempList.length - 1] + "%')";
                for (int i = tempList.length - 2; i >= 0; i--)
                    args = args + "or lower(ing.ingredient_name) like lower('%" + tempList[i] + "%')";
                args = args + ")";
            }
            if (query.contains("user:")) {
                String[] tempList = split_search("user:");
                args = args + " and (lower(rcp.owner_name) like lower('%" + tempList[tempList.length - 1] + "%')";
                for (int i = tempList.length - 2; i >= 0; i--)
                    args = args + "or (lower(rcp.owner_name) like lower('%" + tempList[i] + "%')";
                args = args + ")";
            }
            if (query.contains("maxcost:")) {
                String[] tempList = split_search("maxcost:");
                args = args + " and (rcp.cost < " + tempList[tempList.length - 1];
                for (int i = tempList.length - 2; i >= 0; i--)
                    args = args + "or rcp.cost < " + tempList[i];
                args = args + ")";
            }
            if (query.contains("time:")) {
                String[] tempList = split_search("time:");
                args = args + " and (rcp.preparation_time < " + tempList[tempList.length - 1];
                for (int i = tempList.length - 2; i >= 0; i--)
                    args = args + "or rcp.preparation_time < " + tempList[i];
                args = args + ")";
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
//        ShoppingListPane controller = new ShoppingListPane(activeUser);
//        loader.setController(controller);
        changeScene(socialButton, loader);
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
        DatabaseConnection.theme = "lightTheme";
        resetTheme();
    }

    @FXML
    public void onThemeDarkSelection() {
        DatabaseConnection.theme = "darkTheme";
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
        if (DatabaseConnection.theme.equals("lightTheme") || DatabaseConnection.theme.equals("winter")) {
            logo.setImage(new Image("berryLogo.png"));
            recipePic.setImage(new Image("berryRecipe.png"));
            socialPic.setImage(new Image("berryGroup.png"));
            basketPic.setImage(new Image("berryBasket.png"));
            palettePic.setImage(new Image("berryPalette.png"));
        }
        else {
            logo.setImage(new Image("raspLogo.png"));
            recipePic.setImage(new Image("raspRecipe.png"));
            socialPic.setImage(new Image("raspGroup.png"));
            basketPic.setImage(new Image("raspBasket.png"));
            palettePic.setImage(new Image("raspPalette.png"));
        }
    }
}
