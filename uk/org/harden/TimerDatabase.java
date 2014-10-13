package uk.org.harden;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
//import org.h2.tools.DeleteDbFiles;

@SuppressWarnings("UnnecessarySemicolon")
class TimerDatabase {

    private static final Logger LOGGER = Logger.getLogger(TimerDatabase.class.getName());

    public TimerDatabase() {
        //DeleteDbFiles.execute("~/IdeaProjects/P1", "timerDatabase", true);

        ResultSet rs = null;
        int retries = TimerConstants.DB_RETRIES;
        while (retries-- > 0) {
            try (
                    Connection conn = doConnect();
                    Statement  stmt = conn.createStatement();
            )
            {
                DatabaseMetaData md = conn.getMetaData();

                rs = md.getTables(null, null, "TIMERS", null);
                if (!rs.next()) {
                    stmt.execute("CREATE TABLE TIMERS(" +
                            "uuid  CHAR(36) PRIMARY KEY, " +
                            "Index INT)");
                }

                rs = md.getTables(null, null, "STATS", null);
                if (!rs.next()) {
                    stmt.execute("CREATE TABLE STATS(" +
                            "uuid   CHAR(36) PRIMARY KEY, " +
                            TimerConstants.ACTION_START + "  INT, " +
                            TimerConstants.ACTION_STOP + "   INT, " +
                            TimerConstants.ACTION_RESET + "  INT, " +
                            TimerConstants.ACTION_DONE + "   INT, " +
                            TimerConstants.ACTION_DELETE + " INT)");
                }

                rs.close();

                break;
            } catch (SQLException se) {
                if (retries == 0) {
                    LOGGER.info("SQL Exception");
                    se.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null)
                        rs.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            try {
                Thread.sleep(TimerConstants.DB_RETRY_DELAY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Connection doConnect() throws Exception {
        Class.forName("org.h2.Driver");
        return DriverManager.getConnection("jdbc:h2:~/IdeaProjects/P1/timerDatabase", "sa", "");
    }

    //private void doDisconnect(Connection conn) throws SQLException {
    //    conn.close();
    //}

    public void tdAddTimer(final String uuid, final int index) {
        int retries = TimerConstants.DB_RETRIES;
        while (retries-- > 0) {
            try (
                    Connection conn = doConnect();
                    Statement  stmt = conn.createStatement();
            )
            {
                conn.setAutoCommit(false);

                stmt.execute("INSERT INTO timers VALUES('" + uuid + "', " + index + ")");

                stmt.execute("INSERT INTO stats VALUES('" + uuid + "', 0, 0, 0, 0, 0)");

                conn.commit();

                break;
            } catch (SQLException se) {
                if (retries == 0) {
                    LOGGER.info("SQL Exception");
                    se.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(TimerConstants.DB_RETRY_DELAY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void tdUpdateTimer(final String uuid, final String action) {
        int retries = TimerConstants.DB_RETRIES;
        while (retries-- > 0) {
            try (
                    Connection conn = doConnect();
                    Statement  stmt = conn.createStatement();
            )
            {
                stmt.executeUpdate("UPDATE stats SET " + action + " = (" + action + " + 1) WHERE uuid = '" + uuid + "'");

                break;
            } catch (SQLException se) {
                if (retries == 0) {
                    LOGGER.info("SQL Exception");
                    se.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(TimerConstants.DB_RETRY_DELAY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public Map<String, ArrayList<String>> tdGetTimerStats() {
        Map<String, ArrayList<String>> resultMap = new HashMap<>();
        ResultSet rs = null;
        int retries = TimerConstants.DB_RETRIES;
        while (retries-- > 0) {
            try (
                    Connection conn = doConnect();
                    Statement  stmt = conn.createStatement();
            )
            {
                rs = stmt.executeQuery(
                        "SELECT t.Index AS Timer, SUM(Start) AS Start, SUM(Stop) AS Stop, SUM(Reset) AS Reset, SUM(Done) AS Done, SUM(Delete) AS Delete " +
                                "FROM stats s, timers t " +
                                "WHERE t.uuid = s.uuid " +
                                "GROUP BY t.Index"
                );

                int columns = rs.getMetaData().getColumnCount();

                ArrayList<String> heads = new ArrayList<>(columns);
                for (int i = 1; i <= columns; i++) {
                    heads.add(rs.getMetaData().getColumnName(i));
                }
                resultMap.put("Header", heads);

                while (rs.next()) {
                    ArrayList<String> row = new ArrayList<>(columns);

                    for (int i = 1; i <= columns; i++) {
                        row.add(rs.getString(i));
                    }
                    resultMap.put("Timer" + resultMap.size(), row);
                }

                rs.close();

                break;
            } catch (SQLException se) {
                if (retries == 0) {
                    LOGGER.info("SQL Exception");
                    se.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null)
                        rs.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            try {
                Thread.sleep(TimerConstants.DB_RETRY_DELAY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultMap;
    }
}
