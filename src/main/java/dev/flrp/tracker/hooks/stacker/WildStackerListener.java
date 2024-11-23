package dev.flrp.tracker.hooks.stacker;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import com.bgsoftware.wildstacker.api.events.EntityUnstackEvent;

import dev.flrp.espresso.hook.stacker.WildStackerStackerProvider;
import dev.flrp.tracker.Tracker;
import dev.flrp.tracker.configuration.Settings;
import dev.flrp.tracker.utils.Methods;

public class WildStackerListener extends WildStackerStackerProvider {

    private final Tracker plugin;

    public WildStackerListener(Tracker plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void wildStackerEntityDeath(EntityUnstackEvent event) {
        if(!Settings.KILL_ENABLED) return;
        if(event.getUnstackSource() == null) return;

        Entity source = event.getUnstackSource();
        LivingEntity entity = event.getEntity().getLivingEntity();

        if(source.getType() != EntityType.PLAYER) return;
        if(!Settings.WHITELIST_MOBS.isEmpty() && !Settings.WHITELIST_MOBS.contains(entity.getType().toString())) return;

        Player player = plugin.getServer().getPlayer(source.getUniqueId());
        ItemStack item = Methods.itemInHand(player);
        if(!Settings.KILL_LIST.contains(item.getType().toString())) return;

        plugin.getTrackerManager().handleTracker(player, item, event.getAmount());

    }


}
