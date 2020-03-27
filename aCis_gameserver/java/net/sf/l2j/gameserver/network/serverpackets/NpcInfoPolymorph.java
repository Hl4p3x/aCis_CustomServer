package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.data.xml.PlayerData;
import net.sf.l2j.gameserver.model.CharSelectSlot;
import net.sf.l2j.gameserver.model.actor.PcPolymorph;
import net.sf.l2j.gameserver.model.actor.template.PlayerTemplate;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.model.pledge.Clan;

/**
 * @author Williams
 */
public final class NpcInfoPolymorph extends L2GameServerPacket
{
	private final PcPolymorph _activeChar;
	private final CharSelectSlot _morph;
	private final PlayerTemplate _template;
	private final Clan _clan;
	private final int _x, _y, _z, _heading;
	private final int _mAtkSpd, _pAtkSpd;
	private final int _runSpd, _walkSpd;
	private final float _moveMultiplier;
	
	public NpcInfoPolymorph(PcPolymorph cha)
	{
		_activeChar = cha;
		_morph = cha.getPolymorphInfo();
		_template = PlayerData.getInstance().getTemplate(_morph.getBaseClassId());
		_clan = ClanTable.getInstance().getClan(_morph.getClanId());
		
		_x = _activeChar.getX();
		_y = _activeChar.getY();
		_z = _activeChar.getZ();
		_heading = _activeChar.getHeading();
		
		_mAtkSpd = _activeChar.getMAtkSpd();
		_pAtkSpd = _activeChar.getPAtkSpd();
		
		_moveMultiplier = _activeChar.getStat().getMovementSpeedMultiplier();
		_runSpd = (int) (_activeChar.getStat().getBaseRunSpeed() / _moveMultiplier);
		_walkSpd = (int) (_activeChar.getStat().getBaseWalkSpeed() / _moveMultiplier);
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x03);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(_heading);
		writeD(_activeChar.getObjectId());
		writeS(_morph.getName());
		writeD(_morph.getRace());
		writeD(_morph.getSex());
		
		writeD(_morph.getBaseClassId());
		
		writeD(_morph.getPaperdollItemId(Inventory.PAPERDOLL_HAIRALL));
		writeD(_morph.getPaperdollItemId(Inventory.PAPERDOLL_HEAD));
		writeD(_morph.getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
		writeD(_morph.getPaperdollItemId(Inventory.PAPERDOLL_LHAND));
		writeD(_morph.getPaperdollItemId(Inventory.PAPERDOLL_GLOVES));
		writeD(_morph.getPaperdollItemId(Inventory.PAPERDOLL_CHEST));
		writeD(_morph.getPaperdollItemId(Inventory.PAPERDOLL_LEGS));
		writeD(_morph.getPaperdollItemId(Inventory.PAPERDOLL_FEET));
		writeD(_morph.getPaperdollItemId(Inventory.PAPERDOLL_BACK));
		writeD(_morph.getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
		writeD(_morph.getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
		writeD(_morph.getPaperdollItemId(Inventory.PAPERDOLL_FACE));
		
		// c6 new h's
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(_morph.getAugmentationId());
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		
		writeD(0);
		writeD(0);
		
		writeD(_mAtkSpd);
		writeD(_pAtkSpd);
		
		writeD(0);
		writeD(0);
		
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_runSpd); // swim run speed
		writeD(_walkSpd); // swim walk speed
		writeD(_runSpd); // fl run speed
		writeD(_walkSpd); // fl walk speed
		writeD(_runSpd); // fly run speed
		writeD(_walkSpd); // fly walk speed
		writeF(_activeChar.getStat().getMovementSpeedMultiplier());
		writeF(_activeChar.getStat().getAttackSpeedMultiplier());
		
		writeF(_template.getCollisionRadius());
		writeF(_template.getCollisionHeight());
		
		writeD(_morph.getHairStyle());
		writeD(_morph.getHairColor());
		writeD(_morph.getFace());
		
		writeS(_activeChar.getVisibleTitle());
		
		if (_clan != null)
		{
			writeD(_clan.getClanId());
			writeD(_clan.getCrestId());
			writeD(_clan.getAllyId());
			writeD(_clan.getAllyCrestId());
		}
		else
		{
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
		}
		
		writeD(0);
		
		writeC(1); // standing = 1 sitting = 0
		writeC(_activeChar.isRunning() ? 1 : 0); // running = 1 walking = 0
		writeC(_activeChar.isInCombat() ? 1 : 0);
		writeC(_activeChar.isAlikeDead() ? 1 : 0);
		
		writeC(0); // invisible = 1 visible =0
			
		writeC(0); // 1 on strider 2 on wyvern 0 no mount
		writeC(0); // 1 - sellshop
		
		writeH(0);
		
		writeC(0);
		
		writeD(_activeChar.getAbnormalEffect());

		writeC(0);
		writeH(0); // Blue value for name (0 = white, 255 = pure blue)
		writeD(_morph.getClassId());
		
		writeD(_activeChar.getMaxCp());
		writeD((int) _activeChar.getCurrentCp());
		writeC((_morph.getEnchantEffect() > 127) ? 127 : _morph.getEnchantEffect());
		
		writeC(0x00); // team circle around feet 1= Blue, 2 = red
			
		writeD(_clan != null ? _clan.getCrestLargeId() : 0);
		writeC(0); // Symbol on char menu ctrl+I
		writeC(0); // Hero Aura
		
		writeC(0); // 0x01: Fishing Mode (Cant be undone by setting back to 0)
		writeD(0);
		writeD(0);
		writeD(0);
		
		writeD(_activeChar.getNameColor());
		
		writeD(0x00); // isRunning() as in UserInfo?
		
		writeD(0);
		writeD(0);
		
		writeD(_activeChar.getTitleColor());
		
		writeD(0x00);
	}
}