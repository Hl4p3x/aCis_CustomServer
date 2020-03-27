package net.sf.l2j.gameserver.model.actor.instance;

import java.util.Calendar;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.stat.Experience;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Gabia
 *
 */
public class DobleClass extends Folk
{


	public DobleClass(int objectId, NpcTemplate template)
	    {
	        super(objectId, template);
	    }
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/mods/DobleClass/" + filename + ".htm";
	}

	    

	   
	   @Override
	    public void onBypassFeedback(Player player, String command)
	    {
	        if ((player.getLevel() < 76) && (player.getLevel() <= 80))
	        {
	            player.sendMessage("Usted No tiene El LvL Necesario.");
	            return;
	        }
	        if (!(player.getClassId().level() == 3))
	        {
	            player.sendMessage("Necesitas la tercera Class.");
	            return;
	        }
	        if (player.getInventory().getInventoryItemCount(Config.SUBCLASS_SETITEM, 0) >= 3)
	        {
	            player.sendMessage(player.getName() + " Ya tienes 3 SubClass");
	            return;
	        }
	        if (player.isAio())
	        {
	            player.sendMessage(player.getName() + "You may not add a new subclass." + "AioBuffer");
	            return;
	        }
	        if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegisteredInComp(player)) {
	            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
	        }
	        if (!Config.KARMA_PLAYER_CAN_USE_GK && player.getKarma() > 0) {
	            return;
	        }
	        if (player.isAttackingNow()) {
	            player.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT);
	            return;
	        }
	        if (player.isInCombat()) {
	            player.sendMessage("You cannot leave while you are in combat!");
	            return;
	        }
	        if (player.isInFunEvent()) {
	            player.sendMessage("You cannot leave while you are in TeamVsTeam!");
	            return;
	        }
			if (player.isHero())
			{
				player.sendMessage("Desculpe mais voce ja e um heroi da classe ." + player.setClassName(player.getBaseClass()));
				return;
			}
				
			
	        if (command.startsWith("Sagittarius"))
	        {
	            player.setClassId(92);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);
	            return;
	        }
	        if (command.startsWith("Adventurer"))
	        {
	            player.setClassId(93);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Duelist"))
	        {
	            player.setClassId(88);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Dreadnought"))
	        {
	            player.setClassId(89);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Phoenix_Knight"))
	        {
	            player.setClassId(90);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Hell_Knight"))
	        {
	            player.setClassId(91);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Hierophant"))
	        {
	            player.setClassId(98);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Cardinal"))
	        {
	            player.setClassId(97);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Arcana_Lord"))
	        {
	            player.setClassId(96);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Soultaker"))
	        {
	            player.setClassId(95);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Archmage"))
	        {
	            player.setClassId(94);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Moonlight_Sentinel"))
	        {
	            player.setClassId(102);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Wind_Rider"))
	        {
	            player.setClassId(101);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Sword_Muse"))
	        {
	            player.setClassId(100);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("EvaTemplar"))
	        {
	            player.setClassId(99);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Eva_Saint"))
	        {
	            player.setClassId(105);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Mystic_Muse"))
	        {
	            player.setClassId(103);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Elemental Master"))
	        {
	            player.setClassId(104);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Ghost_Sentinel"))
	        {
	            player.setClassId(109);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Ghost_Hunter"))
	        {
	            player.setClassId(108);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Spectral_Dancer"))
	        {
	            player.setClassId(107);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Shillien_Templar"))
	        {
	            player.setClassId(106);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Shillien_Saint"))
	        {
	            player.setClassId(112);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Storm_Screamer"))
	        {
	            player.setClassId(110);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Spectral_Master"))
	        {
	            player.setClassId(111);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Grand_Khauatari"))
	        {
	            player.setClassId(114);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Titan"))
	        {
	            player.setClassId(113);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Doomcryer"))
	        {
	            player.setClassId(116);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Dominator"))
	        {
	            player.setClassId(115);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Maestro"))
	        {
	            player.setClassId(118);
	            player.setBaseClass(player.getBaseClass());
	            setSubClass(player);

	            return;
	        }
	        if (command.startsWith("Fortune_Seeker"))
	        {
	            player.setClassId(117);
	            player.setBaseClass(player.getBaseClass());
	            
	            setSubClass(player);
	            
	            return;
	        }
	        
	        if (command.startsWith("Clean_Skill"))
	        {

					
			for (L2Skill skill : player.getSkills().values())
			player.removeSkill(skill.getId(), true);
					
			player.sendMessage("You removed all skills " + player.getName() + ".");
			player.sendSkillList();
	            return;
	        }
	        
			else if (command.startsWith("Chat"))
			{
				int val = 0;
				try
				{
					val = Integer.parseInt(command.substring(5));
				}
				catch (IndexOutOfBoundsException ioobe)
				{
				}
				catch (NumberFormatException nfe)
				{
				}
				
				// Show half price HTM depending of current date. If not existing, use the regular "-1.htm".
				if (val == 1)
				{
					Calendar cal = Calendar.getInstance();
					if (cal.get(Calendar.HOUR_OF_DAY) >= 20 && cal.get(Calendar.HOUR_OF_DAY) <= 23 && (cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7))
					{
						NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						String content = HtmCache.getInstance().getHtm("data/html/mods/DobleClass/" + getNpcId() + ".htm");
						if (content == null)
							content = HtmCache.getInstance().getHtmForce("data/html/mods/DobleClass/" + getNpcId() + "-1.htm");
						
						html.setHtml(content);
						html.replace("%objectId%", String.valueOf(getObjectId()));
						player.sendPacket(html);
						
						player.sendPacket(ActionFailed.STATIC_PACKET);
						return;
					}
				}
				showChatWindow(player, val);
			}
	        if (command.startsWith("learn_skills"))
				player.rewardSkills();
	        
	    }
	   
	   public static void setSubClass(Player player)
	    {
		    player.getInventory().addItem("ItemQuest", Config.SUBCLASS_SETITEM, Config.SUBCLASS_COUNT, player, player.getTarget());
		    player.sendMessage(player.getName() + " You renewal " + Config.SUBCLASS_ITEMNAME + "." + Config.SUBCLASS_COUNT);
		    player.addExpAndSp(Experience.LEVEL[Config.SUBCLASS_LVL], 0);
	        player.sendSkillList();
	        player.rewardSkills();
	        player.sendMessage(Config.SUBCLASS_TEX1 + " " + player.getName() + " " + Config.SUBCLASS_TEX2 + " " +  Config.SUBCLASS_TEX3);
	        player.broadcastUserInfo();
	        player.stopAllEffectsExceptThoseThatLastThroughDeath();
			player.getAvailableSkills();
			player.disarmWeapons();
			player.store();


            if (Config.ENABLE_EFFECT_HERO) 
            {
            	player.broadcastPacket(new MagicSkillUse(player, player, 1034, 1, 1, 1));
                player.broadcastUserInfo();
            }
            
            if (Config.ENABLE_EFFECT_LVLUP) 
            {
            	player.broadcastPacket(new MagicSkillUse(player, player, 5103, 1, 1, 0));
                player.broadcastUserInfo();
            }
            setDisconnected(player);
	    }
	   
	   public static void setDisconnected(Player player)
	    {
	        player.sendMessage(player.getName() + " " + "You are disconnected");
	        ThreadPool.schedule(() -> player.logout(false), 9000);
	        return;
	    } 
	   
	   @Override
	    public void showChatWindow(Player playerInstance, int val)
	    {
	        if (playerInstance == null)
	            return;
	        
	        String htmContent = HtmCache.getInstance().getHtm("data/html/mods/DobleClass/" + getNpcId() + ".htm");
	        if (val > 0)
	        {
	            htmContent = HtmCache.getInstance().getHtm("data/html/mods/DobleClass/" + getNpcId() + "-1.htm");
	        }
		   if (htmContent != null)
	        {
		   NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
           npcHtmlMessage.setHtml(htmContent);
           npcHtmlMessage.replace("%name%", playerInstance.getName());
           npcHtmlMessage.replace("%class%", playerInstance.setClassName(playerInstance.getBaseClass()));
           npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
           playerInstance.sendPacket(npcHtmlMessage);
	        }
	      showChatWindow(playerInstance, getHtmlPath(getNpcId(), val));
	    }
	   
	   
	    
}
