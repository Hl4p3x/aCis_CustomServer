package Dev.Phantom.Ai;

import java.util.ArrayList;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Weapon;

import Dev.Phantom.FakePlayer;
import Dev.Phantom.FakePlayerManager;
import Dev.Phantom.Helpers.FakeHelpers;

public class EnchanterAI extends FakePlayerAI {

	private int _enchantIterations = 15;
	//private int _maxEnchant = Config.ENCHANT_MAX_WEAPON;
	private final int iterationsForAction = Rnd.get(5,10);
	
	public EnchanterAI(FakePlayer character) {
		super(character);
	}
	
	@Override
	public void setup() {
		super.setup();
		ItemInstance weapon = _fakePlayer.getActiveWeaponInstance();		
		weapon = checkIfWeaponIsExistsEquipped(weapon);		
		weapon.setEnchantLevel(0);
		_fakePlayer.broadcastCharInfo();
		
	}

	@Override
	public void thinkAndAct() {		
		
		handleDeath();
		setBusyThinking(true);
		if(_enchantIterations % iterationsForAction == 0) {	
			ItemInstance weapon = _fakePlayer.getActiveWeaponInstance();		
			weapon = checkIfWeaponIsExistsEquipped(weapon);			
			double chance = getSuccessChance(weapon);		
						
			int currentEnchantLevel = weapon.getEnchantLevel();
			if(currentEnchantLevel < 25 || serverHasUnlimitedMax()) {
				if (Rnd.nextDouble() < chance || weapon.getEnchantLevel() < 4) {				
					weapon.setEnchantLevel(currentEnchantLevel + 1);
					_fakePlayer.broadcastCharInfo();
				} else {				
					destroyFailedItem(weapon);
				}							
			}
		}
		_enchantIterations++;	
		setBusyThinking(false);
	}
	
	private void destroyFailedItem(ItemInstance weapon) {
		_fakePlayer.getInventory().destroyItem("Enchant", weapon, _fakePlayer, null);
		_fakePlayer.broadcastUserInfo();
		_fakePlayer.setActiveEnchantItem(null);
	}
	
	private static double getSuccessChance(ItemInstance weapon) {
		double chance = 0d;
		if (((Weapon) weapon.getItem()).isMagical())
			chance = (weapon.getEnchantLevel() > 10) ? 15 : 20;
		else
			chance = (weapon.getEnchantLevel() > 10) ? 15 : 20;
		return chance;
	}
	
	private static boolean serverHasUnlimitedMax() {
		return 25 == 0;
	}
	
	private ItemInstance checkIfWeaponIsExistsEquipped(ItemInstance weapon) {
		if(weapon == null) {
			FakeHelpers.giveWeaponsByClass(_fakePlayer, false);
			weapon = _fakePlayer.getActiveWeaponInstance();
		}
		return weapon;
	}

    @Override
    protected ArrayList<Integer> getBuffs() {
        return FakePlayerManager.getFighterBuffs();
    }
}
