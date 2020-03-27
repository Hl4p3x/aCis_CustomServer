package net.sf.l2j.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import net.sf.l2j.L2DatabaseFactory;

public abstract class Mysql
{
	public static final Logger _log = Logger.getLogger(Mysql.class.getName());

	/**
	 * Performs a simple sql queries where unnecessary control parameters <BR>
	 * NOTE: In this method, the parameters passed are not valid for SQL-injection!
	 * @param db 
	 * @param query 
	 * @param vars 
	 * @return 
	 */
	public static boolean setEx(L2DatabaseFactory db, String query, Object... vars)
	{
		Connection con = null;
		Statement statement = null;
		PreparedStatement pstatement = null;
		boolean successed = true;
		
		try
		{
			if(db == null)
				db = L2DatabaseFactory.getInstance();

			con = db.getConnection();
			if(vars.length == 0)
			{
				statement = con.createStatement();
				statement.executeUpdate(query);
				statement.close();
			}
			else
			{
				pstatement = con.prepareStatement(query);
				setVars(pstatement, vars);
				pstatement.executeUpdate();
				pstatement.close();
			}
			con.close();
		}
		catch(Exception e)
		{
			_log.warning("Could not execute update '" + query + "': " + e);
			e.printStackTrace();
			successed = false;
		}
		finally
		{
			closeQuietly(con, pstatement);
			closeQuietly(statement);
		}
		return successed;
	}

	public static void setVars(PreparedStatement statement, Object... vars) throws SQLException
	{
		Number n;
		long long_val;
		double double_val;
		for(int i = 0; i < vars.length; i++)
			if(vars[i] instanceof Number)
			{
				n = (Number) vars[i];
				long_val = n.longValue();
				double_val = n.doubleValue();
				if(long_val == double_val)
					statement.setLong(i + 1, long_val);
				else
					statement.setDouble(i + 1, double_val);
			}
			else if(vars[i] instanceof String)
				statement.setString(i + 1, (String) vars[i]);
	}

	public static boolean set(String query, Object... vars)
	{
		return setEx(null, query, vars);
	}

	public static boolean set(String query)
	{
		return setEx(null, query);
	}
	
	public static void closeQuietly(Connection conn) 
	{
		try {
			close(conn);
		} catch (SQLException e) { // NOPMD
			// quiet
		}
	}

	public static void closeQuietly(Connection conn, Statement stmt, ResultSet rs) {

        try {
            closeQuietly(rs);
        } finally {
            try {
                closeQuietly(stmt);
            } finally {
                closeQuietly(conn);
            }
        }
    }
	
	public static void closeQuietly(Connection conn, Statement stmt) 
    {
    	try {
    		closeQuietly(stmt);
    	} finally {
    		closeQuietly(conn);
    	}
    }

    public static void closeQuietly(ResultSet rs) {
        try {
            close(rs);
        } catch (SQLException e) { // NOPMD
            // quiet
        }
    }

    public static void closeQuietly(Statement stmt) {
        try {
            close(stmt);
        } catch (SQLException e) { // NOPMD
            // quiet
        }
    }

    public static void close(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    public static void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    public static void close(Statement stmt) throws SQLException {
        if (stmt != null) {
            stmt.close();
        }
    }
	
}