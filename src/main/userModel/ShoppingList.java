package main.userModel;
import main.recipeModel.Unit;
import main.recipeModel.Ingredient;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ShoppingList {
    public Map<Integer, Ingredient> shoppingList = new HashMap<>();
    public void addToShoppingList(Ingredient ingredient) {shoppingList.put(ingredient.getId(), ingredient);}
    public void removeFromShoppingList(int ingredientId) {shoppingList.remove(ingredientId);}
    public boolean checkIfInShoppingList(int ingredientId) {return shoppingList.get(ingredientId) != null;}
//
//    public void saveToFile()
//    {
//        try {
//            FileWriter file = new FileWriter("shoppinglist.txt");
//            for (Ingredient ingredient: this.shoppingList){
//                file.write(ingredient.getQuantity() + ingredient.getUnit().toString() + ingredient.getName());
//            }
//            file.close();
//
//        } catch (IOException err) {
//            System.out.println("Error: ");
//            err.printStackTrace();
//    }
//    }
}
