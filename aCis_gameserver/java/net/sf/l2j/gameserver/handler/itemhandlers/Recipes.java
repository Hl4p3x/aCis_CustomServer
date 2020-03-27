package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.xml.RecipeData;
import net.sf.l2j.gameserver.enums.actors.OperateType;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.Recipe;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.RecipeBookItemList;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class Recipes implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		
		if (!Config.IS_CRAFTING_ENABLED)
		{
			player.sendMessage("Crafting is disabled, you cannot register this recipe.");
			return;
		}
		
		if (player.isCrafting())
		{
			player.sendPacket(SystemMessageId.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
			return;
		}
		
		final Recipe recipe = RecipeData.getInstance().getRecipeByItemId(item.getItemId());
		if (recipe == null)
			return;
		
		if (player.hasRecipeList(recipe.getId()))
		{
			player.sendPacket(SystemMessageId.RECIPE_ALREADY_REGISTERED);
			return;
		}
		
		if (recipe.isDwarven())
		{
			if (!player.hasDwarvenCraft())
				player.sendPacket(SystemMessageId.CANT_REGISTER_NO_ABILITY_TO_CRAFT);
			else if (player.getOperateType() == OperateType.MANUFACTURE)
				player.sendPacket(SystemMessageId.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
			else if (recipe.getLevel() > player.getSkillLevel(L2Skill.SKILL_CREATE_DWARVEN))
				player.sendPacket(SystemMessageId.CREATE_LVL_TOO_LOW_TO_REGISTER);
			else if (player.getDwarvenRecipeBook().size() >= player.getDwarfRecipeLimit())
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_REGISTER).addNumber(player.getDwarfRecipeLimit()));
			else if (player.destroyItem("Consume", item.getObjectId(), 1, null, false))
			{
				player.registerDwarvenRecipeList(recipe);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ADDED).addItemName(item));
				player.sendPacket(new RecipeBookItemList(player, true));
			}
		}
		else
		{
			if (!player.hasCommonCraft())
				player.sendPacket(SystemMessageId.CANT_REGISTER_NO_ABILITY_TO_CRAFT);
			else if (player.getOperateType() == OperateType.MANUFACTURE)
				player.sendPacket(SystemMessageId.CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING);
			else if (recipe.getLevel() > player.getSkillLevel(L2Skill.SKILL_CREATE_COMMON))
				player.sendPacket(SystemMessageId.CREATE_LVL_TOO_LOW_TO_REGISTER);
			else if (player.getCommonRecipeBook().size() >= player.getCommonRecipeLimit())
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.UP_TO_S1_RECIPES_CAN_REGISTER).addNumber(player.getCommonRecipeLimit()));
			else if (player.destroyItem("Consume", item.getObjectId(), 1, null, false))
			{
				player.registerCommonRecipeList(recipe);
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ADDED).addItemName(item));
				player.sendPacket(new RecipeBookItemList(player, false));
			}
		}
	}
}