/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.OpenUrl;
import net.sf.l2j.util.Mysql;

import Dev.VoteEngine.Individual;


/**
 * @author Anarchy
 */
public class VoteManager extends Folk
{
	public VoteManager(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		
		
		if (command.equals("getReward"))
		{
			if (Individual.isAccountTookTheReward(player))
			{
				player.sendMessage("12 hours have not passed since you last received the reward.");
				return;
			}
			
			// DO NOT CHECK SITES (Fast Fix)
			if(Config.ALLOW_GIVE_ITEM_WITHOUT_CHECK_OF_TOPSITES)
			{
				Mysql.set("UPDATE characters SET lastVoteReward=? WHERE obj_Id=?",System.currentTimeMillis(),player.getObjectId());
				Individual.giveReward(player);
				player.sendMessage("Thank you for voting.");
				return;
			}
			
			if ((Config.ALLOW_HOPZONE_VOTE_REWARD && !Individual.hasVotedHopzone(player)) || (Config.ALLOW_TOPZONE_VOTE_REWARD && !Individual.hasVotedTopzone(player)) || (Config.ALLOW_NETWORK_VOTE_REWARD && !Individual.votedNet(player)))
			{
				player.sendMessage("You didn't vote on all sites. Press Vote to open the sites on your browser.");
				return;
			}
			
			//player.getMemos().set("lastVoteReward", System.currentTimeMillis());
			//player.getMemos().storeMe();
			Mysql.set("UPDATE characters SET lastVoteReward=? WHERE obj_Id=?",System.currentTimeMillis(),player.getObjectId());
			Individual.giveReward(player);
			player.sendMessage("Thank you for voting.");
		}
		else if (command.equals("vote"))
		{
			player.sendPacket(new OpenUrl("https://l2topzone.com/vote/id/"+Config.TOPZONE_SERVER_ID));
			player.sendPacket(new OpenUrl("https://l2network.eu/index.php?a=in&u=L2Eola"));
			//player.sendPacket(new OpenUrl("http://l2top.co/vote/server/L2Eola"));
			player.sendPacket(new OpenUrl("https://top.l2jbrasil.com/index.php?a=in&u=L2EolaValzerothx500"));
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(getObjectId());
		htm.setFile("data/html/mods/votemanager/"+getNpcId()+(val == 0 ? "" : "-"+val)+".htm");
		String status = Individual.cdInTopList(player, "lastVoteReward") ? Individual.Cd(player, "lastVoteReward", false) : "You can vote";
		htm.replace("%status%", status);
		htm.replace("%objectId%", getObjectId()+"");
		player.sendPacket(htm);
	}
	
    @Override
	public String getHtmlPath(int npcId, int val)
    {
        String pom = "";
        if (val == 0)
            pom = "" + npcId;
        else
            pom = npcId + "-" + val;
                
        return "data/html/mods/votemanager/" + pom + ".htm";
    }
}
