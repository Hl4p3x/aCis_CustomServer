package hwid;

import hwid.crypt.Manager;
import hwid.hwidmanager.HWIDBan;
import hwid.hwidmanager.HWIDManager;
import hwid.utils.Util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.GameClient;
import net.sf.l2j.gameserver.network.serverpackets.ServerClose;
import net.sf.l2j.loginserver.crypt.BlowfishEngine;

public class Hwid
{
	protected static Logger _log = Logger.getLogger(Hwid.class.getName());
	private static byte[] _key = new byte[16];
	static byte version = 11;
	protected static ConcurrentHashMap<String, Manager.InfoSet> _objects = new ConcurrentHashMap<>();

	public static void Init()
	{
		HwidConfig.load();
		if (isProtectionOn())
		{
			HWIDBan.getInstance();
			HWIDManager.getInstance();
			Manager.getInstance();
		}
	}

	public static boolean isProtectionOn()
	{
		if (HwidConfig.ALLOW_GUARD_SYSTEM)
			return true;
		return false;
	}

	public static byte[] getKey(byte[] key)
	{
		// byte[] bfkey = FirstKey.SKBOX;
		byte[] bfkey =
		{
			110,
			36,
			2,
			15,
			-5,
			17,
			24,
			23,
			18,
			45,
			1,
			21,
			122,
			16,
			-5,
			12
		};
		try
		{
			BlowfishEngine bf = new BlowfishEngine();
			bf.init(true, bfkey);
			bf.processBlock(key, 0, _key, 0);
			bf.processBlock(key, 8, _key, 8);
		}
		catch (IOException e)
		{
			_log.info("Bad key!!!");
		}
		return _key;
	}
	
	public static void addPlayer(GameClient client)
	{
		if (isProtectionOn() && client != null)
			Manager.getInstance().addPlayer(client);
	}
	
	public static void removePlayer(GameClient client)
	{
		if (isProtectionOn() && client != null)
			Manager.removePlayer(client.getPlayerName());
	}
	
	public static boolean checkVerfiFlag(GameClient client, int flag)
	{
		boolean result = true;
		int fl = Integer.reverseBytes(flag);
		if (fl == -1)
			// Log.add("Error Verify Flag|" + client.toString(), _logFile);
			return false;
		else if (fl == 1342177280)
			// Log.add("Error get net data client|" + client.toString() + "|DEBUG INFO:" + fl, _logFile);
			return false;
		else
		{
			if ((fl & 1) != 0)
				// Log.add("Sniffer detect |" + client.toString() + "|DEBUG INFO:" + fl, _logFile);
				result = false;
			
			// Console CMD
			if ((fl & 16) != 0)
				// Log.add("Sniffer detect2 |" + client.toString() + "|DEBUG INFO:" + fl, _logFile);
				result = false;
			
			if ((fl & 268435456) != 0)
				// Log.add("L2ext detect |" + client.toString() + "|DEBUG INFO:" + fl, _logFile);
				result = false;
			
			return result;
		}
	}
	
	public static int dumpData(int _id, int position, GameClient pi)
	{
		int value = 0;
		position = position > 4 ? 5 : position;
		boolean isIdZero = false;
		if (_id == 0)
			isIdZero = true;
		// Log.add("Cannot read DumpId|Target:" + _positionName[position] + "|" + pi.toString() + "|DEBUG INFO:" + _id, _logFile);
		switch (position)
		{
			case 0:
				// IG
				if (_id != 1435233386)
				{
					if (!isIdZero)
					{
						// Log.add(_positionName[position] + " was found|" + pi.toString() + "|DEBUG INFO:" + _id, _logFile);
					}
					value = 1/* HwidConfig.PROTECT_PENALTY_IG */;
				}
				break;
			case 1:
				// Console CMD
				if (_id != 16)
				{
					if (!isIdZero)
					{
						// Log.add(_positionName[position] + " was found|" + pi.toString() + "|DEBUG INFO:" + _id, _logFile);
					}
					value = 2/* HwidConfig.PROTECT_PENALTY_CONSOLE_CMD */;
				}
				break;
			case 2:
			case 3:
			case 4:
				// check debuger (0xСС) or hook (0xE9)
				int code = _id & 0xFF000000;
				if (code == 0xCC)
				{
					// Log.add("Attempts!!! Debuger was found|" + pi.toString() + "|DEBUG INFO:" + _id, _logFile);
				}
				// L2phx (connect, send, recv)
				if (code == 0xE9)
					// Log.add(_positionName[position] + " was found|" + pi.toString() + "|DEBUG INFO:" + _id, _logFile);
					value = 3/* HwidConfig.PROTECT_PENALTY_L2PHX */;
				break;
			default:
				value = 0;
				break;
		}
		return value;
	}
	
