package no.xioco.commands;

import net.scratchforfun.xioco.Helpop;
import net.scratchforfun.xioco.XioCo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by jimbo8 on 15.09.2014.
 */
public class HelpListCommand implements CommandExecutor{
    XioCo plugin;

    public HelpListCommand(XioCo instance){
        plugin = instance;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdl, String[] args){
        if(cmd.getName().equalsIgnoreCase("helplist")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                //Needs permission
                if (sender.hasPermission("xioco.helpop")) {
                    //Grabs all the helpops
                    for (String helpop : Helpop.getHelplist()) {
                        if (helpop != null) {
                            //Let's the player know the message is received
                            player.sendMessage(ChatColor.RED + helpop);
                            return true;
                        }else{
                            player.sendMessage(ChatColor.RED + "Helpop-fila er tom.");
                            return true;
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Du har ikke nødvendige rettigheter!");
                    return true;
                }
            }
        }else{
            sender.sendMessage("Du ma vere ingame for a bruke denne kommandoen!");
        }
            return false;

    }


}
