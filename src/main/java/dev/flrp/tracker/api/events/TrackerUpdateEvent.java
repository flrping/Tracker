package dev.flrp.tracker.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class TrackerUpdateEvent extends TrackerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;

    private final int amount;
    private final int increase;

    public TrackerUpdateEvent(Player player, ItemStack item, int increase) {
        super(player, item);
        this.amount = 0;
        this.increase = increase;
    }

    public TrackerUpdateEvent(Player player, ItemStack item, int amount, int increase) {
        super(player, item);
        this.amount = amount;
        this.increase = increase;
    }

    public int getAmount() {
        return amount;
    }

    public int getIncrease() {
        return increase;
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
