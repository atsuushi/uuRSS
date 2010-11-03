package uurss;

import java.sql.*;
import java.util.*;

/**
 * FeedListDAO for JDBC impl.
 */
final class FeedListJdbcDAO extends FeedListDAO {

    private static final String SQL = "SELECT"
                                      + "  name,"
                                      + "  url,"
                                      + "  category,"
                                      + "  enabled,"
                                      + "  showorder,"
                                      + "  fullname"
                                      + " FROM"
                                      + "  feedlist"
                                      + " WHERE"
                                      + "  category=? AND"
                                      + "  enabled='true'"
                                      + " ORDER BY"
                                      + "  showorder";

    Connection conn;
    PreparedStatement stmt;

    FeedListJdbcDAO() throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(SQL);
        } catch (Throwable th) {
            conn.close();
            if (th instanceof SQLException) {
                throw (SQLException)th;
            }
            throw new RuntimeException(th);
        }
        this.conn = conn;
        this.stmt = stmt;
    }

    static Connection getConnection() throws SQLException {
        String url = System.getProperty("connection.url");
        String user = System.getProperty("connection.user");
        String password = System.getProperty("connection.password");
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    List<FeedInfo> select(String category) throws SQLException {
        stmt.clearParameters();
        stmt.setString(1, category);
        ResultSet rs = stmt.executeQuery();
        try {
            List<FeedInfo> a = new ArrayList<FeedInfo>();
            while (rs.next()) {
                a.add(new FeedInfo(rs.getString("name"),
                                   rs.getString("url"),
                                   rs.getString("category"),
                                   Boolean.valueOf(rs.getString("enabled")),
                                   rs.getInt("name"),
                                   rs.getString("fullname")));
            }
            return a;
        } finally {
            rs.close();
        }
    }

    @Override
    void close() throws SQLException {
        try {
            stmt.close();
        } finally {
            conn.close();
        }
    }

}
