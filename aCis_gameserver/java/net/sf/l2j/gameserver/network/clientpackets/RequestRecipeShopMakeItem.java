package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.commons.math.MathUtil;

import net.sf.l2j.gameserver.data.xml.RecipeData;
import net.sf.l2j.gameserver.enums.actors.OperateType;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.craft.RecipeItemMaker;
import net.sf.l2j.gameserver.model.item.Recipe;
import net.sf.l2j.gameserver.network.FloodProtectors;
import net.sf.l2j.gameserver.network.FloodProtectors.Action;
import net.sf.l2j.gameserver.network.SystemMessageId;

public final class RequestRecipeShopMakeItem extends L2GameClientPacket
{
	private int _objectId;
	private int _recipeId;
	@SuppressWarnings("unused")
	private int _unknow;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_recipeId = readD();
		_unknow = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (!FloodProtectors.performAction(getClient(), Action.MANUFACTURE))
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Player manufacturer = World.getInstance().getPlayer(_objectId);
		if (manufacturer == null)
			return;
		
		if (player.isOperating())
			return;
		
		if (manufacturer.getOperateType() != OperateType.MANUFACTURE)
			return;
		
		if (player.isCrafting() || manufacturer.isCrafting())
			return;
		
		if (manufacturer.isInDuel() || player.isInDuel() || manufacturer.isInCombat() || player.isInCombat())
		{
			player.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			return;
		}
		
		if (!MathUtil.checkIfInRange(150, player, manufacturer, true))
			return;
		
		final Recipe recipe = RecipeData.getInstance().getRecipeList(_recipeId);
		if (recipe == null)
			return;
		
		if (recipe.isDwarven())
		{
			if (!manufacturer.getDwarvenRecipeBook().contains(recipe))
				return;
		}
		else
		{
			if (!manufacturer.getCommonRecipeBook().contains(recipe))
				return;
		}
		
		final RecipeItemMaker maker = new RecipeItemMaker(manufacturer, recipe, player);
		if (maker._isValid)
			maker.run();
	}
}