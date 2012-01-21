package org.kitteh.playerflow;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerFlowPlugin extends JavaPlugin {

    private FlowHandler handler;

    public FlowHandler getHandler() {
        return this.handler;
    }

    public void log(String message) {
        this.getServer().getLogger().info("[PlayerFlow] " + ChatColor.stripColor(message));
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        this.log("Version " + this.getDescription().getVersion() + " disabled.");
    }

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);

        this.handler = new FlowHandler(this, this.getConfig().getInt("summaryDelaySeconds", 60), this.getConfig().getBoolean("announceNoChange", false), this.getConfig().getBoolean("logSummaries", false));

        this.saveConfig();

        this.getServer().getPluginManager().registerEvents(new FlowPlayerListener(this), this);

        this.log("Version " + this.getDescription().getVersion() + " enabled.");
    }

}
