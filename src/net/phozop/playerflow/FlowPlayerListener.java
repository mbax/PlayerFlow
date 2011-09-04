package net.phozop.playerflow;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class FlowPlayerListener extends PlayerListener {

    private final PlayerFlowPlugin flow;

    public FlowPlayerListener(PlayerFlowPlugin flow) {
        this.flow = flow;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final String message = event.getJoinMessage();
        event.setJoinMessage(null);
        if (player.hasPermission("playerflow.silent")) {
            return;
        }
        this.flow.getHandler().modJoinCount(1);
        this.messageByPerm(message, "playerflow.receiveperuser");
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final String message = event.getQuitMessage();
        event.setQuitMessage(null);
        if (player.hasPermission("playerflow.silent")) {
            return;
        }
        this.flow.getHandler().modQuitCount(1);
        this.messageByPerm(message, "playerflow.receiveperuser");
    }

    /**
     * Thankfully this will be replaced by
     * Server.broadcast(String message, String permission)
     * 
     * @param message
     * @param perm
     */
    private void messageByPerm(String message, String perm) {
        for (final Player player : this.flow.getServer().getOnlinePlayers()) {
            if ((player != null) && player.hasPermission(perm)) {
                player.sendMessage(message);
            }
        }
    }
}
