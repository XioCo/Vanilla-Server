package no.xioco.commands;

import net.scratchforfun.xioco.XioCo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by scratchforfun on 15.09.2014.
 */
public class ChatCommand implements CommandExecutor{

    XioCo plugin;

    public ChatCommand(XioCo instance){

    }
    @Override
public boolean onCommand(CommandSender sender, Command cmd, String cmdl, String[] args) {
    
        if (cmd.getName().equalsIgnoreCase("chat")) {
            if(sender instanceof Player) {
            Player player = (Player) sender;
            //Needs permission
            if (player.hasPermission("xioco.chat")) {
                if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
                    for (int i = 0; i < 100; i++) {
                        Bukkit.broadcastMessage(" ");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Du har ikke nødvendige rettigheter!");
            }
        }else{
        sender.sendMessage("Du ma vere ingame for a bruke denne kommandoen!");
        }
    }
    return false;
}
}
