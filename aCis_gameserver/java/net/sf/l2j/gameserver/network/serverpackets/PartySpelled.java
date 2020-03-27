package net.sf.l2j.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.model.actor.instance.Servitor;

public class PartySpelled extends L2GameServerPacket
{
	private final List<Effect> _effects;
	private final Creature _creature;
	
	private class Effect
	{
		protected int _id;
		protected int _level;
		protected int _duration;
		
		public Effect(int id, int level, int duration)
		{
			_id = id;
			_level = level;
			_duration = duration;
		}
	}
	
	public PartySpelled(Creature creature)
	{
		_effects = new ArrayList<>();
		_creature = creature;
	}
	
	@Override
	protected final void writeImpl()
	{
		if (_creature == null)
			return;
		
		writeC(0xee);
		
		writeD(_creature instanceof Servitor ? 2 : _creature instanceof Pet ? 1 : 0);
		writeD(_creature.getObjectId());
		writeD(_effects.size());
		
		for (Effect effect : _effects)
		{
			writeD(effect._id);
			writeH(effect._level);
			writeD(effect._duration / 1000);
		}
	}
	
	public void addPartySpelledEffect(int id, int level, int duration)
	{
		_effects.add(new Effect(id, level, duration));
	}
}