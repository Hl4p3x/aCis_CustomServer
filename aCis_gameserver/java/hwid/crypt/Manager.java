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
package hwid.crypt;

import hwid.hwidmanager.HWIDManager;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.network.GameClient;

/**
 * @author Flack
 */
public final class Manager
{
	protected static Logger _log = Logger.getLogger(Manager.class.getName());
	
	protected static String _logFile = "Manager";
	protected static String _logMainFile = "hwid_logs";
	protected static Manager _instance;
	protected static ScheduledFuture<?> _GGTask = null;
	protected static ConcurrentHashMap<String, Manager.InfoSet> _objects = new ConcurrentHashMap<>();
	
	public Manager()
	{
		//
	}
	
	public static Manager getInstance()
	{
		if (_instance == null)
		{
			System.out.println("- HWID Manager read successfully...");
			_instance = new Manager();
		}
		
		return _instance;
	}
	
	public class InfoSet
	{
		public String _playerName = "";
		public long _lastGGSendTime;
		public long _lastGGRecvTime;
		public int _attempts;
		public String _HWID = "";
		
		public InfoSet(String name, String HWID)
		{
			_playerName = name;
			_lastGGSendTime = System.currentTimeMillis();
			_lastGGRecvTime = _lastGGSendTime;
			_attempts = 0;
			_HWID = HWID;
		}
	}
	
	public void addPlayer(GameClient client)
	{
		int i = 1;
		HWIDManager.updateHWIDInfo(client, i++);
		_objects.put(client.getPlayerName(), new Manager.InfoSet(client.getPlayerName(), client.getHWID()));
	}

	public static void removePlayer(String name)
	{
		if (!_objects.containsKey(name))
		{
			if (Config.DEBUG_PATH)
				_log.warning("trying to remove player that non exists");
		}
		else
			_objects.remove(name);
	}
	
	public static int getCountByHWID(String HWID)
	{
		int result = 0;
		Iterator<InfoSet> var3 = _objects.values().iterator();
		
		while (var3.hasNext())
		{
			Manager.InfoSet object = var3.next();

			if (object._HWID.equals(HWID))
				++result;
		}

		return result;
	}
	
}
