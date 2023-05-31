package me.gethertv.szaman.listeners;

import me.gethertv.szaman.Szaman;
import me.gethertv.szaman.utils.ColorFixer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.UUID;

public class InteractListener implements Listener {

    private static HashMap<UUID, Long> fireworkDisable = new HashMap<>();

    private Szaman plugin;
    public InteractListener(Szaman plugin)
    {
        this.plugin = plugin;
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        if(player.getItemInHand().getType()== Material.FIREWORK_ROCKET)
        {
            if(event.getHand()== EquipmentSlot.OFF_HAND)
                return;

            Long time = fireworkDisable.get(player.getUniqueId());
            if(time==null)
                return;


            if(time<System.currentTimeMillis()) {
                fireworkDisable.remove(player.getUniqueId());
                return;
            }

            player.sendMessage(ColorFixer.addColors(plugin.getConfig().getString("lang.confinement-cooldown")));
            event.setCancelled(true);

        }
    }

    public static HashMap<UUID, Long> getFireworkDisable() {
        return fireworkDisable;
    }
}
