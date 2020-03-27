package Dev.Phantom.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.sql.PlayerInfoTable;
import net.sf.l2j.gameserver.data.xml.PlayerData;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.enums.actors.Sex;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.container.player.Appearance;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.actor.stat.Experience;
import net.sf.l2j.gameserver.model.actor.template.PlayerTemplate;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

import Dev.Phantom.FakePlayer;
import Dev.Phantom.FakePlayerNameManager;
import Dev.Phantom.FakePlayerTitleManager;
import Dev.Phantom.Ai.ArchmageAI;
import Dev.Phantom.Ai.DominatorAI;
import Dev.Phantom.Ai.DuelistAI;
import Dev.Phantom.Ai.FakePlayerAI;
import Dev.Phantom.Ai.FallbackAI;
import Dev.Phantom.Ai.HellKnightAI;
import Dev.Phantom.Ai.MysticMuseAI;
import Dev.Phantom.Ai.SoultakerAI;
import Dev.Phantom.Ai.StormScreamerAI;
import Dev.Phantom.Ai.TitanAI;
import Dev.Phantom.Ai.WindRiderAI;


public class FakeHelpers {


	public static Class<? extends Creature> getTestTargetClass() {
		return Monster.class;
	}
	public static Class<? extends Player> getFlagTargetClass() {
		return Player.class;
	}

	public static int getTestTargetRange() {
		return 3000;
	}
	
