package net.scratchforfun.debugg;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Debug {
	
	private long time;
	private static boolean debug = true;

	public static List<DebugPlayer> debug_players = new ArrayList<DebugPlayer>();
	
	public void print(String string){
		if(debug){
			for(Player player : Bukkit.getOnlinePlayers()){
				for(DebugPlayer debug : debug_players){
					if(player.getName().equalsIgnoreCase(debug.player)){
						long delay = (System.currentTimeMillis()-time);
						if(delay >= debug.cap) player.sendMessage(string + delay);
					}
				}
			}
		}
	}
	
	public static void s_print(String string){
		if(debug){
			for(Player player : Bukkit.getOnlinePlayers()){
				for(DebugPlayer debug : debug_players){
					if(player.getName().equalsIgnoreCase(debug.player)){
						player.sendMessage(string);
					}
				}
			}
		}
	}
	
	public static Debug start(){
		Debug debug = new Debug();
		debug.time = System.currentTimeMillis();
		
		return debug;
	}

	public static boolean update(String name) {
		boolean contains = false;
		int i = 0;
		for(i = 0; i < debug_players.size(); i++){
			if(debug_players.get(i).player.equalsIgnoreCase(name)){
				contains = true;
				break;
			}
		}
		
		if(contains){
			debug_players.remove(i);
			return false;
		}else{
			debug_players.add(new DebugPlayer(name));
			return true;
		}
	}
	
	public static boolean update(String name, int cap) {
		boolean contains = false;
		for(int i = 0; i < debug_players.size(); i++){
			DebugPlayer player = debug_players.get(i);
			if(player.player.equals(name)){
				player.cap = cap;
				debug_players.set(i, player);
				contains = true;
			}
		}
		
		return contains;
	}
	
}
