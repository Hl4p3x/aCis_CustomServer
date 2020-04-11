package Dev.Phantom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.sql.PlayerInfoTable;

public enum FakePlayerTitleManager
{
	INSTANCE;
	
	public static final Logger _log = Logger.getLogger(FakePlayerTitleManager.class.getName());
	private List<String> _fakePlayerTitle;
	
	public void initialise()
	{
		loadWordlist();
	}
	
	public String getRandomAvailableTitle()
	{
		String title = getRandomTitleFromWordlist();
		
		while (titleAlreadyExists(title))
		{
			title = getRandomTitleFromWordlist();
		}
		
		return title;
	}
	
	private String getRandomTitleFromWordlist()
	{
		return _fakePlayerTitle.get(Rnd.get(0, _fakePlayerTitle.size() - 1));
	}
	
	public List<String> getFakePlayerTitle()
	{
		return _fakePlayerTitle;
	}
	
	private void loadWordlist()
	{
		try (LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(new File("./config/aCis/Phantom/Faketitle.txt"))));)
		{
			String line;
			ArrayList<String> playersList = new ArrayList<>();
			while ((line = lnr.readLine()) != null)
			{
				if (line.trim().length() == 0 || line.startsWith("#"))
					continue;
				playersList.add(line);
			}
			_fakePlayerTitle = playersList;
			_log.log(Level.INFO, String.format("Loaded %s fake player titles.", _fakePlayerTitle.size()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static boolean titleAlreadyExists(String title)
	{
		return PlayerInfoTable.getInstance().getPlayerObjectId(title) > 0;
	}
}
