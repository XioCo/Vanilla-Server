package net.scratchforfun.xioco.threads;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class GodThread implements Runnable{

	private Thread thread = new Thread(this);

	private static List<GodPlayer> players = new ArrayList<GodPlayer>();
	
	public GodThread(){
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	
	public void stop(){
		thread.stop();
	}

	public void run() {
		while(true){
			List<GodPlayer> removablePlayers = new ArrayList<GodPlayer>();
			for(GodPlayer player : GodThread.players){
				if(player.getTime()<=0){
					removablePlayers.add(player);
				}
			}
			
			for(GodPlayer removable : removablePlayers){
				remove(removable);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static GodPlayer getGod(Player player){
		for(GodPlayer god : GodThread.players){
			if(god.player_UUID.equals(player.getUniqueId().toString())){
				return god;
			}
		}
		
		return null;
	}
	
	public static boolean inGod(Player player){
		return getGod(player) != null;
	}
	
	public static GodPlayer addGod(Player player, int seconds){
		GodPlayer god = new GodPlayer(player, seconds);
		GodThread.players.add(god);
		
		return god;
	}
	
	public static void remove(GodPlayer player){
		GodThread.players.remove(player);	
	}
	
	public static class GodPlayer {
		private String player_UUID;
		private long time;
		
		public GodPlayer(Player player, int seconds){
			this.player_UUID = player.getUniqueId().toString();
			this.time = System.currentTimeMillis()+seconds*1000;
		}
		
		public int getTime(){
			return (int) ((time-System.currentTimeMillis())/1000);
		}
	}
	
}
