package net.scratchforfun.xioco.threads;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ThreadTPA implements Runnable{

	private Thread thread = new Thread(this);
	
	public ThreadTPA(){
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	
	public void stop(){
		thread.stop();
	}
	
	private static List<PlayerTPA> players = new ArrayList<PlayerTPA>();
	
	public void run() {
		while(true){
			List<PlayerTPA> removablePlayers = new ArrayList<PlayerTPA>();
			for(PlayerTPA player : ThreadTPA.players){
				if(player.getTime()<0){
					player.requester.sendMessage(ChatColor.RED + player.accepter.getDisplayName() + " aksepterte ikke i tide.");
					player.accepter.sendMessage(ChatColor.RED + "Du svarte ikke på tpa i tide.");
					
					removablePlayers.add(player);
				}
			}
			
			for(PlayerTPA removable : removablePlayers){
				remove(removable);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
		
	public static void remove(PlayerTPA player){
		ThreadTPA.players.remove(player);	
		GodThread.addGod(player.requester, 5);
	}
	
	public static boolean hasTPARequest(Player player){
		return getTPARequest(player) != null;
	}
	
	public static PlayerTPA getTPARequest(Player player){
		for(PlayerTPA tpa : ThreadTPA.players){
			if(tpa.accepter.getUniqueId().toString().equals(player.getUniqueId().toString())){
				return tpa;
			}
		}
		
		return null;
	}
	
	public static void addTPAPlayer(Player requester, Player accepter){
		ThreadTPA.players.add(new PlayerTPA(requester, accepter));
	}
	
	public static int getSize(){
		return ThreadTPA.players.size();
	}
	
	public static class PlayerTPA {
		public Player requester;
		public Player accepter;
		public long time;
		
		private PlayerTPA(Player requester, Player accepter){
			this.requester = requester;
			this.accepter = accepter;
			this.time = System.currentTimeMillis();
		}
		
		public int getTime(){
			return (int) (60-(System.currentTimeMillis()-time)/1000);
		}
	}
	
}