	public static FakePlayer createRandomFakePlayer() {
		int objectId = IdFactory.getInstance().getNextId();
		String accountName = "AutoPilot";
		String playerName = FakePlayerNameManager.INSTANCE.getRandomAvailableName();
		String playerTitle = FakePlayerTitleManager.INSTANCE.getRandomAvailableTitle();
		ClassId classId = getThirdClasses().get(Rnd.get(0, getThirdClasses().size() - 1));
		final PlayerTemplate template = PlayerData.getInstance().getTemplate(classId);
		Appearance app = getRandomAppearance(template.getClass());

		FakePlayer player = new FakePlayer(objectId, template, accountName, app);
		player.setTitle(playerTitle);
		player.setName(playerName);
		player.setAccessLevel(Config.DEFAULT_ACCESS_LEVEL);
		PlayerInfoTable.getInstance().addPlayer(objectId, accountName, playerName, player.getAccessLevel().getLevel());
		player.setBaseClass(player.getClassId());
		setLevel(player, 81);
		player.rewardSkills();
		giveArmorsByClass(player,true);
		giveWeaponsByClass(player,true);
		player.heal();
		
        player.getAppearance().setNameColor(Integer.decode("0x" + Config.NAME_COLOR));
        player.getAppearance().setTitleColor(Integer.decode("0x" + Config.TITLE_COLOR));
        
        player.getAppearance().setNameColor(Integer.decode("0x" + FakeHelpers.getNameColor()));
        player.getAppearance().setTitleColor(Integer.decode("0x" + FakeHelpers.getTitleColor()));
        
		
		if (Rnd.get(100) < Config.PHANTOM_CHANCE_MALARIA)
		{
			L2Skill skill = SkillTable.getInstance().getInfo(4554, 4);
			skill.getEffects(player, player);
		}

        if (Rnd.get(100) < Config.PHANTOM_CHANCE_HERO) {
            player.setHero(true);
            player.isHero();
            player.broadcastUserInfo();
        }
        player.broadcastTitleInfo();
		return player;
	}
	
	
			public static void giveArmorsByClass(FakePlayer player, boolean randomlyEnchant) {
				List<Integer> itemIds = new ArrayList<>();
				switch (player.getClassId())
				{
					
					case MYSTIC_MUSE:
					{
						itemIds = Config.ARMOR_MYSTIC_MUSE;
						break;
					}
			
					case HIEROPHANT:
					{
						itemIds = Config.ARMOR_HIEROPHANT;
						break;
					}
			
					case ARCANA_LORD:
					{
					itemIds = Config.ARMOR_ARCANA_LORD;
					break;
					}
			
			case CARDINAL:
			{
			itemIds = Config.ARMOR_CARDINAL;
			break;
			}
			
			case ELEMENTAL_MASTER:
			{
			itemIds = Config.ARMOR_ELEMENTAL_MASTER;
			break;
			}
			case DOOMCRYER:
			{
			itemIds = Config.ARMOR_DOOMCRYER;
			break;
			}
			
			case DOMINATOR:
			{
			itemIds = Config.ARMOR_DOMINATOR;
			break;
			}
			
			case GHOST_SENTINEL:
			{
			itemIds = Config.ARMOR_GHOST_SENTINEL;
			break;
			}
			
			case ADVENTURER:
			{
			itemIds = Config.ARMOR_ADVENTURER;
			break;
			}
			
			case SOULTAKER:
			{
			itemIds = Config.ARMOR_SOULTAKER;
			break;
			}	
			
			case ARCHMAGE:
			{
			itemIds = Config.ARMOR_ARCHMAGE;
			break;
			}
			case DUELIST:
			{
			itemIds = Config.ARMOR_DUELIST;
			break;
			}
			
			case MAESTRO:
			{
			itemIds = Config.ARMOR_MAESTRO;
			break;
			}
			
			case HELL_KNIGHT:
			{
			itemIds = Config.ARMOR_HELL_KNIGHT;
			break;
			}
			
			case TITAN:
			{
			itemIds = Config.ARMOR_TITAN;
			break;
			}
			
			case SAGGITARIUS:
			{
			itemIds = Config.ARMOR_SAGGITARIUS;
			break;
			}
			
			case WIND_RIDER:
			{
			itemIds = Config.ARMOR_WIND_RIDER;
			break;
			}
			
			case GRAND_KHAVATARI:
			{
			itemIds = Config.ARMOR_GRAND_KHAVATARI;
			break;
			}
			
			case STORM_SCREAMER:
			{
			itemIds = Config.ARMOR_STORM_SCREAMER;
			break;
			}
			
			case EVAS_SAINT:
			{
			itemIds = Config.ARMOR_EVAS_SAINT;
			break;
			}
			
			case SPECTRAL_MASTER:
			{
			itemIds = Config.ARMOR_SPECTRAL_MASTER;
			break;
			}
			case SHILLIEN_SAINT:
			{
			itemIds = Config.ARMOR_SHILLIEN_SAINT;
			break;
			}
			
			case DREADNOUGHT:
			{
			itemIds = Config.ARMOR_DREADNOUGHT;
			break;
			}
			case PHOENIX_KNIGHT:
			{
			itemIds = Config.ARMOR_PHOENIX_KNIGHT;
			break;
			}
			case SWORD_MUSE:
			{
			itemIds = Config.ARMOR_SWORD_MUSE;
			break;
			}
			case SPECTRAL_DANCER:
			{
			itemIds = Config.ARMOR_SPECTRAL_DANCER;
			break;
			}
			case EVAS_TEMPLAR:
			{
			itemIds = Config.ARMOR_EVAS_TEMPLAR;
			break;
			}
			case SHILLIEN_TEMPLAR:
			{
			itemIds = Config.ARMOR_SHILLIEN_TEMPLAR;
			break;
			}
				
			case MOONLIGHT_SENTINEL:
			{
			itemIds = Config.ARMOR_MOONLIGHT;
			break;
			}
			case GHOST_HUNTER:
			{
			itemIds = Config.ARMOR_GHOST_HUNTER;
			break;
			}
			case FORTUNE_SEEKER:
			{
			itemIds = Config.ARMOR_FORTUNE;
			break;
			}
		}
		
		for (int id : itemIds) {
			player.getInventory().addItem("Armors", id, 1, player, null);
			ItemInstance item = player.getInventory().getItemByItemId(id);
			item.setEnchantLevel(Rnd.get(Config.MIN_ENCHANT_FAKE_ARMOR, Config.MAX_ENCHANT_FAKE_ARMOR));
			player.getInventory().equipItemAndRecord(item);
			player.getInventory().reloadEquippedItems();
			player.broadcastCharInfo();
		}
	}

