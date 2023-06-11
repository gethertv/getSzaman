package me.gethertv.szaman.utils;

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

    public static String addLorePerks(String input, int price) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        input = input.replace("{price}", String.valueOf(price));
        input = ChatColor.translateAlternateColorCodes('&', input);
        return input;
    }


    public static List<String> addLorePerks(List<String> input, int price, List<String> infoLore) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        List<String> lore = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            if(input.get(i).equalsIgnoreCase("{info}"))
            {
                lore.addAll(ColorFixer.addColors(infoLore));
                continue;
            }
            lore.add(addLorePerks(input.get(i), price));
        }
        return lore;
    }

}
