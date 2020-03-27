package Dev.AutoBackup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.l2j.commons.concurrent.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.util.Mysql;


/**
 * @author DevKatara
 * 
 * note to RESTORE
 * Unzip the file
 * Then go to database via navicat and run Execute SQL File
 * Then declick the option "Run multiple queries in each execution" to avoid conflict with locks of tables 
 */
public class BackupDBSave
{
	private String database_name = "c4";
	private boolean DEBUG_SYSTEM = false;
	private long initializeAfterTime = 1000 * 60 * 60 * 1; // Start in 2 hour
	private long checkEveryTime = 1000 * 60 * 60 * 1; // Every 6 hours
	
	protected BackupDBSave()
	{
		ThreadPool.scheduleAtFixedRate(() -> BackupDBToSql(), initializeAfterTime, checkEveryTime);
		
		System.out.println("Database Backup Manager: Loaded");
	}

	public void BackupDBToSql()
	{
		String pathOfMysql = "\"";
		
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT @@basedir");
			rs = statement.executeQuery();
			
			while (rs.next())
			{
				pathOfMysql += rs.getString(1);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			Mysql.closeQuietly(con, statement, rs);
		}
		
		if (pathOfMysql.isEmpty())
		{
			System.out.println("Error on backup database. Empty path of mysql.");
			return;
		}
		
		// Give the specific path (pathOfMysql out = C:\Program Files\MySQL\MySQL Server 5.7\)
		pathOfMysql += "bin\\mysqldump" + "\"";
		
		if(DEBUG_SYSTEM) System.out.println("Path of mysql: " + pathOfMysql);
		
		// Initialize code for backup
		try
		{
			// Section for path of system
			URL applicationRootPathURL = getClass().getProtectionDomain().getCodeSource().getLocation();
			File applicationRootPath = new File(applicationRootPathURL.getPath());
			File myFile = new File(applicationRootPath.getParent());
			File lastMyFile = new File(myFile.getParent());

			String dbUser = Config.DATABASE_LOGIN;
			String dbPass = Config.DATABASE_PASSWORD;
			
			String commandOfMysqlDump = " " + database_name + " --single-transaction -u" + dbUser + " -p" + dbPass + " --skip-create-options --skip-comments --disable-keys > ";
			
			/* NOTE: Creating Path Constraints for folder saving */
			/* NOTE: Here the backup folder is created for saving inside it */
			String folderPath = "backup";
			
			/* NOTE: Creating Folder if it does not exist */
			File f1 = new File(folderPath);
			f1.mkdir();
			
			/* NOTE: Creating Path Constraints for backup saving */
			/* NOTE: Here the backup is saved in a folder called backup with the name backup.sql */
			String pathUntilDirectory = (lastMyFile.getAbsolutePath() + "\\backup\\").replaceAll("%20", " ");
			String savePath = ("\""+pathUntilDirectory + "backup.sql\"").replaceAll("%20", " ");

			/* NOTE: Used to create a cmd command */
			String commandToExecute = "cmd /c "+ pathOfMysql + commandOfMysqlDump + savePath;
			
			if (DEBUG_SYSTEM)
			{
				System.out.println("Save path of sql file: " + savePath);
				System.out.println("Command To Execute: " + commandToExecute);
			}
			
			/* NOTE: Executing the command here */
			Process runtimeProcess = Runtime.getRuntime().exec(new String[] {"cmd", "/c", commandToExecute });
			
			if (DEBUG_SYSTEM)
			{
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(runtimeProcess.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(runtimeProcess.getErrorStream()));
				
				// read the output from the command
				System.out.println("Here is the standard output of the command:\n");
				String s = null;
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}
				
				// read any errors from the attempted command
				System.out.println("Here is the standard error of the command (if any):\n");
				while ((s = stdError.readLine()) != null) {
					System.out.println(s);
				}
			}
			
			int processComplete = runtimeProcess.waitFor();
			
			/* NOTE: processComplete=0 if correctly executed, will contain other values if not */
			if (processComplete == 0)
			{
				System.out.println("Backup to SQL Complete");
				
				// Zip the sql file
				zipAFile(pathUntilDirectory);
				
				// Delete the backup.sql file
				deleteAFile(savePath.replaceAll("\"", ""));
			}
			else
			{
				System.out.println("Backup to SQL Failure");
			}
		}
		catch (IOException | InterruptedException ex)
		{
			System.out.println("Error at Backuprestore" + ex.getMessage());
		}
	}
	
	@SuppressWarnings("resource")
	private static void zipAFile(String pathToSave)
	{
		byte[] buffer = new byte[1024];
		
		try
		{
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
            Date date = new Date();
			
			FileOutputStream fos = new FileOutputStream(pathToSave + "Backup_"+dateFormat.format(date)+".zip");
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry ze = new ZipEntry("backup.sql");
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream(pathToSave + "\\backup.sql");
			
			int len;
			while ((len = in.read(buffer)) > 0)
			{
				zos.write(buffer, 0, len);
			}
			
			in.close();
			zos.closeEntry();
			
			// remember close it
			zos.close();
			
			System.out.println("Done the zip of backup.");
			
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	private static void deleteAFile(String path) {
		try{
    		File file = new File(path);
    		System.out.println(file.delete() ? (file.getName() + " is deleted!") : ("Delete operation is failed."));
    		
    	}catch(Exception e){

    		e.printStackTrace();

    	}
	}

	public static BackupDBSave getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final BackupDBSave _instance = new BackupDBSave();
	}
}