	public static void giveWeaponsByClass(FakePlayer player, boolean randomlyEnchant) {
		List<Integer> itemIds = new ArrayList<>();
		switch (player.getClassId()) {
			
		case FORTUNE_SEEKER:
		{
		itemIds = Config.WEAPON_FORTUNE_SEEKER;
		break;
		}
		
		case GHOST_HUNTER:
		{
		itemIds = Config.WEAPON_GHOST_HUNTER;
		break;
		}
		
		case WIND_RIDER:
		{
		itemIds = Config.WEAPON_WIND_RIDER;
		break;
		}
		
		case ADVENTURER:
		{
		itemIds = Config.WEAPON_ADVENTURER;
		break;
		}
		
		case SAGGITARIUS:
		{
		itemIds = Config.WEAPON_SAGGITARIUS;
		break;
		}
		
		case MOONLIGHT_SENTINEL:
		{
		itemIds = Config.WEAPON_MOONLIGHT_SENTINEL;
		break;
		}
		
		case GHOST_SENTINEL:
		{
		itemIds = Config.WEAPON_GHOST_SENTINEL;
		break;
		}
		
		case PHOENIX_KNIGHT:
		{
		itemIds = Config.WEAPON_PHOENIX_KNIGHT;
		break;
		}
		
		case SWORD_MUSE:
		{
		itemIds = Config.WEAPON_SWORD_MUSE;
		break;
		}
		
		case HELL_KNIGHT:
		{
		itemIds = Config.WEAPON_HELL_KNIGHT;
		break;
		}
		
		case EVAS_TEMPLAR:
		{
		itemIds = Config.WEAPON_EVAS_TEMPLAR;
		break;
		}
		
		case SHILLIEN_TEMPLAR:
		{
		itemIds = Config.WEAPON_SHILLIEN_TEMPLAR;
		break;
		}
		
		case MAESTRO:
		{
		itemIds = Config.WEAPON_MAESTRO;
		break;
		}
		
		case TITAN:
		{
		itemIds = Config.WEAPON_TITAN;
		break;
		}
		
		case DUELIST:
		{
		itemIds = Config.WEAPON_DUELIST;
		break;
		}
		
		case SPECTRAL_DANCER:
		{
		itemIds = Config.WEAPON_SPECTRAL_DANCER;
		break;
		}
		
		case DREADNOUGHT:
		{
		itemIds = Config.WEAPON_DREADNOUGHT;
		break;
		}

		case ARCHMAGE:
		{
		itemIds = Config.WEAPON_ARCHMAGE;
		break;
		}
		
		case SOULTAKER:
		{
		itemIds = Config.WEAPON_SOULTAKER;
		break;
		}
		
		case HIEROPHANT:
		{
		itemIds = Config.WEAPON_HIEROPHANT;
		break;
		}
		
		case ARCANA_LORD:
		{
		itemIds = Config.WEAPON_ARCANA_LORD;
		break;
		}
		
		case CARDINAL:
		{
		itemIds = Config.WEAPON_CARDINAL;
		break;
		}
		
		case MYSTIC_MUSE:
		{
		itemIds = Config.WEAPON_MYSTIC_MUSE;
		break;
		}
		
		case ELEMENTAL_MASTER:
		{
		itemIds = Config.WEAPON_ELEMENTAL_MASTER;
		break;
		}
		
		case EVAS_SAINT:
		{
		itemIds = Config.WEAPON_EVAS_SAINT;
		break;
		}
		
		case STORM_SCREAMER:
		{
		itemIds = Config.WEAPON_STORM_SCREAMER;
		break;
		}
		
		case SPECTRAL_MASTER:
		{
		itemIds = Config.WEAPON_SPECTRAL_MASTER;
		break;
		}
		
		case SHILLIEN_SAINT:
		{
		itemIds = Config.WEAPON_SHILLIEN_SAINT;
		break;
		}
		
		case DOMINATOR:
		{
		itemIds = Config.WEAPON_DOMINATOR;
		break;
		}
		
		case DOOMCRYER:
		{
		itemIds = Config.WEAPON_DOOMCRYER;
		break;
		}

		case GRAND_KHAVATARI:
		{
		itemIds = Config.WEAPON_GRAND_KHAVATARI;
		break;
		}
		
		}
		for (int id : itemIds) {
			player.getInventory().addItem("Weapon", id, 1, player, null);
			ItemInstance item = player.getInventory().getItemByItemId(id);
			item.setEnchantLevel(Rnd.get(Config.MIN_ENCHANT_FAKE_WEAPON, Config.MAX_ENCHANT_FAKE_WEAPON));
			player.getInventory().equipItemAndRecord(item);
			player.getInventory().reloadEquippedItems();
			
		}
	}

