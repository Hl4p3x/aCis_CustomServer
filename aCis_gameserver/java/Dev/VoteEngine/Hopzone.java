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
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import net.sf.l2j.Config;

/**
 * @author Anarchy
 *
 */
public class Hopzone extends VoteSystem
{
	public Hopzone(int votesDiff, int boxes, Map<Integer, Integer> rewards, int checkMins)
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
		
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try
		{
			URLConnection con = new URL(String.format("http://api.hopzone.net/lineage2/votes?token=%s", Config.HOPZONE_SERVER_API_KEY)).openConnection();
			con.addRequestProperty("User-Agent", "Mozilla/4.76");
			isr = new InputStreamReader(con.getInputStream());
			br = new BufferedReader(isr);
			
			String line;
			while ((line = br.readLine()) != null)
			{
				String[] tokens = line.split(",");
				votes = Integer.parseInt(tokens[1].substring(tokens[1].indexOf(":") + 1, tokens[1].length()));
			}
			
			br.close();
			isr.close();
		}
		catch (Exception e)
		{
			System.out.println("There was a problem on getting total votes from Hopzone.");
		}
		
		return votes;
	}
	

	@Override
	public String getSiteName()
	{
		return "Hopzone";
	}
}
