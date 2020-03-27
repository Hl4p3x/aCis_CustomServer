package net.sf.l2j.gameserver.model;

import java.util.List;

public class ExtractableSkill
{
	private final int _hash;
	private final List<ExtractableProductItem> _productItems;
	
	public ExtractableSkill(int hash, List<ExtractableProductItem> productItems)
	{
		_hash = hash;
		_productItems = productItems;
	}
	
	public int getSkillHash()
	{
		return _hash;
	}
	
	public List<ExtractableProductItem> getProductItems()
	{
		return _productItems;
	}
}