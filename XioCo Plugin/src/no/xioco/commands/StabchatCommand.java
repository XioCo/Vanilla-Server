package no.xioco.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by jimbo8 on 16.09.2014.
 */
public class StabchatCommand implements CommandExecutor{


    public boolean onCommand(CommandSender sender, Command cmd, String cmdl, String[] args) {
        if (cmd.getName().equalsIgnoreCase("c")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("xioco.adminchat")) {
                    if(args.length != 0) {
                        //Creates the StringBuilder
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < args.length; i++) {
                            if (sb.length() > 0) {
                                sb.append(" ");
                            }
                            sb.append(args[i]);
                        }
                        Bukkit.broadcast("(" + ChatColor.BLUE + "Vakt" + ChatColor.GRAY + "/" + ChatColor.GOLD + "Stab" + ChatColor.RESET + ") " + player.getDisplayName() + ": " + sb.toString(), "xioco.adminchat");
                    } else {
                        player.sendMessage(ChatColor.RED + "Du glemte å skrive inn en melding, din gjøk! >:(");
                    }
                    } else {
                    player.sendMessage(ChatColor.RED + "Du har ikke nødvendige rettigheter!");
                }

                return true;
            } else {
               sender.sendMessage("Du ma vere ingame for a bruke denne kommandoen!");
            }
        }
        return false;
    }
}
