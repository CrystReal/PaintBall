package com.updg.paintball.Utils;

import com.updg.paintball.Game;
import com.updg.paintball.Models.PPlayer;
import com.updg.paintball.PaintballPlugin;

import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 *
 * @author Sceri
 */
public class Utils {

    public static void resetScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Game game = PaintballPlugin.game;

        game.board = manager.getNewScoreboard();
        game.objective = game.board.registerNewObjective("kills", "dummy");
        game.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        game.objective.setDisplayName("Счет");
    }

    public static Location stringToLoc(String string) {
        String[] loc = string.split("\\|");
        World world = Bukkit.getWorld(loc[0]);
        Double x = Double.parseDouble(loc[1]);
        Double y = Double.parseDouble(loc[2]);
        Double z = Double.parseDouble(loc[3]);

        return new Location(world, x, y, z);
    }


    /**
     * This method return plural form of word based on int value.
     *
     * @param number Int value
     * @param form1  First form of word (i == 1)
     * @param form2  Second form of word (i > 1 && i < 5)
     * @param form3  Third form of word (i > 10 && i < 20)
     * @return string
     */
    public static String plural(int number, String form1, String form2, String form3) {
        int n1 = Math.abs(number) % 100;
        int n2 = number % 10;
        if (n1 > 10 && n1 < 20) return form3;
        if (n2 > 1 && n2 < 5) return form2;
        if (n2 == 1) return form1;
        return form3;
    }
}
