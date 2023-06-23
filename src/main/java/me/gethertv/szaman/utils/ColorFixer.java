package me.gethertv.szaman.utils;

import me.gethertv.szaman.Szaman;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ColorFixer {

    public static String addColors(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        input = ChatColor.translateAlternateColorCodes('&', input);
        return input;
    }


    public static List<String> addColors(List<String> input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        for (int i = 0; i < input.size(); i++) {
            input.set(i, addColors(input.get(i)));
        }
        return input;
    }

    public static String addLorePerks(String input, String info) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        input = input.replace("{price-info}", info);
        input = ChatColor.translateAlternateColorCodes('&', input);
        return input;
    }


    public static List<String> addLorePerks(List<String> input, int price, List<String> infoLore, String level) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String maxLevel = Szaman.getInstance().getConfig().getString("max-level");
        String priceInfo = Szaman.getInstance().getConfig().getString("price-info");
        List<String> lore = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            if(level!=null) {
                input.set(i, input.get(i).replace("{level}", level));

            }

            if(input.get(i).equalsIgnoreCase("{info}"))
            {
                lore.addAll(ColorFixer.addColors(infoLore));
                continue;
            }
            if(price<0) {
                lore.add(addLorePerks(input.get(i), maxLevel));
            }
            else {
                priceInfo = priceInfo.replace("{price}", String.valueOf(price));
                lore.add(addLorePerks(input.get(i), priceInfo));
            }
        }
        return lore;
    }

}
