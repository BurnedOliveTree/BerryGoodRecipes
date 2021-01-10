package main;

import javafx.geometry.HPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;

import main.controller.MainPane;
import main.userModel.Opinion;
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


public class DatabaseConnection {
    static Connection connection;
    public static String theme;

    // TODO save shoppinglist, read shoppinglist, addrecipe, deleterecipe
    public DatabaseConnection() throws IOException {
        Properties prop = new Properties();
        String fileName = "src/resources/app.config";
        InputStream is = new FileInputStream(fileName);
        prop.load(is);
        theme = prop.getProperty("app.theme");
    }

    public static boolean isThemeLight() {
        return DatabaseConnection.theme.equals("light") || DatabaseConnection.theme.equals("winter");
    }

    private static void setConnection() throws SQLException, IOException {
        Properties prop = new Properties();
        String fileName = "src/resources/app.config";
        InputStream is = new FileInputStream(fileName);
        prop.load(is);
        String connectionURL = String.format(
                "jdbc:oracle:thin:%s/%s@//%s:%s/%s",
                prop.getProperty("app.login"), prop.getProperty("app.password"), prop.getProperty("app.host"), prop.getProperty("app.port"), prop.getProperty("app.service.name"));
        OracleDataSource ods = new OracleDataSource();
        ods.setURL(connectionURL);
        connection = ods.getConnection();
        System.out.println("Connection with database opened.");
        connection.setAutoCommit(false);
    }

    private static void closeConnection() throws SQLException {
        connection.close();
        System.out.println("Connection with database closed.");
    }

    public static User login(String username, String password, Label errMess) throws SQLException, IOException {
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
                activeUser = new User(username, userRecipes, favorites);
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
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(String.format("SELECT RECIPE_ID FROM FAVORITE WHERE UPPER(USERNAME) = '%s'", username.toUpperCase()));
        LinkedList<Recipe> favorites = new LinkedList<>();
        while (result.next()) {
            int id = result.getInt("RECIPE_ID");
            Recipe recipe = getRecipe(id);
            favorites.add(recipe);
        }
        result.close();
        statement.close();
        return favorites;
    }

