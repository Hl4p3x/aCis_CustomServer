package net.sf.l2j.gameserver.model.actor.instance;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Base64;
import java.util.StringTokenizer;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author williams
 *
 */
public class ChangePassword extends Folk
{
	public ChangePassword(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command);
		
		String newPass = st.nextToken();
		String repPass = st.nextToken();
		
		if (command.startsWith("password"))
		{
			if (newPass.length() < 3)
			{
				player.sendMessage("The new password is too short!");
				return;
			}
			else if (newPass.length() > 16)
			{
				player.sendMessage("The new password is too long!");
				return;
			}
			else if (!newPass.equals(repPass))
			{
				player.sendMessage("Repeated password doesn't match the new password.");
				return;
			}
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection(); 
				PreparedStatement ps = con.prepareStatement("UPDATE accounts SET password=? WHERE login=?"))
			{
				byte[] newPassword = MessageDigest.getInstance("SHA").digest(newPass.getBytes("UTF-8"));
				
				ps.setString(1, Base64.getEncoder().encodeToString(newPassword));
				ps.setString(2, player.getAccountName());
				ps.executeUpdate();
				
				player.sendMessage("Congratulations! Your password has been changed. You will now be disconnected for security reasons. Please login again.");
				ThreadPool.schedule(() -> player.logout(false), 3000);
			}
			catch (Exception e)
			{
				LOGGER.warn("There was an error while updating account:" + e);
			}
		}
	}

	@Override
	public void showChatWindow(Player player, int val)
	{
		String name = "data/html/mods/ChangePassword/" + getNpcId() + ".htm";
		if (val != 0)
			name = "data/html/mods/ChangePassword/" + getNpcId() + "-" + val + ".htm";
		
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(name);
		html.replace("%name%", player.getName());
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}