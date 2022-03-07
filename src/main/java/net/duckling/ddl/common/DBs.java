package net.duckling.ddl.common;

import net.duckling.common.DucklingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Here are common info and utility methods for Database operation.
 * Especially for compatibility across multiple DBMS.
 */
public class DBs {
    private static final Logger log =
            LoggerFactory.getLogger(DBs.class);
    private static String dbms = null;

    public static String getDbms() {
        if (dbms == null) {
            String cfgFile = Paths.get(System.getProperty("ddl.root"),
                                       "WEB-INF",
                                       "conf",
                                       "vwbconfig.properties").toString();
            try {
                DucklingProperties config =
                        new DucklingProperties(cfgFile);
                dbms = config.getString("c3p0.url", "null:null")
                        .split(":")[1];
            } catch (IOException e) {
                log.error("Can't get config file '{}'\n{}", cfgFile,
                          e.toString());
                return "null";
            }
        }
        return dbms;
    }

}