    private static List<Recipe> getUserRecipes(String username) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(String.format("SELECT RECIPE_ID, NAME, DATE_ADDED FROM RECIPE WHERE UPPER(OWNER_NAME) = '%s'", username.toUpperCase()));
        List<Recipe> UserRecipes = new ArrayList<>();
        while (result.next()) {
            int id = result.getInt("RECIPE_ID");
            Recipe recipe = getRecipe(id);
            Statement stat = connection.createStatement();
            ResultSet resPublicity = stat.executeQuery(String.format("SELECT G.NAME FROM \"GROUP\" G WHERE G.GROUP_ID = (SELECT P.GROUP_ID FROM PUBLICITY P WHERE P.RECIPE_ID = %d)", id));
            resPublicity.next();
            String groupName = resPublicity.getString("NAME");
            resPublicity.close();
            stat.close();
            recipe.setGroupName(groupName);
            UserRecipes.add(recipe);
        }
        result.close();
        statement.close();
        return UserRecipes;
    }

    public static User register(String username, String password, Label errMess) throws SQLException, IOException {
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
                activeUser = new User(username, userRecipes, favorites);
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

    public static void saveUser(User user) throws SQLException, IOException {
        if (user != null) {
            setConnection();
            Statement statement = connection.createStatement();
            if (user.getNewFavorites().size() != 0) {
                for (Recipe recipe : user.getNewFavorites()) {
                    statement.executeUpdate("INSERT INTO FAVORITE SELECT null, '" + user.getUsername() + "', "+ recipe.getId() + " FROM DUAL\n" +
                            "WHERE NOT EXISTS (SELECT NULL FROM FAVORITE WHERE RECIPE_ID=" +  recipe.getId() + " AND USERNAME='" + user.getUsername() + "')");
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

    private static List<String> getGroupParticipants(String GroupID) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "select b.USERNAME as username from \"GROUP\" g join BELONG b on g.GROUP_ID = "+GroupID;
        ResultSet resultSet = statement.executeQuery(query);
        List<String> users = new ArrayList<>();
        while (resultSet.next()) {
            users.add(resultSet.getString("username"));
        }
        resultSet.close();
        statement.close();
        return users;
    }

    public static void getGroups(TilePane tilePane, User user) throws SQLException, IOException {
        setConnection();
        Statement statement = connection.createStatement();
        String query = "select g.GROUP_ID as ID, g.NAME as group_name, b.USERNAME as username from \"GROUP\" g join BELONG b on g.GROUP_ID = b.GROUP_ID where lower(b.USERNAME) = '"+(user.getUsername()).toLowerCase()+"'";
        ResultSet resultSet = statement.executeQuery(query);
        List<MenuButton> panelist = new ArrayList<>();
        while (resultSet.next()) {
            String tempString = resultSet.getString("group_name");
            MenuButton tempButton = new MenuButton(tempString);
            tempButton.setWrapText(true);
            tempButton.setTextAlignment(TextAlignment.CENTER);
            tempButton.setPrefSize(192, 64);
            tempButton.getItems().add(new MenuItem("Delete group"));
            tempButton.getItems().add(new SeparatorMenuItem());
            List<String> tempStringList = getGroupParticipants(resultSet.getString("ID"));
            for (String s : tempStringList) tempButton.getItems().add(new MenuItem("Kick "+s));
//            String tempString = resultSet.getString("RECIPE_ID");
//            tempButton.setOnMouseClicked(e -> mainPane.onRecipeClick(Integer.parseInt(tempString)));
            panelist.add(tempButton);
        }
        tilePane.getChildren().clear();
        tilePane.getChildren().addAll(panelist);
        resultSet.close();
        statement.close();
        closeConnection();
    }

    public static void getFollowed(ListView<String> listView, User user) throws SQLException, IOException {
        setConnection();
        Statement statement = connection.createStatement();
        String query = "select FOLLOWING_USERNAME, FOLLOWED_USERNAME from FOLLOWED where lower(FOLLOWING_USERNAME) = '"+(user.getUsername()).toLowerCase()+"'";
        ResultSet resultSet = statement.executeQuery(query);
        List<String> stringList = new ArrayList<>();
        while (resultSet.next()) {
            stringList.add(resultSet.getString("FOLLOWED_USERNAME"));
        }
        listView.getItems().clear();
        listView.getItems().addAll(stringList);
        resultSet.close();
        statement.close();
        closeConnection();
    }

    public static void fillResults(MainPane mainPane, TilePane tilePain) throws SQLException, IOException {
        fillResults(mainPane, tilePain, null);
    }

    public static void fillResults(MainPane mainPane, TilePane tilePain, String whereStatement) throws SQLException, IOException {
        setConnection();
        Statement statement = connection.createStatement();
        String query = "select distinct rcp.RECIPE_ID, rcp.NAME, rcp.PREPARATION_TIME, rcp.COST, CALC_RATING(rcp.RECIPE_ID) as RATING from RECIPE rcp join INGREDIENT_LIST ing on rcp.RECIPE_ID = ing.RECIPE_ID";
        if (whereStatement != null)
            query = query + " WHERE " + whereStatement;
        ResultSet resultSet = statement.executeQuery(query);
        List<GridPane> panelist = new ArrayList<>();
        while (resultSet.next()) {
            GridPane tempPane = new GridPane();
            for (int i = 0; i < 3; i++) {
                tempPane.getRowConstraints().add(new RowConstraints(32));
            }
            for (int i = 0; i < 6; i++) {
                ColumnConstraints columnConstraints = new ColumnConstraints(32);
                columnConstraints.setHalignment(HPos.CENTER);
                tempPane.getColumnConstraints().add(columnConstraints);
            }
            tempPane.setPrefSize(192, 96);
            Button tempButton = new Button(resultSet.getString("NAME"));
            tempButton.setWrapText(true);
            tempButton.setTextAlignment(TextAlignment.CENTER);
            tempButton.setPrefSize(192, 64);
            int tempInt = resultSet.getInt("RECIPE_ID");
            tempButton.setOnMouseClicked(e -> mainPane.onRecipeClick(tempInt));
            tempPane.add(tempButton, 0, 0, 6, 2);
            String tempString = resultSet.getString("RATING");
            if (tempString == null)
                tempString = "N/A";
            else
                tempString = tempString + "/10";
            tempPane.add(new ImageView(new Image("icons/star.png")), 0, 2, 1, 1);
            tempPane.add(new Label(tempString), 1, 2, 1, 1);
            tempString = resultSet.getString("PREPARATION_TIME");
            if (tempString == null)
                tempString = "N/A";
            tempPane.add(new ImageView(new Image("icons/time.png")), 2, 2, 1, 1);
            tempPane.add(new Label(tempString), 3, 2, 1, 1);
            tempString = resultSet.getString("COST");
            if (tempString == null)
                tempString = "N/A";
            tempPane.add(new ImageView(new Image("icons/coin.png")), 4, 2, 1, 1);
            tempPane.add(new Label(tempString), 5, 2, 1, 1);
            panelist.add(tempPane);
        }
        tilePain.getChildren().clear();
        tilePain.getChildren().addAll(panelist);
        resultSet.close();
        statement.close();
        closeConnection();
    }

    public static Recipe getSelectedRecipe(int recipeId) {
        try {
            setConnection();
            Recipe recipe = getRecipe(recipeId);
            closeConnection();
            return recipe;
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private static Recipe getRecipe(int recipeId) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(String.format("SELECT * FROM RECIPE JOIN PUBLICITY USING(RECIPE_ID) WHERE RECIPE_ID = %s", recipeId));
        result.next();
        String ownerName = result.getString("OWNER_NAME");
        String recipeName = result.getString("NAME");
        String preparationMethod = result.getString("PREPARATION_METHOD");
        double cost = result.getDouble("COST");
        String dateAdded = result.getString("DATE_ADDED");
        int prepareTime = result.getInt("PREPARATION_TIME");
        int portions = result.getInt("PORTIONS");
        int accessibility = result.getInt("GROUP_ID");

        result = statement.executeQuery(String.format("SELECT * FROM INGREDIENT_LIST WHERE RECIPE_ID = %s", recipeId));
        ArrayList<Ingredient> ingredientList = new ArrayList<>();
        while (result.next()) {
            Double amount = result.getDouble("AMOUNT");
            String unitName = result.getString("INGREDIENT_UNIT");
            String name = result.getString("INGREDIENT_NAME");
            int id = result.getInt("INGREDIENT_LIST_ID");
            Unit unit = new Unit(unitName);
            Ingredient ingredient = new Ingredient(id, amount, unit, name);
            ingredientList.add(ingredient);
        }
        result.close();
        return new Recipe(recipeId, recipeName, ownerName, preparationMethod, accessibility, dateAdded, prepareTime, cost, portions, ingredientList);
    }


    public static void createOpinion(Opinion opinion, Label opinionLabel, ListView opinionsView) throws SQLException, IOException {
        setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from OPINION where USERNAME = '"+opinion.getUsername()+ "' and RECIPE_ID = " + opinion.getRecipe().getId() );
        if (resultSet.next()) {
            opinionLabel.setText("You have already added your opinion!");
        }
        else {
            statement.execute("insert into OPINION values(null,'" + opinion.getUsername() + "','" + opinion.getRecipe().getId() + "', '" + opinion.getScore() + "', '" + opinion.getOpinionText() + "')");
            opinionLabel.setText("Opinion saved!");
            String username = opinion.getUsername();
            String score = String.valueOf(opinion.getScore());
            String comment = username + "    Score: " +score + "\n" + opinion.getOpinionText() + "\n";
            opinionsView.getItems().add(comment);
            statement.close();
            closeConnection();
        }
    }

    public static void fillOpinions(Recipe recipe, ListView opinionsView) throws SQLException, IOException {
        setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from OPINION where RECIPE_ID = " +recipe.getId());
        while (resultSet.next()){
            String username = resultSet.getString("USERNAME");
            String score = String.valueOf(resultSet.getInt("SCORE"));

            //if (comment.equals(null)){comment = " ";}
            String item = username + "    Score: " +score +"\n" +resultSet.getString("COMMENT") +"\n";
            opinionsView.getItems().add(item);
        }
        resultSet.close();
        statement.close();
        closeConnection();
    }


    public static void reportOpinion(ListView opinionList, String username, Label label,String opinionAuthor, int recipeId) throws SQLException, IOException {

        setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select OPINION_ID from OPINION where USERNAME = '" +opinionAuthor+ "' and recipe_id = " +recipeId);
        resultSet.next();
        int opinionId = resultSet.getInt("OPINION_ID");
        resultSet = statement.executeQuery("select * from REPORTED where REPORTING_USER = '"+username+ "' and OPINION_ID = " + opinionId );
        if (resultSet.next()) {
            label.setText("You have already reported!");
            label.setWrapText(true);
        }
        else {
            statement.execute("insert into REPORTED values (null,'" + username + "', '" + opinionId + "')");
            label.setText("Done!");
        }
        resultSet.close();
        statement.close();
        closeConnection();
    }


}