package Dev.FakePlayer;

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


/**
 * @author Gabia
 *
 */

public enum FakePlayerTitleManager
{
	INSTANCE;

	public static final Logger _log = Logger.getLogger(FakePlayerTitleManager.class.getName());
	private List<String> _fakePlayerTitle;

	public void initialise()
	{
		loadWordlist();
	}

	public String getRandomAvailableName()
	{
		String name = getRandomNameFromWordlist();

		while (nameAlreadyExists(name))
		{
			name = getRandomNameFromWordlist();
		}

		return name;
	}

	private String getRandomNameFromWordlist()
	{
		return _fakePlayerTitle.get(Rnd.get(0, _fakePlayerTitle.size() - 1));
	}

	public List<String> getFakePlayerNames()
	{
		return _fakePlayerTitle;
	}

	private void loadWordlist()
	{
		try (LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(new File("./config/aCis/phantom/Title_list.txt"))));)
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
			_log.log(Level.INFO, String.format("Loaded %s fake player title.", _fakePlayerTitle.size()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static boolean nameAlreadyExists(String name)
	{
		return PlayerInfoTable.getInstance().getPlayerObjectId(name) > 0;
	}
	
}
