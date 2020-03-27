package net.sf.l2j.gameserver.model.actor.instance;

import java.util.List;

import net.sf.l2j.gameserver.data.xml.WalkerRouteData;
import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.model.actor.ai.type.CreatureAI;
import net.sf.l2j.gameserver.model.actor.ai.type.WalkerAI;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.location.WalkerLocation;

/**
 * A Walker is a {@link Folk} which continuously walks, following a defined route. It got no other Intention than MOVE_TO, so it never stops walking/running - except for programmed timers - and can speak.<br>
 * <br>
 * It can't be killed, and the AI is never detached (works even when region is supposed to be down due to lack of players).
 */
public class Walker extends Folk
{
	public Walker(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setAI(new WalkerAI(this));
	}
	
	@Override
	public void setAI(CreatureAI newAI)
	{
		// AI can't be detached, npc must move with the same AI instance.
		if (!(_ai instanceof WalkerAI))
			_ai = newAI;
	}
	
	@Override
	public WalkerAI getAI()
	{
		return (WalkerAI) _ai;
	}
	
	@Override
	public void detachAI()
	{
		// AI can't be detached.
	}
	
	@Override
	public void onSpawn()
	{
		final List<WalkerLocation> route = WalkerRouteData.getInstance().getWalkerRoute(getNpcId());
		if (route != null && !route.isEmpty())
			getAI().setIntention(IntentionType.MOVE_TO, route.get(1));
	}
}