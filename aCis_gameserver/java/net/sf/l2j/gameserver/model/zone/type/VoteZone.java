/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.model.zone.type;


import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.zone.type.subtype.SpawnZoneType;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;


public class VoteZone extends SpawnZoneType
{
	L2Skill noblesse = SkillTable.getInstance().getInfo(1323, 1);
	
	private boolean _isActive = false;
	private String _name;
	private Location _loc;
	private int _id;
	private String _newzone;
	
	public VoteZone(int id)
	{
		super(id);
	}   
	   
	public void active(boolean give)
	{
		if (give)
		{
		
		}
		_isActive = give;
	}
	
	   @Override
	   public void setParameter(String name, String value)
	   {
	       if (name.equals("name"))
	           _name = value;
	       else if (name.equals("zoneName"))
	    	   _newzone = value;
	       else
	           super.setParameter(name, value);
	   }
	   
	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.CHANGE_PVP_ZONE, true);
		character.setInsideZone(ZoneId.NO_STORE, true);
		if (character instanceof Player)
		{
			final Player player = (Player) character;
			noblesse.getEffects(character, character);
			if (!player.isOnline())
				if (player.getClassId() == ClassId.BISHOP || player.getClassId() == ClassId.CARDINAL || player.getClassId() == ClassId.SHILLIEN_ELDER || player.getClassId() == ClassId.SHILLIEN_SAINT || player.getClassId() == ClassId.EVAS_SAINT || player.getClassId() == ClassId.ELVEN_ELDER || player.getClassId() == ClassId.PROPHET || player.getClassId() == ClassId.HIEROPHANT)
				{
					player.sendPacket(new ExShowScreenMessage("Class Proibida in PvP area..", 6000, 2, true));
					ThreadPool.schedule(new Runnable()
					{
						@Override
						public void run()
						{
							if (player.isOnline() && !player.isInsideZone(ZoneId.PEACE))
								player.teleportTo(83485, 148624, -3402, 50);
						}
					}, 4000L);
				}
			if (player.getMountType() == 2)
			{
				player.sendPacket(SystemMessageId.AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN);
				player.enteredNoLanding(5);
			}
			player.sendPacket(SystemMessageId.ENTERED_COMBAT_ZONE);
			if (!player.isInObserverMode())
			{
				if (player.getPvpFlag() > 0)
					PvpFlagTaskManager.getInstance().remove(player);
				player.updatePvPFlag(1);
			}
			
		}
		
	}

	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.CHANGE_PVP_ZONE, false);
		if (character instanceof Player)
		{
			Player player = (Player) character;
			if (!player.isInObserverMode())
				PvpFlagTaskManager.getInstance().add(player, 20000L);
			player.sendPacket(new EtcStatusUpdate(player));
			player.broadcastUserInfo();
			if (!player.isInsideZone(ZoneId.CHANGE_PVP_ZONE))
				player.sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);
		}
	}
	
	public boolean isActive()
	{
		return _isActive;
	}
	
	public void addLoc(Location loc)
	{
		_loc = loc;
	}
	
	public Location getLoc()
	{
		return _loc;
	}
	
	public void addName(String name)
	{
		_name = name;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getNewZone()
	{
		return _newzone;
	}
	
	
	public int addId(int id)
	{
		return _id = id;
	}
	@Override
	public int getId()
	{
		return _id;
	}
}