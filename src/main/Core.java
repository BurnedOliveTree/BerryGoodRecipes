package main;

//import main.recipeModel.Ingredient;
//import main.recipeModel.Recipe;
//import main.recipeModel.Unit;
//import main.userModel.User;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;

import main.userModel.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Core {
    public static String theme;
    public static String databaseLogin;
    public static String databasePassword;
    public static String databasePort;
    public static String databaseServiceName;
    public static String databaseHost;
    public static User activeUser;
//    private List<User> users;
//    private List<Recipe> recipes;
//    private List<Ingredient> ingredients;
//    private List<Unit> units;

    public Core() {
        loadFile();
//        users = new LinkedList<>();
//        recipes = new LinkedList<>();
    }

    private void loadFile() {
        Properties prop = new Properties();
        String fileName = "src/resources/app.config";
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        try {
            prop.load(is);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        theme = prop.getProperty("app.theme");
        databaseLogin = prop.getProperty("app.login");
        databasePassword = prop.getProperty("app.password");
        databasePort = prop.getProperty("app.port");
        databaseServiceName = prop.getProperty("app.service.name");
        databaseHost = prop.getProperty("app.host");

    }
//    public void addAccount(int argID, String argUsername, String argPassword) {
//        users.add(new User(argID, argUsername, argPassword));
//    }
//    public void deleteAccount(User account) {
//        users.remove(account);
//    }
//    public void addRecipe(String name, User author, String prepareMethod, boolean accessibility,  ArrayList<Ingredient> ingredientList) {
//        recipes.add(new Recipe(name, author, prepareMethod, accessibility, ingredientList));
//    }
//    public void deleteRecipe(Recipe recipe) {
//        recipes.remove(recipe);
//    }
}
