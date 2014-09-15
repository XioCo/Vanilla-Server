package net.scratchforfun.xioco;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.scratchforfun.debugg.Debug;
import net.scratchforfun.xioco.clock.Clock;
import net.scratchforfun.xioco.clock.Clock.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInfo {

	public static String tableNamePlayer = "xioco_player";
	public static String tableNameBan = "xioco_ban";
	public static String tableNameGroup = "xioco_group";
	
	public String uuid;
	public String message;
	public int gull;
	public String group;
	public String groupLeder;
	public String groupInvite;
	public String loginInfo;
	public String socialSpy;
	public String inventory;
	
	public String home1;
	public Home home1L;
	public String home2;
	public Home home2L;
	public String home3;
	public Home home3L;
	
	public PlayerInfo(String uuid){
		//TODO UPDATE WHEN UPDATING SQL STRUCTURE!
		this.uuid = uuid;
		this.message = "";
		this.gull = 0;
		this.group = "";
		this.groupLeder = "false";
		this.groupInvite = "";
		this.loginInfo = "true";
		//TODO this.socialSpy = PermissionsEx.getUser(Bukkit.getOfflinePlayer(this.uuid).getName()).has("xioco.socialspy")?"true":"false";
		this.socialSpy = "false";
		this.inventory = "";
	}
	
	public void grabInventory(Player player) {
		this.inventory = "";
		
		PlayerInventory inventory = player.getInventory();
		for(int i = 0; i < inventory.getSize(); i++){
			ItemStack itemstack = inventory.getItem(i);
			if(itemstack != null){
				this.inventory+=i+":";
				this.inventory+=itemstack.getTypeId()+":";
				this.inventory+=itemstack.getAmount()+";";
			}
		}
		
		//To make sure that it sets you into work, and is not fooled
		if(this.inventory.equals("")) this.inventory = "EMPTY";
	}
	
	public Map<String, Home> getHomes(){
		Map<String, Home> homes = new HashMap<String, Home>();
		homes.put(home1, home1L);
		homes.put(home2, home2L);
		homes.put(home3, home3L);
		
		return homes;
	}
	
	public Home getHome(int i){
		if(i == 1) return getHomes().get(home1);
		if(i == 2) return getHomes().get(home2);
		if(i == 3) return getHomes().get(home3);
		
		return null;
	}
	
	public String getHomeName(int i){
		if(i == 1) return home1;
		if(i == 2) return home2;
		if(i == 3) return home3;
		
		return null;
	}
	
	/** Hvis du bryr deg mye om casesensitivitet! */
	public String getHomeName(String name){
		if(name.equalsIgnoreCase(home1)) return home1;	
		if(name.equalsIgnoreCase(home2)) return home2;
		if(name.equalsIgnoreCase(home3)) return home3;
		
		return null;
	}
	
	public int getHomeNr(String name){
		if(name.equalsIgnoreCase(home1)) return 1;	
		if(name.equalsIgnoreCase(home2)) return 2;
		if(name.equalsIgnoreCase(home3)) return 3;
		
		return 0;
	}
	
	public Home getHome(String home){
		Home homes = getHomes().get(home.toLowerCase());
		return homes;
	}
	
	public void setHome(int i, String name, String world, int x, int y, int z){
		Home home = new Home(name, world, x, y, z);
		if(i == 1){
			home1 = name.toLowerCase();
			home1L = home;
		}
		
		if(i == 2){
			home2 = name.toLowerCase();
			home2L = home;
		}
		
		if(i == 3){
			home3 = name.toLowerCase();
			home3L = home;
		}
	}
	
	public void delHome(int i){
		if(i == 1){
			home1 = null;
			home1L = null;
		}
		
		if(i == 2){
			home2 = null;
			home2L = null;
		}
		
		if(i == 3){
			home3 = null;
			home3L = null;
		}
	}
	
	public boolean inWork(){
		return !this.inventory.equals("");
	}
	
	public Player getPlayer(){
		for(Player player : Bukkit.getOnlinePlayers()){
			if(player.getUniqueId().toString().equals(this.uuid)) return player;
		}
		
		return null;
	}
	
	public static PlayerInfo[] getPlayersInfo(Player[] players) {
		String[] UUIDs = new String[players.length];
		
		for(int i = 0; i < players.length; i++){
			UUIDs[i] = players[i].getUniqueId().toString();
		}
		
		return getPlayersInfo(UUIDs);
	}
	
	public static PlayerInfo[] getPlayersInfo(String[] players) {
		Debug debug = Debug.start();
		SQLConnection.openConnection(false);
		
		List<PlayerInfo> playerInfos = new ArrayList<PlayerInfo>();
		PlayerInfo[] infos = new PlayerInfo[players.length];
		
		String uuidsSeperatedByComma = "";
		for(String player : players){
			if(uuidsSeperatedByComma.length() > 0) uuidsSeperatedByComma+=",";
			uuidsSeperatedByComma+="'"+player+"'";
		}
		
		try {
			Statement stmt = SQLConnection.connection.createStatement();	
			ResultSet result = stmt.executeQuery("SELECT * FROM " + PlayerInfo.tableNamePlayer + " WHERE uuid IN (" + uuidsSeperatedByComma + ");");
			
			while(result.next()){
				//TODO UPDATE WHEN UPDATING SQL STRUCTURE!
				String loginInfo = result.getString("login_notifications");
				String inventory = result.getString("inventory");
				String message = result.getString("message");
				String socialSpy = result.getString("socialspy");
				String home1 = result.getString("home_1_name");
				String home1world = result.getString("home_1_world");
				int home1x = result.getInt("home_1_x");
				int home1y = result.getInt("home_1_y");
				int home1z = result.getInt("home_1_z");
				String home2 = result.getString("home_2_name");
				String home2world = result.getString("home_2_world");
				int home2x = result.getInt("home_2_x");
				int home2y = result.getInt("home_2_y");
				int home2z = result.getInt("home_2_z");
				String home3 = result.getString("home_3_name");
				String home3world = result.getString("home_3_world");
				int home3x = result.getInt("home_3_x");
				int home3y = result.getInt("home_3_y");
				int home3z = result.getInt("home_3_z");
				int gull = result.getInt("gull");
				String group = result.getString("group_");
				String groupLeder = result.getString("groupLeder");
				String groupInvite = result.getString("groupInvite");
				
				PlayerInfo playerInfo = new PlayerInfo(result.getString("uuid"));
				playerInfo.loginInfo = loginInfo;
				playerInfo.inventory = inventory;
				playerInfo.message = message;
				playerInfo.gull = gull;
				playerInfo.group = group;
				playerInfo.groupLeder = groupLeder;
				playerInfo.groupInvite = groupInvite;
				playerInfo.socialSpy = socialSpy;
				playerInfo.home1 = home1.toLowerCase();
				playerInfo.home1L = new Home(home1, home1world, home1x, home1y, home1z);
				playerInfo.home2 = home2.toLowerCase();
				playerInfo.home2L = new Home(home2, home2world, home2x, home2y, home2z);
				playerInfo.home3 = home3.toLowerCase();
				playerInfo.home3L = new Home(home3, home3world, home3x, home3y, home3z);
				
				//Adds it to the list		
				playerInfos.add(playerInfo);	
				for(int i = 0; i < players.length; i++){
					if(players[i].equals(playerInfo.uuid)) infos[i] = playerInfo;
				}				
			}
			
			for(int i = 0; i < infos.length; i++){
				//If the player does not exist create it (?) Or not? <-- Creates fake players
				if(infos[i] == null){
					//addPlayer(players[i]);
				}
			}
			
			result.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("UNABLE TO CONNECT TO SQL! - LoginListener");
			e.printStackTrace();
		}
		
		debug.print(ChatColor.RED + "GetPlayerInfo: ");
		return infos;
	}
	
	public static PlayerInfo getPlayerInfoAndCreate(Player player){
		PlayerInfo info = getPlayerInfo(player); 
		if(info == null){
			addPlayer(player.getUniqueId().toString());
			info = getPlayerInfo(player); 
		}
		
		return info;
	}

	public static PlayerInfo getPlayerInfo(Player player){
		return getPlayerInfo(player.getUniqueId().toString());
	}
	
	public static PlayerInfo getPlayerInfo(String uuid){
		return getPlayersInfo(new String[]{uuid})[0];
	}
	
	@Deprecated
	public static void addPlayer(String uuid){
		Debug debug = Debug.start();
		SQLConnection.openConnection(false);
		
		PlayerInfo playerInfo = new PlayerInfo(uuid);
		
		try {
			//TODO UPDATE WHEN UPDATING SQL STRUCTURE!
			PreparedStatement stmt = SQLConnection.connection.prepareStatement("INSERT INTO " + PlayerInfo.tableNamePlayer + " values(0, '" + playerInfo.uuid + "','" + playerInfo.message + "','" + playerInfo.gull + "','" + playerInfo.group + "','" + playerInfo.groupLeder + "','" + playerInfo.groupInvite + "','" + playerInfo.socialSpy + "','" + playerInfo.loginInfo + "','" + playerInfo.inventory + "','" + "" + "','" + "" + "','" + 0 + "','" + 0 + "','" + 0 + "','" + "" + "','" + "" + "','" + 0 + "','" + 0 + "','" + 0 + "','" + "" + "','" + "" + "','" + 0 + "','" + 0 + "','" + 0 + "');");
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		debug.print(ChatColor.RED + "AddPlayer: ");
	}
	
	//Note! Plz, always make sure that you do not call this method if you are not sure if the player exists or not! <-- (I am not sure what will happen)
	public void writePlayerInfo(){
		Debug debug = Debug.start();
		SQLConnection.openConnection(false);
		
		try {
			String h1n = "";
			String h1w = "";
			int h1x = 0;
			int h1y = 0;
			int h1z = 0;
			if(this.home1L != null){
				h1n = this.home1L.name;
				h1w = this.home1L.world;
				h1x = this.home1L.x;
				h1y = this.home1L.y;
				h1z = this.home1L.z;
			}

			String h2n = "";
			String h2w = "";
			int h2x = 0;
			int h2y = 0;
			int h2z = 0;
			if(this.home2L != null){
				h2n = this.home2L.name;
				h2w = this.home2L.world;
				h2x = this.home2L.x;
				h2y = this.home2L.y;
				h2z = this.home2L.z;
			}

			String h3n = "";
			String h3w = "";
			int h3x = 0;
			int h3y = 0;
			int h3z = 0;
			if(this.home3L != null){
				h3n = this.home3L.name;
				h3w = this.home3L.world;
				h3x = this.home3L.x;
				h3y = this.home3L.y;
				h3z = this.home3L.z;
			}
			
			//TODO UPDATE WHEN UPDATING SQL STRUCTURE!
			PreparedStatement stmt = SQLConnection.connection.prepareStatement("UPDATE " + PlayerInfo.tableNamePlayer + " SET login_notifications='" + this.loginInfo + "', inventory='" + this.inventory + "', message='" + this.message + "', gull='" + this.gull + "', group_='" + this.group + "', groupLeder='" + this.groupLeder + "', groupInvite='" + this.groupInvite + "', socialspy='" + this.socialSpy + "', home_1_name='" + h1n + "', home_1_world='" + h1w + "', home_1_x='" + h1x + "', home_1_y='" + h1y + "', home_1_z='" + h1z + "', home_2_name='" + h2n + "', home_2_world='" + h2w + "', home_2_x='" + h2x + "', home_2_y='" + h2y + "', home_2_z='" + h2z + "', home_3_name='" + h3n + "', home_3_world='" + h3w + "', home_3_x='" + h3x + "', home_3_y='" + h3y + "', home_3_z='" + h3z + "' WHERE uuid='" + this.uuid + "';");
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		debug.print(ChatColor.RED + "WritePlayerInfo: ");
	}
	
	public void addItemsToPlayer(Player player){
		SQLConnection.openConnection(false);
		
		try {			
			Statement stmt = SQLConnection.connection.createStatement();
			ResultSet result = stmt.executeQuery("SELECT * FROM " + tableNamePlayer + " WHERE uuid='" + player.getUniqueId() + "';");
			
			//? Needed? Make sure you call it tho? (We know the UUID is unique, or it shoud atleast be)
			while(result.next()){
				String[] items = result.getString("inventory").split(";");
				
				//Clears inventory
				PlayerInventory inventory = player.getInventory();
				for(int i = 0; i < inventory.getSize(); i++){
					inventory.setItem(i, null);
				}
				
				//Creates and adds prev. items to inventory
				for(String item : items){
					if(!item.equalsIgnoreCase("EMPTY")){
						String[] args = item.split(":");
						
						inventory.setItem(Integer.parseInt(args[0]), new ItemStack(Material.getMaterial(Integer.parseInt(args[1])), Integer.parseInt(args[2])));
					}
				}
			}	
			
			result.close();
			stmt.close();
		} catch (SQLException e) {
			player.sendMessage(ChatColor.RED + "ERROR! PLZ CONTACT PLUGIN CREATOR! --> ScratchForFun");
			e.printStackTrace();
		}		
		
		this.inventory = "";
	}
	
	public static void getTableName(){
		tableNamePlayer = Configs.getValue(Configs.playerInfoFile, "tableName");
	}
	
	public static class BannedPlayer {
		private List<Reason> bans = new ArrayList<Reason>();
		private String uuid;
		
		public BannedPlayer(String uuid){
			this.uuid = uuid;
		}
		
		public static BannedPlayer getBannedPlayer(Player player){
			return getBannedPlayer(player.getUniqueId().toString());
		}
		
		public static BannedPlayer getBannedPlayer(String uuid){
			Debug debug = Debug.start();
			SQLConnection.openConnection(false);
			BannedPlayer bannedPlayer = new BannedPlayer(uuid);
			
			try {
				Statement stmt = SQLConnection.connection.createStatement();
				ResultSet result = stmt.executeQuery("SELECT * FROM " + PlayerInfo.tableNameBan + " WHERE uuid='" + uuid + "';");
				
				while(result.next()){
					//TODO UPDATE WHEN UPDATING SQL STRUCTURE!
					int id = result.getInt("id");
					String playerUUID = result.getString("uuid");
					String type = result.getString("type");
					String subject = result.getString("subject");
					String description = result.getString("descr");
					String authorUUID = result.getString("author");
					String creationDate = result.getString("creationdate");
					String editDate = result.getString("editdate");
					String editAuthor = result.getString("editauthor");
					
					bannedPlayer.bans.add(new Reason(id, playerUUID, Reason.Type.valueOf(type), subject, description, authorUUID, creationDate, editDate, editAuthor));
				}
				
				result.close();
				stmt.close();
			} catch (SQLException e) {
				System.out.println("UNABLE TO CONNECT TO SQL! - BannedPlayer");
				//e.printStackTrace();
			}
			
			debug.print(ChatColor.RED + "GetBannedPlayer: ");
			return bannedPlayer;
		}
		
		public static String getTime(){
			return getTime(Clock.getClock(), Clock.getDate());			
		}
		
		public static String getTime(Clock clock, Date date){
			return date.day+"."+date.month+"."+date.year+" - " + clock.hour+":"+clock.minute;			
		}
		
		public static String getAuthor(CommandSender sender){
			return sender instanceof Player?((Player)sender).getUniqueId().toString():"console";
		}
		
		public Status getStatus(){
			Reason reason = null;
			boolean allowed = true;
			
			if(bans.size() > 0){
				reason = bans.get(bans.size()-1);
				if(reason.editAuthor.equals("") && (reason.type.equals(Reason.Type.BAN) || (reason.type.equals(Reason.Type.TEMPBAN) && tempbanExpired(reason)))) allowed = false;
			}
			
			return new Status(allowed, reason); 
		}
		
		// TODO USES WRONG NAME (Expired = !unbanned)??
		public boolean tempbanExpired(Reason reason){
			String editDate = reason.editDate;
			boolean unbanned = false;
			
			if(!editDate.equals("")){
				Date date = Clock.getDate();
				Clock clock = Clock.getClock();
				
				String[] string = editDate.split(" - ");
				String[] eddate = string[0].split("\\.");
				if(Integer.parseInt(eddate[2]) == Integer.parseInt(date.year)){
					if(Integer.parseInt(eddate[1]) == Integer.parseInt(date.month)){
						if(Integer.parseInt(eddate[0]) == Integer.parseInt(date.day)){
							String[] edday = string[1].split(":");
							if(Integer.parseInt(edday[0]) == Integer.parseInt(clock.hour)){
								if(Integer.parseInt(edday[1]) == Integer.parseInt(clock.minute)){
									unbanned = true;
								}else if (Integer.parseInt(edday[1]) < Integer.parseInt(clock.minute)) unbanned = true;
							}else if (Integer.parseInt(edday[0]) < Integer.parseInt(clock.hour)) unbanned = true;
						}else if (Integer.parseInt(eddate[0]) < Integer.parseInt(date.day)) unbanned = true;
					}else if (Integer.parseInt(eddate[1]) < Integer.parseInt(date.month)) unbanned = true;
				}else if (Integer.parseInt(eddate[2]) < Integer.parseInt(date.year)) unbanned = true;
			}			
			
			return !unbanned;
		}
		
		public void writeBan(Reason reason){
			SQLConnection.openConnection(false);
				
			try {
				//TODO UPDATE WHEN UPDATING SQL STRUCTURE!
				PreparedStatement stmt = SQLConnection.connection.prepareStatement("INSERT INTO " + PlayerInfo.tableNameBan + " values('" + 0 + "','" + reason.playerUUID + "','" + reason.type.toString() + "','" + reason.subject + "','" + reason.description + "','" + reason.authorUUID + "','" + reason.creationDate + "','" + reason.editDate + "','" + reason.editAuthor + "');");
				stmt.execute();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		public void writeUnban(Reason reason) {
			SQLConnection.openConnection(false);
			
			try {
				//TODO UPDATE WHEN UPDATING SQL STRUCTURE!
				PreparedStatement stmt = SQLConnection.connection.prepareStatement("UPDATE " + PlayerInfo.tableNameBan + " SET editdate='" + reason.editDate + "', editauthor='" + reason.editAuthor + "' WHERE id='" + reason.id + "';");
				stmt.execute();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
		
		public class Status {
			/** Either banned or allowed on to the server */
			public boolean allowed;
			public Reason reason;
			
			private Status(boolean allowed, Reason reason){
				this.allowed = allowed;
				this.reason = reason;
			}
		}
		
		public static class Reason {
			int id;
			String playerUUID;
			Type type;
			String subject;
			String description;
			String authorUUID;
			String creationDate;
			String editDate;
			String editAuthor;
			
			public Reason(String playerUUID, Type type, String subject, String description, String authorUUID, String creationDate, String editDate, String editAuthor){
				this(0, playerUUID, type, subject, description, authorUUID, creationDate, editDate, editAuthor);
			}
			
			public Reason(int id, String playerUUID, Type type, String subject, String description, String authorUUID, String creationDate, String editDate, String editAuthor){
				this.id = id;
				this.playerUUID = playerUUID;
				this.type = type;
				this.subject = subject;
				this.description = description;
				this.authorUUID = authorUUID;
				this.creationDate = creationDate;
				this.editDate = editDate;
				this.editAuthor = editAuthor;
			}
			
			public enum Type {
				BAN, KICK, TEMPBAN;
			}
			
		}
	}
	
}
