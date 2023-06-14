package me.gethertv.szaman.storage;
import me.gethertv.szaman.Szaman;
import me.gethertv.szaman.data.PerkType;
import me.gethertv.szaman.data.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.*;
import java.util.*;

public class Mysql extends DatabaseManager {
    private String host;
    private String username;
    private String password;
    private String database;
    private String port;
    private boolean ssl;
    private boolean isFinished;

    private String customUrlConnection;
    private Connection connection;

    public Mysql(String host, String username, String password, String database, String port, boolean ssl) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;
        this.ssl = ssl;

        openConnection();
        createTable();
    }

    private String getUsername() {
        return this.username;
    }

    private String getPassword() {
        return this.password;
    }

    private String getHost() {
        return this.host;
    }

    private String getPort() {
        return this.port;
    }

    private String getDatabase() {
        return this.database;
    }

    private boolean useSSL() {
        return this.ssl;
    }

    public boolean isConnected() {
        return (getConnection() != null);
    }

    public Connection getConnection() {
        validateConnection();
        return this.connection;
    }

    private void openConnection() {
        try {
            long l1 = System.currentTimeMillis();
            long l2 = 0L;
            //Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", getUsername());
            properties.setProperty("password", getPassword());
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("useSSL", String.valueOf(useSSL()));
            properties.setProperty("requireSSL", String.valueOf(useSSL()));
            properties.setProperty("verifyServerCertificate", "false");
            String str = "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabase();
            if(customUrlConnection!=null)
                this.connection = DriverManager.getConnection(customUrlConnection);
            else
                this.connection = DriverManager.getConnection(str, properties);


            l2 = System.currentTimeMillis();
            this.isFinished = true;
            System.out.println("[mysql] Connected successfully");
        } catch (ClassNotFoundException classNotFoundException) {
            this.isFinished = false;
            System.out.println("[mysql] Check your configuration.");
            Bukkit.getPluginManager().disablePlugin(Szaman.getInstance());
        } catch (SQLException sQLException) {
            this.isFinished = false;
            System.out.println("[mysql] (" + sQLException.getLocalizedMessage() + "). Check your configuration.");
            Bukkit.getPluginManager().disablePlugin(Szaman.getInstance());
        }
    }

    private void validateConnection() {
        if (!this.isFinished)
            return;
        try {
            if (this.connection == null) {
                System.out.println("[mysql] aborted. Connecting again");
                reConnect();
            }
            if (!this.connection.isValid(4)) {
                System.out.println("[mysql] timeout.");
                reConnect();
            }
            if (this.connection.isClosed()) {
                System.out.println("[mysql] closed. Connecting again");
                reConnect();
            }
        } catch (Exception exception) {
        }
    }

    private void reConnect() {
        System.out.println("[mysql] connection again");
        openConnection();
    }

    public void closeConnection() {
        if (getConnection() != null) {
            try {
                getConnection().close();
                System.out.println("[mysql] connection closed");
            } catch (SQLException sQLException) {
                System.out.println("[mysql] error when try close connection");
            }
        }
    }

    public int checkExists(String str) {
        int i = 0;
        try {
            ResultSet resultSet = getResult(str);
            if (resultSet.next()) {
                i++;
                resultSet.close();
                return i;
            }
        } catch (SQLException sQLException) {
            return i;
        }
        return i;
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

    public void createUser(Player player)
    {
        update("INSERT INTO "+table+" (uuid, username) VALUES ('"+player.getUniqueId()+"', '"+player.getName()+"')");
    }


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

    public void createTable() {
        String create = "CREATE TABLE IF NOT EXISTS "+table+" (" +
                "id INT(10) AUTO_INCREMENT, PRIMARY KEY (id)," +
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
                ")";


        update(create);
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



}
