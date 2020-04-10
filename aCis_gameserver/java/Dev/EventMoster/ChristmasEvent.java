package Dev.EventMoster;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.sql.SpawnTable;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.SantaClaus;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.spawn.Spawn;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author COMBATE
 *
 */
public class ChristmasEvent implements IXmlReader
{
	private static final List<SantasReward> Rewards = new ArrayList<>();
	private boolean enabled;
	private int minLvl;
	private int maxLvl;
	private int despawnTime;
	private int[] templates;
	private double spawnChance;
	
	public ChristmasEvent()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseFile("./config/aCis/SantaClaus.xml");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		try
		{
			final Node list = doc.getFirstChild();
			for (Node cas = list.getFirstChild(); cas != null; cas = cas.getNextSibling())
			{
				if ("settings".equalsIgnoreCase(cas.getNodeName()))
				{
					final NamedNodeMap attrs = cas.getAttributes();
					enabled = attrs.getNamedItem("enable").getNodeValue().equalsIgnoreCase("true");
					minLvl = Integer.parseInt(attrs.getNamedItem("mobMinLvL").getNodeValue());
					maxLvl = Integer.parseInt(attrs.getNamedItem("mobMaxLvL").getNodeValue());
					despawnTime = Integer.parseInt(attrs.getNamedItem("despawnSeconds").getNodeValue());
					templates = new int[]
						{
							Integer.parseInt(attrs.getNamedItem("NpcTempates").getNodeValue().split(",")[0]),
							Integer.parseInt(attrs.getNamedItem("NpcTempates").getNodeValue().split(",")[1])
						};
					spawnChance = Double.parseDouble(attrs.getNamedItem("spawnChance").getNodeValue());
				}
				else if ("reward".equalsIgnoreCase(cas.getNodeName()))
				{
					
					IntStringHolder exp = new IntStringHolder(-1, "");
					IntStringHolder sp = new IntStringHolder(-1, "");
					IntStringHolder rec = new IntStringHolder(-1, "");
					
					final List<IntIntHolder> rewards = new ArrayList<>();
					
					for (Node cat = cas.getFirstChild(); cat != null; cat = cat.getNextSibling())
					{
						final NamedNodeMap catAttrs = cat.getAttributes();
						
						if ("exp".equalsIgnoreCase(cat.getNodeName()))
							exp = new IntStringHolder(Integer.parseInt(catAttrs.getNamedItem("val").getNodeValue()), catAttrs.getNamedItem("messageOnReward").getNodeValue());
						
						else if ("sp".equalsIgnoreCase(cat.getNodeName()))
							sp = new IntStringHolder(Integer.parseInt(catAttrs.getNamedItem("val").getNodeValue()), catAttrs.getNamedItem("messageOnReward").getNodeValue());
						
						else if ("rec".equalsIgnoreCase(cat.getNodeName()))
							rec = new IntStringHolder(Integer.parseInt(catAttrs.getNamedItem("val").getNodeValue()), catAttrs.getNamedItem("messageOnReward").getNodeValue());
						
						else if ("items".equalsIgnoreCase(cat.getNodeName()))
						{
							for (Node cats = cat.getFirstChild(); cats != null; cats = cats.getNextSibling())
							{
								final NamedNodeMap rewAttrs = cats.getAttributes();
								if (!"item".equalsIgnoreCase(cats.getNodeName()))
									continue;
								
								rewards.add(new IntIntHolder(Integer.parseInt(rewAttrs.getNamedItem("id").getNodeValue()), Integer.parseInt(rewAttrs.getNamedItem("quantity").getNodeValue())));
							}
						}
					}
					Rewards.add(new SantasReward(exp, sp, rec, rewards));
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Error while loading SantaClaus.xml");
		}
	}
	
	public int rewardsSize()
	{
		return Rewards.size();
	}
	
	public SantasReward getRandomReward()
	{
		return Rnd.get(Rewards);
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void luckySpawn(Creature monster, Player selective)
	{
		if (selective == null || monster.getLevel() < minLvl || monster.getLevel() > maxLvl || Rnd.get(100.0) > spawnChance)
			return;
		try
		{
			Spawn spawn = new Spawn(NpcData.getInstance().getTemplate(templates[Rnd.get(templates.length)]));
			spawn.setLoc(monster.getX(), monster.getY(), monster.getZ(), monster.getHeading());
			spawn.setRespawnDelay(0);
			
			SpawnTable.getInstance().addSpawn(spawn, false);
			SantaClaus santa = (SantaClaus) spawn.doSpawn(false);
			spawn.setRespawnState(false);
			santa.setSelective(selective);
			
			final int despawnTime = getDespawnTime();
			((Npc) santa).scheduleDespawn(TimeUnit.SECONDS.toMillis(despawnTime));
			santa.broadcastNpcSay("Ho Ho Ho! What we got here? I'm smelling some rewards for you " + selective.getName() + "! I will stay for " + despawnTime + " seconds! Hurry Up!");
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public int getDespawnTime()
	{
		return despawnTime;
	}
	
	public int getMinLvl()
	{
		return minLvl;
	}
	
	public int getMaxLvl()
	{
		return maxLvl;
	}
	
	public static ChristmasEvent getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ChristmasEvent _instance = new ChristmasEvent();
	}
	
}
