package net.scratchforfun.xioco.threads;

import net.scratchforfun.debugg.Debug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class GuestReminder implements Runnable{

	public Thread thread = new Thread(this);
	
	public void run(){
		while(true){			
			try {
				Thread.sleep(180000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Debug debug = Debug.start();
			for(Player player : Bukkit.getOnlinePlayers()){
				PermissionUser user = PermissionsEx.getUser(player);
				if(user.inGroup("gjest")){
					//player.setSaturation(10000);
					//player.setFoodLevel(100);
					player.sendMessage(ChatColor.GREEN + "Skriv /reg for å registrere deg.");
				}
			}
			debug.print(ChatColor.AQUA+"GuestReminder: ");
		}
	}
	
}
