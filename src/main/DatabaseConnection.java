package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;

import main.controller.MainPane;
import main.controller.UserAdminPane;
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

    // TODO save shoppingList, read shoppingList, addRecipe, deleteRecipe
    // TODO deleteGroup, inviteUser, kickUser, deleteAccount
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
                List<String> followed = getUserFollowed(username);
                activeUser = new User(username, userRecipes, favorites, followed);
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
                activeUser = new User(username);
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

    public static String setPassword(String username, String newPassword, String oldPassword) throws SQLException, IOException {
        String status;
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
        closeConnection();
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

    public static void getGroups(UserAdminPane adminPane, TilePane tilePane, User user) throws SQLException, IOException {
        setConnection();
        Statement statement = connection.createStatement();
        String query = "select g.GROUP_ID as ID, g.NAME as group_name from \"GROUP\" g join BELONG b on g.GROUP_ID = b.GROUP_ID where b.GROUP_ID != 0 and lower(b.USERNAME) = '"+user.getUsername().toLowerCase()+"'";
        ResultSet resultSet = statement.executeQuery(query);
        List<MenuButton> panelist = new ArrayList<>();
        while (resultSet.next()) {
            String groupName = resultSet.getString("group_name");
            MenuButton tempButton = new MenuButton(groupName);
            tempButton.setWrapText(true);
            tempButton.setTextAlignment(TextAlignment.CENTER);
            tempButton.setAlignment(Pos.CENTER);
            tempButton.setPrefSize(192, 64);
            MenuItem menuItem = new MenuItem("Show shopping list");
            menuItem.setOnAction(e -> System.out.println("TODO jump to shopping list"));
            tempButton.getItems().add(menuItem);
            menuItem = new MenuItem("Show recipes");
            menuItem.setOnAction(e -> adminPane.getGroupRecipes(groupName));
            tempButton.getItems().add(menuItem);
            Menu followMenu = new Menu("Follow user");
            List<String> tempStringList = getGroupParticipants(resultSet.getString("ID"));
            tempStringList.remove(user.getUsername());
            for (String s : tempStringList) {
                menuItem = new MenuItem(s);
                if (user.getFollowed().contains(s))
                    menuItem.setDisable(true);
                MenuItem finalMenuItem = menuItem;
                menuItem.setOnAction(e -> {
                    user.followUser(s);
                    adminPane.refreshFollowedList();
                    finalMenuItem.setDisable(true);
                });
                followMenu.getItems().add(menuItem);
            }
            tempButton.getItems().add(followMenu);
            tempButton.getItems().add(new SeparatorMenuItem());
            Menu kickMenu = new Menu("Kick user");
            tempStringList = getGroupParticipants(resultSet.getString("ID"));
            for (String s : tempStringList) {
                menuItem = new MenuItem(s);
                menuItem.setOnAction(e -> System.out.println("TODO actually kick user"));
                kickMenu.getItems().add(menuItem);
            }
            tempButton.getItems().add(kickMenu);
            menuItem = new MenuItem("Delete group");
            menuItem.setOnAction(e -> System.out.println("TODO actually delete group"));
            tempButton.getItems().add(menuItem);
            panelist.add(tempButton);
        }
        tilePane.getChildren().clear();
        tilePane.getChildren().addAll(panelist);
        resultSet.close();
        statement.close();
        closeConnection();
    }

    public static List<Integer> getGroupByName(String[] groupName) throws IOException, SQLException {
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
        closeConnection();
        return groupID;
    }

    public static void addGroup(String name, String username) throws IOException, SQLException {
        setConnection();
        Statement statement = connection.createStatement();
        statement.execute("begin add_group(\'"+username+"\', \'"+name+"\'); end;");
        connection.commit();
        statement.close();
        closeConnection();
    }

    public static void fillResults(MainPane mainPane, TilePane tilePain) throws SQLException, IOException {
        fillResults(mainPane, tilePain, null, null);
    }

    public static void fillResults(MainPane mainPane, TilePane tilePain, String whereStatement, List<Integer> groupID) throws SQLException, IOException {
        tilePain.getChildren().clear();
        if (mainPane.activeUser == null && groupID != null)
            return;  // TODO show a message that not logged in user cannot search by groups
        setConnection();
        Statement statement = connection.createStatement();
        String insideQuery;
        if (mainPane.activeUser != null) {
            insideQuery = "select distinct pub.RECIPE_ID from PUBLICITY pub join BELONG blg on blg.GROUP_ID = pub.GROUP_ID where lower(blg.USERNAME) = \'" + mainPane.activeUser.getUsername().toLowerCase() + "\'";
            if (groupID != null) {
                insideQuery = insideQuery + " and pub.GROUP_ID in (" + groupID.get(0);
                for (int i : groupID)
                    insideQuery = insideQuery + ", " + i;
                insideQuery = insideQuery + ")";
            }
        }
        else
            insideQuery = "select distinct RECIPE_ID from PUBLICITY where GROUP_ID = 0";
        String query = "select distinct rcp.RECIPE_ID, rcp.NAME, rcp.PREPARATION_TIME, rcp.COST, CALC_RATING(rcp.RECIPE_ID) as RATING from RECIPE rcp join INGREDIENT_LIST ing on rcp.RECIPE_ID = ing.RECIPE_ID where rcp.RECIPE_ID in ("+insideQuery+")";
        if (whereStatement != null) {
            query = query + " AND " + whereStatement;
        }
        System.out.println(query);
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

    public static void getUnitSystems(Menu unitSystemMenu, User activeUser) throws SQLException, IOException {
        setConnection();
        Statement statement = connection.createStatement();
        String query = "select NAME from UNIT_SYSTEM where not NAME like 'N%A'";
        ResultSet resultSet = statement.executeQuery(query);
        List<MenuItem> itemList = new ArrayList<>();
        while (resultSet.next()) {
            MenuItem tempItem = new MenuItem(resultSet.getString("name"));
            tempItem.setOnAction(e -> activeUser.setDefaultUnitSystem(tempItem.getText()));
            itemList.add(tempItem);
        }
        unitSystemMenu.getItems().clear();
        unitSystemMenu.getItems().addAll(itemList);
        resultSet.close();
        statement.close();
        closeConnection();
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

    public static ObservableList<String> get_units() throws IOException, SQLException {
        ObservableList<String> unitsList = FXCollections.observableArrayList();
        setConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select NAME from UNIT where name != 'piece'" );
        while (resultSet.next()){
            unitsList.add(resultSet.getString("NAME"));
        }
        return unitsList;
    }
public static Double convertUnit(Double quantity, String first_unit, String second_unit) throws IOException, SQLException {
    setConnection();
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery("select round(convert_unit('" +first_unit+ "', '" +second_unit+ "', " +quantity+ "),2) as result from dual");
    resultSet.next();
    return resultSet.getDouble("result");
}

}