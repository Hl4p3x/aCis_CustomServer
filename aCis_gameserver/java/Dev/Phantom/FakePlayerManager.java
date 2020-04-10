package Dev.Phantom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.data.xml.MapRegionData.TeleportType;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

import Dev.Phantom.Ai.Walker.GiranWalkerAI;
import Dev.Phantom.Helpers.FakeHelpers;

/**
 * @author Rouxy
 *
 */
public enum FakePlayerManager {
	INSTANCE;

	private FakePlayerManager() {

	}

	public static void initialise() {
		FakePlayerNameManager.INSTANCE.initialise();
		FakePlayerTitleManager.INSTANCE.initialise();
		FakePlayerTaskManager.INSTANCE.initialise();
		
	}

	public static FakePlayer spawnPlayer(int x, int y, int z) {
		FakePlayer activeChar = FakeHelpers.createRandomFakePlayer();
		World.getInstance().addPlayer(activeChar);
		handlePlayerClanOnSpawn(activeChar);
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			activeChar.setSpawnProtection(true);
		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2)
				&& activeChar.isInsideZone(net.sf.l2j.gameserver.enums.ZoneId.SIEGE))
			activeChar.teleportTo(TeleportType.TOWN);

		activeChar.heal();
		return activeChar;
	   
	}

	public static void despawnFakePlayer(int objectId) {
		net.sf.l2j.gameserver.model.actor.Player player = World.getInstance().getPlayer(objectId);
		if (player instanceof FakePlayer) {
			FakePlayer fakePlayer = (FakePlayer) player;
			fakePlayer.despawnPlayer();
		}
	}
	
	
	private static void handlePlayerClanOnSpawn(final FakePlayer activeChar) {
        final Clan clan = activeChar.getClan();
        if (clan != null) {
            clan.getClanMember(activeChar.getObjectId()).setPlayerInstance(activeChar);
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN).addCharName(activeChar);
            final PledgeShowMemberListUpdate update = new PledgeShowMemberListUpdate(activeChar);
            for (final Playable member : clan.getOnlineMembers()) {
                if (member != activeChar) {
                    member.sendPacket(msg);
                    member.sendPacket(update);
                }
            }
            for (final Castle castle : CastleManager.getInstance().getCastles()) {
                final Siege siege = castle.getSiege();
                if (!siege.isInProgress()) {
                    continue;
                }


                }
            }
        }
    
	
    public static ArrayList<Integer> getFighterBuffs() {
        return Config.FIGHTER_BUFF_LIST;
    }
    
    public static ArrayList<Integer> getMageBuffs() {
        return Config.MAGE_BUFF_LIST;
    }
    
	public static int getFakePlayersCount() {
		return getFakePlayers().size();
	}

	public static List<FakePlayer> getFakePlayers() {
		return World.getInstance().getPlayers().stream().filter(x -> x instanceof FakePlayer).map(x -> (FakePlayer) x)
				.collect(Collectors.toList());
	}

	/**
	 * @param ENABLE_TELE_TO
	 * @return
	 */
	public static FakePlayer spawnPlayer1(boolean ENABLE_TELE_TO)
	{
		FakePlayer activeChar = FakeHelpers.createRandomFakePlayer();
		World.getInstance().addPlayer(activeChar);
		handlePlayerClanOnSpawn(activeChar);
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			activeChar.setSpawnProtection(true);
		activeChar.spawnMe(Config.TELE_TO_GIRAN[0], Config.TELE_TO_GIRAN[1], Config.TELE_TO_GIRAN[2]);
		activeChar.onPlayerEnter();
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2)
				&& activeChar.isInsideZone(net.sf.l2j.gameserver.enums.ZoneId.SIEGE))
			activeChar.teleportTo(TeleportType.TOWN);

		activeChar.heal();
		
		return activeChar;
	}
	
	public static FakePlayer spawnPlayer2(boolean ENABLE_TELE_TO)
	{
		FakePlayer activeChar = FakeHelpers.createRandomFakePlayer();
		World.getInstance().addPlayer(activeChar);
		handlePlayerClanOnSpawn(activeChar);
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			activeChar.setSpawnProtection(true);
		activeChar.spawnMe(Config.TELE_TO_GODDARD[0], Config.TELE_TO_GODDARD[1], Config.TELE_TO_GODDARD[2]);
		activeChar.onPlayerEnter();
	
        
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2)
				&& activeChar.isInsideZone(net.sf.l2j.gameserver.enums.ZoneId.SIEGE))
			activeChar.teleportTo(TeleportType.TOWN);

		activeChar.heal();
		
		return activeChar;
	}
	
	public static FakePlayer spawnPlayer3(boolean ENABLE_TELE_TO)
	{
		FakePlayer activeChar = FakeHelpers.createRandomFakePlayer();
		World.getInstance().addPlayer(activeChar);
		handlePlayerClanOnSpawn(activeChar);
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			activeChar.setSpawnProtection(true);
		
        ThreadPool.schedule(new Runnable()
        {
          @Override
		public void run()
          {
        	  
	        
        	  FakePlayerManager.WhileLoopExample1.spawn(Config.TELE_TO_GIRAN[0], Config.TELE_TO_GIRAN[1], Config.TELE_TO_GIRAN[2]);
            System.out.println("[Fake Walker Giran]: Starded!");
          }
        }, 1L);
		
		activeChar.onPlayerEnter();

        
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2)
				&& activeChar.isInsideZone(net.sf.l2j.gameserver.enums.ZoneId.SIEGE))
			activeChar.teleportTo(TeleportType.TOWN);

		activeChar.heal();
		
		return activeChar;
	}
	
	  static class WhileLoopExample1
	  {
	    public static void spawn(int x, int y, int z)
	    {
	      int i = 24;
	      while (i > 0)
	      {
	        
	    	System.out.println("Spwan: " + i);
            FakePlayer fakePlayer = FakePlayerManager.spawnPlayer(Config.TELE_TO_GIRAN[0], Config.TELE_TO_GIRAN[1], Config.TELE_TO_GIRAN[2]);
            fakePlayer.setFakeAi(new GiranWalkerAI(fakePlayer));
            
            
	        try
	        {
	          Thread.sleep(9000L);
	        }
	        catch (InterruptedException localInterruptedException) {}
	        i--;
	      }
	    }
	  }
	
}
	 
	
    

