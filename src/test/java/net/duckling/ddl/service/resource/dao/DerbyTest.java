package net.duckling.ddl.service.resource.dao;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Ignore
public class DerbyTest {
    private static Connection conn;
    private static String url, user, password;

    public DerbyTest() {
    }

    @BeforeClass
    public static void setUp() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        url = "jdbc:derby:dkl_ddl";
        user = "duckling";
        password = "mysql4dkl";
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }

    @Before
    public void init() throws Exception {
        conn = DriverManager.getConnection(url, user, password);
    }

    @After
    public void cleanup() throws Exception {
        conn.close();
    }

    @Test
    public void insert_select_NPE_preStmt() throws Exception {
        boolean result = true;
        String sql = 
                "INSERT INTO a1_tag_item (tid, tgid, rid) "+
                "SELECT * FROM ( VALUES ( "+
                "  CAST(? as int), CAST(? as int), CAST(? as int) "+
                "  ) ) AS t "+
                "WHERE NOT EXISTS ( "+
                "  SELECT * FROM a1_tag_item "+
                "  WHERE tid = ? AND tgid = ? "+
                "    AND rid = ? "+
                ")";
        int tid = 7, tgid = 8, rid = 9;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setInt(i++, tid);
            ps.setInt(i++, tgid);
            ps.setInt(i++, rid);
            ps.setInt(i++, tid);
            ps.setInt(i++, tgid);
            ps.setInt(i++, rid);
            result = ps.execute();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }

        // false for no ResultSet
        assertFalse(result);
    }

    @Test
    public void insert_select_NPE_stmt() throws Exception {
        boolean result = true;
        try (Statement stmt = conn.createStatement()) {
            String sql = 
                    "INSERT INTO a1_tag_item (tid, tgid, rid) "+
                    "SELECT * FROM ( VALUES ( 3,2,1 ) ) AS t "+
                    "WHERE NOT EXISTS ( "+
                    "  SELECT * FROM a1_tag_item "+
                    "  WHERE tid = 3 AND tgid = 2 "+
                    "    AND rid = 1 "+
                    ")";
            result = stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }

        // false for no ResultSet
        assertFalse(result);
    }

}
