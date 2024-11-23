package dev.flrp.tracker.configuration;

import dev.flrp.tracker.Tracker;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Locale {

    private static final Tracker instance = Tracker.getInstance();
    private static final Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]){6}>");

    public static String PREFIX;
    public static String TOOL_NOT_SUPPORTED;
    public static String MILESTONE_NOT_REACHED;
    public static String MILESTONE_REACHED;
    public static String CURRENT_MILESTONE;
    public static String NO_MILESTONES_LEFT;
    public static String NO_MILESTONES_EXIST;
    public static String NO_MILESTONES_EXIST_TOTAL;
    public static String MILESTONE_ONCE_HEADER;
    public static String MILESTONE_ONCE_ENTRY;
    public static String MILESTONE_INTERVAL_HEADER;
    public static String MILESTONE_INTERVAL_ENTRY;
    public static List<String> MILESTONE_LIST;
    public static String MILESTONE_ACHIEVED_ONCE;
    public static String MILESTONE_ACHIEVED_INTERVAL;
    public static String MILESTONE_REWARD_ENTRY;
    public static List<String> MILESTONE_REWARD_LIST;
    public static String MODULE_GIVE_PLAYER;
    public static String MODULE_RECEIVED;
    public static String MODULE_APPLIED;
    public static String MODULE_EXISTS;

    public static void load() {
        PREFIX = addMessage("prefix");
        TOOL_NOT_SUPPORTED = addMessage("tool_not_supported");
        MILESTONE_NOT_REACHED = addMessage("milestone-not-reached");
        MILESTONE_REACHED = addMessage("milestone-reached");
        CURRENT_MILESTONE = addMessage("current-milestone");
        NO_MILESTONES_LEFT = addMessage("no-milestones-left");
        NO_MILESTONES_EXIST_TOTAL = addMessage("no-milestones-exist-total");
        NO_MILESTONES_EXIST = addMessage("no-milestones-exist");
        MILESTONE_ONCE_HEADER = addMessage("milestone-once-header");
        MILESTONE_ONCE_ENTRY = addMessage("milestone-once-entry");
        MILESTONE_INTERVAL_HEADER = addMessage("milestone-interval-header");
        MILESTONE_INTERVAL_ENTRY = addMessage("milestone-interval-entry");
        MILESTONE_LIST = addMessages("milestone-list");
        MILESTONE_ACHIEVED_ONCE = addMessage("milestone-achieved-once");
        MILESTONE_ACHIEVED_INTERVAL = addMessage("milestone-achieved-interval");
        MILESTONE_REWARD_ENTRY = addMessage("milestone-reward-entry");
        MILESTONE_REWARD_LIST = addMessages("milestone-reward-list");
        MODULE_GIVE_PLAYER = addMessage("module-give-player");
        MODULE_RECEIVED = addMessage("module-received");
        MODULE_APPLIED = addMessage("module-applied");
        MODULE_EXISTS = addMessage("module-exists");
    }

    private static String addMessage(String identifier) {
        return instance.getLanguage().getConfiguration().getString(identifier);
    }

    private static List<String> addMessages(String identifier) {
        return instance.getLanguage().getConfiguration().getStringList(identifier);
    }

    public static String parse(String context) {
        Matcher matcher = hexPattern.matcher(context);
        while (matcher.find()) {
            final ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
            final String before = context.substring(0, matcher.start());
            final String after = context.substring(matcher.end());
            context = before + hexColor + after;
            matcher = hexPattern.matcher(context);
        }
        return ChatColor.translateAlternateColorCodes('&', context);
    }

    public static void log(String context) {
        Bukkit.getConsoleSender().sendMessage(parse("[Tracker+] " + context));
    }

}
