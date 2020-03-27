package net.sf.l2j.gameserver.scripting.scripts.ai.individual;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.commons.math.MathUtil;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.commons.util.StatsSet;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.SkillTable.FrequentSkill;
import net.sf.l2j.gameserver.data.manager.GrandBossManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.enums.ScriptEventType;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.GrandBoss;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.zone.type.BossZone;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.SpecialCamera;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * That AI is heavily based on Valakas/Baium scripts.<br>
 * It uses the 29019 dummy id in order to register it (addBoss and statsSet), but 3 different templates according the situation.
 */
public class Antharas extends L2AttackableAIScript
{
	private static final BossZone ANTHARAS_LAIR = ZoneManager.getInstance().getZoneById(110001, BossZone.class);
	
	private static final int[] ANTHARAS_IDS =
	{
		29066,
		29067,
		29068
	};
	
	public static final int ANTHARAS = 29019; // Dummy Antharas id used for status updates only.
	
	public static final byte DORMANT = 0; // No one has entered yet. Entry is unlocked.
	public static final byte WAITING = 1; // Someone has entered, triggering a 30 minute window for additional people to enter. Entry is unlocked.
	public static final byte FIGHTING = 2; // Antharas is engaged in battle, annihilating his foes. Entry is locked.
	public static final byte DEAD = 3; // Antharas has been killed. Entry is locked.
	
	private long _timeTracker = 0; // Time tracker for last attack on Antharas.
	private Player _actualVictim; // Actual target of Antharas.
	private final List<Npc> _monsters = new CopyOnWriteArrayList<>(); // amount of Antharas minions.
	
	private int _antharasId; // The current Antharas, used when server shutdowns.
	private L2Skill _skillRegen; // The regen skill used by Antharas.
	private int _minionTimer; // The timer used by minions in order to spawn.
	
