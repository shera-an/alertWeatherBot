package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static final Properties props = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                logger.warn("Файл db.properties не найден, будут использованы только переменные окружения");
            } else {
                props.load(input);
                logger.info("Файл db.properties успешно загружен");
            }
        } catch (Exception e) {
            logger.error("Ошибка загрузки db.properties, будут использованы только переменные окружения", e);
        }
    }

    public static String getUrl() {
        String env = System.getenv("DB_URL");
        if (env != null && !env.isEmpty()) {
            logger.debug("URL БД загружен из переменной окружения");
            return env;
        }
        String fileUrl = props.getProperty("db.url");
        if (fileUrl != null && !fileUrl.isEmpty()) {
            logger.debug("URL БД загружен из db.properties");
            return fileUrl;
        }
        logger.error("URL БД не найден в переменных окружения или в db.properties");
        return null;
    }

    public static String getUser() {
        String env = System.getenv("DB_USER");
        if (env != null && !env.isEmpty()) {
            logger.debug("Пользователь БД загружен из переменной окружения");
            return env;
        }
        String fileUser = props.getProperty("db.user");
        if (fileUser != null && !fileUser.isEmpty()) {
            logger.debug("Пользователь БД загружен из db.properties");
            return fileUser;
        }
        logger.error("Пользователь БД не найден в переменных окружения или в db.properties");
        return null;
    }

    public static String getPassword() {
        String env = System.getenv("DB_PASSWORD");
        if (env != null && !env.isEmpty()) {
            logger.debug("Пароль БД загружен из переменной окружения");
            return env;
        }
        String filePassword = props.getProperty("db.password");
        if (filePassword != null && !filePassword.isEmpty()) {
            logger.debug("Пароль БД загружен из db.properties");
            return filePassword;
        }
        logger.error("Пароль БД не найден в переменных окружения или в db.properties");
        return null;
    }

    public static Connection getConnection() throws SQLException {
        String url = getUrl();
        String user = getUser();
        String password = getPassword();

        if (url == null || user == null || password == null) {
            throw new SQLException("Не удалось получить параметры подключения к БД");
        }

        logger.info("Подключение к БД...");
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            logger.info("Подключение к БД успешно установлено");
            return connection;
        } catch (SQLException e) {
            logger.error("Ошибка подключения к БД: {}", e.getMessage());
            throw e;
        }
    }
}