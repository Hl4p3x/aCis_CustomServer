package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.manager.CursedWeaponManager;
import net.sf.l2j.gameserver.enums.TeamType;
import net.sf.l2j.gameserver.enums.skills.AbnormalEffect;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;

public class CharInfo extends L2GameServerPacket
{
	private final Player _player;
	private final Inventory _inv;
	
	public CharInfo(Player player)
	{
		_player = player;
		_inv = _player.getInventory();
	}
	
	@Override
	protected final void writeImpl()
	{
		boolean canSeeInvis = false;
		
		if (!_player.getAppearance().isVisible())
		{
			final Player tmp = getClient().getPlayer();
			if (tmp != null && tmp.isGM())
				canSeeInvis = true;
		}
		
		writeC(0x03);
		writeD(_player.getX());
		writeD(_player.getY());
		writeD(_player.getZ());
		writeD((_player.getBoat() == null) ? 0 : _player.getBoat().getObjectId());
		writeD(_player.getObjectId());
		writeS(_player.isAio() ? "[AIO]-" + _player.getName() : _player.getName());
		writeD(_player.getRace().ordinal());
		writeD(_player.getAppearance().getSex().ordinal());
		writeD((_player.getClassIndex() == 0) ? _player.getClassId().getId() : _player.getBaseClass());
		
		if (_player.isDressMeEnabled())
		{
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIRALL));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HEAD));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_LHAND));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_GLOVES));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_CHEST));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_LEGS));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_FEET));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_BACK));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_FACE));
		}
		else
		{
			writeD(_player.getDressMeData() == null ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HAIRALL) : (_player.getDressMeData().getHelmetId() == 0 ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HAIRALL) : _player.getDressMeData().getHelmetId()));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HEAD));
			writeD(_player.getDressMeData() == null ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) : (_player.getDressMeData().getWeapId() == 0 ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) : _player.getDressMeData().getWeapId()));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_LHAND));
			writeD(_player.getDressMeData() == null ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_GLOVES) : (_player.getDressMeData().getGlovesId() == 0 ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_GLOVES) : _player.getDressMeData().getGlovesId()));
			writeD(_player.getDressMeData() == null ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_CHEST) : (_player.getDressMeData().getChestId() == 0 ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_CHEST) : _player.getDressMeData().getChestId()));
			writeD(_player.getDressMeData() == null ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEGS) : (_player.getDressMeData().getLegsId() == 0 ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEGS) : _player.getDressMeData().getLegsId()));
			writeD(_player.getDressMeData() == null ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_FEET) : (_player.getDressMeData().getBootsId() == 0 ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_FEET) : _player.getDressMeData().getBootsId()));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_BACK));
			writeD(_player.getDressMeData() == null ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) : (_player.getDressMeData().getWeapId() == 0 ? _player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) : _player.getDressMeData().getWeapId()));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_FACE));
		}
		
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(_inv.getPaperdollAugmentationId(Inventory.PAPERDOLL_RHAND));
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
		writeD(_inv.getPaperdollAugmentationId(Inventory.PAPERDOLL_LHAND));
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		
		writeD(_player.getPvpFlag());
		writeD(_player.getKarma());
		writeD(_player.getMAtkSpd());
		writeD(_player.getPAtkSpd());
		writeD(_player.getPvpFlag());
		writeD(_player.getKarma());
		
		final int runSpd = _player.getStat().getBaseRunSpeed();
		final int walkSpd = _player.getStat().getBaseWalkSpeed();
		final int swimSpd = _player.getStat().getBaseSwimSpeed();
		
		writeD(runSpd);
		writeD(walkSpd);
		writeD(swimSpd);
		writeD(swimSpd);
		writeD(runSpd);
		writeD(walkSpd);
		writeD((_player.isFlying()) ? runSpd : 0);
		writeD((_player.isFlying()) ? walkSpd : 0);
		
		writeF(_player.getStat().getMovementSpeedMultiplier());
		writeF(_player.getStat().getAttackSpeedMultiplier());
		
		final Summon summon = _player.getSummon();
		if (_player.getMountType() != 0 && summon != null)
		{
			writeF(summon.getCollisionRadius());
			writeF(summon.getCollisionHeight());
		}
		else
		{
			writeF(_player.getCollisionRadius());
			writeF(_player.getCollisionHeight());
		}
		
		writeD(_player.getAppearance().getHairStyle());
		writeD(_player.getAppearance().getHairColor());
		writeD(_player.getAppearance().getFace());
		
		writeS((canSeeInvis) ? "Invisible" : _player.isAio() ? Config.AIO_TITLE : _player.getTitle());
		
		writeD(_player.getClanId());
		writeD(_player.getClanCrestId());
		writeD(_player.getAllyId());
		writeD(_player.getAllyCrestId());
		
		writeD(0);
		
		writeC((_player.isSitting()) ? 0 : 1);
		writeC((_player.isRunning()) ? 1 : 0);
		writeC((_player.isInCombat()) ? 1 : 0);
		writeC((_player.isAlikeDead()) ? 1 : 0);
		writeC((!canSeeInvis && !_player.getAppearance().isVisible()) ? 1 : 0);
		
		writeC(_player.getMountType());
		writeC(_player.getOperateType().getId());
		
		writeH(_player.getCubics().size());
		for (int id : _player.getCubics().keySet())
			writeH(id);
		
		writeC((_player.isInPartyMatchRoom()) ? 1 : 0);
		writeD((canSeeInvis) ? (_player.getAbnormalEffect() | AbnormalEffect.STEALTH.getMask()) : _player.getAbnormalEffect());
		writeC(_player.getRecomLeft());
		writeH(_player.getRecomHave());
		writeD(_player.getClassId().getId());
		writeD(_player.getMaxCp());
		writeD((int) _player.getCurrentCp());
		writeC((_player.isMounted()) ? 0 : _player.getEnchantEffect());
		writeC((Config.PLAYER_SPAWN_PROTECTION > 0 && _player.isSpawnProtected()) ? TeamType.BLUE.getId() : _player.getTeam().getId());
		writeD(_player.getClanCrestLargeId());
		writeC((_player.isNoble()) ? 1 : 0);
		writeC((_player.isHero() || (_player.isGM() && Config.GM_HERO_AURA)) ? 1 : 0);
		writeC((_player.isFishing()) ? 1 : 0);
		writeLoc(_player.getFishingStance().getLoc());
		writeD(_player.isVip() ? Config.VIP_COLOR : _player.isAio() ? Config.AIO_COLOR : _player.getAppearance().getNameColor());
		writeD(_player.getHeading());
		writeD(_player.getPledgeClass());
		writeD(_player.getPledgeType());
		writeD(_player.isVip() ? Config.VIP_COLOR : _player.isAio() ? Config.AIO_COLOR : _player.getAppearance().getTitleColor());
		writeD(CursedWeaponManager.getInstance().getCurrentStage(_player.getCursedWeaponEquippedId()));
	}
}