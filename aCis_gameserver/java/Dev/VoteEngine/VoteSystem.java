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
package Dev.VoteEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;

import hwid.HwidConfig;


/**
 * @author Anarchy
 *
 */
public abstract class VoteSystem implements Runnable
{
	private static List<VoteSystem> voteSystems = new ArrayList<>();
	
	protected int votesDiff;
	protected int boxes;
	protected Map<Integer, Integer> rewards;
	protected int checkMins;
	protected int lastVotes = 0;
	private Map<String, Integer> playerIps = new HashMap<>();
	
	public static void initialize()
	{
		System.out.println("Vote System: Loaded");
		
		if (Config.ALLOW_TOPZONE_VOTE_REWARD)
			voteSystems.add(new Topzone(Config.TOPZONE_VOTES_DIFFERENCE, HwidConfig.PROTECT_WINDOWS_COUNT, Config.TOPZONE_REWARD, Config.TOPZONE_REWARD_CHECK_TIME));
		if (Config.ALLOW_HOPZONE_VOTE_REWARD)
			voteSystems.add(new Hopzone(Config.HOPZONE_VOTES_DIFFERENCE, HwidConfig.PROTECT_WINDOWS_COUNT, Config.HOPZONE_REWARD, Config.HOPZONE_REWARD_CHECK_TIME));
		if(Config.ALLOW_NETWORK_VOTE_REWARD)
			voteSystems.add(new NetWorkzone(Config.NETWORK_VOTES_DIFFERENCE, HwidConfig.PROTECT_WINDOWS_COUNT, Config.NETWORK_REWARD, Config.NETWORK_REWARD_CHECK_TIME));
	}
	
	public static VoteSystem getVoteSystem(String name)
	{
		for (VoteSystem vs : voteSystems)
			if (vs.getSiteName().equals(name))
				return vs;
		
		return null;
	}
	
	public VoteSystem(int votesDiff, int boxes, Map<Integer, Integer> rewards, int checkMins)
	{
		this.votesDiff = votesDiff;
		this.boxes = boxes;
		this.rewards = rewards;
		this.checkMins = checkMins;
		
		lastVotes = getVotes();
		
		ThreadPool.scheduleAtFixedRate(this, checkMins*1000*60, checkMins*1000*60);
	}
	
	protected void reward()
	{
		int currentVotes = getVotes();
		
		if (currentVotes == -1)
		{
			System.out.println("There was a problem on getting server votes from "+getSiteName());
			return;
		}
		
		if (lastVotes == 0)
		{
			lastVotes = currentVotes;
			announce(getSiteName()+": Current vote count is "+currentVotes+". We need "+((lastVotes+votesDiff)-currentVotes)+" vote(s) for reward.");
			
			return;
		}
		
		if (currentVotes >= lastVotes+votesDiff)
		{
			Collection<Player> pls = World.getInstance().getPlayers();
			
			announce(getSiteName()+": Everyone has been rewarded! Thanks for voting.");
			announce(getSiteName()+": Current vote count is "+currentVotes+". We need "+votesDiff+" vote(s) for the next reward.");
			for (Player p : pls)
			{
				if (p.getClient() == null || p.getClient().isDetached()) // offline shops protection
					continue;
				
				boolean canReward = false;
				String pIp = p.getClient().getConnection().getInetAddress().getHostAddress();
				if (playerIps.containsKey(pIp))
				{
					int count = playerIps.get(pIp);
					if (count < boxes)
					{
						playerIps.remove(pIp);
						playerIps.put(pIp, count+1);
						canReward = true;
					}
				}
				else
				{
					canReward = true;
					playerIps.put(pIp, 1);
				}
				if (canReward)
					for (int i : rewards.keySet()) {
						p.addItem("Vote reward.", i, rewards.get(i), p, true);
						

					}
			}
			playerIps.clear();
			
			lastVotes = currentVotes;
		}
		else
			announce(getSiteName()+": Current vote count is "+currentVotes+". We need "+((lastVotes+votesDiff)-currentVotes)+" vote(s) for reward.");
	}
	
	private static void announce(String msg)
	{
		new CreatureSay(SayType.CRITICAL_ANNOUNCE, null, "" + msg);


	}
	
	public abstract int getVotes();
	public abstract String getSiteName();
}
