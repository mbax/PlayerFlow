package net.phozop.playerflow;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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

    private final PlayerFlowPlugin flow;
    private int joinCount = 0, quitCount = 0;
    private final String delay;

    private final boolean announceBoring;
    private final boolean logSummaries;

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
    }

    public void modJoinCount(int modification) {
        this.joinCount += modification;
    }

    public void modQuitCount(int modification) {
        this.quitCount += modification;
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
