package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.enums.GaugeColor;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SetupGauge;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

/**
 * @author Williams
 *
 */
public class Casino extends Folk
{
	public Casino(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (player == null)
			return;
		
		if (command.startsWith("play1") && player.getInventory().getInventoryItemCount(9142, 0) >= 2)
			Casino1(player);
		if (command.startsWith("play2") && player.getInventory().getInventoryItemCount(9142, 0) >= 4)
			Casino2(player);
		if (command.startsWith("play3") && player.getInventory().getInventoryItemCount(9142, 0) >= 8)
			Casino3(player);
		if (command.startsWith("play4") && player.getInventory().getInventoryItemCount(9142, 0) >= 16)
			Casino4(player);
		if (command.startsWith("play5") && player.getInventory().getInventoryItemCount(57, 0) >= 500000)
			Casino5(player);
		if (command.startsWith("play6") && player.getInventory().getInventoryItemCount(57, 0) >= 1000000)
			Casino6(player);
		if (command.startsWith("play7") && player.getInventory().getInventoryItemCount(57, 0) >= 10000000)
			Casino7(player);
	}
	
	public static void displayCongrats(Player player)
	{
		player.broadcastPacket(new SocialAction(player, 3));
		MagicSkillUse  MSU = new MagicSkillUse(player, player, 2024, 1, 1, 0);
		player.broadcastPacket(MSU);
		player.sendMessage("Congratulations! you won");
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		String name = "data/html/casino/" + getNpcId() + ".htm";
		if (val != 0)
			name = "data/html/casino/" + getNpcId() + "-" + val + ".htm";
		
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(name);
		html.replace("%objectId%", getObjectId());
		html.replace("%player%", player.getName());
		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public static void Casino1(Player player)
	{
		player.setTarget(player);
		player.getAI().setIntention(IntentionType.IDLE);
		player.disableAllSkills();
		player.broadcastPacketInRadius(new MagicSkillUse(player, 361, 1, 1000, 0), 810000);
		SetupGauge sg = new SetupGauge(GaugeColor.BLUE, 1000);
		player.sendPacket(sg);
		
		Casino1 ef = new Casino1(player);
		ThreadPool.schedule(ef, 1000);
	}
	
	public static class Casino1 implements Runnable
	{
		private Player _player;
		
		public Casino1(Player player)
		{
			_player = player;
		}
		
		@Override
		public void run()
		{
			if (_player.isDead())
				return;
			
			_player.setIsIn7sDungeon(false);
			_player.enableAllSkills();
			
			int chance = Rnd.get(2);
			
			if (_player.getInventory().getInventoryItemCount(9142, 0) >= 2)
			{
				if (chance == 0)
				{
					displayCongrats(_player);
					_player.getInventory().addItem("Adena", 9142, 2, _player, null);
				}
				
				if (chance == 1)
				{
					_player.sendMessage("You lost the bet");
					_player.getInventory().destroyItemByItemId("Adena", 9142, 2, _player, null);
				}
			}
			else
				_player.sendMessage("You do not have eneough items.");
		}
	}
	
	public static void Casino2(Player player)
	{
		player.setTarget(player);
		player.getAI().setIntention(IntentionType.IDLE);
		player.disableAllSkills();
		player.broadcastPacketInRadius(new MagicSkillUse(player, 361, 1, 1000, 0), 810000);
		SetupGauge sg = new SetupGauge(GaugeColor.BLUE, 1000);
		player.sendPacket(sg);
		
		Casino2 ef = new Casino2(player);
		ThreadPool.schedule(ef, 1000);
	}
	
	static class Casino2 implements Runnable
	{
		private Player _player;
		Casino2(Player player)
		{
			_player = player;
		}
		@Override
		public void run()
		{
			if(_player.isDead())
				return;
			
			_player.setIsIn7sDungeon(false);
			_player.enableAllSkills();
			int chance = Rnd.get(3);
			
			if(_player.getInventory().getInventoryItemCount(9142, 0) >= 4)
			{
				if(chance == 0)
				{
					displayCongrats(_player);
					_player.getInventory().addItem("Adena", 9142, 4, _player, null);
				}
				if(chance == 1)
				{
					_player.sendMessage("You lost the bet");
					_player.getInventory().destroyItemByItemId("Adena", 9142, 4, _player, null);
				}
				if(chance == 2)
				{
					_player.sendMessage("You lost the bet");
					_player.getInventory().destroyItemByItemId("Adena", 9142, 4, _player, null);
				}
			}
			else
				_player.sendMessage("You do not have eneough items.");
		}
	}
	
	public static void Casino3(Player player)
	{
		player.setTarget(player);
		player.getAI().setIntention(IntentionType.IDLE);
		player.disableAllSkills();
		player.broadcastPacketInRadius(new MagicSkillUse(player, 361, 1, 1000, 0), 810000);
		SetupGauge sg = new SetupGauge(GaugeColor.BLUE, 1000);
		player.sendPacket(sg);
		
		Casino3 ef = new Casino3(player);
		ThreadPool.schedule(ef, 1000);
	}
	
	static class Casino3 implements Runnable
	{
		private Player _player;
		Casino3(Player player)
		{
			_player = player;
		}
		@Override
		public void run()
		{
			if(_player.isDead())
				return;
			
			_player.setIsIn7sDungeon(false);
			_player.enableAllSkills();
			int chance = Rnd.get(3);
			
			if(_player.getInventory().getInventoryItemCount(9142, 0) >= 8)
			{
				if(chance == 0)
				{
					displayCongrats(_player);
					_player.getInventory().addItem("Adena", 9142, 8, _player, null);
				}
				if(chance == 1)
				{
					_player.sendMessage("You lost the bet");
					_player.getInventory().destroyItemByItemId("Adena", 9142, 8, _player, null);
				}
				if(chance == 2)
				{
					_player.sendMessage("You lost the bet");
					_player.getInventory().destroyItemByItemId("Adena", 9142, 8, _player, null);
				}
			}
			else
				_player.sendMessage("You do not have eneough items.");
		}
	}
	
	public static void Casino4(Player player)
	{
		player.setTarget(player);
		player.getAI().setIntention(IntentionType.IDLE);
		player.disableAllSkills();
		player.broadcastPacketInRadius(new MagicSkillUse(player, 361, 1, 1000, 0), 810000);
		SetupGauge sg = new SetupGauge(GaugeColor.BLUE, 1000);
		player.sendPacket(sg);
		
		Casino4 ef = new Casino4(player);
		ThreadPool.schedule(ef, 1000);
	}
	
	static class Casino4 implements Runnable
	{
		private Player _player;
		Casino4(Player player)
		{
			_player = player;
		}
		@Override
		public void run()
		{
			if(_player.isDead())
				return;
			
			_player.setIsIn7sDungeon(false);
			_player.enableAllSkills();
			int chance = Rnd.get(3);
			
			if(_player.getInventory().getInventoryItemCount(9142, 0) >= 16)
			{
				if(chance == 0)
				{
					displayCongrats(_player);
					_player.getInventory().addItem("Adena", 9142, 16, _player, null);
				}
				if(chance == 1)
				{
					_player.sendMessage("You lost the bet");
					_player.getInventory().destroyItemByItemId("Adena", 9142, 16, _player, null);
				}
				if(chance == 2)
				{
					_player.sendMessage("You lost the bet");
					_player.getInventory().destroyItemByItemId("Adena", 9142, 16, _player, null);
				}
			}
			else
				_player.sendMessage("You do not have eneough items.");
		}
	}
	
	public static void Casino5(Player player)
	{
		player.setTarget(player);
		player.getAI().setIntention(IntentionType.IDLE);
		player.disableAllSkills();
		player.broadcastPacketInRadius(new MagicSkillUse(player, 361, 1, 1000, 0), 810000);
		SetupGauge sg = new SetupGauge(GaugeColor.BLUE, 1000);
		player.sendPacket(sg);
		
		Casino5 ef = new Casino5(player);
		ThreadPool.schedule(ef, 1000);
	}
	
	static class Casino5 implements Runnable
	{
		private Player _player;
		Casino5(Player player)
		{
			_player = player;
		}
		@Override
		public void run()
		{
			if(_player.isDead())
				return;
			
			_player.setIsIn7sDungeon(false);
			_player.enableAllSkills();
			int chance = Rnd.get(2);
			
			if(_player.getInventory().getInventoryItemCount(57, 0) >= 500000)
			{
				if(chance == 0)
				{
					displayCongrats(_player);
					_player.getInventory().addItem("Adena", 57, 500000, _player, null);
				}
				if(chance == 1)
				{
					_player.sendMessage("You lost the bet");
					_player.getInventory().destroyItemByItemId("Adena", 57, 500000, _player, null);
				}
			}
			else
				_player.sendMessage("You do not have eneough items.");
		}
	}
	
	public static void Casino6(Player player)
	{  
		player.setTarget(player);
		player.getAI().setIntention(IntentionType.IDLE);
		player.disableAllSkills();
		player.broadcastPacketInRadius(new MagicSkillUse(player, 361, 1, 1000, 0), 810000);
		SetupGauge sg = new SetupGauge(GaugeColor.BLUE, 1000);
		player.sendPacket(sg);
		
		Casino6 ef = new Casino6(player);
		ThreadPool.schedule(ef, 1000);
	}
	
	static class Casino6 implements Runnable
	{
		private Player _player;
		Casino6(Player player)
		{
			_player = player;
		}
		@Override
		public void run()
		{
			if(_player.isDead())
				return;
			
			_player.setIsIn7sDungeon(false);
			_player.enableAllSkills();
			int chance = Rnd.get(3);
			
			if(_player.getInventory().getInventoryItemCount(57, 0) >= 1000000)
			{
				if(chance == 0)
				{
					displayCongrats(_player);
					_player.getInventory().addItem("Adena", 57, 1000000, _player, null);
				}
				if(chance == 1)
				{
					_player.sendMessage("You lost the bet");
					_player.getInventory().destroyItemByItemId("Adena", 57, 1000000, _player, null);
				}
				if(chance == 2)
				{
					_player.sendMessage("You lost the bet");
					_player.getInventory().destroyItemByItemId("Adena", 57, 1000000, _player, null);
				}
			}
			else
				_player.sendMessage("You do not have eneough items.");
		}
	}
	
	public static void Casino7(Player player)
	{
		player.setTarget(player);
		player.getAI().setIntention(IntentionType.IDLE);
		player.disableAllSkills();
		player.broadcastPacketInRadius(new MagicSkillUse(player, 361, 1, 1000, 0), 810000);
		SetupGauge sg = new SetupGauge(GaugeColor.BLUE, 1000);
		player.sendPacket(sg);
		
		Casino7 ef = new Casino7(player);
		ThreadPool.schedule(ef, 1000);
	}
	
	static class Casino7 implements Runnable
	{
		private Player _player;
		
		Casino7(Player player)
		{
			_player = player;
		}
		@Override
		public void run()
		{
			if(_player.isDead())
				return;
			
			_player.setIsIn7sDungeon(false);
			_player.enableAllSkills();
			int chance = Rnd.get(3);
			
			if(_player.getInventory().getInventoryItemCount(57, 0) >= 10000000)
			{
				if(chance == 0)
				{
					displayCongrats(_player);
					_player.getInventory().addItem("Adena", 57, 10000000, _player, null);
				}
				
				if(chance == 1)
				{
					_player.sendMessage("You lost the bet");
					_player.getInventory().destroyItemByItemId("Adena", 57, 10000000, _player, null);
				}
				
				if(chance == 2)
				{
					_player.sendMessage("You lost the bet");
					_player.getInventory().destroyItemByItemId("Adena", 57, 10000000, _player, null);
				}
			}
			else
				_player.sendMessage("You do not have eneough items.");
		}
	}
}