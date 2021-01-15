package main.recipeModel;

import main.controller.Status;

public class Ingredient {
    Double quantity;
    Unit unit;
    String name;
    int id;
    Status shoppingListStatus = Status.none;
    // class which is necessary for the correct implementation of the converter and the recipe
    public Ingredient(Integer id, Double quantity, Unit unit, String name) {
        this.id = id;
        this.quantity = quantity;
        this.unit = unit;
        this.name = name;
    }

    public int getId() {return id;}

    public void setShoppingListStatus(Status status) {shoppingListStatus = status;}

    public Status getShoppingListStatus() {return shoppingListStatus;}

    public String getName() {
        return name;
    }

    public Double getQuantity() {
        return quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null){
            return false;
        }
        else if (object.getClass() != this.getClass()){
            return false;
        }
        else {
            Ingredient ingredient = (Ingredient) object;
            return ingredient.getId() == this.id;
        }
    }
}
