package me.gethertv.szaman.listeners;

import me.gethertv.szaman.Szaman;
import me.gethertv.szaman.data.User;
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

                    Integer integer = Szaman.getInstance().getHealthCost().get(user.getHealthLevel() + 1);
                    if (integer == null) {
                        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.max-level")));
                        player.closeInventory();
                        return;
                    }
                    if (!hasPoints(user.getPoints(), integer)) {
                        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.no-points")));
                        player.closeInventory();
                        return;
                    }
                    user.setPoints(user.getPoints() - integer);
                    user.setHealthLevel(user.getHealthLevel() + 1);
                    player.closeInventory();
                    player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.success-upgrade")));
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(user.getHealthLevel() * 1 + 20);
                    return;
                }

                // SPEED ITEM
                if (event.getSlot() == config.getInt("speed.slot")) {
                    if (!config.getBoolean("speed.enable"))
                        return;

                    Integer integer = Szaman.getInstance().getSpeedCost().get(user.getSpeedLevel() + 1);
                    if (integer == null) {
                        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.max-level")));
                        player.closeInventory();
                        return;
                    }
                    if (!hasPoints(user.getPoints(), integer)) {
                        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.no-points")));
                        player.closeInventory();
                        return;
                    }
                    user.setPoints(user.getPoints() - integer);
                    user.setSpeedLevel(user.getSpeedLevel() + 1);
                    player.closeInventory();
                    player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.success-upgrade")));
                    player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue((user.getSpeedLevel() * 0.05 * 0.1) + 0.1);
                    return;
                }

                // STRENGTH ITEM
                if (event.getSlot() == config.getInt("strength.slot")) {
                    if (!config.getBoolean("strength.enable"))
                        return;

                    Integer integer = Szaman.getInstance().getStrengthCost().get(user.getStrengthLevel() + 1);
                    if (integer == null) {
                        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.max-level")));
                        player.closeInventory();
                        return;
                    }
                    if (!hasPoints(user.getPoints(), integer)) {
                        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.no-points")));
                        player.closeInventory();
                        return;
                    }
                    user.setPoints(user.getPoints() - integer);
                    user.setStrengthLevel(user.getStrengthLevel() + 1);
                    player.closeInventory();
                    player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.success-upgrade")));
                    player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + (user.getStrengthLevel() * 2));
                    return;
                }

                // VAMPIRISM ITEM
                if (event.getSlot() == config.getInt("vampirism.slot")) {
                    if (!config.getBoolean("vampirism.enable"))
                        return;

                    Integer integer = Szaman.getInstance().getVampirismCost().get(user.getVampirismLevel() + 1);
                    if (integer == null) {
                        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.max-level")));
                        player.closeInventory();
                        return;
                    }
                    if (!hasPoints(user.getPoints(), integer)) {
                        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.no-points")));
                        player.closeInventory();
                        return;
                    }
                    user.setPoints(user.getPoints() - integer);
                    user.setVampirismLevel(user.getVampirismLevel() + 1);
                    player.closeInventory();
                    player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.success-upgrade")));
                    return;
                }

                // BOOSTDROP ITEM
                if (event.getSlot() == config.getInt("boostdrop.slot")) {
                    if (!config.getBoolean("boostdrop.enable"))
                        return;

                    Integer integer = Szaman.getInstance().getSpeedCost().get(user.getDropBoostLevel() + 1);
                    if (integer == null) {
                        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.max-level")));
                        player.closeInventory();
                        return;
                    }
                    if (!hasPoints(user.getPoints(), integer)) {
                        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.no-points")));
                        player.closeInventory();
                        return;
                    }
                    user.setPoints(user.getPoints() - integer);
                    user.setDropBoostLevel(user.getDropBoostLevel() + 1);
                    player.closeInventory();
                    player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.success-upgrade")));
                    return;
                }

                // CONFINEMENT ITEM
                if (event.getSlot() == config.getInt("confinement.slot")) {
                    if (!config.getBoolean("confinement.enable"))
                        return;

                    Integer integer = Szaman.getInstance().getConfinementCost().get(user.getConfinementLevel() + 1);
                    if (integer == null) {
                        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.max-level")));
                        player.closeInventory();
                        return;
                    }
                    if (!hasPoints(user.getPoints(), integer)) {
                        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.no-points")));
                        player.closeInventory();
                        return;
                    }
                    user.setPoints(user.getPoints() - integer);
                    user.setConfinementLevel(user.getConfinementLevel() + 1);
                    player.closeInventory();
                    player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.success-upgrade")));
                    return;
                }

                // FIREWORK NONLIMIT
                if (event.getSlot() == config.getInt("nolimit-firework.slot")) {
                    if (!config.getBoolean("nolimit-firework.enable"))
                        return;

                    Integer integer = config.getInt("nolimit-firework.cost");
                    if (!hasPoints(user.getPoints(), integer)) {
                        player.sendTitle(ColorFixer.addColors("&7"), ColorFixer.addColors(config.getString("lang.no-points")));
                        player.closeInventory();
                        return;
                    }
                    user.setPoints(user.getPoints() - integer);
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
