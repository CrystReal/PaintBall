package com.updg.paintball.Utils;


import com.updg.paintball.Models.PPlayer;
import com.updg.paintball.Models.PTeam;
import com.updg.paintball.Models.enums.GameStatus;
import com.updg.paintball.PaintballPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by Alex
 * Date: 25.10.13  2:46
 */
public class PScoreboard {
    public static void updateSidebarScoreboard() {
        for (PPlayer p : PaintballPlugin.game.players.values()) {
            updateSidebarScoreboard(p);
        }
    }

    public static void updateSidebarScoreboard(PPlayer player) {
        PScoreboard.updateSidebarScoreboard(player, true, null);
    }

    public static void updateSidebarScoreboard(PPlayer player, PTeam damageTeam) {
        PScoreboard.updateSidebarScoreboard(player, true, damageTeam);
    }

    public static void updateSidebarScoreboard(PPlayer player, boolean distance) {
        PScoreboard.updateSidebarScoreboard(player, distance, null);
    }

    public static void updateSidebarScoreboard(PPlayer player, boolean distance, PTeam damageTeam) {
        if (PaintballPlugin.game.getStatus() == GameStatus.INGAME || PaintballPlugin.game.getStatus() == GameStatus.RELOAD || PaintballPlugin.game.getStatus() == GameStatus.POSTGAME) {
            Scoreboard board;
            Objective objective;
            board = player.getBukkitPlayer().getScoreboard();
            objective = board.getObjective("stats");
            if (objective == null) {
                player.getBukkitPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                board = player.getBukkitPlayer().getScoreboard();
                objective = board.registerNewObjective("stats", "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            Score score;
            objective.setDisplayName(ChatColor.RESET + "Очки возрождения");
            for (PTeam item : PaintballPlugin.game.getTeams().values()) {
                if (damageTeam != null && item.equals(damageTeam))
                    score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.WHITE + item.getName() + ":"));
                else
                    score = objective.getScore(Bukkit.getOfflinePlayer(item.getChatColor() + item.getName() + ":"));
                score.setScore(item.getPoints());
            }
        } else {
            Scoreboard board;
            Objective objective;
            board = player.getBukkitPlayer().getScoreboard();
            objective = board.getObjective("lobby");
            if (objective == null) {
                player.getBukkitPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                board = player.getBukkitPlayer().getScoreboard();
                objective = board.registerNewObjective("lobby", "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            Score score;
            objective.setDisplayName(ChatColor.GREEN + "Выбор карты: /vote [имя карты]");
            score = objective.getScore(Bukkit.getOfflinePlayer("Beta Test Map"));
            score.setScore(Bukkit.getOnlinePlayers().length);
        }
    }

    public static void updateSubTitle(PPlayer player) {
        //TODO: FIX SUB TITLE
       /* if (!player.isSpectator() && player.team != null) {
            Scoreboard board;
            board = player.getBukkitModel().getScoreboard();
            Objective objective;
            objective = board.getObjective("team");
            if (objective == null) {
                objective = board.registerNewObjective("team", "dummy");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            } else {
                objective.unregister();
                objective = board.registerNewObjective("team", "dummy");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            }
            L.$(player.getCls().getName());
            objective.setDisplayName(player.getCls().getName() + " 0" + ChatColor.RESET + "");
            objective.getScore(player.getBukkitModel()).setScore(1);
            objective.getScore(player.getBukkitModel()).setScore(0);
            player.getBukkitModel().setScoreboard(board);
        }   */
    }

}
