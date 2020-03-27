package Dev.Phantom.Task;

import java.util.List;

import net.sf.l2j.commons.concurrent.ThreadPool;

import Dev.Phantom.FakePlayerTaskManager;

/**
 * @author Elfocrash
 *
 */
public class AITaskRunner implements Runnable
{	
	@Override
	public void run()
	{		
		FakePlayerTaskManager.INSTANCE.adjustTaskSize();
		List<AITask> aiTasks = FakePlayerTaskManager.INSTANCE.getAITasks();		
		aiTasks.forEach(aiTask -> ThreadPool.execute(aiTask));
	}	
}