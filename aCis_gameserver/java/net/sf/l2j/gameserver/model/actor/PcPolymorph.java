package net.sf.l2j.gameserver.model.actor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.model.CharSelectSlot;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.NpcInfoPolymorph;

/**
 * @author Williams
 *
 */
public class PcPolymorph extends Npc
{
	private CharSelectSlot _polymorphInfo;
	private int _nameColor = 0xFFFFFF;
	private int _titleColor = 0xFFFF77;
	private String _visibleTitle = "";
	
	public PcPolymorph(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setIsInvul(true);
	}
	
	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
	
	public CharSelectSlot getPolymorphInfo()
	{
		return _polymorphInfo;
	}
	
	public void setPolymorphInfo(CharSelectSlot polymorphInfo)
	{
		_polymorphInfo = polymorphInfo;
		
		for (WorldObject object : getKnownType(Player.class))
		{
			if (object instanceof Player)
				sendInfo(object.getActingPlayer());
		}
	}
	
	public int getNameColor()
	{
		return _nameColor;
	}
	
	public void setNameColor(int nameColor)
	{
		_nameColor = nameColor;
	}
	
	public int getTitleColor()
	{
		return _titleColor;
	}
	
	public void setTitleColor(int titleColor)
	{
		_titleColor = titleColor;
	}
	
	public String getVisibleTitle()
	{
		return _visibleTitle;
	}
	
	public void setVisibleTitle(String title)
	{
		_visibleTitle = title == null ? "" : title;
	}
	
	@Override
	public void sendInfo(Player activeChar)
	{
		if (getPolymorphInfo() == null)
		{
			super.sendInfo(activeChar);
			return;
		}
		
		activeChar.sendPacket(new NpcInfoPolymorph(this));
	}

	@Override
	public void showChatWindow(Player player, int val)
	{
		String name = "data/html/polymorph/" + getNpcId() + ".htm";
		if (val != 0)
			name = "data/html/polymorph/" + getNpcId() + "-" + val + ".htm";
		
		broadcastNpcSay("Hello " + player.getName() + " see here the King of Server!");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(name);
		html.replace("%objectId%", getObjectId());
		html.replace("%ownername%", getPolymorphInfo() != null ? getPolymorphInfo().getName() : "");
		player.sendPacket(html);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public static CharSelectSlot loadCharInfo(int objectId)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT char_name, race, base_class, classid, sex, face, hairStyle, hairColor, clanid FROM characters WHERE obj_Id = ?"))
		{
			statement.setInt(1, objectId);
			
			try (ResultSet rs = statement.executeQuery())
			{
				if (rs.next())
				{
					final CharSelectSlot charInfo = new CharSelectSlot(objectId, rs.getString("char_name"));
					charInfo.setRace(rs.getInt("race"));
					charInfo.setBaseClassId(rs.getInt("base_class"));
					charInfo.setClassId(rs.getInt("classid"));
					charInfo.setSex(rs.getInt("sex"));
					charInfo.setFace(rs.getInt("face"));
					charInfo.setHairStyle(rs.getInt("hairStyle"));
					charInfo.setHairColor(rs.getInt("hairColor"));
					charInfo.setClanId(rs.getInt("clanid"));
					
					// Get the augmentation id for equipped weapon
					int weaponObjId = charInfo.getPaperdollObjectId(Inventory.PAPERDOLL_RHAND);
					if (weaponObjId > 0)
					{
						try (PreparedStatement statementAugment = con.prepareStatement("SELECT attributes FROM augmentations WHERE item_id = ?"))
						{
							statementAugment.setInt(1, weaponObjId);
							try (ResultSet rsAugment = statementAugment.executeQuery())
							{
								if (rsAugment.next())
								{
									int augment = rsAugment.getInt("attributes");
									charInfo.setAugmentationId(augment == -1 ? 0 : augment);
								}
							}
						}
					}
					
					return charInfo;
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Could not restore char info: " + e.getMessage(), e);
		}
		
		return null;
	}
}