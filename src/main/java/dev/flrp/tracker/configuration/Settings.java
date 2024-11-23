package dev.flrp.tracker.configuration;

import dev.flrp.tracker.Tracker;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.List;

public final class Settings {

    public static final Tracker instance = Tracker.getInstance();

    // General
    public static String TAG;
    public static List<String> BLOCK_LIST;
    public static List<String> KILL_LIST;
    public static Sound TRACK_SOUND;

    // Module
    public static Material MODULE_MATERIAL;
    public static String MODULE_TITLE;
    public static List<String> MODULE_LORE;

    public static Sound MODULE_SUCCESS_SOUND;
    public static Sound MODULE_FAIL_SOUND;
    public static Sound MODULE_RECEIVE_SOUND;

    // Booleans
    public static boolean SOUND_ENABLED;
    public static boolean BLOCK_ENABLED;
    public static boolean KILL_ENABLED;
    public static boolean MILESTONES_ENABLED;
    public static boolean MODULE_ENABLED;
    public static boolean MODULE_SOUNDS_ENABLED;

    // Extra
    public static String TAG_HEAD;
    public static String TAG_TAIL;
    public static List<String> WHITELIST_BLOCKS;
    public static List<String> WHITELIST_MOBS;
    public static List<String> ENCHANTMENT_LIMITS;

    public static void load() {
        TAG = getString("counter.tag");
        BLOCK_LIST = getStringList("stats.blocks.list");
        KILL_LIST = getStringList("stats.kills.list");
        TRACK_SOUND = getSound("counter.sounds.sound");

        SOUND_ENABLED = getBoolean("counter.sounds.enabled");
        KILL_ENABLED = getBoolean("stats.kills.enabled");
        BLOCK_ENABLED = getBoolean("stats.blocks.enabled");
        MILESTONES_ENABLED = getBoolean("counter.milestones.enabled");
        MODULE_ENABLED = getBoolean("module.enabled");

        MODULE_MATERIAL = getMaterial("module.item.material");
        MODULE_TITLE = getString("module.item.title");
        MODULE_LORE = getStringList("module.item.lore");

        MODULE_SOUNDS_ENABLED = getBoolean("module.sounds.enabled");
        MODULE_RECEIVE_SOUND = getSound("module.sounds.receive");
        MODULE_FAIL_SOUND = getSound("module.sounds.fail");
        MODULE_SUCCESS_SOUND = getSound("module.sounds.success");

        TAG_HEAD = TAG.substring(0, TAG.indexOf("%stats%"));
        TAG_TAIL = TAG.substring(TAG.indexOf("%stats%") + 7);
        WHITELIST_BLOCKS = instance.getWhitelist().getConfiguration().getStringList("blocks");
        WHITELIST_MOBS = instance.getWhitelist().getConfiguration().getStringList("mobs");
        ENCHANTMENT_LIMITS = getStringList("limits.enchantments");
    }

    private static Material getMaterial(String identifier) {
        try {
            return Material.valueOf(instance.getConfig().getString(identifier));
        } catch(Exception ignored) {
            Locale.log("Invalid material on path " + identifier + ". Please use a material your server version has available.");
            return null;
        }
    }

    private static Sound getSound(String identifier) {
        try {
            return Sound.valueOf(instance.getConfig().getString(identifier));
        } catch(Exception ignored) {
            Locale.log("Invalid sound on path " + identifier + ". Please use a sound your server version has available.");
            return null;
        }
    }

    private static String getString(String identifier) {
        return instance.getConfig().getString(identifier);
    }

    private static List<String> getStringList(String identifier) {
        return instance.getConfig().getStringList(identifier);
    }

    private static boolean getBoolean(String identifier) {
        return instance.getConfig().getBoolean(identifier);
    }

}
