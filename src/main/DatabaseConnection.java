package main;

import main.userModel.User;
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
    static Connection connection;
    public static String theme;
    public static String databaseLogin;
    public static String databasePassword;
    public static String databasePort;
    public static String databaseServiceName;
    public static String databaseHost;

    public DatabaseConnection() throws IOException {
        loadFile();
    }

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

    public static void setConnection() throws SQLException {

        String connectionURL = String.format(
                "jdbc:oracle:thin:%s/%s@//%s:%s/%s",
                databaseLogin, databasePassword, databaseHost, databasePort, databaseServiceName);
        OracleDataSource ods = new OracleDataSource();
        ods.setURL(connectionURL);
        connection = ods.getConnection();
    };

    public static void closeConnection() throws SQLException {
        connection.close();
        System.out.println("Connection with database closed.");
    }

    public static Recipe getRecipe(int recipeId) throws SQLException {
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

    public static User login(String username, String password) throws SQLException {
        setConnection();

        User activeUser = null;
        Statement statement = connection.createStatement();

        // check if such a username exists in the database
        ResultSet resultSet = statement.executeQuery("select * from \"USER\" where USERNAME = '"+username+"'");
        if (resultSet.next()) {
            // check if password is correct
            String gotPassword = resultSet.getString("PASSWORD");
            if (gotPassword.equals(password)) {
                // everything is correct, create a user
                activeUser = new User(username, password);
                // TODO add all the other columns in the future
                System.out.println("Successfully logged in!");
            } else {
                System.out.println("Incorrect password!");
            }
        }
        else {
            System.out.println("User with such a username does not exist in the database!");
        }

        resultSet.close();
        statement.close();
        closeConnection();

        return activeUser;
    }

    public static User register(String username, String password) throws SQLException {
        setConnection();

        User activeUser = null;
        Statement statement = connection.createStatement();

        // check if such a username exists in the database
        ResultSet resultSet = statement.executeQuery("select * from \"USER\" where USERNAME = '"+username+"'");
        if (resultSet.next()) {
            System.out.println("User with such a username already exists in the database!");
        }
        else {
            if (!statement.execute("insert into \"USER\" values('"+username+"', '"+password+"', null)")) {
                activeUser = new User(username, password);
                System.out.println("Successfully created an account!");
            }
            else {
                System.out.println("Creating the account failed, somehow");
            }
        }

        resultSet.close();
        statement.close();
        closeConnection();

        return activeUser;
    }
}