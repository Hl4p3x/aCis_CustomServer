package Dev.EventMoster;

import java.util.List;

import net.sf.l2j.commons.lang.StringUtil;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;


/**
 * @author Melron
 */
public class SantasReward
{
   private IntStringHolder _exp;
   private IntStringHolder _sp;
   private IntStringHolder _rec;
   private List<IntIntHolder> rewardItems;
  
   public SantasReward(IntStringHolder exp, IntStringHolder sp, IntStringHolder rec, List<IntIntHolder> rewards)
   {
       _exp = exp;
       _sp = sp;
       _rec = rec;
       rewardItems = rewards;
   }
  
   public boolean applyTo(Player player)
   {
       if (player == null)
           return false;
      
       if (hasExpReward())
       {
           player.getStat().addExp(_exp.getValue());
           player.sendMessage(_exp.getString().replaceAll("\\$player", player.getName()).replaceAll("\\$val", String.valueOf(_exp.getValue())));
       }
      
       if (hasSpReward())
       {
           player.getStat().addSp(_sp.getValue());
           player.sendMessage(_sp.getString().replaceAll("\\$player", player.getName()).replaceAll("\\$val", String.valueOf(_sp.getValue())));
       }
      
       if (hasRecReward())
       {
           player.setRecomHave(_rec.getValue());
           player.sendMessage(_rec.getString().replaceAll("\\$player", player.getName()).replaceAll("\\$val", String.valueOf(_rec.getValue())));
       }
      
       if (hasRewardItems())
           rewardItems.forEach(reward -> player.addItem("SantasReward", reward.getId(), reward.getValue(), null, true));
      
       return true;
   }
  
   public boolean hasExpReward()
   {
       return _exp != null && _exp.getValue() > 0;
   }
  
   public boolean hasSpReward()
   {
       return _sp != null && _sp.getValue() > 0;
   }
  
   public boolean hasRecReward()
   {
       return _rec != null && _rec.getValue() > 0;
   }
  
   public boolean hasRewardItems()
   {
       return rewardItems != null && rewardItems.size() > 0;
   }
  
   @Override
   public String toString()
   {
       final StringBuilder sb = new StringBuilder();
      
       StringUtil.append(sb, "\n==================", "\nExp : " + _exp.getValue(), ",\"" + _exp.getString() + "\"", "\nSp : " + _sp.getValue(), ",\"" + _sp.getString() + "\"", "\nRec : " + _rec.getValue(), ",\"" + _rec.getString() + "\"", "\nRewardItems : ");
       rewardItems.forEach(holder -> StringUtil.append(sb, "\nId: ", holder.getId(), " Quantity: ", holder.getValue()));
       return sb.toString();
   }
}
