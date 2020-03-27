package Dev.Phantom.Ai;



import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.instance.Door;
import net.sf.l2j.gameserver.model.actor.stat.Experience;
import net.sf.l2j.gameserver.model.actor.Player;

import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.location.SpawnLocation;
import net.sf.l2j.gameserver.network.serverpackets.MoveToLocation;
import net.sf.l2j.gameserver.network.serverpackets.MoveToPawn;
import net.sf.l2j.gameserver.network.serverpackets.StopMove;
import net.sf.l2j.gameserver.network.serverpackets.StopRotation;
import net.sf.l2j.gameserver.network.serverpackets.TeleportToLocation;

import Dev.Phantom.FakePlayer;

/**
 * @author Rouxy
 *
 */
public abstract class FakePlayerAI
{
	protected final FakePlayer _fakePlayer;		
	protected volatile boolean _clientMoving;
	protected volatile boolean _clientAutoAttacking;
	private long _moveToPawnTimeout;
	protected int _clientMovingToPawnOffset;	
	protected boolean _isBusyThinking = false;
	protected int iterationsOnDeath = 0;
	private final int toVillageIterationsOnDeath = 15;
	
	public FakePlayerAI(FakePlayer character)
	{
		_fakePlayer = character;
		setup();
		applyDefaultBuffs();
	}
	
