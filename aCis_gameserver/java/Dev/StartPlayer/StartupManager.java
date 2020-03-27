package Dev.StartPlayer;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.stat.Experience;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;
import net.sf.l2j.gameserver.network.serverpackets.HennaInfo;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.taskmanager.ArmorTaskManager;
import net.sf.l2j.gameserver.taskmanager.BuffTaskManager;
import net.sf.l2j.gameserver.taskmanager.PreviewTaskManager;
import net.sf.l2j.gameserver.taskmanager.WeaponTaskManager;

public class StartupManager
{
	public static final StartupManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public static void Welcome(Player player)
	{
		
		final NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
		if (player.getClassId().getId() >= 0)
		{
			html.setFile("data/html/mods/startup/startupsystem.htm");
			player.sendPacket(html);
			player.setIsParalyzed(true);
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					player.sendPacket(new ExShowScreenMessage("Complete your character and get ready for our world!", 10000, 2, true));
					player.getAppearance().isVisible();
				}
			}, 1000 * 2);
		}
	}
	
	public static void doPreview(Player player, int time)
	{
		player.setPreview(true);
		player.setIsParalyzed(true);
		player.getAppearance().isVisible();
		PreviewTaskManager.getInstance().add(player);
		long remainingTime = player.getMemos().getLong("previewEndTime", 0);
		if (remainingTime > 0)
		{
			player.getMemos().set("previewEndTime", remainingTime + TimeUnit.DAYS.toMillis(time));
		}
		else
		{
			player.getMemos().set("previewEndTime", System.currentTimeMillis() + TimeUnit.HOURS.toMillis(time));
			player.broadcastUserInfo();
		}
	}
	
	public static void removePreview(Player player)
	{
		PreviewTaskManager.getInstance().remove(player);
		player.getMemos().set("previewEndTime", 0);
		player.setPreview(false);
		player.broadcastUserInfo();
		
	}
	
	public static void doEquip(Player player, int time)
	{
		player.setEquip(true);
		player.setIsParalyzed(true);
		player.getAppearance().isVisible();
		ArmorTaskManager.getInstance().add(player);
		long remainingTime = player.getMemos().getLong("equipEndTime", 0);
		if (remainingTime > 0)
		{
			player.getMemos().set("equipEndTime", remainingTime + TimeUnit.HOURS.toMillis(time));
		}
		else
		{
			player.getMemos().set("equipEndTime", System.currentTimeMillis() + TimeUnit.HOURS.toMillis(time));
			player.broadcastUserInfo();
		}
	}
	
	public static void removeEquip(Player player)
	{
		ArmorTaskManager.getInstance().remove(player);
		player.getMemos().set("equipEndTime", 0);
		player.setEquip(false);
		player.broadcastUserInfo();
		
	}
	
	public static void doWepEquip(Player player, int time)
	{
		player.setEquip(true);
		player.setIsParalyzed(true);
		player.getAppearance().isVisible();
		WeaponTaskManager.getInstance().add(player);
		long remainingTime = player.getMemos().getLong("weaponEndTime", 0);
		if (remainingTime > 0)
		{
			player.getMemos().set("weaponEndTime", remainingTime + TimeUnit.HOURS.toMillis(time));
		}
		else
		{
			player.getMemos().set("weaponEndTime", System.currentTimeMillis() + TimeUnit.HOURS.toMillis(time));
			player.broadcastUserInfo();
		}
	}
	
	public static void removeWepEquip(Player player)
	{
		WeaponTaskManager.getInstance().remove(player);
		player.getMemos().set("weaponEndTime", 0);
		player.setEquip(false);
		player.broadcastUserInfo();
		
	}
	
	public static void doBuff(Player player, int time)
	{
		player.setBuff(true);
		player.setIsParalyzed(true);
		player.getAppearance().isVisible();
		BuffTaskManager.getInstance().add(player);
		long remainingTime = player.getMemos().getLong("buffEndTime", 0);
		if (remainingTime > 0)
		{
			player.getMemos().set("buffEndTime", remainingTime + TimeUnit.HOURS.toMillis(time));
		}
		else
		{
			player.getMemos().set("buffEndTime", System.currentTimeMillis() + TimeUnit.HOURS.toMillis(time));
			player.broadcastUserInfo();
		}
	}
	
	public static void removeBuff(Player player)
	{
		BuffTaskManager.getInstance().remove(player);
		player.getMemos().set("buffEndTime", 0);
		player.setBuff(false);
		player.broadcastUserInfo();
		
	}
	
	public static void onEnterEquip(Player activeChar)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = activeChar.getMemos().getLong("equipEndTime");
		ClassId classes = activeChar.getClassId();
		
		if (now > endDay)
			StartupManager.removeEquip(activeChar);
		if (activeChar.isMageClass())
		{
			activeChar.setEquip(true);
			activeChar.setIsParalyzed(true);
			activeChar.getAppearance().isVisible();
			NpcHtmlMessage html = new NpcHtmlMessage(1);
			html.setFile("data/html/mods/startup/armors/magearmors.htm");
			activeChar.sendPacket(html);
		}
		else if (classes == ClassId.TREASURE_HUNTER || classes == ClassId.HAWKEYE || classes == ClassId.PLAINS_WALKER || classes == ClassId.SILVER_RANGER || classes == ClassId.ABYSS_WALKER || classes == ClassId.PHANTOM_RANGER)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(1);
			html.setFile("data/html/mods/startup/armors/lightarmors.htm");
			activeChar.sendPacket(html);
		}
		else
		{
			activeChar.setEquip(true);
			NpcHtmlMessage html = new NpcHtmlMessage(1);
			html.setFile("data/html/mods/startup/armors/fighterarmors.htm");
			activeChar.sendPacket(html);
		}
	}
	
	public static void onEnterWepEquip(Player activeChar)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = activeChar.getMemos().getLong("weaponEndTime");
		ClassId classes = activeChar.getClassId();
		
		if (now > endDay)
			StartupManager.removeWepEquip(activeChar);
		else if (activeChar.isMageClass())
		{
			activeChar.setEquip(true);
			activeChar.setIsParalyzed(true);
			activeChar.getAppearance().isVisible();
			NpcHtmlMessage html = new NpcHtmlMessage(1);
			html.setFile("data/html/mods/startup/weapons/mageaweapons.htm");
			activeChar.sendPacket(html);
		}
		else if (classes == ClassId.TREASURE_HUNTER || classes == ClassId.HAWKEYE || classes == ClassId.PLAINS_WALKER || classes == ClassId.SILVER_RANGER || classes == ClassId.ABYSS_WALKER || classes == ClassId.PHANTOM_RANGER)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(1);
			html.setFile("data/html/mods/startup/weapons/lightweapons.htm");
			activeChar.sendPacket(html);
		}
		else
		{
			activeChar.setEquip(true);
			activeChar.setIsParalyzed(true);
			activeChar.getAppearance().isVisible();
			NpcHtmlMessage html = new NpcHtmlMessage(1);
			html.setFile("data/html/mods/startup/weapons/fighterweapons-1.htm");
			activeChar.sendPacket(html);
		}
	}
	
	public static void onEnterPreview(Player activeChar)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = activeChar.getMemos().getLong("previewEndTime");
		
		if (now > endDay)
			StartupManager.removePreview(activeChar);
		else
		{
			activeChar.setPreview(true);
			activeChar.setIsParalyzed(true);
			activeChar.getAppearance().isVisible();
			activeChar.enterNewMode(149918, -112541, -2080);
			StartupManager.HtmlView1(activeChar);
			activeChar.broadcastUserInfo();
		}
	}
	
	public static void onEnterBuff(Player activeChar)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = activeChar.getMemos().getLong("buffEndTime");
		
		if (now > endDay)
			StartupManager.removeBuff(activeChar);
		else
		{
			activeChar.setBuff(true);
			activeChar.setIsParalyzed(true);
			activeChar.getAppearance().isVisible();
			StartupManager.HtmlBuff(activeChar);
			activeChar.broadcastUserInfo();
		}
	}
	
	public static void start(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		switch (player.getClassId().getId())
		{
			case 0:
				html.setFile("data/html/mods/startup/classes/humanclasses.htm");
				player.sendPacket(html);
				break;
			case 10:
				html.setFile("data/html/mods/startup/classes/humanmageclasses.htm");
				player.sendPacket(html);
				break;
			case 18:
				html.setFile("data/html/mods/startup/classes/elfclasses.htm");
				player.sendPacket(html);
				break;
			case 25:
				html.setFile("data/html/mods/startup/classes/elfmageclasses.htm");
				player.sendPacket(html);
				break;
			case 31:
				html.setFile("data/html/mods/startup/classes/darkelfclasses.htm");
				player.sendPacket(html);
				break;
			case 38:
				html.setFile("data/html/mods/startup/classes/darkelfmageclasses.htm");
				player.sendPacket(html);
				break;
			case 44:
				html.setFile("data/html/mods/startup/classes/orcclasses.htm");
				player.sendPacket(html);
				break;
			case 49:
				html.setFile("data/html/mods/startup/classes/orcmageclasses.htm");
				player.sendPacket(html);
				break;
			case 53:
				html.setFile("data/html/mods/startup/classes/dwarfclasses.htm");
				player.sendPacket(html);
				break;
		}
		
	}
	
	public void MageClasses(String command, Player player)
	{
		String params = command.substring(command.indexOf("_") + 1);
		switch (params)
		{
			case "necromancer":
				player.setClassId(13);
				player.setBaseClass(13);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Necromancer!", 4000, 2, true));
				break;
			case "sorceror":
				player.setClassId(12);
				player.setBaseClass(12);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Sorceror!", 4000, 2, true));
				break;
			case "warlock":
				player.setClassId(14);
				player.setBaseClass(14);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Warlock!", 4000, 2, true));
				break;
			case "cleric":
				player.setClassId(15);
				player.setBaseClass(15);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Cleric!", 4000, 2, true));
				break;
			case "bishop":
				player.setClassId(16);
				player.setBaseClass(16);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Bishop!", 4000, 2, true));
				break;
			case "prophet":
				player.setClassId(17);
				player.setBaseClass(17);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Prophet!", 4000, 2, true));
				break;
			case "spellsinger":
				player.setClassId(27);
				player.setBaseClass(27);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Spellsinger!", 4000, 2, true));
				break;
			case "elemental":
				player.setClassId(28);
				player.setBaseClass(28);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Elemental Summoner!", 4000, 2, true));
				break;
			case "elder":
				player.setClassId(30);
				player.setBaseClass(30);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Elven Elder!", 4000, 2, true));
				break;
			case "spellhowler":
				player.setClassId(40);
				player.setBaseClass(40);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Spellhowler!", 4000, 2, true));
				break;
			case "shilliene":
				player.setClassId(43);
				player.setBaseClass(43);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Shillien Elder!", 4000, 2, true));
				break;
			case "overlord":
				player.setClassId(51);
				player.setBaseClass(51);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Overlord!", 4000, 2, true));
				break;
			case "warcryer":
				player.setClassId(52);
				player.setBaseClass(52);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Warcryer!", 4000, 2, true));
				break;
		}
		player.addExpAndSp(Experience.LEVEL[Config.NEWBIE_LVL], 0);
		player.broadcastPacket(new SocialAction(player, 3));
		player.refreshOverloaded();
		player.store();
		player.sendPacket(new HennaInfo(player));
		player.broadcastUserInfo();
		doEquip(player, 90);
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile("data/html/mods/startup/armors/magearmors.htm");
		player.sendPacket(html);
	}
	
	public void FighterClasses(String command, Player player)
	{
		ClassId classes = player.getClassId();
		
		String params = command.substring(command.indexOf("_") + 1);
		switch (params)
		{
			case "gladiator":
				player.setClassId(2);
				player.setBaseClass(2);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Gladiator!", 4000, 2, true));
				break;
			case "warlord":
				player.setClassId(3);
				player.setBaseClass(3);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Warlord!", 4000, 2, true));
				break;
			case "paladin":
				player.setClassId(5);
				player.setBaseClass(5);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Paladin!", 4000, 2, true));
				break;
			case "darkavenger":
				player.setClassId(6);
				player.setBaseClass(6);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Dark Avenger!", 4000, 2, true));
				break;
			case "temple":
				player.setClassId(20);
				player.setBaseClass(20);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Temple Knight!", 4000, 2, true));
				break;
			case "swordsinger":
				player.setClassId(21);
				player.setBaseClass(21);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Swordsinger!", 4000, 2, true));
				break;
			case "shillien":
				player.setClassId(33);
				player.setBaseClass(33);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Shillien Knight!", 4000, 2, true));
				break;
			case "bladedancer":
				player.setClassId(34);
				player.setBaseClass(34);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Bladedancer!", 4000, 2, true));
				break;
			case "phantoms":
				player.setClassId(41);
				player.setBaseClass(41);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Phantom Summoner!", 4000, 2, true));
				break;
			case "destroyer":
				player.setClassId(46);
				player.setBaseClass(46);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Destroyer!", 4000, 2, true));
				break;
			case "tyrant":
				player.setClassId(48);
				player.setBaseClass(48);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Tyrant!", 4000, 2, true));
				break;
			case "bounty":
				player.setClassId(55);
				player.setBaseClass(55);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Bounty Hunter!", 4000, 2, true));
				break;
			case "warsmith":
				player.setClassId(57);
				player.setBaseClass(57);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Warsmith!", 4000, 2, true));
				break;
		}
		player.addExpAndSp(Experience.LEVEL[Config.NEWBIE_LVL], 0);
		player.broadcastPacket(new SocialAction(player, 3));
		player.refreshOverloaded();
		player.store();
		player.sendPacket(new HennaInfo(player));
		player.broadcastUserInfo();
		doEquip(player, 90);
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		if (classes == ClassId.TREASURE_HUNTER || classes == ClassId.HAWKEYE || classes == ClassId.PLAINS_WALKER || classes == ClassId.SILVER_RANGER || classes == ClassId.ABYSS_WALKER || classes == ClassId.PHANTOM_RANGER)
			html.setFile("data/html/mods/startup/armors/lightarmors.htm");
		html.setFile("data/html/mods/startup/armors/fighterarmors.htm");
		player.sendPacket(html);
	}
	
	public void LightClasses(String command, Player player)
	{
		String params = command.substring(command.indexOf("_") + 1);
		switch (params)
		{
			case "treasurehunter":
				player.setClassId(8);
				player.setBaseClass(8);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Treasure Hunter!", 4000, 2, true));
				break;
			case "hawkeye":
				player.setClassId(9);
				player.setBaseClass(9);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Hawkeye!", 4000, 2, true));
				break;
			case "plain":
				player.setClassId(23);
				player.setBaseClass(23);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Plainswalker!", 4000, 2, true));
				break;
			case "silver":
				player.setClassId(24);
				player.setBaseClass(24);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Silver Ranger!", 4000, 2, true));
				break;
			case "abyss":
				player.setClassId(36);
				player.setBaseClass(36);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Abyss Walker!", 4000, 2, true));
				break;
			case "phantom":
				player.setClassId(37);
				player.setBaseClass(37);
				player.sendPacket(new ExShowScreenMessage("Your class has changed to Phantom Ranger!", 4000, 2, true));
				break;
		}
		player.addExpAndSp(Experience.LEVEL[Config.NEWBIE_LVL], 0);
		player.broadcastPacket(new SocialAction(player, 3));
		player.refreshOverloaded();
		player.store();
		player.sendPacket(new HennaInfo(player));
		player.broadcastUserInfo();
		doEquip(player, 90);
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile("data/html/mods/startup/armors/lightarmors.htm");
		player.sendPacket(html);
	}
	
	public void Classes(String command, Player player)
	{
		String params = command.substring(command.indexOf("_") + 1);
		if (params.startsWith("farmzone1"))
		{
			player.enterNewMode(149918, -112541, -2080);
			doPreview(player, 90);
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					StartupManager.HtmlView1(player);
					player.sendMessage("DROP" + " ADENA");
				}
			}, 1000 * 2);
		}
		else if (params.startsWith("farmzone2"))
		{
			player.enterNewMode(181387, -78694, -2732);
			doPreview(player, 90);
			StartupManager.HtmlView2(player);
			ThreadPool.schedule(new Runnable()
			{
				
				@Override
				public void run()
				{
					player.sendMessage("DROP"+ " COINS+ LS, BOG");
				}
			}, 1000 * 2);
		}
		else if (params.startsWith("pvpzone"))
		{
			player.enterNewMode(10468, -24569, -3645);
			doPreview(player, 90);
			StartupManager.HtmlView3(player);
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					player.sendMessage("PVP ZONE"	+ " BE READY FOR PVP!");
				}
			}, 1000 * 2);
		}
		else if (params.startsWith("tlh"))
		{
			removeEquip(player);
			List<Integer> TallumH = Arrays.asList(2382, 547, 5768, 5780, 924, 862, 893, 871, 902);
			ItemInstance items = null;
			for (int id : TallumH)
			{
				if (TallumH.contains(id))
				{
					player.getInventory().addItem("Armors", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					doWepEquip(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile("data/html/mods/startup/weapons/fighterweapons-1.htm");
					player.sendPacket(html);
				}
			}
		}
		else if (params.startsWith("majheavy"))
		{
			List<Integer> MAJH = Arrays.asList(2383, 2419, 5774, 5786, 641, 924, 862, 893, 871, 902);
			ItemInstance items = null;
			for (int id : MAJH)
			{
				if (MAJH.contains(id))
				{
					player.getInventory().addItem("Armors", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeEquip(player);
					doWepEquip(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile("data/html/mods/startup/weapons/fighterweapons-1.htm");
					player.sendPacket(html);
				}
			}
		}
		else if (params.startsWith("mjlight"))
		{
			List<Integer> MJL = Arrays.asList(2395, 2419, 5775, 5787, 924, 862, 893, 871, 902);
			ItemInstance items = null;
			for (int id : MJL)
			{
				if (MJL.contains(id))
				{
					player.getInventory().addItem("Armors", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeEquip(player);
					doWepEquip(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile("data/html/mods/startup/weapons/lightweapons.htm");
					player.sendPacket(html);
				}
			}
		}
		else if (params.startsWith("nightlight"))
		{
			List<Integer> MJL = Arrays.asList(2418, 2394, 5772, 5784, 924, 862, 893, 871, 902);
			ItemInstance items = null;
			for (int id : MJL)
			{
				if (MJL.contains(id))
				{
					player.getInventory().addItem("Armors", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeEquip(player);
					doWepEquip(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile("data/html/mods/startup/weapons/lightweapons.htm");
					player.sendPacket(html);
				}
			}
		}
		else if (params.startsWith("tll"))
		{
			List<Integer> TLL = Arrays.asList(2393, 547, 5769, 5781, 924, 862, 893, 871, 902);
			ItemInstance items = null;
			for (int id : TLL)
			{
				if (TLL.contains(id))
				{
					player.getInventory().addItem("Armors", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeEquip(player);
					doWepEquip(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile("data/html/mods/startup/weapons/fighterweapons-1.htm");
					player.sendPacket(html);
				}
			}
		}
		else if (params.startsWith("dc"))
		{
			List<Integer> MageArmorDC = Arrays.asList(2407, 512, 5767, 5779, 924, 862, 893, 871, 902);
			ItemInstance items = null;
			for (int id : MageArmorDC)
			{
				if (MageArmorDC.contains(id))
				{
					player.getInventory().addItem("Armors", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeEquip(player);
					doWepEquip(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile("data/html/mods/startup/weapons/mageaweapons.htm");
					player.sendPacket(html);
				}
			}
		}
		else if (params.startsWith("tl"))
		{
			List<Integer> MageArmorTL = Arrays.asList(2400, 2405, 547, 5770, 5782, 924, 862, 893, 871, 902);
			ItemInstance items = null;
			for (int id : MageArmorTL)
			{
				if (MageArmorTL.contains(id))
				{
					player.getInventory().addItem("Armors", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeEquip(player);
					doWepEquip(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile("data/html/mods/startup/weapons/mageaweapons.htm");
					player.sendPacket(html);
				}
			}
		}
		else if (params.startsWith("darkheavy"))
		{
			List<Integer> darkheavy = Arrays.asList(365, 388, 512, 5765, 5777, 924, 862, 893, 871, 902);
			ItemInstance items = null;
			for (int id : darkheavy)
			{
				if (darkheavy.contains(id))
				{
					player.getInventory().addItem("Armors", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeEquip(player);
					doWepEquip(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile("data/html/mods/startup/weapons/fighterweapons-1.htm");
					player.sendPacket(html);
				}
			}
		}
		else if (params.startsWith("nmh"))
		{
			List<Integer> NMH = Arrays.asList(374, 2418, 5771, 5783, 2498, 924, 862, 893, 871, 902);
			ItemInstance items = null;
			for (int id : NMH)
			{
				if (NMH.contains(id))
				{
					player.getInventory().addItem("Armors", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeEquip(player);
					doWepEquip(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					NpcHtmlMessage html = new NpcHtmlMessage(1);
					html.setFile("data/html/mods/startup/weapons/fighterweapons-1.htm");
					player.sendPacket(html);
				}
			}
		}
		else if (params.startsWith("som"))
		{
			List<Integer> SOM = Arrays.asList(151);
			ItemInstance items = null;
			for (int id : SOM)
			{
				if (SOM.contains(id))
				{
					player.getInventory().addItem("Weapon", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeWepEquip(player);
					doBuff(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					StartupManager.HtmlBuff(player);
				}
			}
		}
		else if (params.startsWith("bran"))
		{
			List<Integer> bran = Arrays.asList(5607);
			ItemInstance items = null;
			for (int id : bran)
			{
				if (bran.contains(id))
				{
					player.getInventory().addItem("Weapon", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeWepEquip(player);
					doBuff(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					StartupManager.HtmlBuff(player);
				}
			}
		}
		else if (params.startsWith("dread"))
		{
			List<Integer> dread = Arrays.asList(5633);
			ItemInstance items = null;
			for (int id : dread)
			{
				if (dread.contains(id))
				{
					player.getInventory().addItem("Weapon", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeWepEquip(player);
					doBuff(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					StartupManager.HtmlBuff(player);
				}
			}
		}
		else if (params.startsWith("darkl"))
		{
			List<Integer> darkl = Arrays.asList(5648);
			ItemInstance items = null;
			for (int id : darkl)
			{
				if (darkl.contains(id))
				{
					player.getInventory().addItem("Weapon", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeWepEquip(player);
					doBuff(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					StartupManager.HtmlBuff(player);
				}
			}
		}
		else if (params.startsWith("dragon"))
		{
			List<Integer> dra = Arrays.asList(5644);
			ItemInstance items = null;
			for (int id : dra)
			{
				if (dra.contains(id))
				{
					player.getInventory().addItem("Weapon", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeWepEquip(player);
					doBuff(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					StartupManager.HtmlBuff(player);
				}
			}
		}
		else if (params.startsWith("ely"))
		{
			List<Integer> ely = Arrays.asList(5602);
			ItemInstance items = null;
			for (int id : ely)
			{
				if (ely.contains(id))
				{
					player.getInventory().addItem("Weapon", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeWepEquip(player);
					doBuff(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					StartupManager.HtmlBuff(player);
				}
			}
		}
		else if (params.startsWith("carnage"))
		{
			List<Integer> car = Arrays.asList(5609);
			ItemInstance items = null;
			for (int id : car)
			{
				if (car.contains(id))
				{
					player.getInventory().addItem("Weapon", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeWepEquip(player);
					doBuff(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					StartupManager.HtmlBuff(player);
				}
			}
		}
		else if (params.startsWith("soulbow"))
		{
			List<Integer> soul = Arrays.asList(5612);
			ItemInstance items = null;
			for (int id : soul)
			{
				if (soul.contains(id))
				{
					player.getInventory().addItem("Weapon", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeWepEquip(player);
					doBuff(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					StartupManager.HtmlBuff(player);
				}
			}
		}
		else if (params.startsWith("bloody"))
		{
			List<Integer> bloody = Arrays.asList(5614);
			ItemInstance items = null;
			for (int id : bloody)
			{
				if (bloody.contains(id))
				{
					player.getInventory().addItem("Weapon", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeWepEquip(player);
					doBuff(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					StartupManager.HtmlBuff(player);
				}
			}
		}
		else if (params.startsWith("soulsepa"))
		{
			List<Integer> soulsepa = Arrays.asList(5618);
			ItemInstance items = null;
			for (int id : soulsepa)
			{
				if (soulsepa.contains(id))
				{
					player.getInventory().addItem("Weapon", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeWepEquip(player);
					doBuff(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					StartupManager.HtmlBuff(player);
				}
			}
		}
		else if (params.startsWith("damascus"))
		{
			List<Integer> damascus = Arrays.asList(5706);
			ItemInstance items = null;
			for (int id : damascus)
			{
				if (damascus.contains(id))
				{
					player.getInventory().addItem("Weapon", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeWepEquip(player);
					doBuff(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					StartupManager.HtmlBuff(player);
				}
			}
		}
		else if (params.startsWith("grinder"))
		{
			List<Integer> grinder = Arrays.asList(5625);
			ItemInstance items = null;
			for (int id : grinder)
			{
				if (grinder.contains(id))
				{
					player.getInventory().addItem("Weapon", id, 1, player, null);
					items = player.getInventory().getItemByItemId(id);
					player.getInventory().equipItemAndRecord(items);
					player.getInventory().reloadEquippedItems();
					removeWepEquip(player);
					doBuff(player, 90);
					player.broadcastCharInfo();
					new InventoryUpdate();
					StartupManager.HtmlBuff(player);
				}
			}
		}
		else if (params.startsWith("teleport"))
		{
			player.leaveObserverMode();
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					StartupManager.Teleport(player);
				}
			}, 500 * 1);
			
		}
		else if (params.startsWith("welcome"))
			StartupManager.start(player);
		else if (params.startsWith("page1"))
			StartupManager.WeaponsPage1(player);
		else if (params.startsWith("page2"))
			StartupManager.WeaponsPage2(player);


	}
	
	public static void HtmlView(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile("data/html/mods/startup/preview/previewme.htm");
		player.sendPacket(html);
	}
	
	public static void HtmlView1(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile("data/html/mods/startup/preview/previewme-1.htm");
		player.sendPacket(html);
	}
	
	public static void HtmlView2(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile("data/html/mods/startup/preview/previewme-2.htm");
		player.sendPacket(html);
	}
	
	public static void HtmlView3(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile("data/html/mods/startup/teleport.htm");
		player.sendPacket(html);
	}
	
	public static void HtmlBuff(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile("data/html/mods/startup/Observe.htm");
		player.sendPacket(html);
	}
	
	public static void HtmlTeleport(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile("data/html/mods/startup/teleport.htm");
		player.sendPacket(html);
	}
	
	public static void WeaponsPage1(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile("data/html/mods/startup/weapons/fighterweapons-1.htm");
		player.sendPacket(html);
	}
	
	public static void WeaponsPage2(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile("data/html/mods/startup/weapons/fighterweapons-2.htm");
		player.sendPacket(html);
	}
	

	
	public static void Teleport(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		player.teleportTo(Config.TELE_TO_LOCATION[0], Config.TELE_TO_LOCATION[1], Config.TELE_TO_LOCATION[2], 0);
		player.getAppearance().isVisible();
		player.setIsParalyzed(false);
		player.setIsInvul(false);
		player.sendPacket(new ExShowScreenMessage(Config.SERVER_NAME + " "	+  Config.SERVER_TEX, 6000, SMPOS.TOP_CENTER, true));
		removePreview(player);
		removeBuff(player);
		removeEquip(player);
		removeWepEquip(player);
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				html.setFile("data/html/servnews.htm");
				player.sendPacket(html);
			}
		}, 1000 * 2);
	}
	
	private static class SingletonHolder
	{
		protected static final StartupManager _instance = new StartupManager();
	}
}
