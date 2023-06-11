package me.gethertv.szaman.listeners;

import me.gethertv.szaman.Szaman;
import me.gethertv.szaman.data.PerkManager;
import me.gethertv.szaman.data.PerkType;
import me.gethertv.szaman.data.User;
import me.gethertv.szaman.type.SellType;
import me.gethertv.szaman.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickInventory implements Listener {

    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;

        if (event.getWhoClicked() == null)
            return;

        Player player = (Player) event.getWhoClicked();
        User user = Szaman.getInstance().getUserData().get(player.getUniqueId());
        if (event.getInventory().equals(user.getInventory())) {
            event.setCancelled(true);
            if (event.getClickedInventory().equals(event.getInventory())) {
                FileConfiguration config = Szaman.getInstance().getConfig();

                // HEALTH ITEM
                if (event.getSlot() == config.getInt("health.slot")) {
                    if (!config.getBoolean("health.enable"))
                        return;

                    Szaman.getInstance().getPerkData().get(PerkType.HEALTH).upgrade(player, PerkType.HEALTH);
                    return;
                }

                // SPEED ITEM
                if (event.getSlot() == config.getInt("speed.slot")) {
                    if (!config.getBoolean("speed.enable"))
                        return;

                    Szaman.getInstance().getPerkData().get(PerkType.SPEED).upgrade(player, PerkType.SPEED);
                    return;
                }

                // STRENGTH ITEM
                if (event.getSlot() == config.getInt("strength.slot")) {
                    if (!config.getBoolean("strength.enable"))
                        return;

                    Szaman.getInstance().getPerkData().get(PerkType.STRENGTH).upgrade(player, PerkType.STRENGTH);
                    return;
                }

                // VAMPIRISM ITEM
                if (event.getSlot() == config.getInt("vampirism.slot")) {
                    if (!config.getBoolean("vampirism.enable"))
                        return;

                    Szaman.getInstance().getPerkData().get(PerkType.VAMPIRISM).upgrade(player, PerkType.VAMPIRISM);
                    return;
                }

                // BOOSTDROP ITEM
                if (event.getSlot() == config.getInt("boostdrop.slot")) {
                    if (!config.getBoolean("boostdrop.enable"))
                        return;

                    Szaman.getInstance().getPerkData().get(PerkType.BOOSTDROP).upgrade(player, PerkType.BOOSTDROP);
                    return;
                }

                // CONFINEMENT ITEM
                if (event.getSlot() == config.getInt("confinement.slot")) {
                    if (!config.getBoolean("confinement.enable"))
                        return;

                    Szaman.getInstance().getPerkData().get(PerkType.CONFINEMENT).upgrade(player, PerkType.CONFINEMENT);
                    return;
                }

                // FIREWORK NONLIMIT
                if (event.getSlot() == config.getInt("nolimit-firework.slot")) {
                    if (!config.getBoolean("nolimit-firework.enable"))
                        return;

                    Integer integer = config.getInt("nolimit-firework.cost");
                    if(Szaman.SELL_TYPE== SellType.COINS) {
                        if (!hasPoints(user.getPoints(), integer)) {
                            player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.no-points")));
                            player.closeInventory();
                            return;
                        }
                        user.setPoints(user.getPoints() - integer);

                    }
                    if(Szaman.SELL_TYPE==SellType.ITEM)
                    {
                        if(Szaman.ITEM_ODLAMEK==null)
                        {
                            player.sendMessage(ColorFixer.addColors("&cBlad! Ustaw /aszaman setodlamek"));
                            return;
                        }
                        int calcItem = PerkManager.calcItem(player, Szaman.ITEM_ODLAMEK);
                        if(calcItem<integer)
                        {
                            player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.no-points")));
                            player.closeInventory();
                            return;
                        }
                        PerkManager.removeItem(player, Szaman.ITEM_ODLAMEK, integer);

                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            config.getString("nolimit-firework.command")
                                    .replace("{player}", player.getName())
                    );
                    player.closeInventory();
                    player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.success-upgrade")));
                    return;
                }
            }
        }
    }

    private boolean hasPoints(int has, int need) {
        if (has >= need)
            return true;

        return false;
    }
}
