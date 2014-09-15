package net.scratchforfun.xioco;

import java.util.List;

import net.scratchforfun.debugg.Debug;
import net.scratchforfun.xioco.permission.Permission;
import net.scratchforfun.xioco.permission.PermissionPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Dropper;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class BlockListener implements Listener{
	
	/*@EventHandler
	public void onTreeGrow(StructureGrowEvent e){
		Location location = e.getLocation();
		Block block = null;
		String UUID = null;
		
		for(BlockState blockS : e.getBlocks()){
			if(blockS.getX() == location.getBlockX()){
				if(blockS.getY() == location.getBlockY()){
					if(blockS.getZ() == location.getBlockZ()){
						block = blockS.getBlock();
					}
				}
			}
		}
		
		if(block != null){
			UUID = XioCo.instance.blockProtection.isBlockProtected("TREE", block);
			if(UUID != null) XioCo.instance.blockProtection.updateProtection(UUID, false, block);
		}
	}*/	
	
	@EventHandler(ignoreCancelled = true)
	public void onShopRightClick(PlayerInteractEvent e){
		Debug debug = Debug.start();
		if(!XioCo.isGuest(e.getPlayer())){
			e.getPlayer().sendMessage(ChatColor.RED + "Du må registrere deg for å kunne gjøre dette. Skriv /reg for å registrere");
			e.setCancelled(true);
		} else { // TODO / HOTFIX: Guest should not be allowed to shop.
			if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && (e.getClickedBlock().getType().equals(Material.SIGN_POST) || e.getClickedBlock().getType().equals(Material.WALL_SIGN))){
				ItemStack itemInHand = e.getPlayer().getItemInHand();
				if(itemInHand != null && itemInHand.equals(WorkMode.MIDLERTIDIG)){
					if(e.getClickedBlock().getState() instanceof Sign){
						Sign sign = (Sign) e.getClickedBlock().getState();
						String sellingMaterial = Configs.getValue(Configs.chestShopFile, sign.getLine(2));
						if(sellingMaterial != null){
							sign.setLine(2, sellingMaterial);
							sign.update();
						}
					}
				}else clickSign(e.getPlayer(), e.getClickedBlock());
			}
		}
		debug.print(ChatColor.YELLOW+"PlayerInteractEvent: ");
	}
	
	public void clickSign(Player player, Block blck){
		Debug debug = Debug.start();
		
		if(player != null){
			Sign sign = (Sign) blck.getState();
			if(isShopSign(player, sign)){
				int pris;
				int antall;
				
				try{
					String cost = "";
					
					for(int i = 0; i < sign.getLine(1).length(); i++){
						if(String.valueOf(sign.getLine(1).charAt(i)).equals("$")) break;
						cost += sign.getLine(1).charAt(i);
					}
					
					pris = Integer.parseInt(cost);
					antall = Integer.parseInt(sign.getLine(1).split(" ")[2]);
				}catch(Exception ex){
					ex.printStackTrace();
					return;
				}
				
				//
				String sellerUUID = BlockProtection.isBlockProtected(sign.getBlock());
				
				PlayerInfo buyer = PlayerInfo.getPlayerInfo(player);
				
				String sellingMaterial = Configs.getOppositeValue(Configs.chestShopFile, sign.getLine(2));		
				if(sellingMaterial.equals("")) sellingMaterial = sign.getLine(2);
				
				String[] splitMaterial = sellingMaterial.split("\\|");
				
				if(splitMaterial.length >= 2){
					Material material = Material.getMaterial(splitMaterial[0]);
					ItemStack itemstack;
				    itemstack = new ItemStack(material, antall, (short) 0, (byte) Integer.parseInt(splitMaterial[1]));
					
					Block block = sign.getLocation().add(0, -1, 0).getBlock();
					if(block != null){
						Chest chest = (Chest) block.getState();
						if(chest.getBlockInventory().contains(material, antall)){
							if(buyer.gull >= pris){
								buyer.gull -= pris;
								
								int i = antall;
								
								if(!sign.getLine(0).equals(ChatColor.DARK_GREEN+"Admin-Butikk")){
									Inventory inventory = chest.getBlockInventory();
									for(int j = 0; j < inventory.getSize(); j++){
										if(inventory.getItem(j) != null){
											if(inventory.getItem(j).getAmount() > i){
												ItemStack slot = inventory.getItem(j);
												slot.setAmount(inventory.getItem(j).getAmount()-i);
												inventory.setItem(j, slot);
												i = 0;
												break;
											}else if(inventory.getItem(j).getAmount() == i){
												inventory.setItem(j, new ItemStack(Material.AIR));
												i = 0;
												break;
											}else if(inventory.getItem(j).getAmount() < i){
												int k = inventory.getItem(j).getAmount();
												inventory.setItem(j, new ItemStack(Material.AIR));
												i -= k;
											}
										}
									}
								}
								
								buyer.writePlayerInfo();
								
								if(!sign.getLine(0).equals(ChatColor.DARK_GREEN+"Admin-Butikk")){
									PlayerInfo seller = PlayerInfo.getPlayerInfo(sellerUUID);
									seller.gull+=pris;
									seller.writePlayerInfo();
									
									for(Player online : Bukkit.getOnlinePlayers()){
										if(online.getUniqueId().toString().equals(sellerUUID)){
											online.sendMessage(ChatColor.GREEN + "Du har nettopp solgt " + antall + " " + sign.getLine(2) + " til " + player.getName() + " - " + pris + " gull i fortjeneste!");
										}
									}
								}
								
								player.getInventory().addItem(itemstack);
								player.updateInventory();
								player.sendMessage(ChatColor.GREEN + "Du har kjøpt " + antall + " " + sign.getLine(2) + " for " + pris + " gull.");
							}else{
								player.sendMessage(ChatColor.RED + "Du har ikke nok gull på konto.");
							}
						}else{
							player.sendMessage(ChatColor.RED + "Det er dessverre utsolgt.");
						}
					}
				}
			}else if(sign.getLine(0).equals(ChatColor.DARK_PURPLE + "*************") && sign.getLine(3).equals(ChatColor.DARK_PURPLE + "*************")){
				String[] coordinates = sign.getLine(2).split(" ");
				if(coordinates.length == 3){
					try{
						int x = Integer.parseInt(coordinates[0]);
						int y = Integer.parseInt(coordinates[1]);
						int z = Integer.parseInt(coordinates[2]);
						
						player.teleport(new Location(player.getWorld(), x, y, z));
						player.sendMessage(ChatColor.DARK_PURPLE + "Warping...");
					}catch(Exception ex){
						
					}
				}
			}
		}
		
		debug.print(ChatColor.DARK_AQUA+"ShopSignClick: ");
	}
	
	public boolean isWarpSign(Player player, Sign sign){
		if(sign.getLine(0).equals(ChatColor.DARK_PURPLE + "*************") && sign.getLine(3).equals(ChatColor.DARK_PURPLE + "*************")){
			String[] coordinates = sign.getLine(2).split(" ");
			if(coordinates.length == 3){
				try{
					int x = Integer.parseInt(coordinates[0]);
					int y = Integer.parseInt(coordinates[1]);
					int z = Integer.parseInt(coordinates[2]);
					
					return true;
				}catch(Exception ex){
					
				}
			}
		}
		
		return false;
	}
	
	public boolean isWarpSign(Player player, Block sign){
		if(sign.getType().equals(Material.SIGN_POST) || sign.getType().equals(Material.WALL_SIGN)) return isWarpSign(player, (Sign) sign.getState());
		
		return false;
	}
	
	public boolean isShopSign(Player player, Sign sign){
		Block block = sign.getLocation().add(0, -1, 0).getBlock();
		if(block != null){
			String UUID_bellow = BlockProtection.isBlockProtected(block);
			String UUID_ = BlockProtection.isBlockProtected(sign.getBlock());
			if(block.getType().equals(Material.CHEST) && UUID_bellow != null && UUID_bellow.equals(UUID_)){
				if(sign.getLine(0).equals(ChatColor.BLUE+"Kiste-Butikk")) return true;			
				if(sign.getLine(0).equals(ChatColor.DARK_GREEN+"Admin-Butikk") && sign.getLine(3).equals("XioCo")) return true;
			}
		}
		
		return false;
	}
	
	public boolean isShopSign(Player player, Block sign){
		if(sign.getType().equals(Material.SIGN_POST) || sign.getType().equals(Material.WALL_SIGN)) return isShopSign(player, (Sign) sign.getState());
		
		return false;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onCreateShop(SignChangeEvent e){
		PermissionPlayer perm = new PermissionPlayer(e.getPlayer());	
		if(e.getLine(0).equalsIgnoreCase("butikk")){
			Block block = e.getBlock().getLocation().add(0, -1, 0).getBlock();
			if(block != null && block.getType().equals(Material.CHEST)){
				String UUID = BlockProtection.isBlockProtected(block);
				if(UUID != null && UUID.equals(e.getPlayer().getUniqueId().toString())){
					Chest chest = (Chest) block.getState();
					
					ItemStack seldItem = null;
					
					for(ItemStack itemstack : chest.getBlockInventory()){
						if(itemstack != null){
							seldItem = itemstack;
							break;
						}
					}
					
					if(seldItem != null){
						int pris;
						int antall;
						try{
							pris = Integer.parseInt(e.getLine(1));
							antall = Integer.parseInt(e.getLine(2));
						}catch(Exception ex){
							return;
						}
						
						if(pris <= 0) return;
						if(antall <= 0) return;
						
						String sellingName = Configs.getValue(Configs.chestShopFile, seldItem.getType().toString() + "|" + seldItem.getData().getData());
						if(sellingName.equals("")) sellingName = seldItem.getType().toString() + "|" + seldItem.getData().getData();	
						
						if(e.getPlayer().getUniqueId().toString().equals(BlockProtection.isBlockProtected(e.getBlock()))){
							if(e.getLine(3).equalsIgnoreCase(XioCo.adminShopPassword) && perm.has(Permission.XIOCO_ADMINSHOP)){
								e.setLine(0, ChatColor.DARK_GREEN+"Admin-Butikk");
								e.setLine(1, pris + "$ per " + antall);
								e.setLine(2, sellingName);
								e.setLine(3, "XioCo");
							}else{
								e.setLine(0, ChatColor.BLUE+"Kiste-Butikk");
								e.setLine(1, pris + "$ per " + antall);
								e.setLine(2, sellingName);
								e.setLine(3, e.getPlayer().getName());
							}
						}
					}
				}
			}
		}else if(e.getLine(0).equalsIgnoreCase("warp")){
			String[] coordinates = e.getLine(1).split(" ");
			if(coordinates.length == 3){
				try{
					int x = Integer.parseInt(coordinates[0]);
					int y = Integer.parseInt(coordinates[1]);
					int z = Integer.parseInt(coordinates[2]);
					
					if(!e.getLine(2).isEmpty()){
						e.setLine(0, ChatColor.DARK_PURPLE + "*************");
						e.setLine(1, ChatColor.BLUE + e.getLine(2));
						e.setLine(2, x + " " + y + " " + z);
						e.setLine(3, ChatColor.DARK_PURPLE + "*************");
					}
				}catch(Exception ex){
					
				}
			}
		}
	}
	
	// REWAMPED
	@EventHandler(ignoreCancelled = true)
	public void onDispenseItem(BlockDispenseEvent e){
		// Checks if the dispensed item is a banned item
		if(e.getItem().getType().equals(Material.LAVA_BUCKET) || e.getItem().getType().equals(Material.WATER_BUCKET) || e.getItem().getType().equals(Material.FLINT_AND_STEEL) || e.getItem().getType().equals(Material.FIREBALL)){
			// Cancels it
			e.setCancelled(true);
		}
	}
	
	// REWAMPED
	@EventHandler(ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent e){
		// Starts the debug timer
		Debug debug = Debug.start();
		
		// Grabs the desired player
		Player player = (Player) e.getPlayer();
		
		// Grabs the desired permission profile from desired player
		PermissionPlayer perm = new PermissionPlayer(player);	
		
		// Is this the desired object (Chest, DoubleChest, Furnace, Hopper, BrewingStand)
		if (e.getInventory().getHolder() instanceof Chest || e.getInventory().getHolder() instanceof DoubleChest || e.getInventory().getHolder() instanceof Furnace || e.getInventory().getHolder() instanceof Hopper || e.getInventory().getHolder() instanceof BrewingStand /*|| e.getInventory().getHolder() instanceof HopperMinecart || e.getInventory().getHolder() instanceof StorageMinecart || e.getInventory().getHolder() instanceof PoweredMinecart*/) {
			// The players UUID
			String UUID;
			
			// Checks for double chest
			if(e.getInventory().getHolder() instanceof DoubleChest){
				// If this is a double chest, it will not allow BlockState cast
				Location l = ((DoubleChest) e.getInventory().getHolder()).getLocation();
				UUID = BlockProtection.isBlockProtected(l);
			}// Checks for storage minecart
			else if(e.getInventory().getHolder() instanceof StorageMinecart){
				// If this is a double chest, it will not allow BlockState cast
				Location l = ((StorageMinecart) e.getInventory().getHolder()).getLocation();
				UUID = BlockProtection.isBlockProtected(l);
			}// Checks for storage minecart
			else if(e.getInventory().getHolder() instanceof HopperMinecart){
				// If this is a double chest, it will not allow BlockState cast
				Location l = ((HopperMinecart) e.getInventory().getHolder()).getLocation();
				UUID = BlockProtection.isBlockProtected(l);
			}// Checks for storage minecart
			else if(e.getInventory().getHolder() instanceof PoweredMinecart){
				// If this is a double chest, it will not allow BlockState cast
				Location l = ((PoweredMinecart) e.getInventory().getHolder()).getLocation();
				UUID = BlockProtection.isBlockProtected(l);
			}else{
				// Use blockstate casting if this is not a double chest
				Block b = ((BlockState) e.getInventory().getHolder()).getBlock();
				UUID = BlockProtection.isBlockProtected(b);
			}
			
			
			// If you are not the player, then disable the chest opening call
			 
			// If player is not allowed to break
			if(!perm.hasGroupPermission(UUID)) 
				// If you have the XIOCO_OPENCHEST permission you should not cancel
				if(!perm.has(Permission.XIOCO_OPENCHEST)) 
					// Cancel the event
					e.setCancelled(true);
		}
		
		debug.print(ChatColor.YELLOW + "InventoryInteractEvent: ");
		
		//Unused
		/*else if (e.getInventory().getHolder() instanceof DoubleChest) {
			Location l = ((DoubleChest) e.getInventory().getHolder()).getLocation();
			String UUID = XioCo.instance.blockProtection.isBlockProtected(player.getName(), l);
			
			//If you are not the player, then disable the chest opening call
			if(UUID != null && !player.getUniqueId().toString().equals(XioCo.instance.blockProtection.isBlockProtected(player.getName(), l))){
				if(!XioCo.instance.group.inSameGroup(player.getUniqueId().toString(), UUID)) if(!perm.has(Permission.XIOCO_OPENCHEST)) 
					e.setCancelled(true);
			}
			debug.print(ChatColor.YELLOW + "InventoryInteractEvent Works!: ");
		}else{
			debug.print(ChatColor.YELLOW + "Return of InventoryInteractEvent: ");
		}*/
	}
	/* OLD (DO NOT USE TARGET BLOCK!)
		}
		if(e.getInventory().getName().equals("container.chest") || e.getInventory().getName().equals("container.chestDouble") || e.getInventory().getName().equals("container.furnace")){
			Block b = player.getTargetBlock(null, 5);
			
			String UUID = XioCo.instance.blockProtection.isBlockProtected(player.getName(), b);
			
			//If you are not the player, then disable the chest opening call
			if(UUID != null && !player.getUniqueId().toString().equals(XioCo.instance.blockProtection.isBlockProtected(player.getName(), b))){
				if(!XioCo.instance.group.inSameGroup(player.getUniqueId().toString(), UUID)) if(!PermissionsEx.getUser((Player) player).has("xioco.openchest")) e.setCancelled(true);
			}
		}
		debug.print(ChatColor.YELLOW + "InventoryInteractEvent: ");
	}
	
	*/
	/*	@EventHandler()
	public void onInvetoryClick(InventoryClickEvent e){
		Debug debug = Debug.start();
		Player player = (Player) e.getWhoClicked();
		if(e.getInventory().getName().equals("container.chest") || e.getInventory().getName().equals("container.chestDouble") || e.getInventory().getName().equals("container.furnace")){
			Block b = player.getTargetBlock(null, 5);
			
			String UUID = XioCo.instance.blockProtection.isBlockProtected(player.getName(), b);
			
			//If you are not the player, then disable the chest opening call
			if(UUID != null && !player.getUniqueId().toString().equals(UUID)){
				if(!XioCo.instance.group.inSameGroup(player.getUniqueId().toString(), UUID)) if(!PermissionsEx.getUser((Player) player).has("xioco.openchest")) e.setCancelled(true);
			}
		}
		debug.print(ChatColor.YELLOW + "InventoryClickEvent: ");
	}
	*/
	
	// REWAMPED
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		// Calls the place method, which is used by hanging event as well
		e.setCancelled(onBlockPlace(e.getPlayer(), e.getBlockAgainst(), e.getBlock()));
	}
	
	
	/**
	 * @param player
	 * @param blockAgainst
	 * @param block
	 * @return True if cancelled
	 */
	public boolean onBlockPlace(Player player, Block blockAgainst, Block block){
		// Starts the debug timer
		Debug debug = Debug.start();
		
		// Grabs the desired permission profile from desired player
		PermissionPlayer perm = new PermissionPlayer(player);	
		
		// If you are not a guest (Guest does not have the xioco_guest permission)
		if(perm.has(Permission.XIOCO_GUEST)){
			
			// And this is not a shop sign, nor warp sign
			if(!isShopSign(player, blockAgainst) && !isWarpSign(player, blockAgainst)){
				
				// Grabs the material instance
				Material material = block.getType();
				
				// If you are placing a chest
				if(material.equals(Material.CHEST) || material.equals(Material.TRAPPED_CHEST)){
					
					// Gets block x+1
					boolean blockPx = isProtectedDoubleChest(player, material, block.getRelative(1, 0, 0));

					// Gets block x-1
					boolean blockMx = isProtectedDoubleChest(player, material, block.getRelative(-1, 0, 0));

					// Gets block z+1
					boolean blockPz = isProtectedDoubleChest(player, material, block.getRelative(0, 0, 1));

					// Gets block z-1
					boolean blockMz = isProtectedDoubleChest(player, material, block.getRelative(0, 0, -1));
					
					// Check if there are any protected double chests
					if(blockPx || blockMx || blockPz || blockMz){
						// Cancels the event
						return true;
					}
				}
				
				// TODO / HOTFIX: Removed anti-hopper method, it is now redundant
				// TODO FIX THIS METHOD!
				
				// Makes sure you don't have the adminstick equiped
				if(!player.getItemInHand().equals(WorkMode.ADMINSTICK))						
					// Updates the sql protection
					BlockProtection.updateProtection(player, true, block);
			}else{	
				// Do not place sign if this is a shop sign or warp sign
				// Cancels the event
				return true;
			}
		}else{
			// You need to be a user to do this
			player.sendMessage(ChatColor.RED + "Du må registrere deg for å kunne gjøre dette. Skriv /reg for å registrere");
			
			// Cancels the event
			return true;
		}
		
		// Prints out debug result
		debug.print(ChatColor.YELLOW+"BlockPlaceEvent: ");
		
		// The event is not cancelled
		return false;
	}
	
	// REWAMPED
	// Checks if this is protected double chest or not
	public boolean isProtectedDoubleChest(Player player, Material material, Block block){
		// Grabs the protected UUID
		String UUID = BlockProtection.isBlockProtected(block);
		
		// This is protected
		if(UUID != null){
			// If this is a chest
			if(block.getType().equals(material)){
				// This is a double chest
				
				// If you are the one that own the other chest then this does not really mater
				// Then return false, this block is allowed to be placed
				return !UUID.equals(player.getUniqueId().toString());
			}else{
				// This is not a double chest
				return false;
			}
		}
		
		// This is not protected
		return false;
	}
	
	// REWAMPED
	@EventHandler(ignoreCancelled = true)
	public void onHangingItemRemove(EntityDamageByEntityEvent e){
		// Checks if this is an item frame
		if(e.getEntity() instanceof Hanging){
			//Grabs the Hanging instance
			Hanging hanging = (Hanging) e.getEntity();
			
			// Makes sure its the player destroying the item frame
			if(e.getDamager() instanceof Player){
				// Grabs the desired player
				Player player = (Player) e.getDamager();
				
				// Grabs the desired permission profile from desired player
				PermissionPlayer perm = new PermissionPlayer(player);	
				
				// Checks if the player has permission / is in correct group
				if(perm.hasGroupPermission(BlockProtection.isBlockProtected(hanging.getLocation()))){
					// You have the permission
					// Do nothing
				}else{
					// You do not have permission
					e.setCancelled(true);
				}
			}
		}
	}
	
	// REWAMPED
	@EventHandler(ignoreCancelled = true)
	public void onHangingBreak(HangingBreakByEntityEvent e){
		// Makes sure the remover is a player
		if(e.getRemover() instanceof Player){
			// Grabs the player instance
			Player player = (Player) e.getRemover();
			
			// If the player has the adminstick in his hand, then the rest is useless
			if(!player.getItemInHand().equals(WorkMode.ADMINSTICK)){
				// Gets the player permission profile
				PermissionPlayer perm = new PermissionPlayer(player);	
				
				// Makes sure the player is not a guest
				if(perm.has(Permission.XIOCO_GUEST)){
					// Checks if the player has permission / is in correct group
					if(perm.hasGroupPermission(BlockProtection.isBlockProtected(e.getEntity().getLocation()))){
						// You have the permission
						// Do nothing
					}else{
						// You do not have permission
						player.sendMessage(ChatColor.RED + "Du eier ikke denne blokken.");
						// Cancel
						e.setCancelled(true);
					}
				}else{
					// Lets the guest know he needs to register to do this
					player.sendMessage(ChatColor.RED + "Du må registrere deg for å kunne gjøre dette. Skriv /reg for å registrere");
					
					// Cancels the event
					e.setCancelled(true);
				}
			}else{
				// Do nothing if the player has the adminstick in hand
				// Permission granted
			}
		}else{
			// Cancels the event if the remover is not a player
			e.setCancelled(true);
		}
	}
	
	// REWAMPED
	@EventHandler(ignoreCancelled = true)
	public void onHangingPlace(HangingPlaceEvent e){
		// Grabs the player instance
		Player player = e.getPlayer();
		
		// Gets the player permission profile
		PermissionPlayer perm = new PermissionPlayer(player);	
		
		// Makes sure the player is not a guest
		if(perm.has(Permission.XIOCO_GUEST)){
			// Grabs the location of the block placed against
			Location location = e.getBlock().getLocation();
			
			// Adds the relative position of the hanging entity
			location.add(e.getBlockFace().getModX(), e.getBlockFace().getModY(), e.getBlockFace().getModZ());
			
			// Calls the main place method
			e.setCancelled(onBlockPlace(player, e.getBlock(), location.getBlock()));
		}else{
			// Lets the guest know he needs to register to do this
			player.sendMessage(ChatColor.RED + "Du må registrere deg for å kunne gjøre dette. Skriv /reg for å registrere");
			
			// Cancels the event
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onIceMelt(BlockFadeEvent e){
		// Checks if it is the ice melting, and not anything else 
		if(e.getBlock().getType().equals(Material.ICE)){
			// Cancels the melting
			e.setCancelled(true);
		}
	}
	
	// REWAMPED
	@EventHandler(ignoreCancelled = true)
	public void onPortalCreate(PortalCreateEvent e){
		// Portals are not supposed to be created
		e.setCancelled(true);
		
		
		// This might turn into a problem later on
		// If so try tagging fires to get the player placing
	}
	
	// REWAMPED
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		// Starts the debug timer
		Debug debug = Debug.start();
		
		// Grabs the player instance
		Player player = e.getPlayer();
		
		// Grabs the desired permission profile from desired player
		PermissionPlayer perm = new PermissionPlayer(player);	
		
		// Makes sure you are not a guest
		if(perm.has(Permission.XIOCO_GUEST)){
			// Grabs the block in question
			Block block = e.getBlock();
			
			// Checks if you're clicking a door
			if(block.getType().equals(Material.WOODEN_DOOR) || block.getType().equals(Material.IRON_DOOR_BLOCK)){
				// Checks if this is the top of the door
				if(((Door)block.getState().getData()).isTopHalf()){
					// Grabs the top door location
					Location location = block.getLocation();
					
					// Moves down to the bottom door
					location.add(0, -1, 0);
					
					// Grabs the door
					block = location.getBlock();
					
					// Makes sure the door exists
					if(block != null){
						// Makes sure this is the bottom door
						if(!((Door)block.getState().getData()).isTopHalf()){
							
						}else{
							// Cancel the event
							block = null;
						}
					}
				}
			}			
			
			// Makes sure the block you are destroying exists
			if(block != null){
				// Grabs the protector's UUID
				String UUID = BlockProtection.isBlockProtected(block);
				
				// Makes sure you have the permission
				if(perm.hasGroupPermission(UUID)){
					// Makes sure you're not using the adminstick
					if(!player.getItemInHand().equals(WorkMode.ADMINSTICK))
						// Updates the protection
						BlockProtection.updateProtection(player, false, e.getBlock());
				}else{
					// Let's the player know you don't have the permission
					e.getPlayer().sendMessage(ChatColor.RED + "Du eier ikke denne blokken.");
					
					// Cancels the event
					e.setCancelled(true);
				}
			}else{
				// Cancels the event
				e.setCancelled(true);
			}
		}else{
			// Let's the player know it needs to register
			e.getPlayer().sendMessage(ChatColor.RED + "Du må registrere deg for å kunne gjøre dette. Skriv /reg for å registrere");
			
			// Cancels the event
			e.setCancelled(true);
		}
		
		// Prints out the debug result
		debug.print(ChatColor.YELLOW + "BlockBreakEvent: ");
	}
	
	// REWAMPED
	@EventHandler(ignoreCancelled = true)
    public void BlockPistonRetractEvent(BlockPistonRetractEvent e) {
		// Makes sure the piston is sticky
		if(e.isSticky()){
			// Grabs the location of the retracting block
			Location location = new Location(e.getBlock().getWorld(), e.getDirection().getModX()*2+e.getBlock().getX(), e.getDirection().getModY()*2+e.getBlock().getY(), e.getDirection().getModZ()*2+e.getBlock().getZ());
			
			// Grabs the block from the location
			Block block = location.getBlock();
			
			// Makes sure the block exists
			if(block != null){
				// Grabs the owner of the piston
				String pistonUUID = BlockProtection.isBlockProtected(e.getBlock());
				
				// Grabs the permission profile from the player
				PermissionPlayer.UUID player = new PermissionPlayer.UUID(pistonUUID);
				
				// If the player does not have permission to retract the block, then don't do it
				if(!player.hasGroupPermission(pistonUUID)) e.setCancelled(true);
			}
		}
	}
	
	// REWAMPED
	@EventHandler(ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent e) {
		// Grabs the owner of the piston
		String pistonUUID = BlockProtection.isBlockProtected(e.getBlock());
		
		// Makes sure the owner exists
		if(pistonUUID != null){
			// Grabs the permission profile from the player
			PermissionPlayer.UUID player = new PermissionPlayer.UUID(pistonUUID);
			
			// Grabs the blocks the piston is trying to move
			List<Block> blocks = e.getBlocks();
			
			// Makes sure all the blocks being moved are moved by the owner
			for(int i = 0; i < blocks.size(); i++){
				// If the player does not have permission, cancel the entire event
				if(!player.hasGroupPermission(BlockProtection.isBlockProtected(blocks.get(i)))) e.setCancelled(true);
			}
		}
	}
	
	// TODO / HOTFIX: Hoppers should not be allowed to zap items from other people's chests
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {

		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) {			
			return; // No block placement, not interested
		}
		
		if(!e.hasItem()) {
			return; // No placed item, not interested
		}
		
		// CAULDRON START
			// If the player clicks the cauldron
			if(e.getItem().getType().equals(Material.GLASS_BOTTLE) || e.getItem().getType().equals(Material.WATER_BUCKET)){
				// Grabs the player permission instance
				PermissionPlayer.UUID player = new PermissionPlayer.UUID(e.getPlayer().getUniqueId().toString());
				
				// Checks if the player has permission or not
				if(!player.hasGroupPermission(BlockProtection.isBlockProtected(e.getClickedBlock()))) e.setCancelled(true);
			}
		// CAULDRON END
		
		if(e.getItem().getType() != Material.HOPPER_MINECART &&
				e.getItem().getType() != Material.POWERED_MINECART &&
				e.getItem().getType() != Material.STORAGE_MINECART) {
			return; // Not a hopper-cart, not interested
		}
		
		if(e.getClickedBlock().getType() != Material.RAILS &&
				e.getClickedBlock().getType() != Material.POWERED_RAIL &&
				e.getClickedBlock().getType() != Material.DETECTOR_RAIL &&
				e.getClickedBlock().getType() != Material.ACTIVATOR_RAIL) {
			return; // Was not placed on a rail - hopper-cart will therefore not be placed anyway
		}
		
		Plugin plugin = Bukkit.getPluginManager().getPlugin("XioCo");
		
		// Okay, so this is a valid hopper-cart placement. Tag it with the owners name, by removing the "real" cart and replacing it with our own custom, named cart
		e.setCancelled(true);
		if(e.getItem().getType() == Material.HOPPER_MINECART) {
			HopperMinecart cart = (HopperMinecart) e.getPlayer().getWorld().spawnEntity(e.getClickedBlock().getLocation(), EntityType.MINECART_HOPPER);
			cart.setMetadata("cartOwnerUUID", new FixedMetadataValue(plugin, e.getPlayer().getUniqueId().toString()));
		} else if(e.getItem().getType() == Material.POWERED_MINECART) {
			PoweredMinecart cart = (PoweredMinecart) e.getPlayer().getWorld().spawnEntity(e.getClickedBlock().getLocation(), EntityType.MINECART_FURNACE);
			cart.setMetadata("cartOwnerUUID", new FixedMetadataValue(plugin, e.getPlayer().getUniqueId().toString()));
		} else if(e.getItem().getType() == Material.STORAGE_MINECART) {
			StorageMinecart cart = (StorageMinecart) e.getPlayer().getWorld().spawnEntity(e.getClickedBlock().getLocation(), EntityType.MINECART_CHEST);
			cart.setMetadata("cartOwnerUUID", new FixedMetadataValue(plugin, e.getPlayer().getUniqueId().toString()));
		}
	}
	
	@EventHandler
	public void onInventoryPickupItem(InventoryMoveItemEvent e) {
		String aID = null; // Placeholder for the UUID of the initiating block/entity
		
		Plugin plugin = Bukkit.getPluginManager().getPlugin("XioCo");
		
		if(e.getInitiator().getHolder() instanceof HopperMinecart) {
			HopperMinecart cart = (HopperMinecart) e.getInitiator().getHolder();
			if(!cart.hasMetadata("cartOwnerUUID")) {
				e.setCancelled(true); // No metadata found, don't allow hopping
				return;
			}
			
			List<MetadataValue> values = cart.getMetadata("cartOwnerUUID");
			
			MetadataValue value = null; // Placeholder for the value we are interested in
			
			for(MetadataValue m : values) { // Search for the value this plugin has set
				if(m.getOwningPlugin().getName().equalsIgnoreCase(plugin.getName())) {
					value = m; // Got it!
					break;
				}
			}
			
			if(value == null) {
				e.setCancelled(true); // Didn't find one from this plugin, don't allow hopping
				return;
			}
			
			aID = value.asString(); // UUID of hopper-cart owner
			
		} else if(e.getInitiator().getHolder() instanceof Hopper) {
			Hopper hopper = (Hopper) e.getInitiator().getHolder();
			aID = BlockProtection.isBlockProtected(hopper.getBlock());  // UUID of the hopper owner. First parameter seems to be redundant
		}
		
		if(aID == null) {
			e.setCancelled(true); // No player found responsible, don't allow hopping
			return;
		}
		
		Inventory inv = null; // Placeholder for the inventory that is not the hopper
		if(e.getDestination() == e.getInitiator()) { // We are zapping items
			inv = e.getSource();
		} else if(e.getSource() == e.getInitiator()) { // We are moving stuff into another block
			inv = e.getDestination();
		} else {
			return; // This is a situation where the initiator is a hopper, but it's not moving stuff within itself. Should not happen yet, but may in future versions of Minecraft
		}
		
		String bID = null; // Placeholder for UUID of target inventory
		
		Block b = null; // Placeholder for the block we are zapping or moving stuff to
		if(inv.getHolder() instanceof Chest) {
			Chest s = (Chest) inv.getHolder();
			b = s.getBlock();
			
		} else if(inv.getHolder() instanceof DoubleChest) {
			DoubleChest s = (DoubleChest) inv.getHolder();
			b = s.getLocation().getBlock();
			
		} else if(inv.getHolder() instanceof Furnace) {
			Furnace s = (Furnace) inv.getHolder();
			b = s.getBlock();
			
		} else if(inv.getHolder() instanceof Jukebox) {
			Jukebox s = (Jukebox) inv.getHolder();
			b = s.getBlock();
			
		} else if(inv.getHolder() instanceof Dropper) {
			Dropper s = (Dropper) inv.getHolder();
			b = s.getBlock();
			
		} else if(inv.getHolder() instanceof Dispenser) {
			Dispenser s = (Dispenser) inv.getHolder();
			b = s.getBlock();
			
		} else if(inv.getHolder() instanceof Hopper) {
			Hopper s = (Hopper) inv.getHolder();
			b = s.getBlock();
			
		} else if(inv.getHolder() instanceof BrewingStand) {
			BrewingStand s = (BrewingStand) inv.getHolder();
			b = s.getBlock();
		}
		
		if(b == null) {
			// Inventory holder may be a minecart
			if(!(inv.getHolder() instanceof HopperMinecart) &&
						!(inv.getHolder() instanceof PoweredMinecart) &&
						!(inv.getHolder() instanceof StorageMinecart)) {
				e.setCancelled(true); // Unidentified inventory holder or a non-storage minecart, don't allow hopping
				return;
			}
			
			// It's a storage-kind of cart. Get metadata
			Minecart cart = (Minecart) inv.getHolder();
			if(!cart.hasMetadata("cartOwnerUUID")) {
				e.setCancelled(true); // No metadata found, don't allow hopping
				return;
			}
			
			List<MetadataValue> values = cart.getMetadata("cartOwnerUUID");
			
			MetadataValue value = null; // Placeholder for the value we are interested in
			
			for(MetadataValue m : values) { // Search for the value this plugin has set
				if(m.getOwningPlugin().getName().equalsIgnoreCase(plugin.getName())) {
					value = m; // Got it!
					break;
				}
			}
			
			if(value == null) {
				e.setCancelled(true); // Didn't find one from this plugin, don't allow hopping
				return;
			}
			
			bID = value.asString(); // UUID of hopper-cart owner
		} else {
			bID = BlockProtection.isBlockProtected(b); // UUID of the target inventory. First parameter seems to be redundant
		}
		
		if(!matchUUID(aID, bID)) {
			e.setCancelled(true); // Ownership does not match, no hopping!
		}
	}
	
	// Convenience method for comparing UUIDs, also taking groups into account
	private boolean matchUUID(String UUID1, String UUID2) {
		if(UUID1 == null || UUID2 == null) {
			return false; // One or both blocks has no owner. This is not a case where we should allow changes
		}
		
		if(UUID1.equalsIgnoreCase(UUID2) || Group.inSameGroup(UUID1, UUID2)) {
			return true; // Same owner or same group
		} else {
			return false; // Not same owner and not same group
		}
	}
}
