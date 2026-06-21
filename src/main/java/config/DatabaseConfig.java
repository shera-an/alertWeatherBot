package config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.RecursiveTask;

public class DatabaseConfig {
    private static final  Properties props = new Properties();
    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("db.properties")){
            props.load(input);

        }catch (Exception e){
            throw new RuntimeException("Failed to load db.properties", e);

        }
    }
    public static String getUrl(){
        return props.getProperty("db.url");
    }
    public static String getUser(){
        return props.getProperty("db.user");
    }
    public static String getPassword(){
        return props.getProperty("db.password");
    }
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(getUrl(),getUser(),getPassword());
    }
}
