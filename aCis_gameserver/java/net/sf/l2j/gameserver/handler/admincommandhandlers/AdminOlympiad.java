package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.util.StatsSet;

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.taskmanager.HeroTaskManager;

public class AdminOlympiad implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_endoly",
		"admin_addolypoints",
		"admin_removeolypoints",
		"admin_sethero",
		"admin_removehero",
		"admin_setnoble"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		String player = "";
		int value = 0;
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
					value = Integer.parseInt(st.nextToken());
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
		
		if (command.startsWith("admin_endoly"))
		{
			Olympiad.getInstance().manualSelectHeroes();
			activeChar.sendMessage("Heroes have been formed.");
		}
		else if (command.startsWith("admin_addolypoints"))
		{
			if (target == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //addolypoints <char_name> [points]");
				return false;
			}
			
			if (target != null)
			{
				StatsSet playerStat = Olympiad.getInstance().getNobleStats(target.getObjectId());
				if (playerStat == null)
				{
					activeChar.sendMessage("Desculpe mais "+ target.getName() + " ainda não tem registro nas Olimpídas");
					return false;
				}
				
				int oldpoints = Olympiad.getInstance().getNoblePoints(target.getObjectId());
				int points = oldpoints + value;
				if (points > 9999)
				{
					activeChar.sendMessage("Você não pode adicionar mais de 9999 pontos.");
					return false;
				}
				playerStat.set("olympiad_points", points);
				
				activeChar.sendMessage("O Jogador " + target.getName() + " agora tem " + points + " pontos nas Olimpíadas.");
			}
		}
		else if (command.startsWith("admin_removeolypoints"))
		{
			if (target == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //removeolypoints <char_name> [points]");
				return false;
			}
			
			if (target != null)
			{
				StatsSet playerStat = Olympiad.getInstance().getNobleStats(target.getObjectId());
				if (playerStat == null)
				{
					activeChar.sendMessage("Desculpe mais "+ target.getName() + " ainda não tem registro nas Olimpídas");
					return false;
				}
				int oldpoints = Olympiad.getInstance().getNoblePoints(target.getObjectId());
				int points = oldpoints - value;
				
				if (points < 0)
					points = 0;
				
				playerStat.set("olympiad_points", points);
				
				activeChar.sendMessage("O Jogador " + target.getName() + " agora tem " + points + " pontos nas Olimpíadas.");
			}
		}
		else if (command.startsWith("admin_sethero"))
		{
			if (target == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //sethero <char_name> [duration_days]");
				return false;
			}
			
			if (target != null)
				doHero(target, value);
		}
		else if (command.startsWith("admin_removehero"))
		{
			if (target == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //removehero <char_name>");
				return false;
			}
			
			if (target != null)
			{
				if (target.isHero())
				{
					target.setHero(false);
					activeChar.sendMessage(target.getName() + " seu Hero foi removido.");
				}
				else
					activeChar.sendMessage(target.getName() + " não é Hero.");
			}
		}
		else if (command.startsWith("admin_setnoble"))
		{
			if (target == null && player.equals(""))
			{
				activeChar.sendMessage("Usage: //setnoble <char_name>");
				return false;
			}
			
			if (target != null)
			{
				target.setNoble(!target.isNoble(), true);
				target.sendMessage(target.getName() + " agora você é um nobles.");
			}
		}
		
		return true;
	}
	
	public static void doHero(Player target, int time)
	{
		target.setHero(true);
		HeroTaskManager.getInstance().add(target);

		long remainingTime = target.getMemos().getLong("heroTime", 0);
		if (remainingTime > 0)
		{
			target.getMemos().set("heroTime", remainingTime + TimeUnit.DAYS.toMillis(time));
			target.sendMessage(target.getName() + " seu Hero foi estendido por " + time + " dias(s).");
		}
		else
		{
			target.getMemos().set("heroTime", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(time));
			target.sendMessage(target.getName() + " agora você é Hero, sua duração é de " + time + " dia(s).");
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}