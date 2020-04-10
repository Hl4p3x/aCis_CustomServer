package net.sf.l2j.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.logging.LogManager;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.mmocore.SelectorConfig;
import net.sf.l2j.commons.mmocore.SelectorThread;
import net.sf.l2j.commons.util.SysUtil;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.communitybbs.Manager.ForumsBBSManager;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.cache.CrestCache;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.manager.BoatManager;
import net.sf.l2j.gameserver.data.manager.BufferManager;
import net.sf.l2j.gameserver.data.manager.BuyListManager;
import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.data.manager.CastleManorManager;
import net.sf.l2j.gameserver.data.manager.CharacterKillingManager;
import net.sf.l2j.gameserver.data.manager.ClanHallManager;
import net.sf.l2j.gameserver.data.manager.CoupleManager;
import net.sf.l2j.gameserver.data.manager.CursedWeaponManager;
import net.sf.l2j.gameserver.data.manager.DayNightManager;
import net.sf.l2j.gameserver.data.manager.DerbyTrackManager;
import net.sf.l2j.gameserver.data.manager.DimensionalRiftManager;
import net.sf.l2j.gameserver.data.manager.FestivalOfDarknessManager;
import net.sf.l2j.gameserver.data.manager.FishingChampionshipManager;
import net.sf.l2j.gameserver.data.manager.FourSepulchersManager;
import net.sf.l2j.gameserver.data.manager.GrandBossManager;
import net.sf.l2j.gameserver.data.manager.HeroManager;
import net.sf.l2j.gameserver.data.manager.LotteryManager;
import net.sf.l2j.gameserver.data.manager.MovieMakerManager;
import net.sf.l2j.gameserver.data.manager.PartyMatchRoomManager;
import net.sf.l2j.gameserver.data.manager.PetitionManager;
import net.sf.l2j.gameserver.data.manager.RaidBossInfoManager;
import net.sf.l2j.gameserver.data.manager.RaidBossManager;
import net.sf.l2j.gameserver.data.manager.RaidPointManager;
import net.sf.l2j.gameserver.data.manager.SevenSignsManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.data.sql.AuctionTable;
import net.sf.l2j.gameserver.data.sql.AutoSpawnTable;
import net.sf.l2j.gameserver.data.sql.BookmarkTable;
import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.data.sql.OfflineTradersTable;
import net.sf.l2j.gameserver.data.sql.PlayerInfoTable;
import net.sf.l2j.gameserver.data.sql.ServerMemoTable;
import net.sf.l2j.gameserver.data.sql.SpawnTable;
import net.sf.l2j.gameserver.data.xml.AdminData;
import net.sf.l2j.gameserver.data.xml.AnnouncementData;
import net.sf.l2j.gameserver.data.xml.AntiBotData;
import net.sf.l2j.gameserver.data.xml.ArmorSetData;
import net.sf.l2j.gameserver.data.xml.AugmentationData;
import net.sf.l2j.gameserver.data.xml.DonateData;
import net.sf.l2j.gameserver.data.xml.DoorData;
import net.sf.l2j.gameserver.data.xml.EnchantData;
import net.sf.l2j.gameserver.data.xml.FishData;
import net.sf.l2j.gameserver.data.xml.HennaData;
import net.sf.l2j.gameserver.data.xml.HerbDropData;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.data.xml.MapRegionData;
import net.sf.l2j.gameserver.data.xml.MultisellData;
import net.sf.l2j.gameserver.data.xml.NewbieBuffData;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.data.xml.PlayerData;
import net.sf.l2j.gameserver.data.xml.PlayerLevelData;
import net.sf.l2j.gameserver.data.xml.PolymorphData;
import net.sf.l2j.gameserver.data.xml.PvPData;
import net.sf.l2j.gameserver.data.xml.RecipeData;
import net.sf.l2j.gameserver.data.xml.ScriptData;
import net.sf.l2j.gameserver.data.xml.SkillTreeData;
import net.sf.l2j.gameserver.data.xml.SoulCrystalData;
import net.sf.l2j.gameserver.data.xml.SpellbookData;
import net.sf.l2j.gameserver.data.xml.StaticObjectData;
import net.sf.l2j.gameserver.data.xml.SummonItemData;
import net.sf.l2j.gameserver.data.xml.TeleportLocationData;
import net.sf.l2j.gameserver.data.xml.WalkerRouteData;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.gameserver.handler.ChatHandler;
import net.sf.l2j.gameserver.handler.ItemHandler;
import net.sf.l2j.gameserver.handler.SkillHandler;
import net.sf.l2j.gameserver.handler.UserCommandHandler;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.boat.BoatGiranTalking;
import net.sf.l2j.gameserver.model.boat.BoatGludinRune;
import net.sf.l2j.gameserver.model.boat.BoatInnadrilTour;
import net.sf.l2j.gameserver.model.boat.BoatRunePrimeval;
import net.sf.l2j.gameserver.model.boat.BoatTalkingGludin;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.olympiad.OlympiadGameManager;
import net.sf.l2j.gameserver.network.GameClient;
import net.sf.l2j.gameserver.network.GamePacketHandler;
import net.sf.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import net.sf.l2j.gameserver.taskmanager.DecayTaskManager;
import net.sf.l2j.gameserver.taskmanager.DoubleRatesTaskManager;
import net.sf.l2j.gameserver.taskmanager.GameTimeTaskManager;
import net.sf.l2j.gameserver.taskmanager.ItemsOnGroundTaskManager;
import net.sf.l2j.gameserver.taskmanager.PcCafeTaskManager;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;
import net.sf.l2j.gameserver.taskmanager.RandomAnimationTaskManager;
import net.sf.l2j.gameserver.taskmanager.ShadowItemTaskManager;
import net.sf.l2j.gameserver.taskmanager.WaterTaskManager;
import net.sf.l2j.util.DeadLockDetector;
import net.sf.l2j.util.IPv4Filter;

