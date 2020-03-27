package net.sf.l2j.gameserver.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import net.sf.l2j.commons.logging.CLogger;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.model.entity.AuctionItem;

/**
 * @author Williams
 *
 */
public class AuctionTable
{
	private static final CLogger log = new CLogger(AuctionTable.class.getName());
	
	private ArrayList<AuctionItem> _items = new ArrayList<>();
	private int _maxId = 0;
	
	private static final String RESTORE_ITEM = "SELECT * FROM auction_table";
	private static final String ADD_ITEM = "INSERT INTO auction_table VALUES (?,?,?,?,?,?,?)";
	private static final String DELETE_ITEM = "DELETE FROM auction_table WHERE auctionid=?";
	
	public static AuctionTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected AuctionTable()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement stm = con.prepareStatement(RESTORE_ITEM))
		{	
			try (ResultSet rset = stm.executeQuery())
			{
				while (rset.next())
				{
					int auctionId = rset.getInt("auctionid");
					_items.add(new AuctionItem(auctionId, rset.getInt("ownerid"), rset.getInt("itemid"), rset.getInt("count"), rset.getInt("enchant"), rset.getInt("costid"), rset.getInt("costcount")));
					
					if (auctionId > _maxId)
						_maxId = auctionId;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		log.info("AuctionTable: Loaded "+ _items.size() +" items.");
	}
	
	public void addItem(AuctionItem item)
	{
		_items.add(item);
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement stm = con.prepareStatement(ADD_ITEM))
		{
			stm.setInt(1, item.getAuctionId());
			stm.setInt(2, item.getOwnerId());
			stm.setInt(3, item.getItemId());
			stm.setInt(4, item.getCount());
			stm.setInt(5, item.getEnchant());
			stm.setInt(6, item.getCostId());
			stm.setInt(7, item.getCostCount());
			stm.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void deleteItem(AuctionItem item)
	{
		_items.remove(item);
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement stm = con.prepareStatement(DELETE_ITEM))
		{
			stm.setInt(1, item.getAuctionId());
			stm.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public AuctionItem getItem(int auctionId)
	{
		AuctionItem ret = null;
		
		for (AuctionItem item : _items)
		{
			if (item.getAuctionId() == auctionId)
			{
				ret = item;
				break;
			}
		}
		
		return ret;
	}
	
	public ArrayList<AuctionItem> getItems()
	{
		return _items;
	}
	
	public int getNextAuctionId()
	{
		_maxId++;
		return _maxId;
	}
	
	private static class SingletonHolder
	{
		protected static final AuctionTable _instance = new AuctionTable();
	}
}