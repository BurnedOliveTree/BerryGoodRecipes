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

    // used in MainPane, RecipeAdminPane, RecipePane
    public void changeScene(FXMLLoader loader) {
        changeScene(null, loader, "BerryGood Recipes");
    }

    // used in MainPane, OpinionPane, RecipeAdminPane, RecipePane, ScalePane, ShoppingListPane, UserAdminPane
    public void changeScene(Node node, FXMLLoader loader) {
        changeScene(node, loader, "BerryGood Recipes");
    }

    // used in MainPane
    public void changeScene(FXMLLoader loader, String title) {
        changeScene(null, loader, title);
    }

    // used by above methods
    public void changeScene(Node node, FXMLLoader loader, String title) {
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
            if (node == null)
                stage.showAndWait();
            else
                stage.show();
        } catch (IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }

    // used in RecipeAdminPane...
    public void setContextMenu(Control control, MenuItem...  menuItems) {
        ContextMenu menu = new ContextMenu();
        for (MenuItem menuItem: menuItems) {
            menu.getItems().add(menuItem);
        }
        control.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                menu.show(control, mouseEvent.getScreenX(), mouseEvent.getScreenY());
            }
        });
    }

}