import Dev.AutoBackup.BackupDBSave;
import Dev.EventMoster.ChristmasEvent;
import Dev.FakePlayer.PhantomStore;
import Dev.FakePlayer.PhantomWalker;
import Dev.PartyFarm.InitialPartyFarm;
import Dev.PartyFarm.PartyFarm;
import Dev.Phantom.FakePlayerManager;
import Dev.StartPlayer.StartupManager;
import Dev.TeamVsTeam.TvTEventManager;
import Dev.Tournament.Arena2x2;
import Dev.Tournament.Arena4x4;
import Dev.Tournament.Arena9x9;
import Dev.Tournament.ArenaEvent;
import Dev.Tournament.ArenaTask;
import Dev.VoteEngine.VoteSystem;
import Dev.VoteGatekkeper.PvPZoneManager;
import Dev.announcerTopPlayer.AnnounceOnlinePlayers;
import hwid.Hwid;




public class GameServer
{
	private static final CLogger LOGGER = new CLogger(GameServer.class.getName());
	
	private final SelectorThread<GameClient> _selectorThread;
	
	final long serverLoadStart = System.currentTimeMillis();
	
	private static GameServer _gameServer;
	
	public static void main(String[] args) throws Exception
	{
		_gameServer = new GameServer();
	}
	
