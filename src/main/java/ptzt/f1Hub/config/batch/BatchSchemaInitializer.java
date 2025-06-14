package ptzt.f1Hub.config.batch;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@Configuration
@Slf4j
public class BatchSchemaInitializer {

    private final DataSource dataSource;

    public BatchSchemaInitializer(@Qualifier("dataSource") DataSource dataSource) {
        this.dataSource =  dataSource;

    }

    @PostConstruct
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            if (!batchTablesExist(connection)) {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("org/springframework/batch/core/schema-h2.sql"));
                populator.execute(dataSource);
                log.info("Spring Batch schema initialized.");
            } else {
                log.info("Spring Batch schema already exists. Skipping initialization.");
            }
        } catch (Exception e) {
            log.error("Error checking or initializing Spring Batch schema: {}", e.getMessage());
        }
    }

    //Comprobation of the batch tables
    private boolean batchTablesExist(Connection connection) throws Exception {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(null, null, "BATCH_JOB_INSTANCE", null)) {
            return tables.next();
        }
    }
}