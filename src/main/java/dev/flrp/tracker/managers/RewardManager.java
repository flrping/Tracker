package dev.flrp.tracker.managers;

import dev.flrp.tracker.Tracker;
import dev.flrp.tracker.configuration.Locale;
import dev.flrp.tracker.configuration.Settings;
import dev.flrp.tracker.item.TrackedItem;
import dev.flrp.tracker.utils.Methods;
import dev.flrp.tracker.reward.Reward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardManager {

    private final Tracker plugin;

    private final HashMap<String, Reward> rewards = new HashMap<>();
    private final HashMap<Enchantment, Integer> enchantmentLimits = new HashMap<>();

    public RewardManager(Tracker plugin) {
        this.plugin = plugin;
        for(String priority : plugin.getMilestones().getConfiguration().getConfigurationSection("milestones").getKeys(false)) {
            rewards.put(priority, new Reward(priority));
        }
        for(String enchantment : Settings.ENCHANTMENT_LIMITS) {
            enchantmentLimits.put(Methods.getItemEnchantment(enchantment.substring(0, enchantment.indexOf(':'))),
                    Integer.parseInt(enchantment.substring(enchantment.indexOf(':') + 1)));
        }
        Locale.log("Loaded &c" + rewards.size() + "&r rewards.");
    }

    /**
     *
     * @param material
     * @return
     */
    public boolean isPriorityType(Material material) {
        if(rewards.containsKey(material.toString())) return true;
        if(rewards.containsKey("EVERY_" + getItemCategory(material))) return true;
        return rewards.containsKey("ALL");
    }

    /*
     * Gets the prioritized milestone type from the current list of milestones.
     */
    public String getPriority(Material material) {
        if(rewards.containsKey(material.toString())) {
            return material.toString();
        } else
        if(rewards.containsKey("EVERY_" + getItemCategory(material))) {
            return "EVERY_" + getItemCategory(material);
        } else
        if(rewards.containsKey("ALL")) {
            return "ALL";
        } else
            return "NONE";
    }

    /*
     * Gets the category the item fits in.
     */
    public String getItemCategory(Material material) {
        if(material.toString().contains("SWORD")) return "SWORD";
        if(material.toString().contains("PICKAXE")) return "PICKAXE";
        if(material.toString().contains("SHOVEL")) return "SHOVEL";
        if(material.toString().contains("AXE")) return "AXE";
        if(material.toString().contains("BOW")) return "BOW";
        return "OTHER";
    }

    /*
     * Gets the highest milestone a tool can achieve; returns -1 if no milestones are available; returns 0 if all are completed.
     */
    public int getHighestAvailableMilestone(ItemStack item) {
        String priority = getPriority(item.getType());
        if(priority.equals("NONE")) return -1;
        if(!getReward(priority).hasOnceMilestones()) return -1;
        for (int milestone : getReward(priority).getOnceMilestones()) {
            if (plugin.getTrackerManager().getAmount(item) < milestone) {
                return milestone;
            }
        }
        return 0;
    }

    /*
     * Finds where the tool belongs
     */
    public String getObjectiveType(ItemStack item) {
        if(Settings.BLOCK_LIST.contains(item.getType().toString())) {
            return "blocks";
        } else
        if(Settings.KILL_LIST.contains(item.getType().toString())) {
            return "kills";
        }
        return "NONE";
    }

    /*
     * Returns if the goal is reached or not.
     */
    public String isReached(int amount, int milestone) {
        if(amount >= milestone) {
            return Locale.MILESTONE_REACHED;
        }
        return Locale.MILESTONE_NOT_REACHED;
    }

    public void handleRewards(Player player, TrackedItem tracked, int before, int after) {
        String priority = getPriority(tracked.getItemStack().getType());
        Reward reward = getReward(priority);
        if(reward.hasOnceMilestones()) {
            for(int milestone : reward.getOnceMilestones()) {
                if((before < milestone && after > milestone) || milestone == after) {
                    processRewards(player, tracked, reward, milestone, "once");
                }
            }
        }
        if(reward.hasIntervalMilestones()) {
            for(int milestone : reward.getIntervalMilestones()) {
                int x = after / milestone;
                int y = before / milestone;
                for(int i = 0; i < x - y; i++) {
                    processRewards(player, tracked, reward, milestone, "interval");
                }
            }
        }
    }

    private void processRewards(Player player, TrackedItem tracked, Reward reward, int milestone, String rewardType) {

        boolean hasEnchants = reward.hasEnchants(rewardType, milestone);
        boolean hasCommands = reward.hasCommands(rewardType, milestone);

        // Process enchants and build the enchant message
        StringBuilder enchantBuilder = new StringBuilder();
        if (hasEnchants) {
            ItemMeta meta = tracked.getItemMeta();
            for (Map.Entry<Enchantment, Integer> entry : reward.getEnchantments(rewardType, milestone).entrySet()) {
                Enchantment enchantment = entry.getKey();
                int rewardLevel = entry.getValue();

                int currentLevel = meta.getEnchantLevel(enchantment);
                int limit = enchantmentLimits.getOrDefault(enchantment, 0);
                int gainedLevels;

                if (currentLevel + rewardLevel > limit && limit != 0) {
                    if (currentLevel >= limit) {
                        continue;
                    }
                    int remainingSpace = limit - currentLevel;
                    gainedLevels = Math.min(remainingSpace, rewardLevel);
                } else {
                    gainedLevels = rewardLevel;
                }

                if (gainedLevels > 0) {
                    enchantBuilder.append("\n").append(Locale.MILESTONE_REWARD_ENTRY
                            .replace("{0}", String.valueOf(gainedLevels))
                            .replace("{1}", Methods.convertEnchant(enchantment.getKey().getKey()))
                            .replace("_", " ").toUpperCase());
                    meta.addEnchant(enchantment, currentLevel + gainedLevels, true);
                }
            }
            tracked.setItemMeta(meta);
        }

        // Initial message
        String m = rewardType.equals("interval") ? Locale.MILESTONE_ACHIEVED_INTERVAL : Locale.MILESTONE_ACHIEVED_ONCE;

        if (hasCommands || enchantBuilder.length() > 0) {
            player.sendMessage(Locale.parse(m)
                    .replace("{0}", String.valueOf(milestone))
                    .replace("{1}", getObjectiveType(tracked.getItemStack())));

            if (enchantBuilder.length() > 0) {
                List<String> message = Locale.MILESTONE_REWARD_LIST;
                for (String s : message) {
                    player.sendMessage(Locale.parse(s.replace("{0}", enchantBuilder.toString())));
                }
            }
        }

        // Process commands
        // Looks a bit weird being placed here, but its to ensure the command is sent after the message is sent.
        if (hasCommands) {
            for (String command : reward.getCommands(rewardType, milestone)) {
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("%player%", player.getName()));
            }
        }
    }


    /**
     *
     * @return
     */
    public HashMap<String, Reward> getRewards() {
        return rewards;
    }

    /**
     *
     * @param identifier The identifier of the reward.
     * @return
     */
    public Reward getReward(String identifier) {
        return rewards.get(identifier);
    }

}
