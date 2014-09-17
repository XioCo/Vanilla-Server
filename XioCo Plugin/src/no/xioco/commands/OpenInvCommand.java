package no.xioco.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Created by jimbo8 on 15.09.2014.
 */
public class OpenInvCommand implements CommandExecutor{

    public boolean onCommand(CommandSender sender, Command cmd, String cmdl, String[] args) {
        if(cmd.getName().equalsIgnoreCase("open")){
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("xioco.openinv")) {
                    if (args.length == 2) {
                        Player targetPlayer = Bukkit.getServer().getPlayer(args[1]);
                        switch (args[0].toLowerCase()) {
                            case "inv":
                                if (targetPlayer != null) {
                                    if (!targetPlayer.getName().equalsIgnoreCase(player.getName())) {
                                        Inventory inv = targetPlayer.getInventory();
                                        player.openInventory(inv);
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Fy på deg! Hvorfor prøver du å åpne din egen inventory? :(");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Brukeren: " + ChatColor.WHITE + args[1] + ChatColor.RED + " er ikke online!");
                                }

                                break;
                            case "enderchest":
                                if (targetPlayer != null) {
                                    if (!targetPlayer.getName().equalsIgnoreCase(player.getName())) {
                                        Inventory inv = targetPlayer.getEnderChest();
                                        player.openInventory(inv);
                                    } else {
                                        player.sendMessage(ChatColor.RED + "Fy på deg! Hvorfor prøver du å åpne din egen enderchest? :(");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Brukeren: " + ChatColor.WHITE + args[1] + ChatColor.RED + " er ikke online!");
                                }
                                break;
                            default:
                                player.sendMessage(ChatColor.RED + "Denne subkommandoen finnes ikke. Bruk /open <inv/enderchest> [brukernavn]");
                                break;
                        }
                    }else{
                        player.sendMessage("Feil bruk! >:(");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Du har ikke tilgang til denne kommandoen.");
                }
            } else {
                sender.sendMessage("Du ma vere ingame for a bruke denne kommandoen!");
            }
            return true;
        }
        return false;
    }
}
