package net.sf.l2j.gameserver.scripting.quests;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.location.SpawnLocation;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;

public class Q652_AnAgedExAdventurer extends Quest
{
	private static final String qn = "Q652_AnAgedExAdventurer";
	
	// NPCs
	private static final int TANTAN = 32012;
	private static final int SARA = 30180;
	
	// Item
	private static final int SOULSHOT_C = 1464;
	
	// Reward
	private static final int ENCHANT_ARMOR_D = 956;
	
	// Table of possible spawns
	private static final SpawnLocation[] SPAWNS =
	{
		new SpawnLocation(78355, -1325, -3659, 0),
		new SpawnLocation(79890, -6132, -2922, 0),
		new SpawnLocation(90012, -7217, -3085, 0),
		new SpawnLocation(94500, -10129, -3290, 0),
		new SpawnLocation(96534, -1237, -3677, 0)
	};
	
	// Current position
	private int _currentPosition = 0;
	
	public Q652_AnAgedExAdventurer()
	{
		super(652, "An Aged Ex-Adventurer");
		
		addStartNpc(TANTAN);
		addTalkId(TANTAN, SARA);
		
		addSpawn(TANTAN, 78355, -1325, -3659, 0, false, 0, false);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("32012-02.htm"))
		{
			if (st.getQuestItemsCount(SOULSHOT_C) >= 100)
			{
				st.setState(STATE_STARTED);
				st.set("cond", "1");
				st.playSound(QuestState.SOUND_ACCEPT);
				st.takeItems(SOULSHOT_C, 100);
				
				npc.getAI().setIntention(IntentionType.MOVE_TO, new Location(85326, 7869, -3620));
				startQuestTimer("apparition_npc", npc, null, 6000);
			}
			else
			{
				htmltext = "32012-02a.htm";
				st.exitQuest(true);
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("apparition_npc"))
		{
			int chance = Rnd.get(5);
			
			// Loop to avoid to spawn to the same place.
			while (chance == _currentPosition)
				chance = Rnd.get(5);
			
			// Register new position.
			_currentPosition = chance;
			
			npc.deleteMe();
			addSpawn(TANTAN, SPAWNS[chance], false, 0, false);
		}
		
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		QuestState st = player.getQuestState(qn);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				htmltext = (player.getLevel() < 46) ? "32012-00.htm" : "32012-01.htm";
				break;
			
			case STATE_STARTED:
				switch (npc.getNpcId())
				{
					case SARA:
						if (Rnd.get(100) < 50)
						{
							htmltext = "30180-01.htm";
							st.rewardItems(57, 5026);
							st.giveItems(ENCHANT_ARMOR_D, 1);
						}
						else
						{
							htmltext = "30180-02.htm";
							st.rewardItems(57, 10000);
						}
						st.playSound(QuestState.SOUND_FINISH);
						st.exitQuest(true);
						break;
					
					case TANTAN:
						htmltext = "32012-04a.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
}