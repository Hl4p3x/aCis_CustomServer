package net.sf.l2j.gameserver.scripting.scripts.ai.individual;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.commons.util.StatsSet;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.manager.GrandBossManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.data.xml.DoorData;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.enums.ScriptEventType;
import net.sf.l2j.gameserver.enums.skills.AbnormalEffect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.GrandBoss;
import net.sf.l2j.gameserver.model.zone.type.BossZone;
import net.sf.l2j.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.Earthquake;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillCanceled;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.SpecialCamera;
import net.sf.l2j.gameserver.scripting.scripts.ai.L2AttackableAIScript;

public class Frintezza extends L2AttackableAIScript
{
    private static final BossZone ZONE = ZoneManager.getInstance().getZoneById(110012, BossZone.class);
    
    private static final int[][] SKILLS =
    {
        { 5015,1 },
        { 5015,4 },
        { 5015,2 },
        { 5015,5 },
        { 5018,1 },
        { 5016,1 },
        { 5015,3 },
        { 5015,6 },
        { 5018,2 },
        { 5019,1 },
        { 5016,1 }
    };
    
    private static final int[][] MOBS =
    {
        { 18328,172894,-76019,-5107,243 },
        { 18328,174095,-77279,-5107,16216 },
        { 18328,174111,-74833,-5107,49043 },
        { 18328,175344,-76042,-5107,32847 },
        { 18330,173489,-76227,-5134,63565 },
        { 18330,173498,-75724,-5107,58498 },
        { 18330,174365,-76745,-5107,22424 },
        { 18330,174570,-75584,-5107,31968 },
        { 18330,174613,-76179,-5107,31471 },
        { 18332,173620,-75981,-5107,4588 },
        { 18332,173630,-76340,-5107,62454 },
        { 18332,173755,-75613,-5107,57892 },
        { 18332,173823,-76688,-5107,2411 },
        { 18332,174000,-75411,-5107,54718 },
        { 18332,174487,-75555,-5107,33861 },
        { 18332,174517,-76471,-5107,21893 },
        { 18332,174576,-76122,-5107,31176 },
        { 18332,174600,-75841,-5134,35927 },
        { 18329,173481,-76043,-5107,61312 },
        { 18329,173539,-75678,-5107,59524 },
        { 18329,173584,-76386,-5107,3041 },
        { 18329,173773,-75420,-5107,51115 },
        { 18329,173777,-76650,-5107,12588 },
        { 18329,174585,-76510,-5107,21704 },
        { 18329,174623,-75571,-5107,40141 },
        { 18329,174744,-76240,-5107,29202 },
        { 18329,174769,-75895,-5107,29572 },
        { 18333,173861,-76011,-5107,383 },
        { 18333,173872,-76461,-5107,8041 },
        { 18333,173898,-75668,-5107,51856 },
        { 18333,174422,-75689,-5107,42878 },
        { 18333,174460,-76355,-5107,27311 },
        { 18333,174483,-76041,-5107,30947 },
        { 18331,173515,-76184,-5107,6971 },
        { 18331,173516,-75790,-5134,3142 },
        { 18331,173696,-76675,-5107,6757 },
        { 18331,173766,-75502,-5134,60827 },
        { 18331,174473,-75321,-5107,37147 },
        { 18331,174493,-76505,-5107,34503 },
        { 18331,174568,-75654,-5134,41661 },
        { 18331,174584,-76263,-5107,31729 },
        { 18339,173892,-81592,-5123,50849 },
        { 18339,173958,-81820,-5123,7459 },
        { 18339,174128,-81805,-5150,21495 },
        { 18339,174245,-81566,-5123,41760 },
        { 18334,173264,-81529,-5072,1646 },
        { 18334,173265,-81656,-5072,441 },
        { 18334,173267,-81889,-5072,0 },
        { 18334,173271,-82015,-5072,65382 },
        { 18334,174867,-81655,-5073,32537 },
        { 18334,174868,-81890,-5073,32768 },
        { 18334,174869,-81485,-5073,32315 },
        { 18334,174871,-82017,-5073,33007 },
        { 18335,173074,-80817,-5107,8353 },
        { 18335,173128,-82702,-5107,5345 },
        { 18335,173181,-82544,-5107,65135 },
        { 18335,173191,-80981,-5107,6947 },
        { 18335,174859,-80889,-5134,24103 },
        { 18335,174924,-82666,-5107,38710 },
        { 18335,174947,-80733,-5107,22449 },
        { 18335,175096,-82724,-5107,42205 },
        { 18336,173435,-80512,-5107,65215 },
        { 18336,173440,-82948,-5107,417 },
        { 18336,173443,-83120,-5107,1094 },
        { 18336,173463,-83064,-5107,286 },
        { 18336,173465,-80453,-5107,174 },
        { 18336,173465,-83006,-5107,2604 },
        { 18336,173468,-82889,-5107,316 },
        { 18336,173469,-80570,-5107,65353 },
        { 18336,173469,-80628,-5107,166 },
        { 18336,173492,-83121,-5107,394 },
        { 18336,173493,-80683,-5107,0 },
        { 18336,173497,-80510,-5134,417 },
        { 18336,173499,-82947,-5107,0 },
        { 18336,173521,-83063,-5107,316 },
        { 18336,173523,-82889,-5107,128 },
        { 18336,173524,-80627,-5134,65027 },
        { 18336,173524,-83007,-5107,0 },
        { 18336,173526,-80452,-5107,64735 },
        { 18336,173527,-80569,-5134,65062 },
        { 18336,174602,-83122,-5107,33104 },
        { 18336,174604,-82949,-5107,33184 },
        { 18336,174609,-80514,-5107,33234 },
        { 18336,174609,-80684,-5107,32851 },
        { 18336,174629,-80627,-5107,33346 },
        { 18336,174632,-80570,-5107,32896 },
        { 18336,174632,-83066,-5107,32768 },
        { 18336,174635,-82893,-5107,33594 },
        { 18336,174636,-80456,-5107,32065 },
        { 18336,174639,-83008,-5107,33057 },
        { 18336,174660,-80512,-5107,33057 },
        { 18336,174661,-83121,-5107,32768 },
        { 18336,174663,-82948,-5107,32768 },
        { 18336,174664,-80685,-5107,32676 },
        { 18336,174687,-83008,-5107,32520 },
        { 18336,174691,-83066,-5107,32961 },
        { 18336,174692,-80455,-5107,33202 },
        { 18336,174692,-80571,-5107,32768 },
        { 18336,174693,-80630,-5107,32994 },
        { 18336,174693,-82889,-5107,32622 },
        { 18337,172837,-82382,-5107,58363 },
        { 18337,172867,-81123,-5107,64055 },
        { 18337,172883,-82495,-5107,64764 },
        { 18337,172916,-81033,-5107,7099 },
        { 18337,172940,-82325,-5107,58998 },
        { 18337,172946,-82435,-5107,58038 },
        { 18337,172971,-81198,-5107,14768 },
        { 18337,172992,-81091,-5107,9438 },
        { 18337,173032,-82365,-5107,59041 },
        { 18337,173064,-81125,-5107,5827 },
        { 18337,175014,-81173,-5107,26398 },
        { 18337,175061,-82374,-5107,43290 },
        { 18337,175096,-81080,-5107,24719 },
        { 18337,175169,-82453,-5107,37672 },
        { 18337,175172,-80972,-5107,32315 },
        { 18337,175174,-82328,-5107,41760 },
        { 18337,175197,-81157,-5107,27617 },
        { 18337,175245,-82547,-5107,40275 },
        { 18337,175249,-81075,-5107,28435 },
        { 18337,175292,-82432,-5107,42225 },
        { 18338,173014,-82628,-5107,11874 },
        { 18338,173033,-80920,-5107,10425 },
        { 18338,173095,-82520,-5107,49152 },
        { 18338,173115,-80986,-5107,9611 },
        { 18338,173144,-80894,-5107,5345 },
        { 18338,173147,-82602,-5107,51316 },
        { 18338,174912,-80825,-5107,24270 },
        { 18338,174935,-80899,-5107,18061 },
        { 18338,175016,-82697,-5107,39533 },
        { 18338,175041,-80834,-5107,25420 },
        { 18338,175071,-82549,-5107,39163 },
        { 18338,175154,-82619,-5107,36345 }
    };
    
