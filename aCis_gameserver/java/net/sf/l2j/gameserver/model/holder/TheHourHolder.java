package net.sf.l2j.gameserver.model.holder;

public class TheHourHolder
{
	private String _name;
	private int _kills;
	private int _objId;
	
	public TheHourHolder(String name, int kills, int objId)
	{
		_name = name;
		_kills = kills;
		_objId = objId;
	}
	
	public void setPvpKills()
	{
		_kills++;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getObj()
	{
		return _objId;
	}
	
	public int getKills()
	{
		return _kills;
	}
}