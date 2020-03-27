package net.sf.l2j.gameserver.data.xml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.commons.util.StatsSet;

import net.sf.l2j.gameserver.model.holder.IntIntHolder;

import org.w3c.dom.Document;

/**
 * @author Williams
 *
 */
public class PvPData implements IXmlReader
{
	private final List<ColorSystem> _color = new ArrayList<>();
	private final List<RewardSystem> _reward = new ArrayList<>();
	private final List<SpreeKills> _spree = new ArrayList<>();
	
	public PvPData()
	{
		load();
	}
	
	public void reload()
	{
		_color.clear();
		_reward.clear();
		_spree.clear();
		load();
	}
	
	@Override
	public void load()
	{
		parseFile("./data/xml/pvpSystem.xml");
		LOGGER.info("Loaded {} PvP Colors templates.", _color.size());
		LOGGER.info("Loaded {} PvP Rewards templates.", _reward.size());
		LOGGER.info("Loaded {} SpreeKills data.", _spree.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "color", node ->
		{
			final StatsSet set = parseAttributes(node);
			_color.add(new ColorSystem(set));
		}));
		
		forEach(doc, "list", listNode -> forEach(listNode, "reward", node ->
		{
			final StatsSet set = parseAttributes(node);
			_reward.add(new RewardSystem(set));
		}));
		
		forEach(doc, "list", listNode -> forEach(listNode, "SpreeKills", node ->
		{
			final StatsSet set = parseAttributes(node);
			_spree.add(new SpreeKills(set));
		}));
	}

	public List<ColorSystem> getColor()
	{
		return _color;
	}

	public List<RewardSystem> getReward()
	{
		return _reward;
	}
	
	public class RewardSystem
	{
		private final List<IntIntHolder> _reward;
		
		public RewardSystem(StatsSet set)
		{
			_reward = set.getIntIntHolderList("rewards");
		}

		public List<IntIntHolder> getReward()
		{
			return _reward;
		}	
	}
	
	public class ColorSystem
	{
		private int _pvpAmount;
		private int _nameColor;
		private int _titleColor;
		
		public ColorSystem(StatsSet set)
		{
			_pvpAmount = set.getInteger("pvpAmount");
			_nameColor = Integer.decode("0x" + set.getString("nameColor", "FFFFFF"));
			_titleColor = Integer.decode("0x" + set.getString("titleColor", "FFFF77"));
		}
		
		public int getPvpAmount()
		{
			return _pvpAmount;
		}
		
		public int getNameColor()
		{
			return _nameColor;
		}
		
		public int getTitleColor()
		{
			return _titleColor;
		}	
	}

	public List<SpreeKills> getSpreeKills()
	{
		return _spree;
	}

	public class SpreeKills
	{
		private final int _type;
		private final int _kill;
		private final String _msg;
		private final String _sound;
		private final List<IntIntHolder> _reward;
		
		public SpreeKills(StatsSet set)
		{
			_type = set.getInteger("type");
			_kill = set.getInteger("kills");
			_msg = set.getString("msg");
			_sound = set.getString("sound");
			_reward = set.getIntIntHolderList("reward");
		}

		public int getType()
		{
			return _type;
		}
		
		public int getKills()
		{
			return _kill;
		}
		
		public String getMsg()
		{
			return _msg;
		}

		public String getSound()
		{
			return _sound;
		}

		public List<IntIntHolder> getReward()
		{
			return _reward;
		}
	}
	
	public static PvPData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PvPData _instance = new PvPData();
	}
}