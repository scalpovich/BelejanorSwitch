package com.belejanor.switcher.sqlservices;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbutils.AbstractQueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import oracle.jdbc.OracleTypes;

public class ProcRunner extends AbstractQueryRunner {

    public ProcRunner() {
	super();
    }

    public ProcRunner(DataSource ds) {
	super(ds);
    }

    public ProcRunner(boolean pmdKnownBroken) {
        super(pmdKnownBroken);
    }

    public ProcRunner(DataSource ds, boolean pmdKnownBroken) {
        super(ds, pmdKnownBroken);
    }

    public <T> T queryProc(Connection conn, String sql,
                           ResultSetHandler<T> rsh, Object... params)
	         throws SQLException {
	return this.queryProc(conn, false, sql, rsh, params);
    }

    public <T> T queryProc(Connection conn, String sql,
                           ResultSetHandler<T> rsh) throws SQLException {
	 return this.queryProc(conn, false, sql, rsh, (Object[]) null);
	}

    public <T> T queryProc(String sql, ResultSetHandler<T> rsh,
                           Object... params) throws SQLException {
	Connection conn = this.prepareConnection();
        return this.queryProc(conn, true, sql, rsh, params);
    }

    public <T> T queryProc(String sql, ResultSetHandler<T> rsh)
                 throws SQLException {
        Connection conn = this.prepareConnection();
        return this.queryProc(conn, true, sql, rsh, (Object[]) null);
    }

    private <T> T queryProc(Connection conn, boolean closeConn, String sql,
	                    ResultSetHandler<T> rsh, Object... params)
                            throws SQLException {
	if (conn == null) {
		throw new SQLException("Null connection");
	}
	if (sql == null) {
		if (closeConn) {close(conn);}
		throw new SQLException("Null SQL statement");
	}
	if (rsh == null) {
		if (closeConn) {close(conn);}
		throw new SQLException("Null ResultSetHandler");
	}
	if (sql.toUpperCase().indexOf("CALL") == -1) {
		if (closeConn) {close(conn);}
		throw new SQLException("Not a callable statement");
	}
	CallableStatement stmt = null;
	ResultSet rs = null;
	T result = null;

	try {
		stmt = this.prepareCall(conn, sql);
		//`this.fillStatement(stmt, params);
		rs = this.wrap(stmt.executeQuery());
		result = rsh.handle(rs);
	} catch (SQLException e) {
		this.rethrow(e, sql, params);
	} finally {
		try {
			close(rs);
		} finally {
			close(stmt);
			if (closeConn) {
				close(conn);
			}
		}
	}
	return result;
    }

    protected CallableStatement prepareCall(Connection conn, String sql)
                                throws SQLException {
    	
    	CallableStatement cs = null;
    	//call = conn.prepareCall(sql);
    	//call.registerOutParameter("p_iso_resulset", OracleTypes.CURSOR);
    	//return call;
    	cs = conn.prepareCall(sql);
    	
    	cs.setString("i_wISO_000_Mess_Type", "1200");
		cs.setString("i_wISO_011_SysAuditNro", "702002936");
		cs.setLong("i_wISO_012_LocDt_dec", 20170103044506L);
		cs.setString("i_wISO_003_ProcCode", "905000");
		cs.setString("i_wISO_102_Acc_1", "73773773");
		cs.setString("i_wISO_024_NetId", "555551");
		cs.setString("i_wISO_018_MerchType", "C14");
		cs.registerOutParameter("p_iso_resulset", OracleTypes.CURSOR);
		
		return cs;
    	
    }
}
