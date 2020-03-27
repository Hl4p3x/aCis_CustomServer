package Dev.Phantom.Ai.Addon;



import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

import Dev.Phantom.FakePlayer;

public interface IConsumableSpender {

	default void handleConsumable(FakePlayer fakePlayer, int consumableId) {
		if(fakePlayer.getInventory().getItemByItemId(consumableId) != null) {
			if(fakePlayer.getInventory().getItemByItemId(consumableId).getCount() <= 20) {
				fakePlayer.getInventory().addItem("", consumableId, 5000, fakePlayer, null);			
				
			}
		}else {
			fakePlayer.getInventory().addItem("", consumableId, 5000, fakePlayer, null);
			ItemInstance consumable = fakePlayer.getInventory().getItemByItemId(consumableId);
			if(consumable.isEquipable())
				fakePlayer.getInventory().equipItem(consumable);
		}
	}
}
