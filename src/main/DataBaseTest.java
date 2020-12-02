package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DataBaseTest {
    String theme;
    String databaseLogin;
    String databasePassword;

    public DataBaseTest() {}

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
        this.theme = prop.getProperty("app.theme");
        this.databaseLogin = prop.getProperty("app.login");
        this.databasePassword = prop.getProperty("app.password");
    }

    public void connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@//ora4.ii.pw.edu.pl:1521/pdb1.ii.pw.edu.pl", this.databaseLogin, this.databasePassword);

            String query = "select * from INGREDIENTS";
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    String name = rs.getString("NAME");
                    System.out.println(name);
                }
            } catch (SQLException e) {
                throw new Error("Problem", e);
            }

        } catch (SQLException e) {
            throw new Error("Problem", e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void test() {
        loadFile();
        connect();
    }
}