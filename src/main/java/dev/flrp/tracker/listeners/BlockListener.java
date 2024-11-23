package dev.flrp.tracker.listeners;

import dev.flrp.tracker.Tracker;
import dev.flrp.tracker.configuration.Settings;
import dev.flrp.tracker.utils.Methods;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

    private final Tracker plugin;

    public BlockListener(Tracker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if(!Settings.BLOCK_ENABLED) return;
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = Methods.itemInHand(player);

        if(!Settings.BLOCK_LIST.contains(item.getType().toString())) return;
        if(!Settings.WHITELIST_BLOCKS.isEmpty() && !Settings.WHITELIST_BLOCKS.contains(block.getType().toString())) return;

        plugin.getTrackerManager().handleTracker(player, item, 1);
    }

}
