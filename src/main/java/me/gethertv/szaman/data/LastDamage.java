package me.gethertv.szaman.data;

import org.bukkit.entity.Player;



public class LastDamage {
    private Player damager;
    private Long time;

    public LastDamage(Player damager, Long time) {
        this.damager = damager;
        this.time = time;
    }

    public Player getDamager() {
        return damager;
    }

    public Long getTime() {
        return time;
    }
}
