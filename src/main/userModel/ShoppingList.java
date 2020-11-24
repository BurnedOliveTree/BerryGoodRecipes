package main.userModel;
import main.recipeModel.Unit;
import main.recipeModel.Ingredient;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class ShoppingList {
    public LinkedList<Ingredient> shoppingList = new LinkedList<>();

    public void addToShoppingList(Integer quantity, Unit unit, String ingredientName)
    {
        for (Ingredient ingredient : this.shoppingList) {
            if (ingredientName.equals(ingredient.getName())) {
                quantity += ingredient.getQuantity();
                ingredient.setQuantity(quantity);
                return;
            }
        }
        shoppingList.add(new Ingredient(quantity, unit, ingredientName));

    }
    public void removeFromShoppingList(Integer quantity, String ingredientName) {
        for (Ingredient ingredient : this.shoppingList) {
            if (ingredientName.equals(ingredient.getName())) {
                int currQuantity = ingredient.getQuantity() - quantity;
                if (currQuantity > 0) {
                    ingredient.setQuantity(currQuantity);
                }
                if (currQuantity == 0) {
                    this.shoppingList.remove(ingredient);
                }
                return;
            }
        }
    }
    public void saveToFile()
    {
        try {
            FileWriter file = new FileWriter("shoppinglist.txt");
            for (Ingredient ingredient: this.shoppingList){
                file.write(ingredient.getQuantity() + ingredient.getUnit().toString() + ingredient.getName());
            }
            file.close();

        } catch (IOException err) {
            System.out.println("Error: ");
            err.printStackTrace();
    }


    }
}
