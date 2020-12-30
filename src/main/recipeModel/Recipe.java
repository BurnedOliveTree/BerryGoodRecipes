package main.recipeModel;
import main.userModel.User;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Recipe {
    private String author;
    private int id;
    private double cost = 0;
    private String name;
    private int accessibility;
    private double portionNumber;
    private String dateAdded;
    private String prepareMethod;
    private int prepareTime; // in minutes
    // @TODO private LinkList<Opinion> opinions = new LinkList<Opinion>();
    private ArrayList<Ingredient> ingredientList;
    private double avgRate;

    public Recipe() {};

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

//    public void addOpinion(String opinionText, int rate, User user)
//    {
//        Opinion opinion = new Opinion(opinionText, rate);
//        opinions.add(Opinion);
//    }

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

    public void scaleIngredientList(double scale) {
        for (Ingredient ingredient : this.ingredientList) {
            if (scale > 0) {
                ingredient.setQuantity(ingredient.getQuantity()*scale);
            }
            else
                throw new IllegalArgumentException("Value must be greater than 0");
        }
    }


    public void saveToFile(String filename) {
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

    public void setAvgRate(){
        // @TODO with Opinion class
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

    public int isAccessibility() {
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

    public double getAvgRate() {
        return avgRate;
    }

    public String getDateAdded() {return dateAdded;}

    public int getId() {return id;}
}