	public void setup() {
		_fakePlayer.setIsRunning(true);
	}
	

	
	 protected void applyDefaultBuffs() {
	        for (final Integer skillid : getBuffs()) {
	            try {
	                final L2Skill skill = SkillTable.getInstance().getInfo(skillid, SkillTable.getInstance().getMaxLevel(skillid));
	                if (skill == null) {
	                    continue;
	                }
	                skill.getEffects(_fakePlayer, _fakePlayer);
	            }
	            catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }	
	
	protected void handleDeath() 
	{
		//if(_fakePlayer.isDead() && !(_fakePlayer.isInsideZone(ZoneId.FLAG) || _fakePlayer.isInsideZone(ZoneId.CHANGE_PVP_ZONE))) {
		if(_fakePlayer.isDead()){	
			if(iterationsOnDeath >= toVillageIterationsOnDeath)
			{
				toVillageOnDeath();
				setBusyThinking(true);
				if(Config.FAKE_PLAYER_RETURNS_TO_FARM == true)
				{
					rebuffAfterDeathAndTeleportBack();
				}
			}
			iterationsOnDeath++;
			return;
		}
		
		iterationsOnDeath = 5;		
	}
	
    public void setBusyThinking(final boolean thinking) {
        _isBusyThinking = thinking;
    }
    
    public boolean isBusyThinking() {
        return _isBusyThinking;
    }
	
	protected void teleportToLocation(int x, int y, int z, int randomOffset) {
	
		_fakePlayer.abortAttack();
		_fakePlayer.abortCast();		
		_fakePlayer.setIsTeleporting(true);
		_fakePlayer.setTarget(null);		
		_fakePlayer.getAI().setIntention(IntentionType.ACTIVE);	
		
		if (randomOffset > 0)
		{
			x += Rnd.get(-randomOffset, randomOffset);
			y += Rnd.get(-randomOffset, randomOffset);
		}		

	
	z += 5;
	_fakePlayer.broadcastPacket(new TeleportToLocation(_fakePlayer, x, y, z, true));
	_fakePlayer.decayMe();		
	_fakePlayer.setXYZ(x, y, z);
	_fakePlayer.onTeleported();		
	_fakePlayer.revalidateZone(true);
}
	
	
    
   
	protected void tryTargetRandomCreatureByTypeInRadius(Class<? extends Creature> creatureClass, int radius)
	{
		if(_fakePlayer.getTarget() == null) {
			List<Creature> targets = _fakePlayer.getKnownTypeInRadius(creatureClass, radius).stream().filter(x->!x.isDead()).collect(Collectors.toList());
			if(!targets.isEmpty()) {
				Creature target = targets.get(Rnd.get(0, targets.size() -1 ));
				//List<Player> targetFlag = _fakePlayer.get
				_fakePlayer.setTarget(target);	
				
			}
			
		}else 
		{
			
			if (_fakePlayer != null && ((Creature)_fakePlayer).isInsideRadius(_fakePlayer.getX(), _fakePlayer.getY(), _fakePlayer.getZ(), 400, false, false))
				((Creature) _fakePlayer.getTarget()).isDead();
			
			else if (_fakePlayer != null && ((Creature)_fakePlayer).isInsideRadius(_fakePlayer.getX(), _fakePlayer.getY(), _fakePlayer.getZ(), 20, false, false))
				((Creature) _fakePlayer.getTarget()).isDead();
			
			if (_fakePlayer != null && ((Creature)_fakePlayer).isInsideRadius(_fakePlayer.getX(), _fakePlayer.getY(), _fakePlayer.getZ(), 20, false, false))
				((Creature) _fakePlayer.getTarget()).isDead();
			
			
			else if (_fakePlayer != null && ((Creature)_fakePlayer).isInsideRadius(_fakePlayer.getX(), _fakePlayer.getY(), _fakePlayer.getZ(), 400, false, false))
				((Creature) _fakePlayer.getTarget()).isDead();
				
			if (_fakePlayer != null && ((Creature)_fakePlayer).isInsideRadius(_fakePlayer.getX(), _fakePlayer.getY(), _fakePlayer.getZ(), 20, false, false))
				((Creature) _fakePlayer.getTarget()).isDead();
			
			
			else if (_fakePlayer != null && ((Creature)_fakePlayer).isInsideRadius(_fakePlayer.getX(), _fakePlayer.getY(), _fakePlayer.getZ(), 20, false, false))
				((Creature) _fakePlayer.getTarget()).isDead();
			
			
		} 
		
	}

	
	protected void tryFlagTargetRandom(Class<? extends Player> creatureClass, int radius)
	{
		if(_fakePlayer.getTarget() == null) {
			List<Player> targets = _fakePlayer.getKnownTypeInRadius(creatureClass, radius).stream().filter(x->!x.isDead()).collect(Collectors.toList());
			if(!targets.isEmpty()) 
			{
				Player target = targets.get(Rnd.get(0, targets.size() -1 ));
				//List<Player> targetFlag = _fakePlayer.get
				if(target.getKarma() > 0)
				{
				_fakePlayer.setTarget(target);	
				}
				if(target.getPvpFlag() > 0)
					{
					_fakePlayer.setTarget(target);	
					}
			}
		}else 
		{
			if(((Creature)_fakePlayer.getTarget()).isDead())
			{
				
			_fakePlayer.setTarget(null);
			}
			
		}	
	}
		
	

	


		
    public void castSpell(final L2Skill skill) {
        if (!_fakePlayer.isCastingNow()) {
            if (skill.getTargetType() == L2Skill.SkillTargetType.TARGET_GROUND) {
                if (maybeMoveToPosition(_fakePlayer.getCurrentSkillWorldPosition(), skill.getCastRange())) {
                    _fakePlayer.setIsCastingNow(false);
                    return;
                }
            }
            else {
                if (checkTargetLost(_fakePlayer.getTarget())) {
                    if (skill.isOffensive() && _fakePlayer.getTarget() != null) {
                        _fakePlayer.setTarget(null);
                    }
                    _fakePlayer.setIsCastingNow(false);
                    return;
                }
                if (_fakePlayer.getTarget() != null && maybeMoveToPawn(_fakePlayer.getTarget(), skill.getCastRange())) {
                    return;
                }
                if (_fakePlayer.isSkillDisabled(skill)) {
                    return;
                }
            }
            if (skill.getHitTime() > 50 && !skill.isSimultaneousCast()) {
                clientStopMoving(null);
            }
            _fakePlayer.doCast(skill);
        }
        else {
            _fakePlayer.forceAutoAttack((Creature)_fakePlayer.getTarget());
        }
    }
	
    protected void castSelfSpell(final L2Skill skill) {
        if (!_fakePlayer.isCastingNow() && !_fakePlayer.isSkillDisabled(skill)) {
            if (skill.getHitTime() > 50 && !skill.isSimultaneousCast()) {
                clientStopMoving(null);
            }
            _fakePlayer.doCast(skill);
        }
    }
	

	
	public void toVillageOnDeath(){
	

		
	if(_fakePlayer.isDead() || _fakePlayer.isInsideZone(ZoneId.PVP) || _fakePlayer.isInsideZone(ZoneId.BOSS) || _fakePlayer.isInsideZone(ZoneId.PEACE) || _fakePlayer.isInsideZone(ZoneId.TOWN))
		_fakePlayer.doRevive();	
		         
		 // HEAL FAKES NO REVIVE 
		 _fakePlayer.setCurrentCp (_fakePlayer.getMaxCp ()); 
		 _fakePlayer.setCurrentHp (_fakePlayer.getMaxHp ()); 
		 _fakePlayer.setCurrentMp (_fakePlayer.getMaxMp ()); 
		
		 _fakePlayer.addExpAndSp(Experience.LEVEL[81], 0);
	   
	}
	
	
	protected void rebuffAfterDeathAndTeleportBack()
	{		
		switch (_fakePlayer.getClassId())
		{	
		case ARCHMAGE:
		case SOULTAKER:
		case HIEROPHANT:
		case ARCANA_LORD:
		case CARDINAL:
		case MYSTIC_MUSE:
		case ELEMENTAL_MASTER:
		case EVAS_SAINT:
		case STORM_SCREAMER:
		case SPECTRAL_MASTER:
		case SHILLIEN_SAINT:
		case DOMINATOR:
		case DOOMCRYER:
			_fakePlayer.getFakeAi().applyDefaultBuffs();
			break;
		case DUELIST:
		//case DREADNOUGHT:
		case PHOENIX_KNIGHT:
		case SWORD_MUSE:
		case HELL_KNIGHT:
		case SPECTRAL_DANCER:
		case EVAS_TEMPLAR:
		case SHILLIEN_TEMPLAR:
		case TITAN:
		case MAESTRO:
		case SAGGITARIUS:
		case ADVENTURER:
		case WIND_RIDER:
		case MOONLIGHT_SENTINEL:
		case GHOST_HUNTER:
		case GHOST_SENTINEL:
		case FORTUNE_SEEKER:
		case GRAND_KHAVATARI:
			_fakePlayer.getFakeAi().applyDefaultBuffs();
			break;
		default:
			break;
		}
		teleportBackFarmzone();
	}
	//static int [] spawnloc1 = {148989, -168447, 2008};
	static int [] spawnloc2 = Config.SPAWN_FAKE_LOC2;
	static int [] spawnloc1 = Config.SPAWN_FAKE_LOC1;
	static int radius = 3000;
	
	
	protected void teleportBackFarmzone()
	{
		if (spawnloc2[0] >= spawnloc1[0])
		{
			spawnloc2[0]= spawnloc1[0]-100;
			
		}
		if (spawnloc2[1] >= spawnloc1[1])
		{
			spawnloc2[1]= spawnloc1[1]-100;
			
		}
		if (spawnloc1[2] >= spawnloc2[2])
		{
			spawnloc1[2]= spawnloc2[2]-100;
			
		}
		int x = Rnd.get(spawnloc2[0],spawnloc1[0]);
		int y = Rnd.get(spawnloc2[1],spawnloc1[1]);
		int z = Rnd.get(spawnloc1[2]+20,spawnloc2[2]+20);
		int randomOffset = Config.FAKE_SPAWN_RANDOM_OFFSET;
		_fakePlayer.getFakeAi().teleportToLocation(x, y, z, randomOffset);
		
	}
	
	
	
	  protected void clientStopMoving(final SpawnLocation loc) {
	        if (_fakePlayer.isMoving()) {
	           
	        }
	        _clientMovingToPawnOffset = 0;
	        if (_clientMoving || loc != null) {
	            _clientMoving = false;
	            _fakePlayer.broadcastPacket(new StopMove(_fakePlayer));
	            if (loc != null) {
	                _fakePlayer.broadcastPacket(new StopRotation(_fakePlayer.getObjectId(), loc.getHeading(), 0));
	            }
	        }
	    }
	  
	
	    protected boolean checkTargetLost(final WorldObject target) {
	        if (target instanceof Playable) {

	        }
	        if (target == null) {
	        	_fakePlayer.getAI().setIntention(IntentionType.ACTIVE);	
	            return true;
	        }
	        return false;
	    }
	
	    protected boolean maybeMoveToPosition(final Location worldPosition, final int offset) {
	        if (worldPosition == null) {
	            return false;
	        }
	        if (offset < 0) {
	            return false;
	        }
	        if (_fakePlayer.isInsideRadius(worldPosition.getX(), worldPosition.getY(), (int)(offset + _fakePlayer.getCollisionRadius()), false)) {
	            return false;
	        }
	        if (_fakePlayer.isMovementDisabled()) {
	            return true;
	        }
	        int x = _fakePlayer.getX();
	        int y = _fakePlayer.getY();
	        final double dx = worldPosition.getX() - x;
	        final double dy = worldPosition.getY() - y;
	        double dist = Math.sqrt(dx * dx + dy * dy);
	        final double sin = dy / dist;
	        final double cos = dx / dist;
	        dist -= offset - 5;
	        x += (int)(dist * cos);
	        y += (int)(dist * sin);
	        moveTo(x, y, worldPosition.getZ());
	        return true;
	    }
	
	    protected void moveToPawn(final WorldObject pawn, int offset) {
	        if (!_fakePlayer.isMovementDisabled()) {
	            if (offset < 10) {
	                offset = 10;
	            }
	            boolean sendPacket = true;
	            if (_clientMoving && _fakePlayer.getTarget() == pawn) {
	                if (_clientMovingToPawnOffset == offset) {
	                    if (System.currentTimeMillis() < _moveToPawnTimeout) {
	                        return;
	                    }
	                    sendPacket = false;
	                }
	                else if (_fakePlayer.isOnGeodataPath() && System.currentTimeMillis() < _moveToPawnTimeout + 1000L) {
	                    return;
	                }
	            }
	            _clientMoving = true;
	            _clientMovingToPawnOffset = offset;
	            _fakePlayer.setTarget(pawn);
	            _moveToPawnTimeout = System.currentTimeMillis() + 1000L;
	            if (pawn == null) {
	                return;
	            }
	            _fakePlayer.moveToLocation(pawn.getX(), pawn.getY(), pawn.getZ(), offset);
	            if (!_fakePlayer.isMoving()) {
	                return;
	            }
	            if (pawn instanceof Creature) {
	                if (_fakePlayer.isOnGeodataPath()) {
	                    _fakePlayer.broadcastPacket(new MoveToLocation(_fakePlayer));
	                    _clientMovingToPawnOffset = 0;
	                }
	                else if (sendPacket) {
	                    _fakePlayer.broadcastPacket(new MoveToPawn(_fakePlayer, pawn, offset));
	                }
	            }
	            else {
	                _fakePlayer.broadcastPacket(new MoveToLocation(_fakePlayer));
	            }
	        }
	    }
	
	    public void moveTo(final int x, final int y, final int z) {
	        if (!_fakePlayer.isMovementDisabled()) {
	            _clientMoving = true;
	            _clientMovingToPawnOffset = 0;
	            _fakePlayer.moveToLocation(x, y, z, 0);
	            _fakePlayer.broadcastPacket(new MoveToLocation(_fakePlayer));
	        }
	    }
	
	    protected boolean maybeMoveToPawn(final WorldObject target, int offset) {
	        if (target == null || offset < 0) {
	            return false;
	        }
	        offset += (int)_fakePlayer.getCollisionRadius();
	        if (target instanceof Creature) {
	            offset += (int)((Creature)target).getCollisionRadius();
	        }
	        if (!_fakePlayer.isInsideRadius(target, offset, false, false)) {

	            if (target instanceof Creature && !(target instanceof Door)) {
	                if (((Creature)target).isMoving()) {
	                    offset -= 30;
	                }
	                if (offset < 5) {
	                    offset = 5;
	                }
	            }
	            moveToPawn(target, offset);
	            return true;
	        }
	         {

	            return false;
	        }
	    }	
	
	public abstract void thinkAndAct(); 
	protected abstract ArrayList<Integer> getBuffs();
}
