package main.recipeModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Recipe {
    private String author;
    private Integer id;
    private Double cost;
    private String name;
    private Integer accessibility;
    private String groupName;
    private Double portionNumber;
    private String dateAdded;
    private String prepareMethod;
    private Integer prepareTime; // in minutes
    private ArrayList<Ingredient> ingredientList;
    private String avgRate;

    public Recipe() {}

    public Recipe(int id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public Recipe(Integer id, String name, String author, String prepareMethod, Integer accessibility, String dateAdded, Integer prepareTime, Double cost, Double portion_number, ArrayList<Ingredient> ingredientList) {
        this.name = name;
        this.author = author;
        this.accessibility = accessibility;
        this.prepareMethod = prepareMethod;
        this.ingredientList = ingredientList;
        this.dateAdded = dateAdded;
        this.portionNumber = portion_number;
        this.cost = cost;
        this.prepareTime = prepareTime;
        this.id = id;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void setPrepareTime(Integer prepareTime) {
        this.prepareTime = prepareTime;
    }

    private Ingredient findInIngredientList(String ingredientName)
    {
        for (Ingredient ingredient : this.ingredientList) {
            if (ingredientName.equals(ingredient.getName()))
                return ingredient;
        }
        return null;
    }

    public void editIngredientQuantity(Double quantity, String ingredientName)
    {
        Ingredient ingredient = findInIngredientList(ingredientName);
        if (quantity > 0 && ingredient != null)
            ingredient.setQuantity(quantity);
        else
            throw new IllegalArgumentException("Value must be greater than 0");
    }

    public void editIngredientUnit(String unit, String ingredientName)
    {
        Ingredient ingredient = findInIngredientList(ingredientName);
        // @TODO MARIANKA calculate method from unit
    }

    // scale ingredient taking into account the changed number of portions
    public void scaleIngredientList(double scale) {
        for (Ingredient ingredient : this.ingredientList) {
            if (scale > 0) {
                ingredient.setQuantity(ingredient.getQuantity()*scale);
            }
            else
                throw new IllegalArgumentException("Value must be greater than 0");
        }
    }

    @Override   // overload of the base method comparing objects by ID
    public boolean equals(Object r){
        if (r == this) {
            return true;
        } else if (!(r instanceof Recipe)) {
            return false;
        } else  {
            return id.equals(((Recipe) r).getId());
        }
    }

    public void saveToFile(String filename) {
        try {
            FileWriter file = new FileWriter(filename);
            file.write(this.name + "\n\nIngredients:\n");
            for (Ingredient ingredient: this.ingredientList){
                file.write(ingredient.getQuantity() + " " + ingredient.getUnit() + " "  + ingredient.getName() + "\n" );
            }
            file.write("\nPreparation method:\n" + this.prepareMethod);
            file.write("\nAdditional information: \n");
            file.write("Cost: " + this.cost + "\n");
            file.write("Preparation time: " + this.prepareTime + "\n");
            file.write("Number of portions: " + this.portionNumber + "\n");
            file.close();
        } catch (IOException err) {
            System.out.println("Error: ");
            err.printStackTrace();
        }

    }

    public void deleteFile(String filename) {
        File file = new File(filename);
        file.delete();
    }

    public void saveToFile() {
        saveToFile(this.name);
    }

    public String getAuthor() {
        return author;
    }

    public double getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }

    public Integer getAccessibility() {
        return accessibility;
    }

    public void setPortionNumber(double portionNumber) {
         this.portionNumber = portionNumber;
    }

    public double getPortionNumber() {
        return portionNumber;
    }

    public String getPrepareMethod() {
        return prepareMethod;
    }

    public Integer getPrepareTime() {
        return prepareTime;
    }

    public ArrayList<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public String getAvgRate() {
        return avgRate;
    }

    public void setAvgRate(String avgRate) { this.avgRate = avgRate; }

    public String getDateAdded() {return dateAdded;}

    public String getGroupName() {return groupName;}

    public Integer getId() {return id;}

    public void setGroupName(String groupName) {this.groupName = groupName; }


}
