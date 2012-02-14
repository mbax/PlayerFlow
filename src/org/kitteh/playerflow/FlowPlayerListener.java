package org.kitteh.playerflow;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FlowPlayerListener implements Listener {

    private final PlayerFlowPlugin flow;

    public FlowPlayerListener(PlayerFlowPlugin flow) {
        this.flow = flow;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        if (event.getPlayer().hasPermission("playerflow.silent")) {
            return;
        }
        this.flow.getHandler().join();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        if (event.getPlayer().hasPermission("playerflow.silent")) {
            return;
        }
        this.flow.getHandler().quit();
    }

}
