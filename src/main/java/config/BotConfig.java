package config;

import java.io.InputStream;
import java.util.Properties;

public class BotConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = BotConfig.class.getClassLoader()
                .getResourceAsStream("bot.properties")) {
            if (input == null) {
                throw new RuntimeException("bot.properties not found");
            }
            props.load(input);
            System.out.println("bot.properties загружен");
        } catch (Exception e) {
            System.err.println("Ошибка загрузки bot.properties");
            e.printStackTrace();
            throw new RuntimeException("Failed to load bot.properties", e);
        }
    }

    public static String getBotToken() {
        return props.getProperty("tg.bot.token");
    }

    public static String getBotName() {
        return props.getProperty("tg.bot.name");
    }
}