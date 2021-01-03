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
import main.converterModel.Converter;
import main.recipeModel.Recipe;
import main.userModel.User;

import java.io.IOException;
import java.text.DecimalFormat;

public class ScalePane {
    private final Recipe recipe;
    private final User activeUser;
    private final Converter converter;
    ObservableList<String> shapeList = FXCollections.observableArrayList("Round", "Rectangular");

    public ScalePane(Recipe recipe, User activeUser) {
        this.recipe = recipe;
        this.activeUser = activeUser;
        this.converter = new Converter();
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
    public TextArea Height1;
    public TextArea Height2;
    public Label moldLabel;
    public Button moldButton;

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
        moldButton.setOnAction(e->{
            okMoldAction();
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
                inRecipeArea1.setText("");
                inRecipeArea2.setText("");
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
                IHaveArea1.setText("");
                IHaveArea2.setText("");
                IHaveSize.setText("Diameter:");
                IHaveArea1.setVisible(true);
                x2.setText("cm");
            }
        }
    }

    private void okMoldAction(){
        String a1 = IHaveArea1.getText();
        String b1 = IHaveArea2.getText();
        String h1 = Height1.getText();
        String a2 = inRecipeArea1.getText();
        String b2 = inRecipeArea2.getText();
        String h2 = Height2.getText();
        double IHaveVolume;
        double inRecipeVolume;
        if (a1.equals("") || h1.equals("") || a2.equals("")|| h2.equals("")){
            moldLabel.setText("Too little informations");
            return;
        }
        try {
            if (b1.equals("")) {
                IHaveVolume = converter.getRoundMoldVolume(Double.parseDouble(a1), Double.parseDouble(h1));
            } else {
                IHaveVolume = converter.getRectangularMoldVolume(Double.parseDouble(a1), Double.parseDouble(b1), Double.parseDouble(h1));
            }
            if (b2.equals("")) {
                inRecipeVolume = converter.getRoundMoldVolume(Double.parseDouble(a2), Double.parseDouble(h2));
            } else {
                inRecipeVolume = converter.getRectangularMoldVolume(Double.parseDouble(a2), Double.parseDouble(b2), Double.parseDouble(h2));
            }
            double result = inRecipeVolume / IHaveVolume;
            moldLabel.setWrapText(true);
            DecimalFormat df = new DecimalFormat("###.###");

            moldLabel.setText("You need to use " + df.format(result) + "x ingredients in recipe");
        }
        catch(NumberFormatException e){
            moldLabel.setText("Wrong values were given");
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