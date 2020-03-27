package net.sf.l2j.gameserver.data.xml;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.commons.util.ArraysUtil;
import net.sf.l2j.commons.util.StatsSet;

import net.sf.l2j.gameserver.enums.items.CrystalType;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;

import org.w3c.dom.Document;

/**
 * @author Williams
 *
 */
public class EnchantData implements IXmlReader
{
	private final Map<Integer, EnchantScroll> _data = new HashMap<>();
	
	public EnchantData()
	{
		load();
	}
	
	public void reload()
	{
		_data.clear();
		load();
	}
	
	@Override
	public void load()
	{
		parseFile("./data/xml/enchants.xml");
		LOGGER.info("Loaded {} enchant scroll data.", _data.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "scroll", scrollNode ->
		{
			final StatsSet set = parseAttributes(scrollNode);
			forEach(scrollNode, "settings", setNode -> set.putAll(parseAttributes(setNode)));
			forEach(scrollNode, "chances", setNode -> set.putAll(parseAttributes(setNode)));
			forEach(scrollNode, "announce", setNode -> set.putAll(parseAttributes(setNode)));
			_data.put(set.getInteger("id"), new EnchantScroll(set));
		}));
	}
	
	public EnchantScroll getEnchantScroll(ItemInstance item)
	{
		return _data.get(item.getItemId());
	}
	
	public static EnchantData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final EnchantData INSTANCE = new EnchantData();
	}
	
	public class EnchantScroll
	{
		private final int _scrollId;
		private final CrystalType _grade;
		private final boolean _isWeapon;
		private final boolean _cristalize;
		private final int _return;
		private final int[] _chance;
		private final int[] _chanceF;
		private final int[] _chanceM;
		
		private final String _message;
		private final int[] _enchants;
		
		protected EnchantScroll(StatsSet set)
		{
			_scrollId = set.getInteger("id");
			_grade = set.getEnum("grade", CrystalType.class, CrystalType.NONE);
			_isWeapon = set.getBool("isWeapon");
			_cristalize = set.getBool("crystalize", true);
			_return = set.getInteger("return", 0);
			_chance = set.getIntegerArray("rate", ArraysUtil.EMPTY_INT_ARRAY);
			_chanceF = set.getIntegerArray("rateF", ArraysUtil.EMPTY_INT_ARRAY);
			_chanceM = set.getIntegerArray("rateM", ArraysUtil.EMPTY_INT_ARRAY);
			_message = set.getString("message", null);
			_enchants = set.getIntegerArray("enchants", ArraysUtil.EMPTY_INT_ARRAY);
		}
		
		public final int getScrollId()
		{
			return _scrollId;
		}
		
		public final CrystalType getGrade()
		{
			return _grade;
		}
		
		public final boolean isWeapon()
		{
			return _isWeapon;
		}
		
		public final boolean canCrystalized()
		{
			return _cristalize;
		}
		
		public final int getReturnValue()
		{
			return _return;
		}
		
		public final int getChance(ItemInstance item)
		{
			int level = item.getEnchantLevel();
			
			if (item.getItem().getBodyPart() == Item.SLOT_FULL_ARMOR && level <= 4)
				return 100;
			
			if (_chance == ArraysUtil.EMPTY_INT_ARRAY && item.getItem().getType2() == Item.TYPE2_WEAPON && isWeapon() && item.isWeapon())
				return ((Weapon) item.getItem()).isMagical() ? level >= _chanceM.length ? 0 : _chanceM[level] : level >= _chanceF.length ? 0 : _chanceF[level];
				
				return level >= _chance.length ? 0 : _chance[level];
		}
		
		public final String getAnnounceMessage()
		{
			return _message;
		}
		
		public final int[] getAnnounceEnchants()
		{
			return _enchants;
		}
		
		public final boolean announceTheEnchant(ItemInstance item)
		{
			return item != null && _message != null && ArraysUtil.contains(_enchants, item.getEnchantLevel());
		}
		
		public final boolean isValid(ItemInstance item)
		{
			if (_grade != item.getItem().getCrystalType())
				return false;
			
			// check enchant max level
			if (getChance(item) == 0)
				return false;
			
			// checking scroll type
			switch (item.getItem().getType2())
			{
				case Item.TYPE2_WEAPON:
					if (!isWeapon())
						return false;
					break;
					
				case Item.TYPE2_SHIELD_ARMOR:
				case Item.TYPE2_ACCESSORY:
					if (isWeapon())
						return false;
					break;
					
				default:
					return false;
			}
			
			return true;
		}
	}
}