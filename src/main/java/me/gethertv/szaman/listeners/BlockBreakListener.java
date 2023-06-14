package me.gethertv.szaman.listeners;

import me.gethertv.szaman.Szaman;
import me.gethertv.szaman.data.PerkType;
import me.gethertv.szaman.data.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
        Block block = event.getBlock();
        if(plugin.getBoostMaterial().contains(event.getBlock().getType()))
        {
            User user = Szaman.getInstance().getUserData().get(player.getUniqueId());
            if(user==null)
                return;

            int level = user.getLevel(PerkType.BOOSTDROP);
            if(level<=0)
                return;

            List<ItemStack> items = new ArrayList<>();
            items.addAll(block.getDrops(player.getItemInHand()));

            event.setDropItems(false);


            double multiply = plugin.getPerkData().get(PerkType.BOOSTDROP).getPerk(level).getValue();

            Location loc = event.getBlock().getLocation();
            items.forEach(item -> {
                double amount = item.getAmount()*multiply;
                item.setAmount((int) amount);
                loc.getWorld().dropItemNaturally(loc, item);
            });
        }
    }

    public int getAmount(int level) {
        int amount = 1;
        Random random = new Random(System.currentTimeMillis());
        //double min = (double) 1/(level+2)  + (double) (level+1)/2;

        double chance = 1 -( (double) 2/(level+2));


        for (int x = 0 ; x < level ; x++) {
            double winTicket = random.nextDouble();
            if (winTicket <= chance) {
                amount++;
            }
        }
        return amount;
    }
}
