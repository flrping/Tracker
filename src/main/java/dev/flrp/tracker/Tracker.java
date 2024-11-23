package dev.flrp.tracker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.flrp.espresso.configuration.Configuration;
import dev.flrp.tracker.commands.Commands;
import dev.flrp.tracker.configuration.Locale;
import dev.flrp.tracker.configuration.Settings;
import dev.flrp.tracker.listeners.BlockListener;
import dev.flrp.tracker.listeners.PlayerListener;
import dev.flrp.tracker.managers.HookManager;
import dev.flrp.tracker.managers.RewardManager;
import dev.flrp.tracker.managers.TrackerManager;
import dev.flrp.tracker.module.StackerModule;
import dev.flrp.tracker.utils.UpdateChecker;
import me.mattstudios.mf.base.CommandManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Tracker extends JavaPlugin {

    private static Tracker instance;

    private static String version;

    private Configuration whitelist;
    private Configuration language;
    private Configuration milestones;

    private TrackerManager trackerManager;
    private RewardManager rewardManager;
    private HookManager hookManager;

    @Override
    public void onEnable() {
        instance = this;
        version = Bukkit.getVersion();

        Locale.log("&8--------------");
        Locale.log("&cTracker &rby flrp &8(&cv" + this.getDescription().getVersion() + "&8)");
        Locale.log("Thanks for supporting me! Join &cDiscord &rif you require support.");
        Locale.log("&8--------------");
        Locale.log("&cStarting...");

        // bStats
        new Metrics(this, 15167);

        // Files
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        initiateFiles();

        // Initiation
        Settings.load();
        Locale.load();

        // Initiation
        initiateClasses();

        // Modules
        Injector hookInjector = Guice.createInjector(new StackerModule(this));
        hookManager = hookInjector.getInstance(HookManager.class);
        hookManager.getStackerProvider().registerEvents();

        // Update Checker
        new UpdateChecker(this, 108425).checkForUpdate(version -> {
            if(getConfig().getBoolean("check-for-updates")) {
                if(!getDescription().getVersion().equalsIgnoreCase(version)) {
                    Locale.log("&8--------------");
                    Locale.log("A new version of Tracker is available!");
                    Locale.log("Download it here:&c https://www.spigotmc.org/resources/tracker.108425/");
                    Locale.log("&8--------------");
                }
            }
        });

        // Hooks
        File dir = new File(getDataFolder(), "hooks");
        if(!dir.exists()) dir.mkdir();

        // Commands
        CommandManager commandManager = new CommandManager(this);
        commandManager.register(new Commands(this));

        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        Locale.log("&cDone!");
    }

    public void onReload() {
        Locale.log("&cReloading...");
        hookManager.getStackerProvider().unregisterEvents();

        // Files
        initiateFiles();

        // Initiation
        Locale.load();
        Settings.load();
        initiateClasses();


        hookManager.getStackerProvider().registerEvents();
        Locale.log("&cDone!");
    }

    private void initiateFiles() {
        whitelist = new Configuration(this, "whitelist");
        language = new Configuration(this, "language");
        milestones = new Configuration(this, "milestones");
    }

    private void initiateClasses() {
        trackerManager = new TrackerManager(this);
        rewardManager = new RewardManager(this);
    }

    public Configuration getWhitelist() {
        return whitelist;
    }

    public Configuration getLanguage() {
        return language;
    }

    public Configuration getMilestones() {
        return milestones;
    }

    public TrackerManager getTrackerManager() { return trackerManager; }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public static Tracker getInstance() {
        return instance;
    }

    public static String getVersion() {
        return version;
    }

}
