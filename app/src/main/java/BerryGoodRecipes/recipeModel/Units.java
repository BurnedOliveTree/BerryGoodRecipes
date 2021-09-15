package BerryGoodRecipes.recipeModel;

import java.util.HashMap;
import java.util.Set;

public class Units {
    private final HashMap<String, Double> unitList;

    public Units (HashMap<String, Double> unitList) {
        this.unitList = unitList;
    }

    public Set<String> getUnits() {
        return unitList.keySet();
    }

    public Double convertUnit(String unitOne, String unitTwo, Double quantity) {
        return unitList.get(unitOne) / unitList.get(unitTwo) * quantity;
    }
}
