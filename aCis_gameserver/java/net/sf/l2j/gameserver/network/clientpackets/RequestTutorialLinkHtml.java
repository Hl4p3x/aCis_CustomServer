package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.ClassMaster;
import net.sf.l2j.gameserver.scripting.QuestState;

public class RequestTutorialLinkHtml extends L2GameClientPacket
{
	private String _bypass;
	
	@Override
	protected void readImpl()
	{
		_bypass = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;

		ClassMaster.onTutorialLink(player, _bypass);
		
		final QuestState qs = player.getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent(_bypass, null, player);
	}
}