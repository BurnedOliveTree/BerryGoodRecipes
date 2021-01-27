package main.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import main.DatabaseConnection;
import main.recipeModel.Converter;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class ScalePane extends BasicPaneActions {
    private final Converter converter;
    private final BasicPaneActions returnPane;
    ObservableList<String> shapeList = FXCollections.observableArrayList("Round", "Rectangular");

    public ScalePane(BasicPaneActions returnPane) {
        this.converter = new Converter();
        this.returnPane = returnPane;
    }

    @FXML
    public Button exitButton;
    @FXML private ImageView exitPic;
    public ChoiceBox<String> IHaveBox;
    public ChoiceBox<String> inRecipeBox;
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
    public TextArea unitArea1;
    public TextArea unitArea2;
    public ChoiceBox<String> unitChoiceBox1;
    public ChoiceBox<String> unitChoiceBox2;
    public Button okUnit;
    public Label unitLabel;

    @FXML
    private void initialize() throws IOException, SQLException {
        if (DatabaseConnection.isThemeLight()) {
            exitPic.setImage(new Image("icons/berryExit.png"));
        }
        exitButton.setOnAction(e -> onAction());
        unitChoiceBox1.setItems(FXCollections.observableArrayList(DatabaseConnection.getUnits()));
        unitChoiceBox2.setItems(FXCollections.observableArrayList(DatabaseConnection.getUnits()));
        IHaveBox.setItems(shapeList);
        inRecipeBox.setItems(shapeList);
        IHaveBox.setOnAction(e -> sizeBoxAction(IHaveBox));
        inRecipeBox.setOnAction(e -> sizeBoxAction(inRecipeBox));
        moldButton.setOnAction(e -> okMoldAction());
        okUnit.setOnAction(e-> {
            try {
                okUnitAction();
            } catch (IOException | SQLException err) {
                err.printStackTrace();
            }
        });
        unitArea2.setDisable(true);
        unitArea2.setStyle("-fx-opacity: 1;");
    }

    public void okUnitAction() throws IOException, SQLException {
        unitLabel.setText("");
        unitLabel.setWrapText(true);
        String unitArea1Text = unitArea1.getText(); // quantity of ingredient in first unit
        double quantity = 0.0;
        String firstChoice;
        String secondChoice;
        try {
            firstChoice = unitChoiceBox1.getValue(); // converter converts from that unit
            secondChoice = unitChoiceBox2.getValue(); // to this one
        }
        catch(NullPointerException e){
            unitLabel.setText("Not enough info");
            return;
        }
        try{
            quantity = Double.parseDouble(unitArea1Text);
        }catch(NumberFormatException e){
            unitLabel.setText("Wrong quantity");
        }
        unitArea2.setText(Double.toString(DatabaseConnection.convertUnit(quantity,firstChoice,secondChoice)));
    }

    private void sizeBoxAction(ChoiceBox<String> box) {
        if (box.getId().equals("inRecipeBox")) {
            if (inRecipeBox.getValue().equals("Rectangular")) { // both areas are set visibe bc we need two sides of rectangle
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
            if (IHaveBox.getValue().equals("Rectangular")) { // one area is set visible bc we only need a diameter of circle
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
        // converts mold sizes, displays how much of ingredient user need to use
        String a1 = IHaveArea1.getText();
        String b1 = IHaveArea2.getText();
        String h1 = Height1.getText();
        String a2 = inRecipeArea1.getText();
        String b2 = inRecipeArea2.getText();
        String h2 = Height2.getText();
        double IHaveVolume;
        double inRecipeVolume;
        moldLabel.setWrapText(true);
        if (a1.equals("") || h1.equals("") || a2.equals("")|| h2.equals("")){
            moldLabel.setText("Too little information");
            return;
        }
        try {
            if (b1.equals("")) {
                IHaveVolume = converter.getRoundMoldVolume(Double.parseDouble(a1), Double.parseDouble(h1)); // volume of round mold user has
            } else {
                IHaveVolume = converter.getRectangularMoldVolume(Double.parseDouble(a1), Double.parseDouble(b1), Double.parseDouble(h1)); // volume of rectangular mold user has
            }
            if (b2.equals("")) {
                inRecipeVolume = converter.getRoundMoldVolume(Double.parseDouble(a2), Double.parseDouble(h2)); // volume of round mold used in recipe
            } else {
                inRecipeVolume = converter.getRectangularMoldVolume(Double.parseDouble(a2), Double.parseDouble(b2), Double.parseDouble(h2)); // volume of rectangular mold used in recipe
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

    private void onAction() {
        FXMLLoader loader = loadFXML(returnPane, "/resources/recipePage.fxml");
        changeScene(exitButton, loader);
    }}