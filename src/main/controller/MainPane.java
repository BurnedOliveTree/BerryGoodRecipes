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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class MainPane extends OrdinaryButtonAction {
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
    void initialize() {
        try {
            DatabaseConnection.fillResults(this, tilePain);
        } catch (SQLException e) { e.printStackTrace(); }
        if (DatabaseConnection.theme.equals("lightTheme") || DatabaseConnection.theme.equals("winter")) {
            try {
                logo.setImage(new Image(new FileInputStream("src/resources/berryLogo.png")));
                recipePic.setImage(new Image(new FileInputStream("src/resources/berryRecipe.png")));
                socialPic.setImage(new Image(new FileInputStream("src/resources/berryGroup.png")));
                basketPic.setImage(new Image(new FileInputStream("src/resources/berryBasket.png")));
                palettePic.setImage(new Image(new FileInputStream("src/resources/berryPalette.png")));
            } catch (FileNotFoundException e) { System.err.printf("Error: %s%n", e.getMessage()); }
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

    @FXML
    public void search(ActionEvent ae) {
        String query = search.getText();
        System.out.println(query);
        if (query.contains(":")) {
            int withPosStart = query.indexOf("with:");
//            System.out.println(withPosStart);
            query = query.replaceFirst("with:", "");
            System.out.println("query: "+query);
            String with = query.substring(withPosStart);
            System.out.println("with: "+with);
            int withPosEnd = with.indexOf(" ");
//            System.out.println(withPosEnd);
            if (withPosEnd == -1) {
                query = query.replaceFirst("[ ]?"+query.substring(withPosStart), "");
            }
            else {
                with = with.substring(0, withPosEnd);
                query = query.replaceFirst("[ ]?"+query.substring(withPosStart, withPosStart + withPosEnd + 1), "");
            }
            System.out.println("with: "+with);
            System.out.println("query: "+query);
            if (!query.equals(""))
                query = "lower(rcp.name) like lower('%" + query + "%') and lower(ing.ingredient_name) like lower('%" + with + "%')";
            else
                query = "lower(ing.ingredient_name) like lower('%" + with + "%')";
            System.out.println("query: "+query);
        }
        else
            query = "lower(rcp.name) like lower('%" + query + "%')";
        try {
            DatabaseConnection.fillResults(this, tilePain, query);
        } catch (SQLException throwables) { throwables.printStackTrace(); }
    }

    @FXML
    public void onMyRecipesAction(MouseEvent mouseEvent) {
        mouseEvent.consume();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RecipeAdminPage.fxml"));
        RecipeAdminPane controller = new RecipeAdminPane(activeUser);
        loader.setController(controller);
        changeScene(myRecipesButton, loader);
    }

    @FXML
    public void onSocialButtonClick(MouseEvent mouseEvent) {
        mouseEvent.consume();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/userAdminPage.fxml"));
        UserAdminPane controller = new UserAdminPane(activeUser);
        loader.setController(controller);
        changeScene(socialButton, loader);
    }

    @FXML
    public void onBasketButtonClick(MouseEvent mouseEvent) {
        mouseEvent.consume();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/shoppingListPage.fxml"));
//        ShoppingListPane controller = new ShoppingListPane(activeUser);
//        loader.setController(controller);
        changeScene(socialButton, loader);
    }

    public void onRecipeClick(Button button, int RecipeID) {
        try {
            FXMLLoader loader =  new FXMLLoader(getClass().getResource("/resources/recipePage.fxml"));
            loader.setController(new RecipePane(DatabaseConnection.getSelectedRecipe(RecipeID), activeUser));
            Scene mainPageScene = new Scene(loader.load());
            Stage stage = new Stage();
            mainPageScene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
            stage.setScene(mainPageScene);
            stage.showAndWait();
        } catch (SQLException | IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }

    @FXML
    public void onSignInButtonClick(MouseEvent mouseEvent) {
        // create a new Window with sign in
        try {
            mouseEvent.consume();
            if (activeUser != null) {
                // log user out
                activeUser = null;
                Main.activeUser = null;
                setButtonActivity();
                return;
            }
            try {
                System.out.println("Active user: "+activeUser.getUsername());
            } catch (NullPointerException e) {
                System.err.println("No active user");
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/logInWindow.fxml"));
            loader.setController(new LogInWindow(this));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Sign In");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());
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
        try {
            if (DatabaseConnection.theme.equals("lightTheme") || DatabaseConnection.theme.equals("winter")) {
                logo.setImage(new Image(new FileInputStream("src/resources/berryLogo.png")));
                recipePic.setImage(new Image(new FileInputStream("src/resources/berryRecipe.png")));
                socialPic.setImage(new Image(new FileInputStream("src/resources/berryGroup.png")));
                basketPic.setImage(new Image(new FileInputStream("src/resources/berryBasket.png")));
                palettePic.setImage(new Image(new FileInputStream("src/resources/berryPalette.png")));
            }
            else {
                logo.setImage(new Image(new FileInputStream("src/resources/raspLogo.png")));
                recipePic.setImage(new Image(new FileInputStream("src/resources/raspRecipe.png")));
                socialPic.setImage(new Image(new FileInputStream("src/resources/raspGroup.png")));
                basketPic.setImage(new Image(new FileInputStream("src/resources/raspBasket.png")));
                palettePic.setImage(new Image(new FileInputStream("src/resources/raspPalette.png")));
            }
        } catch (FileNotFoundException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }

    @Override
    public void onExitButtonAction() { }
}
