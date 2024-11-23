package dev.flrp.tracker.module;

import com.google.inject.AbstractModule;
import dev.flrp.espresso.hook.stacker.StackerProvider;
import dev.flrp.espresso.hook.stacker.StackerType;
import dev.flrp.tracker.Tracker;
import dev.flrp.tracker.configuration.Locale;
import dev.flrp.tracker.hooks.stacker.RoseStackerListener;
import dev.flrp.tracker.hooks.stacker.StackMobListener;
import dev.flrp.tracker.hooks.stacker.UltimateStackerListener;
import dev.flrp.tracker.hooks.stacker.WildStackerListener;
import dev.flrp.tracker.listeners.EntityListener;

public class StackerModule extends AbstractModule {

    private final Tracker plugin;

    public StackerModule(Tracker plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Tracker.class).toInstance(plugin);

        bind(StackerProvider.class).toProvider(() -> {
            StackerType stackerType = plugin.getConfig().contains("stacker") ? StackerType.valueOf(plugin.getConfig().getString("stacker")) : StackerType.NONE;
            switch (stackerType) {
                case ROSE_STACKER:
                    Locale.log("Using RoseStacker for stacker support.");
                    return new RoseStackerListener(plugin);
                case STACK_MOB:
                    Locale.log("Using StackMob for stacker support.");
                    return new StackMobListener(plugin);
                case ULTIMATE_STACKER:
                    Locale.log("Using UltimateStacker for stacker support.");
                    return new UltimateStackerListener(plugin);
                case WILD_STACKER:
                    Locale.log("Using WildStacker for stacker support.");
                    return new WildStackerListener(plugin);
                default:
                    Locale.log("No stacker plugin found. Using default listener.");
                    return new EntityListener(plugin);
            }
        });
    }
}
