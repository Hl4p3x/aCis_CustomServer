package Dev.ItemSkins;


import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.clientpackets.RequestBypassToServer;

/**
 * @author Juvenil Walker
 *
 */
public class Skin31 implements IItemHandler

{

	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
    {
        if(Config.ALLOW_DRESS_ME_SYSTEM)
        {
            if(!(playable instanceof Player))
                return;
            Player activeChar = (Player)playable;
            
            RequestBypassToServer.setPart(activeChar, "weap", Config.SKIN_NAME31);
            
            if (activeChar.isDressMeEnabled())
			{
				activeChar.setDressMeEnabled(false);
				activeChar.broadcastUserInfo();
				activeChar.sendMessage("You have disabled skin.");
				
			}
			else
			{
				activeChar.setDressMeEnabled(true);
				activeChar.broadcastUserInfo();
				activeChar.sendMessage("You have activated " + Config.NAME31 + " skin.");
				
			}
        }
        else
        	playable.sendMessage("Sorry, admin has disabled skins.");
    }
	
	public int[] getItemIds()
    {
        return ITEM_IDS;
    }

    private static final int ITEM_IDS[] = {
    	Config.SKIN_ITEM_31
    };
}