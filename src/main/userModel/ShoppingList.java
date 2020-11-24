package main.userModel;
import main.recipeModel.Unit;
import main.recipeModel.Ingredient;

import java.util.LinkedList;

public class ShoppingList {
    public LinkedList<Ingredient> shoppingList = new LinkedList<Ingredient>();

    public void addToShoppingList(Integer quantity, Unit unit, String ingredientName)
    {
        boolean found = false;
        for (Ingredient ingredient : this.shoppingList) {
            if (ingredientName.equals(ingredient.getName())) {
                quantity += ingredient.getQuantity();
                ingredient.setQuantity(quantity);
                found = true;
                break;
            }
        }
        if (!found){
            shoppingList.add(new Ingredient(quantity, unit, ingredientName));
        }
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
                break;
            }
        }
    }
}
