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
import java.util.Map;

import net.sf.l2j.Config;

/**
 * @author Anarchy
 *
 */
public class Topzone extends VoteSystem
{
	public Topzone(int votesDiff, int boxes, Map<Integer, Integer> rewards, int checkMins)
	{
		super(votesDiff, boxes, rewards, checkMins);
	}
	
	@Override
	public void run()
	{
		reward();
	}

	@Override
	public int getVotes()
	{
		int votes = -1;
		
		try
		{
			final URL obj = new URL(String.format("http://l2topzone.com/tv.php?id=%s", Config.TOPZONE_SERVER_ID));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestProperty("User-Agent", "L2TopZone");
			con.setConnectTimeout(5000);
			final int responseCode = con.getResponseCode();
			if (responseCode == 200)
			{
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					final StringBuilder sb = new StringBuilder();
					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{
						sb.append(inputLine);
					}
					
					return Integer.parseInt(sb.toString().trim());
				}
			}
			
			con.disconnect();
		}
		catch (Exception e)
		{
			System.out.println("There was a problem on getting total votes from Topzone.");
		}
		
		return votes;
	}

	@Override
	public String getSiteName()
	{
		return "Topzone";
	}
}
