package main;

import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import main.recipeModel.Unit;
import oracle.jdbc.pool.OracleDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class DatabaseConnection {
    Connection connection;
    public static String theme;
    public static String databaseLogin;
    public static String databasePassword;
    public static String databasePort;
    public static String databaseServiceName;
    public static String databaseHost;

    public DatabaseConnection() throws IOException {
        loadFile();
    }

//    public void connect() {
//        Connection conn = null;
//        try {
//            conn = DriverManager.getConnection("jdbc:oracle:thin:@//ora4.ii.pw.edu.pl:1521/pdb1.ii.pw.edu.pl", Core.databaseLogin, Core.databasePassword);
//
//            String query = "select * from INGREDIENT";
//            try (Statement stmt = conn.createStatement()) {
//                ResultSet rs = stmt.executeQuery(query);
//                while (rs.next()) {
//                    String name = rs.getString("NAME");
//                    System.out.println(name);
//                }
//            } catch (SQLException e) {
//                throw new Error("Problem", e);
//            }
//
//        } catch (SQLException e) {
//            throw new Error("Problem", e);
//        } finally {
//            try {
//                if (conn != null) {
//                    conn.close();
//                }
//            } catch (SQLException ex) {
//                System.out.println(ex.getMessage());
//            }
//        }
//    }
//
//    public void test() {
//        connect();
//    }

    private void loadFile() throws IOException {
        Properties prop = new Properties();
        String fileName = "src/resources/app.config";
        InputStream is = new FileInputStream(fileName);
        prop.load(is);
        theme = prop.getProperty("app.theme");
        databaseLogin = prop.getProperty("app.login");
        databasePassword = prop.getProperty("app.password");
        databasePort = prop.getProperty("app.port");
        databaseServiceName = prop.getProperty("app.service.name");
        databaseHost = prop.getProperty("app.host");
    }

    public void setConnection() throws SQLException {

        String connectionURL = String.format(
                "jdbc:oracle:thin:%s/%s@//%s:%s/%s",
                databaseLogin, databasePassword, databaseHost, databasePort, databaseServiceName);
        OracleDataSource ods = new OracleDataSource();
        ods.setURL(connectionURL);
        connection = ods.getConnection();
    };

    public void closeConnection() throws SQLException {
        connection.close();
        System.out.println("Connection with database closed.");
    }

    public Recipe getRecipe(int recipeId) throws SQLException {
        setConnection();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(String.format("SELECT * FROM RECIPE WHERE RECIPE_ID = %s", recipeId));
        result.next();
        String ownerName = result.getString("OWNER_NAME");
        System.out.println(ownerName);
        String recipeName = result.getString("NAME");
        String preparationMethod = result.getString("PREPARATION_METHOD");
        double cost = result.getDouble("COST");
        String dateAdded = result.getString("DATE_ADDED");
        int prepareTime = result.getInt("PREPARATION_TIME");
        int portions = result.getInt("PORTIONS");

        result.close();

        result = statement.executeQuery(String.format("SELECT GROUP_ID FROM PUBLICITY WHERE RECIPE_ID = %s", recipeId));
        result.next();
        int accessibility = result.getInt("GROUP_ID");
        result.close();

        result = statement.executeQuery(String.format("SELECT AMOUNT, INGREDIENT_UNIT, INGREDIENT_NAME FROM INGREDIENT_LIST WHERE RECIPE_ID = %s", recipeId));
        ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
        while (result.next()) {
            int amount = result.getInt("AMOUNT");
            String unitName = result.getString("INGREDIENT_UNIT");
            String name = result.getString("INGREDIENT_NAME");
            Unit unit = new Unit(unitName);
            Ingredient ingredient = new Ingredient(amount, unit, name);
            ingredientList.add(ingredient);
        }
        result.close();

        Recipe newRecipe = new Recipe(recipeId, recipeName, ownerName, preparationMethod, accessibility, dateAdded, prepareTime, cost, portions, ingredientList);

        statement.close();
        closeConnection();

        return newRecipe;
    }
}