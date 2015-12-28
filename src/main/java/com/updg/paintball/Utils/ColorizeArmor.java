package com.updg.paintball.Utils;

import com.updg.paintball.Models.PTeam;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Created by Alex
 * Date: 24.10.13  19:10
 */
public class ColorizeArmor {
    public static ItemStack c(ItemStack item, PTeam team) {
        LeatherArmorMeta lam1 = (LeatherArmorMeta) item.getItemMeta();
        if (team.getCode().equals("red"))
            lam1.setColor(Color.RED);
        if (team.getCode().equals("blue"))
            lam1.setColor(Color.BLUE);
        item.setItemMeta(lam1);
        return item;
    }
}
