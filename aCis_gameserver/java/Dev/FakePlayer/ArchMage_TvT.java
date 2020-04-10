//package Dev.FakePlayer;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.LineNumberReader;
//import java.lang.System.Logger;
//import java.sql.Date;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import net.sf.l2j.gameserver.model.location.Location;
//import net.sf.l2j.gameserver.network.GameClient;
//import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
//
//import net.sf.l2j.commons.concurrent.ThreadPool;
//import net.sf.l2j.commons.random.Rnd;
//
//import net.sf.l2j.Config;
//import net.sf.l2j.gameserver.data.SkillTable;
//import net.sf.l2j.gameserver.data.sql.PlayerInfoTable;
//import net.sf.l2j.gameserver.data.xml.ItemData;
//import net.sf.l2j.gameserver.data.xml.PlayerData;
//import net.sf.l2j.gameserver.enums.IntentionType;
//import net.sf.l2j.gameserver.enums.actors.ClassRace;
//import net.sf.l2j.gameserver.enums.actors.Sex;
//import net.sf.l2j.gameserver.geoengine.GeoEngine;
//import net.sf.l2j.gameserver.idfactory.IdFactory;
//import net.sf.l2j.gameserver.model.Augmentation;
//import net.sf.l2j.gameserver.model.L2Skill;
//import net.sf.l2j.gameserver.model.World;
//import net.sf.l2j.gameserver.model.WorldObject;
//import net.sf.l2j.gameserver.model.actor.Creature;
//import net.sf.l2j.gameserver.model.actor.Player;
//import net.sf.l2j.gameserver.model.actor.container.player.Appearance;
//import net.sf.l2j.gameserver.model.actor.stat.Experience;
//import net.sf.l2j.gameserver.model.actor.template.PlayerTemplate;
//import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
//
//import Dev.FakePlayer.PhantomWalker.L2Set;
//import Dev.Phantom.FakePlayer;
//import Dev.Phantom.Helpers.FakeHelpers;
//import Dev.TeamVsTeam.TvTEvent;
//
//
///**
// * @author COMBATE
// *
// */
//public class ArchMage_TvT
//{
//	 public static boolean _task_1;
//	    public static boolean _task_2;
//	    public static boolean _task_3;
//	    public static boolean _task_4;
//	    public static boolean _task_5;
//	    public static boolean _task_6;
//	    public static boolean _task_7;
//	    public static boolean _task_8;
//
//	    static int _PhantomsCount;
//	    static int _PhantomsLimit;
//	    static int _setsCount;
//	    static int _setsCountClan;
//	    static ArrayList<L2Set> _sets;
//	    static int _setsArcherCount;
//	    static ArrayList<L2Set> _setsArcher;
//	    static ArchMage_TvT _instance;
//	    static int _setsOlyCount;
//	    static ArrayList<L2Set> _setsOly;
//	    static int _locsCount;
//	    static ArrayList<Location> _PhantomsTownLoc;
//	    static int _PhantomsEnchPhsCount;
//	    static ArrayList<String> _PhantomsEnchPhrases;
//	    static int _PhantomsLastPhsCount;
//	    static ArrayList<String> _PhantomsLastPhrases;
//	    static Map<Integer, ConcurrentLinkedQueue<Player>> _PhantomsTownClan;
//	    static Map<Integer, ConcurrentLinkedQueue<Integer>> _PhantomsTownClanList;
//	    public static ArrayList<Player> _add_phantom;
//	    static SimpleDateFormat sdf;
//	    
//	    public static ArchMage_TvT getInstance() {
//	        return ArchMage_TvT._instance;
//	    }
//	    
//	    private void load() {
//	        if (Config.ALLOW_PHANTOM_PLAYERS) {
//	            parceArmors();
//	            this.cacheFantoms();
//	            parceTownLocs();
//	        }
//	    }
//	    
//	    private static void parceTownLocs() {
//	        ArchMage_TvT._PhantomsTownLoc.clear();
//	        LineNumberReader localLineNumberReader = null;
//	        BufferedReader localBufferedReader = null;
//	        FileReader localFileReader = null;
//	        try {
//	            final File localFile = new File("./config/phantom/town_locs.ini");
//	            if (!localFile.exists()) {
//	                return;
//	            }
//	            localFileReader = new FileReader(localFile);
//	            localBufferedReader = new BufferedReader(localFileReader);
//	            localLineNumberReader = new LineNumberReader(localBufferedReader);
//	            String str;
//	            while ((str = localLineNumberReader.readLine()) != null) {
//	                if (str.trim().length() != 0 && !str.startsWith("#")) {
//	                    final String[] arrayOfString = str.split(",");
//	                    ArchMage_TvT._PhantomsTownLoc.add(new Location(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), Integer.parseInt(arrayOfString[2])));
//	                }
//	            }
//	            ArchMage_TvT._locsCount = ArchMage_TvT._PhantomsTownLoc.size() - 1;
//	        }
//	        catch (Exception localException2) {
//	            localException2.printStackTrace();
//	        }
//	        finally {
//	            try {
//	                if (localFileReader != null) {
//	                    localFileReader.close();
//	                }
//	                if (localBufferedReader != null) {
//	                    localBufferedReader.close();
//	                }
//	                if (localLineNumberReader != null) {
//	                    localLineNumberReader.close();
//	                }
//	            }
//	            catch (Exception ex) {}
//	        }
//	    }
//	    
//	    public void reload() {
//	        parceArmors();
//	    }
//	    
//	    public static void init() {
//	        (ArchMage_TvT._instance = new ArchMage_TvT()).load();
//	    }
//	    
//	    static int getFaceEquipe() {
//	        return Config.LIST_PHANTOM_FACE.get(Rnd.get(Config.LIST_PHANTOM_FACE.size()));
//	    }
//	    
//	    static int getHairEquipe() {
//	        return Config.LIST_PHANTOM_HAIR.get(Rnd.get(Config.LIST_PHANTOM_HAIR.size()));
//	    }
//	    
//
//	    
//	    static String getNameColor() {
//	        return Config.PHANTOM_PLAYERS_NAME_CLOLORS.get(Rnd.get(Config.PHANTOM_PLAYERS_NAME_CLOLORS.size()));
//	    }
//	    
//	    static String getTitleColor() {
//	        return Config.PHANTOM_PLAYERS_TITLE_CLOLORS.get(Rnd.get(Config.PHANTOM_PLAYERS_TITLE_CLOLORS.size()));
//	    }
//	    
//	    private static void parceArmors() {
//	        if (!ArchMage_TvT._sets.isEmpty()) {
//	            ArchMage_TvT._sets.clear();
//	        }
//	        LineNumberReader localLineNumberReader = null;
//	        BufferedReader localBufferedReader = null;
//	        FileReader localFileReader = null;
//	        try {
//	            final File localFile = new File("./config/phantom/mage_sets.ini");
//	            if (!localFile.exists()) {
//	                return;
//	            }
//	            localFileReader = new FileReader(localFile);
//	            localBufferedReader = new BufferedReader(localFileReader);
//	            localLineNumberReader = new LineNumberReader(localBufferedReader);
//	            String str;
//	            while ((str = localLineNumberReader.readLine()) != null) {
//	                if (str.trim().length() != 0 && !str.startsWith("#")) {
//	                    final String[] arrayOfString = str.split(",");
//	                    ArchMage_TvT._sets.add(new L2Set(Integer.parseInt(arrayOfString[0]), Integer.parseInt(arrayOfString[1]), Integer.parseInt(arrayOfString[2]), Integer.parseInt(arrayOfString[3]), Integer.parseInt(arrayOfString[4]), Integer.parseInt(arrayOfString[5]), Integer.parseInt(arrayOfString[6])));
//	                }
//	            }
//	            ArchMage_TvT._setsCount = ArchMage_TvT._sets.size();
//	        }
//	        catch (Exception localException2) {
//	            localException2.printStackTrace();
//	        }
//	        finally {
//	            try {
//	                if (localFileReader != null) {
//	                    localFileReader.close();
//	                }
//	                if (localBufferedReader != null) {
//	                    localBufferedReader.close();
//	                }
//	                if (localLineNumberReader != null) {
//	                    localLineNumberReader.close();
//	                }
//	            }
//	            catch (Exception ex) {}
//	        }
//	    }
//	    
//	    private void cacheFantoms() {
//	        new Thread(new Runnable() {
//	            @Override
//	            public void run() {
//	                ThreadPool.schedule(new FantomTask(1), Rnd.get(3000, 13000));
//	            }
//	        }).start();
//	    }
//	    
//	    static L2Set getRandomSet() {
//	        return ArchMage_TvT._sets.get(Rnd.get(ArchMage_TvT._setsCount));
//	    }
//	    
//	    public static int getPhantomCount() {
//	        if (ArchMage_TvT._add_phantom != null) {
//	            return ArchMage_TvT._add_phantom.size();
//	        }
//	        return 0;
//	    }
//	    
//	    public static void removePhantom(final Player spec) {
//	        if (ArchMage_TvT._add_phantom != null && ArchMage_TvT._add_phantom.contains(spec)) {
//	            ArchMage_TvT._add_phantom.remove(spec);
//	        }
//	    }
//	    
//	    public static FakePlayer createRandomFakePlayer() {
//	        final int objectId = IdFactory.getInstance().getNextId();
//	        final String accountName = "AutoPilot";
//	        final String playerName = FakePlayerNameManager.INSTANCE.getRandomAvailableName();
//	        final int[] classes = { 0, 18, 31, 44, 10, 25, 38, 49, 53 };
//	        final int classId = classes[Rnd.get(classes.length)];
//	        final PlayerTemplate template = PlayerData.getInstance().getTemplate(classId);
//	        final Appearance app = getRandomAppearance(template.getRace());
//	        final FakePlayer player = new FakePlayer(objectId, template, accountName, app);
//	        player.setName(playerName);
//	        player.setAccessLevel(Config.DEFAULT_ACCESS_LEVEL);
//	        PlayerInfoTable.getInstance().addPlayer(objectId, accountName, playerName, player.getAccessLevel().getLevel());
//	        player.setBaseClass(player.getClassId());
//	        player.addExpAndSp(Experience.LEVEL[81], 0);
//	        player.heal();
//	        return player;
//	    }
//	    
//	    public static Appearance getRandomAppearance(final ClassRace race) {
//	        final Sex randomSex = (Rnd.get(1, 2) == 1) ? Sex.MALE : Sex.FEMALE;
//	        final int hairStyle = Rnd.get(0, (randomSex == Sex.MALE) ? 4 : 6);
//	        final int hairColor = Rnd.get(0, 3);
//	        final int faceId = Rnd.get(0, 2);
//	        return new Appearance((byte)faceId, (byte)hairColor, (byte)hairStyle, randomSex);
//	    }
//	    
//
//	    
//	    public static FakePlayer spawnPlayer() {
//	        final GameClient client = new GameClient(null);
//	        client.setDetached(true);
//	        final FakePlayer activeChar = FakeHelpers.createRandomFakePlayer();
//	        activeChar.setClient(client);
//	        client.setActiveChar(activeChar);
//	        activeChar.setOnlineStatus(true, false);
//	        client.setState(GameClient.GameClientState.IN_GAME);
//	        client.setAccountName(activeChar.getAccountName());
//	        World.getInstance().addPlayer(activeChar);
//
//	        final L2Set localL2Set = getRandomSet();
//	        final ItemInstance localL2ItemInstance1 = ItemData.getInstance().createDummyItem(localL2Set._body);
//	        final ItemInstance localL2ItemInstance2 = ItemData.getInstance().createDummyItem(localL2Set._gaiters);
//	        final ItemInstance localL2ItemInstance3 = ItemData.getInstance().createDummyItem(localL2Set._gloves);
//	        final ItemInstance localL2ItemInstance4 = ItemData.getInstance().createDummyItem(localL2Set._boots);
//	        final ItemInstance localL2ItemInstance5 = ItemData.getInstance().createDummyItem(localL2Set._weapon);
//	        ItemInstance localL2ItemInstance6 = null;
//	        final ItemInstance WINGS = ItemData.getInstance().createDummyItem(getFaceEquipe());
//	        final ItemInstance HAIR = ItemData.getInstance().createDummyItem(getHairEquipe());
//	        activeChar.getInventory().equipItemAndRecord(localL2ItemInstance1);
//	        activeChar.getInventory().equipItemAndRecord(localL2ItemInstance2);
//	        activeChar.getInventory().equipItemAndRecord(localL2ItemInstance3);
//	        activeChar.getInventory().equipItemAndRecord(localL2ItemInstance4);
//	        if (Config.ALLOW_PHANTOM_FACE) {
//	            activeChar.getInventory().equipItemAndRecord(WINGS);
//	        }
//	        if (Config.ALLOW_PHANTOM_HAIR) {
//	            activeChar.getInventory().equipItemAndRecord(HAIR);
//	        }
//	        if (localL2Set._custom > 0) {
//	            localL2ItemInstance6 = ItemData.getInstance().createDummyItem(localL2Set._custom);
//	            activeChar.getInventory().equipItemAndRecord(localL2ItemInstance6);
//	        }
//	        activeChar.getInventory().equipItemAndRecord(localL2ItemInstance5);
//	        if (Rnd.get(100) < 30 && Config.PHANTOM_PLAYERS_ARGUMENT_ANIM) {
//	            localL2ItemInstance5.setAugmentation(new Augmentation(1067847165, 3250, 1));
//	        }
//	        localL2ItemInstance5.setEnchantLevel(Rnd.get(Config.PHANTOM_PLAYERS_ENCHANT_MIN, Config.PHANTOM_PLAYERS_ENCHANT_MAX));
//	        if (Config.PLAYER_SPAWN_PROTECTION > 0) {
//	            activeChar.setSpawnProtection(true);
//	        }
//	        activeChar.getAppearance().setNameColor(Integer.decode("0x" + Config.NAME_COLOR));
//	        activeChar.getAppearance().setTitleColor(Integer.decode("0x" + Config.TITLE_COLOR));
//
//	        activeChar.addSkill(SkillTable.getInstance().getInfo(9901, 1), true);
//	        if (Rnd.get(100) < Config.PHANTOM_CHANCE_MALARIA) {
//	            final L2Skill skill = SkillTable.getInstance().getInfo(4554, 4);
//	            skill.getEffects(activeChar, activeChar);
//	        }
//	        final Location localLocation = getRandomLoc();
//	        activeChar.spawnMe(localLocation.getX() + Rnd.get(-80, 80), localLocation.getY() + Rnd.get(-80, 80), localLocation.getZ());
//	        activeChar.setLastCords(activeChar.getX(), activeChar.getY(), activeChar.getZ());
//	        activeChar.onPlayerEnter();
//	        activeChar.heal();
//	        if (TvTEvent.isParticipating()) {
//	        	TvTEvent.addParticipant(activeChar);
//	        }
//	        PhantomWalker._players.add(activeChar);
//	        return activeChar;
//	    }
//	    
//	    public static boolean doCastlist(final Player player) {
//	        if (player.isDead() || player.isAttackP()) {
//	            return false;
//	        }
//	        final List<Creature> targetList = new ArrayList<>();
//	        for (final WorldObject obj : player.getKnownType(WorldObject.class)) {
//	            if (obj instanceof Player && player._inEventTvT && !((Player)obj).isDead() && !((Player)obj).isSpawnProtected() && !((Player)obj).isPhantomAntBot() && !((Player)obj).getAppearance().getInvisible() && ((Player)obj).isInsideRadius(player.getX(), player.getY(), player.getZ(), Config.PHANTOM_MAGE_RANGE / 2, false, false) && ((Player)obj)._inEventTvT && !((Player)obj)._teamNameTvT.equals(player._teamNameTvT)) {
//	                targetList.add((Player)obj);
//	            }
//	        }
//	        if (targetList.size() == 0) {
//	            for (final WorldObject obj : player.getKnownType(WorldObject.class)) {
//	                if (obj instanceof Player && player._inEventTvT && !((Player)obj).isDead() && !((Player)obj).isSpawnProtected() && !((Player)obj).isPhantomAntBot() && !((Player)obj).getAppearance().getInvisible() && ((Player)obj).isInsideRadius(player.getX(), player.getY(), player.getZ(), Config.PHANTOM_MAGE_RANGE, false, false) && ((Player)obj)._inEventTvT && !((Player)obj)._teamNameTvT.equals(player._teamNameTvT)) {
//	                    targetList.add((Player)obj);
//	                }
//	            }
//	        }
//	        if (targetList.size() == 0) {
//	            for (final WorldObject obj : player.getKnownType(WorldObject.class)) {
//	                if (obj instanceof Player && player._inEventTvT && !((Player)obj).isDead() && !((Player)obj).isSpawnProtected() && !((Player)obj).isPhantomAntBot() && !((Player)obj).getAppearance().getInvisible() && ((Player)obj).isInsideRadius(player.getX(), player.getY(), player.getZ(), 1500, false, false) && ((Player)obj)._inEventTvT && !((Player)obj)._teamNameTvT.equals(player._teamNameTvT)) {
//	                    targetList.add((Player)obj);
//	                }
//	            }
//	        }
//	        if (targetList.size() == 0) {
//	            for (final WorldObject obj : player.getKnownType(WorldObject.class)) {
//	                if (obj instanceof Player && player._inEventTvT && !((Player)obj).isDead() && !((Player)obj).isSpawnProtected() && !((Player)obj).isPhantomAntBot() && !((Player)obj).getAppearance().getInvisible() && ((Player)obj).isInsideRadius(player.getX(), player.getY(), player.getZ(), 3000, false, false) && ((Player)obj)._inEventTvT && !((Player)obj)._teamNameTvT.equals(player._teamNameTvT)) {
//	                    targetList.add((Player)obj);
//	                }
//	            }
//	        }
//	        if (targetList.size() == 0) {
//	            for (final WorldObject obj : player.getKnownType(WorldObject.class)) {
//	                if (obj instanceof Player && player._inEventTvT && !((Player)obj).isDead() && !((Player)obj).isSpawnProtected() && !((Player)obj).isPhantomAntBot() && !((Player)obj).getAppearance().getInvisible() && ((Player)obj).isInsideRadius(player.getX(), player.getY(), player.getZ(), 6000, false, false) && ((Player)obj)._inEventTvT && !((Player)obj)._teamNameTvT.equals(player._teamNameTvT)) {
//	                    targetList.add((Player)obj);
//	                }
//	            }
//	        }
//	        if (targetList.size() == 0) {
//	            player.stopMove(null);
//	            player.setTarget(null);
//	            player.getAI().setIntention(CtrlIntention.FOLLOW, null);
//	            ThreadPool.schedule(new Runnable() {
//	                @Override
//	                public void run() {
//	                    if (!player.isDead() && Rnd.get(100) < Config.PHANTOM_MAGE_RANDOM_WALK) {
//	                        if (player.isSpawnProtected()) {
//	                            player.setSpawnProtection(false);
//	                        }
//	                        player.rndWalkMonster();
//	                        try {
//	                            Thread.sleep(2000L);
//	                        }
//	                        catch (InterruptedException ex) {}
//	                    }
//	                    ArchMage_TvT.doCastlist(player);
//	                }
//	            }, 1000L);
//	            return false;
//	        }
//	        if (targetList.isEmpty()) {
//	            return true;
//	        }
//	        final int nextTargetIdx = Rnd.get(targetList.size());
//	        final WorldObject target = targetList.get(nextTargetIdx);
//	        player.setTarget(target);
//	        player.setRunning();
//	        try {
//	            Thread.sleep(1000L);
//	        }
//	        catch (InterruptedException ex) {}
//	        doCast(player, (Player)target);
//	        return true;
//	    }
//	    
//	    static void Seguir(final Player player, final Player target) {
//	        if (player.isDead() || player.isAttackP()) {
//	            return;
//	        }
//	        player.getAI().setIntention(IntentionType.FOLLOW, target);
//	        try {
//	            Thread.sleep(1200L);
//	        }
//	        catch (InterruptedException ex) {}
//	        doCastlist(player);
//	    }
//	    
//	    static void doCast(final Player player, final Player target) {
//	        if (!player.isDead() && player.getTarget() != null && !target.isDead() && TvTEvent.isStarted() && player.getZ() > target.getZ() + 100) {
//	            Seguir(player, target);
//	            return;
//	        }
//	        if (!player.isDead() && player.getTarget() != null && !target.isDead() && TvTEvent.isStarted() && !GeoEngine.getInstance().canSeeTarget(player, target)) {
//	            Seguir(player, target);
//	            return;
//	        }
//	        if (!player.isDead() && (target.isDead() || player.getTarget() == null || !TvTEvent.isStarted())) {
//
//	            player.setTarget(null);
//	            doCastlist(player);
//	            return;
//	        }
//	        Mage_Surrender(player, target, 1083, 17, Config.PHANTOM_SURRENDER_INTERVAL, 25);
//	        if (!player.isDead()) {
//	            Attack_Target(player, target);
//	        }
//	    }
//	    
//	    static void Mage_Surrender(final Player player, final Player target, final int skill_id, final int skill_level, final int delay, final int random) {
//	        if (player.isSpawnProtected()) {
//	            player.setSpawnProtection(false);
//	        }
//	        if (!player.isDead() && !player.isAllSkillsDisabled() && !player.isAttackP()) {
//	            checkRange(player, target);
//	            if (!player.isDead() && player.isInsideRadius(target.getX(), target.getY(), target.getZ(), Config.PHANTOM_MAGE_RANGE, false, false) && GeoEngine.getInstance().canSeeTarget(player, target)) {
//	                if (!target.isInvul()) {
//	                    final L2Skill skill = SkillTable.getInstance().getInfo(skill_id, skill_level);
//	                    skill.getEffects(target, target);
//	                }
//	 
//	                player.getAI().setIntention(CtrlIntention.FOLLOW, null);
//	                player.broadcastPacket(new MagicSkillUse(player, target, skill_id, skill_level, 500, 0, false));
//	                target.getActingPlayer().getAI().clientStartAutoAttack();
//	                player.getActingPlayer().getAI().clientStartAutoAttack();
//	                try {
//	                    Thread.sleep(delay);
//	                }
//	                catch (InterruptedException ex) {}
//	                player.getAI().setIntention(IntentionType.FOLLOW, null);
//	            }
//	        }
//	    }
//	    
//	    static void Mage_Attack(final Player player, final Player target, final int skill_id, final int skill_level, final int delay) {
//	        if (player.isSpawnProtected()) {
//	            player.setSpawnProtection(false);
//	        }
//	        if (!player.isDead() && !player.isAllSkillsDisabled() && !player.isAttackP()) {
//	            checkRange(player, target);
//	            if (!player.isDead() && player.isInsideRadius(target.getX(), target.getY(), target.getZ(), Config.PHANTOM_MAGE_RANGE, false, false) && GeoEngine.getInstance().canSeeTarget(player, target) && TvT.is_started()) {
//	                player.stopMove(null);
//	                player.getAI().setIntention(IntentionType.FOLLOW, null);
//	                player.broadcastPacket(new MagicSkillUse(player, target, skill_id, skill_level, Config.PHANTOM_SPELLSINGER_EFFECT, 0, false));
//	                target.getActingPlayer().getAI().clientStartAutoAttack();
//	                player.getActingPlayer().getAI().clientStartAutoAttack();
//	                final double mDef = target.getMDef(player, null);
//	                final double damage = 91.0 * Math.sqrt(Config.PHANTOM_ARCHMAGE_MATCK) / mDef * 1000.0;
//	                if (Rnd.get(100) < Config.PHANTOM_ARCHMAGE_PERCENTAGE) {
//	                    target.reduceCurrentHp(damage, player, null);
//	                }
//	                else {
//	                    target.reduceCurrentHp(damage / 2.0, player, null);
//	                }
//	                try {
//	                    Thread.sleep(delay);
//	                }
//	                catch (InterruptedException ex) {}
//	                player.getAI().setIntention(CtrlIntention.FOLLOW, null);
//	            }
//	        }
//	    }
//	    
//	    static void Attack_Target(final Player player, final Player target) {
//	        boolean doAtack = true;
//	        while (doAtack) {
//	            if (player.isAttackP()) {
//	                doAtack = false;
//	            }
//	            else if (!target.isDead() && player.getTarget() != null && GeoEngine.getInstance().canSeeTarget(player, target) && TvT.is_started()) {
//	                doAtack = true;
//	                Mage_Attack(player, target, 1230, 1, Config.PHANTOM_ARCHMAGE_DANO_INTERVAL);
//	            }
//	            else if (!player.isDead() && player.getTarget() != null && !target.isDead() && TvT.is_started() && !GeoEngine.getInstance().canSeeTarget(player, target)) {
//	                doAtack = false;
//	                Seguir(player, target);
//	            }
//	            else if (!player.isDead() && (target.isDead() || player.getTarget() == null || !TvT.is_started())) {
//	                doAtack = false;
//	                player.stopMove(null);
//	                player.setTarget(null);
//	                if (Rnd.get(100) < 60) {
//	                    player.rndWalkTvT();
//	                    try {
//	                        Thread.sleep(2500L);
//	                    }
//	                    catch (InterruptedException ex) {}
//	                }
//	                doCastlist(player);
//	            }
//	            else {
//	                doAtack = false;
//	            }
//	        }
//	    }
//	    
//	    static void checkRange(final Player player, final Player target) {
//	        if (!player.isDead() && !player.isInsideRadius(target.getX(), target.getY(), target.getZ(), Config.PHANTOM_MAGE_RANGE, false, false) && !player.isMovementDisabled()) {
//	            player.getAI().setIntention(IntentionType.FOLLOW, target);
//	            try {
//	                Thread.sleep(1200L);
//	            }
//	            catch (InterruptedException ex) {}
//	        }
//	    }
//	    
//
//	    
//	    public static boolean is_task_1() {
//	        return ArchMage_TvT._task_1;
//	    }
//	    
//	    public static boolean is_task_2() {
//	        return ArchMage_TvT._task_2;
//	    }
//	    
//	    public static boolean is_task_3() {
//	        return ArchMage_TvT._task_3;
//	    }
//	    
//	    public static boolean is_task_4() {
//	        return ArchMage_TvT._task_4;
//	    }
//	    
//	    public static boolean is_task_5() {
//	        return ArchMage_TvT._task_5;
//	    }
//	    
//	    public static boolean is_task_6() {
//	        return ArchMage_TvT._task_6;
//	    }
//	    
//	    public static boolean is_task_7() {
//	        return ArchMage_TvT._task_7;
//	    }
//	    
//	    public static boolean is_task_8() {
//	        return ArchMage_TvT._task_8;
//	    }
//	    
//	    static Location getRandomLoc() {
//	        Location loc = null;
//	        if (loc == null) {
//	            loc = ArchMage_TvT._PhantomsTownLoc.get(Rnd.get(0, ArchMage_TvT._locsCount));
//	        }
//	        return loc;
//	    }
//	    
//	    
//	    static class L2Set
//	    {
//	        public int _body;
//	        public int _gaiters;
//	        public int _gloves;
//	        public int _boots;
//	        public int _weapon;
//	        public int _custom;
//	        public int _grade;
//	        
//	        L2Set(final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4, final int paramInt5, final int paramInt6, final int paramInt7) {
//	            this._body = paramInt1;
//	            this._gaiters = paramInt2;
//	            this._gloves = paramInt3;
//	            this._boots = paramInt4;
//	            this._weapon = paramInt5;
//	            this._grade = paramInt6;
//	            this._custom = paramInt7;
//	        }
//	    }
//	    
//	    static class L2Fantome
//	    {
//	        public String name;
//	        public String title;
//	        public int x;
//	        public int y;
//	        public int z;
//	        
//	        L2Fantome(final String paramString1, final String paramString2, final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4) {
//	            this.name = paramString1;
//	            this.title = paramString2;
//	            this.x = paramInt1;
//	            this.y = paramInt2;
//	            this.z = paramInt3;
//	        }
//	    }
//	    
//	    public class FantomTask implements Runnable
//	    {
//	        public int _task;
//	        
//	        public FantomTask(final int paramInt) {
//	            this._task = paramInt;
//	        }
//	        
//	        @Override
//	        public void run() {
//	            WhileLoopExample.spawn();
//	        }
//	    }
//	    
//	    static class WhileLoopExample
//	    {
//	        public static void spawn() {
//	            if (Config.TVT_PHANTON_TASK_1_LIST.contains(ArchMage_TvT.sdf.format(new Date(System.currentTimeMillis())))) {
//	                ArchMage_TvT._task_2 = false;
//	                ArchMage_TvT._task_3 = false;
//	                ArchMage_TvT._task_4 = false;
//	                ArchMage_TvT._task_1 = true;
//	            }
//	            else if (Config.TVT_PHANTON_TASK_2_LIST.contains(ArchMage_TvT.sdf.format(new Date(System.currentTimeMillis())))) {
//	                ArchMage_TvT._task_1 = false;
//	                ArchMage_TvT._task_3 = false;
//	                ArchMage_TvT._task_4 = false;
//	                ArchMage_TvT._task_2 = true;
//	            }
//	            else if (Config.TVT_PHANTON_TASK_3_LIST.contains(ArchMage_TvT.sdf.format(new Date(System.currentTimeMillis())))) {
//	                ArchMage_TvT._task_1 = false;
//	                ArchMage_TvT._task_2 = false;
//	                ArchMage_TvT._task_4 = false;
//	                ArchMage_TvT._task_3 = true;
//	            }
//	            else if (Config.TVT_PHANTON_TASK_4_LIST.contains(ArchMage_TvT.sdf.format(new Date(System.currentTimeMillis())))) {
//	                ArchMage_TvT._task_1 = false;
//	                ArchMage_TvT._task_2 = false;
//	                ArchMage_TvT._task_3 = false;
//	                ArchMage_TvT._task_4 = true;
//	            }
//	            else {
//	                ArchMage_TvT._task_1 = true;
//	                ArchMage_TvT._task_2 = false;
//	                ArchMage_TvT._task_3 = false;
//	                ArchMage_TvT._task_4 = false;
//	            }
//	            int i = 0;
//	            if (ArchMage_TvT.is_task_1()) {
//	                i = Config.TVT_PHANTON_TASK_1_CONT_ARCHMAGE;
//	            }
//	            else if (ArchMage_TvT.is_task_2()) {
//	                i = Config.TVT_PHANTON_TASK_2_CONT_ARCHMAGE;
//	            }
//	            else if (ArchMage_TvT.is_task_3()) {
//	                i = Config.TVT_PHANTON_TASK_3_CONT_ARCHMAGE;
//	            }
//	            else if (ArchMage_TvT.is_task_4()) {
//	                i = Config.TVT_PHANTON_TASK_4_CONT_ARCHMAGE;
//	            }
//	            while (i > 0) {
//	                ArchMage_TvT.spawnPlayer();
//	                try {
//	                    Thread.sleep(Rnd.get(2100, 5200));
//	                }
//	                catch (InterruptedException ex) {}
//	                --i;
//	            }
//	        }
//	    }
//}
//