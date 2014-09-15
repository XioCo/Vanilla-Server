package net.scratchforfun.xioco.permission;

import net.scratchforfun.xioco.Group;

import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionPlayer {

	PermissionUser player;
	
	public PermissionPlayer(Player player){
		this.player = PermissionsEx.getUser(player);
	}
	
	public boolean has(Permission permission){
		return player.has(permission.permission);
	}

	// REWAMPED
	public boolean hasGroupPermission(String protectedUUID){
		return hasGroupPermission(this.player.getPlayer().getUniqueId().toString(), protectedUUID);
	}
	
	// REWAMPED
	private static boolean hasGroupPermission(String playerUUID, String protectedUUID){
		// Makes sure the block is protected
		if(protectedUUID != null){
			// Is this block protected
			if(playerUUID != null){
				// Do you own this block
				if(playerUUID.equals(protectedUUID)){
					// You own this block
					return true;
				}else{
					// You do not own this block
					
					// Are you in the same group as the protector?
					if(Group.inSameGroup(playerUUID, protectedUUID)){
						// You are in the same group as the owner
						return true;
					}else{
						// You are not in the same group as the protector
						return false;
					}
				}
			}else{
				// This block is not protected
				return true;
			}
		}else{
			// The block is not protected
			return true;
		}
	}
	
	public static class UUID {
		String UUID;
		
		public UUID(String UUID){
			this.UUID = UUID;
		}
		
		public boolean hasGroupPermission(String protectedUUID){
			return PermissionPlayer.hasGroupPermission(UUID, protectedUUID);
		}
	}
	
}
