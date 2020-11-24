package main.recipeModel;

public class Ingredient {
    Integer quantity;
    Unit unit;
    String name;

    public Ingredient(Integer quantity, Unit unit, String name) {
        this.quantity = quantity;
        this.unit = unit;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setName(String name) {
        this.name = name;
    }
}