    private static final int[][] PORTRAITS = 
    {
        { 29049,175876,-88713 },
        { 29049,172608,-88702 },
        { 29048,175833,-87165 },
        { 29048,172634,-87165 }
    };
    
    private static final int[][] DEMONS = 
    {
        { 29051,175876,-88713,-4972,28205 },
        { 29051,172608,-88702,-4972,64817 },
        { 29050,175833,-87165,-4972,35048 },
        { 29050,172634,-87165,-4972,57730 }
    };
    
    public static final int FRINTEZZA = 29045;
    public static final int SCARLET1 = 29046;
    public static final int SCARLET2 = 29047;
    public static final int CUBE = 29061;
    
    public static final byte DORMANT = 0;
    public static final byte WAITING = 1;
    public static final byte FIGHTING = 2;
    public static final byte DEAD = 3;
    
    private Set<Npc> _roomMobs = ConcurrentHashMap.newKeySet();
    private GrandBoss _frintezza;
    private int _transformed;
    private int _notice;
    private int _currentSong;
    private long _lastAction;
    
    private static final int[] NPCS =
    {
        SCARLET1, SCARLET2, FRINTEZZA, 18328, 18329, 18330, 18331, 18332, 18333,
        18334, 18335, 18336, 18337, 18338, 18339, 29048, 29049, 29050, 29051, 32011
    };
    
    @Override
    protected void registerNpcs()
    {
        addEventIds(NPCS, ScriptEventType.ON_ATTACK, ScriptEventType.ON_KILL);
    }
    
    public Frintezza()
    {
        super("ai/individual");
        
        final int status = GrandBossManager.getInstance().getBossStatus(FRINTEZZA);
        if (status == DEAD)
        {
            final long temp = GrandBossManager.getInstance().getStatsSet(FRINTEZZA).getLong("respawn_time") - System.currentTimeMillis();
            if (temp > 0)
                startQuestTimer("frintezza_unlock", null, null, temp);
            else
                GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
        }
        else if (status != DORMANT)
            GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
    }
    
