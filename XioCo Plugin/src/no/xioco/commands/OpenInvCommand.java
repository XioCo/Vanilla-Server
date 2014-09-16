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
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(args.length == 2){
                    if(args[0].equalsIgnoreCase("inv")) {
                        //TEMPORARY SOLUTION! For some reason, getting the player returns null even when it is checked for. Will figure it out later.
                        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                            if (onlinePlayers.getName().equalsIgnoreCase(args[1])) {
                                if (!args[1].equalsIgnoreCase(player.getName())) {
                                    Inventory inv = onlinePlayers.getInventory();
                                    player.openInventory(inv);
                                } else {
                                    player.sendMessage(ChatColor.RED + "Fy på deg! Hvorfor prøver du å åpne din egen inventory? :(");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Brukeren: " + ChatColor.WHITE + args[1] + ChatColor.RED + " er ikke online!");
                            }
                        }
                    }else if(args[0].equalsIgnoreCase("enderchest")) {
                        //TEMPORARY SOLUTION! For some reason, getting the player returns null even when it is checked for. Will figure it out later.
                        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                            if (onlinePlayers.getName().equalsIgnoreCase(args[1])) {
                                if (!args[1].equalsIgnoreCase(player.getName())) {
                                    Inventory inv = onlinePlayers.getEnderChest();
                                    player.openInventory(inv);
                                } else {
                                    player.sendMessage(ChatColor.RED + "Fy på deg! Hvorfor prøver du å åpne din egen inventory? :(");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Brukeren: " + ChatColor.WHITE + args[1] + ChatColor.RED + " er ikke online!");
                            }
                        }
                        }else{
                            player.sendMessage(ChatColor.RED + "Denne subkommandoen finnes ikke. Bruk /open <inv/enderchest> [brukernavn]");
                        }
                }
            }else{
                sender.sendMessage("Du ma vere ingame for a bruke denne kommandoen!");
            }
            return true;
        }
        return false;
    }
}
