package no.xioco.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by jimbo8 on 17.09.2014.
 */
public class BroadcastCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String cmdl, String[] args){
        if(cmd.getName().equalsIgnoreCase("bc") || cmd.getName().equalsIgnoreCase("broadcast")){
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if(player.hasPermission("")) {
                    if (args.length > 1) {
                        //Creates the StringBuilder
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            if (sb.length() > 0) {
                                sb.append(" ");
                            }
                            sb.append(args[i]);
                        }
                        switch (args[0].toLowerCase()) {
                            case "viktig":
                                //broadcasts message
                                Bukkit.broadcastMessage(ChatColor.RED + "[Viktig] " + ChatColor.WHITE + sb.toString());
                                break;
                            case "tips":
                                //broadcasts message
                                Bukkit.broadcastMessage("(" + ChatColor.GREEN + "Tips" + ChatColor.RESET + ") " + ChatColor.WHITE + sb.toString());
                                break;
                            default:
                                player.sendMessage(ChatColor.RED + "Feil subkommando! Bruk /bc <Viktig/tips> [melding]");
                                break;
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Dududududu!! Det er /bc <viktig/tips> [melding], din løk! >:(");
                    }
                }else{
                    player.sendMessage(ChatColor.RED + "Du har ikke tilgang til denne kommandoen.");
                }
            }else{
                sender.sendMessage("Du ma vere ingame for a bruke denne kommandoen! >:(");
            }
        }
        return true;
    }
}
