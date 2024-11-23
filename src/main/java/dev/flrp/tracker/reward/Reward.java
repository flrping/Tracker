package dev.flrp.tracker.reward;

import dev.flrp.tracker.Tracker;
import dev.flrp.tracker.configuration.Locale;
import dev.flrp.tracker.utils.Methods;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Reward {

    private final Tracker instance = Tracker.getInstance();

    private final String identifier;
    private final List<Integer> onceMilestones = new ArrayList<>();
    private final List<Integer> intervalMilestones = new ArrayList<>();

    public Reward(String identifier) {
        this.identifier = identifier;
        handleMilestoneAmounts("once", onceMilestones);
        handleMilestoneAmounts("interval", intervalMilestones);
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<Integer> getOnceMilestones() {
        return onceMilestones;
    }

    public List<Integer> getIntervalMilestones() {
        return intervalMilestones;
    }

    public List<String> getCommands(String rewardType, int milestone) {
        return instance.getMilestones().getConfiguration().getStringList("milestones." + identifier + "." + rewardType + "." + milestone+ ".commands");
    }

    public HashMap<Enchantment, Integer> getEnchantments(String rewardType, int milestone) {
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        for(String s : instance.getMilestones().getConfiguration().getStringList("milestones." + identifier + "." + rewardType + "." + milestone + ".enchants")) {
            try {
                Enchantment enchantment = Methods.getItemEnchantment(s.substring(0, s.indexOf(':')));
                int x = Integer.parseInt(s.substring(s.indexOf(':') + 1));
                enchantments.put(enchantment, x);
            } catch (Exception e) {
                Locale.log("Can't identify the enchant '" + s.substring(0, s.indexOf(':')) + "'. Please make sure you're using the correct enchantment names for your version.");
            }
        }
        return enchantments;
    }

    public boolean hasCommands(String rewardType, int milestone) {
        return !getCommands(rewardType, milestone).isEmpty();
    }

    public boolean hasEnchants(String rewardType, int milestone) {
        return !instance.getMilestones().getConfiguration().getStringList("milestones." + identifier + "." + rewardType + "." + milestone + ".enchants").isEmpty();
    }

    public boolean hasOnceMilestones() {
        return !onceMilestones.isEmpty();
    }

    public boolean hasIntervalMilestones() {
        return !intervalMilestones.isEmpty();
    }

    // Helpers
    private void handleMilestoneAmounts(String rewardType, List<Integer> list) {
        if(instance.getMilestones().getConfiguration().isSet("milestones." + identifier + "." + rewardType)) {
            Set<String> set = instance.getMilestones().getConfiguration().getConfigurationSection("milestones." + identifier + "." + rewardType).getKeys(false);
            for(String x : set) list.add(Integer.parseInt(x));
        }
    }

}