	public static List<ClassId> getThirdClasses() {
		// removed summoner classes because fuck those guys
		List<ClassId> classes = new ArrayList<>();

		/*
		 * classes.add(ClassId.EVAS_SAINT); classes.add(ClassId.SHILLIEN_TEMPLAR);
		 * classes.add(ClassId.SPECTRAL_DANCER); classes.add(ClassId.GHOST_HUNTER);
		 * 
		 * classes.add(ClassId.PHOENIX_KNIGHT);

		 * 
		 * classes.add(ClassId.HIEROPHANT); classes.add(ClassId.EVAS_TEMPLAR);
		 * classes.add(ClassId.SWORD_MUSE);
		 * 
		 * classes.add(ClassId.DOOMCRYER); classes.add(ClassId.FORTUNE_SEEKER);
		 * classes.add(ClassId.MAESTRO);
		 */

		// classes.add(ClassId.ARCANA_LORD);
		// classes.add(ClassId.ELEMENTAL_MASTER);
		// classes.add(ClassId.SPECTRAL_MASTER);
		// classes.add(ClassId.SHILLIEN_SAINT);
		
		classes.add(ClassId.HELL_KNIGHT);
		//classes.add(ClassId.SAGGITARIUS);
		classes.add(ClassId.ARCHMAGE);
		classes.add(ClassId.SOULTAKER);
		classes.add(ClassId.MYSTIC_MUSE);
		classes.add(ClassId.STORM_SCREAMER);
		//classes.add(ClassId.MOONLIGHT_SENTINEL);
		//classes.add(ClassId.GHOST_SENTINEL);
		//classes.add(ClassId.ADVENTURER);
		classes.add(ClassId.WIND_RIDER);
		classes.add(ClassId.DOMINATOR);
		classes.add(ClassId.TITAN);
		//classes.add(ClassId.CARDINAL);
		classes.add(ClassId.DUELIST);
		//classes.add(ClassId.GRAND_KHAVATARI);
		//classes.add(ClassId.DREADNOUGHT);
		
		return classes;
	}

	public static Map<ClassId, Class<? extends FakePlayerAI>> getAllAIs() {
		Map<ClassId, Class<? extends FakePlayerAI>> ais = new HashMap<>();
		ais.put(ClassId.STORM_SCREAMER, StormScreamerAI.class);
		ais.put(ClassId.MYSTIC_MUSE, MysticMuseAI.class);
		ais.put(ClassId.ARCHMAGE, ArchmageAI.class);
		ais.put(ClassId.SOULTAKER, SoultakerAI.class);
		//ais.put(ClassId.SAGGITARIUS, SaggitariusAI.class);
		//ais.put(ClassId.MOONLIGHT_SENTINEL, MoonlightSentinelAI.class);
		//ais.put(ClassId.GHOST_SENTINEL, GhostSentinelAI.class);
		//ais.put(ClassId.ADVENTURER, AdventurerAI.class);
		ais.put(ClassId.WIND_RIDER, WindRiderAI.class);
		//ais.put(ClassId.GHOST_HUNTER, GhostHunterAI.class);
		ais.put(ClassId.HELL_KNIGHT, HellKnightAI.class);
		ais.put(ClassId.DOMINATOR, DominatorAI.class);
		ais.put(ClassId.TITAN, TitanAI.class);
		//ais.put(ClassId.CARDINAL, CardinalAI.class);
		ais.put(ClassId.DUELIST, DuelistAI.class);
		//ais.put(ClassId.GRAND_KHAVATARI, GrandKhavatariAI.class);
		//ais.put(ClassId.DREADNOUGHT, DreadnoughtAI.class);
		return ais;
	}

	public static Appearance getRandomAppearance(Class<? extends PlayerTemplate> class1) {

		Sex randomSex = Rnd.get(1, 2) == 1 ? Sex.MALE : Sex.FEMALE;
		int hairStyle = Rnd.get(0, randomSex == Sex.MALE ? 2 : 6);
		int hairColor = Rnd.get(0, 3);
		int faceId = Rnd.get(0, 2);

		return new Appearance((byte) faceId, (byte) hairColor, (byte) hairStyle, randomSex);
	}

	public static void setLevel(FakePlayer player, int level) {
		if (level >= 1 && level <= Experience.MAX_LEVEL) {
			long pXp = player.getExp();
			long tXp = Experience.LEVEL[81];

			if (pXp > tXp)
				player.removeExpAndSp(pXp - tXp, 0);
			else if (pXp < tXp)
				player.addExpAndSp(tXp - pXp, 0);
		}
	}

	public static Class<? extends FakePlayerAI> getAIbyClassId(ClassId classId) {
		Class<? extends FakePlayerAI> ai = getAllAIs().get(classId);
		if (ai == null)
			return FallbackAI.class;

		return ai;
	}
	
    static String getNameColor() {
        return Config.PHANTOM_PLAYERS_NAME_CLOLORS.get(Rnd.get(Config.PHANTOM_PLAYERS_NAME_CLOLORS.size()));
    }
    
    static String getTitleColor() {
        return Config.PHANTOM_PLAYERS_TITLE_CLOLORS.get(Rnd.get(Config.PHANTOM_PLAYERS_TITLE_CLOLORS.size()));
    }
	
}
