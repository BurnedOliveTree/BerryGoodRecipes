package main;

import main.controller.Status;
import main.userModel.Group;
import main.userModel.Opinion;
import main.userModel.User;
import main.recipeModel.Ingredient;
import main.recipeModel.Recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import oracle.jdbc.pool.OracleDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;


public class DatabaseConnection {
    static Connection connection;
    public static String theme;

    public DatabaseConnection() throws IOException {
        // constructor, user's property is read in it - like theme
        Properties prop = new Properties();
        String fileName = "src/resources/app.config";
        InputStream is = new FileInputStream(fileName);
        prop.load(is);
        theme = prop.getProperty("app.theme");
    }

    public static boolean isThemeLight() {
        // check what theme is selected
        return DatabaseConnection.theme.equals("light") || DatabaseConnection.theme.equals("winter");
    }

    public static void setConnection() throws SQLException, IOException {
        // set connection of database, read property for it
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
        // close connection and set it to null
        connection.close();
        connection = null;
        System.out.println("Connection with database closed.");
    }

    // USER

    public static User login(String username, String password, Label errMess) throws SQLException, IOException {
        // sign into users account and set him as the active user
        if (connection == null)
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
                List<String> followed = getUserFollowed(username);
                List<Group> groups = getGroups(username);
                ArrayList<Ingredient> shoppingList = getShoppingList(username);
                activeUser = new User(username, userRecipes, favorites, followed, shoppingList, groups, DatabaseConnection.getUnits());
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

        return activeUser;
    }

