package no.xioco.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;

/**
 * Created by jimbo8 on 16.09.2014.
 */
public class PlayerListener implements Listener{

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event){
        event.setCancelled(true);
    }
}
