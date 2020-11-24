package main.recipeModel;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Recipe {
    // @TODO private User
    private int cost;
    private String name;
    private boolean accessibility; // enum in future?
    private int portionNumber;
    private String prepareMethod;
    private int prepareTime; // in minutes
    // @TODO private LinkList<Opinion> opinions = new LinkList<Opinion>();
    // @TODO private ArrayList<Triplet<String, int, Unit>> ingredientList = new ArrayList<Triplet<String, int, Unit>>()
    private double avgRate;


    public Recipe(String name, String prepareMethod, boolean accessibility) {
        this.name = name;
        this.accessibility = accessibility;
        this.prepareMethod = prepareMethod;
        // @TODO this.ingredientList = ingredientList;
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

//    public void editIngredientList(int quantity, String ingredient)
//    {
//        Iterator<Triplet<String, int, Unit>> = this.ingredientList.iterator();
//        while(iter.hasNext())
//        {
//
//        }
//    }

    public void saveToFile(String filename) {
        try {
            FileWriter file = new FileWriter(filename);
            file.write(this.name + "\nIngredients:\n");
            // @TODO ingredientList
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


}
