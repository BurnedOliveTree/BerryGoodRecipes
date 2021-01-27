package main.recipeModel;
import main.controller.Status;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class Recipe {
    private String dateAdded;
    private String author;
    private Integer id;
    private Double cost;
    private String name;
    private Integer accessibility;
    private String groupName;
    private Double portionNumber;
    private String prepareMethod;
    private Integer prepareTime; // in minutes
    private ArrayList<Ingredient> ingredientList;
    private String avgRate;
    private Status favoriteStatus = Status.none;

    // constructors
    public Recipe() {}

    public Recipe(Integer id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.ingredientList = new ArrayList<>();
        this.groupName = "private";
        this.portionNumber = 1.0;
        setDateAdded();
    }

    public Recipe(Integer id, String name, String author, String prepareMethod, Integer accessibility, String newDateAdded, Integer prepareTime, Double cost, Double portion_number, ArrayList<Ingredient> ingredientList) {
        this.name = name;
        this.author = author;
        this.accessibility = accessibility;
        this.prepareMethod = prepareMethod;
        this.portionNumber = portion_number;
        this.cost = cost;
        this.prepareTime = prepareTime;
        this.id = id;
        this.dateAdded = newDateAdded;
        this.setIngredientList(ingredientList);
    }

    public Recipe(Integer id, String name, String author, String prepareMethod, Integer accessibility, Integer prepareTime, Double cost, Double portion_number, ArrayList<Ingredient> ingredientList) {
        this.name = name;
        this.author = author;
        this.accessibility = accessibility;
        this.prepareMethod = prepareMethod;
        this.portionNumber = portion_number;
        this.cost = cost;
        this.prepareTime = prepareTime;
        this.id = id;
        setDateAdded();
        this.setIngredientList(ingredientList);
    }

    public Recipe(Recipe that) {
        this(that.getId(), that.getName(), that.getAuthor(), that.getPrepareMethod(), that.getAccessibility(), that.getDateAdded(), that.getPrepareTime(), that.getPortionNumber(), that.getPortionNumber(),null);
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for (Ingredient ingredient: that.getIngredientList()) {
            ingredients.add(new Ingredient(ingredient));
        }
        this.ingredientList = ingredients;
    }

    // operations on attributes

    public double scaleIngredientList(Double newNumPortions) {
        // scale ingredient taking into account the changed number of portions
        if (portionNumber != null) {
            double scale = newNumPortions / portionNumber;
            for (Ingredient ingredient : this.ingredientList) {
                if (scale > 0) {
                    ingredient.setQuantity(ingredient.getQuantity()*scale);
                }
                else
                    throw new IllegalArgumentException("Value must be greater than 0");
            }
            portionNumber = newNumPortions;
            return scale;
        }
        return 0.0;
    }

    // file operations

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
            file.write("Preparation time: " + this.getPrepareTime() + "\n");
            file.write("Number of portions: " + this.portionNumber + "\n");
            file.close();
        } catch (IOException err) {
            System.out.println("Error: ");
            err.printStackTrace();
        }
    }

    public void saveToFile() {
        saveToFile(this.name);
    }

    public boolean deleteFile(String filename) {
        File file = new File(filename);
        return file.delete();
    }


    // getter

    public String getAuthor() {
        return author;
    }

    public double getCost() { return cost; }

    public String getName() {
        return name;
    }

    public Integer getId() {return id;}

    public Integer getAccessibility() {
        return Objects.requireNonNullElse(this.accessibility, 0);
    }

    public double getPortionNumber() {
        return Objects.requireNonNullElse(this.portionNumber, 1.0);
    }

    public String getPrepareMethod() {
        return Objects.requireNonNullElse(this.prepareMethod, "");
    }

    public Integer getPrepareTime() {
        return Objects.requireNonNullElse(this.prepareTime, 0);
    }

    public ArrayList<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public String getAvgRate() {
        return this.avgRate;
    }

    public String getDateAdded() {return dateAdded;}

    public Status getFavoriteStatus() {
        return favoriteStatus;
    }

    public String getGroupName() {  return this.groupName; }

    // setter

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void setPrepareTime(Integer prepareTime) {
        this.prepareTime = prepareTime;
    }

    public void setPortionNumber(double portionNumber) {
         this.portionNumber = portionNumber;
    }

    public void setAvgRate(String avgRate) { this.avgRate = avgRate; }

    public void setId(int recipeId) { this.id = recipeId; }

    public void setFavoriteStatus(Status status) {
        this.favoriteStatus = status;
    }

    public void setGroupName(String groupName) { this.groupName = groupName; }

    public void setDateAdded() {
        // gets the date when the recipe was created
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.dateAdded = date.format(dateTimeFormatter);
    }

    public void setIngredientList(ArrayList<Ingredient> ingredients) {
        this.ingredientList = new ArrayList<>();
        if (ingredients != null) {
            for (Ingredient ingredient: ingredients) {
                addIngredient(ingredient);
            }
        }
    }

    public boolean addIngredient(Ingredient ingredient) {
        if (!ingredientList.contains(ingredient)) {
            ingredientList.add(ingredient);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object r){
        // overload of the base method comparing objects by ID
        if (r == this) {
            return true;
        } else if (!(r instanceof Recipe)) {
            return false;
        } else  {
            return id.equals(((Recipe) r).getId());
        }
    }

}
