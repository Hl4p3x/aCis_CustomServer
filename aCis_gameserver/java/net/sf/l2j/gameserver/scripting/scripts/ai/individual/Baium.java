package net.sf.l2j.gameserver.scripting.scripts.ai.individual;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.math.MathUtil;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.commons.util.StatsSet;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.manager.GrandBossManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.enums.ScriptEventType;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.GrandBoss;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.location.SpawnLocation;
import net.sf.l2j.gameserver.model.zone.type.BossZone;
import net.sf.l2j.gameserver.network.serverpackets.Earthquake;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * Following animations are handled in that time tempo :
 * <ul>
 * <li>wake(2), 0-13 secs</li>
 * <li>neck(3), 14-24 secs.</li>
 * <li>roar(1), 25-37 secs.</li>
 * </ul>
 * Waker's sacrifice is handled between neck and roar animation.
 */
public class Baium extends L2AttackableAIScript
{
	private static final BossZone BAIUM_LAIR = ZoneManager.getInstance().getZoneById(110002, BossZone.class);
	
	private static final int STONE_BAIUM = 29025;
	private static final int LIVE_BAIUM = 29020;
	private static final int ARCHANGEL = 29021;
	
	// Baium status tracking
	public static final byte ASLEEP = 0; // baium is in the stone version, waiting to be woken up. Entry is unlocked.
	public static final byte AWAKE = 1; // baium is awake and fighting. Entry is locked.
	public static final byte DEAD = 2; // baium has been killed and has not yet spawned. Entry is locked.
	
	// Archangels spawns
	private static final SpawnLocation[] ANGEL_LOCATION =
	{
		new SpawnLocation(114239, 17168, 10080, 63544),
		new SpawnLocation(115780, 15564, 10080, 13620),
		new SpawnLocation(114880, 16236, 10080, 5400),
		new SpawnLocation(115168, 17200, 10080, 0),
		new SpawnLocation(115792, 16608, 10080, 0)
	};
	
	private Creature _actualVictim;
	private long _timeTracker = 0;
	private final List<Npc> _minions = new ArrayList<>(5);
	
	public Baium()
	{
		super("ai/individual");
		
		// Quest NPC starter initialization
		addStartNpc(STONE_BAIUM);
		addTalkId(STONE_BAIUM);
		
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(LIVE_BAIUM);
		final int status = GrandBossManager.getInstance().getBossStatus(LIVE_BAIUM);
		
		if (status == DEAD)
		{
			// load the unlock date and time for baium from DB
			long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
			if (temp > 0)
			{
				// The time has not yet expired. Mark Baium as currently locked (dead).
				startQuestTimer("baium_unlock", null, null, temp);
			}
			else
			{
				// The time has expired while the server was offline. Spawn the stone-baium as ASLEEP.
				addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0, false);
				GrandBossManager.getInstance().setBossStatus(LIVE_BAIUM, ASLEEP);
			}
		}
		else if (status == AWAKE)
		{
			final int loc_x = info.getInteger("loc_x");
			final int loc_y = info.getInteger("loc_y");
			final int loc_z = info.getInteger("loc_z");
			final int heading = info.getInteger("heading");
			final int hp = info.getInteger("currentHP");
			final int mp = info.getInteger("currentMP");
			
			final Npc baium = addSpawn(LIVE_BAIUM, loc_x, loc_y, loc_z, heading, false, 0, false);
			GrandBossManager.getInstance().addBoss((GrandBoss) baium);
			
			baium.setCurrentHpMp(hp, mp);
			baium.setRunning();
			
			// start monitoring baium's inactivity
			_timeTracker = System.currentTimeMillis();
			startQuestTimerAtFixedRate("baium_despawn", baium, null, 60000);
			startQuestTimerAtFixedRate("skill_range", baium, null, 2000);
			
			// Spawns angels
			for (SpawnLocation loc : ANGEL_LOCATION)
			{
				Npc angel = addSpawn(ARCHANGEL, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), false, 0, true);
				((Monster) angel).setMinion(true);
				angel.setRunning();
				_minions.add(angel);
			}
			