	public Antharas()
	{
		super("ai/individual");
		
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(ANTHARAS);
		
		switch (GrandBossManager.getInstance().getBossStatus(ANTHARAS))
		{
			case DEAD: // Launch the timer to set DORMANT, or set DORMANT directly if timer expired while offline.
				long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
				if (temp > 0)
					startQuestTimer("antharas_unlock", null, null, temp);
				else
					GrandBossManager.getInstance().setBossStatus(ANTHARAS, DORMANT);
				break;
			
			case WAITING: // Launch beginning timer.
				startQuestTimer("beginning", null, null, Config.WAIT_TIME_ANTHARAS);
				break;
			
			case FIGHTING:
				final int loc_x = info.getInteger("loc_x");
				final int loc_y = info.getInteger("loc_y");
				final int loc_z = info.getInteger("loc_z");
				final int heading = info.getInteger("heading");
				final int hp = info.getInteger("currentHP");
				final int mp = info.getInteger("currentMP");
				
				// Update Antharas informations.
				updateAntharas();
				
				final Npc antharas = addSpawn(_antharasId, loc_x, loc_y, loc_z, heading, false, 0, false);
				GrandBossManager.getInstance().addBoss(ANTHARAS, (GrandBoss) antharas);
				
				antharas.setCurrentHpMp(hp, mp);
				antharas.setRunning();
				
				// stores current time for inactivity task.
				_timeTracker = System.currentTimeMillis();
				
				startQuestTimerAtFixedRate("regen_task", antharas, null, 60000);
				startQuestTimerAtFixedRate("skill_task", antharas, null, 2000);
				startQuestTimerAtFixedRate("minions_spawn", antharas, null, _minionTimer);
				break;
		}
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(ANTHARAS_IDS, ScriptEventType.ON_ATTACK, ScriptEventType.ON_SPAWN);
		addKillId(29066, 29067, 29068, 29069, 29070, 29071, 29072, 29073, 29074, 29075, 29076);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		// Regeneration && inactivity task
		if (name.equalsIgnoreCase("regen_task"))
		{
			// Inactivity task - 30min
			if (_timeTracker + 1800000 < System.currentTimeMillis())
			{
				// Set it dormant.
				GrandBossManager.getInstance().setBossStatus(ANTHARAS, DORMANT);
				
				// Drop all players from the zone.
				ANTHARAS_LAIR.oustAllPlayers();
				
				// Drop tasks.
				dropTimers(npc);
				
				// Delete current instance of Antharas.
				npc.deleteMe();
				return null;
			}
			_skillRegen.getEffects(npc, npc);
		}
		// Spawn cinematic, regen_task and choose of skill.
		else if (name.equalsIgnoreCase("spawn_1"))
			ANTHARAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 700, 13, -19, 0, 20000, 0, 0, 1, 0));
		else if (name.equalsIgnoreCase("spawn_2"))
		{
			npc.broadcastPacket(new SocialAction(npc, 1));
			ANTHARAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 700, 13, 0, 6000, 20000, 0, 0, 1, 0));
		}
		else if (name.equalsIgnoreCase("spawn_3"))
			ANTHARAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 3700, 0, -3, 0, 10000, 0, 0, 1, 0));
		else if (name.equalsIgnoreCase("spawn_4"))
		{
			npc.broadcastPacket(new SocialAction(npc, 2));
			ANTHARAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1100, 0, -3, 22000, 30000, 0, 0, 1, 0));
		}
		else if (name.equalsIgnoreCase("spawn_5"))
			ANTHARAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1100, 0, -3, 300, 7000, 0, 0, 1, 0));
		else if (name.equalsIgnoreCase("spawn_6"))
		{
			// stores current time for inactivity task.
			_timeTracker = System.currentTimeMillis();
			
			GrandBossManager.getInstance().setBossStatus(ANTHARAS, FIGHTING);
			npc.setIsInvul(false);
			npc.setRunning();
			
			startQuestTimerAtFixedRate("regen_task", npc, null, 60000);
			startQuestTimerAtFixedRate("skill_task", npc, null, 2000);
			startQuestTimerAtFixedRate("minions_spawn", npc, null, _minionTimer);
		}
		else if (name.equalsIgnoreCase("skill_task"))
			callSkillAI(npc);
		else if (name.equalsIgnoreCase("minions_spawn"))
		{
			boolean isBehemoth = Rnd.get(100) < 60;
			int mobNumber = isBehemoth ? 2 : 3;
			
			// Set spawn.
			for (int i = 0; i < mobNumber; i++)
			{
				if (_monsters.size() > 9)
					break;
				
				final int npcId = isBehemoth ? 29069 : Rnd.get(29070, 29076);
				final Npc dragon = addSpawn(npcId, npc.getX() + Rnd.get(-200, 200), npc.getY() + Rnd.get(-200, 200), npc.getZ(), 0, false, 0, true);
				((Monster) dragon).setMinion(true);
				
				_monsters.add(dragon);
				
				final Player victim = getRandomPlayer(dragon);
				if (victim != null)
					attack(((Attackable) dragon), victim);
				
				if (!isBehemoth)
					startQuestTimer("self_destruct", dragon, null, (_minionTimer / 3));
			}
		}
		else if (name.equalsIgnoreCase("self_destruct"))
		{
			L2Skill skill;
			switch (npc.getNpcId())
			{
				case 29070:
				case 29071:
				case 29072:
				case 29073:
				case 29074:
				case 29075:
					skill = SkillTable.getInstance().getInfo(5097, 1);
					break;
				default:
					skill = SkillTable.getInstance().getInfo(5094, 1);
			}
			npc.doCast(skill);
		}
		// Cinematic
		else if (name.equalsIgnoreCase("beginning"))
		{
			updateAntharas();
			
			final Npc antharas = addSpawn(_antharasId, 181323, 114850, -7623, 32542, false, 0, false);
			GrandBossManager.getInstance().addBoss(ANTHARAS, (GrandBoss) antharas);
			antharas.setIsInvul(true);
			
			// Launch the cinematic, and tasks (regen + skill).
			startQuestTimer("spawn_1", antharas, null, 16);
			startQuestTimer("spawn_2", antharas, null, 3016);
			startQuestTimer("spawn_3", antharas, null, 13016);
			startQuestTimer("spawn_4", antharas, null, 13216);
			startQuestTimer("spawn_5", antharas, null, 24016);
			startQuestTimer("spawn_6", antharas, null, 25916);
		}
		// spawn of Teleport Cube.
		else if (name.equalsIgnoreCase("die_1"))
		{
			addSpawn(31859, 177615, 114941, -7709, 0, false, 900000, false);
			startQuestTimer("remove_players", null, null, 900000);
		}
		else if (name.equalsIgnoreCase("antharas_unlock"))
			GrandBossManager.getInstance().setBossStatus(ANTHARAS, DORMANT);
		else if (name.equalsIgnoreCase("remove_players"))
			ANTHARAS_LAIR.oustAllPlayers();
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		npc.disableCoreAI(true);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onAttack(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc.isInvul())
			return null;
		
		if (attacker instanceof Playable)
		{
			// Curses
			if (testCursesOnAttack(npc, attacker))
				return null;
			
			// Refresh timer on every hit.
			_timeTracker = System.currentTimeMillis();
		}
		return super.onAttack(npc, attacker, damage, skill);
	}
	
	@Override
	public String onKill(Npc npc, Creature killer)
	{
		if (npc.getNpcId() == _antharasId)
		{
			// Drop tasks.
			dropTimers(npc);
			
			// Launch death animation.
			ANTHARAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1200, 20, -10, 10000, 13000, 0, 0, 0, 0));
			ANTHARAS_LAIR.broadcastPacket(new PlaySound(1, "BS01_D", npc));
			startQuestTimer("die_1", null, null, 8000);
			
			GrandBossManager.getInstance().setBossStatus(ANTHARAS, DEAD);
			
			long respawnTime = (long) Config.SPAWN_INTERVAL_ANTHARAS + Rnd.get(-Config.RANDOM_SPAWN_TIME_ANTHARAS, Config.RANDOM_SPAWN_TIME_ANTHARAS);
			respawnTime *= 3600000;
			
			startQuestTimer("antharas_unlock", null, null, respawnTime);
			
			StatsSet info = GrandBossManager.getInstance().getStatsSet(ANTHARAS);
			info.set("respawn_time", System.currentTimeMillis() + respawnTime);
			GrandBossManager.getInstance().setStatsSet(ANTHARAS, info);
		}
		else
		{
			cancelQuestTimers("self_destruct", npc);
			_monsters.remove(npc);
		}
		
		return super.onKill(npc, killer);
	}
	
	private void callSkillAI(Npc npc)
	{
		if (npc.isInvul() || npc.isCastingNow())
			return;
		
		// Pickup a target if no or dead victim. 10% luck he decides to reconsiders his target.
		if (_actualVictim == null || _actualVictim.isDead() || !npc.knows(_actualVictim) || Rnd.get(10) == 0)
			_actualVictim = getRandomPlayer(npc);
		
		// If result is still null, Antharas will roam. Don't go deeper in skill AI.
		if (_actualVictim == null)
		{
			if (Rnd.get(10) == 0)
			{
				final Location loc = npc.getMove().getDestination();
				loc.setUsingRandomOffset(npc.getPosition(), 1400);
				
				npc.getAI().setIntention(IntentionType.MOVE_TO, loc);
			}
			return;
		}
		
		final L2Skill skill = getRandomSkill(npc);
		
		// Cast the skill or follow the target.
		if (MathUtil.checkIfInRange((skill.getCastRange() < 600) ? 600 : skill.getCastRange(), npc, _actualVictim, true))
		{
			npc.getAI().setIntention(IntentionType.IDLE);
			npc.setTarget(_actualVictim);
			npc.doCast(skill);
		}
		else
			npc.getAI().setIntention(IntentionType.FOLLOW, _actualVictim, null);
	}
	
	/**
	 * Pick a random skill.<br>
	 * The use is based on current HPs ratio.
	 * @param npc Antharas
	 * @return a usable skillId
	 */
	private static L2Skill getRandomSkill(Npc npc)
	{
		final double hpRatio = npc.getCurrentHp() / npc.getMaxHp();
		
		// Find enemies surrounding Antharas.
		final int[] playersAround = getPlayersCountInPositions(1100, npc, false);
		
		if (hpRatio < 0.25)
		{
			if (Rnd.get(100) < 30)
				return FrequentSkill.ANTHARAS_MOUTH.getSkill();
			
			if (playersAround[1] >= 10 && Rnd.get(100) < 80)
				return FrequentSkill.ANTHARAS_TAIL.getSkill();
			
			if (playersAround[0] >= 10)
			{
				if (Rnd.get(100) < 40)
					return FrequentSkill.ANTHARAS_DEBUFF.getSkill();
				
				if (Rnd.get(100) < 10)
					return FrequentSkill.ANTHARAS_JUMP.getSkill();
			}
			
			if (Rnd.get(100) < 10)
				return FrequentSkill.ANTHARAS_METEOR.getSkill();
		}
		else if (hpRatio < 0.5)
		{
			if (playersAround[1] >= 10 && Rnd.get(100) < 80)
				return FrequentSkill.ANTHARAS_TAIL.getSkill();
			
			if (playersAround[0] >= 10)
			{
				if (Rnd.get(100) < 40)
					return FrequentSkill.ANTHARAS_DEBUFF.getSkill();
				
				if (Rnd.get(100) < 10)
					return FrequentSkill.ANTHARAS_JUMP.getSkill();
			}
			
			if (Rnd.get(100) < 7)
				return FrequentSkill.ANTHARAS_METEOR.getSkill();
		}
		else if (hpRatio < 0.75)
		{
			if (playersAround[1] >= 10 && Rnd.get(100) < 80)
				return FrequentSkill.ANTHARAS_TAIL.getSkill();
			
			if (playersAround[0] >= 10 && Rnd.get(100) < 10)
				return FrequentSkill.ANTHARAS_JUMP.getSkill();
			
			if (Rnd.get(100) < 5)
				return FrequentSkill.ANTHARAS_METEOR.getSkill();
		}
		else
		{
			if (playersAround[1] >= 10 && Rnd.get(100) < 80)
				return FrequentSkill.ANTHARAS_TAIL.getSkill();
			
			if (Rnd.get(100) < 3)
				return FrequentSkill.ANTHARAS_METEOR.getSkill();
		}
		
		if (Rnd.get(100) < 6)
			return FrequentSkill.ANTHARAS_BREATH.getSkill();
		
		if (Rnd.get(100) < 50)
			return FrequentSkill.ANTHARAS_NORMAL_ATTACK.getSkill();
		
		if (Rnd.get(100) < 5)
		{
			if (Rnd.get(100) < 50)
				return FrequentSkill.ANTHARAS_FEAR.getSkill();
			
			return FrequentSkill.ANTHARAS_SHORT_FEAR.getSkill();
		}
		
		return FrequentSkill.ANTHARAS_NORMAL_ATTACK_EX.getSkill();
	}
	
	/**
	 * Update Antharas informations depending about how much players joined the fight.<br>
	 * Used when server restarted and Antharas is fighting, or used while the cinematic occurs (after the 30min timer).
	 */
	private void updateAntharas()
	{
		final int playersNumber = ANTHARAS_LAIR.getAllowedPlayers().size();
		if (playersNumber < 45)
		{
			_antharasId = ANTHARAS_IDS[0];
			_skillRegen = SkillTable.getInstance().getInfo(4239, 1);
			_minionTimer = 180000;
		}
		else if (playersNumber < 63)
		{
			_antharasId = ANTHARAS_IDS[1];
			_skillRegen = SkillTable.getInstance().getInfo(4240, 1);
			_minionTimer = 150000;
		}
		else
		{
			_antharasId = ANTHARAS_IDS[2];
			_skillRegen = SkillTable.getInstance().getInfo(4241, 1);
			_minionTimer = 120000;
		}
	}
	
	/**
	 * Drop timers, meaning Antharas is dead or inactivity task occured.
	 * @param npc : The NPC to affect.
	 */
	private void dropTimers(Npc npc)
	{
		cancelQuestTimers("regen_task", npc);
		cancelQuestTimers("skill_task", npc);
		cancelQuestTimers("minions_spawn", npc);
		
		cancelQuestTimers("self_destruct");
		
		_monsters.forEach(Npc::deleteMe);
		_monsters.clear();
	}
}