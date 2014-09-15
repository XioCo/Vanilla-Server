package net.scratchforfun.xioco;

import java.util.UUID;

import net.scratchforfun.xioco.permission.Permission;
import net.scratchforfun.xioco.permission.PermissionPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AdminStick implements Listener{
	
	// REWAMPED
	@EventHandler
	public void onAdminStickEntityInteract(PlayerInteractEntityEvent e){
		// Grabs the player instance
		Player player = e.getPlayer();
		
		// Grabs the itemstack from the players hand
		ItemStack item = player.getItemInHand();
		
		// Makes sure you are actually holding an item
		if(item != null){
			
			// Makes sure this item is an itemstick
			if(item.equals(WorkMode.ADMINSTICK)){
				
				// Grabs the clicked entity
				Entity entity = e.getRightClicked();
				
				// Makes sure this entity is not a player
				if(!(e.getRightClicked() instanceof Player)){
					
					// Removes the entity
					e.getRightClicked().remove();
					
					// Let's the player know the entity was successfully removed
					player.sendMessage(ChatColor.GREEN + "Entity fjernet!");
				}else{
					
					// The adminstick can not be used on players
					player.sendMessage(ChatColor.RED + "Kan ikke brukes på spillere.");
				}
			}
		}
		
		/*
		if(player.getItemInHand() != null && player.getItemInHand().equals(WorkMode.ADMINSTICK) && !(e.getRightClicked() instanceof Player)){
			e.getRightClicked().remove();
		}*/
	}
	
	// REWAMPED
	@EventHandler
	public void onAdminStickInteract(PlayerInteractEvent e){
		// Grabs the item you are currently holding in your hand
		ItemStack item = e.getItem();
		
		// Makes sure you are actually holding something
		if(item != null){
			
			// TODO / HOTFIX: NullPointerException may occur if equals() is used in the same statement as the null check
			// TODO / MAGVAG: Really? That's more if you use || right? Or are there any difference between != and !equal? It should automatically stop if it sees anything false? I have used it many times before :/ It works beautifully
			
			// Makes sure the item is an adminstick
			if(e.getItem().equals(WorkMode.ADMINSTICK)){
				
				// Grabs the block instance
				Block block = e.getClickedBlock();
				
				// Makes sure the block interacted with is not equal to null
				if(block != null){
					
					// Grabs the player instance
					Player player = e.getPlayer();
					
					// Grabs the permission player profile
					PermissionPlayer perm = new PermissionPlayer(player);
					
					// Makes sure the player has XIOCO_PROTECT
					if(perm.has(Permission.XIOCO_PROTECT)){
						
						// Do something if right click
						if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
							
							// Grabs the protected block's log
							ProtectedBlock[] pBlocks = BlockProtection.getLoggProtectedBlock(block.getX(), block.getY(), block.getZ());	
							
							// Marks the new use of the adminstick 
							e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "==========================");
							
							// Loops through the protected blocks
							for(int i = pBlocks.length-1; i >= 0 ; i--){
								
								// Makes sure the protected block is not null
								if(pBlocks[i] != null){
									
									// The name instance of the protected block
									// By default ERROR --> If the player does no longer exist
									String name = "ERROR";
									
									// TODO THIS DOES NOT WORK !!! IT TAKES A NAME! NOT A UUID!
									//Player online = Bukkit.getServer().getPlayer(pBlocks[i].UUID);
									//if(online != null) name = online.getName();
									
									// Grabs the player the correct way
									Player online = Bukkit.getServer().getPlayer(UUID.fromString(pBlocks[i].UUID));
									
									// Makes sure the player exists
									if(online != null)
										// Sets the name instance
										name = online.getName();
									else{
										// If the player is not online
										OfflinePlayer offline = Bukkit.getServer().getOfflinePlayer(UUID.fromString(pBlocks[i].UUID));
										
										// Makes sure the player actually exists
										if(offline != null){
											// Grabs the players name
											name = offline.getName();
										}
									}
										
									
									// Grabs the desired color
									ChatColor color = pBlocks[i].plasert.equalsIgnoreCase("true")?ChatColor.GREEN:
										pBlocks[i].plasert.equalsIgnoreCase("false")?ChatColor.RED:ChatColor.AQUA;
									
									// Block log message
									String message = pBlocks[i].date + " " + pBlocks[i].clock + " " + name + " " + pBlocks[i].blockName + " == X:"+pBlocks[i].x+" Y:" + pBlocks[i].y + " Z:" + pBlocks[i].z;
									
									// Prints out the protected block in the chat
									player.sendMessage(color + message);
								
									
									// TODO MIGHT BE UNUSED!
									/*if(name == null){
										for(OfflinePlayer offline : Bukkit.getServer().getOfflinePlayers()){
											if(offline.getUniqueId().toString().equalsIgnoreCase(pBlocks[i].UUID)) name = offline.getName();
										}
									}*/
								}
							}
						}
						
						// Do something if left click
						if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
							// Updates the block protection
							BlockProtection.updateProtection(player, false, block);
							
							// Removes the block
							block.setType(Material.AIR);
						}
					}
				}
			}
		}
		
		//Makes sure it's actually the adminstick you are holding and not an ordinary stick
		/*if(e.getItem() != null){ 
			if(e.getItem().equals(WorkMode.ADMINSTICK)) { 
				Block block = e.getClickedBlock();
				if(block != null){
					PermissionPlayer perm = new PermissionPlayer(e.getPlayer());
					if(perm.has(Permission.XIOCO_PROTECT)){
						//When the player right clicks using the admin stick. Used to retrieve block place/break data
						if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
							ProtectedBlock[] pBlocks = BlockProtection.getLoggProtectedBlock(block.getX(), block.getY(), block.getZ());	
							
							e.getPlayer().sendMessage(ChatColor.DARK_AQUA + "==========================");
							for(int i = pBlocks.length-1; i >= 0 ; i--){
								if(pBlocks[i] != null){
									String name = null;
									
									Player online = Bukkit.getServer().getPlayer(pBlocks[i].UUID);
									if(online != null) name = online.getName();
									
									if(name == null){
										for(OfflinePlayer offline : Bukkit.getServer().getOfflinePlayers()){
											if(offline.getUniqueId().toString().equalsIgnoreCase(pBlocks[i].UUID)) name = offline.getName();
										}
									}
									
									e.getPlayer().sendMessage((pBlocks[i].plasert.equalsIgnoreCase("true")?ChatColor.GREEN:pBlocks[i].plasert.equalsIgnoreCase("false")?ChatColor.RED:ChatColor.AQUA) + pBlocks[i].date + " " + pBlocks[i].clock + " " + name + " " + pBlocks[i].blockName + " == X:"+pBlocks[i].x+" Y:" + pBlocks[i].y + " Z:" + pBlocks[i].z);
								}
							}
						}else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
							//TODO XioCo.instance.blockProtection.removeBlock("ADMINSTICK", e.getPlayer().getUniqueId().toString(), true, block);
							block.setType(Material.AIR);
						}
					}else{
						e.getPlayer().sendMessage(ChatColor.RED + "Du har ikke nødvendige rettigheter!");
					}
				}
			}
		}*/
	}
	
}
