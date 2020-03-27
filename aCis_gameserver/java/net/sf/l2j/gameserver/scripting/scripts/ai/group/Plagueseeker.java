package net.sf.l2j.gameserver.scripting.scripts.ai.group;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.commons.concurrent.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.enums.ScriptEventType;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.type.BossZone;
import net.sf.l2j.gameserver.network.serverpackets.Earthquake;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;
import net.sf.l2j.gameserver.network.serverpackets.SpecialCamera;
import net.sf.l2j.gameserver.network.serverpackets.StopMove;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

/**
 * @author Gabia
 *
 */
public class Plagueseeker extends L2AttackableAIScript
{
	 private static final BossZone PLAGUESEEKER_LAIR = ZoneManager.getInstance().getZoneById(150000, BossZone.class);
	    private static Npc Plagueseeker = null;
	    private long _lastAttack = 0L;
	    private static final int PLAGUESEEKER = 20798;
	    private static final int[] helpersIds = new int[]
	    {
	        21083,
	        21088,
	        21649
	    };
	    private static final List<Integer> healerClassIds = Arrays.asList(15, 16, 29, 30, 42, 43, 97, 105, 112, 115, 136, 116);
	    private static List<Npc> helpers = new CopyOnWriteArrayList<>();
	   
	    public Plagueseeker()
	    {
	        super("ai/group");
	    }
	    
	    @Override
	    protected void registerNpcs()
	    {
	    	addEventIds(PLAGUESEEKER, ScriptEventType.ON_ATTACK, ScriptEventType.ON_SPAWN, ScriptEventType.ON_KILL);
	    	addEventIds(helpersIds, ScriptEventType.ON_KILL);
	    }
	    


		
	    
	    public String onAttack(Npc npc, Player player, int damage, boolean isPet, L2Skill skill)
	    {
	        if (Plagueseeker != null && !Plagueseeker.isInvul())
	            _lastAttack = System.currentTimeMillis();
	       
	        return super.onAttack(npc, player, damage, skill);
	    }
	    
	    @Override
	    public String onSpawn(Npc npc)
	    {
	        Plagueseeker = npc;
	        Plagueseeker.setIsInvul(true);
	        paralyzeAll(true);
	        startSpecialCamera(true);
	        return super.onSpawn(npc);
	    }
	   
	    public String onKill(Npc npc, Player killer, boolean isPet)
	    {
	        // raid killed
	        if (npc.getNpcId() == PLAGUESEEKER)
	            clear();
	       
	        // minion killed
	        else
	        {
	            cancelQuestTimer("self_destruct", npc, null);
	            helpers.remove(npc);
	        }
	        return super.onKill(npc, killer);
	    }
	   
	    private void clear()
	    {
	        // cancel tasks
	        cancelQuestTimer("action_task", Plagueseeker, null);
	        cancelQuestTimer("inactivity_task", Plagueseeker, null);
	        cancelQuestTimer("mass_kill_cin", Plagueseeker, null);
	        cancelQuestTimer("mass_kill", Plagueseeker, null);
	       
	        // clear data
	        helpers.clear();
	        _lastAttack = 0L;
	       
	    }
	   
	    @Override
	    public String onAdvEvent(String event, Npc npc, Player player)
	    {
	        if (event.contains("camera"))
	            broadcastCamera(event);
	       
	        else if (event.equalsIgnoreCase("self_destruct"))
	            // suicide skill, damage nearby units
	            npc.doCast(SkillTable.getInstance().getInfo(4143, 10));
	       
	        else if (event.equalsIgnoreCase("mass_kill_cin"))
	        {
	            paralyzeAll(true);
	            startSpecialCamera(true);
	        }
	       
	        else if (event.equalsIgnoreCase("mass_kill"))
	        {
	            int killed = 0;
	            for (Creature creature : PLAGUESEEKER_LAIR.getCharacters())
	            {
	                // stop if 10 players killed
	                if (killed == 10)
	                    break;
	               
	                if (!(creature instanceof Player) || creature.isDead())
	                    continue;
	               
	                creature.doDie(Plagueseeker);
	                killed++;
	            }
	        }
	        else if (event.equalsIgnoreCase("action_task"))
	        {
	            final int rnd = Rnd.get(100);
	            // checking if boss have lower than 50% hp and mass kill never happened. if not, kill 10 players instant
	            if (Plagueseeker.isScriptValue(0) && bossHp() < 0.5)
	            {
	
	                
	                startQuestTimerAtFixedRate("mass_kill_cin", npc, null, 1000);
	                
	                
	                Plagueseeker.setScriptValue(1);
	            }
	           
	            // 20% chance to debuff 1 healer
	            else if (rnd <= 20)
	            {
	               
	                final Player healer = getAvailableHealer();
	                if (healer != null)
	                {
	                    final L2Skill effect = getRandomEffect();
	                    if (effect != null)
	                        effect.getEffects(healer, healer);
	                }
	            }
	            // 5% chance
	            else if (rnd <= 25)
	            {
	                // if boss have lower than 40% hp spawn 10 mobs for help
	                if (bossHp() < 0.4)
	                    spawnHelpers();
	               
	            }
	            // 5% chance
	            else if (rnd <= 30)
	            {
	                // instant kill
	                final Player pl = getRandomPlayerInRadius(700);
	                if (pl != null)
	                    pl.doDie(Plagueseeker);
	            }
	        }
	        else if (event.equalsIgnoreCase("inactivity_task"))
	        {
	            if (_lastAttack + 900000 < System.currentTimeMillis())
	            {
	                PLAGUESEEKER_LAIR.oustAllPlayers();
	                clear();
	                Plagueseeker.deleteMe();
	            }
	        }
	        return super.onAdvEvent(event, npc, player);
	    }
	   
