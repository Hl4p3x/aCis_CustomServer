package Dev.FakePlayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.random.Rnd;


import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.data.sql.PlayerInfoTable;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.enums.actors.OperateType;
import net.sf.l2j.gameserver.enums.items.WeaponType;
import net.sf.l2j.gameserver.model.Augmentation;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.stat.Experience;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Weapon;

import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.network.GameClient;
import net.sf.l2j.gameserver.network.GameClient.GameClientState;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreMsgSell;

/**
 * @author Gabia
 *
 */
public class PhantomStore
{
	static final Logger _log = Logger.getLogger(PhantomStore.class.getName());
	static String _phantomAcc = Config.PHANTOM_PLAYERS_ACC_2;
	static int _PhantomsCount = 0;
	static int _PhantomsLimit = 0;
	static int _setsCount = 0;
	static int _setsCountClan = 0;
	volatile int _PhantomsTownTotal = 0;
	static ArrayList<L2Set> _sets = new ArrayList<>();
	static int _setsArcherCount = 0;
	static ArrayList<L2Set> _setsArcher = new ArrayList<>();
	static PhantomStore _instance;
	static int _setsOlyCount = 0;
	static ArrayList<L2Set> _setsOly = new ArrayList<>();
	static int _locsCount = 0;
	static ArrayList<Location> _PhantomsTownLoc = new ArrayList<>();
	static HashMap<Integer, L2Fantome> _phantoms = new HashMap<>();
	static int _PhantomsEnchPhsCount = 0;
	static ArrayList<String> _PhantomsEnchPhrases = new ArrayList<>();
	static int _PhantomsLastPhsCount = 0;
	static ArrayList<String> _PhantomsLastPhrases = new ArrayList<>();
	static Map<Integer, ConcurrentLinkedQueue<Player>> _PhantomsTown = new ConcurrentHashMap<>();
	static Map<Integer, ConcurrentLinkedQueue<Player>> _PhantomsTownClan = new ConcurrentHashMap<>();
	static Map<Integer, ConcurrentLinkedQueue<Integer>> _PhantomsTownClanList = new ConcurrentHashMap<>();
	public static ArrayList<Player> _players = new ArrayList<>();
	
	public static PhantomStore getInstance()
	{
		return _instance;
	}
	
	private void load()
	{
		
		parceTownLocs();
		parceArmors();
		cacheFantoms();
		_PhantomsTown.put(Integer.valueOf(1), new ConcurrentLinkedQueue<Player>());
		_PhantomsTown.put(Integer.valueOf(2), new ConcurrentLinkedQueue<Player>());
		
		FakePlayerNameManager.INSTANCE.initialise();
		FakePlayerTitleManager.INSTANCE.initialise();
		
	}
	
	public void reload()
	{
		parceArmors();
		parceTownLocs();
	}
	
	public static void init()
	{
		_instance = new PhantomStore();
		_instance.load();
	}
	
	static int getFaceEquipe()
	{
		return Config.LIST_PHANTOM_FACE.get(Rnd.get(Config.LIST_PHANTOM_FACE.size())).intValue();
	}
	
	
	static int getPrivateBuy()
	{
		return Config.LIST_PRIVATE_BUY.get(Rnd.get(Config.LIST_PRIVATE_BUY.size())).intValue();
	}
	
	static int getPrivateSell()
	{
		return Config.LIST_PRIVATE_SELL.get(Rnd.get(Config.LIST_PRIVATE_SELL.size())).intValue();
	}
	
	@SuppressWarnings("null")
	static Location getRandomLoc()
	{
		Location loc = null;
		if (loc == null)
		{
			loc = _PhantomsTownLoc.get(Rnd.get(0, _locsCount));
		}
		return loc;
	}
	
