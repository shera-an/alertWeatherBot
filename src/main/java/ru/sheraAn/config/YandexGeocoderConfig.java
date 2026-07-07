package ru.sheraAn.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class YandexGeocoderConfig {

    private static final Logger logger = LoggerFactory.getLogger(YandexGeocoderConfig.class);
    private static final Properties props = new Properties();

    static {
        try (InputStream input = YandexGeocoderConfig.class.getClassLoader()
                .getResourceAsStream("ygeocode.properties")) {
            if (input == null) {
                logger.warn("Файл ygeocode.properties не найден, будут использованы только переменные окружения");
            } else {
                props.load(input);
                logger.info("Файл ygeocode.properties успешно загружен");
            }
        } catch (Exception e) {
            logger.error("Ошибка загрузки ygeocode.properties, будут использованы только переменные окружения", e);
        }
    }

    public static String getYgeocoderKey() {
        // 1. Проверяем переменную окружения (приоритет)
        String envKey = System.getenv("YANDEX_GEOCODER_KEY");
        if (envKey != null && !envKey.isEmpty()) {
            logger.debug("Ключ Яндекс Геокодера загружен из переменной окружения");
            return envKey;
        }

        // 2. Fallback: читаем из файла (для локальной разработки)
        String fileKey = props.getProperty("ygeocoder.api.key");
        if (fileKey != null && !fileKey.isEmpty()) {
            logger.debug("Ключ Яндекс Геокодера загружен из ygeocode.properties");
            return fileKey;
        }

        logger.error("Ключ Яндекс Геокодера не найден в переменных окружения или в ygeocode.properties");
        return null;
    }

    public static String getYgeocoderUrl() {
        // 1. Проверяем переменную окружения (приоритет)
        String envUrl = System.getenv("YANDEX_GEOCODER_URL");
        if (envUrl != null && !envUrl.isEmpty()) {
            logger.debug("URL Яндекс Геокодера загружен из переменной окружения: {}", envUrl);
            return envUrl;
        }

        // 2. Fallback: читаем из файла (для локальной разработки)
        String fileUrl = props.getProperty("ygeocoder.api.url");
        if (fileUrl != null && !fileUrl.isEmpty()) {
            logger.debug("URL Яндекс Геокодера загружен из ygeocode.properties: {}", fileUrl);
            return fileUrl;
        }

        logger.error("URL Яндекс Геокодера не найден в переменных окружения или в ygeocode.properties");
        return null;
    }
}