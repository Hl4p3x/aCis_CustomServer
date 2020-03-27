package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.data.manager.BoatManager;
import net.sf.l2j.gameserver.enums.AiEventType;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.enums.items.WeaponType;
import net.sf.l2j.gameserver.model.actor.Boat;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.ai.NextAction;
import net.sf.l2j.gameserver.model.location.BoatEntrance;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.MoveToLocationInVehicle;
import net.sf.l2j.gameserver.network.serverpackets.StopMoveInVehicle;

public final class RequestMoveToLocationInVehicle extends L2GameClientPacket
{
	private int _boatId;
	private int _targetX;
	private int _targetY;
	private int _targetZ;
	private int _originX;
	private int _originY;
	private int _originZ;
	
	@Override
	protected void readImpl()
	{
		_boatId = readD();
		_targetX = readD();
		_targetY = readD();
		_targetZ = readD();
		_originX = readD();
		_originY = readD();
		_originZ = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (_targetX == _originX && _targetY == _originY && _targetZ == _originZ)
		{
			player.sendPacket(new StopMoveInVehicle(player, _boatId));
			return;
		}
		
		if (player.isAttackingNow() && player.getAttackType() == WeaponType.BOW)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.isSitting() || player.isMovementDisabled())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Boat boat;
		if (player.isInBoat())
		{
			boat = player.getBoat();
			if (boat.getObjectId() != _boatId)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			// In case the player is already onboard, we don't want to do any server side movement, except updating position inside the boat.
			player.getBoatPosition().set(_targetX, _targetY, _targetZ);
			
			player.broadcastPacket(new MoveToLocationInVehicle(player, boat, _targetX, _targetY, _targetZ, _originX, _originY, _originZ));
		}
		else
		{
			boat = BoatManager.getInstance().getBoat(_boatId);
			if (boat == null)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final BoatEntrance closestEntrance = boat.getClosestEntrance(player.getPosition());
			
			NextAction nextAction = new NextAction(AiEventType.ARRIVED, IntentionType.MOVE_TO, () ->
			{
				player.getBoatPosition().set(closestEntrance.getInnerLocation());
				
				// Since we're close enough to the boat we just send client onboarding packet without any movement on the server.
				player.broadcastPacket(new MoveToLocationInVehicle(player, boat, closestEntrance.getInnerLocation().getX(), closestEntrance.getInnerLocation().getY(), closestEntrance.getInnerLocation().getZ(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ()));
			});
			
			// We want to schedule the real movement as much as close to the boat and the enter a boat as a post-action.
			player.getAI().setIntention(IntentionType.MOVE_TO, closestEntrance.getOuterLocation());
			player.getAI().setNextAction(nextAction);
		}
	}
}