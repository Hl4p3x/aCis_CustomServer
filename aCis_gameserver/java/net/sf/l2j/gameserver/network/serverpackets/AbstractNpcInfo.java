package net.sf.l2j.gameserver.network.serverpackets;

import java.text.DecimalFormat;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.data.xml.PolymorphData.Polymorph;
import net.sf.l2j.gameserver.enums.PolyType;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.pledge.Clan;

public abstract class AbstractNpcInfo extends L2GameServerPacket
{
	protected int _x, _y, _z, _heading;
	protected int _idTemplate;
	protected boolean _isAttackable, _isSummoned;
	protected int _mAtkSpd, _pAtkSpd;
	protected int _runSpd, _walkSpd;
	protected int _rhand, _lhand, _chest, _enchantEffect;
	protected double _collisionHeight, _collisionRadius;
	protected int _clanCrest, _allyCrest, _allyId, _clanId;
	
	protected String _name = "", _title = "";
	
	public AbstractNpcInfo(Creature cha)
	{
		_isSummoned = cha.isShowSummonAnimation();
		_x = cha.getX();
		_y = cha.getY();
		_z = cha.getZ();
		_heading = cha.getHeading();
		_mAtkSpd = cha.getMAtkSpd();
		_pAtkSpd = cha.getPAtkSpd();
		_runSpd = cha.getStat().getBaseRunSpeed();
		_walkSpd = cha.getStat().getBaseWalkSpeed();
	}
	
	/**
	 * Packet for Npcs
	 */
	public static class NpcInfo extends AbstractNpcInfo
	{
		private final Npc _npc;
		
		public NpcInfo(Npc cha, Creature attacker)
		{
			super(cha);
			_npc = cha;
			
			_enchantEffect = _npc.getEnchantEffect();
			_isAttackable = _npc.isAutoAttackable(attacker);
			
			// Support for polymorph.
			if (_npc.getPolyType() == PolyType.NPC)
			{
				_idTemplate = _npc.getPolyTemplate().getIdTemplate();
				_rhand = _npc.getPolyTemplate().getRightHand();
				_lhand = _npc.getPolyTemplate().getLeftHand();
				_collisionHeight = _npc.getPolyTemplate().getCollisionHeight();
				_collisionRadius = _npc.getPolyTemplate().getCollisionRadius();
			}
			else
			{
				_idTemplate = _npc.getTemplate().getIdTemplate();
				_rhand = _npc.getRightHandItemId();
				_lhand = _npc.getLeftHandItemId();
				_collisionHeight = _npc.getCollisionHeight();
				_collisionRadius = _npc.getCollisionRadius();
			}
			
			if (_npc.getTemplate().isUsingServerSideName())
				_name = _npc.getName();
			
			if (_npc.isChampion())
				_title = "Champion";
			else if (_npc.getTemplate().isUsingServerSideTitle())
				_title = _npc.getTitle();
			
			if (Config.SHOW_NPC_LVL && _npc instanceof Monster)
				_title = "Lv " + _npc.getLevel() + (_npc.getTemplate().getAggroRange() > 0 ? "* " : " ") + _title;
			
			// NPC crest system
			if (Config.SHOW_NPC_CREST && _npc.getCastle() != null && _npc.getCastle().getOwnerId() != 0)
			{
				Clan clan = ClanTable.getInstance().getClan(_npc.getCastle().getOwnerId());
				_clanCrest = clan.getCrestId();
				_clanId = clan.getClanId();
				_allyCrest = clan.getAllyCrestId();
				_allyId = clan.getAllyId();
			}
		}
		
