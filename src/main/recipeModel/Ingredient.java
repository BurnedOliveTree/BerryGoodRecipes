package main.recipeModel;

public class Ingredient {
    Double quantity;
    Unit unit;
    String name;
    int id;
    // class which is necessary for the correct implementation of the converter and the recipe
    public Ingredient(int id, Double quantity, Unit unit, String name) {
        this.id = id;
        this.quantity = quantity;
        this.unit = unit;
        this.name = name;
    }

    public int getId() {return id;}

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
}
