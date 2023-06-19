package me.gethertv.szaman.listeners;

import me.gethertv.szaman.Szaman;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ConnectUser implements Listener {


    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {

        new BukkitRunnable() {
            @Override
            public void run() {
                Szaman.getInstance().getDatabaseManager().loadUser(event.getPlayer());
            }
        }.runTaskAsynchronously(Szaman.getInstance());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                Szaman.getInstance().getDatabaseManager().updateUser(player);
                Szaman.getInstance().getUserData().remove(player.getUniqueId());
            }
        }.runTaskAsynchronously(Szaman.getInstance());
    }
}