		@Override
		protected void writeImpl()
		{
			Polymorph fpc = _npc.getFakePc();
			if (fpc != null)
			{
				writeC(0x03);
				writeD(_x);
				writeD(_y);
				writeD(_z);
				writeD(_heading);
				writeD(_npc.getObjectId());
				writeS(fpc.getName());
				writeD(fpc.getRace());
				writeD(fpc.getSex());
				writeD(fpc.getClassId());
				
				writeD(0x00);
				writeD(0x00);
				writeD(fpc.getRightHand());
				writeD(fpc.getLeftHand());
				writeD(fpc.getGloves());
				writeD(fpc.getChest());
				writeD(fpc.getLegs());
				writeD(fpc.getFeet());
				writeD(fpc.getHero());
				writeD(fpc.getRightHand());
				writeD(fpc.getHair());
				writeD(fpc.getHair2());
				
				write('H', 0, 24);
				
				writeD(0x00);
				writeD(0x00);
				
				writeD(_mAtkSpd);
				writeD(_pAtkSpd);
				
				writeD(0x00);
				writeD(0x00);
				
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeF(_npc.getStat().getMovementSpeedMultiplier());
				writeF(_npc.getStat().getAttackSpeedMultiplier());
				
				writeF(fpc.getRadius());
				writeF(fpc.getHeight());
				
				writeD(fpc.getHairStyle());
				writeD(fpc.getHairColor());
				writeD(fpc.getFace());
				
				if (_npc instanceof Monster)
					writeS(fpc.getTitle() + " - HP " + new DecimalFormat("#.##").format(100.0 * _npc.getCurrentHp() / _npc.getMaxHp()) + "%"); // visible title
				else
					writeS(fpc.getTitle());
				
				writeD(fpc.getClanId());
				writeD(fpc.getClanCrest());
				writeD(fpc.getAllyId());
				writeD(fpc.getAllyCrest());
				
				writeD(0x00);
				writeC(0x01);
				
				writeC(_npc.isRunning() ? 1 : 0);
				writeC(_npc.isInCombat() ? 1 : 0);
				writeC(_npc.isAlikeDead() ? 1 : 0);
				
				write('C', 0, 3);
				
				writeH(0x00); // cubic count
				
				writeC(0x00);
				writeD(0x00);
				writeC(0x00);
				writeH(0x00);
				
				writeD(fpc.getClassId());
				writeD(0x00);
				writeD(0x00);
				writeC(fpc.getEnchant());
				writeC(0x00);
				
				writeD(0x00);
				
				writeC(0x00);
				writeC(fpc.getHero());
				
				writeC(0x00);
				write('D', 0, 3);
				
				writeD(fpc.getNameColor());
				writeD(_heading);
				writeD(0x00);
				writeD(0x00);
				writeD(fpc.getTitleColor());
				writeD(0x00);
			}
			else
			{
				writeC(0x16);
				
				writeD(_npc.getObjectId());
				writeD(_idTemplate + 1000000);
				writeD(_isAttackable ? 1 : 0);
				
				writeD(_x);
				writeD(_y);
				writeD(_z);
				writeD(_heading);
				
				writeD(0x00);
				
				writeD(_mAtkSpd);
				writeD(_pAtkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				writeD(_runSpd);
				writeD(_walkSpd);
				
				writeF(_npc.getStat().getMovementSpeedMultiplier());
				writeF(_npc.getStat().getAttackSpeedMultiplier());
				
				writeF(_collisionRadius);
				writeF(_collisionHeight);
				
				writeD(_rhand);
				writeD(_chest);
				writeD(_lhand);
				
				writeC(1); // name above char
				writeC(_npc.isRunning() ? 1 : 0);
				writeC(_npc.isInCombat() ? 1 : 0);
				writeC(_npc.isAlikeDead() ? 1 : 0);
				writeC(_isSummoned ? 2 : 0);
				
				writeS(_name);
				writeS(_title);
				
				writeD(0x00);
				writeD(0x00);
				writeD(0x00);
				
				writeD(_npc.getAbnormalEffect());
				
				writeD(_clanId);
				writeD(_clanCrest);
				writeD(_allyId);
				writeD(_allyCrest);
				
				writeC(_npc.getMove().getMoveType().getId());
				writeC(0x00);
				
				writeF(_collisionRadius);
				writeF(_collisionHeight);
				
				writeD(_enchantEffect);
				writeD(_npc.isFlying() ? 1 : 0);
			}
		}
		
		private void write(char type, int value, int times)
		{
			for (int i = 0; i < times; i++)
			{
				switch (type)
				{
					case 'C':
						writeC(value);
						break;
					case 'D':
						writeD(value);
						break;
					case 'F':
						writeF(value);
						break;
					case 'H':
						writeH(value);
						break;
				}
			}
		}
	}
	
	/**
	 * Packet for summons
	 */
	public static class SummonInfo extends AbstractNpcInfo
	{
		private final Summon _summon;
		private final Player _owner;
		private int _summonAnimation = 0;
		