	    /**
	     * @return the percentage of the current hp
	     */
	    private static float bossHp()
	    {
	        return ((float) Plagueseeker.getCurrentHp()) / Plagueseeker.getMaxHp();
	    }
	   
	    private void spawnHelpers()
	    {
	        for (int i = 0; i < 10; i++)
	        {
	            if (helpers.size() > 9)
	                break;
	           
	            final int npcId = helpersIds[Rnd.get(helpersIds.length)];
	            final Npc npc = addSpawn(npcId, Plagueseeker.getX() + Rnd.get(-200, 200), Plagueseeker.getY() + Rnd.get(-200, 200), Plagueseeker.getZ(), 0, false, 0, true);
	            ((Attackable) npc).setIsRaidMinion(true);
	           
	            helpers.add(npc);
	           
	            final Player victim = getRandomPlayer(npc);
	            if (victim != null)
	                attack(((Attackable) npc), victim);
	           
	            // suicide after 10 sec

	            startQuestTimerAtFixedRate("self_destruct", npc, null, 10000);
	            
	            
	        }
	       
	    }
	   
	    /**
	     * @param radius
	     * @return random player
	     */
	    private static Player getRandomPlayerInRadius(int radius)
	    {
	        return Plagueseeker.getKnownTypeInRadius(Player.class, radius).stream().filter(player -> !player.isDead()).findFirst().orElse(null);
	    }
	   
	    private static L2Skill getRandomEffect()
	    {
	        final int rnd = Rnd.get(100);
	        // Curse of Doom
	        if (rnd < 20)
	            return SkillTable.getInstance().getInfo(1336, 1);
	        // Curse Fear
	        else if (rnd < 35)
	            return SkillTable.getInstance().getInfo(1169, 14);
	        // Cancel
	        else if (rnd < 45)
	            return SkillTable.getInstance().getInfo(1056, 12);
	        // stun (Armor Crush) 50%
	        else
	            return SkillTable.getInstance().getInfo(362, 1);
	       
	    }
	   
	    private static Player getAvailableHealer()
	    {
	        return (Player) PLAGUESEEKER_LAIR.getCharacters().stream().filter(creature -> creature instanceof Player && isHealer((Player) creature) && !creature.isDead()).findFirst().orElse(null);
	    }
	   
	    /**
	     * @param player
	     * @return true if the player is healer
	     */
	    private static boolean isHealer(Player player)
	    {
	        return healerClassIds.contains(player.getClassId().getId());
	    }
	   
	    private void runTasks()
	    {
	        paralyzeAll(false);
	        Plagueseeker.setIsInvul(false);
	        

			
			startQuestTimerAtFixedRate("action_task", Plagueseeker, null, 2000);
			
			startQuestTimerAtFixedRate("inactivity_task", Plagueseeker, null, 900000);
			
			
	
	    }
	   
	    private static void paralyzeAll(boolean val)
	    {
	        for (Creature creature : PLAGUESEEKER_LAIR.getCharacters())
	        {
	            creature.setIsParalyzed(val);
	            if (val)
	                creature.broadcastPacket(new StopMove(creature));
	        }
	       
	    }
	   
