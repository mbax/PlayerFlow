package net.phozop.playerflow;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class PlayerFlowPlugin extends JavaPlugin {

    final FlowPlayerListener listener = new FlowPlayerListener(this);

    private PluginDescriptionFile selfDescription;
    private Logger log;
    private FlowHandler handler;

    public FlowHandler getHandler() {
        return this.handler;
    }

    public void log(String message) {
        this.log.info("[PlayerFlow] " + ChatColor.stripColor(message));
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        this.log("Version " + this.selfDescription.getVersion() + " disabled.");
    }

    @Override
    public void onEnable() {
        this.log = Logger.getLogger("Minecraft");

        this.selfDescription = this.getDescription();

        final Configuration config = this.getConfiguration();

        this.handler = new FlowHandler(this, config.getInt("summaryDelaySeconds", 60), config.getBoolean("announceNoChange", false), config.getBoolean("logSummaries", false));

        config.save();

        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, this.listener, Priority.Highest, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, this.listener, Priority.Highest, this);

        this.log("Version " + this.selfDescription.getVersion() + " enabled.");
    }

}
