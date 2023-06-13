package me.gethertv.szaman.api;

import org.bukkit.entity.Player;

public interface ISzamanApi {

    public boolean hasCooldown(Player player);

    public double getMultiplyDrop(Player player);
}
