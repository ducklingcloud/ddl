package net.duckling.ddl.common;

import net.duckling.common.DucklingProperties;
import org.junit.Ignore;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

@Ignore("SQL statements have finished.")
public class CreateTablesTest {

    @BeforeClass
    public static void setUp() throws Exception {
        // System.setProperty("clb.appRoot", "src/main/webapp");
        // cfg_derby = Config.getInstance();

        // config.setProperty("clb.db.jdbcurl",
        //                   "jdbc:derby:dkl_test;create=true");

        // String[][] args = {
        //     { "test.conn-url",
        //       "jdbc:mysql://localhost/dkl_test"
        //       + "?useUnicode=true&characterEncoding=UTF-8" },
        //     { "database.driver",
        //       "com.mysql.jdbc.Driver" }
        // };
        // cfg_mysql = new Config("src/main/webapp", "WEB-INF/conf/umt.properties") {
        //         public Config setProperty(String[][] args) {
        //             for (String[] pair : args) {
        //                 this.props.setProperty(pair[0], pair[1]);
        //             }
        //             return this;
        //         }
        //     }.setProperty(args);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createTablesInDerby() throws Exception {
        String cfgFile = "src/main/webapp/WEB-INF/conf/vwbconfig.properties";
        DucklingProperties config = new DucklingProperties(cfgFile);

        System.setProperty("ddl.root", "src/main/webapp");
        assertTrue(new CreateTables(config).createTablesIfNeeded());
    }

    @Test
    public void createTablesInMysql() {
    }

    // private void execCreateTable(CreateTable bean, String sqlFile) {
    //     bean.dropTablesIfExist(sqlFile);
    //     assertFalse(bean.isTableExist());
    //     bean.createTable(sqlFile);
    //     assertTrue(bean.isTableExist());
    // }

}
