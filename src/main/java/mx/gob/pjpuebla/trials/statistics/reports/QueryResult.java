package mx.gob.pjpuebla.trials.statistics.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class QueryResult implements AutoCloseable {
    private final Connection conn;
    private final PreparedStatement ps;
    private final ResultSet rs;

    public QueryResult(Connection conn, PreparedStatement ps, ResultSet rs) {
        this.conn = conn;
        this.ps = ps;
        this.rs = rs;
    }

    public ResultSet rs() {
        return rs;
    }

    @Override
    public void close() {
        try {
            if (rs != null) rs.close();
        } catch (Exception ignore) {
        }
        try {
            if (ps != null) ps.close();
        } catch (Exception ignore) {
        }
        try {
            if (conn != null) conn.close();
        } catch (Exception ignore) {
        }
    }
}
