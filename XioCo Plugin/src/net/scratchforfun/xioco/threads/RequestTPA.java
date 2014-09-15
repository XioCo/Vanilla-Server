package net.scratchforfun.xioco.threads;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Deprecated
public class RequestTPA implements Runnable{

	public Player requester;
	public Player accepter;
	public Thread thread = new Thread(this);

	@Deprecated
	public RequestTPA(Player requester, Player accepter){
		this.requester = requester;
		this.accepter = accepter;
		thread.start();
	}
	
	public long time = System.currentTimeMillis();
	public void run() {
		while(true){
			if(time+60*1000 < System.currentTimeMillis()){
				requester.sendMessage(ChatColor.RED + accepter.getDisplayName() + " aksepterte ikke i tide.");
				accepter.sendMessage(ChatColor.RED + "Du svarte ikke på tpa i tide.");
				//ThreadTPA.remove(accepter);
				thread.stop();
				return;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
