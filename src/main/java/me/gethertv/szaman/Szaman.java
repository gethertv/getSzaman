package me.gethertv.szaman;

import me.gethertv.szaman.api.ISzamanApi;
import me.gethertv.szaman.cmd.AdminSzaman;
import me.gethertv.szaman.cmd.SzamanCmd;
import me.gethertv.szaman.data.Perk;
import me.gethertv.szaman.data.PerkManager;
import me.gethertv.szaman.data.PerkType;
import me.gethertv.szaman.data.User;
import me.gethertv.szaman.listeners.*;
import me.gethertv.szaman.placeholder.StatsPoints;
import me.gethertv.szaman.storage.DatabaseManager;
import me.gethertv.szaman.storage.DatabaseType;
import me.gethertv.szaman.storage.Mysql;
import me.gethertv.szaman.storage.SQLite;
import me.gethertv.szaman.task.AutoSave;
import me.gethertv.szaman.type.SellType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class Szaman extends JavaPlugin implements ISzamanApi {

    private static Szaman instance;

    private HashMap<UUID, User> userData = new HashMap<>();
        private List<Material> boostMaterial = new ArrayList<>();

    private double chanceConfinement;

    private HashMap<PerkType, PerkManager> perkData = new HashMap<>();

    public static SellType SELL_TYPE;
    public static ItemStack ITEM_ODLAMEK;

    private DatabaseManager databaseManager;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();

        implementsSql();
        if(!databaseManager.isConnected())
        {
            getLogger().log (Level.WARNING, "Nie można połączyć sie z baza danych!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            (new StatsPoints()).register();

        loadMaterialBoost();
        injectVault();
        /*
            LOAD PERKS COST DATA AND VALUE
         */
        loadPerksData();

        loadUserOnline();

        // events register
        getServer().getPluginManager().registerEvents(new ClickInventory(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new ConnectUser(), this);
        getServer().getPluginManager().registerEvents(new DamageEntity(), this);
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);

        // register command
        getCommand("szaman").setExecutor(new SzamanCmd());
        AdminSzaman adminSzaman = new AdminSzaman();
        getCommand("aszaman").setExecutor(adminSzaman);
        getCommand("aszaman").setTabCompleter(adminSzaman);


        // autosave data user
        new AutoSave().runTaskTimer(this, 20L*300, 20L*300);
    }

    private void injectVault() {
        SELL_TYPE = SellType.valueOf(getConfig().getString("sell-type").toUpperCase());

        if(getConfig().isSet("odlamek"))
        {
            ITEM_ODLAMEK = getConfig().getItemStack("odlamek").clone();
        }
    }

    private void loadMaterialBoost() {
        for(String materialName : getConfig().getStringList("drop-material"))
            boostMaterial.add(Material.valueOf(materialName.toUpperCase()));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            (new StatsPoints()).unregister();

        if(getDatabaseManager()!=null)
        {
            for(Player player : Bukkit.getOnlinePlayers()) {
                getDatabaseManager().updateUser(player);
                player.closeInventory();
            }
        }


        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
    }

    private void loadPerksData() {

        chanceConfinement = getConfig().getDouble("confinement.chance");

        for(String typeString : getConfig().getConfigurationSection("data").getKeys(false))
        {
            PerkType perkType = PerkType.valueOf(typeString.toUpperCase());
            perkData.put(perkType, new PerkManager(perkType, this));
        }
    }

    private void loadUserOnline() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers())
                {
                    getDatabaseManager().loadUser(player);
                }
            }
        }.runTaskAsynchronously(Szaman.getInstance());
    }



    public double getChanceConfinement() {
        return chanceConfinement;
    }

    public static Szaman getInstance() {
        return instance;
    }

    public List<Material> getBoostMaterial() {
        return boostMaterial;
    }

    public HashMap<UUID, User> getUserData() {
        return userData;
    }

    @Override
    public boolean hasCooldown(Player player) {
        Long time = InteractListener.getFireworkDisable().get(player.getUniqueId());
        if(time!=null)
        {
            if(time>=System.currentTimeMillis())
                return true;
        }
        return false;
    }

    @Override
    public double getMultiplyDrop(Player player) {
        User user = getUserData().get(player.getUniqueId());
        if(user!=null)
        {
            int level = user.getLevel(PerkType.BOOSTDROP);
            Perk perk = getPerkData().get(PerkType.BOOSTDROP).getPerk(level);
            if(perk!=null)
            {
                return perk.getValue();
            }
        }
        return 1;
    }

    private void implementsSql() {
        DatabaseType databaseType = DatabaseType.valueOf(getConfig().getString("database"));
        if(databaseType==DatabaseType.MYSQL)
            setupMysql();

        if(databaseType==DatabaseType.SQLITE)
            databaseManager = new SQLite(getConfig().getString("sqlite.name"));

    }
    private void setupMysql() {
        String host = getConfig().getString("mysql.host");
        String username = getConfig().getString("mysql.username");
        String password = getConfig().getString("mysql.password");
        String database = getConfig().getString("mysql.database");
        String port = getConfig().getString("mysql.port");

        boolean ssl = false;
        if (getConfig().get("mysql.ssl") != null) {
            ssl = getConfig().getBoolean("mysql.ssl");
        }

        databaseManager = new Mysql(host, username, password, database, port, ssl);

    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public HashMap<PerkType, PerkManager> getPerkData() {
        return perkData;
    }
}
