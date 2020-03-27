package net.sf.l2j.gameserver.taskmanager;

import java.util.Vector;

import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.Player;
/**
 * @author Williams
 *
 */
public class CancelTaskManager implements Runnable
{
    private Player _player;
    private Vector<L2Skill> _buffsCanceled;

    public CancelTaskManager(Player player, Vector<L2Skill> skill)
    {
    	_player = player;
    	_buffsCanceled = skill;
    }
    
    @Override
    public void run()
    {
        if (_player == null)        
            return;
        
        for (L2Skill skill : _buffsCanceled)
        {
            if (skill == null)
                continue;
          
            skill.getEffects(_player, _player);
        }
    }
}