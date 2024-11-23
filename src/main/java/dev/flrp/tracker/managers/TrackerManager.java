package dev.flrp.tracker.managers;

import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.flrp.tracker.Tracker;
import dev.flrp.tracker.api.events.TrackerUpdateEvent;
import dev.flrp.tracker.configuration.Locale;
import dev.flrp.tracker.configuration.Settings;
import dev.flrp.tracker.item.TrackedItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrackerManager {

    private final Tracker plugin;

    public TrackerManager(Tracker plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the amount of the tracker.
     * @param item The ItemStack being processed.
     * @return Returns the tracker amount.
     */
    public int getAmount(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        return nbti.getInteger("TrackerAmount");
    }

    /**
     * Gets the amount of the tracker.
     * @param item The NTBItem being processed.
     * @return Returns the tracker amount.
     */
    public int getAmount(NBTItem item) {
        return item.getInteger("TrackerAmount");
    }

    /**
     * Handles the tracker process with the help of the NBT api.
     *
     * @param player The owner of the item.
     * @param item The ItemStack being processed.
     * @param increase The increase of the tracker.
     */
    public void handleTracker(Player player, ItemStack item, int increase) {
        TrackedItem tracked = new TrackedItem(item);
        if(Settings.MODULE_ENABLED && !tracked.hasTrackerModule()) return;
        if(tracked.hasTrackerData()) {
            TrackerUpdateEvent trackerUpdateEvent = new TrackerUpdateEvent(player, item, tracked.getTrackerAmount(), increase);
            if(trackerUpdateEvent.isCancelled()) return;
            Bukkit.getPluginManager().callEvent(trackerUpdateEvent);
            int before = tracked.getTrackerAmount();
            tracked.applyTracker(increase);
            if(Settings.MILESTONES_ENABLED) plugin.getRewardManager().handleRewards(player, tracked, before, tracked.getTrackerAmount());
            item.setItemMeta(tracked.getItemMeta());
            return;
        }
        TrackerUpdateEvent trackerUpdateEvent = new TrackerUpdateEvent(player, item, increase);
        if(trackerUpdateEvent.isCancelled()) return;
        Bukkit.getPluginManager().callEvent(trackerUpdateEvent);
        tracked.applyTracker(increase);
        if(Settings.MILESTONES_ENABLED) plugin.getRewardManager().handleRewards(player, tracked, 0, tracked.getTrackerAmount());
        item.setItemMeta(tracked.getItemMeta());
    }

    // Modules
    public boolean isModule(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if(!Locale.parse(Settings.MODULE_TITLE).equals(meta.getDisplayName())) return false;
        for(int i = 0; i < Settings.MODULE_LORE.size(); i++) {
            String a = Locale.parse(Settings.MODULE_LORE.get(i));
            String b = meta.getLore().get(i);
            if(!a.equals(b)) return false;
        }
        return true;
    }

}
