package hwid.hwidmanager;

import hwid.HwidConfig;

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Player;

public class HWIDAdminBan implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_ban_hwid"
	};

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (!HwidConfig.ALLOW_GUARD_SYSTEM)
			return false;

		if (activeChar == null)
			return false;

		if (command.startsWith("admin_ban_hwid"))
		{
			WorldObject playerTarger = activeChar.getTarget();
			
			if (playerTarger != null && !playerTarger.equals(activeChar))
			{
				activeChar.sendMessage("Target is empty");
				return false;
			}

			if (playerTarger == null && activeChar.equals(""))
			{
				activeChar.sendMessage("Usage: //ban_hwid <account_name> (if none, target char's account gets banned).");
				return false;
			}
			
			Player target = (Player) playerTarger;
			if (target != null)
			{
				HWIDBan.addHWIDBan(target.getClient());
				activeChar.sendMessage(target.getName() + " banned in HWID");
			}
		}

		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}