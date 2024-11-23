package dev.flrp.tracker.hooks.stacker;

import dev.flrp.espresso.hook.stacker.RoseStackerStackerProvider;
import dev.flrp.tracker.Tracker;
import dev.flrp.tracker.configuration.Settings;
import dev.flrp.tracker.utils.Methods;
import dev.rosewood.rosestacker.event.EntityUnstackEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class RoseStackerListener extends RoseStackerStackerProvider {

    private final Tracker plugin;

    public RoseStackerListener(Tracker plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onStackDeath(EntityUnstackEvent event) {
        if(!Settings.KILL_ENABLED) return;

        LivingEntity entity = event.getStack().getEntity();

        if(!(entity instanceof Player)) return;
        if(entity.getKiller() == null) return;
        if(!Settings.WHITELIST_MOBS.isEmpty() && !Settings.WHITELIST_MOBS.contains(entity.getType().toString())) return;

        int before = event.getStack().getStackSize();
        int after = event.getResult().getStackSize();
        Player player = event.getStack().getEntity().getKiller();
        ItemStack item = Methods.itemInHand(player);
        if(!Settings.KILL_LIST.contains(item.getType().toString())) return;

        plugin.getTrackerManager().handleTracker(player, item, before - after);
    }
}
