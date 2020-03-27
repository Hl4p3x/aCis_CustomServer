package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.model.actor.Player;

public final class NpcHtmlMessage extends L2GameServerPacket
{
	private final int _objectId;
	
	private String _html;
	private int _itemId = 0;
	private boolean _validate = true;
	
	public NpcHtmlMessage(int objectId)
	{
		_objectId = objectId;
	}
	
	@Override
	public void runImpl()
	{
		if (!_validate)
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		player.clearBypass();
		for (int i = 0; i < _html.length(); i++)
		{
			int start = _html.indexOf("\"bypass ", i);
			int finish = _html.indexOf("\"", start + 1);
			if (start < 0 || finish < 0)
				break;
			
			if (_html.substring(start + 8, start + 10).equals("-h"))
				start += 11;
			else
				start += 8;
			
			i = finish;
			int finish2 = _html.indexOf("$", start);
			if (finish2 < finish && finish2 > 0)
				player.addBypass2(_html.substring(start, finish2).trim());
			else
				player.addBypass(_html.substring(start, finish).trim());
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x0f);
		
		writeD(_objectId);
		writeS(_html);
		writeD(_itemId);
	}
	
	public void disableValidation()
	{
		_validate = false;
	}
	
	public void setItemId(int itemId)
	{
		_itemId = itemId;
	}
	
	public void setHtml(String text)
	{
		if (text.length() > 8192)
		{
			_html = "<html><body>Html was too long.</body></html>";
			LOGGER.warn("An html content was too long.");
			return;
		}
		_html = text;
	}
	
	public void setFile(String filename)
	{
		setHtml(HtmCache.getInstance().getHtmForce(filename));
	}
	
	public void basicReplace(String pattern, String value)
	{
		_html = _html.replaceAll(pattern, value);
	}
	
	public void replace(String pattern, String value)
	{
		_html = _html.replaceAll(pattern, value.replaceAll("\\$", "\\\\\\$"));
	}
	
	public void replace(String pattern, int value)
	{
		_html = _html.replaceAll(pattern, Integer.toString(value));
	}
	
	public void replace(String pattern, long value)
	{
		_html = _html.replaceAll(pattern, Long.toString(value));
	}
	
	public void replace(String pattern, double value)
	{
		_html = _html.replaceAll(pattern, Double.toString(value));
	}
	
	public String getHtml()
	{
		return _html;
	}
}