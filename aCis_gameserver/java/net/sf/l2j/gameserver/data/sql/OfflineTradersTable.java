package net.sf.l2j.gameserver.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import net.sf.l2j.commons.logging.CLogger;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.LoginServerThread;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.enums.actors.OperateType;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.craft.ManufactureItem;
import net.sf.l2j.gameserver.model.craft.ManufactureList;
import net.sf.l2j.gameserver.model.trade.TradeItem;
import net.sf.l2j.gameserver.network.GameClient;
import net.sf.l2j.gameserver.network.GameClient.GameClientState;

public final class OfflineTradersTable
{
	private static final CLogger LOGGER = new CLogger(OfflineTradersTable.class.getName());
	
	private static final String SAVE_OFFLINE_STATUS = "INSERT INTO character_offline_trade (charId, time, type, title) VALUES (?, ?, ?, ?)";
	private static final String SAVE_ITEMS = "INSERT INTO character_offline_trade_items (charId, item, count, price) VALUES (?, ?, ?, ?)";
	private static final String CLEAR_OFFLINE_TABLE = "DELETE FROM character_offline_trade";
	private static final String CLEAR_OFFLINE_TABLE_ITEMS = "DELETE FROM character_offline_trade_items";
	private static final String LOAD_OFFLINE_STATUS = "SELECT * FROM character_offline_trade";
	private static final String LOAD_OFFLINE_ITEMS = "SELECT * FROM character_offline_trade_items WHERE charId=?";
	
	public static void storeOffliners()
	{
		try (final Connection con = L2DatabaseFactory.getInstance().getConnection();
			final PreparedStatement save_offline_status = con.prepareStatement(SAVE_OFFLINE_STATUS);
			final PreparedStatement save_items = con.prepareStatement(SAVE_ITEMS))
		{
			try (final Statement stm = con.createStatement())
			{
				stm.execute(CLEAR_OFFLINE_TABLE);
				stm.execute(CLEAR_OFFLINE_TABLE_ITEMS);
			}
			
			for (final Player pc : World.getInstance().getPlayers())
			{
				try
				{
					if (pc.getOperateType() != OperateType.NONE && (pc.getClient() == null || pc.getClient().isDetached()))
					{
						save_offline_status.setInt(1, pc.getObjectId());
						save_offline_status.setLong(2, pc.getOfflineStartTime());
						save_offline_status.setInt(3, pc.getOperateType().getId());
						switch (pc.getOperateType())
						{
							case BUY:
							{
								if (!Config.OFFLINE_TRADE_ENABLE)
									continue;
								
								save_offline_status.setString(4, pc.getBuyList().getTitle());
								
								for (final TradeItem i : pc.getBuyList().getItems())
								{
									save_items.setInt(1, pc.getObjectId());
									save_items.setInt(2, i.getItem().getItemId());
									save_items.setLong(3, i.getCount());
									save_items.setLong(4, i.getPrice());
									save_items.addBatch();
								}
								break;
							}
							case SELL:
							case PACKAGE_SELL:
							{
								if (!Config.OFFLINE_TRADE_ENABLE)
									continue;
								
								save_offline_status.setString(4, pc.getSellList().getTitle());
								for (final TradeItem i : pc.getSellList().getItems())
								{
									save_items.setInt(1, pc.getObjectId());
									save_items.setInt(2, i.getObjectId());
									save_items.setLong(3, i.getCount());
									save_items.setLong(4, i.getPrice());
									save_items.addBatch();
								}
								break;
							}
							case MANUFACTURE:
							{
								if (!Config.OFFLINE_CRAFT_ENABLE)
									continue;
								
								save_offline_status.setString(4, pc.getManufactureList().getStoreName());
								for (final ManufactureItem i : pc.getManufactureList().getList())
								{
									save_items.setInt(1, pc.getObjectId());
									save_items.setInt(2, i.getId());
									save_items.setLong(3, 0);
									save_items.setLong(4, i.getValue());
									save_items.addBatch();
								}
								break;
							}
						}
						save_items.executeBatch();
						save_offline_status.execute();
					}
				}
				catch (final Exception e)
				{
					LOGGER.info("error while saving offline trader " + pc.getObjectId() + ".", e);
				}
			}
			
			LOGGER.info(OfflineTradersTable.class.getSimpleName() + ": offline traders stored.");
		}
		catch (final SQLException e)
		{
			LOGGER.warn("error while saving offline traders.", e);
		}
	}
	
