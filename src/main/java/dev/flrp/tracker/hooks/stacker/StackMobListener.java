package dev.flrp.tracker.hooks.stacker;

import dev.flrp.espresso.hook.stacker.StackMobStackerProvider;
import dev.flrp.tracker.Tracker;
import dev.flrp.tracker.configuration.Settings;
import dev.flrp.tracker.utils.Methods;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import uk.antiperson.stackmob.events.StackDeathEvent;

public class StackMobListener extends StackMobStackerProvider {

    private final Tracker plugin;

    public StackMobListener(Tracker plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void stackMobEntityDeath(StackDeathEvent event) {
        if(!Settings.KILL_ENABLED) return;

        LivingEntity entity = event.getStackEntity().getEntity();

        if(entity.getKiller() == null) return;
        if(!Settings.WHITELIST_MOBS.isEmpty() && !Settings.WHITELIST_MOBS.contains(entity.getType().toString())) return;

        Player player = entity.getKiller();
        ItemStack item = Methods.itemInHand(player);
        if(!Settings.KILL_LIST.contains(item.getType().toString())) return;

        plugin.getTrackerManager().handleTracker(player, item, event.getDeathStep());
    }

}
