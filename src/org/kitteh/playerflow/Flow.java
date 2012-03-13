package org.kitteh.playerflow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Flow implements Runnable {

    private final boolean onlyUpdated;
    private final boolean log;
    private int joins;
    private int quits;
    private int online;
    private final String message;
    private final String permission;

    public Flow(String name, boolean onlyUpdated, boolean log, int online, String message) {
        this.permission = "playerflow.receive." + name;
        this.onlyUpdated = onlyUpdated;
        this.log = log;
        this.online = online;
        this.message = message.replace("&&", String.valueOf(ChatColor.COLOR_CHAR));
    }

    public void join(int online) {
        this.joins++;
        this.online = online;
    }

    public void quit(int online) {
        this.quits++;
        this.online = online;
    }

    @Override
    public void run() {
        if ((this.quits == 0) && (this.joins == 0) && this.onlyUpdated) {
            return;
        }
        String send = this.message.replace("%join%", String.valueOf(this.joins));
        send = send.replace("%quit%", String.valueOf(this.quits));
        send = send.replace("%online%", String.valueOf(this.online));
        Bukkit.broadcast(send, this.permission);
        if (this.log) {
            Bukkit.getLogger().info(ChatColor.stripColor(send));
        }
        this.joins = 0;
        this.quits = 0;
    }

}
