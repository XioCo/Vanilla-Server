package net.scratchforfun.xioco;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.scratchforfun.debugg.Debug;

import org.bukkit.ChatColor;

public class Group {

	private static PreparedStatement group_exists;
	private static PreparedStatement group_create;
	private static PreparedStatement group_get;
	
	private String tableName = "xioco_group";
	
	public Group(){
		try {
			Group.group_exists = SQLConnection.connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE name=? LIMIT 1;");
			Group.group_create = SQLConnection.connection.prepareStatement("INSERT INTO `" + tableName + "` values(0, ?)");
			Group.group_get = SQLConnection.connection.prepareStatement("SELECT group_ FROM`" + PlayerInfo.tableNamePlayer + "` WHERE uuid=?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean doesExist(String name){
		Debug debug = Debug.start();
		SQLConnection.openConnection(false);
		
		try{
			group_exists.setString(1, name);
			
			ResultSet result = group_exists.executeQuery();
			
			if(result.next()){
				debug.print(ChatColor.BLUE+"GroupExists: ");
				return true;
			}
			
			result.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		debug.print(ChatColor.BLUE+"GroupExists: ");
		return false;
	}

	public static void createGroup(String name) {
		Debug debug = Debug.start();
		SQLConnection.openConnection(false);
		
		try{
			group_create.setString(1, name);
			group_create.execute();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		debug.print(ChatColor.BLUE+"GroupCreate: ");
	}
	
	public static String getGroup(String UUID){
		Debug debug = Debug.start();
		String group = null;
		
		try{
			group_get.setString(1, UUID);
			ResultSet result = group_get.executeQuery();
			if(result.next()) group = result.getString("group_");
			result.close();
		}catch(SQLException e){
			e.printStackTrace();
		}

		debug.print(ChatColor.BLUE+"GetGroup: ");
		return group;
	}

	public static boolean inSameGroup(String breaker, String owner) {
		// If you're not in a group, just return false
		if(getGroup(breaker).equals("")) return false;

		// If the owner is not in a group, just return false
		if(getGroup(owner).equals("")) return false;
		
		// Return true if the players are in the same group
		return getGroup(breaker).equals(getGroup(owner));
	}
	
	public static boolean legalCharacters(String text){
		return text.matches("[a-zA-ZÊ¯Â∆ÿ≈]+");
	}
	
}
