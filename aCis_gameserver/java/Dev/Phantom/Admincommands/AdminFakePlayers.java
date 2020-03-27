package Dev.Phantom.Admincommands;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import Dev.Phantom.FakePlayer;
import Dev.Phantom.FakePlayerManager;
import Dev.Phantom.FakePlayerTaskManager;
import Dev.Phantom.Ai.EnchanterAI;
import Dev.Phantom.Ai.Walker.GiranWalkerAI;
import Dev.Phantom.Ai.Walker.GoddardWalkerAI;

/**
 * @author rouxy
 *
 */
public class AdminFakePlayers implements IAdminCommandHandler
{
	private final String fakesFolder = "data/html/admin/fakeplayers/";
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_fakes",
		"admin_pvp",
		"admin_deletepvp",
		"admin_takecontrol",
		"admin_releasecontrol",
		"admin_fakegiran",
		"admin_enchanter",
		"admin_fakegoddard",
		"admin_roboto",
		"admin_walkers"


	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void showFakeDashboard(Player activeChar) {
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(fakesFolder + "index.htm");
		html.replace("%fakecount%", FakePlayerManager.getFakePlayersCount());
		html.replace("%taskcount%", FakePlayerTaskManager.INSTANCE.getTaskCount());
		activeChar.sendPacket(html);
	}
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_fakes"))
		{
			showFakeDashboard(activeChar);
		}
		
		if(Config.CONTROLL_FAKE_ON == true)
		{
				if (command.startsWith("admin_takecontrol"))
				{
					if(activeChar.getTarget() != null && activeChar.getTarget() instanceof FakePlayer) {
						FakePlayer fakePlayer = (FakePlayer)activeChar.getTarget();
						fakePlayer.setUnderControl(true);
						activeChar.setPlayerUnderControl(fakePlayer);
						activeChar.sendMessage("You are now controlling: " + fakePlayer.getName());
						
					}
					activeChar.sendMessage("You can only take control of a Fake Player");
				}
				if (command.startsWith("admin_releasecontrol"))
				{
					if(activeChar.isControllingFakePlayer()) {
						FakePlayer fakePlayer = activeChar.getPlayerUnderControl();
						activeChar.sendMessage("You are not controlling a Fake Player: " + fakePlayer.getName());
						fakePlayer.setUnderControl(false);
						activeChar.setPlayerUnderControl(null);
					}
				}
		}

	    if (command.startsWith("admin_pvp")) {

	          {
	           
	            FakePlayer fakePlayer = FakePlayerManager.spawnPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ());
	            fakePlayer.assignDefaultAI(true);
	          }
	    }
	        
	    
	    if (command.startsWith("admin_fakegiran")) {
	    	
	    	activeChar.sendMessage("Giran Walker Fake Player");

	          {
	            FakePlayer fakePlayer = FakePlayerManager.spawnPlayer1(Config.ENABLE_GIRAN_TO);
	            fakePlayer.setFakeAi(new GiranWalkerAI(fakePlayer));
	        
	       }
	        
	      }
	    
	    if (command.startsWith("admin_fakegoddard")) {
	    	activeChar.sendMessage("Goddard Walker Fake Player");
	      
	          {
	            FakePlayer fakePlayer = FakePlayerManager.spawnPlayer2(Config.ENABLE_GODARD_TO);
	            
	            fakePlayer.setFakeAi(new GoddardWalkerAI(fakePlayer));
	     
	           
	          }
	        }
	      
		
		if (command.startsWith("admin_enchanter")) {
			activeChar.sendMessage("Fake Player Enchanter");
			FakePlayer fakePlayer = FakePlayerManager.spawnPlayer(activeChar.getX(),activeChar.getY(),activeChar.getZ());
			fakePlayer.setFakeAi(new EnchanterAI(fakePlayer));
			}
			
		if(command.startsWith("admin_deletepvp")) {
			if(activeChar.getTarget() != null && activeChar.getTarget() instanceof FakePlayer) {
				FakePlayer fakePlayer = (FakePlayer)activeChar.getTarget();
				fakePlayer.despawnPlayer();
			}
		
		}
		
	    if (command.startsWith("admin_roboto")) {
	        ThreadPool.schedule(new Runnable()
	        {
	          @Override
			public void run()
	          {
	        	  
		        
	            AdminFakePlayers.WhileLoopExample.spawn(Config.TELE_TO_SPWAN50[0], Config.TELE_TO_SPWAN50[1], Config.TELE_TO_SPWAN50[2]);
	            System.out.println("[Fake pvp]: Starded!");
	          }
	        }, 1L);
	      }
	    
	    if (command.startsWith("admin_walkers")) {
	        ThreadPool.schedule(new Runnable()
	        {
	          @Override
			public void run()
	          {
	        	  
		        
	            AdminFakePlayers.WhileLoopExample1.spawnwalker(Config.TELE_TO_GIRAN[0], Config.TELE_TO_GIRAN[1], Config.TELE_TO_GIRAN[2]);
	            System.out.println("[Fake Walkers]: Starded!");
	          }
	        }, 1L);
	      }
	    

			return false;
	
	}
	
	  static class WhileLoopExample1
	  {
	    public static void spawnwalker(int x, int y, int z)
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
	  
	  static class WhileLoopExample
	  {
	    public static void spawn(int x, int y, int z)
	    {
	      int i = 49;
	      while (i > 0)
	      {
	        System.out.println("Puxar: " + i);
	        
            FakePlayer fakePlayer = FakePlayerManager.spawnPlayer(Config.TELE_TO_SPWAN50[0],Config.TELE_TO_SPWAN50[1],Config.TELE_TO_SPWAN50[2]);
            fakePlayer.assignDefaultAI(true);
            
            
	        try
	        {
	          Thread.sleep(1000L);
	        }
	        catch (InterruptedException localInterruptedException) {}
	        i--;
	      }
	    }
	  }
	
	
}
	
