package me.gethertv.szaman.storage;

import org.bukkit.entity.Player;

public abstract class DatabaseManager {


    public String table = "get_szaman";

    public abstract void loadUser(Player player);
    public abstract void updateUser(Player player);

    public abstract boolean isConnected();




}
