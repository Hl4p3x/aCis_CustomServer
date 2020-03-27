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
public class DonateData implements IXmlReader
{
	private final List<Donate> _services = new ArrayList<>();
	
	public DonateData()
	{
		load();
	}

	public void reload()
	{
		_services.clear();
		load();
	}
	
	@Override
	public void load()
	{
		parseFile("./data/xml/donate.xml");
		LOGGER.info("Loaded {} Donate Service data.", _services.size());
	}

	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "donate", node ->
		{
			final StatsSet set = parseAttributes(node);
			_services.add(new Donate(set));
		}));
	}	

	public List<Donate> getDonate()
	{
		return _services;
	}
	
	public class Donate
	{
		private int _service;
		private int _duration;
		private final List<IntIntHolder> _price;
		
		public Donate(StatsSet set)
		{
			_service = set.getInteger("service");
			_duration = set.getInteger("duration");
			_price = set.getIntIntHolderList("price");
		}

		public int getService()
		{
			return _service;
		}

		public int getDuration()
		{
			return _duration;
		}
		
		public List<IntIntHolder> getPrice()
		{
			return _price;
		}	
	}
	
	public static DonateData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final DonateData _instance = new DonateData();
	}
}