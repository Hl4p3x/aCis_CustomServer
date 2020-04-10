package net.sf.l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;

import Dev.EventMoster.ChristmasEvent;


public class SantaClaus extends Folk
{
   private Player selective = null;
   private boolean rewarded = false;
  
   public SantaClaus(int objectId, NpcTemplate template)
   {
       super(objectId, template);
   }
  
   @Override
   public void onBypassFeedback(Player player, String command)
   {
       StringTokenizer st = new StringTokenizer(command, " ");
       String currentCommand = st.nextToken();
      
       if (currentCommand.startsWith("claim"))
       {
           if (player == null || selective == null)
               return;
          
           if (selective == player && !rewarded)
           {
               rewarded = ChristmasEvent.getInstance().getRandomReward().applyTo(player);
               if (rewarded)
               {
                   broadcastNpcSay("Bye!");
                   deleteMe();
               }
              
           }
       }
   }
  
   @Override
   public String getHtmlPath(int npcId, int val)
   {
       return "data/html/mods/santa/" + (this.getNpcId() == 50010 ? "1" : "2") + ".htm";
   }
  
   public void setSelective(Player player)
   {
       selective = player;
   }
}
 