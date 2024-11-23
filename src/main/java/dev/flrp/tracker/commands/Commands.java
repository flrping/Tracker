package dev.flrp.tracker.commands;

import dev.flrp.tracker.Tracker;
import dev.flrp.tracker.configuration.Locale;
import dev.flrp.tracker.configuration.Settings;
import dev.flrp.tracker.reward.Reward;
import dev.flrp.tracker.utils.Methods;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Command("tracker")
public class Commands extends CommandBase {

    private final Tracker plugin;

    public Commands(Tracker plugin) {
        this.plugin = plugin;
    }

    @Default
    public void helpCommand(final CommandSender commandSender) {
        commandSender.sendMessage(Locale.parse("&7"));
        commandSender.sendMessage(Locale.parse("&c&lTRACKER+ &7" + plugin.getDescription().getVersion() + " &8| &7By flrp <3"));
        commandSender.sendMessage(Locale.parse("&f/tracker &8- &7Displays this help guide."));
        commandSender.sendMessage(Locale.parse("&f/tracker progress &8- &7Displays the milestone progress for this tool."));
        commandSender.sendMessage(Locale.parse("&f/tracker milestones &8- &7Displays all the milestones for this tool."));
        if(commandSender.hasPermission("tracker.admin")) {
            commandSender.sendMessage(Locale.parse("&f/tracker give <player> &8- &7Gives a player a milestone module."));
            commandSender.sendMessage(Locale.parse("&f/tracker reload &8- &7Reloads the plugin."));
        }
        commandSender.sendMessage(Locale.parse("&7"));
    }

    @SubCommand("progress")
    @Permission("tracker.progress")
    public void progressCommand(final CommandSender commandSender) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            ItemStack item = Methods.itemInHand(player);
            if(Settings.BLOCK_LIST.contains(item.getType().toString()) || Settings.KILL_LIST.contains(item.getType().toString())) {
                int amount = plugin.getRewardManager().getHighestAvailableMilestone(item);
                switch (amount) {
                    case -1:
                        commandSender.sendMessage(Locale.parse(Locale.PREFIX + Locale.NO_MILESTONES_EXIST_TOTAL));
                        return;
                    case 0:
                        commandSender.sendMessage(Locale.parse(Locale.PREFIX + Locale.NO_MILESTONES_LEFT));
                        return;
                    default:
                        String message = Locale.PREFIX + Locale.CURRENT_MILESTONE;
                        message = message
                                .replace("{0}", String.valueOf(amount))
                                .replace("{1}", plugin.getRewardManager().getObjectiveType(item));
                        commandSender.sendMessage(Locale.parse(message));
                        return;
                }
            }
            commandSender.sendMessage(Locale.parse(Locale.PREFIX + Locale.TOOL_NOT_SUPPORTED));
            return;
        }
        Locale.log("&cThis command can only be run by players.");
    }

    @SubCommand("milestones")
    @Permission("tracker.milestones")
    public void milestonesCommand(final CommandSender commandSender) {
        if(commandSender instanceof  Player) {
            Player player = (Player) commandSender;
            ItemStack item = Methods.itemInHand(player);
            if(Settings.BLOCK_LIST.contains(item.getType().toString()) || Settings.KILL_LIST.contains(item.getType().toString())) {
                String priority = plugin.getRewardManager().getPriority(item.getType());
                Reward reward = plugin.getRewardManager().getReward(priority);

                if(!reward.hasOnceMilestones() && !reward.hasIntervalMilestones()) {
                    commandSender.sendMessage(Locale.parse(Locale.PREFIX + Locale.NO_MILESTONES_EXIST));
                    return;
                }

                StringBuilder format = new StringBuilder();
                if(reward.hasOnceMilestones()) {
                    int amount = plugin.getTrackerManager().getAmount(item);
                    format.append(Locale.MILESTONE_ONCE_HEADER);
                    for(Integer milestone : reward.getOnceMilestones()) {
                        String message = Locale.MILESTONE_ONCE_ENTRY;
                        format.append("\n").append(message
                                .replace("{0}", milestone.toString())
                                .replace("{1}", plugin.getRewardManager().getObjectiveType(item))
                                .replace("{2}", plugin.getRewardManager().isReached(amount, milestone)));
                    }
                }

                if(reward.hasIntervalMilestones()) {
                    if(reward.hasOnceMilestones()) format.append("\n&r\n");
                    format.append(Locale.parse(Locale.MILESTONE_INTERVAL_HEADER));
                    for(Integer milestone : reward.getIntervalMilestones()) {
                        String message = Locale.MILESTONE_INTERVAL_ENTRY;
                        format.append("\n").append(message.replace("{0}", milestone.toString())
                                .replace("{1}", plugin.getRewardManager().getObjectiveType(item)));
                    }
                }

                List<String> message = Locale.MILESTONE_LIST;
                for(String entry : message) {
                    commandSender.sendMessage(Locale.parse(entry.replace("{0}", format)));
                }
                return;
            }
            commandSender.sendMessage(Locale.parse(Locale.PREFIX + Locale.TOOL_NOT_SUPPORTED));
        } else {
            Locale.log("&cThis command can only be run by players.");
        }
    }

    @SubCommand("reload")
    @Permission("tracker.admin")
    public void reloadCommand(final CommandSender commandSender) {
        plugin.reloadConfig();
        plugin.onReload();
        commandSender.sendMessage(Locale.parse(Locale.PREFIX + "&7Plugin successfully reloaded."));
    }

    @SubCommand("give")
    @Permission("tracker.admin")
    public void statTrakCommand(final CommandSender commandSender, final Player player) {

        ItemStack item = new ItemStack(Settings.MODULE_MATERIAL, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Locale.parse(Settings.MODULE_TITLE));
        List<String> lore = new ArrayList<>();
        for(String s : Settings.MODULE_LORE) {
            lore.add(Locale.parse(s));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        try {
            player.getInventory().addItem(item);
            if(Settings.MODULE_SOUNDS_ENABLED && Settings.MODULE_RECEIVE_SOUND != null) player.playSound(player.getLocation(), Settings.MODULE_RECEIVE_SOUND, 1, 1);
            player.sendMessage(Locale.parse(Locale.PREFIX + Locale.MODULE_RECEIVED));
            if(commandSender instanceof Player) {
                commandSender.sendMessage(Locale.parse(Locale.PREFIX + Locale.MODULE_GIVE_PLAYER.replace("{0}", player.getName())));
            } else {
                Locale.log(player.getName() + " was given a tracker enchantment item.");
            }
        } catch(Exception e) {
            commandSender.sendMessage(Locale.parse(Locale.PREFIX + "&cCould not give the defined player a module."));
        }
    }

}