			// Angels AI
			startQuestTimerAtFixedRate("angels_aggro_reconsider", null, null, 5000);
		}
		else
			addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0, false);
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(LIVE_BAIUM, ScriptEventType.ON_ATTACK, ScriptEventType.ON_KILL, ScriptEventType.ON_SPAWN);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("skill_range"))
		{
			callSkillAI(npc);
		}
		else if (name.equalsIgnoreCase("baium_neck"))
		{
			npc.broadcastPacket(new SocialAction(npc, 3));
		}
		else if (name.equalsIgnoreCase("sacrifice_waker"))
		{
			if (player != null)
			{
				// If player is far of Baium, teleport him back.
				if (!MathUtil.checkIfInShortRadius(300, player, npc, true))
				{
					BAIUM_LAIR.allowPlayerEntry(player, 10);
					player.teleportTo(115929, 17349, 10077, 0);
				}
				
				// 60% to die.
				if (Rnd.get(100) < 60)
					player.doDie(npc);
			}
		}
		else if (name.equalsIgnoreCase("baium_roar"))
		{
			// Roar animation
			npc.broadcastPacket(new SocialAction(npc, 1));
			
			// Spawn angels
			for (SpawnLocation loc : ANGEL_LOCATION)
			{
				Npc angel = addSpawn(ARCHANGEL, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), false, 0, true);
				((Monster) angel).setMinion(true);
				angel.setRunning();
				_minions.add(angel);
			}
			
			// Angels AI
			startQuestTimerAtFixedRate("angels_aggro_reconsider", null, null, 5000);
		}
		else if (name.equalsIgnoreCase("baium_move"))
		{
			npc.setIsInvul(false);
			npc.setRunning();
			
			// Start monitoring baium's inactivity and activate the AI
			_timeTracker = System.currentTimeMillis();
			
			startQuestTimerAtFixedRate("baium_despawn", npc, null, 60000);
			startQuestTimerAtFixedRate("skill_range", npc, null, 2000);
		}
		// despawn the live baium after 30 minutes of inactivity
		// also check if the players are cheating, having pulled Baium outside his zone...
		else if (name.equalsIgnoreCase("baium_despawn"))
		{
			if (_timeTracker + 1800000 < System.currentTimeMillis())
			{
				// despawn the live-baium
				npc.deleteMe();
				
				// Unspawn angels
				for (Npc minion : _minions)
				{
					minion.getSpawn().setRespawnState(false);
					minion.deleteMe();
				}
				_minions.clear();
				
				addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0, false); // spawn stone-baium
				GrandBossManager.getInstance().setBossStatus(LIVE_BAIUM, ASLEEP); // Baium isn't awaken anymore
				BAIUM_LAIR.oustAllPlayers();
				cancelQuestTimers("baium_despawn");
			}
			else if ((_timeTracker + 300000 < System.currentTimeMillis()) && (npc.getCurrentHp() / npc.getMaxHp() < 0.75))
			{
				npc.setTarget(npc);
				npc.doCast(SkillTable.getInstance().getInfo(4135, 1));
			}
			else if (!BAIUM_LAIR.isInsideZone(npc))
				npc.teleportTo(116033, 17447, 10104, 0);
		}
		else if (name.equalsIgnoreCase("baium_unlock"))
		{
			GrandBossManager.getInstance().setBossStatus(LIVE_BAIUM, ASLEEP);
			addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0, false);
		}
		else if (name.equalsIgnoreCase("angels_aggro_reconsider"))
		{
			boolean updateTarget = false; // Update or no the target
			
			for (Npc minion : _minions)
			{
				Attackable angel = ((Attackable) minion);
				Creature victim = angel.getMostHated();
				
				if (Rnd.get(100) < 10) // Chaos time
					updateTarget = true;
				else
				{
					if (victim != null) // Target is a unarmed player ; clean aggro.
					{
						if (victim instanceof Player && victim.getActiveWeaponInstance() == null)
						{
							angel.stopHating(victim); // Clean the aggro number of previous victim.
							updateTarget = true;
						}
					}
					else
						// No target currently.
						updateTarget = true;
				}
				
				if (updateTarget)
				{
					Creature newVictim = getRandomTarget(minion);
					if (newVictim != null && victim != newVictim)
					{
						angel.addDamageHate(newVictim, 0, 10000);
						angel.getAI().setIntention(IntentionType.ATTACK, newVictim);
					}
				}
			}
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		if (GrandBossManager.getInstance().getBossStatus(LIVE_BAIUM) == ASLEEP)
		{
			GrandBossManager.getInstance().setBossStatus(LIVE_BAIUM, AWAKE);
			
			final Npc baium = addSpawn(LIVE_BAIUM, npc, false, 0, false);
			baium.setIsInvul(true);
			
			GrandBossManager.getInstance().addBoss((GrandBoss) baium);
			
			// First animation
			baium.broadcastPacket(new SocialAction(baium, 2));
			baium.broadcastPacket(new Earthquake(baium.getX(), baium.getY(), baium.getZ(), 40, 10));
			
			// Second animation, waker sacrifice, followed by angels spawn, third animation and finally movement.
			startQuestTimer("baium_neck", baium, null, 13000);
			startQuestTimer("sacrifice_waker", baium, player, 24000);
			startQuestTimer("baium_roar", baium, null, 28000);
			startQuestTimer("baium_move", baium, null, 35000);
			
			// Delete the statue.
			npc.deleteMe();
		}
		return null;
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
		cancelQuestTimers("baium_despawn");
		npc.broadcastPacket(new PlaySound(1, "BS01_D", npc));
		
		// spawn the "Teleportation Cubic" for 15 minutes (to allow players to exit the lair)
		addSpawn(29055, 115203, 16620, 10078, 0, false, 900000, false);
		
		long respawnTime = (long) Config.SPAWN_INTERVAL_BAIUM + Rnd.get(-Config.RANDOM_SPAWN_TIME_BAIUM, Config.RANDOM_SPAWN_TIME_BAIUM);
		respawnTime *= 3600000;
		
		GrandBossManager.getInstance().setBossStatus(LIVE_BAIUM, DEAD);
		startQuestTimer("baium_unlock", null, null, respawnTime);
		
		StatsSet info = GrandBossManager.getInstance().getStatsSet(LIVE_BAIUM);
		info.set("respawn_time", System.currentTimeMillis() + respawnTime);
		GrandBossManager.getInstance().setStatsSet(LIVE_BAIUM, info);
		
		// Unspawn angels.
		for (Npc minion : _minions)
		{
			minion.getSpawn().setRespawnState(false);
			minion.deleteMe();
		}
		_minions.clear();
		
		// Clean Baium AI
		cancelQuestTimers("skill_range");
		
		// Clean angels AI
		cancelQuestTimers("angels_aggro_reconsider");
		
		return super.onKill(npc, killer);
	}
	
	/**
	 * This method allows to select a random target, and is used both for Baium and angels.
	 * @param npc to check.
	 * @return the random target.
	 */
	private Creature getRandomTarget(Npc npc)
	{
		int npcId = npc.getNpcId();
		List<Creature> result = new ArrayList<>();
		
		for (Creature obj : npc.getKnownType(Creature.class))
		{
			if (obj instanceof Player)
			{
				if (obj.isDead() || !(GeoEngine.getInstance().canSeeTarget(npc, obj)))
					continue;
				
				if (((Player) obj).isGM() && !((Player) obj).getAppearance().isVisible())
					continue;
				
				if (npcId == ARCHANGEL && ((Player) obj).getActiveWeaponInstance() == null)
					continue;
				
				result.add(obj);
			}
			// Case of Archangels, they can hit Baium.
			else if (obj instanceof GrandBoss && npcId == ARCHANGEL)
				result.add(obj);
		}
		
		// If there's no players available, Baium and Angels are hitting each other.
		if (result.isEmpty())
		{
			if (npcId == LIVE_BAIUM) // Case of Baium. Angels should never be without target.
			{
				for (Npc minion : _minions)
					result.add(minion);
			}
		}
		
		return (result.isEmpty()) ? null : Rnd.get(result);
	}
	
	/**
	 * The personal casting AI for Baium.
	 * @param npc baium, basically...
	 */
	private void callSkillAI(Npc npc)
	{
		if (npc.isInvul() || npc.isCastingNow())
			return;
		
		// Pickup a target if no or dead victim. If Baium was hitting an angel, 50% luck he reconsiders his target. 10% luck he decides to reconsiders his target.
		if (_actualVictim == null || _actualVictim.isDead() || !npc.knows(_actualVictim) || (_actualVictim instanceof Monster && Rnd.get(10) < 5) || Rnd.get(10) == 0)
			_actualVictim = getRandomTarget(npc);
		
		// If result is null, return directly.
		if (_actualVictim == null)
			return;
		
		final L2Skill skill = SkillTable.getInstance().getInfo(getRandomSkill(npc), 1);
		
		// Adapt the skill range, because Baium is fat.
		if (MathUtil.checkIfInRange((int) (skill.getCastRange() + npc.getCollisionRadius()), npc, _actualVictim, true))
		{
			npc.getAI().setIntention(IntentionType.IDLE);
			npc.setTarget(skill.getId() == 4135 ? npc : _actualVictim);
			npc.doCast(skill);
		}
		else
			npc.getAI().setIntention(IntentionType.FOLLOW, _actualVictim, null);
	}
	
	/**
	 * Pick a random skill through that list.<br>
	 * If Baium feels surrounded, he will use AoE skills. Same behavior if he is near 2+ angels.<br>
	 * @param npc baium
	 * @return a usable skillId
	 */
	private static int getRandomSkill(Npc npc)
	{
		// Baium's selfheal. It happens exceptionaly.
		if (npc.getCurrentHp() / npc.getMaxHp() < 0.1)
		{
			if (Rnd.get(10000) == 777) // His lucky day.
				return 4135;
		}
		
		int skill = 4127; // Default attack if nothing is possible.
		final int chance = Rnd.get(100); // Remember, it's 0 to 99, not 1 to 100.
		
		// If Baium feels surrounded or see 2+ angels, he unleashes his wrath upon heads :).
		if (getPlayersCountInRadius(600, npc, false) >= 20 || npc.getKnownTypeInRadius(Monster.class, 600).size() >= 2)
		{
			if (chance < 25)
				skill = 4130;
			else if (chance >= 25 && chance < 50)
				skill = 4131;
			else if (chance >= 50 && chance < 75)
				skill = 4128;
			else if (chance >= 75 && chance < 100)
				skill = 4129;
		}
		else
		{
			if (npc.getCurrentHp() / npc.getMaxHp() > 0.75)
			{
				if (chance < 10)
					skill = 4128;
				else if (chance >= 10 && chance < 20)
					skill = 4129;
			}
			else if (npc.getCurrentHp() / npc.getMaxHp() > 0.5)
			{
				if (chance < 10)
					skill = 4131;
				else if (chance >= 10 && chance < 20)
					skill = 4128;
				else if (chance >= 20 && chance < 30)
					skill = 4129;
			}
			else if (npc.getCurrentHp() / npc.getMaxHp() > 0.25)
			{
				if (chance < 10)
					skill = 4130;
				else if (chance >= 10 && chance < 20)
					skill = 4131;
				else if (chance >= 20 && chance < 30)
					skill = 4128;
				else if (chance >= 30 && chance < 40)
					skill = 4129;
			}
			else
			{
				if (chance < 10)
					skill = 4130;
				else if (chance >= 10 && chance < 20)
					skill = 4131;
				else if (chance >= 20 && chance < 30)
					skill = 4128;
				else if (chance >= 30 && chance < 40)
					skill = 4129;
			}
		}
		return skill;
	}
}