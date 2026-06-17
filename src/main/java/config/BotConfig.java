package config;

import java.io.InputStream;
import java.util.Properties;

public class BotConfig {
    private static final Properties props = new Properties();

    static {
        try(InputStream input = BotConfig.class.getClassLoader()
                .getResourceAsStream("bot.properties")) {
            props.load(input);
        }catch (Exception e){
            throw new RuntimeException("Failed to load bot.properties", e);
        }
    }
    public static String getBotToken(){
        return props.getProperty("tg.bot.token");
    }
    public static String getBotName(){
        return props.getProperty("tg.bot.name");
    }
}