		public SummonInfo(Summon cha, Player attacker, int val)
		{
			super(cha);
			_summon = cha;
			_owner = _summon.getOwner();
			
			_summonAnimation = val;
			if (_summon.isShowSummonAnimation())
				_summonAnimation = 2; // override for spawn
			
			_isAttackable = _summon.isAutoAttackable(attacker);
			_rhand = _summon.getWeapon();
			_lhand = 0;
			_chest = _summon.getArmor();
			_enchantEffect = _summon.getTemplate().getEnchantEffect();
			_title = (_owner == null || !_owner.isOnline()) ? "" : _owner.getName();
			_idTemplate = _summon.getTemplate().getIdTemplate();
			
			_collisionHeight = _summon.getCollisionHeight();
			_collisionRadius = _summon.getCollisionRadius();
			
			// NPC crest system
			if (Config.SHOW_SUMMON_CREST && _owner != null && _owner.getClan() != null)
			{
				Clan clan = ClanTable.getInstance().getClan(_owner.getClanId());
				_clanCrest = clan.getCrestId();
				_clanId = clan.getClanId();
				_allyCrest = clan.getAllyCrestId();
				_allyId = clan.getAllyId();
			}
		}
		
		@Override
		protected void writeImpl()
		{
			if (_owner != null && !_owner.getAppearance().isVisible() && _owner != getClient().getPlayer())
				return;
			
			writeC(0x16);
			
			writeD(_summon.getObjectId());
			writeD(_idTemplate + 1000000);
			writeD(_isAttackable ? 1 : 0);
			
			writeD(_x);
			writeD(_y);
			writeD(_z);
			writeD(_heading);
			
			writeD(0x00);
			
			writeD(_mAtkSpd);
			writeD(_pAtkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			
			writeF(_summon.getStat().getMovementSpeedMultiplier());
			writeF(_summon.getStat().getAttackSpeedMultiplier());
			
			writeF(_collisionRadius);
			writeF(_collisionHeight);
			
			writeD(_rhand);
			writeD(_chest);
			writeD(_lhand);
			
			writeC(1); // name above char
			writeC(_summon.isRunning() ? 1 : 0);
			writeC(_summon.isInCombat() ? 1 : 0);
			writeC(_summon.isAlikeDead() ? 1 : 0);
			writeC(_summonAnimation);
			
			writeS(_name);
			writeS(_title);
			
			writeD(_summon instanceof Pet ? 0x00 : 0x01);
			writeD(_summon.getPvpFlag());
			writeD(_summon.getKarma());
			
			writeD(_summon.getAbnormalEffect());
			
			writeD(_clanId);
			writeD(_clanCrest);
			writeD(_allyId);
			writeD(_allyCrest);
			
			writeC(_summon.getMove().getMoveType().getId());
			writeC(_summon.getTeam().getId());
			
			writeF(_collisionRadius);
			writeF(_collisionHeight);
			
			writeD(_enchantEffect);
			writeD(0x00);
		}
	}
	
	/**
	 * Packet for morphed PCs
	 */
	public static class PcMorphInfo extends AbstractNpcInfo
	{
		private final Player _pc;
		private final NpcTemplate _template;
		private final int _swimSpd;
		
		public PcMorphInfo(Player cha, NpcTemplate template)
		{
			super(cha);
			_pc = cha;
			_template = template;
			
			_swimSpd = cha.getStat().getBaseSwimSpeed();
			
			_rhand = _template.getRightHand();
			_lhand = _template.getLeftHand();
			
			_collisionHeight = _template.getCollisionHeight();
			_collisionRadius = _template.getCollisionRadius();
			
			_enchantEffect = _template.getEnchantEffect();
		}
		
		@Override
		protected void writeImpl()
		{
			writeC(0x16);
			
			writeD(_pc.getObjectId());
			writeD(_pc.getPolyId() + 1000000);
			writeD(1);
			
			writeD(_x);
			writeD(_y);
			writeD(_z);
			writeD(_heading);
			
			writeD(0x00);
			
			writeD(_mAtkSpd);
			writeD(_pAtkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_swimSpd);
			writeD(_swimSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			writeD(_runSpd);
			writeD(_walkSpd);
			
			writeF(_pc.getStat().getMovementSpeedMultiplier());
			writeF(_pc.getStat().getAttackSpeedMultiplier());
			
			writeF(_collisionRadius);
			writeF(_collisionHeight);
			
			writeD(_rhand);
			writeD(0);
			writeD(_lhand);
			
			writeC(1); // name above char
			writeC(_pc.isRunning() ? 1 : 0);
			writeC(_pc.isInCombat() ? 1 : 0);
			writeC(_pc.isAlikeDead() ? 1 : 0);
			writeC(0); // 0 = teleported, 1 = default, 2 = summoned
			
			writeS(_name);
			writeS(_title);
			
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			
			writeD(_pc.getAbnormalEffect());
			
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			
			writeC(_pc.getMove().getMoveType().getId());
			writeC(0x00);
			
			writeF(_collisionRadius);
			writeF(_collisionHeight);
			
			writeD(_enchantEffect);
			writeD(0x00);
		}
	}
}