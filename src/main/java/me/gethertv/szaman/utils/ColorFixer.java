package me.gethertv.szaman.utils;

import org.bukkit.ChatColor;

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

    public static String addLorePerks(String input, int level, int price) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        input = input.replace("{level}", String.valueOf(level));
        input = input.replace("{price}", String.valueOf(price));
        input = ChatColor.translateAlternateColorCodes('&', input);
        return input;
    }


    public static List<String> addLorePerks(List<String> input, int level, int price) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        for (int i = 0; i < input.size(); i++) {
            input.set(i, addLorePerks(input.get(i), level, price));
        }
        return input;
    }

}
