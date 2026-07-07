package ru.sheraAn.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class BotConfig {

    private static final Logger logger = LoggerFactory.getLogger(BotConfig.class);
    private static final Properties props = new Properties();

    static {
        try (InputStream input = BotConfig.class.getClassLoader()
                .getResourceAsStream("bot.properties")) {
            if (input == null) {
                logger.warn("Файл ru.sheraAn.bot.properties не найден, будут использованы только переменные окружения");
            } else {
                props.load(input);
                logger.info("Файл ru.sheraAn.bot.properties успешно загружен");
            }
        } catch (Exception e) {
            logger.error("Ошибка загрузки ru.sheraAn.bot.properties, будут использованы только переменные окружения", e);
        }
    }

    public static String getBotToken() {
        // 1. Проверяем переменную окружения (приоритет)
        String envToken = System.getenv("TG_BOT_TOKEN");
        if (envToken != null && !envToken.isEmpty()) {
            logger.debug("Токен бота загружен из переменной окружения");
            return envToken;
        }

        // 2. Fallback: читаем из файла (для локальной разработки)
        String fileToken = props.getProperty("tg.bot.token");
        if (fileToken != null && !fileToken.isEmpty()) {
            logger.debug("Токен бота загружен из ru.sheraAn.bot.properties");
            return fileToken;
        }

        logger.error("Токен бота не найден в переменных окружения или в ru.sheraAn.bot.properties");
        return null;
    }

    public static String getBotName() {
        String envName = System.getenv("TG_BOT_USERNAME");
        if (envName != null && !envName.isEmpty()) {
            logger.debug("Имя бота загружено из переменной окружения: {}", envName);
            return envName;
        }

        String fileName = props.getProperty("tg.ru.sheraAn.bot.username");
        if (fileName != null && !fileName.isEmpty()) {
            logger.debug("Имя бота загружено из ru.sheraAn.bot.properties: {}", fileName);
            return fileName;
        }

        logger.error("Имя бота не найдено в переменных окружения или в ru.sheraAn.bot.properties");
        return null;
    }
}