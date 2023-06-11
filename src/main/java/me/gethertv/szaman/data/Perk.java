package me.gethertv.szaman.data;

import me.gethertv.szaman.Szaman;
import org.bukkit.entity.Player;

public class Perk{

    private PerkType type;
    private int cost;
    private double value;

    public Perk(PerkType type, int cost, double value) {
        this.type = type;
        this.cost = cost;
        this.value = value;
    }

    public PerkType getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

    public double getValue() {
        return value;
    }

}
