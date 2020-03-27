package net.sf.l2j.gameserver.data.xml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.commons.logging.CLogger;


import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Armor;
import net.sf.l2j.gameserver.model.item.kind.EtcItem;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.skills.DocumentItem;

/**
 * This class loads and stores all {@link Item} templates.
 */
public class ItemData
{
	private static final CLogger LOGGER = new CLogger(ItemData.class.getName());
	
	private Item[] _templates;


	
	protected ItemData()
	{
		load();
	}
	
	public void reload()
	{
		load();
	}
	
	private void load()
	{
		final File dir = new File("./data/xml/items");
		
		final Map<Integer, Armor> armors = new HashMap<>();
		final Map<Integer, EtcItem> etcItems = new HashMap<>();
		final Map<Integer, Weapon> weapons = new HashMap<>();
		
		int highest = 0;
		for (File file : dir.listFiles())
		{
			DocumentItem document = new DocumentItem(file);
			document.parse();
			
			for (Item item : document.getItemList())
			{
				if (highest < item.getItemId())
					highest = item.getItemId();
				
				if (item instanceof EtcItem)
					etcItems.put(item.getItemId(), (EtcItem) item);
				else if (item instanceof Armor)
					armors.put(item.getItemId(), (Armor) item);
				else
					weapons.put(item.getItemId(), (Weapon) item);
			}
		}
		
		// Feed an array with all items templates.
		_templates = new Item[highest + 1];
		
		for (Armor item : armors.values())
			_templates[item.getItemId()] = item;
		
		for (Weapon item : weapons.values())
			_templates[item.getItemId()] = item;
		
		for (EtcItem item : etcItems.values())
			_templates[item.getItemId()] = item;
		
		LOGGER.info("Loaded items.");
	}

	/**
	 * @return the list of all {@link Item} templates.
	 */
	public Item[] getTemplates()
	{
		return _templates;
	}
	
	/**
	 * @param id : the item id to check.
	 * @return the {@link Item} corresponding to the item id.
	 */
	public Item getTemplate(int id)
	{
		return (id >= _templates.length) ? null : _templates[id];
	}
	
	/**
	 * Dummy item is created by setting the ID of the object in the world at null value
	 * @param itemId : int designating the item
	 * @return ItemInstance designating the dummy item created
	 */
	public ItemInstance createDummyItem(int itemId)
	{
		final Item item = getTemplate(itemId);
		if (item == null)
			return null;
		
		return new ItemInstance(0, item);
	}
	
	public static ItemData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemData INSTANCE = new ItemData();
	}


}