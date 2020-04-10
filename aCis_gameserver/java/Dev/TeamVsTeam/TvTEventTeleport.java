package Dev.TeamVsTeam;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;

/**
 * @author COMBATE
 *
 */
public class TvTEventTeleport implements Runnable
{
	/** Gives Noblesse to players */
	static L2Skill noblesse = SkillTable.getInstance().getInfo(1323, 1);
	/** The instance of the player to teleport */
	public Player _playerInstance;
	/** Coordinates of the spot to teleport to */
	public int[] _coordinates = new int[3];
	/** Admin removed this player from event */
	private boolean _adminRemove;
	
	/**
	 * Initialize the teleporter and start the delayed task
	 * @param playerInstance
	 * @param coordinates
	 * @param fastSchedule
	 * @param adminRemove
	 */
	public TvTEventTeleport(Player playerInstance, int[] coordinates, boolean fastSchedule, boolean adminRemove)
	{
		_playerInstance = playerInstance;
		_coordinates = coordinates;
		_adminRemove = adminRemove;
		
		// in config as seconds
		long delay = (TvTEvent.isStarted() ? Config.TVT_EVENT_RESPAWN_TELEPORT_DELAY : Config.TVT_EVENT_START_LEAVE_TELEPORT_DELAY) * 1000;
		
		if (fastSchedule)
			delay = 0;
		
		ThreadPool.schedule(this, delay);
	}
	
	/**
	 * The task method to teleport the player<br>
	 * 1. Unsummon pet if there is one 2. Remove all effects 3. Revive and full heal the player 4. Teleport the player 5. Broadcast status and user info
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		if (_playerInstance == null)
			return;
		
		Summon summon = _playerInstance.getSummon();
		
		if (summon != null)
			summon.unSummon(_playerInstance);
		
		for (L2Effect effect : _playerInstance.getAllEffects())
		{
			if (Config.TVT_EVENT_REMOVE_BUFFS && effect != null)
				effect.exit();
		}
		
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				_playerInstance.doRevive();
				_playerInstance.setCurrentHp(_playerInstance.getMaxHp());
				_playerInstance.setCurrentCp(_playerInstance.getMaxCp());
				_playerInstance.setCurrentMp(_playerInstance.getMaxMp());
				noblesse.getEffects(_playerInstance, _playerInstance);
				_playerInstance.teleportTo(_coordinates[0], _coordinates[1], _coordinates[2], 0);
			}
		}, 4000);
		
		if (TvTEvent.isStarted() && !_adminRemove)
			_playerInstance.setTeam(TvTEvent.getParticipantTeamId(_playerInstance.getName()) + 1);
		else
			_playerInstance.setTeam(0);
		
		_playerInstance.broadcastStatusUpdate();
		_playerInstance.broadcastUserInfo();
	}
}
