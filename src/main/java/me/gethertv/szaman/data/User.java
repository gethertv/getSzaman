package me.gethertv.szaman.data;

import me.gethertv.szaman.Szaman;
import me.gethertv.szaman.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class User {

    private Player player;
    private int points;
    private int healthLevel;
    private int speedLevel;
    private int strengthLevel;
    private int vampirismLevel;
    private int dropBoostLevel;
    private int confinementLevel;

    private Inventory inventory;

    private HashMap<UUID, Long> lastTimeKill;

    public User(Player player, int points, int healthLevel, int speedLevel, int strengthLevel, int vampirismLevel, int dropBoostLevel, int confinementLevel, HashMap<UUID, Long> lastTimeKill) {
        this.player = player;
        this.points = points;
        this.healthLevel = healthLevel;
        this.speedLevel = speedLevel;
        this.strengthLevel = strengthLevel;
        this.vampirismLevel = vampirismLevel;
        this.dropBoostLevel = dropBoostLevel;
        this.confinementLevel = confinementLevel;
        this.lastTimeKill = lastTimeKill;
    }

    public User(Player player) {
        this.player = player;
        this.points = 0;
        this.healthLevel = 0;
        this.speedLevel = 0;
        this.strengthLevel = 0;
        this.vampirismLevel = 0;
        this.dropBoostLevel = 0;
        this.lastTimeKill = new HashMap<>();
    }

    public void openInv() {
        if (inventory == null)
            inventory = Bukkit.createInventory(null, Szaman.getInstance().getConfig().getInt("inv.size"), ColorFixer.addColors(Szaman.getInstance().getConfig().getString("inv.title")));

        inventory.clear();

        fillBackground();

        loadPerks();

        player.openInventory(inventory);
    }

    private void loadPerks() {
        Szaman plugin = Szaman.getInstance();

        FileConfiguration config = Szaman.getInstance().getConfig();

        if (config.getBoolean("health.enable"))
            loadPerk("health", healthLevel, (plugin.getHealthCost().get(healthLevel + 1) != null) ? plugin.getHealthCost().get(healthLevel + 1) : 0);
        if (config.getBoolean("speed.enable"))
            loadPerk("speed", speedLevel, (plugin.getSpeedCost().get(speedLevel + 1) != null) ? plugin.getSpeedCost().get(speedLevel + 1) : 0);
        if (config.getBoolean("strength.enable"))
            loadPerk("strength", strengthLevel, (plugin.getStrengthCost().get(strengthLevel + 1) != null) ? plugin.getStrengthCost().get(strengthLevel + 1) : 0);
        if (config.getBoolean("vampirism.enable"))
            loadPerk("vampirism", vampirismLevel, (plugin.getVampirismCost().get(vampirismLevel + 1) != null) ? plugin.getVampirismCost().get(vampirismLevel + 1) : 0);
        if (config.getBoolean("boostdrop.enable"))
            loadPerk("boostdrop", dropBoostLevel, (plugin.getBoostDropCost().get(dropBoostLevel + 1) != null) ? plugin.getBoostDropCost().get(dropBoostLevel + 1) : 0);
        if (config.getBoolean("confinement.enable"))
            loadPerk("confinement", confinementLevel, (plugin.getConfinementCost().get(confinementLevel + 1) != null) ? plugin.getConfinementCost().get(confinementLevel + 1) : 0);
        if (config.getBoolean("nolimit-firework.enable"))
            loadPerk("nolimit-firework", 0, config.getInt("nolimit-firework.cost"));

    }

    public void loadPerk(String name, int actuallyLevel, int price) {
        FileConfiguration config = Szaman.getInstance().getConfig();
        {
            ItemStack itemStack = new ItemStack(Material.valueOf(config.getString(name + ".material").toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ColorFixer.addColors(config.getString(name + ".displayname")));
            List<String> lore = new ArrayList<>();
            lore.addAll(config.getStringList(name + ".lore"));
            itemMeta.setLore(ColorFixer.addLorePerks(
                    lore,
                    actuallyLevel,
                    price));

            itemStack.setItemMeta(itemMeta);

            inventory.setItem(config.getInt(name + ".slot"), itemStack);
        }
    }

    private void fillBackground() {
        ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ColorFixer.addColors("&7 "));
        itemStack.setItemMeta(itemMeta);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, itemStack);
        }
    }

    public HashMap<UUID, Long> getLastTimeKill() {
        return lastTimeKill;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getHealthLevel() {
        return healthLevel;
    }

    public void setHealthLevel(int healthLevel) {
        this.healthLevel = healthLevel;
    }

    public int getSpeedLevel() {
        return speedLevel;
    }

    public void setSpeedLevel(int speedLevel) {
        this.speedLevel = speedLevel;
    }

    public int getStrengthLevel() {
        return strengthLevel;
    }

    public void setStrengthLevel(int strengthLevel) {
        this.strengthLevel = strengthLevel;
    }

    public int getVampirismLevel() {
        return vampirismLevel;
    }

    public void setVampirismLevel(int vampirismLevel) {
        this.vampirismLevel = vampirismLevel;
    }

    public int getDropBoostLevel() {
        return dropBoostLevel;
    }

    public void setDropBoostLevel(int dropBoostLevel) {
        this.dropBoostLevel = dropBoostLevel;
    }

    public int getConfinementLevel() {
        return confinementLevel;
    }

    public void setConfinementLevel(int confinementLevel) {
        this.confinementLevel = confinementLevel;
    }
}
