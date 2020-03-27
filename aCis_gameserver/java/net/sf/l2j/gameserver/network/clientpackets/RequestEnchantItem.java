package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.xml.ArmorSetData;
import net.sf.l2j.gameserver.data.xml.EnchantData;
import net.sf.l2j.gameserver.data.xml.EnchantData.EnchantScroll;
import net.sf.l2j.gameserver.enums.items.WeaponType;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.ArmorSet;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance.ItemLocation;
import net.sf.l2j.gameserver.model.item.kind.Armor;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.EnchantResult;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public final class RequestEnchantItem extends L2GameClientPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null || _objectId == 0)
			return;
		
		if (!player.isOnline() || getClient().isDetached())
		{
			player.setActiveEnchantItem(null);
			return;
		}

		if (player.isProcessingTransaction() || player.isOperating())
		{
			player.sendPacket(SystemMessageId.CANNOT_ENCHANT_WHILE_STORE);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}

		if (player.getActiveTradeList() != null)
		{
			player.cancelActiveTrade();
			player.sendPacket(SystemMessageId.TRADE_ATTEMPT_FAILED);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		final ItemInstance item = player.getInventory().getItemByObjectId(_objectId);
		ItemInstance scroll = player.getActiveEnchantItem();
		
		if (item == null || scroll == null)
		{
			player.sendPacket(SystemMessageId.ENCHANT_SCROLL_CANCELLED);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		// template for scroll
		final EnchantScroll enchant = EnchantData.getInstance().getEnchantScroll(scroll);
		if (enchant == null)
			return;
		
		// first validation check
		if (!isEnchantable(item) || !enchant.isValid(item) || item.getOwnerId() != player.getObjectId())
		{
			player.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		// attempting to destroy scroll
		scroll = player.getInventory().destroyItem("Enchant", scroll.getObjectId(), 1, player, item);
		if (scroll == null)
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		synchronized (item)
		{
			// success
			if (Rnd.get(100) < enchant.getChance(item))
			{
				// announce the success
				SystemMessage sm;
				
				if (item.getEnchantLevel() == 0)
					sm = SystemMessage.getSystemMessage(SystemMessageId.S1_SUCCESSFULLY_ENCHANTED);
				else
				{
					sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_SUCCESSFULLY_ENCHANTED);
					sm.addNumber(item.getEnchantLevel());
				}
				sm.addItemName(item.getItemId());
				player.sendPacket(sm);
				 				
				item.setEnchantLevel(item.getEnchantLevel() + 1);
				item.updateDatabase();
				
				// If item is equipped, verify the skill obtention (+4 duals, +6 armorset).
				if (item.isEquipped())
				{
					final Item it = item.getItem();
					
					// Add skill bestowed by +4 duals.
					if (it instanceof Weapon && item.getEnchantLevel() == 4)
					{
						final L2Skill enchant4Skill = ((Weapon) it).getEnchant4Skill();
						if (enchant4Skill != null)
						{
							player.addSkill(enchant4Skill, false);
							player.sendSkillList();
						}
					}
					// Add skill bestowed by +6 armorset.
					else if (it instanceof Armor && item.getEnchantLevel() == 6)
					{
						// Checks if player is wearing a chest item
						final ItemInstance chestItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
						if (chestItem != null)
						{
							final ArmorSet armorSet = ArmorSetData.getInstance().getSet(chestItem.getItemId());
							if (armorSet != null && armorSet.isEnchanted6(player)) // has all parts of set enchanted to 6 or more
							{
								final int skillId = armorSet.getEnchant6skillId();
								if (skillId > 0)
								{
									final L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
									if (skill != null)
									{
										player.addSkill(skill, false);
										player.sendSkillList();
									}
								}
							}
						}
					}
				}
				player.sendPacket(EnchantResult.SUCCESS);
				
				if (enchant.announceTheEnchant(item))
					World.announceToOnlinePlayers(String.format(enchant.getAnnounceMessage(), player.getName(), "+" + item.getEnchantLevel() + " " + item.getName()), true);
			}
			else
			{
				// Drop passive skills from items.
				if (item.isEquipped() && (enchant.canCrystalized() || enchant.getReturnValue() != -1))
				{
					final Item it = item.getItem();
					
					// Remove skill bestowed by +4 duals.
					if (it instanceof Weapon && item.getEnchantLevel() >= 4)
					{
						final L2Skill enchant4Skill = ((Weapon) it).getEnchant4Skill();
						if (enchant4Skill != null)
						{
							player.removeSkill(enchant4Skill.getId(), false);
							player.sendSkillList();
						}
					}
					// Add skill bestowed by +6 armorset.
					else if (it instanceof Armor && item.getEnchantLevel() >= 6)
					{
						// Checks if player is wearing a chest item
						final ItemInstance chestItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
						if (chestItem != null)
						{
							final ArmorSet armorSet = ArmorSetData.getInstance().getSet(chestItem.getItemId());
							if (armorSet != null && armorSet.isEnchanted6(player)) // has all parts of set enchanted to 6 or more
							{
								final int skillId = armorSet.getEnchant6skillId();
								if (skillId > 0)
								{
									player.removeSkill(skillId, false);
									player.sendSkillList();
								}
							}
						}
					}
				}
				
				if (!enchant.canCrystalized())
				{
					// blessed enchant - clear enchant value
					player.sendPacket(SystemMessageId.BLESSED_ENCHANT_FAILED);
					if (enchant.getReturnValue() != -1)
					{
						item.setEnchantLevel(enchant.getReturnValue());
						item.updateDatabase();
					}
					player.sendPacket(EnchantResult.UNSUCCESS);
				}
				else
				{
					ItemInstance destroyItem = player.getInventory().destroyItem("Enchant", item, player, null);
					if (destroyItem == null)
					{
						player.setActiveEnchantItem(null);
						player.sendPacket(EnchantResult.CANCELLED);
						return;
					}
					
					// add crystals, if item crystalizable
					int crystalId = item.getItem().getCrystalItemId();
					{
						// get crystals count
						int crystalCount = item.getCrystalCount() - (item.getItem().getCrystalCount() + 1) / 2;
						if (crystalCount < 1)
							crystalCount = 1;
						
						// add crystals to inventory
						player.getInventory().addItem("Enchant", crystalId, crystalCount, player, destroyItem);
						
						// send message
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(crystalId).addItemNumber(crystalCount));
					}
					
					InventoryUpdate iu = new InventoryUpdate();
					if (destroyItem.getCount() == 0)
						iu.addRemovedItem(destroyItem);
					else
						iu.addModifiedItem(destroyItem);
					
					player.sendPacket(iu);
					
					// update weight
					StatusUpdate su = new StatusUpdate(player);
					su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
					player.sendPacket(su);
					
					// send message
					if (item.getEnchantLevel() > 0)
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ENCHANTMENT_FAILED_S1_S2_EVAPORATED).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
					else
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ENCHANTMENT_FAILED_S1_EVAPORATED).addItemName(item.getItemId()));
					
					if (crystalId == 0)
						player.sendPacket(EnchantResult.UNK_RESULT_4);
					else
						player.sendPacket(EnchantResult.UNK_RESULT_1);
				}
			}
			
			player.sendPacket(new ItemList(player, false));
			player.broadcastUserInfo();
			player.setActiveEnchantItem(null);
		}
	}

	/**
	 * @param item The instance of item to make checks on.
	 * @return true if item can be enchanted.
	 */
	private static final boolean isEnchantable(ItemInstance item)
	{
		if (item.isHeroItem() || item.isShadowItem() || item.isEtcItem() || item.getItem().getItemType() == WeaponType.FISHINGROD)
			return false;
		
		// only equipped items or in inventory can be enchanted
		if (item.getLocation() != ItemLocation.INVENTORY && item.getLocation() != ItemLocation.PAPERDOLL)
			return false;
		
		return true;
	}
}