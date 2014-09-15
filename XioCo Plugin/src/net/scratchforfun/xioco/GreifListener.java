package net.scratchforfun.xioco;

import net.scratchforfun.xioco.threads.GodThread;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class GreifListener implements Listener{
	
	@EventHandler
	public void onExitVehicle(VehicleExitEvent e){
		System.out.println("EXIT");
		if(e.getExited() instanceof Player){
			System.out.println("PLAYER");
			if(e.getVehicle() instanceof Boat || e.getVehicle() instanceof RideableMinecart){
				System.out.println("REMOVED");
				e.getVehicle().remove();
			}
		}		
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player player = (Player) e.getEntity();

			if(GodThread.inGod(player)){
				e.setCancelled(true);
				
				if(player.getFireTicks() > 0){
					player.setFireTicks(0);
					/*final Player schedPlayer = player;
					Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
					  public void run() {
					    schedPlayer.setFireTicks(0);
					    System.out.println("Scheduler running to remove fire from" + schedPlayer.getName());
					  }
					}, 1L);*/
				}
			}
		}	
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e){
		ItemStack item = e.getItemDrop().getItemStack();
		if(item.equals(WorkMode.ADMINSTICK)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.TELEPORTER)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.WOODENAXE)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.TOOL)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.WATER)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.LAVA)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.FIRE)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.BEDROCK)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onIgnite(BlockIgniteEvent e){
		if(e.getPlayer() != null && !PermissionsEx.getUser(e.getPlayer()).has("xioco.flint")){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e){
		ItemStack item = e.getItem().getItemStack();
		if(item.equals(WorkMode.ADMINSTICK)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.TELEPORTER)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.WOODENAXE)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.TOOL)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.WATER)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.LAVA)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.FIRE)){
			e.setCancelled(true);
		}else if(item.equals(WorkMode.BEDROCK)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent e){
		PermissionUser user = PermissionsEx.getUser(e.getPlayer());
		
		if(e.getBucket().equals(Material.LAVA_BUCKET)){
			if(!user.has("xioco.lava")){
				e.getPlayer().sendMessage(ChatColor.RED + "Det er ikke tillatt å plassere lava. Bruk /helpop for å kontakte stab.");
				e.setCancelled(true);
			}
		}else if(e.getBucket().equals(Material.WATER_BUCKET)){
			if(!user.has("xioco.water")){
				e.getPlayer().sendMessage(ChatColor.RED + "Det er ikke tillatt å plassere vann. Bruk /helpop for å kontakte stab.");
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e){
		e.setCancelled(true);
	}
	
	//Unable to cancel the hit event :/
	@EventHandler
	public void onFishhookHitItemFrame(ProjectileHitEvent e){
		//System.out.println(e.);
		
		/*if(e.getEntityType().getName().equals("PrimedTnt")){
			TNTPrimed tnt = (TNTPrimed) e.getEntity();
			
			if(tnt.getSource() instanceof Player){
				((Player)tnt.getSource()).sendMessage(ChatColor.RED + "Det er ikke tillatt å sprenge TNT.");
			}
			
			e.setCancelled(true);
		}*/
	}
	
	@EventHandler
	public void onTNTExplode(ExplosionPrimeEvent e){
		if(e.getEntityType().getName().equals("PrimedTnt")){
			TNTPrimed tnt = (TNTPrimed) e.getEntity();
			
			if(tnt.getSource() instanceof Player){
				((Player)tnt.getSource()).sendMessage(ChatColor.RED + "Det er ikke tillatt å sprenge TNT.");
			}
			
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onCreeperExplode(EntityExplodeEvent e){
		if(e.getEntityType().getName().equals("Creeper")){
			e.blockList().clear();
		}
	}
	
}
