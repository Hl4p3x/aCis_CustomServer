package net.sf.l2j.gameserver.model.zone.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.gameserver.enums.SpawnType;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.zone.type.subtype.SpawnZoneType;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.taskmanager.ZoneTaskManager;

/**
 * @author Williams
 *
 */
public class MultiZone extends SpawnZoneType
{
	private String _zoneName;
	private boolean _isNoStore;
	private boolean _isNoSummonFriend;
	private static boolean _isFlagEnabled;
	private boolean _isHealEnabled;
	private int _duration;
	private int _reviveDelay;
	private static List<Integer> _restrictedItems = new ArrayList<>();
	
	public MultiZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("name"))
			_zoneName = value;
		else if (name.equals("isNoStore"))
			_isNoStore = Boolean.parseBoolean(value);
		else if (name.equals("isNoSummonFriend"))
			_isNoSummonFriend = Boolean.parseBoolean(value);
		else if (name.equals("isFlagEnabled"))
			_isFlagEnabled = Boolean.parseBoolean(value);
		else if (name.equals("isHealEnabled"))
			_isHealEnabled = Boolean.parseBoolean(value);
		else if (name.equals("duration"))
			_duration = Integer.parseInt(value);
		else if (name.equals("reviveDelay"))
			_reviveDelay = Integer.parseInt(value);
		else if (name.equals("restrictedItems"))
			_restrictedItems = parseIntegers(value);
		else
			super.setParameter(name, value);
	}

	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.MULTI, true);
		
		if (_isNoStore)
			character.setInsideZone(ZoneId.NO_STORE, true);
		
		if (_isNoSummonFriend)
			character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
		
		if (character instanceof Player || character instanceof Pet)
		{
			final Player player = (Player) character;
			player.sendPacket(new ExShowScreenMessage("You have entered a multi zone.", 5000));
			
			if (_isFlagEnabled)
				player.updatePvPFlag(1);
			
			checkItemRestriction(player);
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.MULTI, false);
		
		if (_isNoStore)
			character.setInsideZone(ZoneId.NO_STORE, false);
		
		if (_isNoSummonFriend)
			character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
		
		if (character instanceof Player)
		{
			final Player player = (Player) character;
			player.sendPacket(new ExShowScreenMessage("You have left a multi zone.", 5000));
			
			if (_isFlagEnabled)
				player.updatePvPFlag(0);
		}
	}
	
	public void onDie(Creature character)
	{
		if (character instanceof Player && _reviveDelay > 0)
		{
			ThreadPool.schedule(() -> respawnCharacter(((Player) character)), _reviveDelay * 1000);
			character.sendPacket(new ExShowScreenMessage("You will be revived in " + _reviveDelay + " second(s).", 5000));
		}
	}

	private final void respawnCharacter(Player player)
	{
		if (player == null || !player.isDead())
			return;
		
		player.doRevive();
		player.teleportTo(getRndSpawn(SpawnType.NORMAL), 0);
	}
	
	public void onRevive(Creature character)
	{
		if (character instanceof Player && _isHealEnabled)
		{
			final Summon pet = character.getSummon();
			if (pet != null)
				pet.setCurrentHpMp(pet.getMaxHp(), pet.getMaxMp());
			
			character.setCurrentCp(character.getMaxCp());
			character.setCurrentHpMp(character.getMaxHp(), character.getMaxMp());
		}
	}
	
	private final static void checkItemRestriction(Player player)
	{
		for (ItemInstance item : player.getInventory().getPaperdollItems())
		{
			if (item == null || !isRestrictedItem(item.getItemId()))
				continue;
			
			player.getInventory().unEquipItemInSlot(item.getLocationSlot());
			InventoryUpdate iu = new InventoryUpdate();
			iu.addModifiedItem(item);
			player.sendPacket(iu);
		}
	}

	public List<Integer> parseIntegers(String line)
	{
		final List<Integer> value = new ArrayList<>();
		Arrays.asList(line.split(";")).forEach(id -> value.add(Integer.parseInt(id)));
		
		return value;
	}
	
	public boolean isActive()
	{
		return ZoneTaskManager.getInstance().getCurrentZoneId() == getId();
	}
	
	public String getZoneName()
	{
		return _zoneName;
	}
	
	public int getTime()
	{
		return _duration;
	}
	
	public int getRevive()
	{
		return _reviveDelay;
	}
	
	public static boolean isFlagEnabled()
	{
		return _isFlagEnabled;
	}
	
	public static boolean isRestrictedItem(int itemId)
	{
		return _restrictedItems.contains(itemId);
	}
}