    public static User register(String username, String password, Label errMess) throws SQLException, IOException {
        // register a new account
        if (connection == null)
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
                activeUser = new User(username);
                errMess.setText("Successfully created an account!");
            }
            else {
                errMess.setText("Creating the account failed!");
            }
        }

        resultSet.close();
        statement.close();

        return activeUser;
    }

    public static void deleteUser(String username) throws IOException, SQLException {
        // delete account and all directly linked data
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        statement.execute("begin delete_account('" +username+ "'); end;");
        connection.commit();
        statement.close();
    }

    public static String setPassword(String username, String newPassword, String oldPassword) throws SQLException, IOException {
        // change password of a given user
        String status;
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from \"USER\" where USERNAME = '"+username+"'");
        if (resultSet.next()) {
            String gotPassword = resultSet.getString("PASSWORD");
            if (gotPassword.equals(oldPassword)) {
                statement.executeUpdate("update \"USER\" set PASSWORD = '"+newPassword+"' where USERNAME = '"+username+"'");
                status = "Successfully changed password!";
            }
            else
                status = "Incorrect password!";
        }
        else
            status = "Username does not exist!";
        resultSet.close();
        statement.close();
        return status;
    }

    private static List<Recipe> getUserRecipes(String username) throws SQLException, IOException {
        // get user recipe and accessibility
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(String.format("SELECT RECIPE_ID, NAME, DATE_ADDED FROM RECIPE WHERE UPPER(OWNER_NAME) = '%s'", username.toUpperCase()));
        List<Recipe> userRecipes = new ArrayList<>();
        while (result.next()) {
            int id = result.getInt("RECIPE_ID");
            Recipe recipe = getRecipe(id);
            recipe.setId(id);
            getRecipeAccessibility(recipe);
            userRecipes.add(recipe);
        }
        result.close();
        statement.close();
        return userRecipes;
    }

    private static void getRecipeAccessibility(Recipe recipe) throws IOException, SQLException {
        // get from publicity table information about recipe accessibility
        if (connection == null)
            setConnection();
        Statement stat = connection.createStatement();
        ResultSet resPublicity = stat.executeQuery(String.format("SELECT G.NAME FROM \"GROUP\" G WHERE G.GROUP_ID = (SELECT P.GROUP_ID FROM PUBLICITY P WHERE P.RECIPE_ID = %d)", recipe.getId()));
        resPublicity.next();
        String groupName = resPublicity.getString("NAME");
        recipe.setGroupName(groupName);
        resPublicity.close();
        stat.close();
    }

    private static List<String> getUserFollowed(String username) throws SQLException, IOException {
        // get all users which are followed by a given user
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        String query = "select FOLLOWING_USERNAME, FOLLOWED_USERNAME from FOLLOWED where lower(FOLLOWING_USERNAME) = '"+username.toLowerCase()+"'";
        ResultSet resultSet = statement.executeQuery(query);
        List<String> stringList = new LinkedList<>();
        while (resultSet.next()) {
            stringList.add(resultSet.getString("FOLLOWED_USERNAME"));
        }
        resultSet.close();
        statement.close();
        return stringList;
    }

    private static void saveFollowed(User user) throws IOException, SQLException {
        // saved followed changes to database
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        if (user.getNewFollowed().size() != 0) {
            for (String username: user.getNewFollowed()) {
                statement.executeUpdate(String.format("insert into FOLLOWED values (null, '%s', '%s')", user.getUsername(), username));
            }
        }
        if (user.getDeletedFollowed().size() != 0) {
            for (String username: user.getDeletedFollowed()) {
                statement.executeUpdate(String.format("delete from FOLLOWED where FOLLOWING_USERNAME = '%s' and FOLLOWED_USERNAME = '%s'", user.getUsername(), username));
            }
        }
        statement.close();
        connection.commit();
    }

    private static List<Recipe> getUserFavorites(String username) throws SQLException {
        // get user's favorites recipes from database
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(String.format("SELECT RECIPE_ID FROM FAVORITE WHERE UPPER(USERNAME) = '%s'", username.toUpperCase()));
        LinkedList<Recipe> favorites = new LinkedList<>();
        while (result.next()) {
            int id = result.getInt("RECIPE_ID");
            Recipe recipe = getRecipe(id);
            recipe.setFavoriteStatus(Status.loaded);
            favorites.add(recipe);
        }
        result.close();
        statement.close();
        return favorites;
    }

    private static void saveFavorites(User user) throws IOException, SQLException {
        // saved changes in user favorites to database
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        for (Recipe recipe: user.getAllFavorites()) {
            if (recipe.getFavoriteStatus() == Status.added) {
                statement.executeUpdate("INSERT INTO FAVORITE SELECT null, '" + user.getUsername() + "', "+ recipe.getId() + " FROM DUAL\n" +
                        "WHERE NOT EXISTS (SELECT NULL FROM FAVORITE WHERE RECIPE_ID=" +  recipe.getId() + " AND USERNAME='" + user.getUsername() + "')");

            }
            if (recipe.getFavoriteStatus() == Status.deleted){
                statement.executeUpdate(String.format("DELETE FROM FAVORITE WHERE RECIPE_ID = %d AND USERNAME = '%s'", recipe.getId(),  user.getUsername()));
            }
        }
        statement.close();
        connection.commit();
    }

    public static void saveUser(User user) throws SQLException, IOException {
        // called when the application is closed, save information created in the session to the database
        if (user != null) {
            saveFavorites(user);
            saveFollowed(user);
            updateShoppingListView(user);
        }
        if (connection != null)
            closeConnection();
    }

    // GROUP

    public static void addGroup(String name, String username) throws IOException, SQLException {
        // add group of the given name and add user to it
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        statement.execute("begin add_group('"+username+ "', '" +name+"'); end;");
        connection.commit();
        statement.close();
    }

    public static void deleteGroup(int groupID) throws IOException, SQLException {
        // delete group of a given ID
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        statement.execute("begin delete_group("+groupID+"); end;");
        connection.commit();
        statement.close();
    }

    public static List<Group> getGroups(String username) throws SQLException, IOException {
        // get all groups in which the active user belongs
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        String query = "select g.GROUP_ID as ID, g.NAME as group_name from \"GROUP\" g join BELONG b on g.GROUP_ID = b.GROUP_ID where b.GROUP_ID != 0 and lower(b.USERNAME) = '"+username.toLowerCase()+"'";
        ResultSet resultSet = statement.executeQuery(query);
        List<Group> groupList = new ArrayList<>();
        while (resultSet.next()) {
            Group group = new Group(resultSet.getInt("ID"), resultSet.getString("group_name"));
            group.setParticipants(getGroupParticipants(resultSet.getString("ID")), username);
            groupList.add(group);
        }
        resultSet.close();
        statement.close();
        return groupList;
    }

    public static List<Integer> getGroupIDs(String[] groupName) throws IOException, SQLException {
        // retrieve a list of IDs of groups of given names
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        String query = "select GROUP_ID from \"GROUP\" where lower(NAME) in ('" + groupName[0].toLowerCase() + "'";
        for (String s : groupName)
            query = query + ", '" + s.toLowerCase() + "'";
        query = query + ")";
        ResultSet resultSet = statement.executeQuery(query);
        List<Integer> groupID = new ArrayList<>();
        if (resultSet.next())
            groupID.add(resultSet.getInt("GROUP_ID"));
        resultSet.close();
        statement.close();
        return groupID;
    }

    private static List<String> getGroupParticipants(String GroupID) throws SQLException, IOException {
        // get all users which belong to a given group
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        String query = "select USERNAME from BELONG where GROUP_ID = "+GroupID;
        ResultSet resultSet = statement.executeQuery(query);
        List<String> users = new ArrayList<>();
        while (resultSet.next()) {
            users.add(resultSet.getString("username"));
        }
        resultSet.close();
        statement.close();
        return users;
    }

    public static void invite(String username, int groupID) throws IOException, SQLException {
        // add given user to a given group
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        statement.execute("insert into BELONG values (null, "+groupID+", '"+username+"')");
        statement.close();
        connection.commit();
    }

    public static void kickUser(String username, int groupID) throws IOException, SQLException {
        // kick a user from a specified group
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate("delete from BELONG where GROUP_ID = "+groupID+ " and USERNAME = '" +username+ "'");
        statement.close();
        connection.commit();
    }

    // RECIPE

    public static int addRecipe(Recipe recipe, User activeUser) throws IOException, SQLException {
        // save new recipe in database
        if (connection == null)
            setConnection();
        CallableStatement statement = connection.prepareCall("{ call add_recipe(?, ?, ?, ?, ?, ?, ?, ?, ?) }");
        statement.setString(1, activeUser.getUsername());
        statement.setString(2, recipe.getName());
        statement.setString(3, recipe.getPrepareMethod());
        if (recipe.getCost() == 0.0)
            statement.setNull(4 ,  Types.NULL);
        else
            statement.setDouble(4, recipe.getCost());
        statement.setDouble(5, recipe.getPortionNumber());
        statement.setString(6, recipe.getDateAdded());
        if (recipe.getPrepareTime() == null)
            statement.setNull(7, Types.NULL);
        else
            statement.setInt(7, recipe.getPrepareTime());
        if (recipe.getAccessibility() == null)
            statement.setNull(8, Types.NULL);
        else
            statement.setInt(8, recipe.getAccessibility());
        statement.registerOutParameter(9, Types.NUMERIC);
        statement.execute();
        int recipe_id = statement.getInt(9);
        connection.commit();
        statement.close();
        Statement ingredientStatement = connection.createStatement();
        // TODO if private then don't add to publicity
        for (Ingredient ingredient: recipe.getIngredientList()){
            ingredientStatement.execute("BEGIN add_ingredient_to_recipe('" + ingredient.getName() +  "', '" + ingredient.getUnit() +"', "+ ingredient.getQuantity() +  ", " +  recipe_id + "); END;");
        }
        connection.commit();
        ingredientStatement.close();
        return recipe_id;
    }

    public static void deleteRecipe(User activeUser, Recipe recipe) throws IOException, SQLException {
        // delete user recipe from database
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        statement.execute("DELETE RECIPE WHERE OWNER_NAME='" + activeUser.getUsername() + "' AND NAME='" + recipe.getName() +"'");
        connection.commit();
        statement.close();
    }

    public static List<Recipe> search(User activeUser) throws SQLException, IOException {
        return search(activeUser, null, null);
    }

    public static List<Recipe> search(User activeUser, String whereStatement, List<Integer> groupID) throws SQLException, IOException {
        // the search engine of our app, queries all available recipes with given conditions
        if (activeUser == null && groupID != null)
            return null;
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        StringBuilder insideQuery;
        if (activeUser != null) {
            insideQuery = new StringBuilder("select distinct pub.RECIPE_ID from PUBLICITY pub join BELONG blg on blg.GROUP_ID = pub.GROUP_ID where lower(blg.USERNAME) = '" + activeUser.getUsername().toLowerCase() + "'");
            if (groupID != null) {
                insideQuery.append(" and pub.GROUP_ID in (").append(groupID.get(0));
                for (int i : groupID)
                    insideQuery.append(", ").append(i);
                insideQuery.append(")");
            }
        }
        else
            insideQuery = new StringBuilder("select distinct RECIPE_ID from PUBLICITY where GROUP_ID = 0");
        String query = "select distinct rcp.RECIPE_ID, rcp.NAME, rcp.OWNER_NAME, rcp.PREPARATION_TIME, rcp.COST, CALC_RATING(rcp.RECIPE_ID) as RATING from RECIPE rcp where rcp.RECIPE_ID in ("+insideQuery+")";
        if (whereStatement != null) {
            query = query + " AND " + whereStatement;
        }
        ResultSet resultSet = statement.executeQuery(query);
        List<Recipe> resultList = new ArrayList<>();
        while (resultSet.next()) {
            Recipe tempRecipe = new Recipe(resultSet.getInt("RECIPE_ID"), resultSet.getString("NAME"), resultSet.getString("OWNER_NAME"));
            tempRecipe.setAvgRate(resultSet.getString("RATING"));
            tempRecipe.setCost(resultSet.getDouble("COST"));
            tempRecipe.setPrepareTime(resultSet.getInt("PREPARATION_TIME"));
            resultList.add(tempRecipe);
        }
        resultSet.close();
        statement.close();
        return resultList;
    }

    public static Recipe getSelectedRecipe(int recipeId) {
        // get data of a selected recipe
        try {
            if (connection == null)
                setConnection();
            return getRecipe(recipeId);
        } catch (SQLException | IOException err) {
            err.printStackTrace();
        }
        return null;
    }

    private static Recipe getRecipe(int recipeId) throws SQLException {
        // get all information about recipe from database, and set them to recipe class
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(String.format("SELECT * FROM RECIPE JOIN PUBLICITY USING(RECIPE_ID) WHERE RECIPE_ID = %s", recipeId));
        result.next();
        String ownerName = result.getString("OWNER_NAME");
        String recipeName = result.getString("NAME");
        String preparationMethod = result.getString("PREPARATION_METHOD");
        double cost = result.getDouble("COST");
        String dateAdded = result.getString("DATE_ADDED");
        int prepareTime = result.getInt("PREPARATION_TIME");
        double portions = result.getDouble("PORTIONS");
        int accessibility = result.getInt("GROUP_ID");

        result = statement.executeQuery(String.format("SELECT * FROM INGREDIENT_LIST WHERE RECIPE_ID = %s", recipeId));
        ArrayList<Ingredient> ingredientList = new ArrayList<>();
        while (result.next()) {
            Double amount = result.getDouble("AMOUNT");
            String unitName = result.getString("INGREDIENT_UNIT");
            String name = result.getString("INGREDIENT_NAME");
            int id = result.getInt("INGREDIENT_LIST_ID");
            Ingredient ingredient = new Ingredient(id, amount, unitName, name);
            ingredientList.add(ingredient);
        }
        result.close();
        return new Recipe(recipeId, recipeName, ownerName, preparationMethod, accessibility, dateAdded, prepareTime, cost, portions, ingredientList);
    }

    // UNIT

    public static ObservableList<String> getUnits() throws IOException, SQLException {
        // returns ObservableList of available units names. Does not return "piece" because it is not used in most functions.
        ObservableList<String> unitsList = FXCollections.observableArrayList();
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select NAME from UNIT where name != 'piece'" );
        while (resultSet.next()){
            unitsList.add(resultSet.getString("NAME"));
        }
        resultSet.close();
        statement.close();
        return unitsList;
    }

    public static String getBestUnit(String preferredSystem, String currentUnit, Double quantity) throws IOException, SQLException {
        // returns unit from given preferredSystem that is that has te closest to 1 ratio to given currentUnit multiplied by given quantity
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        String unit;
        String bestUnit = "";
        double error = Double.POSITIVE_INFINITY;
        ResultSet resultSet = statement.executeQuery("select name from unit where unit_system_id = '" + preferredSystem+ "'");
        while (resultSet.next()){
            unit = resultSet.getString("name");
            if(Math.abs(1-convertUnit(quantity, currentUnit, unit)) < error) {
                error = Math.abs(1 - convertUnit((double) 1, currentUnit, unit));
                bestUnit = unit;
            }
        }
        resultSet.close();
        statement.close();
        return bestUnit;
    }

    public static List<String> getUnitSystems() throws SQLException, IOException {
        //  returns list that contains all unit systems from the database

        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        String query = "select NAME from UNIT_SYSTEM where not NAME like 'N%A'";
        ResultSet resultSet = statement.executeQuery(query);
        List<String> stringList = new ArrayList<>();
        while (resultSet.next()) {
            stringList.add(resultSet.getString("name"));
        }
        resultSet.close();
        statement.close();

        return stringList;
    }

    public static Double convertUnit(Double quantity, String firstUnit, String secondUnit) throws IOException, SQLException {
        // returns quantity of ingredient in given unit (secondUnit)
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select convert_unit('" +firstUnit+ "', '" +secondUnit+ "', " +quantity+ ") as result from dual");
        resultSet.next();
        Double result =  resultSet.getDouble("result");
        resultSet.close();
        statement.close();
        return result;
    }

    private static Double changeToDefaultUnit(Ingredient ingredient) throws IOException, SQLException {
        // returns quantity of given ingredient in unit saved for ingredient with this ingredient_list_id in database
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select INGREDIENT_UNIT from INGREDIENT_LIST where INGREDIENT_LIST_ID = " +ingredient.getId());
        resultSet.next();
        String defaultUnit = resultSet.getString("INGREDIENT_UNIT");
        Double result = convertUnit(ingredient.getQuantity(), ingredient.getUnit(), defaultUnit);
        resultSet.close();
        statement.close();
        return result;
    }

    // OPINION

    public static void createOpinion(Opinion opinion, Label opinionLabel, ListView<String> opinionsView) throws SQLException, IOException {
        // Saves opinion. One user can add only one opinion per recipe. Sets label from OpinionPane with message with information about the success of the operation.
        if (connection == null)
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

        }
        resultSet.close();
        statement.close();
    }

    public static void fillOpinions(Recipe recipe, ListView<String> opinionsView) throws SQLException, IOException {
        // loads recipes opinions into ListView from OpinionPane
        if (connection == null)
            setConnection();
        opinionsView.getItems().clear();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from OPINION where RECIPE_ID = " +recipe.getId());
        while (resultSet.next()){
            String username = resultSet.getString("USERNAME");
            String score = String.valueOf(resultSet.getInt("SCORE"));
            if (resultSet.getString("COMMENT") == null){
                opinionsView.getItems().add(username + "    Score: " +score +"\n\n");
            }
            else{
                opinionsView.getItems().add(username + "    Score: " + score + "\n" + resultSet.getString("COMMENT") + "\n");
            }

        }
        resultSet.close();
        statement.close();
    }

    public static void deleteOpinion(Recipe recipe, String userName, ListView<String> opinionsView) throws IOException, SQLException {
        // deletes opinion selected by user (user can only select his/hers own opinion).
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("delete from OPINION where USERNAME = '" +userName+ "' and RECIPE_ID = " +recipe.getId());
        connection.commit();
        fillOpinions(recipe, opinionsView);
        resultSet.close();
        statement.close();
    }

    public static void reportOpinion(String username, Label label, String opinionAuthor, int recipeId) throws SQLException, IOException {
        // Reports opinion. User can report opinion once. User cant report opinions that don't have comment in them.
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from OPINION where USERNAME = '" +opinionAuthor+ "' and recipe_id = " +recipeId);
        resultSet.next();
        if (resultSet.getString("COMMENT") == null){
            label.setText("You can't report score only opinions");
            label.setWrapText(true);
            resultSet.close();
            statement.close();
            return;
        }
        int opinionId = resultSet.getInt("OPINION_ID");
        resultSet = statement.executeQuery("select * from REPORTED where REPORTING_USER = '"+username+ "' and OPINION_ID = " + opinionId );
        if (resultSet.next()) {
            label.setText("You have already reported!");
            label.setWrapText(true);
        }
        else {
            resultSet = statement.executeQuery("select count(*) as counter from REPORTED where OPINION_ID =" + opinionId);
            resultSet.next();
            if (resultSet.getInt("counter") > 3){
                statement.execute("delete from REPORTED where opinion_id = " +opinionId);
                statement.execute("delete from OPINION where opinion_id = " +opinionId);
                label.setText("Done!!");
            }
            else {
                statement.execute("insert into REPORTED values (null,'" + username + "', '" + opinionId + "')");
                label.setText("Done!");
            }
        }
        connection.commit();
        resultSet.close();
        statement.close();
    }

    // SHOPPING LIST

    private static ArrayList<Ingredient> getShoppingList(String username) throws SQLException {
        // get user shopping list saved in database
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SHOPPING_LIST WHERE USERNAME = '" + username + "' AND GROUP_ID IS NULL" );
        ArrayList<Ingredient> shoppingList = new ArrayList<>();
        while (resultSet.next()) {
            int ingredientId = resultSet.getInt("INGREDIENT_LIST_ID");
            Statement ingredientStatement = connection.createStatement();
            ResultSet ingredientResult = ingredientStatement.executeQuery("SELECT * FROM INGREDIENT_LIST WHERE INGREDIENT_LIST_ID =" + ingredientId);
            if (ingredientResult.next()) {
                Ingredient ingredient = new Ingredient(ingredientId, resultSet.getDouble("AMOUNT"), ingredientResult.getString("INGREDIENT_UNIT"), ingredientResult.getString("INGREDIENT_NAME"));
                ingredient.setShoppingListStatus(Status.loaded);
                shoppingList.add(ingredient);

            }
            ingredientResult.close();
            ingredientStatement.close();
        }
        resultSet.close();
        statement.close();
        return shoppingList;
    }

    public static Map<Ingredient, String> getGroupShoppingList(User activeUser, String groupName) throws IOException, SQLException {
        // get saved group shopping list
        if (connection == null)
            setConnection();
        // get group id with group name and username
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT GROUP_ID FROM \"GROUP\" JOIN BELONG USING (GROUP_ID) WHERE NAME = '" + groupName + "' AND USERNAME = '" + activeUser.getUsername() +"'");
        if (resultSet.next()) {
            int groupId = resultSet.getInt("GROUP_ID");
            // get all ingredient_list id from shopping list
            Statement listStatement = connection.createStatement();
            ResultSet listResult = listStatement.executeQuery("SELECT * FROM SHOPPING_LIST WHERE GROUP_ID = " + groupId);
            Map<Ingredient, String> shoppingList = new HashMap<>();
            while (listResult.next()){
                // change id to name
                int ingredientId = listResult.getInt("INGREDIENT_LIST_ID");
                Statement ingredientStatement = connection.createStatement();
                ResultSet ingredientResult = ingredientStatement.executeQuery("SELECT * FROM INGREDIENT_LIST WHERE INGREDIENT_LIST_ID =" + ingredientId);
                if(ingredientResult.next()){
                    shoppingList.put(new Ingredient(ingredientId, listResult.getDouble("AMOUNT"), ingredientResult.getString("INGREDIENT_UNIT"), ingredientResult.getString("INGREDIENT_NAME")), listResult.getString("USERNAME"));
                }
                ingredientResult.close();
                ingredientStatement.close();
            }
            listResult.close();
            listStatement.close();
            statement.close();
            resultSet.close();
            return shoppingList;
        }
        statement.close();
        resultSet.close();
        return null;
    }

    private static void updateShoppingListView(User activeUser) throws SQLException, IOException {
        // save shopping list to database after changes in session
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SHOPPING_LIST " +
                                                            "WHERE UPPER(USERNAME) = '" + activeUser.getUsername().toUpperCase() +
                                                            "' AND GROUP_ID IS NULL");
        // check if the records are not being deleted
        while (resultSet.next()) {
            int id = resultSet.getInt("INGREDIENT_LIST_ID");
            Ingredient ingredient = activeUser.getIngredientFromShoppingListWithID(id);
            if (ingredient != null && ingredient.getShoppingListStatus() == Status.deleted){
                Statement deleteStatement = connection.createStatement();
                deleteStatement.execute("DELETE FROM SHOPPING_LIST " +
                                            "WHERE UPPER(USERNAME) = '" + activeUser.getUsername().toUpperCase() +
                                            "' AND INGREDIENT_LIST_ID = " + ingredient.getId());
                activeUser.removeFromShoppingList(ingredient);
                deleteStatement.close();
            } else if (ingredient != null && ingredient.getShoppingListStatus() == Status.added && ingredient.getQuantity() == resultSet.getDouble("AMOUNT")) {
                // if someone delete and add again recipe
                ingredient.setShoppingListStatus(Status.loaded);
            }
        }
        // add records with Status.added or Status.edited
        for (Ingredient ingredient : activeUser.getShoppingList()) {
                Statement ingStatement = connection.createStatement();
            if (ingredient.getShoppingListStatus() == Status.added && ingredient.getId() != null) {
                Double quantity = ingredient.getQuantity();
                if (!ingredient.getUnit().equals("piece")) {
                    quantity = changeToDefaultUnit(ingredient); // quantity has to be in unit that is already saved in database for give ingredient_list_id
                }
                ingStatement.execute("INSERT INTO SHOPPING_LIST values(null, "
                                                                + quantity + ", '"
                                                                + ingredient.getId()  + "', '"
                                                                + activeUser.getUsername() + "', NULL)");
                ingredient.setShoppingListStatus(Status.loaded);
            } else if (ingredient.getShoppingListStatus() == Status.added && ingredient.getId() == null) {
                ingStatement.execute("BEGIN add_new_ingredient_to_shopping_list('"
                                                                    + ingredient.getName() + "', '"
                                                                    + ingredient.getUnit() + "', "
                                                                    + ingredient.getQuantity() + ", '"
                                                                    + activeUser.getUsername() +"'); END;");
                ingredient.setShoppingListStatus(Status.loaded);
            }
            else if (ingredient.getShoppingListStatus() == Status.edited){
                ingStatement.execute("SELECT shopping_list_id FROM shopping_list WHERE ingredient_list_id = " +ingredient.getId());
                if (resultSet.next()) {
                    ingStatement.execute("UPDATE shopping_list SET amount =" + ingredient.getQuantity() + " WHERE username = '" + activeUser.getUsername() + "' and ingredient_list_id = " + ingredient.getId());
                }
                else{
                    Double quantity = ingredient.getQuantity();
                    if (!ingredient.getUnit().equals("piece")) {
                        quantity = changeToDefaultUnit(ingredient); // quantity has to be in unit that is already saved in database for give ingredient_list_id
                    }
                    ingStatement.execute("INSERT INTO SHOPPING_LIST values(null, "
                            + quantity + ", '"
                            + ingredient.getId()  + "', '"
                            + activeUser.getUsername() + "', NULL)");
                }
                ingredient.setShoppingListStatus(Status.loaded);
            }
            ingStatement.close();
        }

        connection.commit();
        resultSet.close();
        statement.close();
    }

    public static void shareList(User activeUser, int groupID) throws IOException, SQLException {
        // share shopping list with group
        if (connection == null)
            setConnection();
        updateShoppingListView(activeUser);
        if (groupID != -1) {
            Statement statement = connection.createStatement();
            statement.execute("BEGIN share_shopping_list('" + activeUser.getUsername() +  "', "+  groupID + "); END;");
            connection.commit();
            statement.close();
        }
    }

    public static void deleteGroupShoppingList(int groupID) throws IOException, SQLException {
        // delete group shopping list in database (change in current session)
        if (connection == null)
            setConnection();
        if (groupID != -1) {
            Statement statement = connection.createStatement();
            statement.execute("DELETE SHOPPING_LIST WHERE GROUP_ID ="+ groupID);
            statement.close();
        }
    }

    public static void deleteIngredientFromGroupShoppingList(int groupID, Ingredient ingredient) throws IOException, SQLException {
        // delete ingredient from group shopping list changed in current session
        if (connection == null)
            setConnection();
        if (groupID != -1) {
            Statement statement = connection.createStatement();
            statement.execute("DELETE SHOPPING_LIST WHERE GROUP_ID ="+ groupID +" AND INGREDIENT_LIST_ID=" + ingredient.getId());
            statement.close();
        }
    }
}