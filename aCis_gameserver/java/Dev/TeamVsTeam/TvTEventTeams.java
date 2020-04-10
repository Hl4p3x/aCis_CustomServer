package Dev.TeamVsTeam;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.sf.l2j.gameserver.model.actor.Player;

/**
 * @author COMBATE
 *
 */
public class TvTEventTeams
{
	/**
	 * The name of the team<br>
	 */
	private String _name;
	/**
	 * The team spot coordinated<br>
	 */
	private int[] _coordinates = new int[3];
	/**
	 * The points of the team<br>
	 */
	private short _points;
	/**
	 * Name and instance of all participated players in FastMap<br>
	 */
	private Map<String, Player> _participatedPlayers = new HashMap<>();
	/**
	 * Name of all participated players in Vector<br>
	 */
	private Vector<String> _participatedPlayerNames = new Vector<>();
	
	/**
	 * C'tor initialize the team
	 * @param name
	 * @param coordinates
	 */
	public TvTEventTeams(String name, int[] coordinates)
	{
		_name = name;
		_coordinates = coordinates;
		_points = 0;
	}
	
	/**
	 * Adds a player to the team
	 * @param playerInstance
	 * @return boolean
	 */
	public boolean addPlayer(Player playerInstance)
	{
		if (playerInstance == null)
			return false;
		
		synchronized (_participatedPlayers)
		{
			String playerName = playerInstance.getName();
			
			_participatedPlayers.put(playerName, playerInstance);
			
			if (!_participatedPlayerNames.contains(playerName))
				_participatedPlayerNames.add(playerName);
		}
		
		return true;
	}
	
	/**
	 * Removes a player from the team
	 * @param playerName
	 */
	public void removePlayer(String playerName)
	{
		synchronized (_participatedPlayers)
		{
			_participatedPlayers.remove(playerName);
			_participatedPlayerNames.remove(playerName);
		}
	}
	
	/**
	 * Increases the points of the team<br>
	 */
	public void increasePoints()
	{
		_points++;
	}
	
	/**
	 * Cleanup the team and make it ready for adding players again<br>
	 */
	public void cleanMe()
	{
		_participatedPlayers.clear();
		_participatedPlayerNames.clear();
		_participatedPlayers = new HashMap<>();
		_participatedPlayerNames = new Vector<>();
		_points = 0;
	}
	
	/**
	 * Is given player in this team?
	 * @param playerName
	 * @return boolean
	 */
	public boolean containsPlayer(String playerName)
	{
		boolean containsPlayer;
		
		synchronized (_participatedPlayers)
		{
			containsPlayer = _participatedPlayerNames.contains(playerName);
		}
		
		return containsPlayer;
	}
	
	/**
	 * Returns the name of the team
	 * @return String
	 */
	public String getName()
	{
		return _name;
	}
	
	/**
	 * Returns the coordinates of the team spot
	 * @return int[]
	 */
	public int[] getCoordinates()
	{
		return _coordinates;
	}
	
	/**
	 * Returns the points of the team
	 * @return short
	 */
	public short getPoints()
	{
		return _points;
	}
	
	/**
	 * Returns name and instance of all participated players in FastMap
	 * @return Map<String, Player>
	 */
	public Map<String, Player> getParticipatedPlayers()
	{
		Map<String, Player> participatedPlayers = null;
		
		synchronized (_participatedPlayers)
		{
			participatedPlayers = _participatedPlayers;
		}
		
		return participatedPlayers;
	}
	
	/**
	 * Returns name of all participated players in Vector
	 * @return Vector<String>
	 */
	public Vector<String> getParticipatedPlayerNames()
	{
		Vector<String> participatedPlayerNames = null;
		
		synchronized (_participatedPlayers)
		{
			participatedPlayerNames = _participatedPlayerNames;
		}
		
		return participatedPlayerNames;
	}
	
	/**
	 * Returns player count of this team
	 * @return int
	 */
	public int getParticipatedPlayerCount()
	{
		int participatedPlayerCount;
		
		synchronized (_participatedPlayers)
		{
			participatedPlayerCount = _participatedPlayers.size();
		}
		
		return participatedPlayerCount;
	}
}
