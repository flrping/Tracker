package dev.flrp.tracker.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import dev.flrp.espresso.hook.stacker.StackerProvider;
import dev.flrp.espresso.hook.stacker.StackerType;
import dev.flrp.tracker.Tracker;
import dev.flrp.tracker.api.events.TrackerUpdateEvent;
import dev.flrp.tracker.configuration.Settings;
import dev.flrp.tracker.utils.Methods;

public class EntityListener implements StackerProvider {

    private final Tracker plugin;

    public EntityListener(Tracker plugin) {
        this.plugin = plugin;
    }

    @Override
    public StackerType getType() {
        return StackerType.NONE;
    }

    @Override
    public int getStackSize(LivingEntity livingEntity) {
        return 1;
    }

    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void unregisterEvents() {
        EntityDeathEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(!Settings.KILL_ENABLED) return;

        LivingEntity entity = event.getEntity();

        if((entity instanceof Player)) return;
        if(event.getEntity().getKiller() == null) return;
        if(!Settings.WHITELIST_MOBS.isEmpty() && !Settings.WHITELIST_MOBS.contains(entity.getType().toString())) return;

        Player player = event.getEntity().getKiller();
        ItemStack item = Methods.itemInHand(player);
        if(!Settings.KILL_LIST.contains(item.getType().toString())) return;

        plugin.getTrackerManager().handleTracker(player, item, 1);
    }

    @EventHandler
    public void onTrackerUpdate(TrackerUpdateEvent event) {
        Player player = event.getPlayer();
        if(Settings.SOUND_ENABLED && Settings.TRACK_SOUND != null) player.playSound(player.getLocation(), Settings.TRACK_SOUND, 1, 1);
    }

}
