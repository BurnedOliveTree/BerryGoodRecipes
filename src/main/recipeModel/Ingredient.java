package main.recipeModel;

import main.controller.Status;

public class Ingredient {
    // class which is necessary for the correct implementation of the converter and the recipe
    Double quantity;
    String unit;
    String name;
    Integer id; // id of record in ingredient_list in database
    Status shoppingListStatus = Status.none; // status of ingredient in shopping list - solution to save on database connections, all information is modified at the end of the program
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

    public Integer getId() {return id;}

    public void setShoppingListStatus(Status status) {shoppingListStatus = status;}

    public Status getShoppingListStatus() {return shoppingListStatus;}

    public String getName() {
        return name;
    }

    public Double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
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
