package dev.flrp.tracker.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public abstract class TrackerEvent extends Event {

    private final Player player;
    private final ItemStack item;

    public TrackerEvent(Player player, ItemStack item) {
        this.player = player;
        this.item = item;
    }

    public Player getPlayer() {
        return  player;
    }

    public ItemStack getItem() {
        return item;
    }

}
