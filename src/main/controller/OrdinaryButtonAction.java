package main.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.DatabaseConnection;

import java.io.IOException;

public class OrdinaryButtonAction {
    public void onExitButtonAction() {};

    public FXMLLoader loadFXML(Object c, String path) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        loader.setController(c);
        return loader;
    }

    public void changeScene(Button button, FXMLLoader loader) {
        changeScene(button, loader, false);
    }

    public void changeScene(Button button, FXMLLoader loader, boolean showAndWait) {
        try {
            Scene scene = new Scene(loader.load());
            Stage stage;
            if (showAndWait)
                stage = new Stage();
            else
                stage = (Stage) button.getScene().getWindow();
            scene.getStylesheets().add(getClass().getResource("/resources/"+DatabaseConnection.theme+".css").toExternalForm());
            stage.setScene(scene);
            if (showAndWait)
                stage.showAndWait();
            else
                stage.show();
        } catch (IOException e) {
            System.err.printf("Error: %s%n", e.getMessage());
        }
    }

    public void setContentMenu(Control control, MenuItem...  menuItems) {
        ContextMenu menu = new ContextMenu();
        for (MenuItem menuItem: menuItems) {
            menu.getItems().add(menuItem);
        }
        control.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    menu.show(control, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
            }
        });
    }

}
