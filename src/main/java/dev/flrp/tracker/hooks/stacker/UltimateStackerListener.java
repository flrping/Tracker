package dev.flrp.tracker.hooks.stacker;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import com.craftaro.ultimatestacker.api.events.entity.EntityStackKillEvent;

import dev.flrp.espresso.hook.stacker.UltimateStackerStackerProvider;
import dev.flrp.tracker.Tracker;
import dev.flrp.tracker.configuration.Settings;
import dev.flrp.tracker.utils.Methods;

public class UltimateStackerListener extends UltimateStackerStackerProvider {

    private final Tracker plugin;

    public UltimateStackerListener(Tracker plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onStackDeath(EntityStackKillEvent event) {
        if(!Settings.KILL_ENABLED) return;

        LivingEntity entity = event.getEntity();
        if(entity.getKiller() == null) return;
        if(!Settings.WHITELIST_MOBS.isEmpty() && !Settings.WHITELIST_MOBS.contains(entity.getType().toString())) return;

        Player player = entity.getKiller();
        ItemStack item = Methods.itemInHand(player);
        if(!Settings.KILL_LIST.contains(item.getType().toString())) return;
        int stackSize = event.isInstantKill() ? event.getStackSize() : event.getStackSize() - event.getNewStackSize();
        plugin.getTrackerManager().handleTracker(player, item, stackSize);
    }

}
