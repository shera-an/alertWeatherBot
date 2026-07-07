package config;

import java.io.InputStream;
import java.util.Properties;

public class YandexGeocoderConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = YandexGeocoderConfig.class.getClassLoader()
                .getResourceAsStream("ygeocode.properties")){
            if(input == null){
                System.err.println("ygeocoder.properties не найден!");
                throw new RuntimeException("ygeocoder.properties not found");
            }
            props.load(input);
            System.out.println("ygeocoder.properties загружен");
            System.out.println("Ключ: " + props.getProperty("ygeocoder.api.key"));
            System.out.println("URL: " + props.getProperty("ygeocoder.api.url"));
        }
        catch (Exception e){
            System.err.println("Ошибка загрузки ygeocoder.properties");
            e.printStackTrace();
            throw new RuntimeException("Failed to load ygeocoder.properties", e);
        }
    }

    public static String getYgeocoderKey(){
        return props.getProperty("ygeocoder.api.key");
    }
    public static String getYgeocoderUrl(){
        return props.getProperty("ygeocoder.api.url");
    }

}
