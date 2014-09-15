package net.scratchforfun.xioco;

import java.util.UUID;

import net.scratchforfun.debugg.Debug;
import net.scratchforfun.xioco.PlayerInfo.BannedPlayer;
import net.scratchforfun.xioco.clock.Clock;
import net.scratchforfun.xioco.clock.Clock.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class LoginListener implements Listener{

	public int maxJoins = 100;
	//public boolean loginNotification = false;
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		e.setDeathMessage(null);
	}
	
	@EventHandler
	public void onLoggon(PlayerJoinEvent e){
		Debug debug = Debug.start();
		//If can't find player, it creates the player
		PlayerInfo player = PlayerInfo.getPlayerInfoAndCreate(e.getPlayer());
		String joinMessage = ChatColor.GREEN+"+ "+ChatColor.GRAY+e.getPlayer().getDisplayName();
		e.setJoinMessage(null);
		
		if(!XioCo.DEBUG){
			PlayerInfo[] info = PlayerInfo.getPlayersInfo(Bukkit.getOnlinePlayers());
			for(PlayerInfo playerInfo : info){
				if(playerInfo.loginInfo.equalsIgnoreCase("true")) Bukkit.getPlayer(UUID.fromString(playerInfo.uuid)).sendMessage(joinMessage);
			}
		}else{
			e.setJoinMessage(joinMessage);
		}
		
		if(PermissionsEx.getUser(e.getPlayer()).inGroup("gjest")){
			//e.getPlayer().setSaturation(10000);
			//e.getPlayer().setFoodLevel(100);
			e.getPlayer().sendMessage(ChatColor.GREEN + "Skriv /reg for å registrere deg.");
		}
		
		if(player.inWork()){
			//e.getPlayer().setAllowFlight(true);
			//if(!e.getPlayer().isOnGround()) e.getPlayer().setFlying(true);
		}
		debug.print(ChatColor.RED + "PlayerJoinEvent: ");
	}
	
	@EventHandler
	public void onPreLoggon(AsyncPlayerPreLoginEvent e){
		Debug debug = Debug.start();
		if(Bukkit.getOnlinePlayers().length < maxJoins || PermissionsEx.getUser(e.getName()).has("xioco.vip")){
			BannedPlayer bannedPlayer = BannedPlayer.getBannedPlayer(e.getUniqueId().toString());
			BannedPlayer.Status status = bannedPlayer.getStatus();
			
			if(!status.allowed){
				String reason = status.reason.subject;
				String editDate = status.reason.editDate;
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
					
					if(!unbanned) reason+="\nDu er bannet til " + status.reason.editDate + "!";
				}			
				
				if(!unbanned) e.disallow(Result.KICK_OTHER, reason);
				else bannedPlayer.writeUnban(status.reason);
			}
		}else{
			e.disallow(Result.KICK_FULL, "Serveren er dessverre full\nPrøv igjen senere.");
		}
		debug.print(ChatColor.RED + "AsyncPlayerPreLoginEvent: ");
	}
	
	@EventHandler
	public void onSetMaxPlayers(ServerListPingEvent e){
		e.setMaxPlayers(maxJoins);
		//e.setMotd(motd);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		Debug debug = Debug.start();
		String quitMessage = ChatColor.RED+"- "+ChatColor.GRAY+e.getPlayer().getDisplayName();
		e.setQuitMessage(null);
		
		if(!XioCo.DEBUG){
			PlayerInfo[] info = PlayerInfo.getPlayersInfo(Bukkit.getOnlinePlayers());
			for(PlayerInfo playerInfo : info){
				if(playerInfo.loginInfo.equalsIgnoreCase("true")) Bukkit.getPlayer(UUID.fromString(playerInfo.uuid)).sendMessage(quitMessage);
			}
		}else{
			e.setQuitMessage(quitMessage);
		}
		
		debug.print(ChatColor.GOLD + "PlayerQuitEvent: ");
	}
	
	
	
}