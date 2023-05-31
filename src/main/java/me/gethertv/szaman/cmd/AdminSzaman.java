package me.gethertv.szaman.cmd;

import me.gethertv.szaman.Szaman;
import me.gethertv.szaman.data.User;
import me.gethertv.szaman.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminSzaman implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("admin"))
            return false;

        if(args.length==2)
        {
            if(args[0].equalsIgnoreCase("reset")) {
                Player player = Bukkit.getPlayer(args[1]);
                if(player==null)
                {
                    sender.sendMessage(ColorFixer.addColors("&cPodany gracz nie jest online!"));
                    return false;
                }
                User user = Szaman.getInstance().getUserData().get(player.getUniqueId());
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
                player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2);
                player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);

                user.setVampirismLevel(0);
                user.setDropBoostLevel(0);
                user.setSpeedLevel(0);
                user.setHealthLevel(0);
                user.setStrengthLevel(0);
                user.setConfinementLevel(0);

                sender.sendMessage(ColorFixer.addColors("&aPomyslnie zresetowano perki!"));
                return true;
            }
            if(args[0].equalsIgnoreCase("info"))
            {
                Player player = Bukkit.getPlayer(args[1]);
                if(player==null)
                {
                    sender.sendMessage(ColorFixer.addColors("&cPodany gracz nie jest online!"));
                    return false;
                }
                User user = Szaman.getInstance().getUserData().get(player.getUniqueId());
                sender.sendMessage("# SERCA: "+user.getHealthLevel());
                sender.sendMessage("# SZYBKOSC: "+user.getSpeedLevel());
                sender.sendMessage("# SILA: "+user.getStrengthLevel());
                sender.sendMessage("# WAMPIRYZM: "+user.getVampirismLevel());
                sender.sendMessage("# DROP: "+user.getDropBoostLevel());
                sender.sendMessage("# UWIEZIENIE: "+user.getConfinementLevel());
            }
        }

        if(args.length==3)
        {
            if(args[0].equalsIgnoreCase("set"))
            {
                Player player = Bukkit.getPlayer(args[1]);
                if(player==null)
                {
                    sender.sendMessage(ColorFixer.addColors("&cPodany gracz nie jest online!"));
                    return false;
                }
                if(!isInt(args[2]))
                {
                    sender.sendMessage(ColorFixer.addColors("&cMusisz podac liczbe calkowita!"));
                    return false;
                }
                User user = Szaman.getInstance().getUserData().get(player.getUniqueId());
                user.setPoints(Integer.parseInt(args[2]));
                sender.sendMessage(ColorFixer.addColors("&aPomyslnie ustawiono punkty!"));
                return true;
            }
            if(args[0].equalsIgnoreCase("add"))
            {
                Player player = Bukkit.getPlayer(args[1]);
                if(player==null)
                {
                    sender.sendMessage(ColorFixer.addColors("&cPodany gracz nie jest online!"));
                    return false;
                }
                if(!isInt(args[2]))
                {
                    sender.sendMessage(ColorFixer.addColors("&cMusisz podac liczbe calkowita!"));
                    return false;
                }
                User user = Szaman.getInstance().getUserData().get(player.getUniqueId());
                user.setPoints(user.getPoints()+Integer.parseInt(args[2]));
                sender.sendMessage(ColorFixer.addColors("&aPomyslnie dodano punkty!"));
                return true;
            }
            if(args[0].equalsIgnoreCase("resettime"))
            {
                Player from = Bukkit.getPlayer(args[1]);
                Player player = Bukkit.getPlayer(args[2]);
                if(from==null || player==null)
                {
                    sender.sendMessage(ColorFixer.addColors("&cPodany gracz nie jest online!"));
                    return false;
                }
                User user = Szaman.getInstance().getUserData().get(from.getUniqueId());
                if(!user.getLastTimeKill().containsKey(player.getUniqueId()))
                {
                    sender.sendMessage(ColorFixer.addColors("&cGracz {player} nie istnieje na liscie!".replace("{player}", player.getName())));
                    return false;
                }
                user.getLastTimeKill().remove(player.getUniqueId());
                sender.sendMessage(ColorFixer.addColors("&cPomyslnie usunieto gracza!"));
                return true;
            }

        }
        return false;
    }

    private boolean isInt(String input)
    {
        try {
            int a = Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {}

        return false;
    }
}
