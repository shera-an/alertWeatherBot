package config;

import java.io.InputStream;
import java.util.Properties;

public class OpenMeteoConfig {
    private static final Properties props = new Properties();

    static {
        try(InputStream input = OpenMeteoConfig.class.getClassLoader()
                .getResourceAsStream("openmeteo.properties")) {
            if(input == null){
                System.err.println("openmeteo.properties не найден");
                throw new RuntimeException("openmeteo.properties not found" );
            }
            props.load(input);
            System.out.println("openmeteo.properties загружен");
            System.out.println("URL: " + props.getProperty("openmeteo.api.url"));

        }catch (Exception e){
            System.err.println("Ошибка загрузки openmeteo.properties");
            e.printStackTrace();
            throw new RuntimeException("Failed to load openmeteo.properties", e);
        }
    }
    public static String getUrl(){return props.getProperty("openmeteo.api.url");}
}
