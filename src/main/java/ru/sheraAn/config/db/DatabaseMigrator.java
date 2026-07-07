package ru.sheraAn.config.db;

import ru.sheraAn.config.DatabaseConfig;
import org.flywaydb.core.Flyway;

public class DatabaseMigrator {
    public static void migrate(){
        Flyway flyway = Flyway.configure()
                .dataSource(DatabaseConfig.getUrl(),
                        DatabaseConfig.getUser(),
                        DatabaseConfig.getPassword())
                .load();
        flyway.migrate();
    }
}
