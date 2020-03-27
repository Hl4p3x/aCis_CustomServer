package Dev.Phantom.Task;

import java.util.List;

import Dev.Phantom.FakePlayer;
import Dev.Phantom.FakePlayerManager;

/**
 * @author Elfocrash
 */
public class AITask implements Runnable
{
	private final int _from;
	private int _to;
	
	

	public AITask(int from, int to)
	{
		_from = from;
		_to = to;
		
	
	}

	@Override
	public void run()
	{
		adjustPotentialIndexOutOfBounds();
		List<FakePlayer> fakePlayers = FakePlayerManager.getFakePlayers().subList(_from, _to);
		try
		{
			fakePlayers.stream().filter(x -> !x.getFakeAi().isBusyThinking()).forEach(x -> x.getFakeAi().thinkAndAct());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	private void adjustPotentialIndexOutOfBounds()
	{
		if (_to > FakePlayerManager.getFakePlayersCount())
			_to = FakePlayerManager.getFakePlayersCount();
	}
}
