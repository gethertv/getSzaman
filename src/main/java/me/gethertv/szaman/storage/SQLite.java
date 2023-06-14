package me.gethertv.szaman.storage;

import me.gethertv.szaman.Szaman;
import me.gethertv.szaman.data.PerkType;
import me.gethertv.szaman.data.User;
import me.gethertv.szaman.storage.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class SQLite extends DatabaseManager {

    private Connection connection;
    private String database;

    private String createTableSQL = "CREATE TABLE IF NOT EXISTS "+table+" (" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "uuid VARCHAR(100), " +
            "username VARCHAR(100), " +
            "points INT(11) NOT NULL DEFAULT '0'," +
            "health INT(11) NOT NULL DEFAULT '0'," +
            "speed INT(11) NOT NULL DEFAULT '0'," +
            "strength INT(11) NOT NULL DEFAULT '0'," +
            "vampirism INT(11) NOT NULL DEFAULT '0'," +
            "boostdrop INT(11) NOT NULL DEFAULT '0'," +
            "confinement INT(11) NOT NULL DEFAULT '0'," +
            "killusers TEXT" +
            ");";

    public SQLite(String database){
        super();
        this.database = database;

        openConnection();
        createTable(createTableSQL);
    }

    public void update(String paramString) {
        try {
            Connection connection = getConnection();
            if (connection != null) {
                Statement statement = getConnection().createStatement();
                statement.executeUpdate(paramString);
            }
        } catch (SQLException sQLException) {
            System.out.println("[mysql] wrong update : '" + paramString + "'!");
        }
    }

    public void openConnection() {
        File dataFolder = new File(Szaman.getInstance().getDataFolder(), database+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                Szaman.getInstance().getLogger().log(Level.SEVERE, "File write error: "+database+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                this.connection = connection;
            }
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
        } catch (SQLException ex) {
            Szaman.getInstance().getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            Szaman.getInstance().getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
    }

    public void createTable(String sqlCreate) {

        update(sqlCreate);
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void loadUser(Player player) {
        if(!playerExists(player.getUniqueId()))
        {
            createUser(player);
            Szaman.getInstance().getUserData().put(player.getUniqueId(), new User(player));
            return;
        }

        String str = "SELECT * FROM "+table+" WHERE uuid = '" + player.getUniqueId() + "'";
        try {
            ResultSet resultSet = getResult(str);
            while (resultSet.next()) {
                int points = resultSet.getInt("points");
                int health = resultSet.getInt("health");
                int speed = resultSet.getInt("speed");
                int strength = resultSet.getInt("strength");
                int vampirism = resultSet.getInt("vampirism");
                int boostdrop = resultSet.getInt("boostdrop");
                int confinement = resultSet.getInt("confinement");
                String killusers = "";

                if(resultSet.getString("killusers")!=null)
                    killusers = resultSet.getString("killusers");

                HashMap<UUID, Long> killData = new HashMap<>();
                if(killusers.length()>0)
                {
                    String[] data = killusers.split(";");
                    for (int i = 0; i < data.length; i++) {
                        String[] userKill = data[i].split(":");
                        killData.put(UUID.fromString(userKill[0]), Long.parseLong(userKill[1]));
                    }
                }

                Szaman.getInstance().getUserData().put(
                        player.getUniqueId(), new User(player, points, health, speed, strength, vampirism, boostdrop, confinement, killData));
            }

        } catch (SQLException | NullPointerException sQLException) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    System.out.println(sQLException.getMessage());
                    player.kickPlayer("Bląd! Zgłoś sie na discord!");
                }
            }.runTask(Szaman.getInstance());
        }
    }

    @Override
    public void updateUser(Player player) {
        User user = Szaman.getInstance().getUserData().get(player.getUniqueId());

        String userList = "";
        List<UUID> toRemove =  new ArrayList<>();
        for(Map.Entry<UUID, Long> killData : user.getLastTimeKill().entrySet())
        {
            if(killData.getValue()<=System.currentTimeMillis())
            {
                toRemove.add(killData.getKey());
                continue;
            }
            userList+=killData.getKey()+":"+killData.getValue()+";";
        }

        for(UUID uuid : toRemove)
            user.getLastTimeKill().remove(uuid);

        update("UPDATE "+table+" SET " +
                "points = '"+user.getPoints()+"', " +
                "health = '"+user.getLevel(PerkType.HEALTH)+"', " +
                "speed = '"+user.getLevel(PerkType.SPEED)+"', " +
                "strength = '"+user.getLevel(PerkType.STRENGTH)+"', " +
                "vampirism = '"+user.getLevel(PerkType.VAMPIRISM)+"', " +
                "boostdrop = '"+user.getLevel(PerkType.BOOSTDROP)+"', " +
                "confinement = '"+user.getLevel(PerkType.CONFINEMENT)+"', " +
                "killusers = '"+userList+"' " +
                "WHERE uuid = '"+player.getUniqueId()+"'");
    }

    public void createUser(Player player)
    {
        update("INSERT INTO "+table+" (uuid, username) VALUES ('"+player.getUniqueId()+"', '"+player.getName()+"')");
    }


    public boolean playerExists(UUID uuid) {
        return (getPlayerID(uuid) != 0);
    }

    private int getPlayerID(UUID uuid) {
        return getInt("id", "SELECT id FROM "+table+" WHERE uuid='" + uuid.toString() + "'");
    }
    private int getInt(String paramString1, String paramString2) {
        try {
            ResultSet resultSet = getResult(paramString2);
            if (resultSet.next()) {
                int i = resultSet.getInt(paramString1);
                resultSet.close();
                return i;
            }
        } catch (SQLException sQLException) {
            return 0;
        }
        return 0;
    }

    public ResultSet getResult(String paramString) {
        ResultSet resultSet = null;
        Connection connection = getConnection();
        try {
            if (connection != null) {
                Statement statement = getConnection().createStatement();
                resultSet = statement.executeQuery(paramString);
            }
        } catch (SQLException sQLException) {
            System.out.println("[mysql] wrong when want get result: '" + paramString + "'!");
        }
        return resultSet;
    }
    public boolean isConnected() {
        return (getConnection() != null);
    }


}