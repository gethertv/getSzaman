package me.gethertv.szaman;

import me.gethertv.szaman.api.ISzamanApi;
import me.gethertv.szaman.cmd.AdminSzaman;
import me.gethertv.szaman.cmd.SzamanCmd;
import me.gethertv.szaman.data.User;
import me.gethertv.szaman.listeners.*;
import me.gethertv.szaman.placeholder.StatsPoints;
import me.gethertv.szaman.storage.Mysql;
import me.gethertv.szaman.task.AutoSave;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
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

    private Mysql sql;

    private HashMap<Integer, Integer> healthCost = new HashMap<>();
    private HashMap<Integer, Integer> speedCost = new HashMap<>();
    private HashMap<Integer, Integer> strengthCost = new HashMap<>();
    private HashMap<Integer, Integer> vampirismCost = new HashMap<>();
    private HashMap<Integer, Integer> boostDropCost = new HashMap<>();
    private HashMap<Integer, Double> vampirismData = new HashMap<>();
    private HashMap<Integer, Integer> confinementCost = new HashMap<>();

    private List<Material> boostMaterial = new ArrayList<>();

    private double chanceConfinement;

    private HashMap<Integer, Double> dropMultiply = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();

        setupSql();
        if (!sql.isConnected()) {
            getLogger().log (Level.WARNING, "Nie można połączyć sie z baza danych!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            (new StatsPoints()).register();

        loadMultiplyDrop();
        loadMaterialBoost();
        /*
            LOAD PERKS COST DATA
         */
        loadPerksData();
        loadVapirismData();

        loadUserOnline();

        // events register
        getServer().getPluginManager().registerEvents(new ClickInventory(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new ConnectUser(), this);
        getServer().getPluginManager().registerEvents(new DamageEntity(), this);
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);

        // register command
        getCommand("szaman").setExecutor(new SzamanCmd());
        getCommand("aszaman").setExecutor(new AdminSzaman());

        // autosave data user
        new AutoSave().runTaskTimer(this, 20L*300, 20L*300);
    }

    private void loadMaterialBoost() {
        for(String materialName : getConfig().getStringList("drop-material"))
            boostMaterial.add(Material.valueOf(materialName.toUpperCase()));
    }

    private void loadMultiplyDrop() {
        for(String level : getConfig().getConfigurationSection("multiply-drop").getKeys(false))
        {
            int levelDrop = Integer.parseInt(level);
            double multiply = getConfig().getDouble("multiply-drop."+level);
            dropMultiply.put(levelDrop, multiply);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            (new StatsPoints()).unregister();

        if(sql!=null)
        {
            for(Player player : Bukkit.getOnlinePlayers()) {
                sql.updatePlayer(player);
                player.closeInventory();
            }
        }


        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
    }

    private void loadVapirismData() {
        for(String level : getConfig().getConfigurationSection("vampirism-chance").getKeys(false))
        {
            vampirismData.put(Integer.parseInt(level), getConfig().getDouble("vampirism-chance."+level));
        }
    }

    private void loadPerksData() {

        chanceConfinement = getConfig().getDouble("confinement.chance");

        /*
            LOAD HEALTH DATA COST
         */
        {
            for(String level : getConfig().getConfigurationSection("cost-data.health").getKeys(false))
            {
                healthCost.put(Integer.parseInt(level), getConfig().getInt("cost-data.health."+level));
            }
        }

        /*
            LOAD SPEED DATA COST
         */
        {
            for(String level : getConfig().getConfigurationSection("cost-data.speed").getKeys(false))
            {
                speedCost.put(Integer.parseInt(level), getConfig().getInt("cost-data.speed."+level));
            }
        }

        /*
            LOAD STRENGTH DATA COST
         */
        {
            for(String level : getConfig().getConfigurationSection("cost-data.strength").getKeys(false))
            {
                strengthCost.put(Integer.parseInt(level), getConfig().getInt("cost-data.strength."+level));
            }
        }

         /*
            LOAD VAMPIRISM DATA COST
         */
        {
            for(String level : getConfig().getConfigurationSection("cost-data.vampirism").getKeys(false))
            {
                vampirismCost.put(Integer.parseInt(level), getConfig().getInt("cost-data.vampirism."+level));
            }
        }

        /*
            LOAD BOOSTDROP DATA COST
         */
        {
            for(String level : getConfig().getConfigurationSection("cost-data.boostdrop").getKeys(false))
            {
                boostDropCost.put(Integer.parseInt(level), getConfig().getInt("cost-data.boostdrop."+level));
            }
        }

        /*
            LOAD CONFINEMENT DATA COST
         */
        {
            for(String level : getConfig().getConfigurationSection("cost-data.confinement").getKeys(false))
            {
                confinementCost.put(Integer.parseInt(level), getConfig().getInt("cost-data.confinement."+level));
            }
        }
    }

    private void loadUserOnline() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers())
                {
                    sql.loadUser(player);
                }
            }
        }.runTaskAsynchronously(Szaman.getInstance());
    }


    private void setupSql() {
        String host = getConfig().getString("mysql.host");
        String username = getConfig().getString("mysql.username");
        String password = getConfig().getString("mysql.password");
        String database = getConfig().getString("mysql.database");
        String port = getConfig().getString("mysql.port");

        boolean ssl = false;
        if (getConfig().get("mysql.ssl") != null) {
            ssl = getConfig().getBoolean("mysql.ssl");
        }
        this.sql = new Mysql(host, username, password, database, port, ssl);
    }

    public double getChanceConfinement() {
        return chanceConfinement;
    }

    public HashMap<Integer, Double> getDropMultiply() {
        return dropMultiply;
    }

    public HashMap<Integer, Double> getVampirismData() {
        return vampirismData;
    }

    public HashMap<Integer, Integer> getBoostDropCost() {
        return boostDropCost;
    }

    public HashMap<Integer, Integer> getHealthCost() {
        return healthCost;
    }

    public HashMap<Integer, Integer> getSpeedCost() {
        return speedCost;
    }

    public HashMap<Integer, Integer> getStrengthCost() {
        return strengthCost;
    }

    public HashMap<Integer, Integer> getVampirismCost() {
        return vampirismCost;
    }

    public HashMap<Integer, Integer> getConfinementCost() {
        return confinementCost;
    }

    public Mysql getSql() {
        return sql;
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
}
