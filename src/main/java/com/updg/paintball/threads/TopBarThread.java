package com.updg.paintball.threads;

import com.updg.paintball.Game;
import com.updg.paintball.Models.enums.GameStatus;
import com.updg.paintball.PaintballPlugin;
import com.updg.paintball.Utils.Utils;
import me.confuser.barapi.BarAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Alex
 * Date: 26.02.14  22:48
 */
public class TopBarThread extends Thread {
    public void run() {
        while (true) {
            try {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (PaintballPlugin.game.getStatus() == GameStatus.WAITING) {
                        if (PaintballPlugin.game.tillGame != PaintballPlugin.game.tillGameDefault)
                            BarAPI.setMessage(p, ChatColor.GREEN + "До игры" + Utils.plural(PaintballPlugin.game.tillGame, " осталась " + PaintballPlugin.game.tillGame + " секунда", " осталось " + PaintballPlugin.game.tillGame + " секунды", " осталось " + PaintballPlugin.game.tillGame + " секунд") + ".", (float) PaintballPlugin.game.tillGame / ((float) PaintballPlugin.game.tillGameDefault / 100F));
                        else if (PaintballPlugin.game.getActivePlayers() < PaintballPlugin.game.minPlayers)
                            BarAPI.setMessage(p, ChatColor.GREEN + "Ожидаем игроков. " + ChatColor.YELLOW + "(" + PaintballPlugin.game.getActivePlayers() + "/" + PaintballPlugin.game.minPlayers + "/" + PaintballPlugin.game.maxPlayers + ")", (float) PaintballPlugin.game.getActivePlayers() / ((float) PaintballPlugin.game.minPlayers / 100F));
                        else
                            BarAPI.setMessage(p, ChatColor.GREEN + "Ожидаем игроков.", 100F);
                    }
                    if (PaintballPlugin.game.getStatus() == GameStatus.INGAME) {
                        BarAPI.setMessage(p, ChatColor.GREEN + "Бой", 100F);
                    }
                    if (PaintballPlugin.game.getStatus() == GameStatus.POSTGAME) {
                        BarAPI.setMessage(p, ChatColor.AQUA + "Победила " + PaintballPlugin.game.winner.getName(), 100F);
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}