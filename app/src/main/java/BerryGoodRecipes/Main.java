// this file exists to fix the fat jar issue in JavaFX 11

package BerryGoodRecipes;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        App.main(args);
    }
}