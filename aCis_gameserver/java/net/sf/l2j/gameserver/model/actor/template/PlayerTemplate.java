package net.sf.l2j.gameserver.model.actor.template;

import java.util.List;

import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.commons.util.ArraysUtil;
import net.sf.l2j.commons.util.StatsSet;

import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.enums.actors.ClassRace;
import net.sf.l2j.gameserver.enums.actors.Sex;
import net.sf.l2j.gameserver.model.holder.ItemTemplateHolder;
import net.sf.l2j.gameserver.model.holder.skillnode.GeneralSkillNode;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.model.location.Location;

/**
 * A datatype extending {@link CreatureTemplate}, used to retain Player template informations such as classId, specific collision values for female, hp/mp/cp tables, etc.<br>
 * <br>
 * Since each PlayerTemplate is associated to a {@link ClassId}, it is also used as a container for {@link GeneralSkillNode}s this class can use.<br>
 * <br>
 * Finally, it holds starter equipment (under an int array of itemId) and initial spawn {@link Location} for newbie templates.
 */
public class PlayerTemplate extends CreatureTemplate
{
	private final ClassId _classId;
	
	private final int[] _safeFallHeight;
	
	private final int _baseSwimSpd;
	
	private final double _collisionRadiusFemale;
	private final double _collisionHeightFemale;
	
	private final List<Location> _spawnLocations;
	
	private final int _classBaseLevel;
	
	private final double[] _hpTable;
	private final double[] _mpTable;
	private final double[] _cpTable;
	
	private final List<ItemTemplateHolder> _items;
	private final List<GeneralSkillNode> _skills;
	
	private final Weapon _fists;
	
	private final boolean _isbuffs;
	private final boolean _isShow;
	
	private final String _title;
	
	private final int[] _buffs;
	
	private final int _level;
	private final int _sp;
	
	private final int _vip;
	
	public PlayerTemplate(StatsSet set)
	{
		super(set);
		
		_classId = ClassId.VALUES[set.getInteger("id")];
		
		_safeFallHeight = set.getIntegerArray("safeFallHeight");
		
		_baseSwimSpd = set.getInteger("swimSpd", 1);
		
		_collisionRadiusFemale = set.getDouble("radiusFemale");
		_collisionHeightFemale = set.getDouble("heightFemale");
		
		_spawnLocations = set.getList("spawnLocations");
		
		_classBaseLevel = set.getInteger("baseLvl");
		
		_hpTable = set.getDoubleArray("hpTable");
		_mpTable = set.getDoubleArray("mpTable");
		_cpTable = set.getDoubleArray("cpTable");
		
		_items = set.getList("items");
		_skills = set.getList("skills");
		
		_isbuffs = set.getBool("isbuffs", false);
		_isShow = set.getBool("isShow", false);
		
		_buffs = set.getIntegerArray("buffs", ArraysUtil.EMPTY_INT_ARRAY);
		
		_level = set.getInteger("level", 1);
		_sp = set.getInteger("sp", 1);
		_title = set.getString("title", null);
		
		_vip = set.getInteger("daysVip", 1);
		
		_fists = (Weapon) ItemData.getInstance().getTemplate(set.getInteger("fists"));
	}
	
	public final ClassId getClassId()
	{
		return _classId;
	}
	
	public final ClassRace getRace()
	{
		return _classId.getRace();
	}
	
	public final String getClassName()
	{
		return _classId.toString();
	}
	
	public final int getSafeFallHeight(Sex sex)
	{
		return (sex == Sex.MALE) ? _safeFallHeight[1] : _safeFallHeight[0];
	}
	
	public final int getBaseSwimSpeed()
	{
		return _baseSwimSpd;
	}
	
	/**
	 * @param sex
	 * @return : height depends on sex.
	 */
	public double getCollisionRadiusBySex(Sex sex)
	{
		return (sex == Sex.MALE) ? _collisionRadius : _collisionRadiusFemale;
	}
	
	/**
	 * @param sex
	 * @return : height depends on sex.
	 */
	public double getCollisionHeightBySex(Sex sex)
	{
		return (sex == Sex.MALE) ? _collisionHeight : _collisionHeightFemale;
	}
	
	public final Location getRandomSpawn()
	{
		final Location loc = Rnd.get(_spawnLocations);
		return (loc == null) ? Location.DUMMY_LOC : loc;
	}
	
	public final int getClassBaseLevel()
	{
		return _classBaseLevel;
	}
	
	@Override
	public final double getBaseHpMax(int level)
	{
		return _hpTable[level - 1];
	}
	
	@Override
	public final double getBaseMpMax(int level)
	{
		return _mpTable[level - 1];
	}
	
	public final double getBaseCpMax(int level)
	{
		return _cpTable[level - 1];
	}
	
	public final String getTitle()
	{
		return _title;
	}
	
	public final int getLevel()
	{
		return _level;
	}
	
	public final int getSp()
	{
		return _sp;
	}
	
	public final int getVip()
	{
		return _vip;
	}
	
	public final int[] getBuffIds()
	{
		return _buffs;
	}
	
	public final boolean isBuffIds()
	{
		return _isbuffs;
	}
	
	public final boolean isShow()
	{
		return _isShow;
	}
	
	/**
	 * @return the {@link List} of {@link ItemTemplateHolder}s holding the starter equipment informations for this {@link PlayerTemplate}.
	 */
	public final List<ItemTemplateHolder> getItems()
	{
		return _items;
	}
	
	/**
	 * @return the {@link List} of all available {@link GeneralSkillNode} for this {@link PlayerTemplate}.
	 */
	public final List<GeneralSkillNode> getSkills()
	{
		return _skills;
	}
	
	/**
	 * Find if the skill exists on skill tree.
	 * @param id : The skill id to check.
	 * @param level : The skill level to check.
	 * @return the associated {@link GeneralSkillNode} if a matching id/level is found on this {@link PlayerTemplate}, or null.
	 */
	public GeneralSkillNode findSkill(int id, int level)
	{
		return _skills.stream().filter(s -> s.getId() == id && s.getValue() == level).findFirst().orElse(null);
	}
	
	/**
	 * @return the {@link Weapon} used as fists for this {@link PlayerTemplate}.
	 */
	public final Weapon getFists()
	{
		return _fists;
	}
}