package me.gethertv.szaman.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.gethertv.szaman.Szaman;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StatsPoints extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "szaman";
    }

    @Override
    public @NotNull String getAuthor() {
        return "gethertv";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        if (offlinePlayer.getPlayer() == null) return null;
        Player player = offlinePlayer.getPlayer();
        if(identifier.equalsIgnoreCase("points"))
            return String.valueOf(Szaman.getInstance().getUserData().get(player.getUniqueId()).getPoints());


        return null;
    }
}
