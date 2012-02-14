package org.kitteh.playerflow;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.monstercraft.irc.IRC;

public class FlowHandler {

    private class Announcement implements Runnable {

        private final PlayerFlowPlugin flow;

        public Announcement(PlayerFlowPlugin flow) {
            this.flow = flow;
        }

        @Override
        public void run() {
            final int quits = this.flow.getHandler().getQuitCount();
            final int joins = this.flow.getHandler().getJoinCount();
            if ((quits == 0) && (joins == 0) && !this.flow.getHandler().boring()) {
                return;
            }
            final Player[] playerList = this.flow.getServer().getOnlinePlayers();
            final StringBuilder recentActivityBuilder = new StringBuilder();
            recentActivityBuilder.append(ChatColor.YELLOW + "In " + this.flow.getHandler().getDelay() + ", " + joins + " player");
            if (joins != 1) {
                recentActivityBuilder.append("s");
            }
            recentActivityBuilder.append(" joined and " + quits + " quit.");
            final String recentActivity = recentActivityBuilder.toString();
            final StringBuilder currentlyOnlineBuilder = new StringBuilder();
            currentlyOnlineBuilder.append(ChatColor.YELLOW + String.valueOf(playerList.length) + " player");
            if (playerList.length != 1) {
                currentlyOnlineBuilder.append("s");
            }
            currentlyOnlineBuilder.append(" currently online");
            final String currentlyOnline = currentlyOnlineBuilder.toString();
            for (final Player player : playerList) {
                if ((player != null) && player.hasPermission("playerflow.receivesummary")) {
                    player.sendMessage(recentActivity);
                    player.sendMessage(currentlyOnline);
                }
            }
            if (this.flow.getHandler().logSummaries()) {
                this.flow.log(recentActivity);
                this.flow.log(currentlyOnline);
            }
            FlowHandler.this.resetCounts();
        }
    }

    private class IRCAnnouncement implements Runnable {

        private int joins = 0;
        private int quits = 0;

        public void join() {
            this.joins++;
        }

        public void quit() {
            this.quits++;
        }

        @Override
        public void run() {
            final String message = FlowHandler.this.flow.getMessage().replace("%j", Integer.toString(this.joins)).replace("%q", Integer.toString(this.quits)).replace("%o", Integer.toString(flow.getServer().getOnlinePlayers().length));
            if (!FlowHandler.this.flow.getServer().getPluginManager().isPluginEnabled("MonsterIRC")) {
                return;
            }
            try {
                for (final String channel : FlowHandler.this.flow.getChannels()) {
                    IRC.getHandleManager().getIRCHandler().sendMessage(message, "#" + channel);
                }
            } catch (final Exception e) {
            }
            this.joins = 0;
            this.quits = 0;
        }
    }

    private final PlayerFlowPlugin flow;
    private int joinCount = 0, quitCount = 0;
    private final String delay;

    private final boolean announceBoring;
    private final boolean logSummaries;

    private final IRCAnnouncement ircAnnouncement = new IRCAnnouncement();

    public FlowHandler(PlayerFlowPlugin flow, int delay, boolean announceBoring, boolean logSummaries) {
        this.flow = flow;
        this.announceBoring = announceBoring;
        this.logSummaries = logSummaries;
        final StringBuilder delayBuilder = new StringBuilder();
        if ((delay % 3600) == 0) {
            final int del = delay / 3600;
            delayBuilder.append(del);
            delayBuilder.append(" ");
            delayBuilder.append("hour");
            if (del > 1) {
                delayBuilder.append("s");
            }
        } else if ((delay % 60) == 0) {
            final int del = delay / 60;
            delayBuilder.append(del);
            delayBuilder.append(" ");
            delayBuilder.append("minute");
            if (del > 1) {
                delayBuilder.append("s");
            }
        } else {
            delayBuilder.append(delay);
            delayBuilder.append(" ");
            delayBuilder.append("second");
            if (delay > 1) {
                delayBuilder.append("s");
            }
        }
        this.delay = delayBuilder.toString();
        final int tickdelay = delay * 20;
        this.flow.getServer().getScheduler().scheduleSyncRepeatingTask(flow, new Announcement(this.flow), tickdelay, tickdelay);
        this.flow.getServer().getScheduler().scheduleSyncRepeatingTask(flow, this.ircAnnouncement, 1200, 6000);
    }

    public void join() {
        this.joinCount++;
        this.ircAnnouncement.join();
    }

    public void quit() {
        this.quitCount++;
        this.ircAnnouncement.quit();
    }

    private boolean boring() {
        return this.announceBoring;
    }

    private String getDelay() {
        return this.delay;
    }

    private int getJoinCount() {
        return this.joinCount;
    }

    private int getQuitCount() {
        return this.quitCount;
    }

    private boolean logSummaries() {
        return this.logSummaries;
    }

    private void resetCounts() {
        this.joinCount = 0;
        this.quitCount = 0;
    }
}
