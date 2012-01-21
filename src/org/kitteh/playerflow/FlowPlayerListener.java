package org.kitteh.playerflow;

import org.bukkit.entity.Player;
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
        final String message = event.getJoinMessage();
        event.setJoinMessage(null);
        if (event.getPlayer().hasPermission("playerflow.silent")) {
            return;
        }
        this.flow.getHandler().modJoinCount(1);
        this.messageByPerm(message, "playerflow.receiveperuser");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final String message = event.getQuitMessage();
        event.setQuitMessage(null);
        if (event.getPlayer().hasPermission("playerflow.silent")) {
            return;
        }
        this.flow.getHandler().modQuitCount(1);
        this.messageByPerm(message, "playerflow.receiveperuser");
    }

    private void messageByPerm(String message, String perm) {
        for (final Player player : this.flow.getServer().getOnlinePlayers()) {
            if ((player != null) && player.hasPermission(perm)) {
                player.sendMessage(message);
            }
        }
    }
}
