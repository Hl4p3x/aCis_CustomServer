package Dev.PartyFarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.Config;

public class InitialPartyFarm
{
	private static InitialPartyFarm _instance = null;
	protected static final Logger _log = Logger.getLogger(InitialPartyFarm.class.getName());
	private Calendar NextEvent;
	private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");

	public static InitialPartyFarm getInstance()
	{
		if (_instance == null)
			_instance = new InitialPartyFarm();
		return _instance;
	}

	public String getRestartNextTime()
	{
		if (NextEvent.getTime() != null)
			return format.format(NextEvent.getTime());
		return "Erro";
	}

	public void StartCalculationOfNextEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar testStartTime = null;
			long flush2 = 0L;
			long timeL = 0L;
			int count = 0;
			for (String timeOfDay : Config.EVENT_BEST_FARM_INTERVAL_BY_TIME_OF_DAY)
			{
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(11, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(12, Integer.parseInt(splitTimeOfDay[1]));
				testStartTime.set(13, 0);
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
					testStartTime.add(5, 1);
				timeL = testStartTime.getTimeInMillis() - currentTime.getTimeInMillis();
				if (count == 0)
				{
					flush2 = timeL;
					NextEvent = testStartTime;
				}
				if (timeL < flush2)
				{
					flush2 = timeL;
					NextEvent = testStartTime;
				}
				count++;
			}
			_log.info("[Party Farm]: Proximo Evento: " + NextEvent.getTime().toString());
			ThreadPool.schedule(new StartEventTask(), flush2);
		}
		catch (Exception e)
		{
			System.out.println("[Party Farm]: Algum erro nas config foi encontrado!");
		}
	}

	class StartEventTask implements Runnable
	{
		StartEventTask()
		{
		}

		@Override
		public void run()
		{
			InitialPartyFarm._log.info("[Party Farm]: Event Started.");
			PartyFarm.bossSpawnMonster();
		}
	}
}
