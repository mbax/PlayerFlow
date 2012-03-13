package org.kitteh.playerflow;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerFlowPlugin extends JavaPlugin implements Listener {

    private HashSet<Flow> flows;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void join(PlayerJoinEvent event) {
        this.getServer().broadcast(event.getJoinMessage(), "playerflow.receiveperuser");
        event.setJoinMessage(null);
        if (event.getPlayer().hasPermission("playerflow.silent")) {
            return;
        }
        if (event.getPlayer().hasPermission("")) {
            final int online = this.getServer().getOnlinePlayers().length;
            for (final Flow flow : this.flows) {
                flow.join(online);
            }
        }
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        this.getLogger().info("v" + this.getDescription().getVersion() + " disabled.");
    }

    @Override
    public void onEnable() {
        this.flows = new HashSet<Flow>();

        this.saveDefaultConfig();
        final FileConfiguration config = this.getConfig();
        final Set<String> keys = config.getKeys(false);
        final int online = this.getServer().getOnlinePlayers().length;
        for (final String key : keys) {
            final ConfigurationSection section = config.getConfigurationSection(key);
            final boolean onlyUpdated = section.getBoolean("onlyUpdated", false);
            final boolean log = section.getBoolean("log", true);
            final String message = section.getString("message");
            if (message == null) {
                continue;
            }
            final int period = section.getInt("period", 60) * 20;//in ticks
            final Flow flow = new Flow(key, onlyUpdated, log, online, message);
            this.getServer().getScheduler().scheduleSyncRepeatingTask(this, flow, period, period);
            this.flows.add(flow);
        }

        this.getServer().getPluginManager().registerEvents(this, this);

        this.getLogger().info("v" + this.getDescription().getVersion() + " enabled.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void quit(PlayerQuitEvent event) {
        this.getServer().broadcast(event.getQuitMessage(), "playerflow.receiveperuser");
        event.setQuitMessage(null);
        if (event.getPlayer().hasPermission("playerflow.silent")) {
            return;
        }
        if (event.getPlayer().hasPermission("")) {
            final int online = this.getServer().getOnlinePlayers().length;
            for (final Flow flow : this.flows) {
                flow.quit(online);
            }
        }
    }

}
