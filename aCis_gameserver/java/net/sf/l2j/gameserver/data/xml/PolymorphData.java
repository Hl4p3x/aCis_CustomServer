package net.sf.l2j.gameserver.data.xml;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.commons.util.StatsSet;

import org.w3c.dom.Document;

/**
 * @author Williams
 *
 */
public class PolymorphData implements IXmlReader
{
	private final Map<Integer, Polymorph> _fakePcs = new HashMap<>();
	
	public PolymorphData()
	{
		load();
	}

	public void reload()
	{
		_fakePcs.clear();
		load();
	}
	
	@Override
	public void load()
	{
		parseFile("./data/xml/polymorph.xml");
		LOGGER.info("Loaded {} polymorph templates.", _fakePcs.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "npc", node ->
		{
			final StatsSet set = parseAttributes(node);
			forEach(node, "appearance", setNode -> set.putAll(parseAttributes(setNode)));
			forEach(node, "items", setNode -> set.putAll(parseAttributes(setNode)));
			forEach(node, "clan", setNode -> set.putAll(parseAttributes(setNode)));
			_fakePcs.put(set.getInteger("id"), new Polymorph(set));
		}));
	}
	
	public class Polymorph
	{
		private final String _name;
		private final String _title;
		private final int _nameColor;
		private final int _titleColor;
		private final double _radius;
		private final double _height;
		private final int _race;
		private final int _sex;
		private final int _classId;
		private final int _hairStyle;
		private final int _hairColor;
		private final int _face;
		private final byte _hero;
		private final int _enchant;
		
		private final int _rightHand;
		private final int _leftHand;
		private final int _chest;
		private final int _legs;
		private final int _gloves;
		private final int _feet;
		private final int _hair;
		private final int _hair2;
		
		private final int _clanId;
		private final int _clanCrest;
		private final int _allyId;
		private final int _allyCrest;
		private final int _pledge;
		
		protected Polymorph(StatsSet set)
		{
			//aparnce fake
			_name = set.getString("name", null);
			_title = set.getString("title", null);
			_nameColor = Integer.decode("0x" + set.getString("nameColor", "FFFFFF"));
			_titleColor = Integer.decode("0x" + set.getString("titleColor", "FFFF77"));
			_radius = set.getDouble("radius", 0);
			_height = set.getDouble("height", 0);
			_race = set.getInteger("race", 0);
			_sex = set.getInteger("sex", 0);
			_classId = set.getInteger("classId", 0);
			_hairStyle = set.getInteger("hairStyle", 0);
			_hairColor = set.getInteger("hairColor", 0);
			_face = set.getInteger("face", 0);
			_hero = set.getByte("hero", (byte) -1);
			_enchant = set.getInteger("enchant", 0);
			
			// items for fake
			_rightHand = set.getInteger("rightHand", 0);
			_leftHand = set.getInteger("leftHand", 0);
			_chest = set.getInteger("chest", 0);
			_legs = set.getInteger("legs", 0);
			_gloves = set.getInteger("gloves", 0);
			_feet = set.getInteger("feet", 0);
			_hair = set.getInteger("hair", 0);
			_hair2 = set.getInteger("hair2", 0);
			
			// clan for fake
			_clanId = set.getInteger("clanId", 0);
			_clanCrest = set.getInteger("clanCrest", 0);
			_allyId = set.getInteger("allyId", 0);
			_allyCrest = set.getInteger("allyCrest", 0);
			_pledge = set.getInteger("pledge", 0);
		}
		
		public final String getName()
		{
			return _name;
		}

		public final String getTitle()
		{
			return _title;
		}

		public final int getNameColor()
		{
			return _nameColor;
		}
		
		public final int getTitleColor()
		{
			return _titleColor;
		}
		
		public final double getRadius()
		{
			return _radius;
		}

		public final double getHeight()
		{
			return _height;
		}
		
		public final int getRace()
		{
			return _race;
		}

		public final int getSex()
		{
			return _sex;
		}

		public final int getClassId()
		{
			return _classId;
		}

		public final int getHairStyle()
		{
			return _hairStyle;
		}

		public final int getHairColor()
		{
			return _hairColor;
		}

		public final int getFace()
		{
			return _face;
		}

		public final byte getHero()
		{
			return _hero;
		}

		public final int getEnchant()
		{
			return _enchant;
		}

		public final int getRightHand()
		{
			return _rightHand;
		}

		public final int getLeftHand()
		{
			return _leftHand;
		}

		public final int getChest()
		{
			return _chest;
		}

		public final int getLegs()
		{
			return _legs;
		}

		public final int getGloves()
		{
			return _gloves;
		}

		public final int getFeet()
		{
			return _feet;
		}

		public final int getHair()
		{
			return _hair;
		}

		public final int getHair2()
		{
			return _hair2;
		}

		public final int getClanId()
		{
			return _clanId;
		}

		public final int getClanCrest()
		{
			return _clanCrest;
		}

		public final int getAllyId()
		{
			return _allyId;
		}

		public final int getAllyCrest()
		{
			return _allyCrest;
		}

		public final int getPledge()
		{
			return _pledge;
		}
	}

	public Polymorph getFakePc(int npcId)
	{
		return _fakePcs.get(npcId);
	}

	public static PolymorphData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PolymorphData _instance = new PolymorphData();
	}
}