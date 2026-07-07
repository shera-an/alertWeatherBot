package config;

import java.io.InputStream;
import java.util.Properties;

public class YandexWeatherConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = YandexWeatherConfig.class.getClassLoader()
                .getResourceAsStream("ywapi.properties")) {
            if (input == null) {
                System.err.println("ywapi.properties не найден!");
                throw new RuntimeException("ywapi.properties not found");
            }
            props.load(input);
            System.out.println("ywapi.properties загружен");
            System.out.println("Ключ: " + props.getProperty("ywapi.api.key"));
            System.out.println("URL: " + props.getProperty("ywapi.api.url"));
        } catch (Exception e) {
            System.err.println("Ошибка загрузки ywapi.properties");
            e.printStackTrace();
            throw new RuntimeException("Failed to load ywapi.properties", e);
        }
    }

    public static String getywapiKey() {
        return props.getProperty("ywapi.api.key");
    }

    public static String getywapiUrl() {
        return props.getProperty("ywapi.api.url");
    }

}