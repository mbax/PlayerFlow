package org.kitteh.playerflow;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerFlowPlugin extends JavaPlugin {

    private FlowHandler handler;

    private final Random random = new Random();

    private ArrayList<String> messageList;

    private HashSet<String> channels;

    public HashSet<String> getChannels() {
        return this.channels;
    }

    public FlowHandler getHandler() {
        return this.handler;
    }

    public String getMessage() {
        return this.messageList.get(this.random.nextInt(this.messageList.size()));
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

        this.channels = new HashSet<String>(this.getConfig().getStringList("channels"));

        this.saveConfig();

        this.getServer().getPluginManager().registerEvents(new FlowPlayerListener(this), this);
        boolean exception = false;
        this.messageList = new ArrayList<String>();
        final File messages = new File(this.getDataFolder(), "messages.txt");
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(messages);
        } catch (final Exception e) {
            exception = true;
            this.getLogger().severe("File not found: " + messages);
        }
        final BufferedReader buffer = new BufferedReader(fileReader);
        String line = null;
        try {
            while ((line = buffer.readLine()) != null) {
                this.messageList.add(line);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            exception = true;
        }
        try {
            buffer.close();
        } catch (final Exception e) {
            e.printStackTrace();
            exception = true;
        }
        if (exception) {
            this.messageList.add("OMG WTF %j JOINED %q QUIT LOL");
        }

        this.log("Version " + this.getDescription().getVersion() + " enabled.");
    }

}