	    private void startSpecialCamera(boolean halfHp)
	    {
	        if (halfHp)
	        {
	        	startQuestTimer("mass_kill_camera_1", Plagueseeker, null, 1000);
	            startQuestTimer("mass_kill_camera_2", Plagueseeker, null, 5900);
	            startQuestTimer("mass_kill_camera_3", Plagueseeker, null, 10800);
	            startQuestTimer("mass_kill_camera_4", Plagueseeker, null, 11700);
	            startQuestTimer("mass_kill_camera_5", Plagueseeker, null, 12600);
	           
	            // kill them
	            startQuestTimer("mass_kill", Plagueseeker, null, 13500);
	           
	            startQuestTimer("mass_kill_camera_6", Plagueseeker, null, 13500);
	            startQuestTimer("mass_kill_camera_7", Plagueseeker, null, 14400);
	            startQuestTimer("mass_kill_camera_8", Plagueseeker, null, 15300);
	        }
	        else
	        {
	            startQuestTimer("camera_1", Plagueseeker, null, 1000);
	            startQuestTimer("camera_2", Plagueseeker, null, 3400);
	            startQuestTimer("camera_3", Plagueseeker, null, 5800);
	            startQuestTimer("camera_4", Plagueseeker, null, 8200);
	            startQuestTimer("camera_5", Plagueseeker, null, 10600);
	            startQuestTimer("camera_6", Plagueseeker, null, 12200);
	            startQuestTimer("camera_7", Plagueseeker, null, 13800);
	            startQuestTimer("camera_8", Plagueseeker, null, 15400);
	            startQuestTimer("camera_9", Plagueseeker, null, 17000);
	            startQuestTimer("camera_10", Plagueseeker, null, 18600);
	            startQuestTimer("camera_11", Plagueseeker, null, 20200);
	            startQuestTimer("camera_12", Plagueseeker, null, 21800);
	        }
	    }
	   
	    private void broadcastCamera(String event)
	    {
	        if (event.equalsIgnoreCase("camera_1"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 400, 0, 25, 2500, 2500, 0, 0, 0, 0));
	        else if (event.equalsIgnoreCase("camera_2"))
	        {
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 400, 0, 15, 2500, 2500, 0, 0, 0, 0));
	            PLAGUESEEKER_LAIR.broadcastPacket(new ExShowScreenMessage("Your text here", 2500, SMPOS.TOP_CENTER, false));
	        }
	        else if (event.equalsIgnoreCase("camera_3"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 400, 0, 10, 2500, 2500, 0, 0, 0, 0));
	        else if (event.equalsIgnoreCase("camera_4"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 750, 0, 20, 2500, 2500, 0, 0, 0, 0));
	        else if (event.equalsIgnoreCase("camera_5"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 750, 50, 20, 1700, 1700, 0, 0, 1, 0));
	        else if (event.equalsIgnoreCase("camera_6"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 750, 100, 20, 1700, 1700, 0, 0, 1, 0));
	        else if (event.equalsIgnoreCase("camera_7"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 750, 150, 20, 1700, 1700, 0, 0, 1, 0));
	        else if (event.equalsIgnoreCase("camera_8"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 750, 200, 20, 1700, 1700, 0, 0, 1, 0));
	        else if (event.equalsIgnoreCase("camera_9"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 750, 250, 20, 1700, 1700, 0, 0, 1, 0));
	        else if (event.equalsIgnoreCase("camera_10"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 750, 300, 20, 1700, 1700, 0, 0, 1, 0));
	        else if (event.equalsIgnoreCase("camera_11"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 750, 350, 20, 1700, 1700, 0, 0, 1, 0));
	        else if (event.equalsIgnoreCase("camera_12"))
	        {
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 750, 0, 20, 1700, 1700, 0, 0, 0, 0));
	            ThreadPool.schedule(() -> runTasks(), 1700);
	        }
	       
	        else if (event.equalsIgnoreCase("mass_kill_camera_1"))
	        {
	            Plagueseeker.setIsInvul(true);
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 450, 0, 20, 5000, 5000, 0, 0, 1, 0));
	        }
	       
	        else if (event.equalsIgnoreCase("mass_kill_camera_2"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 750, 0, 50, 5000, 5000, 0, 0, 1, 0));
	       
	        else if (event.equalsIgnoreCase("mass_kill_camera_3"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 1100, 50, 50, 1000, 1000, 0, 0, 0, 0));
	       
	        else if (event.equalsIgnoreCase("mass_kill_camera_4"))
	        {
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 700, 100, 50, 1000, 1000, 0, 0, 0, 0));
	            PLAGUESEEKER_LAIR.broadcastPacket(new Earthquake(Plagueseeker.getX(), Plagueseeker.getY(), Plagueseeker.getZ(), 50, 4));
	        }
	       
	        else if (event.equalsIgnoreCase("mass_kill_camera_5"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 700, 150, 50, 1000, 1000, 0, 0, 0, 0));
	       
	        else if (event.equalsIgnoreCase("mass_kill_camera_6"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 700, 200, 50, 1000, 1000, 0, 0, 0, 0));
	       
	        else if (event.equalsIgnoreCase("mass_kill_camera_7"))
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 700, 250, 50, 1000, 1000, 0, 0, 0, 0));
	       
	        else if (event.equalsIgnoreCase("mass_kill_camera_8"))
	        {
	            PLAGUESEEKER_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 700, 300, 50, 1000, 1000, 0, 0, 0, 0));
	            paralyzeAll(false);
	            Plagueseeker.setIsInvul(false);
	        }
	    }
}