	public static void restoreOfflineTraders()
	{
		try (final Connection con = L2DatabaseFactory.getInstance().getConnection();
			final Statement stm = con.createStatement();
			final ResultSet rs = stm.executeQuery(LOAD_OFFLINE_STATUS))
		{
			int nTraders = 0;
			while (rs.next())
			{
				final long time = rs.getLong("time");
				if (Config.OFFLINE_MAX_DAYS > 0)
				{
					final Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(time);
					cal.add(Calendar.DAY_OF_YEAR, Config.OFFLINE_MAX_DAYS);
					if (cal.getTimeInMillis() <= System.currentTimeMillis())
						continue;
				}
				
				OperateType type = null;
				for (final OperateType t : OperateType.values())
				{
					if (t.getId() == rs.getInt("type"))
					{
						type = t;
						break;
					}
				}
				
				if (type == null)
				{
					LOGGER.warn("PrivateStoreType with id " + rs.getInt("type") + " could not be found.");
					continue;
				}
				
				if (type == OperateType.NONE)
					continue;
				
				final Player player = Player.restore(rs.getInt("charId"));
				if (player == null)
					continue;
				
				try (final PreparedStatement stm_items = con.prepareStatement(LOAD_OFFLINE_ITEMS))
				{
					player.setRunning();
					player.sitDown(true);
					player.setOnlineStatus(true, false);
					
					World.getInstance().addPlayer(player);
					
					final GameClient client = new GameClient(null);
					client.setDetached(true);
					player.setClient(client);
					client.setPlayer(player);
					client.setAccountName(player.getAccountNamePlayer());
					player.setOnlineStatus(true, true);
					client.setState(GameClientState.IN_GAME);
					player.setOfflineStartTime(time);
					player.spawnMe();
					
					LoginServerThread.getInstance().addClient(player.getAccountName(), client);
					
					stm_items.setInt(1, player.getObjectId());
					try (final ResultSet items = stm_items.executeQuery())
					{
						switch (type)
						{
							case BUY:
							{
								while (items.next())
									if (player.getBuyList().addItemByItemId(items.getInt(2), items.getInt(3), items.getInt(4), items.getInt(5)) == null)
										throw new NullPointerException("NPE at BUY of offlineShop " + player.getObjectId() + " " + items.getInt(2) + " " + items.getInt(3) + " " + items.getInt(4) + " " + items.getInt(5));
									
								player.getBuyList().setTitle(rs.getString("title"));
								break;
							}
							case SELL:
							case PACKAGE_SELL:
							{
								while (items.next())
									if (player.getSellList().addItem(items.getInt(2), items.getInt(3), items.getInt(4)) == null)
										throw new NullPointerException("NPE at SELL of offlineShop " + player.getObjectId() + " " + items.getInt(2) + " " + items.getInt(3) + " " + items.getInt(4) + " " + items.getInt(5));
									
								player.getSellList().setTitle(rs.getString("title"));
								player.getSellList().setPackaged(type == OperateType.PACKAGE_SELL);
								break;
							}
							case MANUFACTURE:
							{
								final ManufactureList createList = new ManufactureList();
								createList.setStoreName(rs.getString("title"));
								while (items.next())
									createList.add(new ManufactureItem(items.getInt(2), items.getInt(4)));
								
								break;
							}
						}
					}
					
					player.setOperateType(type);
					player.restoreEffects();
					player.broadcastUserInfo();
					
					nTraders++;
				}
				catch (final Exception e)
				{
					LOGGER.warn("error loading trader " + player.getObjectId() + ".", e);
					player.logout(true);
				}
			}
			
			LOGGER.info("Loaded " + nTraders + " offline traders.");
			
			try (final Statement stm1 = con.createStatement())
			{
				stm1.execute(CLEAR_OFFLINE_TABLE);
				stm1.execute(CLEAR_OFFLINE_TABLE_ITEMS);
			}
		}
		catch (final SQLException e)
		{
			LOGGER.warn("error while loading offline traders.", e);
		}
	}
	
	public static boolean offlineMode(final Player player)
	{
		if (player.isInOlympiadMode() || player.isFestivalParticipant() || player.isInJail() || player.getBoat() != null)
			return false;
		
		if (Config.OFFLINE_MODE_IN_PEACE_ZONE && !player.isInsideZone(ZoneId.PEACE))
			return false;
		
		switch (player.getOperateType())
		{
			case SELL:
			case PACKAGE_SELL:
			case BUY:
				return Config.OFFLINE_TRADE_ENABLE;
			case MANUFACTURE:
				return Config.OFFLINE_CRAFT_ENABLE;
		}
		
		return false;
	}
}