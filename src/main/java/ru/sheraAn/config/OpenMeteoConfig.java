package ru.sheraAn.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class OpenMeteoConfig {

    private static final Logger logger = LoggerFactory.getLogger(OpenMeteoConfig.class);
    private static final Properties props = new Properties();

    static {
        try (InputStream input = OpenMeteoConfig.class.getClassLoader()
                .getResourceAsStream("openmeteo.properties")) {
            if (input == null) {
                logger.warn("Файл openmeteo.properties не найден, будут использованы только переменные окружения");
            } else {
                props.load(input);
                logger.info("Файл openmeteo.properties успешно загружен");
            }
        } catch (Exception e) {
            logger.error("Ошибка загрузки openmeteo.properties, будут использованы только переменные окружения", e);
        }
    }

    public static String getUrl() {
        // 1. Проверяем переменную окружения (приоритет)
        String envUrl = System.getenv("OPENMETEO_URL");
        if (envUrl != null && !envUrl.isEmpty()) {
            logger.debug("URL Open-Meteo загружен из переменной окружения: {}", envUrl);
            return envUrl;
        }

        // 2. Fallback: читаем из файла (для локальной разработки)
        String fileUrl = props.getProperty("openmeteo.api.url");
        if (fileUrl != null && !fileUrl.isEmpty()) {
            logger.debug("URL Open-Meteo загружен из openmeteo.properties: {}", fileUrl);
            return fileUrl;
        }

        logger.error("URL Open-Meteo не найден в переменных окружения или в openmeteo.properties");
        return null;
    }
}