package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.taskmanager.VipTaskManager;

/**
 * @author Williams
 *
 */
public class AdminVip implements IAdminCommandHandler
{
	private static String[] _adminCommands =
	{
		"admin_setvip",
		"admin_removevip"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		String player = "";
		int duration = 0;
		Player target = null;
		
		// One parameter, player name
		if (st.hasMoreTokens())
		{
			player = st.nextToken();
			target = World.getInstance().getPlayer(player);
			
			// Second parameter, duration
			if (st.hasMoreTokens())
			{
				try
				{
					duration = Integer.parseInt(st.nextToken());
				}
				catch (NumberFormatException nfe)
				{
					activeChar.sendMessage("Invalid number format used: " + nfe);
					return false;
				}
			}
		}
		else
		{
			// If there is no name, select target
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof Player)
				target = (Player) activeChar.getTarget();
		}
		
		if (command.startsWith("admin_setvip"))
		{
			if (target == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //setvip <char_name> [duration_days]");
				return false;
			}
			
			if (target != null)
				doVip(target, duration);
		}
		else if (command.startsWith("admin_removevip"))
		{
			if (target == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //removevip <char_name>");
				return false;
			}
			
			if (target != null)
			{
				if (target.isVip())
				{
					target.setVip(false);
					activeChar.sendMessage(target.getName() + " seu Vip foi removido.");
				}
				else
					activeChar.sendMessage(target.getName() + " não é Vip.");
			}
		}
		return true;
	}
	
	public static void doVip(Player target, int time)
	{
		target.setVip(true);
		VipTaskManager.getInstance().add(target);
		
		long remainingTime = target.getMemos().getLong("vipTime", 0);
		if (remainingTime > 0)
		{
			target.getMemos().set("vipTime", remainingTime + TimeUnit.DAYS.toMillis(time));
			target.sendMessage(target.getName() + " seu Vip foi estendido por " + time + " dias(s).");
		}
		else
		{
			target.getMemos().set("vipTime", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(time));
			target.sendMessage(target.getName() + " agora você é Vip, sua duração é de " + time + " dia(s).");
			
			for (IntIntHolder item : Config.LIST_VIP_ITEMS)
			{
				if (item.getId() > 0)
					target.addItem("Add", item.getId(), item.getValue(), target, true);
			}
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return _adminCommands;
	}
}