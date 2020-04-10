package net.sf.l2j.gameserver.handler.custom;


public class DressMeData
{
	private int chestId,
	helmetId,
	legsId,
	glovesId,
	feetId,
	weapId;
	
	public DressMeData()
	{
		helmetId = 0;
		chestId = 0;
		legsId = 0;
		glovesId = 0;
		feetId = 0;
		weapId = 0;
	}
	
	public int getHelmetId()
	{
		return helmetId;
	}
	
	public int getChestId()
	{
		return chestId;
	}
	
	public int getLegsId()
	{
		return legsId;
	}
	
	public int getGlovesId()
	{
		return glovesId;
	}
	
	public int getBootsId()
	{
		return feetId;
	}
	
	public int getWeapId()
	{
		return weapId;
	}
	
	public void setHelmetId(int val)
	{
		helmetId = val;
	}
	
	public void setChestId(int val)
	{
		chestId = val;
	}
	
	public void setLegsId(int val)
	{
		legsId = val;
	}
	
	public void setGlovesId(int val)
	{
		glovesId = val;
	}
	
	public void setBootsId(int val)
	{
		feetId = val;
	}
	
	public void setWeapId(int val)
	{
		weapId = val;
	}
}