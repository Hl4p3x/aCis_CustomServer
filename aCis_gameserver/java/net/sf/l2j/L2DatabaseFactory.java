package net.sf.l2j;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class L2DatabaseFactory
{
	protected static Logger _log = Logger.getLogger(L2DatabaseFactory.class.getName());
	
	private static HikariDataSource _hds;
	
	public L2DatabaseFactory()
	{
		try
		{
			HikariConfig config = new HikariConfig();
			config.setDriverClassName("org.mariadb.jdbc.Driver");
			config.setJdbcUrl(Config.DATABASE_URL);
			config.setUsername(Config.DATABASE_LOGIN);
			config.setPassword(Config.DATABASE_PASSWORD);
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("useServerPrepStmts", "true");
			
			config.setMaximumPoolSize(Math.max(10, Config.DATABASE_MAX_CONNECTIONS));
			config.setMaxLifetime(60000);
			config.setMinimumIdle(13);
			config.setIdleTimeout(30000);
			config.setLeakDetectionThreshold(48);
			
			_hds = new HikariDataSource(config);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void shutdown()
	{
		try
		{
			_hds.close();
			_hds = null;
		}
		catch (Exception e)
		{
			_log.log(Level.INFO, "", e);
		}
	}
	
	public Connection getConnection()
	{
		Connection con = null;
		
		while (con == null)
		{
			try
			{
				con = _hds.getConnection();
			}
			catch (SQLException e)
			{
				_log.warning("L2DatabaseFactory: getConnection() failed, trying again " + e);
			}
		}
		return con;
	}
	
	public static L2DatabaseFactory getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final L2DatabaseFactory _instance = new L2DatabaseFactory();
	}
}