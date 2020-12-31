package main.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import main.DatabaseConnection;
import main.Main;
import main.recipeModel.Recipe;
import main.userModel.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class MainPane {
    public User activeUser;
    @FXML
    public Button recipeLink;
    public Button loginButton;
    public ImageView logo;
    public Button myRecipesButton;
    public TilePane tilePain;

    public MainPane(User activeUser) {
        this.activeUser = activeUser;
    }

    @FXML
    void initialize() {
//        recipeLink.setText("Placki");
        try {
            DatabaseConnection.fillResults(this, tilePain);
        } catch (SQLException e) { e.printStackTrace(); }
        if (DatabaseConnection.theme.equals("lightTheme") || DatabaseConnection.theme.equals("winter")) {
            try {
                logo.setImage(new Image(new FileInputStream("src/resources/berryLogo.png")));
            } catch (FileNotFoundException e) { System.err.printf("Error: %s%n", e.getMessage()); }
        }
        if (activeUser != null) {
            loginButton.setText("Sign out");
            myRecipesButton.setDisable(false);
        }
        else
            myRecipesButton.setDisable(true);
    }

    @FXML
    public void onMyRecipesAction(MouseEvent mouseEvent) {
        mouseEvent.consume();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RecipeAdminPage.fxml"));
        RecipeAdminPane controller = new RecipeAdminPane(activeUser);
        loader.setController(controller);
        changeScene(myRecipesButton, loader);
    }

    private void changeScene(Button button,FXMLLoader loader) {
        try {
            Scene mainPageScene = new Scene(loader.load());
            Stage stage = (Stage) button.getScene().getWindow();
            mainPageScene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
            stage.setScene(mainPageScene);
            stage.show();
        } catch (IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());}
    }

    public void onRecipeClick(Button button, int RecipeID) {
        // change main Stage Scene to recipe Scene
        try {
            FXMLLoader loader =  new FXMLLoader(getClass().getResource("/resources/recipePage.fxml"));
            loader.setController(new RecipePane(DatabaseConnection.getRecipe(RecipeID), activeUser));   // TODO apparently it's about to not work
            changeScene(button, loader);
        } catch (SQLException e) {
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
                loginButton.setText("Sign in");
                myRecipesButton.setDisable(true);
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
        if (DatabaseConnection.theme.equals("lightTheme") || DatabaseConnection.theme.equals("winter")) {
            try {
                logo.setImage(new Image(new FileInputStream("src/resources/berryLogo.png")));
            } catch (FileNotFoundException e) {
                System.err.printf("Error: %s%n", e.getMessage());
            }
        }
        else {
            try {
                logo.setImage(new Image(new FileInputStream("src/resources/raspLogo.png")));
            } catch (FileNotFoundException e) {
                System.err.printf("Error: %s%n", e.getMessage());
            }
        }
    }
}
