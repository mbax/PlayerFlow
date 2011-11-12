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
        if (event.getPlayer().hasPermission("playerflow.silent")) {
            return;
        }
        this.flow.getHandler().modJoinCount(1);
        this.messageByPerm(event.getJoinMessage(), "playerflow.receiveperuser");
        event.setJoinMessage(null);
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().hasPermission("playerflow.silent")) {
            return;
        }
        this.flow.getHandler().modQuitCount(1);
        this.messageByPerm(event.getQuitMessage(), "playerflow.receiveperuser");
        event.setQuitMessage(null);
    }

    private void messageByPerm(String message, String perm) {
        for (final Player player : this.flow.getServer().getOnlinePlayers()) {
            if ((player != null) && player.hasPermission(perm)) {
                player.sendMessage(message);
            }
        }
    }
}
