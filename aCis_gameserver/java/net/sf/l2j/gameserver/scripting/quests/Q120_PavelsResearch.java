package net.sf.l2j.gameserver.scripting.quests;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;

public class Q120_PavelsResearch extends Quest
{
	private static final String qn = "Q120_PavelsResearch";
	
	// NPCs
	private static final int YUMI = 32041;
	private static final int WEATHERMASTER_1 = 32042; // north
	private static final int WEATHERMASTER_2 = 32043; // east
	private static final int WEATHERMASTER_3 = 32044; // west
	private static final int DOCTOR_CHAOS_SECRET_BOOKSHELF = 32045;
	private static final int SUSPICIOUS_PILE_OF_STONES = 32046;
	private static final int WENDY = 32047;
	
	// Items
	private static final int LOCKUP_RESEARCH_REPORT = 8058;
	private static final int RESEARCH_REPORT = 8059;
	private static final int KEY_OF_ENIGMA = 8060;
	private static final int FLOWER_OF_PAVEL = 8290;
	private static final int HEART_OF_ATLANTA = 8291;
	private static final int WENDY_NECKLACE = 8292;
	
	// Reward
	private static final int EARRING_OF_BINDING = 854;
	
	public Q120_PavelsResearch()
	{
		super(120, "Pavel's Research");
		
		setItemsIds(LOCKUP_RESEARCH_REPORT, RESEARCH_REPORT, KEY_OF_ENIGMA, FLOWER_OF_PAVEL, HEART_OF_ATLANTA, WENDY_NECKLACE);
		
		addStartNpc(SUSPICIOUS_PILE_OF_STONES);
		addTalkId(YUMI, WEATHERMASTER_1, WEATHERMASTER_2, WEATHERMASTER_3, DOCTOR_CHAOS_SECRET_BOOKSHELF, SUSPICIOUS_PILE_OF_STONES, WENDY);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("32041-03.htm"))
		{
			st.set("cond", "3");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32041-04.htm"))
		{
			st.set("cond", "4");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32041-12.htm"))
		{
			st.set("cond", "8");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32041-16.htm"))
		{
			st.set("cond", "16");
			st.giveItems(KEY_OF_ENIGMA, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32041-22.htm"))
		{
			st.set("cond", "17");
			st.takeItems(KEY_OF_ENIGMA, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32041-32.htm"))
		{
			st.takeItems(WENDY_NECKLACE, 1);
			st.giveItems(EARRING_OF_BINDING, 1);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(false);
		}
		else if (event.equalsIgnoreCase("32042-06.htm"))
		{
			if (st.getInt("cond") == 10)
			{
				if (st.getInt("talk") + st.getInt("talk1") == 2)
				{
					st.set("cond", "11");
					st.set("talk", "0");
					st.set("talk1", "0");
					st.playSound(QuestState.SOUND_MIDDLE);
				}
				else
					htmltext = "32042-03.htm";
			}
		}
		else if (event.equalsIgnoreCase("32042-08.htm"))
			st.playSound("AmbSound.dt_percussion_01");
		else if (event.equalsIgnoreCase("32042-10.htm"))
		{
			if (st.getInt("talk") + st.getInt("talk1") + st.getInt("talk2") == 3)
				htmltext = "32042-14.htm";
		}
		else if (event.equalsIgnoreCase("32042-11.htm"))
		{
			if (st.getInt("talk") == 0)
				st.set("talk", "1");
		}
		else if (event.equalsIgnoreCase("32042-12.htm"))
		{
			if (st.getInt("talk1") == 0)
				st.set("talk1", "1");
		}
		else if (event.equalsIgnoreCase("32042-13.htm"))
		{
			if (st.getInt("talk2") == 0)
				st.set("talk2", "1");
		}
		else if (event.equalsIgnoreCase("32042-15.htm"))
		{
			st.set("cond", "12");
			st.set("talk", "0");
			st.set("talk1", "0");
			st.set("talk2", "0");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32043-06.htm"))
		{
			if (st.getInt("cond") == 17)
			{
				if (st.getInt("talk") + st.getInt("talk1") == 2)
				{
					st.set("cond", "18");
					st.set("talk", "0");
					st.set("talk1", "0");
					st.playSound(QuestState.SOUND_MIDDLE);
				}
				else
					htmltext = "32043-03.htm";
			}
		}
		else if (event.equalsIgnoreCase("32043-15.htm"))
		{
			if (st.getInt("talk") + st.getInt("talk1") == 2)
				htmltext = "32043-29.htm";
		}
		else if (event.equalsIgnoreCase("32043-18.htm"))
		{
			if (st.getInt("talk") == 1)
				htmltext = "32043-21.htm";
		}
		else if (event.equalsIgnoreCase("32043-20.htm"))
		{
			st.set("talk", "1");
			st.playSound("AmbSound.ed_drone_02");
		}
		else if (event.equalsIgnoreCase("32043-28.htm"))
		{
			st.set("talk1", "1");
		}
		else if (event.equalsIgnoreCase("32043-30.htm"))
		{
			st.set("cond", "19");
			st.set("talk", "0");
			st.set("talk1", "0");
		}
		else if (event.equalsIgnoreCase("32044-06.htm"))
		{
			if (st.getInt("cond") == 20)
			{
				if (st.getInt("talk") + st.getInt("talk1") == 2)
				{
					st.set("cond", "21");
					st.set("talk", "0");
					st.set("talk1", "0");
					st.playSound(QuestState.SOUND_MIDDLE);
					st.playSound("AmbSound.ac_percussion_02");
				}
				else
					htmltext = "32044-03.htm";
			}
		}
		else if (event.equalsIgnoreCase("32044-08.htm"))
		{
			if (st.getInt("talk") + st.getInt("talk1") == 2)
				htmltext = "32044-11.htm";
		}
		else if (event.equalsIgnoreCase("32044-09.htm"))
		{
			if (st.getInt("talk") == 0)
				st.set("talk", "1");
		}
		else if (event.equalsIgnoreCase("32044-10.htm"))
		{
			if (st.getInt("talk1") == 0)
				st.set("talk1", "1");
		}
		else if (event.equalsIgnoreCase("32044-17.htm"))
		{
			st.set("cond", "22");
			st.set("talk", "0");
			st.set("talk1", "0");
			st.playSound(QuestState.SOUND_MIDDLE);
			
			st.playSound("AmbSound.ed_drone_02");
			npc.setTarget(player);
			npc.doCast(SkillTable.getInstance().getInfo(5073, 5));
		}
		else if (event.equalsIgnoreCase("32045-02.htm"))
		{
			st.set("cond", "15");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.giveItems(LOCKUP_RESEARCH_REPORT, 1);
			
			npc.setTarget(player);
			npc.doCast(SkillTable.getInstance().getInfo(5073, 5));
		}
		else if (event.equalsIgnoreCase("32046-04.htm") || event.equalsIgnoreCase("32046-05.htm"))
		{
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("32046-06.htm"))
		{
			if (player.getLevel() >= 50)
			{
				st.setState(STATE_STARTED);
				st.set("cond", "1");
				st.playSound(QuestState.SOUND_ACCEPT);
			}
			else
			{
				htmltext = "32046-00.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("32046-08.htm"))
		{
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32046-12.htm"))
		{
			st.set("cond", "6");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.giveItems(FLOWER_OF_PAVEL, 1);
		}
		else if (event.equalsIgnoreCase("32046-22.htm"))
		{
			st.set("cond", "10");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32046-29.htm"))
		{
			st.set("cond", "13");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32046-35.htm"))
		{
			st.set("cond", "20");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32046-38.htm"))
		{
			st.set("cond", "23");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.giveItems(HEART_OF_ATLANTA, 1);
		}
		else if (event.equalsIgnoreCase("32047-06.htm"))
		{
			st.set("cond", "5");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32047-10.htm"))
		{
			st.set("cond", "7");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(FLOWER_OF_PAVEL, 1);
		}
		else if (event.equalsIgnoreCase("32047-15.htm"))
		{
			st.set("cond", "9");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32047-18.htm"))
		{
			st.set("cond", "14");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32047-26.htm"))
		{
			st.set("cond", "24");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.takeItems(HEART_OF_ATLANTA, 1);
		}
		else if (event.equalsIgnoreCase("32047-32.htm"))
		{
			st.set("cond", "25");
			st.playSound(QuestState.SOUND_MIDDLE);
			st.giveItems(WENDY_NECKLACE, 1);
		}
		else if (event.equalsIgnoreCase("w1_1"))
		{
			st.set("talk", "1");
			htmltext = "32042-04.htm";
		}
		else if (event.equalsIgnoreCase("w1_2"))
		{
			st.set("talk1", "1");
			htmltext = "32042-05.htm";
		}
		else if (event.equalsIgnoreCase("w2_1"))
		{
			st.set("talk", "1");
			htmltext = "32043-04.htm";
		}
		else if (event.equalsIgnoreCase("w2_2"))
		{
			st.set("talk1", "1");
			htmltext = "32043-05.htm";
		}
		else if (event.equalsIgnoreCase("w3_1"))
		{
			st.set("talk", "1");
			htmltext = "32044-04.htm";
		}
		else if (event.equalsIgnoreCase("w3_2"))
		{
			st.set("talk1", "1");
			htmltext = "32044-05.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case STATE_CREATED:
				QuestState st2 = player.getQuestState("Q114_ResurrectionOfAnOldManager");
				if (st2 != null && st2.isCompleted())
					htmltext = (player.getLevel() < 50) ? "32046-00.htm" : "32046-01.htm";
				else
					htmltext = "32046-00.htm";
				break;
			
			case STATE_STARTED:
				int cond = st.getInt("cond");
				switch (npc.getNpcId())
				{
					case SUSPICIOUS_PILE_OF_STONES:
						if (cond == 1)
							htmltext = "32046-06.htm";
						else if (cond > 1 && cond < 5)
							htmltext = "32046-09.htm";
						else if (cond == 5)
							htmltext = "32046-10.htm";
						else if (cond > 5 && cond < 9)
							htmltext = "32046-13.htm";
						else if (cond == 9)
							htmltext = "32046-14.htm";
						else if (cond == 10 || cond == 11)
							htmltext = "32046-23.htm";
						else if (cond == 12)
							htmltext = "32046-26.htm";
						else if (cond > 12 && cond < 19)
							htmltext = "32046-30.htm";
						else if (cond == 19)
							htmltext = "32046-31.htm";
						else if (cond == 20 || cond == 21)
							htmltext = "32046-36.htm";
						else if (cond == 22)
							htmltext = "32046-37.htm";
						else if (cond > 22)
							htmltext = "32046-39.htm";
						break;
					
					case WENDY:
						if (cond == 2 || cond == 3 || cond == 4)
							htmltext = "32047-01.htm";
						else if (cond == 5)
							htmltext = "32047-07.htm";
						else if (cond == 6)
							htmltext = "32047-08.htm";
						else if (cond == 7)
							htmltext = "32047-11.htm";
						else if (cond == 8)
							htmltext = "32047-12.htm";
						else if (cond > 8 && cond < 13)
							htmltext = "32047-15.htm";
						else if (cond == 13)
							htmltext = "32047-16.htm";
						else if (cond == 14)
							htmltext = "32047-19.htm";
						else if (cond > 14 && cond < 23)
							htmltext = "32047-20.htm";
						else if (cond == 23)
							htmltext = "32047-21.htm";
						else if (cond == 24)
							htmltext = "32047-26.htm";
						else if (cond == 25)
							htmltext = "32047-33.htm";
						break;
					
					case YUMI:
						if (cond == 2)
							htmltext = "32041-01.htm";
						else if (cond == 3)
							htmltext = "32041-05.htm";
						else if (cond > 3 && cond < 7)
							htmltext = "32041-06.htm";
						else if (cond == 7)
							htmltext = "32041-07.htm";
						else if (cond > 7 && cond < 15)
							htmltext = "32041-13.htm";
						else if (cond == 15)
							htmltext = "32041-14.htm";
						else if (cond == 16)
							htmltext = (!st.hasQuestItems(RESEARCH_REPORT)) ? "32041-17.htm" : "32041-18.htm";
						else if (cond > 16 && cond < 25)
							htmltext = "32041-22.htm";
						else if (cond == 25)
							htmltext = "32041-26.htm";
						break;
					
					case WEATHERMASTER_1:
						if (cond == 10)
						{
							htmltext = "32042-01.htm";
							st.playSound("AmbSound.cd_crystal_loop");
						}
						else if (cond == 11)
						{
							if (st.getInt("talk") + st.getInt("talk1") + st.getInt("talk2") == 3)
								htmltext = "32042-14.htm";
							else
								htmltext = "32042-06.htm";
						}
						else if (cond > 11)
							htmltext = "32042-15.htm";
						break;
					
					case WEATHERMASTER_2:
						if (cond == 17)
							htmltext = "32043-01.htm";
						else if (cond == 18)
						{
							if (st.getInt("talk") + st.getInt("talk1") == 2)
								htmltext = "32043-29.htm";
							else
								htmltext = "32043-06.htm";
						}
						else if (cond > 18)
							htmltext = "32043-30.htm";
						break;
					
					case WEATHERMASTER_3:
						if (cond == 20)
							htmltext = "32044-01.htm";
						else if (cond == 21)
							htmltext = "32044-06.htm";
						else if (cond > 21)
							htmltext = "32044-18.htm";
						break;
					
					case DOCTOR_CHAOS_SECRET_BOOKSHELF:
						if (cond == 14)
							htmltext = "32045-01.htm";
						else if (cond > 14)
							htmltext = "32045-03.htm";
						break;
				}
				break;
			
			case STATE_COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}
}