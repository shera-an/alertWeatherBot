package config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("db.properties not found");
            }
            props.load(input);
            System.out.println("✅ db.properties загружен");
        } catch (Exception e) {
            System.err.println("❌ Ошибка загрузки db.properties");
            e.printStackTrace();
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }

    public static String getUrl() {
        return props.getProperty("db.url");
    }

    public static String getUser() {
        return props.getProperty("db.user");
    }

    public static String getPassword() {
        return props.getProperty("db.password");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }
}