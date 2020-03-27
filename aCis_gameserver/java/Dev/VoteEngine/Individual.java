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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class Individual
{
	public static void giveReward(Player player)
	{
		player.getInventory().addItem("DelayItem", 6673, 25, player, null);
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(6673).addNumber(25));
		

		/*if (!player.getMemos().containsKey("lastVoteReward"))
		{
			player.removeSkill(9999, false);
			return;
		}
		if (System.currentTimeMillis() - player.getMemos().getLong("lastVoteReward") > 12*1000*60*60)
		{
			player.removeSkill(9999, false);
			return;
		}
		
		player.addSkill(SkillTable.getInstance().getInfo(9999, 1), false);
		ThreadPool.schedule(() -> player.removeSkill(9999, false), player.getMemos().getLong("lastVoteReward") + (12*1000*60*60) - System.currentTimeMillis());*/
	}
	
	public static synchronized boolean hasVotedTopzone(Player player)
	{
		try
		{
			final URL obj = new URL(String.format("https://api.l2topzone.com/v1/vote?token=%s&ip=%s", Config.TOPZONE_SERVER_API_KEY, player.getClient().getConnection().getInetAddress().getHostAddress()));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(5000);
			con.setConnectTimeout(1000);
			con.addRequestProperty("User-Agent", "L2TopZone");
			final int responseCode = con.getResponseCode();
			
			if (responseCode == 200)
			{
				final StringBuilder sb = new StringBuilder();
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{
						sb.append(inputLine);
					}
				}

				for(String splitted : sb.toString().split(":"))
				{
					if(splitted.contains("isVoted") && splitted.contains("true"))
						return true;
				}
				
				return sb.toString().substring(12).contains("true");
			}
			
			con.disconnect();
		}
		catch (Exception e)
		{
			System.out.println("There was a problem on getting vote status on Topzone for player: "	+	player.getName());
			return false;
		}
		
		return false;
	}
	
	public static synchronized boolean hasVotedHopzone(Player player)
	{
		try
		{
			final URL obj = new URL(String.format("http://api.hopzone.net/lineage2/vote?token=%s&ip_address=%s", Config.HOPZONE_SERVER_API_KEY, player.getClient().getConnection().getInetAddress().getHostAddress()));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla/4.76");
			con.setRequestMethod("GET");
			con.setConnectTimeout(5000);
			final int responseCode = con.getResponseCode();
			if (responseCode == 200)
			{
				final StringBuilder sb = new StringBuilder();
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{
						sb.append(inputLine);
					}
				}
				
				return Boolean.parseBoolean(substringBetween(sb.toString(), "\"voted:\"", "\"hopzoneServerTime\"").trim());
			}
			
			con.disconnect();
		}
		catch (Exception e)
		{
			System.out.println("There was a problem on getting vote status on Hopzone for player: "+player.getName());
		}
		
		return false;
	}
	
	public static final boolean votedNet(Player player)
	{
		try
		{
			final URL obj = new URL(String.format("https://l2network.eu/index.php?a=in&u=%s&ipc=%s", Config.API_NETWORK, player.getClient().getConnection().getInetAddress().getHostAddress()));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestProperty("User-Agent", "L2Network");
			con.setConnectTimeout(5000);
			
			final int responseCode = con.getResponseCode();
			if (responseCode == 200)
			{
				final StringBuilder sb = new StringBuilder();
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{
						sb.append(inputLine);
					}
				}
				return sb.toString().equals("1");
			}
		}
		catch (Exception e)
		{
			System.out.println("There was a problem on getting vote status on NetWork for player: "+player.getName());
		}
		return false;
	}
	
	
	private static String substringBetween(final String str, final String open, final String close)
	{
		if (str == null || open == null || close == null)
			return null;
		
		final int start = str.indexOf(open);
		if (start != -1)
		{
			final int end = str.indexOf(close, start + open.length());
			if (end != -1)
				return str.substring(start + open.length(), end);
		}
		
		return null;
	}
	
	public static boolean isAccountTookTheReward(Player player)
	{
		// Time restriction
		long lastVoteReward = getLastVoteReward(player);
		if(System.currentTimeMillis() - lastVoteReward < (1000*60*60*12))
			return true;
		
		List<Long> _accountCharacters = new ArrayList<>();
		getLastVoteRewardsOfPlayerCharacters(player, _accountCharacters);
		for(long lastVoteRewardFromCharAccount : _accountCharacters)
		{
			if(System.currentTimeMillis() - lastVoteRewardFromCharAccount < (1000*60*60*12))
				return true;
		}
		
		List<Long> _IPCharacters = new ArrayList<>();
		getLastVoteRewardsOfPlayerIP(player, _IPCharacters);
		for(long lastVoteRewardFromIPChar : _IPCharacters)
		{
			if(System.currentTimeMillis() - lastVoteRewardFromIPChar < (1000*60*60*12))
				return true;
		}
		
		List<Long> _HWIDCharacters = new ArrayList<>();
		getLastVoteRewardsOfPlayerHWID(player, _HWIDCharacters);
		for(long lastVoteRewardFromHWIDChar : _HWIDCharacters)
		{
			if(System.currentTimeMillis() - lastVoteRewardFromHWIDChar < (1000*60*60*12))
				return true;
		}

		return false;
	}
	
	@SuppressWarnings("resource")
	private static long getLastVoteReward(Player activeChar)
	{
		long lastVoteReward = 0;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT lastVoteReward FROM characters WHERE obj_Id=?");
    		stm.setInt(1, activeChar.getObjectId());
    		ResultSet rset = stm.executeQuery();
    		
    		if(rset.next()) {
    			lastVoteReward = rset.getLong("lastVoteReward");
    		}
			
			rset.close();
			stm.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
		}
		
		return lastVoteReward;
	}
	
	@SuppressWarnings("resource")
	private static void getLastVoteRewardsOfPlayerIP(Player activeChar, List<Long> _test)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT lastVoteReward FROM characters WHERE lastip=?");
    		stm.setString(1, activeChar.getIP());
    		ResultSet rset = stm.executeQuery();
    		
    		while(rset.next()) {
    			_test.add(rset.getLong("lastVoteReward"));
    		}
			
			rset.close();
			stm.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
		}
	}
	
	@SuppressWarnings("resource")
	private static void getLastVoteRewardsOfPlayerHWID(Player activeChar, List<Long> _test)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT lastVoteReward FROM characters WHERE lasthwid=?");
    		stm.setString(1, activeChar.getHWID());
    		ResultSet rset = stm.executeQuery();
    		
    		while(rset.next()) {
    			_test.add(rset.getLong("lastVoteReward"));
    		}
			
			rset.close();
			stm.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
		}
	}
	
	@SuppressWarnings("resource")
	private static void getLastVoteRewardsOfPlayerCharacters(Player activeChar, List<Long> _test)
	{
		for(int _objectId : activeChar.getAccountChars().keySet())
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement stm = con.prepareStatement("SELECT lastVoteReward FROM characters WHERE obj_Id=?");
	    		stm.setInt(1, _objectId);
	    		ResultSet rset = stm.executeQuery();
	    		
	    		while(rset.next()) {
	    			_test.add(rset.getLong("lastVoteReward"));
	    		}
				
				rset.close();
				stm.close();
				con.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			finally
			{
			}
		}
	}
	
	public static boolean cdInTopList(Player player, String variable)
	{
		return Long.parseLong(Cd(player, variable, true)) > System.currentTimeMillis();
	}
	
	@SuppressWarnings("resource")
	public static String Cd(Player player, String variable, boolean returnInTimestamp)
	{
		long CdMs = 0;
		long voteDelay = 43200000L;
		
		PreparedStatement statement = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT "+variable+" FROM characters WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());
			
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				CdMs = rset.getLong(variable);
			}
			
			if((CdMs + voteDelay) < System.currentTimeMillis())
				CdMs = System.currentTimeMillis() - voteDelay;
			
			rset.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(statement != null)
					statement.close();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		
		if(returnInTimestamp)
			return String.valueOf(CdMs+voteDelay);
		
		Date resultdate = new Date(CdMs + voteDelay);
		return sdf.format(resultdate);
	}
}
