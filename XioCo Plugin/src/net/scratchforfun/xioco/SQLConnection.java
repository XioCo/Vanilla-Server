package net.scratchforfun.xioco;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import net.scratchforfun.debugg.Debug;

import org.bukkit.ChatColor;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

public class SQLConnection {

	public static Connection connection;
	
	
	//Default values!
	private static String ipAddress = "localhost";
	private static String database = "XioCo";
	private static int port = 3306;
	
	private static String username = "root";
	private static String password = "";
	
	private static long time = 0;
	private static int count = 0;
	
	public static void grabInformation(File config){
		try{
			//Grabs the text from the file
			Scanner scanner = new Scanner(config);
			while(scanner.hasNext()){
				String text = scanner.nextLine();
				String[] string = text.split(":");
				
				//Grabs the data
				ipAddress = Configs.getValue(Configs.sqlFile, "ip");
				database = Configs.getValue(Configs.sqlFile, "database");
				port = Integer.parseInt(Configs.getValue(Configs.sqlFile, "port"));
				username = Configs.getValue(Configs.sqlFile, "username");
				password = Configs.getValue(Configs.sqlFile, "password");
			}
			//Closes of the scanner
			scanner.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public synchronized static void openConnection(boolean startup){
		boolean established = true;
		
		if(System.currentTimeMillis()-time > 1000){
			time = System.currentTimeMillis();
			Debug.s_print(ChatColor.RED+"SQLConnectionsPerSecond: " + count);
			count=0;
		};
		count++;
		
		try{
			if(connection == null || connection.isClosed()){
				//jdbc:mysql://IP_HERE:PORT_HERE/DATABASE_NAME", "USERNAME", "PASSWORD
				connection = DriverManager.getConnection("jdbc:mysql://" + ipAddress + ":" + port + "/" + database, username, password);
			}
		}catch(Exception e){
			e.printStackTrace();
			established = false;
			System.out.println(Ansi.ansi().fg(Color.RED)+"Unable to connect to SQL!"+Ansi.ansi().fg(Color.WHITE));
		}finally{
			if(startup && established) System.out.println(Ansi.ansi().fg(Color.GREEN)+"SQL Connection established!"+Ansi.ansi().fg(Color.WHITE));
		}
	}
	
	public static void closeConnection(){
		try {
			//Does this actually do anything?
			if(connection != null && connection.isClosed());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