	public static void startWalk(Player paramPlayer)
	{
		ThreadPool.schedule(new PhantomWalk(paramPlayer), Rnd.get(5200, 48540));
	}
	
	
	@SuppressWarnings("resource")
	private static void parceArmors()
	{
		if (!_sets.isEmpty())
		{
			_sets.clear();
		}
		LineNumberReader localLineNumberReader = null;
		BufferedReader localBufferedReader = null;
		FileReader localFileReader = null;
		try
		{
			File localFile = new File("./config/aCis/phantom/store_sets.ini");
			if (!localFile.exists())
			{
				return;
			}
			localFileReader = new FileReader(localFile);
			localBufferedReader = new BufferedReader(localFileReader);
			localLineNumberReader = new LineNumberReader(localBufferedReader);
			String str;
			while ((str = localLineNumberReader.readLine()) != null)
			{
				if ((str.trim().length() != 0) && (!str.startsWith("#")))
				{
					String[] arrayOfString = str.split(",");
					_sets.add(new L2Set(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), Integer.parseInt(arrayOfString[2]), Integer.parseInt(arrayOfString[3]), Integer.parseInt(arrayOfString[4]), Integer.parseInt(arrayOfString[5]), Integer.parseInt(arrayOfString[6])));
				}
			}
			_setsCount = _sets.size();
			_log.info("Load " + _setsCount + " phantom Store armor sets");
			return;
		}
		catch (Exception localException2)
		{
			localException2.printStackTrace();
		}
		finally
		{
			try
			{
				if (localFileReader != null)
				{
					localFileReader.close();
				}
				if (localBufferedReader != null)
				{
					localBufferedReader.close();
				}
				if (localLineNumberReader != null)
				{
					localLineNumberReader.close();
				}
			}
			catch (Exception localException6)
			{
			}
		}
	}
	
	private void cacheFantoms()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				ThreadPool.schedule(new FantomTask(1), 10000);
			}
		}).start();
	}
	
	static L2Set getRandomSet()
	{
		return _sets.get(Rnd.get(_setsCount));
	}
	
	static class PhantomWalk implements Runnable
	{
		Player _phantom;
		
		public PhantomWalk(Player paramPlayer)
		{
			_phantom = paramPlayer;
		}
		
		@Override
		public void run()
		{
			if (!_phantom.isDead())
			{
				if ((Rnd.get(100) < Config.PHANTOM_PRIVATE_BUY_CHANCE) && (Config.PHANTOM_PRIVATE_STORE))
				{
					_phantom.addItem(":", 57, 1000000000, _phantom, false);
					_phantom.getBuyList().addItemByItemId(getPrivateBuy(), 1, Rnd.get(1000, 24678), 0);
					
					_phantom.getBuyList().setTitle(getPrivateBuy_Title());
					_phantom.sitDown(false);
					_phantom.setOperateType(OperateType.BUY);
					_phantom.setStoreType(Player.StoreType.BUY);
					_phantom.broadcastUserInfo();
					_phantom.broadcastPacket(new PrivateStoreMsgBuy(_phantom));
				}
				else if ((Rnd.get(100) < Config.PHANTOM_PRIVATE_SELL_CHANCE) && (Config.PHANTOM_PRIVATE_STORE))
				{
					_phantom.addItem(":", getPrivateSell(), 1, _phantom, true);
					for (ItemInstance itemDrop : _phantom.getInventory().getItems())
					{
						_phantom.getSellList().addItem(itemDrop.getObjectId(), 1, Rnd.get(100456789, 150456789));
					}
					_phantom.getSellList().setTitle(getPrivateSell_Title());
					_phantom.getSellList().setPackaged(Player.StoreType.SELL == Player.StoreType.PACKAGE_SELL);
					_phantom.sitDown(false);
					_phantom.setOperateType(OperateType.SELL);
					_phantom.setStoreType(Player.StoreType.SELL);

					_phantom.broadcastUserInfo();
					_phantom.broadcastPacket(new PrivateStoreMsgSell(_phantom));
				}
				else
				{
					if (_phantom.isSpawnProtected())
					{
						_phantom.setSpawnProtection(false);
					}
					_phantom.rndWalk();
					
					PhantomStore.startWalk(_phantom);
				}
			}
		}
	}
	
	static class L2Set
	{
		public int _body;
		public int _gaiters;
		public int _gloves;
		public int _boots;
		public int _weapon;
		public int _custom;
		public int _grade;
		
		L2Set(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
		{
			_body = paramInt1;
			_gaiters = paramInt2;
			_gloves = paramInt3;
			_boots = paramInt4;
			_weapon = paramInt5;
			_grade = paramInt6;
			_custom = paramInt7;
		}
	}
	
	static class L2Fantome
	{
		public String name;
		public String title;
		public int x;
		public int y;
		public int z;
		
		L2Fantome(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
		{
			name = paramString1;
			title = paramString2;
			x = paramInt1;
			y = paramInt2;
			z = paramInt3;
		}
	}
	
	public static int getPlayersCount()
	{
		if (_players != null)
		{
			return _players.size();
		}
		return 0;
	}
	
	public static void removePlayers(Player spec)
	{
		if ((_players != null) && (_players.contains(spec)))
		{
			_players.remove(spec);
			
		}
	}
	
	static SimpleDateFormat sdf = new SimpleDateFormat("HH");

	public class FantomTask implements Runnable
	{
		public int _task;


		public FantomTask(int paramInt)
		{
			_task = paramInt;
		}

		@SuppressWarnings("resource")
		@Override
		public void run()
		{
			int i = 0;
			switch (_task)
			{
				case 1:
					PhantomStore._log.info("PhantomStore: Account " + Config.PHANTOM_PLAYERS_ACC_2 + ", spawn started.");
					try (Connection con = L2DatabaseFactory.getInstance().getConnection())
					{
						PreparedStatement stm = con.prepareStatement("SELECT obj_Id,char_name,title,x,y,z,sex,clanid FROM characters WHERE account_name = ?");
						stm.setString(1, PhantomStore._phantomAcc);
						
						ResultSet rs = stm.executeQuery();
						while (rs.next())
						{
							Player player = null;
							try
							{
								PhantomStore.L2Set localL2Set = PhantomStore.getRandomSet();
								ItemInstance localL2ItemInstance1 = ItemData.getInstance().createDummyItem(localL2Set._body);
								ItemInstance localL2ItemInstance2 = ItemData.getInstance().createDummyItem(localL2Set._gaiters);
								ItemInstance localL2ItemInstance3 = ItemData.getInstance().createDummyItem(localL2Set._gloves);
								ItemInstance localL2ItemInstance4 = ItemData.getInstance().createDummyItem(localL2Set._boots);
								ItemInstance localL2ItemInstance5 = ItemData.getInstance().createDummyItem(localL2Set._weapon);
								ItemInstance localL2ItemInstance6 = null;
								
								ItemInstance WINGS = ItemData.getInstance().createDummyItem(PhantomStore.getFaceEquipe());
								
								int k = localL2Set._grade;
								int m = 1;
								int n = 0;
								if (k == 0)
								{
									m = Rnd.get(1, 19);
								}
								if (k == 1)
								{
									m = Rnd.get(20, 39);
								}
								if (k == 2)
								{
									m = Rnd.get(40, 51);
								}
								if (k == 3)
								{
									m = Rnd.get(52, 60);
								}
								if (k == 4)
								{
									m = Rnd.get(61, 75);
								}
								if (k == 5)
								{
									m = Rnd.get(76, 80);
								}
								GameClient client = new GameClient(null);
								client.setDetached(true);
								player = Player.loadPhantom(rs.getInt("obj_Id"), m, n, false);
								
								player.setClient(client);
								client.setActiveChar(player);
								player.setOnlineStatus(false, true);
								World.getInstance().addPlayer(player);
								client.setState(GameClient.GameClientState.IN_GAME);
								
							
								
								client.setAccountName(player.getAccountName());
								
								String playerName = FakePlayerNameManager.INSTANCE.getRandomAvailableName();
								player.setName(playerName);
								PlayerInfoTable.getInstance().updatePlayerData(player, false);
								
								player.setClan(ClanTable.getInstance().getClan(Config.CLANID));

                                player.getAppearance().setNameColor(Integer.decode("0x" + Config.NAME_COLOR));
                                player.getAppearance().setTitleColor(Integer.decode("0x" + Config.TITLE_COLOR));
                                player.getAppearance().setNameColor(Integer.decode("0x" + PhantomStore.getNameColor()));
                                player.getAppearance().setTitleColor(Integer.decode("0x" + PhantomStore.getTitleColor()));
 
                                
                                if (Rnd.get(100) < Config.PHANTOM_CHANCE_HERO) {
                                    player.setHero(true);
                                    player.isHero();
                                    player.broadcastUserInfo();
                                }
								
								player.addExpAndSp(Experience.LEVEL[81], 0);
								player.rewardSkills();
								player.broadcastUserInfo();
								
								String playertitle = FakePlayerTitleManager.INSTANCE.getRandomAvailableName();
								player.setTitle(playertitle);	
								player.broadcastTitleInfo();
								
								player.getInventory().equipItemAndRecord(localL2ItemInstance1);
								player.getInventory().equipItemAndRecord(localL2ItemInstance2);
								player.getInventory().equipItemAndRecord(localL2ItemInstance3);
								player.getInventory().equipItemAndRecord(localL2ItemInstance4);
								if ((Rnd.get(100) < 70) && (Config.ALLOW_PHANTOM_FACE))
								{
									player.getInventory().equipItemAndRecord(WINGS);
								}
								
								int[] arrayOfInt =
								{
									92,
									102,
									109
								};
								if (localL2Set._custom > 0)
								{
									localL2ItemInstance6 = ItemData.getInstance().createDummyItem(localL2Set._custom);
									player.getInventory().equipItemAndRecord(localL2ItemInstance6);
								}
								Weapon localL2Weapon = localL2ItemInstance5.getWeaponItem();
								if ((localL2Weapon.getItemType() == WeaponType.BOW) && ((player.getClassId().getId() != 92) || (player.getClassId().getId() != 102) || (player.getClassId().getId() != 109)))
								{
									player.setClassId(arrayOfInt[Rnd.get(arrayOfInt.length)]);
								}
								player.getInventory().equipItemAndRecord(localL2ItemInstance5);
								
								Location localLocation = PhantomStore.getRandomLoc();
								player.setXYZInvisible(localLocation.getX(), localLocation.getY(), localLocation.getZ());
								player.spawnMe(localLocation.getX(), localLocation.getY(), localLocation.getZ());
								player.setLastCords(player.getX(), player.getY(), player.getZ());
								if (Config.PLAYER_SPAWN_PROTECTION > 0)
								{
									player.setSpawnProtection(true);
								}
								player.broadcastUserInfo();
								
								if (Rnd.get(100) < 15)
								{
									PhantomStore.startWalk(player);
								}
								else if (Rnd.get(100) < Config.PHANTOM_PLAYERS_WALK)
								{
									PhantomStore.startWalk(player);
								}
								
								PhantomStore._players.add(player);
								
								localL2ItemInstance5.setEnchantLevel(Rnd.get(Config.PHANTOM_PLAYERS_ENCHANT_MIN, Config.PHANTOM_PLAYERS_ENCHANT_MAX));
								if ((Rnd.get(100) < 30) && (Config.PHANTOM_PLAYERS_ARGUMENT_ANIM))
								{
									localL2ItemInstance5.setAugmentation(new Augmentation(1067847165, 3250, 1));
								}
								if (player.isDead())
								{
									player.doRevive();
								}
								if ((Config.PHANTOM_PLAYERS_SOULSHOT_ANIM) && (Rnd.get(100) < 45))
								{
									try
									{
										Thread.sleep(900L);
									}
									catch (InterruptedException localInterruptedException)
									{
									}
									player.broadcastPacket(new MagicSkillUse(player, player, 2154, 1, 0, 0));
									try
									{
										Thread.sleep(300L);
									}
									catch (InterruptedException localInterruptedException1)
									{
									}
									player.broadcastPacket(new MagicSkillUse(player, player, 2164, 1, 0, 0));
								}
								try
								{
									Thread.sleep(Config.PHANTOM_DELAY_SPAWN_FIRST);
								}
								catch (InterruptedException localInterruptedException2)
								{
								}
								player.setRunning();
								
								if (Rnd.get(100) < Config.PHANTOM_CHANCE_MALARIA)
								{
									L2Skill skill = SkillTable.getInstance().getInfo(4554, 4);
									skill.getEffects(player, player);
								}
								PhantomStore.startWalk(player);
								
								player.addSkill(SkillTable.getInstance().getInfo(9900, 1), false);
								
								PhantomStore._PhantomsTown.get(Integer.valueOf(1)).add(player);
								PhantomStore._players.add(player);
								
								i++;
							}
							catch (Exception e)
							{
								PhantomStore._log.log(Level.WARNING, "FakePlayers: " + player, e);
								if (player != null)
								{
									player.deleteMe();
								}
							}
							ThreadPool.schedule(new PhantomStore.Disconnection(player), Config.DISCONNETC_DELAY);
							
						}
						rs.close();
						stm.close();
					}
					catch (Exception e)
					{
						PhantomStore._log.log(Level.WARNING, "FakePlayerss: ", e);
					}
					PhantomStore._log.info("Phantom Store: Foi Gerado " + i + " Phantom Store.");
					break;
			}
		}
	}
	
	public class Disconnection implements Runnable
	{
		private final Player _activeChar;

		public Disconnection(Player activeChar)
		{
			_activeChar = activeChar;
		}

		@Override
		public void run()
		{
			if (_activeChar.isOnline())
			{
				PhantomStore.removePlayers(_activeChar);
				final GameClient client = _activeChar.getClient();
				// detach the client from the char so that the connection isnt closed in the deleteMe
				_activeChar.setClient(null);
				// removing player from the world
				_activeChar.deleteMe();
				client.setActiveChar(null);
				client.setState(GameClientState.AUTHED);
			}
		}

	}
	
	@SuppressWarnings("resource")
	private static void parceTownLocs()
	{
		_PhantomsTownLoc.clear();
		LineNumberReader localLineNumberReader = null;
		BufferedReader localBufferedReader = null;
		FileReader localFileReader = null;
		try
		{
			File localFile = new File("./config/aCis/phantom/store_locs.ini");
			if (!localFile.exists())
			{
				return;
			}
			localFileReader = new FileReader(localFile);
			localBufferedReader = new BufferedReader(localFileReader);
			localLineNumberReader = new LineNumberReader(localBufferedReader);
			String str;
			while ((str = localLineNumberReader.readLine()) != null)
			{
				if ((str.trim().length() != 0) && (!str.startsWith("#")))
				{
					String[] arrayOfString = str.split(",");
					_PhantomsTownLoc.add(new Location(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), Integer.parseInt(arrayOfString[2])));
				}
			}
			_locsCount = _PhantomsTownLoc.size() - 1;
			_log.info("Load " + _locsCount + " phantom Store Locations");
			return;
		}
		catch (Exception localException2)
		{
			localException2.printStackTrace();
		}
		finally
		{
			try
			{
				if (localFileReader != null)
				{
					localFileReader.close();
				}
				if (localBufferedReader != null)
				{
					localBufferedReader.close();
				}
				if (localLineNumberReader != null)
				{
					localLineNumberReader.close();
				}
			}
			catch (Exception localException5)
			{
			}
		}
	}
	
	static final List<String> list_msg_buy = new ArrayList<>();
	static final List<String> list_msg_sell = new ArrayList<>();
	
	
	@SuppressWarnings("null")
	static String getPrivateBuy_Title()
	{
		String msg = null;
		if (msg == null)
		{
			msg = Config.PHANTOM_PRIVATE_BUY_TITLE.get(Rnd.get(Config.PHANTOM_PRIVATE_BUY_TITLE.size()));
		}
		if (list_msg_buy.contains(msg))
		{
			boolean gerar = true;
			while (gerar)
			{
				msg = Config.PHANTOM_PRIVATE_BUY_TITLE.get(Rnd.get(Config.PHANTOM_PRIVATE_BUY_TITLE.size()));
				if (!list_msg_buy.contains(msg))
				{
					list_msg_buy.add(msg);
					gerar = false;
					return msg;
				}
				if (list_msg_buy.size() == Config.PHANTOM_PRIVATE_BUY_TITLE.size())
				{
					gerar = false;
					return "";
				}
			}
		}
		else if (!list_msg_buy.contains(msg))
		{
			list_msg_buy.add(msg);
			return msg;
		}
		return msg;
	}
	
	@SuppressWarnings("null")
	static String getPrivateSell_Title()
	{
		String msg = null;
		if (msg == null)
		{
			msg = Config.PHANTOM_PRIVATE_SELL_TITLE.get(Rnd.get(Config.PHANTOM_PRIVATE_SELL_TITLE.size()));
		}
		if (list_msg_sell.contains(msg))
		{
			boolean gerar = true;
			while ((list_msg_sell.size() < Config.PHANTOM_PRIVATE_SELL_TITLE.size()) && (gerar))
			{
				msg = Config.PHANTOM_PRIVATE_SELL_TITLE.get(Rnd.get(Config.PHANTOM_PRIVATE_SELL_TITLE.size()));
				if (!list_msg_sell.contains(msg))
				{
					list_msg_sell.add(msg);
					gerar = false;
					return msg;
				}
				if (list_msg_sell.size() == Config.PHANTOM_PRIVATE_SELL_TITLE.size())
				{
					gerar = false;
					return "";
				}
			}
		}
		else if (!list_msg_sell.contains(msg))
		{
			list_msg_sell.add(msg);
			return msg;
		}
		return msg;
	}
	
    static String getNameColor() {
        return Config.PHANTOM_PLAYERS_NAME_CLOLORS.get(Rnd.get(Config.PHANTOM_PLAYERS_NAME_CLOLORS.size()));
    }

    static String getTitleColor() {
        return Config.PHANTOM_PLAYERS_TITLE_CLOLORS.get(Rnd.get(Config.PHANTOM_PLAYERS_TITLE_CLOLORS.size()));
    }
    

}
