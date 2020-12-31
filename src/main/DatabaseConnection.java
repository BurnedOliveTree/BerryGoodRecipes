package main;

import javafx.scene.text.TextAlignment;
import main.controller.MainPane;
import main.userModel.User;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;
import main.recipeModel.Unit;
import oracle.jdbc.pool.OracleDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;

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
        connection.setAutoCommit(false);
    }

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

        result = statement.executeQuery(String.format("SELECT GROUP_ID FROM PUBLICITY WHERE RECIPE_ID = %s", recipeId));
        result.next();
        int accessibility = result.getInt("GROUP_ID");

        result = statement.executeQuery(String.format("SELECT AMOUNT, INGREDIENT_UNIT, INGREDIENT_NAME FROM INGREDIENT_LIST WHERE RECIPE_ID = %s", recipeId));
        ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
        while (result.next()) {
            Double amount = result.getDouble("AMOUNT");
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

    public static User login(String username, String password, Label errMess) throws SQLException {
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
                List<Recipe> userRecipes = getUserRecipes(username);
                List<Recipe> favorites = getUserFavorites(username);
                activeUser = new User(username, password, userRecipes, favorites);
                // TODO add all the other columns in the future
                errMess.setText("Successfully logged in!");
            } else {
                errMess.setText("Incorrect password!");
            }
        }
        else {
            errMess.setText("Username does not exist!");
        }

        resultSet.close();
        statement.close();
        closeConnection();

        return activeUser;
    }

    private static List<Recipe> getUserFavorites(String username) throws SQLException {
        setConnection();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(String.format("SELECT RECIPE_ID FROM FAVORITE WHERE UPPER(USERNAME) = '%s'", username.toUpperCase()));
        LinkedList<Recipe> favorites = new LinkedList<>();
        while (result.next()) {
            int id = result.getInt("RECIPE_ID");
            Statement stat = connection.createStatement();
            ResultSet res = stat.executeQuery(String.format("SELECT NAME, OWNER_NAME, DATE_ADDED, COST, PREPARATION_TIME FROM RECIPE WHERE RECIPE_ID = %d", id));
            res.next();
            String name = res.getString("NAME");
            String dateAdded = res.getString("DATE_ADDED");
            String author = res.getString("OWNER_NAME");
            int cost = res.getInt("COST");
            int time = res.getInt("PREPARATION_TIME");
            res.close();
            stat.close();
            Recipe recipe = new Recipe(id, name, author, dateAdded, cost, time);
            favorites.add(recipe);
        }
        result.close();
        statement.close();
        closeConnection();
        return favorites;
    }

    public static List<Recipe> getUserRecipes(String username) throws SQLException {
        setConnection();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(String.format("SELECT RECIPE_ID, NAME, DATE_ADDED FROM RECIPE WHERE UPPER(OWNER_NAME) = '%s'", username.toUpperCase()));
        List<Recipe> UserRecipes = new ArrayList<Recipe>();
        while (result.next()) {
            int id = result.getInt("RECIPE_ID");
            String name = result.getString("NAME");
            String dateAdded = result.getString("DATE_ADDED");
            Statement stat = connection.createStatement();
            ResultSet resPublicity = stat.executeQuery(String.format("SELECT G.NAME FROM \"GROUP\" G WHERE G.GROUP_ID = (SELECT P.GROUP_ID FROM PUBLICITY P WHERE P.RECIPE_ID = %d)", id));
            resPublicity.next();
            String groupName = resPublicity.getString("NAME");
            resPublicity.close();
            stat.close();
            Recipe recipe = new Recipe(id, name, groupName, dateAdded);
            UserRecipes.add(recipe);
        }
        result.close();
        statement.close();
        closeConnection();
        return UserRecipes;
    }

    public static User register(String username, String password, Label errMess) throws SQLException {
        setConnection();

        User activeUser = null;
        Statement statement = connection.createStatement();

        // check if such a username exists in the database
        ResultSet resultSet = statement.executeQuery("select * from \"USER\" where USERNAME = '"+username+"'");
        if (resultSet.next()) {
            errMess.setText("Username already exists!");
        }
        else {
            if (!statement.execute("insert into \"USER\" values('"+username+"', '"+password+"', null)")) {
                List<Recipe> userRecipes = getUserRecipes(username);
                List<Recipe> favorites = getUserFavorites(username);
                activeUser = new User(username, password, userRecipes, favorites);
                errMess.setText("Successfully created an account!");
            }
            else {
                errMess.setText("Creating the account failed!");
            }
        }

        resultSet.close();
        statement.close();
        closeConnection();

        return activeUser;
    }

    public static void saveUser(User user) throws SQLException {
        if (user != null) {
            setConnection();
            Statement statement = connection.createStatement();
            if (user.getNewFavorites().size() != 0) {
                for (Recipe recipe : user.getNewFavorites()) {
                    statement.executeUpdate(String.format("INSERT INTO FAVORITE VALUES(null, '%s', %d)", user.getUsername(), recipe.getId()));
                }
            }
            if (user.getDeletedFavorites().size() != 0) {
                for (Recipe recipe: user.getDeletedFavorites()) {
                    statement.executeUpdate(String.format("DELETE FROM FAVORITE WHERE RECIPE_ID = %d AND USERNAME = '%s'", recipe.getId(),  user.getUsername()));
                }
            }
            statement.close();
            connection.commit();
            closeConnection();
        }
    }

    public static void fillResults(MainPane mainPane, TilePane tilePain) throws SQLException {
        setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select RECIPE_ID, NAME from RECIPE");
        List<GridPane> panelist = new ArrayList<>();
        while (resultSet.next()) {
            GridPane tempPane = new GridPane();
            for (int i=0; i<3; i++) {
                ColumnConstraints columnConstraints = new ColumnConstraints(64);
                columnConstraints.setHalignment(HPos.CENTER);
                tempPane.getColumnConstraints().add(columnConstraints);
                tempPane.getRowConstraints().add(new RowConstraints(32));
            }
            tempPane.setPrefSize(192, 96);
            Button tempButton = new Button(resultSet.getString("NAME"));
            tempButton.setWrapText(true);
            tempButton.setTextAlignment(TextAlignment.CENTER);
            tempButton.setPrefSize(192, 64);
            String tempString = resultSet.getString("RECIPE_ID");
            tempButton.setOnMouseClicked(e -> mainPane.onRecipeClick(tempButton, Integer.parseInt(tempString)));
            tempPane.add(tempButton, 0, 0, 3, 2);
            tempPane.add(new Label("Rating"), 0, 2, 1, 1);
            tempPane.add(new Label("Time"), 1, 2, 1, 1);
            tempPane.add(new Label("Cost"), 2, 2, 1, 1);
            panelist.add(tempPane);
        }
        tilePain.getChildren().clear();
        tilePain.getChildren().addAll(panelist);
        resultSet.close();
        statement.close();
        closeConnection();
    }
}