    @Override
    public String onTimer(String name, Npc npc, Player player)
    {
        if (name.equalsIgnoreCase("beginning"))
        {
            closeDoors();
            _notice = 35;
            for (int i = 0; i <= 17; i++)
                _roomMobs.add(addSpawn(MOBS[i][0], MOBS[i][1], MOBS[i][2], MOBS[i][3], MOBS[i][4], false, 0, false));
            
            ZONE.broadcastPacket(new CreatureSay(0, SayType.SHOUT, "Hall Alarm Device", "Intruders! Sound the alarm!"));
            
            startQuestTimer("notice", null, null, 60000);
            startQuestTimer("frintezza_despawn", null, null, 60000);
        }
        else if (name.equalsIgnoreCase("notice"))
        {
            if (_notice == 0)
            {
                ZONE.broadcastPacket(new CreatureSay(0, SayType.SHOUT, "Frintezza Gatekeeper", "Time limit exceeded, challenge failed!"));
                ZONE.oustAllPlayers();
                
                cancelQuestTimers("notice");
                cancelQuestTimers("frintezza_despawn");
                
                deleteAllMobs();
                closeDoors();
                
                GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
            }
            
            _notice--;
            ZONE.broadcastPacket(new ExShowScreenMessage(_notice + " minute(s) remaining.", 10000));
        }
        else if (name.equalsIgnoreCase("waiting"))
        {
            startQuestTimer("close", null, null, 27000);
            startQuestTimer("camera_1", null, null, 30000);
            ZONE.broadcastPacket(new Earthquake(174232, -88020, -5116, 45, 27));
        }
        else if (name.equalsIgnoreCase("frintezza_unlock"))
            GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
        else if (name.equalsIgnoreCase("remove_players"))
            ZONE.oustAllPlayers();
        else if (name.equalsIgnoreCase("close"))
            closeDoors();
        else if (name.equalsIgnoreCase("spawn_minion"))
        {
            if (npc != null && !npc.isDead())
            {
                Npc mob = addSpawn(npc.getNpcId() + 2, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0, false);
                
                startQuestTimer("action", mob, null, 200);
                startQuestTimer("spawn_minion", npc, null, 18000);
            }
        }
        else if (name.equalsIgnoreCase("action"))
            ZONE.broadcastPacket(new SocialAction(npc, 1));
        else if (name.equalsIgnoreCase("camera_1"))
        {
            GrandBossManager.getInstance().setBossStatus(FRINTEZZA, FIGHTING);
            
            Npc dummy = addSpawn(29052, 174232, -88020, -5116, 49151, false, 0, false);
            dummy.setIsInvul(true);
            dummy.setIsImmobilized(true);
            dummy.setCollisionHeight(600);
            ZONE.broadcastPacket(new NpcInfo(dummy, null));
            
            stopPcActions();
            startQuestTimer("camera_2", dummy, null, 1000);
        }
        else if (name.equalsIgnoreCase("camera_2"))
        {
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 0, 75, -89, 0, 100));
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 0, 75, -89, 0, 100));
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 300, 90, -10, 6500, 7000));
            
            _frintezza = (GrandBoss) addSpawn(FRINTEZZA, 174240, -89805, -5022, 16048, false, 0, false);
            _frintezza.setIsImmobilized(true);
            _frintezza.setIsInvul(true);
            _frintezza.disableAllSkills();
            
            GrandBossManager.getInstance().addBoss(_frintezza);
            
            for (int[] demon : DEMONS)
            {
                Npc d = addSpawn(demon[0], demon[1], demon[2], demon[3], demon[4], false, 0, false);
                d.setIsImmobilized(true);
                d.disableAllSkills();
            }
            
            startQuestTimer("camera_4", npc, null, 6500);
        }
        else if (name.equalsIgnoreCase("camera_4"))
        {
            npc.deleteMe();
            
            Npc dummy = addSpawn(29052, 174240, -89805, -5022, 16048, false, 0, false);
            dummy.setIsInvul(true);
            dummy.setIsImmobilized(true);
            
            ZONE.broadcastPacket(new SpecialCamera(dummy.getObjectId(), 1800, 90, 8, 6500, 7000));
            startQuestTimer("camera_5",  dummy, null, 900);
        }
        else if (name.equalsIgnoreCase("camera_5"))
        {
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 140, 90, 10, 2500, 4500));
            startQuestTimer("camera_5b", npc, null, 4000);
        }
        else if (name.equalsIgnoreCase("camera_5b"))
        {
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 40, 75, -10, 0, 1000));
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 40, 75, -10, 0, 12000));
            startQuestTimer("camera_7", npc, null, 1350);
        }
        else if (name.equalsIgnoreCase("camera_7"))
        {
            ZONE.broadcastPacket(new SocialAction(_frintezza, 2));
            startQuestTimer("camera_8", npc, null, 7000);
        }
        else if (name.equalsIgnoreCase("camera_8"))
        {
            npc.deleteMe();
            startQuestTimer("camera_9", null, null, 1000);
        }
        else if (name.equalsIgnoreCase("camera_9"))
        {
            for (Npc mob : ZONE.getKnownTypeInside(Npc.class))
            {
                if (mob.getNpcId() == 29051 || mob.getNpcId() == 29050)
                    ZONE.broadcastPacket(new SocialAction(mob, 1));
            }
                
            startQuestTimer("camera_9b", null, null, 400);
        }
        else if (name.equalsIgnoreCase("camera_9b"))
        {
            for (Npc mob : ZONE.getKnownTypeInside(Npc.class))
            {
                if (mob.getNpcId() == 29051 || mob.getNpcId() == 29050)
                    ZONE.broadcastPacket(new SocialAction(mob, 1));
            }
                
            Npc dummy1 = addSpawn(29052, 172450, -87890, -5089, 16048, false, 0, false);
            Npc dummy2 = addSpawn(29052, 176012, -87890, -5089, 16048, false, 0, false);
            dummy1.setIsImmobilized(true);
            dummy1.setIsInvul(true);
            dummy2.setIsImmobilized(true);
            dummy2.setIsInvul(true);
            
            for (Creature pc : ZONE.getKnownTypeInside(Player.class))
            {
                if (pc.getX() < 174232)
                    pc.broadcastPacket(new SpecialCamera(dummy1.getObjectId(), 1000, 118, 0, 0, 1000));
                else
                    pc.broadcastPacket(new SpecialCamera(dummy2.getObjectId(), 1000, 62, 0, 0, 1000));
            }
            for (Creature pc : ZONE.getKnownTypeInside(Player.class))
            {
                if (pc.getX() < 174232)
                    pc.broadcastPacket(new SpecialCamera(dummy1.getObjectId(), 1000, 118, 0, 0, 10000));
                else
                    pc.broadcastPacket(new SpecialCamera(dummy2.getObjectId(), 1000, 62, 0, 0, 10000));
            }
            
            dummy1.deleteMe();
            dummy2.deleteMe();
            
            startQuestTimer("camera_10", null, null, 2000);
        }
        else if (name.equalsIgnoreCase("camera_10"))
        {
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 240, 90, 0, 0, 1000));
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 240, 90, 25, 5500, 10000));
            ZONE.broadcastPacket(new SocialAction(_frintezza, 3));
            startQuestTimer("camera_12", null, null, 4500);
        }
        else if (name.equalsIgnoreCase("camera_12"))
        {
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 100, 195, 35, 0, 10000));
            startQuestTimer("camera_13", null, null, 700);
        }
        else if (name.equalsIgnoreCase("camera_13"))
        {
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 100, 195, 35, 0, 10000));
            startQuestTimer("camera_14", null, null, 1300);
        }
        else if (name.equalsIgnoreCase("camera_14"))
        {
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 120, 180, 45, 1500, 10000));
            ZONE.broadcastPacket(new MagicSkillUse(_frintezza, _frintezza, 5006, 1, 34000, 0));
            startQuestTimer("camera_16", null, null, 1500);
        }
        else if (name.equalsIgnoreCase("camera_16"))
        {
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 520, 135, 45, 8000, 10000));
            startQuestTimer("camera_17", null, null, 7500);
        }
        else if (name.equalsIgnoreCase("camera_17"))
        {
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 1500, 110, 25, 10000, 13000));
            startQuestTimer("camera_18", null, null, 9500);
        }
        else if (name.equalsIgnoreCase("camera_18"))
        {
            Npc dummy = addSpawn(29052, 174232, -88020, -5111, 49151, false, 0, false);
            Npc scarletDummy = addSpawn(29052, 174232, -88020, -5111, 49151, false, 0, false);
            dummy.setIsInvul(true);
            dummy.setIsImmobilized(true);
            dummy.setCollisionHeight(600);
            scarletDummy.setIsInvul(true);
            scarletDummy.setIsImmobilized(true);
            
            npc = addSpawn(SCARLET1, 174232, -88020, -5111, 20458, false, 0, false);
            
            ZONE.broadcastPacket(new NpcInfo(dummy, null));
            ZONE.broadcastPacket(new SpecialCamera(SCARLET1, 930, 160, -20, 0, 1000));
            ZONE.broadcastPacket(new SpecialCamera(SCARLET1, 930, 160, -20, 0, 10000));
            
            stopNpcActions();
            
            ZONE.broadcastPacket(new MagicSkillUse(scarletDummy, dummy, 5004, 1, 5800, 0));
            
            scarletDummy.deleteMe();
            dummy.deleteMe();
            
            startQuestTimer("camera_19", npc, null, 5500);
            startQuestTimer("camera_19b", npc, null, 5400);
        }
        else if (name.equalsIgnoreCase("camera_19"))
        {
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 800, 160, 5, 1000, 10000));
            startQuestTimer("camera_20", npc, null, 2100);
        }
        else if (name.equalsIgnoreCase("camera_19b"))
            ZONE.broadcastPacket(new SocialAction(npc, 3));
        else if (name.equalsIgnoreCase("camera_20"))
        {
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 300, 60, 8, 0, 10000));
            startQuestTimer("camera_21", npc, null, 2000);
        }
        else if (name.equalsIgnoreCase("camera_21"))
        {
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 500, 90, 10, 3000, 5000));
            startQuestTimer("camera_22", npc, null, 3000);
        }
        else if (name.equalsIgnoreCase("camera_22"))
        {
            for (int[] portrait : PORTRAITS)
            {
                Npc p = addSpawn(portrait[0], portrait[1], portrait[2], -5000, 0, false, 0, false);
                p.setIsImmobilized(true);
                p.disableAllSkills();
            }
            
            startQuestTimer("camera_23", npc, null, 2000);
            startQuestTimer("songs_play", npc, null, 10000 + Rnd.get(10000));
            startQuestTimer("skill_ai", npc, null, 10000 + Rnd.get(10000));
        }
        else if (name.equalsIgnoreCase("camera_23"))
        {
            for (Npc minion : ZONE.getKnownTypeInside(Npc.class))
            {
                if (minion.getNpcId() == FRINTEZZA)
                    continue;
                
                minion.setIsImmobilized(false);
                minion.enableAllSkills();
                
                if (minion.getNpcId() == 29049 || minion.getNpcId() == 29048)
                    startQuestTimer("spawn_minion", minion, null, 20000);
            }
            
            startPcActions();
            startNpcActions();
        }
        else if (name.equalsIgnoreCase("start_pc"))
            startPcActions();
        else if (name.equalsIgnoreCase("songs_play"))
        {
            if (npc.isCastingNow() || _currentSong != 0)
                startQuestTimer("songs_play", npc, null,  5000 + Rnd.get(5000));
            else if (_frintezza != null && !_frintezza.isDead())
            {
                _currentSong = Rnd.get(1, 5);
                String songName = "";
                
                switch (_currentSong)
                {
                    case 1:
                        songName = "Requiem of Hatred";
                        break;
                    case 2:
                        songName = "Fugue of Jubilation";
                        break;
                    case 3:
                        songName = "Frenetic Toccata";
                        break;
                    case 4:
                        songName = "Hypnotic Mazurka";
                        break;
                    case 5:
                        songName = "Mournful Chorale Prelude";
                        break;
                    default:
                        songName = "Rondo of Solitude";
                        break;
                }
                
                ZONE.broadcastPacket(new ExShowScreenMessage(songName, 4000));
                
                if (_currentSong == 1 && _transformed == 2 && npc.getCurrentHp() < npc.getMaxHp() * 0.6 && Rnd.get(100) < 80)
                {
                    ZONE.broadcastPacket(new MagicSkillUse(_frintezza, _frintezza, 5007, 1, 32000, 0));
                    startQuestTimer("songs_effect", null, null, 4000);
                    startQuestTimer("songs_play", npc, null, 32000 + Rnd.get(10000));
                }
                else if (_currentSong == 2 || _currentSong == 3)
                {
                    ZONE.broadcastPacket(new MagicSkillUse(_frintezza, _frintezza, 5007, _currentSong, 32000, 0));
                    startQuestTimer("songs_effect", null, null, 5000);
                    startQuestTimer("songs_play", npc, null, 32000 + Rnd.get(10000));
                }
                else if (_currentSong == 4 && _currentSong == 1)
                {
                    ZONE.broadcastPacket(new MagicSkillUse(_frintezza, _frintezza, 5007, 4, 31000, 0));
                    startQuestTimer("songs_effect", null, null, 5000);
                    startQuestTimer("songs_play", npc, null, 31000 + Rnd.get(10000));
                }
                else if (_currentSong == 5 && _transformed == 2)
                {
                    ZONE.broadcastPacket(new MagicSkillUse(_frintezza, _frintezza, 5007, 5, 35000, 0));
                    startQuestTimer("songs_effect", null, null, 5000);
                    startQuestTimer("songs_play", npc, null, 35000 + Rnd.get(10000));
                }
                else
                {
                    ZONE.broadcastPacket(new MagicSkillUse(_frintezza, _frintezza, 5006, 1, 34000, 0));
                    startQuestTimer("songs_play", npc, null, 5000 + Rnd.get(5000));
                }
            }
        }
        else if (name.equalsIgnoreCase("songs_effect"))
        {
            if (_currentSong > 0)
            {
                L2Skill skill = SkillTable.getInstance().getInfo(5008, _currentSong);
                
                if (_currentSong < 5)
                {
                    for (Creature cha : ZONE.getKnownTypeInside(Player.class))
                    {
                        if (Rnd.get(100) < 80)
                            skill.getEffects(_frintezza, cha);
                    }
                }
                else if (_currentSong == 5)
                {
                    for (Creature cha : ZONE.getKnownTypeInside(Player.class))
                    {
                        if (Rnd.get(100) < 70)
                        {
                            cha.abortAttack();
                            cha.abortCast();
                            cha.disableAllSkills();
                            cha.getMove().stopMove();
                            cha.setIsImmobilized(true);
                            cha.setIsParalyzed(true);
                            cha.getAI().setIntention(IntentionType.IDLE);
                            skill.getEffects(_frintezza, cha);
                            cha.startAbnormalEffect(AbnormalEffect.DANCE_STUNNED);
                        }
                    }
                    startQuestTimer("stop_effect", null, null, 25000);
                }
            }
        }
        else if (name.equalsIgnoreCase("stop_effect"))
        {
            _currentSong = 0;
            
            for (Creature cha : ZONE.getKnownTypeInside(Player.class))
            {
                cha.stopAbnormalEffect(AbnormalEffect.DANCE_STUNNED);
                cha.stopAbnormalEffect(AbnormalEffect.FLOATING_ROOT);
                cha.enableAllSkills();
                cha.setIsImmobilized(false);
                cha.setIsParalyzed(false);
            }
        }
        else if (name.equalsIgnoreCase("start_npc"))
            startNpcActions();
        else if (name.equalsIgnoreCase("skill_ai"))
        {
            if (!npc.isDead())
            {
                if (_transformed == 0)
                {
                    int i = Rnd.get(0, 1);
                    final L2Skill skill = SkillTable.getInstance().getInfo(SKILLS[i][0], SKILLS[i][1]);
                    if (skill != null)
                    {
                        npc.getMove().stopMove();
                        npc.doCast(skill);
                    }
                }
                else if (_transformed == 1)
                {
                    int i = Rnd.get(2, 5);
                    final L2Skill skill = SkillTable.getInstance().getInfo(SKILLS[i][0], SKILLS[i][1]);
                    if (skill != null)
                    {
                        npc.getMove().stopMove();
                        npc.doCast(skill);
                    }
                    if (i == 5)
                        startQuestTimer("float_effect", npc, null, 4000);
                }
                else
                {
                    int i = Rnd.get(6, 10);
                    final L2Skill skill = SkillTable.getInstance().getInfo(SKILLS[i][0], SKILLS[i][1]);
                    if (skill != null)
                    {
                        npc.getMove().stopMove();
                        npc.doCast(skill);
                    }
                    if (i == 10)
                        startQuestTimer("float_effect", npc, null, 3000);
                }
                startQuestTimer("skill_ai", npc, null, 15000 + Rnd.get(10000));
            }
        }
        else if (name.equalsIgnoreCase("float_effect"))
        {
            if (npc.isCastingNow())
                startQuestTimer("float_effect", npc, null, 500);
            else
            {
                for (Creature cha : ZONE.getKnownTypeInside(Player.class))
                {
                    if (cha.getFirstEffect(5016) != null)
                    {
                        cha.abortAttack();
                        cha.abortCast();
                        cha.disableAllSkills();
                        cha.getMove().stopMove();
                        cha.setIsImmobilized(true);
                        cha.setIsParalyzed(true);
                        cha.getAI().setIntention(IntentionType.IDLE);
                        cha.startAbnormalEffect(AbnormalEffect.FLOATING_ROOT);
                    }
                }
                startQuestTimer("stop_effect", null, null, 25000);
            }
        }
        else if (name.equalsIgnoreCase("frintezza_despawn"))
        {
            if (System.currentTimeMillis() - _lastAction > 900000)
            {
                ZONE.oustAllPlayers();
                
                cancelQuestTimers("spawn_minion");
                cancelQuestTimers("notice");
                cancelQuestTimers("frintezza_despawn");
                cancelQuestTimers("waiting");
                
                deleteAllMobs();
                closeDoors();
                stopAttacks();
                
                GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DORMANT);
            }
        }
        else if (name.equalsIgnoreCase("morph_01"))
        {
            int angle;
            if (npc.getHeading() < 32768)
                angle = Math.abs(180 - (int) (npc.getHeading() / 182.044444444));
            else
                angle = Math.abs(540 - (int) (npc.getHeading() / 182.044444444));
            
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 250, angle, 12, 2000, 15000));
            startQuestTimer("morph_02", npc, null, 3000);
        }
        else if (name.equalsIgnoreCase("morph_02"))
        {
            ZONE.broadcastPacket(new SocialAction(npc, 1));
            startQuestTimer("morph_04", npc, null, 5500);
        }
        else if (name.equalsIgnoreCase("morph_04"))
        {
            ZONE.broadcastPacket(new SocialAction(npc, 4));
            final L2Skill skill = SkillTable.getInstance().getInfo(5017, 1);
            if (skill != null)
                skill.getEffects(npc, npc);
            
            startQuestTimer("start_pc", null, null, 3000);
            startQuestTimer("start_npc", null, null, 3000);
        }
        else if (name.equalsIgnoreCase("morph_05a"))
        {
            ZONE.broadcastPacket(new SocialAction(_frintezza, 4));
            startQuestTimer("morph_05", npc, null, 100);
        }
        else if (name.equalsIgnoreCase("morph_05"))
        {
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 250, 120, 15, 0, 1000));
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 250, 120, 15, 0, 10000));
            startQuestTimer("morph_07", npc, null, 7000);
        }
        else if (name.equalsIgnoreCase("morph_07"))
        {
            ZONE.broadcastPacket(new MagicSkillUse(_frintezza, _frintezza, 5006, 1, 34000, 0));
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 500, 70, 15, 3000, 10000));
            startQuestTimer("morph_08", npc, null, 3000);
        }
        else if (name.equalsIgnoreCase("morph_08"))
        {
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 2500, 90, 12, 6000, 10000));
            startQuestTimer("morph_09", npc, null, 3000);
        }
        else if (name.equalsIgnoreCase("morph_09"))
        {
            int angle;
            if (npc.getHeading() < 32768)
                angle = Math.abs(180 - (int) (npc.getHeading() / 182.044444444));
            else
                angle = Math.abs(540 - (int) (npc.getHeading() / 182.044444444));
            
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 250, angle, 12, 0, 1000));
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 250, angle, 12, 0, 10000));
            startQuestTimer("morph_11", npc, null, 500);
        }
        else if (name.equalsIgnoreCase("morph_11"))
        {
            int angle;
            if (npc.getHeading() < 32768)
                angle = Math.abs(180 - (int) (npc.getHeading() / 182.044444444));
            else
                angle = Math.abs(540 - (int) (npc.getHeading() / 182.044444444));
            
            npc.doDie(npc);
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 450, angle, 14, 8000, 8000));
            startQuestTimer("morph_12", npc, null, 6250);
        }
        else if (name.equalsIgnoreCase("morph_12"))
        {
            npc.deleteMe();
            startQuestTimer("morph_13", npc, null, 950);
        }
        else if (name.equalsIgnoreCase("morph_13"))
        {
            int angle;
            if (npc.getHeading() < 32768)
                angle = Math.abs(180 - (int) (npc.getHeading() / 182.044444444));
            else
                angle = Math.abs(540 - (int) (npc.getHeading() / 182.044444444));
            
            npc = addSpawn(SCARLET2, npc.getX(), npc.getY(), npc.getZ(), 28193, false, 0, false);
            stopNpcActions();
            
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 450, angle, 12, 500, 14000));
            startQuestTimer("morph_15", npc, null, 8100);
        }
        else if (name.equalsIgnoreCase("morph_15"))
        {
            ZONE.broadcastPacket(new SocialAction(npc, 2));
            final L2Skill skill = SkillTable.getInstance().getInfo(5017, 1);
            if (skill != null)
                skill.getEffects(npc, npc);
            
            startQuestTimer("start_pc", null, null, 6000);
            startQuestTimer("start_npc", null, null, 6000);
            startQuestTimer("songs_play", npc, null, 10000 + Rnd.get(10000));
            startQuestTimer("skill_ai", npc, null, 10000 + Rnd.get(10000));
        }
        else if (name.equalsIgnoreCase("morph_17b"))
        {
            _frintezza.doDie(_frintezza);
            startQuestTimer("morph_18", npc, null, 100);
        }
        else if (name.equalsIgnoreCase("morph_18"))
        {
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 100, 120, 5, 0, 7000));
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 100, 90, 5, 5000, 15000));
            startQuestTimer("morph_20", npc, null, 7000);
        }
        else if (name.equalsIgnoreCase("morph_20"))
        {
            ZONE.broadcastPacket(new SpecialCamera(_frintezza.getObjectId(), 900, 90, 25, 7000, 10000));
            
            closeDoors();
            deleteAllMobs();
            
            addSpawn(CUBE, 174232, -88020, -5114, 49151, false, 900000, false);
            startQuestTimer("start_pc", null, null, 7000);
        }
        
        return super.onTimer(name, npc, player);
    }

    @Override
    public String onAttack(Npc npc, Creature attacker, int damage, L2Skill skill)
    {
        _lastAction = System.currentTimeMillis();
        
        if (npc.getNpcId() == SCARLET1 && _transformed == 0 && npc.getCurrentHp() < npc.getMaxHp() * 0.75)
        {
            _transformed = 1;
            
            stopAttacks();
            stopPcActions();
            stopNpcActions();
            startQuestTimer("morph_01", npc, null, 1100);
        }
        else if (npc.getNpcId() == SCARLET1 && _transformed == 1 && npc.getCurrentHp() < npc.getMaxHp() * 0.5)
        {
            _transformed = 2;
            
            stopAttacks();
            stopPcActions();
            stopNpcActions();
            startQuestTimer("morph_05a", npc, null, 2000);
        }
        else if ((npc.getNpcId() == 29050 || npc.getNpcId() == 29051) && npc.getCurrentHp() < npc.getMaxHp() * 0.1 && Rnd.get(100) < 30)
            npc.doCast(SkillTable.getInstance().getInfo(5011, 1));
        
        return super.onAttack(npc, attacker, damage, skill);
    }
    
    @Override
    public String onKill(Npc npc, Creature killer)
    {
        if (npc.getNpcId() == SCARLET2)
        {
            ZONE.broadcastPacket(new PlaySound(1, "BS01_D", npc));
            
            stopPcActions();
            stopNpcActions();
            
            int angle;
            if (npc.getHeading() < 32768)
                angle = Math.abs(180 - (int) (npc.getHeading() / 182.044444444));
            else
                angle = Math.abs(540 - (int) (npc.getHeading() / 182.044444444));
            
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 300, angle - 180, 5, 0, 7000));
            ZONE.broadcastPacket(new SpecialCamera(npc.getObjectId(), 200, angle, 85, 4000, 10000));
            startQuestTimer("morph_17b", npc, null, 7400);
            
            GrandBossManager.getInstance().setBossStatus(FRINTEZZA, DEAD);
            long respawnTime = (long) Config.SPAWN_INTERVAL_FRINTEZZA + Rnd.get(-Config.RANDOM_SPAWN_TIME_FRINTEZZA, Config.RANDOM_SPAWN_TIME_FRINTEZZA);
            respawnTime *= 3600000;
            
            cancelQuestTimers("spawn_minion");
            cancelQuestTimers("frintezza_despawn");
            startQuestTimer("remove_players", null, null, 900000);
            startQuestTimer("frintezza_unlock", null, null, respawnTime);
            
            StatsSet info = GrandBossManager.getInstance().getStatsSet(FRINTEZZA);
            info.set("respawn_time", System.currentTimeMillis() + respawnTime);
            GrandBossManager.getInstance().setStatsSet(FRINTEZZA, info);
        }
        else if (npc.getNpcId() == 18328)
        {
            int alarmsRemaining = getMobs(npc);
            if (alarmsRemaining == 1)
            {
                for (int i = 25150051; i <= 25150058; i++)
                    DoorData.getInstance().getDoor(i).openMe();
            }
            else if (alarmsRemaining == 0)
            {
                ZONE.broadcastPacket(new CreatureSay(npc.getObjectId(), SayType.SHOUT, npc.getName(), "De-activate the alarm."));
                deleteAllMobs();
                room2Spawn(1);
            }
        }
        else if (npc.getNpcId() == 18339)
        {
            if (getMobs(npc) == 0)
                room2Spawn(2);
        }
        else if (npc.getNpcId() == 18334)
        {
            if (getMobs(npc) == 0)
            {
                deleteAllMobs();
                DoorData.getInstance().getDoor(25150045).openMe();
                DoorData.getInstance().getDoor(25150046).openMe();
                cancelQuestTimers("notice");
                startQuestTimer("waiting", null, null, Config.WAIT_TIME_FRINTEZZA);
            }
        }
        
        return super.onKill(npc, killer);
    }
    
    private int getMobs(Npc npc)
    {
        _roomMobs.remove(npc);
        
        int count = 0;
        for (Npc mob : _roomMobs)
        {
            if (mob.getNpcId() == npc.getNpcId())
                count++;
        }
        
        return count;
    }
    
    private void deleteAllMobs()
    {
        for (Npc mob : _roomMobs)
            mob.deleteMe();
        
        for (Npc mob : ZONE.getKnownTypeInside(Npc.class))
            mob.deleteMe();
        
        _roomMobs.clear();
        
        if (_frintezza != null)
        {
            _frintezza.deleteMe();
            _frintezza = null;
        }
    }
    
    private void stopAttacks()
    {
        cancelQuestTimers("skill_ai");
        cancelQuestTimers("songs_play");
        cancelQuestTimers("songs_effect");
        
        if (_frintezza != null)
            ZONE.broadcastPacket(new MagicSkillCanceled(_frintezza.getObjectId()));
    }
    
    private void room2Spawn(int spawn)
    {
        if (spawn == 1)
        {
            for (int i = 41; i <= 44; i++)
                _roomMobs.add(addSpawn(MOBS[i][0], MOBS[i][1], MOBS[i][2], MOBS[i][3], MOBS[i][4], false, 0, false));
            
            for (int i = 25150051; i <= 25150058; i++)
                DoorData.getInstance().getDoor(i).openMe();
            
            DoorData.getInstance().getDoor(25150042).openMe();
            DoorData.getInstance().getDoor(25150043).openMe();
        }
        else
        {
            DoorData.getInstance().getDoor(25150042).closeMe();
            DoorData.getInstance().getDoor(25150043).closeMe();
            DoorData.getInstance().getDoor(25150045).closeMe();
            DoorData.getInstance().getDoor(25150046).closeMe();
            
            for (int i = 25150061; i <= 25150070; i++)
                DoorData.getInstance().getDoor(i).openMe();
            
            for (int i = 45; i <= 131; i++)
                _roomMobs.add(addSpawn(MOBS[i][0], MOBS[i][1], MOBS[i][2], MOBS[i][3], MOBS[i][4], false, 0, false));
        }
    }
    
    private static void stopNpcActions()
    {
        for (Npc mob : ZONE.getKnownTypeInside(Npc.class))
        {
            if (mob.getNpcId() != FRINTEZZA)
            {
                mob.disableAllSkills();
                mob.setIsInvul(true);
                mob.setIsImmobilized(true);
            }
        }
    }
    
    private static void startNpcActions()
    {
        for (Npc mob : ZONE.getKnownTypeInside(Npc.class))
        {
            if (mob.getNpcId() != FRINTEZZA)
            {
                mob.enableAllSkills();
                mob.setRunning();
                mob.setIsInvul(false);
                mob.setIsImmobilized(false);
            }
        }
    }
    
    private static void closeDoors()
    {
        for (int i = 25150051; i <= 25150058; i++)
            DoorData.getInstance().getDoor(i).closeMe();
        
        for (int i = 25150061; i <= 25150070; i++)
            DoorData.getInstance().getDoor(i).closeMe();
        
        DoorData.getInstance().getDoor(25150042).closeMe();
        DoorData.getInstance().getDoor(25150043).closeMe();
        DoorData.getInstance().getDoor(25150045).closeMe();
        DoorData.getInstance().getDoor(25150046).closeMe();
    }
    
    private static void stopPcActions()
    {
        for (Creature cha : ZONE.getKnownTypeInside(Player.class))
        {
            cha.abortAttack();
            cha.abortCast();
            cha.disableAllSkills();
            cha.setTarget(null);
            cha.getMove().stopMove();
            cha.setIsImmobilized(true);
            cha.getAI().setIntention(IntentionType.IDLE);
        }
    }
    
    private static void startPcActions()
    {
        for (Creature cha : ZONE.getKnownTypeInside(Player.class))
        {
            cha.enableAllSkills();
            cha.setIsImmobilized(false);
        }
    }
}