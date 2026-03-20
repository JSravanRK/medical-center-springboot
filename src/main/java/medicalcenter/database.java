package medicalcenter;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * database.java  — UPDATED FOR SPRING BOOT
 *
 * Same API as the original (JSPs still call new database(), db.select(), etc.)
 * but now reads DB credentials from application.properties automatically.
 *
 * Original: hardcoded root/admin
 * Updated : reads spring.datasource.url / username / password from classpath
 */
public class database {

    private Connection con = null;
    private Statement st;
    private ResultSet rs;

    // Defaults (used if application.properties is not found)
    private String url      = "jdbc:mysql://localhost:3306/central_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private String userName = "root";
    private String password = "admin";

    public database() throws SQLException {
        // Try to read credentials from application.properties on the classpath
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                String dsUrl  = props.getProperty("spring.datasource.url");
                String dsUser = props.getProperty("spring.datasource.username");
                String dsPass = props.getProperty("spring.datasource.password");
                if (dsUrl  != null) url      = dsUrl;
                if (dsUser != null) userName = dsUser;
                if (dsPass != null) password = dsPass;
            }
        } catch (Exception e) {
            System.out.println("database: could not read application.properties, using defaults. " + e);
        }

        try {
            // Use the newer driver class (old com.mysql.jdbc.Driver still works too)
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(url, userName, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new SQLException("Could not connect to database: " + ex.getMessage());
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        st = con.createStatement();
        rs = st.executeQuery(query);
        return rs;
    }

    public void executeProcedure(String procName, int paramValue) throws SQLException {
        String query = " { CALL " + procName + "( ? ) } ";
        CallableStatement cs = con.prepareCall(query);
        cs.setInt(1, paramValue);
        cs.execute();
    }

    public void executeProcedure(String procName, int paramValue1, int paramValue2) throws SQLException {
        String query = " { CALL " + procName + "( ? , ? ) } ";
        CallableStatement cs = con.prepareCall(query);
        cs.setInt(1, paramValue1);
        cs.setInt(2, paramValue2);
        cs.execute();
    }

    public ResultSet select(String tbl) throws SQLException {
        return select(tbl, "*", "", "");
    }

    public ResultSet select(String tbl, String fld) throws SQLException {
        return select(tbl, fld, "", "");
    }

    public ResultSet select(String tbl, String fld, String cnd) throws SQLException {
        return select(tbl, fld, " where " + cnd, "");
    }

    public ResultSet select(String tbl, String fld, String cnd, String ord) throws SQLException {
        if (!ord.equals("")) ord = " order by " + ord;
        String query = "select " + fld + " from " + tbl + cnd + ord;
        rs = null;
        st = con.createStatement();
        rs = st.executeQuery(query);
        return rs;
    }

    public void closeStatement() throws SQLException {
        st.close();
    }

    public void insert(String tbl, String fld, String values) throws SQLException {
        if (!fld.equals("")) fld = "( " + fld + " ) ";
        String query = "insert into " + tbl + fld + " values (" + values + ")";
        st = con.createStatement();
        st.execute(query);
    }

    public void insert(String tbl, String[] fld, String[] values) throws SQLException {
        String temp1 = "", temp2 = "";
        temp1 += fld[0];
        temp2 += values[0];
        for (int i = 1; i < fld.length; i++) {
            temp1 = temp1 + ", " + fld[i];
            temp2 = temp2 + ", " + values[i];
        }
        String query = "insert into " + tbl + "(" + temp1 + ")" + "values(" + temp2 + ")";
        st = con.createStatement();
        st.execute(query);
    }

    public void insert(String tbl, String[] fld, String[][] rows) throws SQLException {
        String temp1 = "", temp2 = "";
        int i;
        for (i = 0; i < fld.length - 1; i++) {
            temp1 += fld[i] + ", ";
            temp2 += "?, ";
        }
        temp1 += fld[i];
        temp2 += "?";
        String query = "insert into " + tbl + "(" + temp1 + ")" + " values(" + temp2 + ")";
        PreparedStatement pst = con.prepareStatement(query);
        for (i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length; j++)
                pst.setInt(j + 1, Integer.parseInt(rows[i][j]));
            pst.addBatch();
        }
        pst.executeBatch();
        pst.close();
    }

    public void insert2(String tbl, String[] fld, String[][] rows) throws SQLException {
        String temp1 = "", temp2 = "";
        int i;
        for (i = 0; i < fld.length - 1; i++) {
            temp1 += fld[i] + ", ";
            temp2 += "?, ";
        }
        temp1 += fld[i];
        temp2 += "?";
        String query = "insert into " + tbl + "(" + temp1 + ")" + " values(" + temp2 + ")";
        PreparedStatement pst = con.prepareStatement(query);
        for (i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length; j++) {
                if (j == 1)       pst.setString(j + 1, rows[i][j]);
                else if (j == 6)  pst.setString(j + 1, rows[i][j]);
                else if (j == 2) {
                    if (rows[i][j].equals("0")) pst.setNull(j + 1, java.sql.Types.INTEGER);
                    else                        pst.setInt(j + 1, Integer.parseInt(rows[i][j]));
                } else            pst.setInt(j + 1, Integer.parseInt(rows[i][j]));
            }
            pst.addBatch();
        }
        pst.executeBatch();
        pst.close();
    }

    public void insert3(String tbl, String[] fld, String[][] rows) throws SQLException {
        String temp1 = "", temp2 = "";
        int i;
        for (i = 0; i < fld.length - 1; i++) {
            temp1 += fld[i] + ", ";
            temp2 += "?, ";
        }
        temp1 += fld[i];
        temp2 += "?";
        String query = "insert into " + tbl + "(" + temp1 + ")" + " values(" + temp2 + ")";
        PreparedStatement pst = con.prepareStatement(query);
        Date date = new Date(0);
        for (i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length; j++) {
                if (j == 3)            pst.setDouble(j + 1, Double.parseDouble(rows[i][j]));
                else if (j == 4 || j == 5) pst.setDate(j + 1, date.valueOf(rows[i][j]));
                else                   pst.setInt(j + 1, Integer.parseInt(rows[i][j]));
            }
            pst.addBatch();
        }
        pst.executeBatch();
        pst.close();
    }

    public void delete(String tbl, String cnd) throws SQLException {
        if (!cnd.equals("")) cnd = " where " + cnd;
        String query = "Delete from " + tbl + cnd;
        st = con.createStatement();
        st.execute(query);
    }

    public void update(String tbl, String values, String cnd) throws SQLException {
        if (!cnd.equals("")) cnd = " where " + cnd;
        String query = "update " + tbl + " set " + values + cnd;
        st = con.createStatement();
        st.execute(query);
    }

    public void truncate(String tbl) throws SQLException {
        String query = "truncate table " + tbl;
        st = con.createStatement();
        st.executeQuery(query);
    }

    public void commit() throws SQLException {
        con.commit();
    }

    public void setAutoCommit(boolean val) throws SQLException {
        con.setAutoCommit(val);
    }

    public Connection getCon() {
        return con;
    }

    public void close() throws SQLException {
        con.close();
    }
}
