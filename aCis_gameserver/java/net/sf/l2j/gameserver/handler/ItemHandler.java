package net.sf.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.gameserver.handler.itemhandlers.BeastSoulShots;
import net.sf.l2j.gameserver.handler.itemhandlers.BeastSpices;
import net.sf.l2j.gameserver.handler.itemhandlers.BeastSpiritShots;
import net.sf.l2j.gameserver.handler.itemhandlers.BlessedSpiritShots;
import net.sf.l2j.gameserver.handler.itemhandlers.Books;
import net.sf.l2j.gameserver.handler.itemhandlers.Calculators;
import net.sf.l2j.gameserver.handler.itemhandlers.Elixirs;
import net.sf.l2j.gameserver.handler.itemhandlers.EnchantScrolls;
import net.sf.l2j.gameserver.handler.itemhandlers.FishShots;
import net.sf.l2j.gameserver.handler.itemhandlers.Harvesters;
import net.sf.l2j.gameserver.handler.itemhandlers.ItemSkills;
import net.sf.l2j.gameserver.handler.itemhandlers.Keys;
import net.sf.l2j.gameserver.handler.itemhandlers.Maps;
import net.sf.l2j.gameserver.handler.itemhandlers.MercenaryTickets;
import net.sf.l2j.gameserver.handler.itemhandlers.PaganKeys;
import net.sf.l2j.gameserver.handler.itemhandlers.PetFoods;
import net.sf.l2j.gameserver.handler.itemhandlers.Recipes;
import net.sf.l2j.gameserver.handler.itemhandlers.RollingDices;
import net.sf.l2j.gameserver.handler.itemhandlers.ScrollsOfResurrection;
import net.sf.l2j.gameserver.handler.itemhandlers.Seeds;
import net.sf.l2j.gameserver.handler.itemhandlers.SevenSignsRecords;
import net.sf.l2j.gameserver.handler.itemhandlers.SoulCrystals;
import net.sf.l2j.gameserver.handler.itemhandlers.SoulShots;
import net.sf.l2j.gameserver.handler.itemhandlers.SpecialXMas;
import net.sf.l2j.gameserver.handler.itemhandlers.SpiritShots;
import net.sf.l2j.gameserver.handler.itemhandlers.SummonItems;
import net.sf.l2j.gameserver.model.item.kind.EtcItem;

import Dev.ItemSkins.Skin1;
import Dev.ItemSkins.Skin10;
import Dev.ItemSkins.Skin11;
import Dev.ItemSkins.Skin12;
import Dev.ItemSkins.Skin13;
import Dev.ItemSkins.Skin14;
import Dev.ItemSkins.Skin15;
import Dev.ItemSkins.Skin16;
import Dev.ItemSkins.Skin17;
import Dev.ItemSkins.Skin18;
import Dev.ItemSkins.Skin19;
import Dev.ItemSkins.Skin2;
import Dev.ItemSkins.Skin20;
import Dev.ItemSkins.Skin21;
import Dev.ItemSkins.Skin22;
import Dev.ItemSkins.Skin23;
import Dev.ItemSkins.Skin24;
import Dev.ItemSkins.Skin25;
import Dev.ItemSkins.Skin26;
import Dev.ItemSkins.Skin27;
import Dev.ItemSkins.Skin28;
import Dev.ItemSkins.Skin29;
import Dev.ItemSkins.Skin3;
import Dev.ItemSkins.Skin30;
import Dev.ItemSkins.Skin31;
import Dev.ItemSkins.Skin32;
import Dev.ItemSkins.Skin33;
import Dev.ItemSkins.Skin34;
import Dev.ItemSkins.Skin35;
import Dev.ItemSkins.Skin36;
import Dev.ItemSkins.Skin37;
import Dev.ItemSkins.Skin38;
import Dev.ItemSkins.Skin39;
import Dev.ItemSkins.Skin4;
import Dev.ItemSkins.Skin40;
import Dev.ItemSkins.Skin41;
import Dev.ItemSkins.Skin42;
import Dev.ItemSkins.Skin43;
import Dev.ItemSkins.Skin44;
import Dev.ItemSkins.Skin45;
import Dev.ItemSkins.Skin46;
import Dev.ItemSkins.Skin5;
import Dev.ItemSkins.Skin6;
import Dev.ItemSkins.Skin7;
import Dev.ItemSkins.Skin8;
import Dev.ItemSkins.Skin9;


public class ItemHandler
{
	private final Map<Integer, IItemHandler> _entries = new HashMap<>();
	
	protected ItemHandler()
	{
		//Skins Custom Armor
		registerHandler(new Skin1());
		registerHandler(new Skin2());
		registerHandler(new Skin3());
		registerHandler(new Skin4());
		registerHandler(new Skin5());
		registerHandler(new Skin6());
		registerHandler(new Skin7());
		registerHandler(new Skin8());
		registerHandler(new Skin9());
		registerHandler(new Skin10());
		registerHandler(new Skin11());
		registerHandler(new Skin12());
		registerHandler(new Skin13());
		registerHandler(new Skin14());
		registerHandler(new Skin15());
		registerHandler(new Skin16());
		registerHandler(new Skin17());
		registerHandler(new Skin18());
		// Skins Weapons
		registerHandler(new Skin19());
		registerHandler(new Skin20());
		registerHandler(new Skin21());
		registerHandler(new Skin22());
		registerHandler(new Skin23());
		registerHandler(new Skin24());
		registerHandler(new Skin25());
		registerHandler(new Skin26());
		registerHandler(new Skin27());
		registerHandler(new Skin28());
		registerHandler(new Skin29());
		registerHandler(new Skin30());
		registerHandler(new Skin31());
		registerHandler(new Skin32());
		registerHandler(new Skin33());
		registerHandler(new Skin34());
		registerHandler(new Skin35());
		registerHandler(new Skin36());
		registerHandler(new Skin37());
		registerHandler(new Skin38());
		registerHandler(new Skin39());
		registerHandler(new Skin40());
		registerHandler(new Skin41());
		registerHandler(new Skin42());
		registerHandler(new Skin43());
		registerHandler(new Skin44());
		registerHandler(new Skin45());
		registerHandler(new Skin46());
		
		registerHandler(new BeastSoulShots());
		registerHandler(new BeastSpices());
		registerHandler(new BeastSpiritShots());
		registerHandler(new BlessedSpiritShots());
		registerHandler(new Books());
		registerHandler(new Calculators());
		registerHandler(new Elixirs());
		registerHandler(new EnchantScrolls());
		registerHandler(new FishShots());
		registerHandler(new Harvesters());
		registerHandler(new ItemSkills());
		registerHandler(new Keys());
		registerHandler(new Maps());
		registerHandler(new MercenaryTickets());
		registerHandler(new PaganKeys());
		registerHandler(new PetFoods());
		registerHandler(new Recipes());
		registerHandler(new RollingDices());
		registerHandler(new ScrollsOfResurrection());
		registerHandler(new Seeds());
		registerHandler(new SevenSignsRecords());
		registerHandler(new SoulShots());
		registerHandler(new SpecialXMas());
		registerHandler(new SoulCrystals());
		registerHandler(new SpiritShots());
		registerHandler(new SummonItems());
	}
	
	private void registerHandler(IItemHandler handler)
	{
		_entries.put(handler.getClass().getSimpleName().intern().hashCode(), handler);
	}
	
	public IItemHandler getHandler(EtcItem item)
	{
		if (item == null || item.getHandlerName() == null)
			return null;
		
		return _entries.get(item.getHandlerName().hashCode());
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static ItemHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemHandler INSTANCE = new ItemHandler();
	}
}