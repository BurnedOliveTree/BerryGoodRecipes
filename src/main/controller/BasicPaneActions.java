package main.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import main.DatabaseConnection;

import java.io.IOException;
import java.util.Optional;

public class BasicPaneActions {
    // class with methods used by children class
    // stores the basic functions supported by a typical application window

    // used in MainPane, OpinionPane, RecipeAdminPane, RecipePane, ScalePane, ShoppingListPane, UserAdminPane
    // set controller to FXML
    public FXMLLoader loadFXML(Object c, String path) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        loader.setControllerFactory(param -> c);
        return loader;
    }

    // used in RecipeAdminPane
    public void changeScene(FXMLLoader loader) {
        changeScene(null, loader, "BerryGood Recipes", 0, 0);
    }

    // used in RecipePane
    public void changeScene(FXMLLoader loader, int maxWidth, int maxHeight) {
        changeScene(null, loader, "BerryGood Recipes", maxWidth, maxHeight);
    }
    
    // used in MainPane
    public void changeScene(FXMLLoader loader, String title, int maxWidth, int maxHeight) {
        changeScene(null, loader, title, maxWidth, maxHeight);
    }

    // used in MainPane, OpinionPane, RecipeAdminPane, RecipePane, ScalePane, ShoppingListPane, UserAdminPane
    public void changeScene(Node node, FXMLLoader loader) {
        changeScene(node, loader, "BerryGood Recipes", 0, 0);
    }

    // currently not used
    public void changeScene(FXMLLoader loader, String title) {
        changeScene(null, loader, title, 0, 0);
    }

    // used by above methods
    public void changeScene(Node node, FXMLLoader loader, String title, int maxWidth, int maxHeight) {
        try {
            Scene scene = new Scene(loader.load());
            Stage stage;
            if (node == null)
                stage = new Stage();
            else
                stage = (Stage) node.getScene().getWindow();
            scene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
            if (DatabaseConnection.isThemeLight())
                stage.getIcons().add(new Image("icons/berryLogo.png"));
            else
                stage.getIcons().add(new Image("icons/raspLogo.png"));
            if (title != null)
                stage.setTitle(title);
            stage.setScene(scene);
            if (maxWidth != 0)
                stage.setMaxWidth(maxWidth);
            if (maxHeight != 0)
                stage.setMaxHeight(maxHeight);
            if (node == null)
                stage.showAndWait();
            else
                stage.show();
        } catch (IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }

    // used in RecipeAdminPane, RecipePane, ShoppingListPane, UserAdminPane
    public void setContextMenu(Node node, MenuItem...  menuItems) {
        ContextMenu menu = new ContextMenu();
        for (MenuItem menuItem: menuItems) {
            menu.getItems().add(menuItem);
        }
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                menu.show(node, mouseEvent.getScreenX(), mouseEvent.getScreenY());
            }
        });
    }

    // used in MainPane, RecipeAdminPane, UserAdminPane
    public Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String headerText, String contextText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setGraphic(null);
        alert.setContentText(contextText);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
        return alert.showAndWait();
    }
}
