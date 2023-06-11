package me.gethertv.szaman.listeners;

import me.gethertv.szaman.Szaman;
import me.gethertv.szaman.data.PerkType;
import me.gethertv.szaman.data.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BlockBreakListener implements Listener {

    private final Szaman plugin;

    public BlockBreakListener(Szaman plugin)
    {
        this.plugin = plugin;
    }
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        if(plugin.getBoostMaterial().contains(event.getBlock().getType()))
        {
            List<ItemStack> items = new ArrayList<>();
            items.addAll(event.getBlock().getDrops());

            event.setDropItems(false);

            User user = Szaman.getInstance().getUserData().get(player.getUniqueId());
            if(user==null)
                return;

            int level = user.getLevel(PerkType.BOOSTDROP);
            if(level<=0)
                return;

            double multiply = plugin.getPerkData().get(PerkType.BOOSTDROP).getPerk(level).getValue();

            Location loc = event.getBlock().getLocation();
            items.forEach(item -> {
                double amount = item.getAmount()*multiply;
                item.setAmount((int) amount);
                loc.getWorld().dropItemNaturally(loc, item);
            });
        }
    }
}
