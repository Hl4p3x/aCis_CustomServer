package Dev.Phantom.Ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.ShotType;

import Dev.Phantom.FakePlayer;
import Dev.Phantom.FakePlayerManager;
import Dev.Phantom.Helpers.FakeHelpers;
import Dev.Phantom.Model.HealingSpell;
import Dev.Phantom.Model.OffensiveSpell;
import Dev.Phantom.Model.SupportSpell;
public class ArchmageAI extends CombatAI
{
    public ArchmageAI(final FakePlayer character) {
        super(character);
    }
    
	@Override
	public void thinkAndAct()
	{
		super.thinkAndAct();		
		setBusyThinking(true);		
		applyDefaultBuffs();		
		handleShots();		
		tryTargetRandomCreatureByTypeInRadius(FakeHelpers.getTestTargetClass(), FakeHelpers.getTestTargetRange());		
		if(Config.FAKE_PLAYER_CAN_TARGET_REAL_PLAYER == true)
		{
			tryFlagTargetRandom(FakeHelpers.getFlagTargetClass(), FakeHelpers.getTestTargetRange());
		}
		tryAttackingUsingMageOffensiveSkill();
		
		setBusyThinking(false);
	}
    
    @Override
    protected ShotType getShotType() {
        return ShotType.BLESSED_SPIRITSHOT;
    }
    
    @Override
    protected List<OffensiveSpell> getOffensiveSpells() {
        @SuppressWarnings("unused")
		final List<OffensiveSpell> _offensiveSpells = new ArrayList<OffensiveSpell>();
        _offensiveSpells.add(new OffensiveSpell(1230, 1));
        _offensiveSpells.add(new OffensiveSpell(1339, 1));
        return _offensiveSpells;
    }
    
    @Override
    protected ArrayList<Integer> getBuffs() {
        return FakePlayerManager.getMageBuffs();
    }
    
    @Override
    protected List<HealingSpell> getHealingSpells() {
        return Collections.emptyList();
    }
    
    @Override
    protected List<SupportSpell> getSelfSupportSpells() {
        return Collections.emptyList();
    }
}
