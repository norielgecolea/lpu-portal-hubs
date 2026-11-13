package org.nors.dev.codes.lpu.config;

import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

/**
 * Creates the hubs tables and seeds the two catalogs. Safe to run on every startup.
 * Docker also mounts the same script for a fresh Postgres volume.
 */
@Component
public class SchemaMigrator {

    public SchemaMigrator(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/schema.sql"));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to apply the hubs portal schema", ex);
        }
    }
}
