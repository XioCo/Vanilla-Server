package net.scratchforfun.xioco;

import java.util.ArrayList;
import java.util.List;

import net.scratchforfun.debugg.Debug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Registration implements Listener {

	/*public static List<RegStatus> registratingPlayers = new ArrayList<RegStatus>();
	
	public Registration(String UUID){
		boolean exists = false;
		
		Debug.s_print(ChatColor.GREEN+"RegistrationEvent");
		
		for(RegStatus status : Registration.registratingPlayers){
			if(status.UUID.equals(UUID)) exists = true;
		}
		
		if(!exists) Registration.registratingPlayers.add(new RegStatus(UUID));
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		/*for(RegStatus status : registratingPlayers){
			if(status.UUID);
		}*
		
		Debug debug = Debug.start();
		Registration.registratingPlayers.remove(e.getPlayer().getUniqueId().toString());
		debug.print(ChatColor.GREEN+"RegistrationLeaveEvent: ");
	}
	
	public static RegStatus getRegistrator(String UUID){
		for(RegStatus status : Registration.registratingPlayers){
			if(status.UUID.equals(UUID)) return status;
		}
		
		return null;
	}
	
	public static void updateRegistration(Player player, RegStatus status){
		for(int i = 0; i < Registration.registratingPlayers.size(); i++){
			if(Registration.registratingPlayers.get(i).UUID.equals(status.UUID)){
				if(status.correct == 10){
					player.sendMessage("Du er nå registrert. Kos deg på vår server!");
					//player.setSaturation(100);
					player.setFoodLevel(20);
					
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pex user " + player.getName() + " group set bruker");
					Registration.remove(status);
				}else{
					Registration.registratingPlayers.remove(i);
					Registration.registratingPlayers.add(status);
					
					status.setRandomQuestion();
					
					XioCo.askQuestion(player, Configs.getValues(Configs.greylistQuestionsFile, status.question+"")[1]);
				}
			}
		}
	}
	
	public static void remove(RegStatus status){
		for(int i = 0; i < Registration.registratingPlayers.size(); i++){
			if(Registration.registratingPlayers.get(i).UUID.equals(status.UUID)){
				Registration.registratingPlayers.remove(i);
			}
		}
	}*/
	
}
