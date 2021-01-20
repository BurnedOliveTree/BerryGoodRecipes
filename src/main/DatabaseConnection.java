package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import main.controller.Status;
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

    // TODO KAROLINA addRecipe
    // TODO KSAWERY inviteUser
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
        // @TODO KSAWERY java.sql.SQLRecoverableException
        connection = ods.getConnection();
        System.out.println("Connection with database opened.");
        connection.setAutoCommit(false);
    }

    private static void closeConnection() throws SQLException {
        connection.close();
        connection = null;
        System.out.println("Connection with database closed.");
    }

    public static User login(String username, String password, Label errMess) throws SQLException, IOException {
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
                List<String>  groups = getGroupNames(username);
                ArrayList<Ingredient> shoppingList = getShoppingList(username);
                activeUser = new User(username, userRecipes, favorites, followed, shoppingList, groups);
                // TODO KSAWERY add all the other columns in the future
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



    private static ArrayList<Ingredient> getShoppingList(String username) throws SQLException, IOException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SHOPPING_LIST WHERE USERNAME = '" + username + "' AND GROUP_ID IS NULL" );
        ArrayList<Ingredient> shoppingList = new ArrayList<>();
        while (resultSet.next()) {
            int ingredientId = resultSet.getInt("INGREDIENT_LIST_ID");
            Statement ingredientStatement = connection.createStatement();
            ResultSet ingredientResult = ingredientStatement.executeQuery("SELECT * FROM INGREDIENT_LIST WHERE INGREDIENT_LIST_ID =" + ingredientId);
            if (ingredientResult.next()); {
                Ingredient ingredient = new Ingredient(ingredientId, resultSet.getDouble("AMOUNT"), new Unit(ingredientResult.getString("INGREDIENT_UNIT")), ingredientResult.getString("INGREDIENT_NAME"));
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

    public static User register(String username, String password, Label errMess) throws SQLException, IOException {
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

    public static String setPassword(String username, String newPassword, String oldPassword) throws SQLException, IOException {
        String status;
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from \"USER\" where USERNAME = '"+username+"'");
        if (resultSet.next()) {
            String gotPassword = resultSet.getString("PASSWORD");
            if (gotPassword.equals(oldPassword)) {
                statement.executeUpdate("update \"USER\" set PASSWORD = \'"+newPassword+"\' where USERNAME = \'"+username+"\'");
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

    private static List<String> getUserFollowed(String username) throws SQLException {
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

    public static void saveUser(User user) throws SQLException, IOException {
        if (user != null) {
            if (connection == null)
                setConnection();
            Statement statement = connection.createStatement();
            if (user.getNewFavorites().size() != 0) {
                for (Recipe recipe: user.getNewFavorites()) {
                    statement.executeUpdate("INSERT INTO FAVORITE SELECT null, '" + user.getUsername() + "', "+ recipe.getId() + " FROM DUAL\n" +
                            "WHERE NOT EXISTS (SELECT NULL FROM FAVORITE WHERE RECIPE_ID=" +  recipe.getId() + " AND USERNAME='" + user.getUsername() + "')");
                }
            }
            if (user.getDeletedFavorites().size() != 0) {
                for (Recipe recipe: user.getDeletedFavorites()) {
                    statement.executeUpdate(String.format("DELETE FROM FAVORITE WHERE RECIPE_ID = %d AND USERNAME = '%s'", recipe.getId(),  user.getUsername()));
                }
            }
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
            updateShoppingListView(user);
            connection.commit();
            closeConnection();
        }
    }

    private static List<String> getGroupParticipants(String GroupID) throws SQLException {
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

    public static List<String> getGroupNames(String username) throws SQLException, IOException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT NAME FROM \"GROUP\" JOIN BELONG USING (GROUP_ID) WHERE USERNAME = '" + username + "' AND GROUP_ID != 0");
        List<String> groups = new ArrayList<>();
        while(resultSet.next()) {
            groups.add(resultSet.getString("NAME"));
        }
        statement.close();
        resultSet.close();
        return groups;
    }

    public static List<List<String>> getGroups(User user) throws SQLException, IOException {
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        String query = "select g.GROUP_ID as ID, g.NAME as group_name from \"GROUP\" g join BELONG b on g.GROUP_ID = b.GROUP_ID where b.GROUP_ID != 0 and lower(b.USERNAME) = '"+user.getUsername().toLowerCase()+"'";
        ResultSet resultSet = statement.executeQuery(query);
        List<List<String>> stringList = new ArrayList<>();
        while (resultSet.next()) {
            List<String> tempList = new ArrayList<>();
            tempList.add(resultSet.getString("ID"));
            tempList.add(resultSet.getString("group_name"));
            tempList.addAll(getGroupParticipants(resultSet.getString("ID")));
            stringList.add(tempList);
        }
        resultSet.close();
        statement.close();
        return stringList;
    }

    public static List<Integer> getGroupByName(String[] groupName) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        String query = "select GROUP_ID from \"GROUP\" where lower(NAME) in (\'" + groupName[0].toLowerCase() + "\'";
        for (String s : groupName)
            query = query + ", \'" + s.toLowerCase() + "\'";
        query = query + ")";
        ResultSet resultSet = statement.executeQuery(query);
        List<Integer> groupID = new ArrayList<>();
        if (resultSet.next())
            groupID.add(resultSet.getInt("GROUP_ID"));
        resultSet.close();
        statement.close();
        return groupID;
    }

    public static void addGroup(String name, String username) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        statement.execute("begin add_group(\'"+username+"\', \'"+name+"\'); end;");
        connection.commit();
        statement.close();
    }

    public static void deleteGroup(int groupID) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        statement.execute("begin delete_group("+groupID+"); end;");
        connection.commit();
        statement.close();
    }

    public static void kickUser(String username, int groupID) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate("delete from BELONG where GROUP_ID = "+groupID+" and USERNAME = \'"+username+"\'");
        connection.commit();
        statement.close();
    }

    public static void deleteUser(String username) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        statement.execute("begin delete_account(\'"+username+"\'); end;");
        connection.commit();
        statement.close();
    }

    public static List<Recipe> search(User activeUser) throws SQLException, IOException {
        return search(activeUser, null, null);
    }

    public static List<Recipe> search(User activeUser, String whereStatement, List<Integer> groupID) throws SQLException, IOException {
        if (activeUser == null && groupID != null)
            return null;
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        StringBuilder insideQuery;
        if (activeUser != null) {
            insideQuery = new StringBuilder("select distinct pub.RECIPE_ID from PUBLICITY pub join BELONG blg on blg.GROUP_ID = pub.GROUP_ID where lower(blg.USERNAME) = \'" + activeUser.getUsername().toLowerCase() + "\'");
            if (groupID != null) {
                insideQuery.append(" and pub.GROUP_ID in (").append(groupID.get(0));
                for (int i : groupID)
                    insideQuery.append(", ").append(i);
                insideQuery.append(")");
            }
        }
        else
            insideQuery = new StringBuilder("select distinct RECIPE_ID from PUBLICITY where GROUP_ID = 0");
        String query = "select distinct rcp.RECIPE_ID, rcp.NAME, rcp.OWNER_NAME, rcp.PREPARATION_TIME, rcp.COST, CALC_RATING(rcp.RECIPE_ID) as RATING from RECIPE rcp join INGREDIENT_LIST ing on rcp.RECIPE_ID = ing.RECIPE_ID where rcp.RECIPE_ID in ("+insideQuery+")";
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
        try {
            if (connection == null)
                setConnection();
            Recipe recipe = getRecipe(recipeId);
            closeConnection();
            return recipe;
        } catch (SQLException | IOException err) {
            err.printStackTrace();
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
        double portions = result.getDouble("PORTIONS");
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

    public static List<String> getUnitSystems() throws SQLException, IOException {
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

    public static void createOpinion(Opinion opinion, Label opinionLabel, ListView opinionsView) throws SQLException, IOException {
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

    public static void fillOpinions(Recipe recipe, ListView opinionsView) throws SQLException, IOException {
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

    public static void deleteOpinion(Recipe recipe, String userName, ListView opinionsView) throws IOException, SQLException {

        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("delete from OPINION where USERNAME = '" +userName+ "' and RECIPE_ID = " +recipe.getId());
        connection.commit();
        fillOpinions(recipe, opinionsView);
        resultSet.close();
        statement.close();
    }

    public static void reportOpinion(ListView opinionList, String username, Label label,String opinionAuthor, int recipeId) throws SQLException, IOException {
        if (connection == null)
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

    public static ObservableList<String> getUnits() throws IOException, SQLException {
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
    public static Double convertUnit(Double quantity, String first_unit, String second_unit) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select convert_unit('" +first_unit+ "', '" +second_unit+ "', " +quantity+ ") as result from dual");
        resultSet.next();
        Double result =  resultSet.getDouble("result");
        resultSet.close();
        statement.close();
        return result;
    }

    public static String getBestUnit(String preferedSystem, String currentUnit, Double quantity) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        String unit;
        String bestUnit = "";
        Double error = Double.POSITIVE_INFINITY;
        ResultSet resultSet = statement.executeQuery("select name from unit where unit_system_id = '" + preferedSystem+ "'");
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

    private static void updateShoppingListView(User activeUser) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM SHOPPING_LIST " +
                                                            "WHERE UPPER(USERNAME) = '" + activeUser.getUsername().toUpperCase() +
                                                            "' AND GROUP_ID=NULL");
        // check if the records are not being deleted
        while (resultSet.next()) {
            Ingredient ingredient = activeUser.getShoppingList().get(resultSet.getInt("INGREDIENT_LIST_ID"));
            if (ingredient != null && ingredient.getShoppingListStatus() == Status.deleted){
                statement.execute("DELETE FROM SHOPPING_LIST " +
                                            "WHERE UPPER(USERNAME) = '" + activeUser.getUsername().toUpperCase() +
                                            "' AND INGREDIENT_LIST_ID = " + ingredient.getId());
                activeUser.getShoppingList().remove(resultSet.getInt("INGREDIENT_LIST_ID"));
            } else if (ingredient != null && ingredient.getShoppingListStatus() == Status.added && ingredient.getQuantity() == resultSet.getDouble("AMOUNT")) {
                // if someone delete and add again recipe
                ingredient.setShoppingListStatus(Status.loaded);
            }
        }
        // add records with Status.added
        for (Ingredient ingredient : activeUser.getShoppingList()) {
            if (ingredient.getShoppingListStatus() == Status.added && ingredient.getId() != null) {
                statement.execute("INSERT INTO SHOPPING_LIST values(null, "
                                                                + ingredient.getQuantity() + ", '"
                                                                + ingredient.getId()  + "', '"
                                                                + activeUser.getUsername() + "', NULL)");
                ingredient.setShoppingListStatus(Status.loaded);
            } else if (ingredient.getShoppingListStatus() == Status.added && ingredient.getId() == null) {
                statement.execute("BEGIN add_new_ingredient_to_shopping_list('"
                                                                    + ingredient.getName() + "', '"
                                                                    + ingredient.getUnit().getName() + "', "
                                                                    + ingredient.getQuantity() + ", '"
                                                                    + activeUser.getUsername() +"'); END;");
                ingredient.setShoppingListStatus(Status.loaded);
            }
        }
        connection.commit();
        resultSet.close();
        statement.close();
    }

    public static void shareList(User activeUser, String groupName) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        updateShoppingListView(activeUser);
        Integer groupId = getGroupIdWithName(groupName, activeUser);
        if (groupId != null){
            Statement statement = connection.createStatement();
            statement.execute("BEGIN share_shopping_list('" + activeUser.getUsername() +  "', "+  groupId + "); END;");
            connection.commit();
            statement.close();
        }
    }

    public static Map<Ingredient, String> getGroupShoppingList(User activeUser, String groupName) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT GROUP_ID FROM \"GROUP\" JOIN BELONG USING (GROUP_ID) WHERE NAME = '" + groupName + "' AND USERNAME = '" + activeUser.getUsername() +"'");
        if (resultSet.next()) {
            int groupId = resultSet.getInt("GROUP_ID");
            Statement listStatement = connection.createStatement();
            ResultSet listResult = listStatement.executeQuery("SELECT * FROM SHOPPING_LIST WHERE GROUP_ID = " + groupId);
            Map<Ingredient, String> shoppingList = new HashMap<Ingredient, String>();
            while (listResult.next()){
                int ingredientId = listResult.getInt("INGREDIENT_LIST_ID");
                Statement ingredientStatement = connection.createStatement();
                ResultSet ingredientResult = ingredientStatement.executeQuery("SELECT * FROM INGREDIENT_LIST WHERE INGREDIENT_LIST_ID =" + ingredientId);
                if(ingredientResult.next()){
                    shoppingList.put(new Ingredient(ingredientId, listResult.getDouble("AMOUNT"), new Unit(ingredientResult.getString("INGREDIENT_UNIT")), ingredientResult.getString("INGREDIENT_NAME")), listResult.getString("USERNAME"));
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

    public static Integer getGroupIdWithName(String groupName, User activeUser) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT GROUP_ID " +
                "FROM \"GROUP\" " +
                "    JOIN BELONG USING (GROUP_ID) " +
                "    WHERE NAME = '" + groupName + "' " +
                "      AND USERNAME = '" + activeUser.getUsername() +"'");
        if (resultSet.next()) {
            Integer groupId = resultSet.getInt("GROUP_ID");
            resultSet.close();
            statement.close();
            return groupId;
        }
        return null;
    }

    public static void deleteIngredientFromGroupShoppingList(User activeUser, String groupName, Ingredient ingredient) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        Integer groupId = getGroupIdWithName(groupName, activeUser);
        if (groupId != null) {
            Statement statement = connection.createStatement();
            statement.execute("DELETE SHOPPING_LIST WHERE GROUP_ID ="+ groupId +"AND INGREDIENT_LIST_ID=" + ingredient.getId());
            statement.close();
        }
    }

    public static void deleteGroupShoppingList(User activeUser, String groupName) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        Integer groupId = getGroupIdWithName(groupName, activeUser);
        if (groupId != null) {
            Statement statement = connection.createStatement();
            statement.execute("DELETE SHOPPING_LIST WHERE GROUP_ID ="+ groupId);
            statement.close();
        }
    }

    public static void deleteRecipe(User activeUser, Recipe recipe) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        statement.execute("DELETE RECIPE WHERE OWNER_NAME='" + activeUser.getUsername() + "' AND NAME='" + recipe.getName() +"'");
        connection.commit();
        statement.close();
    }

    public static void addRecipe(Recipe recipe, User activeUser) throws IOException, SQLException {
        if (connection == null)
            setConnection();
        Statement statement = connection.createStatement();
        statement.execute("BEGIN add_recipe('"
                + activeUser.getUsername() + "', '"
                + recipe.getName() + "', '"
                + recipe.getPrepareMethod() + "', "
                + ((recipe.getCost() == 0.0)?"null":recipe.getCost())
                + ", " +  recipe.getPortionNumber()
                + ", '" + recipe.getDateAdded() + "', "
                + ((recipe.getPrepareTime() == null)?"null":recipe.getPrepareTime()) + ", "
                + ((recipe.getAccessibility() == null)?"null":recipe.getAccessibility()) + "); END;");
        connection.commit();
        statement.close();
    }
}