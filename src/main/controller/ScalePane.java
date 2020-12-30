package main.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.DatabaseConnection;
import main.recipeModel.Recipe;
import main.userModel.User;

import java.io.IOException;

public class ScalePane {
    private final Recipe recipe;
    private final User activeUser;
    ObservableList<String> shapeList = FXCollections.observableArrayList("Round", "Rectangular");

    public ScalePane(Recipe recipe, User activeUser) {
        this.recipe = recipe;
        this.activeUser = activeUser;
    }

    ;

    @FXML
    public Button exitButton;
    public ChoiceBox IHaveBox;
    public ChoiceBox inRecipeBox;
    public Label inRecipeSize;
    public Label IHaveSize;
    public TextArea inRecipeArea1;
    public TextArea inRecipeArea2;
    public TextArea IHaveArea1;
    public TextArea IHaveArea2;
    public Label x1;
    public Label x2;
    public Label cm1;
    public Label cm2;


    @FXML
    private void initialize() {
        exitButton.setOnAction(e -> {
            onAction(exitButton, "/resources/recipePage.fxml");
        });
        IHaveBox.setItems(shapeList);
        inRecipeBox.setItems(shapeList);
        IHaveBox.setOnAction(e -> {
            sizeBoxAction(IHaveBox);
        });
        inRecipeBox.setOnAction(e -> {
            sizeBoxAction(inRecipeBox);
        });

    }

    private void sizeBoxAction(ChoiceBox box) {
        if (box.getId().equals("inRecipeBox")) {
            if (inRecipeBox.getValue().toString() == "Rectangular") {
                inRecipeArea1.setVisible(false);
                inRecipeArea2.setVisible(false);
                cm1.setText("");
                inRecipeSize.setText("Size:");
                inRecipeArea1.setVisible(true);
                inRecipeArea2.setVisible(true);
                x1.setText("x");
                cm1.setText("cm");
            } else {
                cm1.setText("");
                inRecipeArea1.setVisible(false);
                inRecipeArea2.setVisible(false);
                inRecipeSize.setText("Diameter:");
                inRecipeArea1.setVisible(true);
                x1.setText("cm");
            }
        }
        else {
            if (IHaveBox.getValue().toString() == "Rectangular") {
                IHaveArea1.setVisible(false);
                IHaveArea2.setVisible(false);
                cm2.setText("");
                IHaveSize.setText("Size:");
                IHaveArea1.setVisible(true);
                IHaveArea2.setVisible(true);
                x2.setText("x");
                cm2.setText("cm");
            } else {
                cm2.setText("");
                IHaveArea1.setVisible(false);
                IHaveArea2.setVisible(false);
                IHaveSize.setText("Diameter:");
                IHaveArea1.setVisible(true);
                x2.setText("cm");
            }
        }
    }



    private void  onAction(Button button, String namePath) {
        try {
            FXMLLoader loader =  new FXMLLoader(getClass().getResource(namePath));
            RecipePane controller = new RecipePane(this.recipe, activeUser);
            loader.setController(controller);
            Parent recipePage = loader.load();
            Scene recipePageScene = new Scene(recipePage);
            Stage stage = (Stage) exitButton.getScene().getWindow();
            recipePageScene.getStylesheets().add(getClass().getResource("/resources/"+ DatabaseConnection.theme+".css").toExternalForm());
            stage.setScene(recipePageScene);
            stage.show();
        } catch (IOException e) {
            System.err.println(String.format("Error: %s", e.getMessage()));}
    }}