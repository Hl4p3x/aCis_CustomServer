package hwid.hwidmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.network.GameClient;

public class HWIDManager
{
	protected static Logger _log = Logger.getLogger(HWIDManager.class.getName());
	private static HWIDManager _instance;
	public static Map<Integer, HWIDInfoList> _listHWID;
	public static Map<Integer, Integer> _sessions;

	public HWIDManager()
	{
		_listHWID = new HashMap<>();
		load();
		System.out.println("- HWID Info: Loaded " + _listHWID.size() + " HWIDs");
	}
	
	public static HWIDManager getInstance()
	{
		if (_instance == null)
			_instance = new HWIDManager();

		return _instance;
	}
	
	@SuppressWarnings("resource")
	private static void load()
	{
		try
		{
			Connection con = L2DatabaseFactory.getInstance().getConnection();
			Throwable localThrowable2 = null;
			try
			{
				PreparedStatement statement = con.prepareStatement("SELECT * FROM hwid_info");
				ResultSet rset = statement.executeQuery();
				int counterHWIDInfo = 0;
				while (rset.next())
				{
					HWIDInfoList hInfo = new HWIDInfoList(counterHWIDInfo);
					hInfo.setHwids(rset.getString("HWID"));
					hInfo.setCount(rset.getInt("WindowsCount"));
					hInfo.setLogin(rset.getString("Account"));
					hInfo.setPlayerID(rset.getInt("PlayerID"));
					hInfo.setLockType(HWIDInfoList.LockType.valueOf(rset.getString("LockType")));
					_listHWID.put(Integer.valueOf(counterHWIDInfo), hInfo);
					counterHWIDInfo++;
				}
			}
			catch (Throwable localThrowable1)
			{
				localThrowable2 = localThrowable1;
				throw localThrowable1;
			}
			finally
			{
				if (con != null)
					if (localThrowable2 != null)
						try
				{
							con.close();
				}
				catch (Throwable x2)
				{
					localThrowable2.addSuppressed(x2);
				}
					else
						con.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static int getAllowedWindowsCount(final GameClient pi)
	{
		if (_listHWID.size() == 0)
			return -1;

		for (int i = 0; i < _listHWID.size(); i++)
			if (_listHWID.get(i).getHWID().equals(pi.getHWID()))
			{
				if (_listHWID.get(i).getHWID().equals(""))
					return -1;
				
				return _listHWID.get(i).getCount();
			}
		return -1;
	}
	
	public static void reload()
	{
		_instance = new HWIDManager();
	}
	
	public static void updateHWIDInfo(GameClient client, int windowscount)
	{
		updateHWIDInfo(client, windowscount, HWIDInfoList.LockType.NONE);
	}

	public static void updateHwidInfo(final GameClient client, final HWIDInfoList.LockType lockType)
	{
		updateHWIDInfo(client, 1, lockType);
	}

	@SuppressWarnings("resource")
	public static void updateHWIDInfo(GameClient client, int windowsCount, HWIDInfoList.LockType lockType)
	{
		int counterHwidInfo = _listHWID.size();
		boolean isFound = false;

		for (int i = 0; i < _listHWID.size(); i++)
			if (_listHWID.get(i).getHWID().equals(client.getHWID()))
			{
				isFound = true;
				counterHwidInfo = i;
				break;
			}

		final HWIDInfoList hInfo = new HWIDInfoList(counterHwidInfo);
		hInfo.setHwids(client.getHWID());
		hInfo.setCount(windowsCount);
		hInfo.setLogin(client.getAccountName());
		hInfo.setPlayerID(client.getPlayerId());
		hInfo.setLockType(lockType);
		_listHWID.put(Integer.valueOf(counterHwidInfo), hInfo);

		if (isFound)
			try
		{
				Connection con = L2DatabaseFactory.getInstance().getConnection();
				Throwable localThrowable3 = null;
				try
				{
					PreparedStatement statement = con.prepareStatement("UPDATE hwid_info SET WindowsCount=?,Account=?,PlayerID=?,LockType=? WHERE HWID=?");
					statement.setInt(1, windowsCount);
					statement.setString(2, client.getPlayerName());
					statement.setInt(3, client.getPlayerId());
					statement.setString(4, lockType.toString());
					statement.setString(5, client.getHWID());
					statement.execute();
				}
				catch (Throwable localThrowable1)
				{
					localThrowable3 = localThrowable1;
					throw localThrowable1;
				}
				finally
				{
					if (con != null)
						if (localThrowable3 != null)
							try
					{
								con.close();
					}
					catch (Throwable x2)
					{
						localThrowable3.addSuppressed(x2);
					}
						else
							con.close();
				}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		else
			try
		{
				Connection con = L2DatabaseFactory.getInstance().getConnection();
				Throwable localThrowable3 = null;
				try
				{
					PreparedStatement statement = con.prepareStatement("INSERT INTO hwid_info (HWID, WindowsCount, Account, PlayerID, LockType) values (?,?,?,?,?)");
					statement.setString(1, client.getHWID());
					statement.setInt(2, windowsCount);
					statement.setString(3, client.getPlayerName());// .getAccountName());
					statement.setInt(4, client.getPlayerId());
					statement.setString(5, lockType.toString());
					statement.execute();
				}
				catch (Throwable localThrowable2)
				{
					localThrowable3 = localThrowable2;
					throw localThrowable2;
				}
				finally
				{
					if (con != null)
						if (localThrowable3 != null)
							try
					{
								con.close();
					}
					catch (Throwable x2)
					{
						localThrowable3.addSuppressed(x2);
					}
						else
							con.close();
				}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void updateHWIDInfo(GameClient client, HWIDInfoList.LockType lockType)
	{
		updateHWIDInfo(client, 1, lockType);
	}
	
	public static int getCountHwidInfo()
	{
		return _listHWID.size();
	}
}