package dev.flrp.tracker.item;

import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.flrp.tracker.Tracker;
import dev.flrp.tracker.configuration.Locale;
import dev.flrp.tracker.configuration.Settings;
import dev.flrp.tracker.utils.Methods;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;

public class TrackedItem {

    private final ItemStack item;
    private final NBTItem nbti;

    public TrackedItem(ItemStack item) {
        this.item = item;
        this.nbti = new NBTItem(item);
    }

    public ItemStack getItemStack() {
        return item;
    }

    public NBTItem getNBTItem() {
        return nbti;
    }

    public ItemMeta getItemMeta() {
        return nbti.getItem().getItemMeta();
    }

    public boolean hasTrackerData() {
        return nbti.hasKey("TrackerAmount");
    }

    public boolean hasTrackerModule() {
        return nbti.hasKey("TrackerModule");
    }

    public void setTrackerAmount(int amount) {
        nbti.setInteger("TrackerAmount", amount);
    }

    public int getTrackerAmount() {
        return nbti.getInteger("TrackerAmount");
    }

    public void setTrackerModule() {
        nbti.setBoolean("TrackerModule", true);
    }

    public boolean getTrackerModule() {
        return nbti.getBoolean("TrackerModule");
    }

    public void setItemMeta(ItemMeta meta) {
        nbti.getItem().setItemMeta(meta);
    }

    public void setItemMeta() {
        nbti.getItem().setItemMeta(getItemMeta());
    }

    /**
     * Updates the tracker tag.
     * @param increase The increase of the tracker.
     */
    public void updateTag(int increase) {
        ItemMeta meta = getItemMeta();
        String name = meta.getDisplayName();
        String extra = name.substring(0, name.lastIndexOf(Locale.parse(Settings.TAG_HEAD)));
        if(Tracker.getInstance().getConfig().getBoolean("counter.format.enabled")) {
            meta.setDisplayName(extra + Locale.parse(Settings.TAG_HEAD) + formatTracker(increase) + Locale.parse(Settings.TAG_TAIL));
        } else {
            meta.setDisplayName(extra + Locale.parse(Settings.TAG_HEAD) + increase + Locale.parse(Settings.TAG_TAIL));
        }
        setItemMeta(meta);
    }

    /**
     * Adds a counter to the display name.
     * @param increase The increase of the tracker.
     */
    public void addTag(int increase) {
        ItemMeta meta = getItemMeta();
        String name;
        if(meta.hasDisplayName()) {
            name = getItemMeta().getDisplayName();
        } else {
            name = Methods.getProperName(getItemStack().getType());
        }
        meta.setDisplayName(Locale.parse("&f" + name + " " + Locale.parse(Settings.TAG_HEAD) + increase + Locale.parse(Settings.TAG_TAIL)));
        setItemMeta(meta);
    }

    /**
     * Checks if the item has a tracker tag.
     * @return Returns true if it finds a tracker.
     */
    public boolean hasTrackerTag() {
        ItemMeta meta = getItemMeta();

        if (!meta.hasDisplayName()) {
            return false;
        }

        String name = meta.getDisplayName();
        String tagHead = Locale.parse(Settings.TAG_HEAD);
        String tagTail = Locale.parse(Settings.TAG_TAIL);

        if (!name.contains(tagHead) || !name.contains(tagTail)) {
            return false;
        }

        int headIndex = name.lastIndexOf(tagHead);
        int tailIndex = name.lastIndexOf(tagTail);

        if (headIndex >= tailIndex) {
            return false;
        }

        String counter = name.substring(headIndex + tagHead.length(), tailIndex);
        String currentNumber = String.valueOf(getTrackerAmount());

        return counter.equals(currentNumber) || counter.equals(formatTracker(Double.parseDouble(currentNumber)));
    }

    /**
     *
     * @param increase The increase of the tracker.
     */
    public void applyTracker(int increase) {
        boolean hasData = hasTrackerData();
        boolean hasTracker = hasTrackerTag();
        if(hasData) {
            if(hasTracker) {
                setTrackerAmount(getTrackerAmount() + increase);
                updateTag(getTrackerAmount());
                return;
            }
            setTrackerAmount(getTrackerAmount() + increase);
            addTag(getTrackerAmount());
        } else {
            setTrackerAmount(increase);
            addTag(increase);
        }
    }

    public String formatTracker(double number) {
        String[] suffixes = {"", "k", "m", "b", "t", "q", "qi"};

        if (number == 0) {
            return "0";
        }

        int magnitude = (int) Math.log10(Math.abs(number));
        int index = Math.max(0, magnitude / 3);

        double roundedNumber = number / Math.pow(10, index * 3);
        String suffix = suffixes[index];

        String pattern = (magnitude > 15) ? "0.00" : "#,##0.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);

        return decimalFormat.format(roundedNumber) + suffix;
    }

}
