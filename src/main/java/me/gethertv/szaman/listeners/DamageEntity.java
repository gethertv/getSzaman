package me.gethertv.szaman.listeners;

import me.gethertv.szaman.Szaman;
import me.gethertv.szaman.data.LastDamage;
import me.gethertv.szaman.data.Perk;
import me.gethertv.szaman.data.PerkType;
import me.gethertv.szaman.data.User;
import me.gethertv.szaman.type.SellType;
import me.gethertv.szaman.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class DamageEntity implements Listener {


    private Random random = new Random();
    private HashMap<UUID, LastDamage> lastHit = new HashMap<>();
    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();

        LastDamage lastDamage = lastHit.get(player.getUniqueId());
        if(lastDamage==null)
            return;

        if(lastDamage.getTime()<System.currentTimeMillis())
        {
            lastHit.remove(player.getUniqueId());
            return;
        }

        lastHit.remove(player.getUniqueId());
        if(!lastDamage.getDamager().isOnline())
            return;

        if(Szaman.SELL_TYPE!=SellType.COINS)
            return;

        User user = Szaman.getInstance().getUserData().get(lastDamage.getDamager().getUniqueId());
        if(user.getLastTimeKill().get(player.getUniqueId())!=null)
        {
            if(System.currentTimeMillis()<user.getLastTimeKill().get(player.getUniqueId()))
                return;
        }
        user.getLastTimeKill().put(player.getUniqueId(), System.currentTimeMillis()+(20L*1000*3600));
        user.setPoints(user.getPoints()+1);

    }

    @EventHandler
    public void onDamge(EntityDamageByEntityEvent event)
    {
        if(!(event.getDamager() instanceof Player))
            return;



        Player player = (Player) event.getDamager();
        User user = Szaman.getInstance().getUserData().get(player.getUniqueId());
        if(event.getEntity() instanceof Player)
        {
            Player victim = (Player) event.getEntity();
            lastHit.put(victim.getUniqueId(), new LastDamage(player, System.currentTimeMillis()+1000L*30));
            int level = user.getLevel(PerkType.CONFINEMENT);
            if(level>0)
            {
                if(runConfinement()) {
                    Perk perk = Szaman.getInstance().getPerkData().get(PerkType.CONFINEMENT).getPerk(level);
                    if(perk==null)
                        return;

                    double cooldown = perk.getValue();

                    double valueTime = 1000 * cooldown;
                    InteractListener.getFireworkDisable().put(victim.getUniqueId(), System.currentTimeMillis() + (int) valueTime);
                    victim.sendMessage(ColorFixer.addColors(Szaman.getInstance().getConfig().getString("lang.get-confinement")));
                }
            }
        }

        int levelVampirism = user.getLevel(PerkType.VAMPIRISM);
        if(levelVampirism<=0)
            return;

        double y = 100.0D;
        double x = 0.0D;

        double chanceWin = Szaman.getInstance().getPerkData().get(PerkType.VAMPIRISM).getPerk(levelVampirism).getValue();
        y -= chanceWin;

        double start = random.nextDouble() * (y - x) + x;
        double finish = start + chanceWin;

        double winTicket = random.nextDouble() * 100.0D + 0.0D;
        if (winTicket >= start && winTicket <= finish) {
            double heal = player.getHealth()+event.getFinalDamage();
            if(heal>player.getMaxHealth())
                player.setHealth(player.getMaxHealth());
            else
                player.setHealth(heal);
        }

    }

    private boolean runConfinement()
    {
        double y = 100.0D;
        double x = 0.0D;

        //Bukkit.broadcastMessage("#CONFINEMENT");
        y -= Szaman.getInstance().getChanceConfinement();

        double start = random.nextDouble() * (y - x) + x;
        double finish = start + Szaman.getInstance().getChanceConfinement();

        double winTicket = random.nextDouble() * 100.0D + 0.0D;
        if (winTicket >= start && winTicket <= finish) {
            return true;
        }

        return false;
    }
}
