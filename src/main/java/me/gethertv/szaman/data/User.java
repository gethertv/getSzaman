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

import java.text.DecimalFormat;
import java.util.*;

public class User {

    private Player player;
    private int points;

    private HashMap<PerkType, Integer> levelPerks;

    private Inventory inventory;

    private HashMap<UUID, Long> lastTimeKill;
    private DecimalFormat decimalFormat = new DecimalFormat("#.#");

    public User(Player player, int points, int healthLevel, int speedLevel, int strengthLevel, int vampirismLevel, int dropBoostLevel, int confinementLevel, HashMap<UUID, Long> lastTimeKill) {
        this.levelPerks = new HashMap<>();
        this.player = player;
        this.points = points;


        levelPerks.put(PerkType.HEALTH, healthLevel);
        levelPerks.put(PerkType.SPEED, speedLevel);
        levelPerks.put(PerkType.STRENGTH, strengthLevel);
        levelPerks.put(PerkType.VAMPIRISM, vampirismLevel);
        levelPerks.put(PerkType.BOOSTDROP, dropBoostLevel);
        levelPerks.put(PerkType.CONFINEMENT, confinementLevel);

        this.lastTimeKill = lastTimeKill;
    }

    public User(Player player) {
        this.levelPerks = new HashMap<>();
        this.player = player;
        this.points = 0;
        levelPerks.put(PerkType.HEALTH, 0);
        levelPerks.put(PerkType.SPEED, 0);
        levelPerks.put(PerkType.STRENGTH, 0);
        levelPerks.put(PerkType.VAMPIRISM, 0);
        levelPerks.put(PerkType.BOOSTDROP, 0);
        levelPerks.put(PerkType.CONFINEMENT, 0);
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




        if (config.getBoolean("health.enable")) {
            int level = getLevel(PerkType.HEALTH);
            Perk nextPerk = Szaman.getInstance().getPerkData().get(PerkType.HEALTH).getPerk(level+1);
            loadPerk(PerkType.HEALTH, level, nextPerk!=null ? nextPerk.getCost() : -1);
        }
        if (config.getBoolean("speed.enable"))
        {
            int level = getLevel(PerkType.SPEED);
            Perk nextPerk = Szaman.getInstance().getPerkData().get(PerkType.SPEED).getPerk(level+1);
            loadPerk(PerkType.SPEED, level , nextPerk!=null ? nextPerk.getCost() : -1);
        }
        if (config.getBoolean("strength.enable"))
        {
            int level = getLevel(PerkType.STRENGTH);
            Perk nextPerk = Szaman.getInstance().getPerkData().get(PerkType.STRENGTH).getPerk(level+1);
            loadPerk(PerkType.STRENGTH, level , nextPerk!=null ? nextPerk.getCost() : -1);
        }
        if (config.getBoolean("vampirism.enable"))
        {
            int level = getLevel(PerkType.VAMPIRISM);
            Perk nextPerk = Szaman.getInstance().getPerkData().get(PerkType.VAMPIRISM).getPerk(level+1);
            loadPerk(PerkType.VAMPIRISM, level , nextPerk!=null ? nextPerk.getCost() : -1);
        }
        if (config.getBoolean("boostdrop.enable"))
        {
            int level = getLevel(PerkType.BOOSTDROP);
            Perk nextPerk = Szaman.getInstance().getPerkData().get(PerkType.BOOSTDROP).getPerk(level+1);
            loadPerk(PerkType.BOOSTDROP, level , nextPerk!=null ? nextPerk.getCost() : -1);
        }
        if (config.getBoolean("confinement.enable"))
        {
            int level = getLevel(PerkType.CONFINEMENT);
            Perk nextPerk = Szaman.getInstance().getPerkData().get(PerkType.CONFINEMENT).getPerk(level+1);
            loadPerk(PerkType.CONFINEMENT, level , nextPerk!=null ? nextPerk.getCost() : -1);
        }
        if (config.getBoolean("nolimit-firework.enable"))
        {
            loadPerkFirework("nolimit-firework", config.getInt("nolimit-firework.cost"));
        }


    }

    private void loadPerkFirework(String name, int price) {

        FileConfiguration config = Szaman.getInstance().getConfig();


        ItemStack itemStack = new ItemStack(Material.valueOf(config.getString( name+ ".material").toUpperCase()));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ColorFixer.addColors(config.getString(name + ".displayname")));
        List<String> lore = new ArrayList<>();
        lore.addAll(config.getStringList(name + ".lore"));


        List<String> infoLore = new ArrayList<>();

        itemMeta.setLore(ColorFixer.addLorePerks(
                lore,
                price,
                infoLore));

        itemStack.setItemMeta(itemMeta);

        inventory.setItem(config.getInt(name + ".slot"), itemStack);

    }

    public void loadPerk(PerkType type, int userLevel, int price) {

        FileConfiguration config = Szaman.getInstance().getConfig();
        String name = type.name().toLowerCase();


        PerkManager perkManager = Szaman.getInstance().getPerkData().get(type);


        ItemStack itemStack = new ItemStack(Material.valueOf(config.getString( name+ ".material").toUpperCase()));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ColorFixer.addColors(config.getString(name + ".displayname")));
        List<String> lore = new ArrayList<>();
        lore.addAll(config.getStringList(name + ".lore"));

        if(price>0)
            lore.add(config.getString("extra-line-buy"));


        List<String> infoLore = new ArrayList<>();


        for (Map.Entry<Integer, Perk> entry : perkManager.getPerkLevels().entrySet()) {
            Integer level = entry.getKey();
            Perk perk = entry.getValue();
            String active = config.getString(name+".format.active");
            String noActive = config.getString(name+".format.no-active");
            if(level<=userLevel)
                infoLore.add(active
                        .replace("{level}", String.valueOf(level))
                        .replace("{value}", (type==PerkType.HEALTH ? decimalFormat.format(perk.getValue()/2) : decimalFormat.format(perk.getValue())))
                );

            else
                infoLore.add(noActive
                        .replace("{level}", String.valueOf(level))
                        .replace("{value}", (type==PerkType.HEALTH ? decimalFormat.format(perk.getValue()/2) : decimalFormat.format(perk.getValue())))
                );
        }


        itemMeta.setLore(ColorFixer.addLorePerks(
                lore,
                price,
                infoLore));

        itemStack.setItemMeta(itemMeta);

        inventory.setItem(config.getInt(name + ".slot"), itemStack);


    }

    private void fillBackground() {
        FileConfiguration config = Szaman.getInstance().getConfig();
        for(String key : config.getConfigurationSection("background").getKeys(false))
        {
            ItemStack itemStack = new ItemStack(Material.valueOf(config.getString("background."+key+".material")));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ColorFixer.addColors(config.getString("background."+key+".displayname")));
            List<String> lore = new ArrayList<>();
            lore.addAll(config.getStringList("background."+key+".lore"));

            itemMeta.setLore(ColorFixer.addColors(lore));

            itemStack.setItemMeta(itemMeta);

            List<Integer> slots = new ArrayList<>();
            slots.addAll(config.getIntegerList("background."+key+".slots"));

            for (int slot : slots) {
                inventory.setItem(slot, itemStack);
            }
        }

    }

    public void setLevelPerk(PerkType perkType, int level)
    {
        levelPerks.put(perkType, level);
    }

    public int getLevel(PerkType perkType)
    {
      return levelPerks.get(perkType);
    }
    public void upgradeLevel(PerkType perkType)
    {
        levelPerks.put(perkType, levelPerks.get(perkType)+1);
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

}