	public GameServer() throws Exception
	{
		// Create log folder
		new File("./log").mkdir();
		new File("./log/report").mkdirs();
		new File("./log/chat").mkdir();
		new File("./log/donate").mkdir();
		new File("./log/console").mkdir();
		new File("./log/error").mkdir();
		new File("./log/gmaudit").mkdir();
		new File("./log/item").mkdir();
		new File("./data/crests").mkdirs();
		
		// Create input stream for log file -- or store file data into memory
		try (InputStream is = new FileInputStream(new File("config/logging.properties")))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		
		// Initialize config
		Config.loadGameServer();
		
		// Factories
		L2DatabaseFactory.getInstance();
		ThreadPool.init();
		
		StringUtil.printSection("IdFactory");
		IdFactory.getInstance();
		
		StringUtil.printSection("World");
		World.getInstance();
		MapRegionData.getInstance();
		AnnouncementData.getInstance();
		ServerMemoTable.getInstance();
		AntiBotData.getInstance();

		
		StringUtil.printSection("Skills");
		SkillTable.getInstance();
		SkillTreeData.getInstance();
		
		StringUtil.printSection("Items");
		ItemData.getInstance();
		EnchantData.getInstance();
		SummonItemData.getInstance();
		HennaData.getInstance();
		BuyListManager.getInstance();
		MultisellData.getInstance();
		RecipeData.getInstance();
		ArmorSetData.getInstance();
		FishData.getInstance();
		SpellbookData.getInstance();
		SoulCrystalData.getInstance();
		AugmentationData.getInstance();
		CursedWeaponManager.getInstance();
		
		StringUtil.printSection("Admins");
		AdminData.getInstance();
		BookmarkTable.getInstance();
		MovieMakerManager.getInstance();
		PetitionManager.getInstance();
		
		StringUtil.printSection("Characters");
		PlayerData.getInstance();
		PlayerInfoTable.getInstance();
		PlayerLevelData.getInstance();
		NewbieBuffData.getInstance();
		TeleportLocationData.getInstance();
		HtmCache.getInstance();
		PartyMatchRoomManager.getInstance();
		RaidPointManager.getInstance();
		PvPData.getInstance();
		
		if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
			OfflineTradersTable.restoreOfflineTraders();
		
		StringUtil.printSection("Community server");
		if (Config.ENABLE_COMMUNITY_BOARD) // Forums has to be loaded before clan data
			ForumsBBSManager.getInstance().initRoot();
		else
			LOGGER.info("Community server is disabled.");
		
		StringUtil.printSection("Clans");
		CrestCache.getInstance();
		ClanTable.getInstance();
		
		StringUtil.printSection("Geodata & Pathfinding");
		GeoEngine.getInstance();
		
		StringUtil.printSection("Zones");
		ZoneManager.getInstance();
		PvPZoneManager.getInstance();
		
		StringUtil.printSection("Castles & Clan Halls");
		CastleManager.getInstance();
		ClanHallManager.getInstance();
		
		StringUtil.printSection("Task Managers");
		AttackStanceTaskManager.getInstance();
		DecayTaskManager.getInstance();
		GameTimeTaskManager.getInstance();
		ItemsOnGroundTaskManager.getInstance();
		PvpFlagTaskManager.getInstance();
		RandomAnimationTaskManager.getInstance();
		ShadowItemTaskManager.getInstance();
		WaterTaskManager.getInstance();
		
		StringUtil.printSection("Auto Spawns");
		AutoSpawnTable.getInstance();
		
		StringUtil.printSection("Seven Signs");
		SevenSignsManager.getInstance().spawnSevenSignsNPC();
		FestivalOfDarknessManager.getInstance();
		
		StringUtil.printSection("Manor Manager");
		CastleManorManager.getInstance();
		
		StringUtil.printSection("NPCs");
		BufferManager.getInstance();
		HerbDropData.getInstance();
		NpcData.getInstance();
		WalkerRouteData.getInstance();
		DoorData.getInstance().spawn();
		StaticObjectData.getInstance();
		SpawnTable.getInstance();
		RaidBossManager.getInstance();
		GrandBossManager.getInstance();
		DayNightManager.getInstance().notifyChangeMode();
		DimensionalRiftManager.getInstance();
		AuctionTable.getInstance();
		RaidBossInfoManager.getInstance();
		DonateData.getInstance();
		PolymorphData.getInstance();
		
		StringUtil.printSection("Olympiads & Heroes");
		OlympiadGameManager.getInstance();
		Olympiad.getInstance();
		HeroManager.getInstance();
		
		StringUtil.printSection("Four Sepulchers");
		FourSepulchersManager.getInstance();
		
		StringUtil.printSection("Quests & Scripts");
		ScriptData.getInstance();
		
		if (Config.ALLOW_BOAT)
		{
			BoatManager.getInstance();
			BoatGiranTalking.load();
			BoatGludinRune.load();
			BoatInnadrilTour.load();
			BoatRunePrimeval.load();
			BoatTalkingGludin.load();
		}
		
		StringUtil.printSection("Events");
		DerbyTrackManager.getInstance();
		LotteryManager.getInstance();

		if (Config.CKM_ENABLED)
			CharacterKillingManager.getInstance().init();

		if (Config.ALLOW_WEDDING)
			CoupleManager.getInstance();
	
		if (Config.TIME_DOUBLE_RATES > 0)
			DoubleRatesTaskManager.getInstance();

		if (Config.PCB_INTERVAL > 0)
			PcCafeTaskManager.getInstance();

		if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
			FishingChampionshipManager.getInstance();
		
		StringUtil.printSection("Newbie System");
		if (Config.ENABLE_STARTUP)
		{
			StartupManager.getInstance();
			LOGGER.info("Newbie System Actived");
		}
		else
			LOGGER.info("Newbie System Desatived");
		
		StringUtil.printSection("Hwid Manager");
		Hwid.Init();

        StringUtil.printSection("Party Farm Event");
        LOGGER.info("Evento Party Farm");
        if (Config.PARTY_FARM_BY_TIME_OF_DAY && !Config.START_PARTY) {
            InitialPartyFarm.getInstance().StartCalculationOfNextEventTime();
            LOGGER.info("[Party Farm Time]: Enabled");
        } else if (Config.START_PARTY && !Config.PARTY_FARM_BY_TIME_OF_DAY) {
            LOGGER.info("[Start Spawn Party Farm]: Enabled");
            ThreadPool.schedule(new SpawnMonsters(), Config.NPC_SERVER_DELAY * 1000L);
        }
        
        StringUtil.printSection("Tournament Event");
        
		ThreadPool.schedule(Arena2x2.getInstance(), 5000L);
		ThreadPool.schedule(Arena9x9.getInstance(), 5000L);
		ThreadPool.schedule(Arena4x4.getInstance(), 5000L);
		if (Config.TOURNAMENT_EVENT_TIME)
		{
			LOGGER.info("Tournament Event is enabled.");
			ArenaEvent.getInstance().StartCalculationOfNextEventTime();
		}
		else if (Config.TOURNAMENT_EVENT_START)
		{
			LOGGER.info("Tournament Event is enabled.");
			ArenaTask.spawnNpc1();
		}
		else
			LOGGER.info("Tournament Event is disabled");
		
		StringUtil.printSection("Vote System");
		VoteSystem.initialize();
		FakePlayerManager.initialise();
		StringUtil.printSection("Phantom Store");

		if (Config.ALLOW_PHANTOM_STORE)
		{
			PhantomStore.init();
			LOGGER.info("Phantom Store Enabled");
		}
		else
		{
			LOGGER.info("Phantom Store Desatived");
		}
		
		
		StringUtil.printSection("Phantom Walker");

		if (Config.ALLOW_PHANTOM_PLAYERS)
		{
			PhantomWalker.init();
			LOGGER.info("Phantom Walker Enabled");
		}
		else
		{
			LOGGER.info("Phantom Walker Desatived");
		}
		
		if (Config.ALLOW_ANNOUNCE_ONLINE_PLAYERS)
			AnnounceOnlinePlayers.getInstance();
		
		StringUtil.printSection("DataBase Auto Save");
        if (Config.ENABLE_BACKUP_BOOLEAN) {
        	BackupDBSave.getInstance();
            LOGGER.info("[DataBase Auto Save]: Enabled");
        }else
		{
			LOGGER.info("[DataBase Auto Save]: Desatived");
		}
        
        StringUtil.printSection("Team Vs Team");
        TvTEventManager.getInstance();
        
        StringUtil.printSection("Christmas Event");
        LOGGER.info("Loaded " + ChristmasEvent.getInstance().rewardsSize() + " Rewards!");
		
		StringUtil.printSection("Handlers");
		LOGGER.info("Loaded {} admin command handlers.", AdminCommandHandler.getInstance().size());
		LOGGER.info("Loaded {} chat handlers.", ChatHandler.getInstance().size());
		LOGGER.info("Loaded {} item handlers.", ItemHandler.getInstance().size());
		LOGGER.info("Loaded {} skill handlers.", SkillHandler.getInstance().size());
		LOGGER.info("Loaded {} user command handlers.", UserCommandHandler.getInstance().size());
		
		StringUtil.printSection("System");
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		ForumsBBSManager.getInstance();
		
		if (Config.DEADLOCK_DETECTOR)
		{
			LOGGER.info("Deadlock detector is enabled. Timer: {}s.", Config.DEADLOCK_CHECK_INTERVAL);
			
			final DeadLockDetector deadDetectThread = new DeadLockDetector();
			deadDetectThread.setDaemon(true);
			deadDetectThread.start();
		}
		else
			LOGGER.info("Deadlock detector is disabled.");
		
		System.gc();
		initStats();
		StringUtil.printSection("Login");
		LoginServerThread.getInstance().start();
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		final GamePacketHandler handler = new GamePacketHandler();
		_selectorThread = new SelectorThread<>(sc, handler, handler, handler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (Exception e)
			{
				LOGGER.error("The GameServer bind address is invalid, using all available IPs.", e);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.PORT_GAME);
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to open server socket.", e);
			System.exit(1);
		}
		_selectorThread.start();
	}
	
	public static GameServer getInstance()
	{
		return _gameServer;
	}
	
	public SelectorThread<GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
	
	public void initStats()
	{
		StringUtil.printSection("GameInfo");
		LOGGER.info("Gameserver used memory: " + SysUtil.getUsedMemory() + " / " + SysUtil.getMaxMemory() + " Mo.");
		LOGGER.info("Players Online: " + World.getInstance().getPlayers().size());
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				initStats();
			}
		}, 3600 * 1000);
	}
	
	
	public class SpawnMonsters implements Runnable
	{
		public SpawnMonsters()
		{
		}

		@Override
		public void run()
		{
			PartyFarm._aborted = false;
			PartyFarm._started = true;

			PartyFarm.spawnMonsters();
		}
	}
}