	public static int calcPenalty(byte[] data, GameClient pi)
	{
		int sum = -1;
		if (Util.verifyChecksum(data, 0, data.length))
		{
			ByteBuffer buf = ByteBuffer.wrap(data, 0, data.length - 4);
			sum = 0;
			int lenPenalty = (data.length - 4) / 4;
			for (int i = 0; i < lenPenalty; i++)
				sum += Hwid.dumpData(buf.getInt(), i, pi);
		}
		return sum;
	}
	
	public static boolean CheckHWIDs(GameClient client, int LastError1, int LastError2)
	{
		boolean resultHWID = false;
		boolean resultLastError = false;
		String HWID = client.getHWID();
		
		if (HWID.equalsIgnoreCase("fab800b1cc9de379c8046519fa841e6"))
			// Log.add("HWID:" + HWID + "|is empty, account:" + client.getLoginName() + "|IP: " + client.toString(), _logFile);
			if (HwidConfig.PROTECT_KICK_WITH_EMPTY_HWID)
				resultHWID = true;
		
		if (LastError1 != 0)
			// Log.add("LastError(HWID):" + LastError1 + "|" + Util.LastErrorConvertion(Integer.valueOf(LastError1)) + "|isn\'t empty, " + client.toString(), _logFile);
			if (HwidConfig.PROTECT_KICK_WITH_LASTERROR_HWID)
				resultLastError = true;
		
		return resultHWID || resultLastError;
	}
	
	public static String fillHex(int data, int digits)
	{
		String number = Integer.toHexString(data);
		
		for (int i = number.length(); i < digits; ++i)
			number = "0" + number;
		
		return number;
	}
	
	public static String ExtractHWID(byte[] _data)
	{
		if (!Util.verifyChecksum(_data, 0, _data.length))
			return null;
		StringBuilder resultHWID = new StringBuilder();
		
		for (int i = 0; i < _data.length - 8; ++i)
			resultHWID.append(fillHex(_data[i] & 255, 2));
		
		return resultHWID.toString();
	}
	
	public static boolean doAuthLogin(GameClient client, byte[] data, String loginName)
	{
		if (!isProtectionOn())
			return true;
		client.setLoginName(loginName);
		String fullHWID = ExtractHWID(data);
		
		if (fullHWID == null)
		{
			_log.info("AuthLogin CRC Check Fail! May be BOT or unprotected client! Client IP: " + client.toString());
			client.close(ServerClose.STATIC_PACKET);
			return false;
		}

		int LastError1 = ByteBuffer.wrap(data, 16, 4).getInt();
		if (CheckHWIDs(client, LastError1, 0))
		{
			_log.info("HWID error, look protection_logs.txt file, from IP: " + client.toString());
			client.close(ServerClose.STATIC_PACKET);
			return false;
		}
		if (HWIDBan.getInstance().checkFullHWIDBanned(client))
		{
			_log.info("Client " + client + " is banned. Kicked! |HWID: " + client.getHWID() + " IP: " + client.toString());
			client.close(ServerClose.STATIC_PACKET);
		}
		
		int VerfiFlag = ByteBuffer.wrap(data, 40, 4).getInt();
		if (!checkVerfiFlag(client, VerfiFlag))
			return false;
		return true;
	}
	
	public static void doDisconection(GameClient client)
	{
		removePlayer(client);
	}
	
	public static boolean checkPlayerWithHWID(GameClient client, int playerID, String playerName)
	{
		if (!isProtectionOn())
			return true;

		client.setPlayerName(playerName);
		client.setPlayerId(playerID);
		
		if (HwidConfig.PROTECT_WINDOWS_COUNT != 0)
		{
			final int count = Manager.getCountByHWID(client.getHWID());
			if (count > HwidConfig.PROTECT_WINDOWS_COUNT && count > HWIDManager.getAllowedWindowsCount(client))
			{
				_log.info("Multi windows: " + client.toString());
				client.close(ServerClose.STATIC_PACKET);
				return false;
			}
		}
		addPlayer(client);
		return true;

	}

	public static void nopath(GameClient client)
	{
		_log.info("Client " + client + " is no have path kick: " + client.getHWID() + " IP: " + client.toString());
		client.close(ServerClose.STATIC_PACKET);
	}
	
	public static void enterlog(Player player, GameClient client)
	{
		if (HwidConfig.ALLOW_GUARD_SYSTEM && HwidConfig.ENABLE_CONSOLE_LOG)
			_log.info("HWID : [" + client.getHWID() + "], character: [" + player.getName() + "] PlayerId: [" + player.getObjectId() + " ]");
	}
	
	public static void waitSecs(int i)
	{
		try
		{
			Thread.sleep(i * 1000);
		}
		catch (InterruptedException ie)
		{
			ie.printStackTrace();
		}
	}
}