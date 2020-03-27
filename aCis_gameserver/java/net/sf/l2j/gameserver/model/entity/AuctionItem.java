package net.sf.l2j.gameserver.model.entity;

/**
 * @author Williams
 *
 */
public class AuctionItem
{
	private int _auctionId;
	private int _ownerId;
	private int _itemId;
	private int _count;
	private int _enchant;
	private int _costId;
	private int _costCount;
	
	public AuctionItem(int auctionId, int ownerId, int itemId, int count, int enchant, int costId, int costCount)
	{
		_auctionId = auctionId;
		_ownerId = ownerId;
		_itemId = itemId;
		_count = count;
		_enchant = enchant;
		_costId = costId;
		_costCount = costCount;
	}
	
	public int getAuctionId()
	{
		return _auctionId;
	}
	
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public int getCount()
	{
		return _count;
	}
	
	public int getEnchant()
	{
		return _enchant;
	}
	
	public int getCostId()
	{
		return _costId;
	}
	
	public int getCostCount()
	{
		return _costCount;
	}
}