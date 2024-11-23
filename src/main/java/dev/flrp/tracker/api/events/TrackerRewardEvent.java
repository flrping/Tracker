package dev.flrp.tracker.api.events;

import dev.flrp.tracker.reward.Reward;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class TrackerRewardEvent extends TrackerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;

    private final Reward reward;
    private final int milestone;

    public TrackerRewardEvent(Player player, ItemStack item, Reward reward, int milestone) {
        super(player, item);
        this.reward = reward;
        this.milestone = milestone;
    }

    public Reward getReward() {
        return reward;
    }

    public int getMilestone() {
        return milestone;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

}
