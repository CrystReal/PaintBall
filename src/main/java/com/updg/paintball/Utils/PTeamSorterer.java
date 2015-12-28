package com.updg.paintball.Utils;

import com.updg.paintball.Models.PPlayer;
import com.updg.paintball.Models.PTeam;
import com.updg.paintball.PaintballPlugin;

import java.util.HashMap;

/**
 * Created by Alex
 * Date: 28.10.13  20:18
 */
public class PTeamSorterer {
    public static void sort(HashMap<String, PPlayer> players, HashMap<String, PTeam> teams) {
        for (PPlayer p : players.values()) {
            PTeam team = null;
            int min = 1000;
            for (PTeam t : teams.values()) {
                if (t.getPlayers().size() < min) {
                    min = t.getPlayers().size();
                    team = t;
                }
            }
            if (team != null)
                PaintballPlugin.game.addPlayerToTeam(p, team.getCode());
            else {
                for (PTeam t1 : teams.values()) {
                    PaintballPlugin.game.addPlayerToTeam(p, t1.getCode());
                    break;
                }
            }
        }
    }
}
