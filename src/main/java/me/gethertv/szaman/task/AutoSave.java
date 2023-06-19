package me.gethertv.szaman.task;

import me.gethertv.szaman.Szaman;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSave extends BukkitRunnable {

    @Override
    public void run() {

        new BukkitRunnable() {
            @Override
            public void run() {

                for(Player player : Bukkit.getOnlinePlayers())
                    Szaman.getInstance().getDatabaseManager().updateUser(player);

            }
        }.runTaskAsynchronously(Szaman.getInstance());
    }
}
