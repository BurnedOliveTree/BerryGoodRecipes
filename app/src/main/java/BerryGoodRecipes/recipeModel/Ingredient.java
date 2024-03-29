package BerryGoodRecipes.recipeModel;

import BerryGoodRecipes.controller.Status;

public class Ingredient {
    // class which is necessary for the correct implementation of the converter and the recipe
    Double quantity;
    String unit;
    String name;
    Integer id; // id of record in ingredient_list in database
    Status shoppingListStatus = Status.none; // status of ingredient in shopping list - solution to save on database connections, all information is modified at the end of the program

    // constructors

    public Ingredient(Integer id, Double quantity, String unit, String name) {
        this.id = id;
        this.quantity = quantity;
        this.unit = unit;
        this.name = name;
    }

    public Ingredient(Integer id, Double quantity, String unit, String name, Status shoppingListStatus){
        this.id = id;
        this.quantity = quantity;
        this.unit = unit;
        this.name = name;
        this.shoppingListStatus = shoppingListStatus;
    }

    public Ingredient(Ingredient that) {
        this(that.getId(), that.getQuantity(), that.getUnit(), that.getName(), that.getShoppingListStatus());
    }

    // getter

    public Integer getId() {
        return id;
    }

    public Status getShoppingListStatus() {
        return shoppingListStatus;
    }

    public String getName() {
        return name;
    }

    public Double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    // setter

    public void setShoppingListStatus(Status status) {
        shoppingListStatus = status;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int ingredientId) { id = ingredientId; }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        else if (object.getClass() != this.getClass()) {
            return false;
        }
        else {
            Ingredient ingredient = (Ingredient) object;
            if (ingredient.getId() != null)
                return ingredient.getId().equals(this.id);
            else {
                return ingredient.getQuantity().equals(this.quantity) && ingredient.getName().equals(this.name);
            }
        }
    }

    @Override
    public String toString() {
        return String.format((this.getQuantity() % 1 == 0)?"%1.0f %s %s":"%1.2f %s %s", this.getQuantity(), this.getUnit(), this.getName());
    }

}
