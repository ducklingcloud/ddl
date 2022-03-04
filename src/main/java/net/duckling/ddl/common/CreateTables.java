package net.duckling.ddl.common;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import net.duckling.common.DucklingProperties;
import net.duckling.common.db.DBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTables {
    private final Logger log = LoggerFactory.getLogger(CreateTables.class);

    DucklingProperties config;

    public CreateTables(DucklingProperties config) {
        this.config = config;
    }

    /**
     * @return true if created successfully or not needed at all
     */
    public boolean createTablesIfNeeded() {
        boolean result = true;

        String driver = config.getString("c3p0.driverClass", "");
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            log.error(e.toString());
            return false;
        }

        String sqlfile = Paths.get(System.getProperty("ddl.root"),
                                   "WEB-INF", "sql", "ddl.sql").toString();
        String url = config.getString("c3p0.create.url", "");
        String user = config.getString("c3p0.username", "");
        String password = config.getString("c3p0.password", "");
        try (Connection conn =
             DriverManager.getConnection(url, user, password)) {
            // Better test the last table in the sqlfile
            if (! DBUtils.tableExists("vwb_user_feedback", conn)) {
                if (DBUtils.execSqlFile(sqlfile, conn)) {
                    log.info("DDL tables initialized successfully.");
                } else {
                    result = false;
                }
            }
        } catch (SQLException e) {
            log.error("Failed to create tables using '{}' for '{}'\n{}",
                      sqlfile, url, e.toString());
            log.debug("Failed to create tables", e);
            result = false;
        }

        sqlfile = Paths.get(System.getProperty("ddl.root"),
                            "WEB-INF", "sql", "ddlsub.sql").toString();
        url = config.getString("databaseSub.create.url", "");
        user = config.getString("databaseSub.username", "");
        password = config.getString("databaseSub.password", "");
        try (Connection conn =
             DriverManager.getConnection(url, user, password)) {
            // Better test the last table in the sqlfile
            if (! DBUtils.tableExists("vec4user", conn)) {
                if (DBUtils.execSqlFile(sqlfile, conn)) {
                    log.info("DDLSUB tables initialized successfully.");
                } else {
                    result = false;
                }
            }
        } catch (SQLException e) {
            log.error("Failed to create tables using '{}' for '{}'\n{}",
                      sqlfile, url, e.toString());
            log.debug("Failed to create tables", e);
            result = false;
        }

        return result;
    }

}
