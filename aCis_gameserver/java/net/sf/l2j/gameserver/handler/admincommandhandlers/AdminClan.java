package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.commons.lang.StringUtil;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.network.SystemMessageId;

/**
 * @author Williams
 *
 */
public class AdminClan implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS = 
	{
		"admin_clanfull"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player player)
	{
		if (command.startsWith("admin_clanfull"))
		{
			String clanName = command.substring(15);
			final Clan clan = ClanTable.getInstance().getClanByName(clanName);
			if (clan == null)
				return false;
			
			if (!StringUtil.isValidString(clanName, "^[A-Za-z0-9]{3,16}$"))
			{
				player.sendPacket(SystemMessageId.INCORRECT_NAME_TRY_AGAIN);
				return false;
			}
			else if (clan.getLevel() == 8)
			{
				player.sendMessage("Desculpe, mais o clan "+ clan.getName() +" já estar level 8!");
				return false;
			}
			
			for (int i = 370; i <= 391; i++)
				player.getClan().addNewSkill(SkillTable.getInstance().getInfo(i, SkillTable.getInstance().getMaxLevel(i)), false);            
			
			clan.addReputationScore(30000000);
			clan.changeLevel(8);
			clan.updateClanInDB();
			
			player.sendMessage("O Clan " + clan.getName() +" esta clanfull agora!");
		}
		
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}