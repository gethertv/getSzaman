package me.gethertv.szaman.cmd;

import me.gethertv.szaman.Szaman;
import me.gethertv.szaman.data.PerkType;
import me.gethertv.szaman.data.User;
import me.gethertv.szaman.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class AdminSzaman implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("admin"))
            return false;

        if(args.length==1)
        {
            if(args[0].equalsIgnoreCase("setodlamek"))
            {
                if(sender instanceof Player)
                {
                    Player player = (Player) sender;
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    if(itemStack==null)
                    {
                        player.sendMessage(ColorFixer.addColors("&cMusisz trzymac przedmiot w rece!"));
                        return false;

                    }
                    itemStack.setAmount(1);
                    Szaman.getInstance().getConfig().set("odlamek", itemStack);
                    Szaman.getInstance().saveConfig();
                    player.sendMessage(ColorFixer.addColors("&aPomyslnie ustawiono odlamek!"));
                    return true;
                }
            }
        }
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

                user.setLevelPerk(PerkType.VAMPIRISM, 0);
                user.setLevelPerk(PerkType.BOOSTDROP, 0);
                user.setLevelPerk(PerkType.SPEED, 0);
                user.setLevelPerk(PerkType.HEALTH, 0);
                user.setLevelPerk(PerkType.STRENGTH, 0);
                user.setLevelPerk(PerkType.CONFINEMENT, 0);

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
                sender.sendMessage("# SERCA: "+user.getLevel(PerkType.HEALTH));
                sender.sendMessage("# SZYBKOSC: "+user.getLevel(PerkType.SPEED));
                sender.sendMessage("# SILA: "+user.getLevel(PerkType.STRENGTH));
                sender.sendMessage("# WAMPIRYZM: "+user.getLevel(PerkType.VAMPIRISM));
                sender.sendMessage("# DROP: "+user.getLevel(PerkType.BOOSTDROP));
                sender.sendMessage("# UWIEZIENIE: "+user.getLevel(PerkType.CONFINEMENT));
            }
        }

        if(args.length==3)
        {
            if(args[0].equalsIgnoreCase("give"))
            {
                Player player = Bukkit.getPlayer(args[1]);
                if(player==null)
                {
                    sender.sendMessage(ColorFixer.addColors("&cPodany gracz nie jest online!"));
                    return false;
                }
                if(!isInt(args[2]))
                {
                    sender.sendMessage(ColorFixer.addColors("&cPodaj liczbe!"));
                    return false;
                }
                int amount = Integer.parseInt(args[2]);
                ItemStack itemOdlamek = Szaman.ITEM_ODLAMEK.clone();
                itemOdlamek.setAmount(amount);
                player.getInventory().addItem(itemOdlamek);
                sender.sendMessage(ColorFixer.addColors("&aPomyslnie nadano odlamek!"));
                return true;
            }
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

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length==1)
        {
            return Arrays.asList("reset", "info", "set", "add", "resettime", "setodlamek");
        }
        return null;
    }
}
