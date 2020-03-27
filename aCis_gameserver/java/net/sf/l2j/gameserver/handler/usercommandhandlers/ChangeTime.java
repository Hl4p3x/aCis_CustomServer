package net.sf.l2j.gameserver.handler.usercommandhandlers;

import java.math.BigDecimal;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IUserCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

/**
 * @author Williams
 *
 */
public class ChangeTime implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		120,121
	};
	
	@Override
	public void useUserCommand(int id, Player activeChar)
	{
		int _calcule = (int) arredondaValor(1, activeChar.getOnlineTime() / 3600);

		if (id == 120)
		{
			if ((_calcule >= 1) && (activeChar.getPvpKills() >= Config.MIN_PVP))
			{
				activeChar.addItem("Coins", Config.ID_REWARD, _calcule, activeChar, true);
				activeChar.setOnlineTime(0);
			}
			else
			{
				if (activeChar.getPvpKills() < Config.MIN_PVP)
					activeChar.sendMessage("Voc� precisa de "+ Config.MIN_PVP +" pvp para prosseguir com a troca. Voc� s� tem " + activeChar.getPvpKills() + " PvP'S");
				
				if (_calcule < 1)
					activeChar.sendMessage("Voc� n�o tem 1 hora online agora.");
			}
		}
		else if(id == 121)
		{
			if (_calcule >= 1) 
				activeChar.sendMessage("Voc� tem atualmente "+ _calcule +" horas online.");
			else  if (_calcule < 1)
				activeChar.sendMessage("Voc� tem atualmente " + activeChar.getOnlineTime() / 60 + " minutos online.");
		}
		
		return;
	}


	@SuppressWarnings("deprecation")
	public double arredondaValor(int casasDecimais, double valor)
	{
		return new BigDecimal(valor).setScale(casasDecimais, 3).doubleValue();
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}