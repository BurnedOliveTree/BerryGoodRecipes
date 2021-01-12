package main.recipeModel;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Recipe {
    private String author;
    private Integer id;
    private double cost;
    private String name;
    private int accessibility;
    private String groupName;
    private double portionNumber;
    private String dateAdded;
    private String prepareMethod;
    private int prepareTime; // in minutes
    // @TODO private LinkList<Opinion> opinions = new LinkList<Opinion>();
    private ArrayList<Ingredient> ingredientList;
    private String avgRate;

    public Recipe() {}

    public Recipe(int id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public Recipe(int id, String name, String author, String prepareMethod, int accessibility, String dateAdded, int prepareTime, double cost, double portion_number, ArrayList<Ingredient> ingredientList) {
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

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setPortionNumber(int portionNumber) {
        this.portionNumber = portionNumber;
    }

    public void setPrepareTime(int prepareTime) {
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

    public void editIngredientUnit(Unit unit, String ingredientName)
    {
        Ingredient ingredient = findInIngredientList(ingredientName);
        // @TODO calculate method from unit
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
        // @TODO update and add to GUI
        try {
            FileWriter file = new FileWriter(filename);
            file.write(this.name + "\nIngredients:\n");
            for (Ingredient ingredient: this.ingredientList){
                file.write(ingredient.getQuantity() + ingredient.getUnit().toString() + ingredient.getName());
            }
            file.write("\nPreparation method:\n" + this.prepareMethod);
            file.write("Additional information:" );
            file.write("Cost: " + this.cost);
            file.write("Preparation time: " + this.prepareTime);
            file.write("Number of portions" + this.portionNumber);
            file.close();
        } catch (IOException err) {
            System.out.println("Error: ");
            err.printStackTrace();
        }

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

    public int getAccessibility() {
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

    public int getPrepareTime() {
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

    public void setAvgRate(){
        // @TODO with Opinion class
    }

}
