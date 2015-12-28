package com.updg.paintball.Models.enums.upgrades;

import org.bukkit.Color;

public enum ColorUpgrade {
    DEFAULT(Color.YELLOW),
    GREEN(Color.GREEN),
    RED(Color.RED),
    BLUE(Color.BLUE),
    PURPLE(Color.PURPLE),
    GOLD(Color.ORANGE);

    private Color value;

    private ColorUpgrade(Color value) {
        this.value = value;
    }
    
    public static Color getValueById(int id) {
        return values()[id].value;      
    }
    
}