package net.scratchforfun.xioco;

import net.scratchforfun.debugg.Debug;
import net.scratchforfun.xioco.clock.Clock;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BlockProtection{	

	public static String tableName = "xioco_blockprotection"; 
	public static String newTableName = "xioco_block"; 
	
	//Try indexing x, y, z
	//Test ORDER BY, use LIMIT 1
	//PreparedStatement
	private static PreparedStatement select_old;
	private static PreparedStatement insert_old;
	private static PreparedStatement insert_new;
	private static PreparedStatement select_new;
	private static PreparedStatement update_hole_new;
	private static PreparedStatement update_part_new;
	
	{
		try {
			BlockProtection.select_old = SQLConnection.connection.prepareStatement("SELECT * FROM `" + tableName + "` WHERE x=? AND y=? AND z=? ORDER BY id DESC LIMIT 20;");
			BlockProtection.insert_old = SQLConnection.connection.prepareStatement("INSERT INTO `" + tableName + "` values(?,?,?,?,?,?,?,?,?);");
			BlockProtection.insert_new = SQLConnection.connection.prepareStatement("INSERT INTO `" + newTableName + "` values(?,?,?,?,?,?,?);");
			BlockProtection.select_new = SQLConnection.connection.prepareStatement("SELECT * FROM `" + newTableName + "` WHERE x=? AND y=? AND z=? ORDER BY id DESC LIMIT 1;");
			BlockProtection.update_hole_new = SQLConnection.connection.prepareStatement("UPDATE `" + newTableName + "` SET uuid=?, plasert=?, block_name=? WHERE x=? AND y=? AND z=?;");
			BlockProtection.update_part_new = SQLConnection.connection.prepareStatement("UPDATE `" + newTableName + "` SET plasert=? WHERE x=? AND y=? AND z=?;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//Unused / unchanged
	public void grabTableName(File config){
		try{
			//Grabs the text from the file
			Scanner scanner = new Scanner(config);
			while(scanner.hasNext()){
				String text = scanner.nextLine();
				
				//Setup the tableName variable!
				if(text.startsWith("tableName")){
					tableName = text.split(":")[1];
				}else
				//Use # to makes comments in the config!
				if(!text.startsWith("#") && !text.isEmpty()){
					//Stuff can be done here if needed!		
				}
			}
			//Closes of the scanner
			scanner.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	//Gets the logg from the protected block
	public static ProtectedBlock[] getLoggProtectedBlock(int x, int y, int z){
		ProtectedBlock[] block = new ProtectedBlock[20];
		
		try{
			select_old.setInt(1, x);
			select_old.setInt(2, y);
			select_old.setInt(3, z);
			
			ResultSet result = select_old.executeQuery();
			for(int i = 0; result.next(); i++){	
				block[i] = new ProtectedBlock(result.getInt("id"), result.getString("uuid"), x, y, z, result.getString("date"), result.getString("clock"), result.getString("plasert"), result.getString("block_name"));
			}
			result.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return block;
	}
	
	//Gets the protected block
	public static ProtectedBlock getProtectedBlock(int x, int y, int z){
		ProtectedBlock block = null;
		
		try{
			select_new.setInt(1, x);
			select_new.setInt(2, y);
			select_new.setInt(3, z);
			
			ResultSet result = select_new.executeQuery();
			
			// If theres no blocks found in the database
			// Then it will automatically close the result set
			if(!result.isClosed()){
				while(result.next()){	
					block = new ProtectedBlock(result.getInt("id"), result.getString("uuid"), x, y, z, result.getString("plasert"));
				}
			}
			result.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return block;
	}
	
	public static String isBlockProtected(Block block){
		return isBlockProtected(block.getLocation());
	}
	
	//Checks if the block is protected
	public static String isBlockProtected(Location l){
		ProtectedBlock pBlock = getProtectedBlock(l.getBlockX(), l.getBlockY(), l.getBlockZ());
		
		if(pBlock != null){
			if(pBlock.plasert.equals("true")){
				if(pBlock.x == l.getBlockX() && pBlock.y == l.getBlockY() && pBlock.z == l.getBlockZ()) return pBlock.UUID;

				//If Furnace:
				if(pBlock.blockName.equals(Material.FURNACE.toString()) && l.getBlock().getType().equals(Material.BURNING_FURNACE)) return pBlock.UUID;
				if(pBlock.blockName.equals(Material.SIGN) && (l.getBlock().getType().equals(Material.SIGN_POST) || l.getBlock().getType().equals(Material.WALL_SIGN))) return pBlock.UUID;
			}else{
				return null;
			}
		}
			
		return null;
	}
	

	//updates the new sql database
	public static void updateProtection(Player player, boolean placed, Block block){
		updateProtection(player.getUniqueId().toString(), placed, block);
	}
	
	//updates the new sql database
	public static void updateProtection(String player, boolean placed, Block block) {
		if(!Blacklist.blocks.contains(block.getType())){
			Debug debug = Debug.start();
			SQLConnection.openConnection(false);
			
			try {	
				ProtectedBlock pBlock = getProtectedBlock(block.getX(), block.getY(), block.getZ());
				
				//AdminStick
				Clock clock = Clock.getClock();
				Clock.Date date = Clock.getDate();
				
				String s_date = date.day+"."+date.month+"."+date.year; //dd.mm.yyyy
				String s_clock = clock.hour+":"+clock.minute; //hh:mm
				
				
				if(pBlock == null){
					if(placed){
						insert_new.setInt(1, 0);
						insert_new.setString(2, player);
						insert_new.setInt(3, block.getX());
						insert_new.setInt(4, block.getY());
						insert_new.setInt(5, block.getZ());
						insert_new.setString(6, "true");
						insert_new.setString(7, block.getType().toString());
						insert_new.execute();

						insert_old.setInt(1, 0);
						insert_old.setString(2, player);
						insert_old.setInt(3, block.getX());
						insert_old.setInt(4, block.getY());
						insert_old.setInt(5, block.getZ());
						insert_old.setString(6, s_date);
						insert_old.setString(7, s_clock);
						insert_old.setString(8, "true");
						insert_old.setString(9, block.getType().toString());
						insert_old.execute();
					}else{
                        //Do nothing?
						//Just removing natural block
						insert_old.setInt(1, 0);
						insert_old.setString(2, player);
						insert_old.setInt(3, block.getX());
						insert_old.setInt(4, block.getY());
						insert_old.setInt(5, block.getZ());
						insert_old.setString(6, s_date);
						insert_old.setString(7, s_clock);
						insert_old.setString(8, "false");
						insert_old.setString(9, block.getType().toString());
						insert_old.execute();
					}
				}else{
					if(placed){
                        update_hole_new.setString(1, player);
						update_hole_new.setString(2, "true");
						update_hole_new.setString(3, block.getType().toString());
						update_hole_new.setInt(4, block.getX());
						update_hole_new.setInt(5, block.getY());
						update_hole_new.setInt(6, block.getZ());
						update_hole_new.execute();
						
						insert_old.setInt(1, 0);
						insert_old.setString(2, player);
						insert_old.setInt(3, block.getX());
						insert_old.setInt(4, block.getY());
						insert_old.setInt(5, block.getZ());
						insert_old.setString(6, s_date);
						insert_old.setString(7, s_clock);
						insert_old.setString(8, "true");
						insert_old.setString(9, block.getType().toString());
						insert_old.execute();
					}else{
                        update_part_new.setString(1, "false");
						update_part_new.setInt(2, block.getX());
						update_part_new.setInt(3, block.getY());
						update_part_new.setInt(4, block.getZ());
						update_part_new.execute();
						
						insert_old.setInt(1, 0);
						insert_old.setString(2, player);
						insert_old.setInt(3, block.getX());
						insert_old.setInt(4, block.getY());
						insert_old.setInt(5, block.getZ());
						insert_old.setString(6, s_date);
						insert_old.setString(7, s_clock);
						insert_old.setString(8, "false");
						insert_old.setString(9, block.getType().toString());
						insert_old.execute();
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			debug.print(ChatColor.DARK_PURPLE+"UpdateProtection: ");
		}
	}
	
	public static class Blacklist{
		public static List<Material> blocks = new ArrayList<Material>();
		
		public Blacklist(File blacklist){
			try {
				readBlacklist(blacklist);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		public List<Material> getBlacklist(){
			return Blacklist.blocks;
		}
		
		private void readBlacklist(File blacklist) throws FileNotFoundException{
			//Grabs the text from the file
			Scanner scanner = new Scanner(blacklist);
			while(scanner.hasNext()){
				String text = scanner.nextLine();
				
				//Use # to makes comments in the config!
				if(!text.startsWith("#") && !text.isEmpty()){
					//Converts the text into a material!
					Blacklist.blocks.add(Material.getMaterial(text));
				}
			}
			//Closes of the scanner
			scanner.close();
		}
	}
	
}
