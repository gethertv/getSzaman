package me.gethertv.szaman.data;

import me.gethertv.szaman.Szaman;
import me.gethertv.szaman.type.SellType;
import me.gethertv.szaman.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PerkManager {

    private PerkType perkType;
    private HashMap<Integer, Perk> perkLevels = new HashMap<>();

    public PerkManager(PerkType perkType, Szaman plugin)
    {
        this.perkType = perkType;
        perkLevels.clear();
        String name = perkType.name().toLowerCase();
        for(String levelKey : plugin.getConfig().getConfigurationSection("data."+name).getKeys(false))
        {
            int level = Integer.parseInt(levelKey);
            int cost = plugin.getConfig().getInt("data."+name+"."+level+".cost");
            double value = plugin.getConfig().getDouble("data."+name+"."+level+".value");
            perkLevels.put(level, new Perk(perkType, cost, value));
        }

    }

    public void upgrade(Player player, PerkType perkType)
    {
        FileConfiguration config = Szaman.getInstance().getConfig();
        User user = Szaman.getInstance().getUserData().get(player.getUniqueId());
        if(user==null)
            return;

        Perk perk = perkLevels.get(user.getLevel(perkType) + 1);
        if (perk == null) {
            player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.max-level")));
            player.closeInventory();
            return;
        }
        if(Szaman.SELL_TYPE== SellType.COINS) {
            if (!hasPoints(user.getPoints(), perk.getCost())) {
                player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.no-points")));
                player.closeInventory();
                return;
            }
            user.setPoints(user.getPoints() - perk.getCost());

        }
        if(Szaman.SELL_TYPE==SellType.ITEM)
        {
            if(Szaman.ITEM_ODLAMEK==null)
            {
                player.sendMessage(ColorFixer.addColors("&cBlad! Ustaw /aszaman setodlamek"));
                return;
            }
            int calcItem = calcItem(player, Szaman.ITEM_ODLAMEK);
            if(calcItem<perk.getCost())
            {
                player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.no-points")));
                player.closeInventory();
                return;
            }
            removeItem(player, Szaman.ITEM_ODLAMEK, perk.getCost());

        }
        user.upgradeLevel(perkType);
        player.closeInventory();
        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.success-upgrade")));

        if(perkType==PerkType.HEALTH)
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(perk.getValue() + 20);


        if(perkType==PerkType.SPEED)
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue((perk.getValue() * 0.001) + 0.1);

        if(perkType==PerkType.STRENGTH)
            player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + perk.getValue());

        return;
    }

    public static int calcItem(Player player, ItemStack fromItem)
    {
        int amount = 0;
        for(ItemStack itemStack : player.getInventory())
        {
            if(itemStack==null || itemStack.getType()== Material.AIR)
                continue;

            if(itemStack.isSimilar(fromItem))
                amount+=itemStack.getAmount();
        }

        return amount;
    }

    public static void removeItem(Player player, ItemStack fromItem, int amount)
    {
        int removeItem = amount;


        for(ItemStack itemStack : player.getInventory()) {
            if(itemStack==null || itemStack.getType()== Material.AIR)
                continue;

            if(itemStack.isSimilar(fromItem))
            {
                if(removeItem<=0)
                    break;

                if(itemStack.getAmount()<=removeItem)
                {
                    removeItem-=itemStack.getAmount();
                    itemStack.setAmount(0);
                } else {
                    itemStack.setAmount(itemStack.getAmount()-removeItem);
                    removeItem=0;
                }
            }
        }
    }

    private boolean hasPoints(int has, int need) {
        if (has >= need)
            return true;

        return false;
    }

    public Perk getPerk(int level)
    {
       return perkLevels.get(level);
    }

    public HashMap<Integer, Perk> getPerkLevels() {
        return perkLevels;
    }
}
