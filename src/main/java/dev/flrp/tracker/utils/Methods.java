package dev.flrp.tracker.utils;

import dev.flrp.tracker.Tracker;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Methods {

    private static final Tracker instance = Tracker.getInstance();

    public static String getProperName(Material material) {
        String item = material.toString();
        item = item.toLowerCase().replaceAll("_", " ");

        char[] charList = item.toCharArray();
        charList[0] = Character.toUpperCase(charList[0]);
        for(int i = 0; i < charList.length; i++) {
            if(charList[i] == ' ') {
                charList[i+1] = Character.toUpperCase(charList[i+1]);
            }
        }
        return String.valueOf(charList);
    }


    public static Enchantment getItemEnchantment(String string) {
        if(extractVersion(instance.getServer().getVersion()).matches("MC: 1\\.(8\\.[0-9]+|9\\.[0-9]+|10\\.[0-9]+|11\\.[0-9]+)")) {
            return Enchantment.getByName(string);
        } else {
            return Enchantment.getByKey(NamespacedKey.minecraft(string));
        }
    }

    public static ItemStack itemInHand(Player player) {
        if(instance.getServer().getVersion().contains("1.8")) {
            return player.getItemInHand();
        } else {
            return player.getInventory().getItemInMainHand();
        }
    }

    public static String convertEnchant(String enchant) {
        String enchantment = enchant.toUpperCase();
        switch(enchantment) {
            case "ARROW_DAMAGE":
                return "POWER";
            case "ARROW_FIRE":
                return "FLAME";
            case "ARROW_INFINITE":
                return "INFINITY";
            case "ARROW_KNOCKBACK":
                return "PUNCH";
            case "DAMAGE_ALL":
                return "SHARPNESS";
            case "DAMAGE_ARTHROPODS":
                return "BANE OF ARTHROPODS";
            case "DAMAGE_UNDEAD":
                return "SMITE";
            case "DIG_SPEED":
                return "EFFICIENCY";
            case "DURABILITY":
                return "UNBREAKING";
            case "LOOT_BONUS_BLOCKS":
                return "FORTUNE";
            case "LOOT_BONUS_MOBS":
                return "LOOTING";
            case "OXYGEN":
                return "RESPIRATION";
            case "PROTECTION_ENVIRONMENTAL":
                return "PROTECTION";
            case "PROTECTION_EXPLOSIONS":
                return "BLAST PROTECTION";
            case "PROTECTION_FALL":
                return "FEATHER FALLING";
            case "PROTECTION_FIRE":
                return "FIRE PROTECTION";
            case "PROTECTION_PROJECTILE":
                return "PROJECTILE PROTECTION";
            case "WATER_WORKER":
                return "AQUA AFFINITY";
            case "BINDING_CURSE":
                return "CURSE OF BINDING";
            case "VANISHING_CURSE":
                return "CURSE OF VANISHING";
            default:
                return enchantment;
        }
    }

    private static String extractVersion(String fullVersion) {
        int startIndex = fullVersion.indexOf('(');
        int endIndex = fullVersion.indexOf(')');

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return fullVersion.substring(startIndex + 1, endIndex);
        } else {
            return "unknown";
        }
    }


}
