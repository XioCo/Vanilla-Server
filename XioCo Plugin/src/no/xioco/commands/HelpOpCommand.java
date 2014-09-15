package no.xioco.commands;

import net.scratchforfun.xioco.Helpop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpOpCommand implements CommandExecutor{

// Created by jimbo8 @15.09.2014
    public boolean onCommand(CommandSender sender, Command cmd, String cmdl, String[] args) {
        if (cmd.getName().equalsIgnoreCase("helpop")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Du ma vere ingame for a bruke denne kommandoen!");
                return true;
            } else {
                Player player = (Player) sender;
                if (args.length > 0) {
                    //Creates the StringBuilder
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < args.length; i++) {
                        if (sb.length() > 0) {
                            sb.append(" ");
                        }
                        sb.append(args[i]);
                    }

                    //Creates the string 'helpop' before used multiple times
                    String helpop = ChatColor.RED + "Helpop: " + ChatColor.AQUA + player.getDisplayName() + ": " + sb.toString();
                    //broadcast the message to everyone with the specific permission
                    Bukkit.broadcast(helpop,"xioco.helpop");

                    //Let's the player know the message is received
                    player.sendMessage(helpop);

                    //Saves the helpop message to a file, use /helplist to retrieve the stuff in this file
                    Helpop.writeHelpopToFile(helpop);
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "Bruk /helpop MELDING");
                    return true;
                }
            }
        }
    return false;
    }
}
