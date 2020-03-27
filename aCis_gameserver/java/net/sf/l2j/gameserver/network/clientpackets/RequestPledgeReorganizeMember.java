package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.model.pledge.ClanMember;
import net.sf.l2j.gameserver.network.serverpackets.PledgeReceiveMemberInfo;

public final class RequestPledgeReorganizeMember extends L2GameClientPacket
{
	private int _isMemberSelected;
	private String _memberName;
	private int _newPledgeType;
	private String _selectedMemberName;
	
	@Override
	protected void readImpl()
	{
		_isMemberSelected = readD();
		_memberName = readS();
		_newPledgeType = readD();
		_selectedMemberName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Clan clan = player.getClan();
		if (clan == null)
			return;
		
		if (!player.hasClanPrivileges(Clan.CP_CL_MANAGE_RANKS))
			return;
		
		final ClanMember member1 = clan.getClanMember(_memberName);
		
		if (_isMemberSelected == 0)
		{
			if (member1 != null)
				player.sendPacket(new PledgeReceiveMemberInfo(member1)); // client changes affiliation info even if it fails, so we have to fix it manually
			return;
		}
		
		final ClanMember member2 = clan.getClanMember(_selectedMemberName);
		
		if (member1 == null || member1.getObjectId() == clan.getLeaderId() || member2 == null || member2.getObjectId() == clan.getLeaderId())
			return;
		
		// Do not send sub pledge leaders to other pledges than main
		if (clan.isSubPledgeLeader(member1.getObjectId()))
		{
			player.sendPacket(new PledgeReceiveMemberInfo(member1));
			return;
		}
		
		final int oldPledgeType = member1.getPledgeType();
		if (oldPledgeType == _newPledgeType)
			return;
		
		member1.setPledgeType(_newPledgeType);
		member2.setPledgeType(oldPledgeType);
		
		clan.broadcastClanStatus();
	}
}