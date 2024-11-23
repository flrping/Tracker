package dev.flrp.tracker.listeners;

import dev.flrp.tracker.Tracker;
import dev.flrp.tracker.api.events.ModuleAddEvent;
import dev.flrp.tracker.configuration.Locale;
import dev.flrp.tracker.configuration.Settings;
import dev.flrp.tracker.item.TrackedItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final Tracker plugin;

    public PlayerListener(Tracker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnchantmentDrag(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null && event.getCursor() == null) return;
        if (event.getAction() != InventoryAction.SWAP_WITH_CURSOR) return;

        // Get values
        ItemStack dropped = event.getCursor();
        ItemStack grabbed = event.getCurrentItem();
        int slot = event.getSlot();

        // Requirement checks
        if(!Settings.KILL_LIST.contains(grabbed.getType().toString()) && !Settings.BLOCK_LIST.contains(grabbed.getType().toString())) return;
        if(!plugin.getTrackerManager().isModule(dropped)) return;
        TrackedItem tracked = new TrackedItem(grabbed);
        if(tracked.hasTrackerModule()) {
            player.sendMessage(Locale.parse(Locale.PREFIX + Locale.MODULE_EXISTS));
            if(Settings.MODULE_SOUNDS_ENABLED && Settings.MODULE_FAIL_SOUND != null) player.playSound(player.getLocation(), Settings.MODULE_FAIL_SOUND, 1, 1);
            event.setCancelled(true);
            return;
        }

        ModuleAddEvent moduleAdd = new ModuleAddEvent(player, grabbed);
        if(moduleAdd.isCancelled()) return;
        Bukkit.getPluginManager().callEvent(moduleAdd);

        // Applying data.
        tracked.setTrackerModule();
        tracked.setTrackerAmount(0);
        tracked.addTag(0);
        grabbed.setItemMeta(tracked.getItemMeta());

        // Applying the module
        event.setCancelled(true);

        if(dropped.getAmount() > 1) {
            dropped.setAmount(dropped.getAmount() - 1);
        } else {
            if(Tracker.getVersion().matches("[1].[01289][^3456]{1,2}")) {
                event.setCursor(null);
            } else {
                dropped.setAmount(0);
            }
        }

        player.getInventory().setItem(slot, grabbed);
        if(Settings.MODULE_SOUNDS_ENABLED && Settings.MODULE_SUCCESS_SOUND != null) player.playSound(player.getLocation(), Settings.MODULE_SUCCESS_SOUND, 1, 1);

        player.sendMessage(Locale.parse(Locale.PREFIX + Locale.MODULE_APPLIED));
    }

}
