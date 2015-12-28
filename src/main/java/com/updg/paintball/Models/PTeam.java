package com.updg.paintball.Models;

import com.updg.paintball.Utils.ConfigUtils;
import com.updg.paintball.Utils.EconomicSettings;
import com.updg.paintball.Utils.PScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex
 * Date: 26.02.14  23:07
 */
public class PTeam {
    private int id = 0;
    private String code;
    private String name;
    private String inName;

    int playersCount = 0;
    private HashMap<String, PPlayer> players = new HashMap<String, PPlayer>();

    private Location spawn;
    private int points = 100;

    public PTeam(int id, String code, String name, String inName, String location) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.inName = inName;

        this.spawn = ConfigUtils.stringToLocation(location);
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getInName() {
        return inName;
    }

    public HashMap<String, PPlayer> getPlayers() {
        return this.players;
    }

    public int getPlayersCount() {
        return this.playersCount;
    }

    public boolean isPlayerInTeam(PPlayer pl) {
        return this.players.containsKey(pl.getName().toLowerCase());
    }

    // FUNC-s
    public void addPlayer(PPlayer pl) {
        if (!this.players.containsKey(pl.getName().toLowerCase())) {
            this.players.put(pl.getName().toLowerCase(), pl);
            this.playersCount++;
        }
    }

    public void removePlayer(PPlayer pl) {
        if (this.players.containsKey(pl.getName().toLowerCase())) {
            this.players.remove(pl.getName().toLowerCase());
            this.playersCount--;
        }
    }

    public void sendMessage(String msg) {
        for (PPlayer player : this.players.values()) {
            player.sendMessage(msg);
        }
    }

    public void joinPlayersToMap() {
        for (PPlayer item : players.values()) {
            item.getBukkitPlayer().teleport(this.spawn);
            item.clearInventory();
            item.setAdventureMode();
            item.setDefaultArmor(this);
            item.takeKitStart();
        }
    }

    public HashMap<String, PPlayer> getActivePlayers() {
        HashMap<String, PPlayer> out = new HashMap<String, PPlayer>();
        for (Map.Entry<String, PPlayer> item : getPlayers().entrySet()) {
            if (item.getValue().isActive())
                out.put(item.getKey(), item.getValue());
        }
        return out;
    }

    public void loose() {
        Bukkit.broadcastMessage(ChatColor.GOLD + name + " команда проиграла!");
        for (PPlayer item : this.players.values()) {
            item.getStat().tillFinish();
        }
    }

    public void win() {
        Bukkit.broadcastMessage(ChatColor.GOLD + name + " команда выиграла!");
        for (PPlayer item : this.players.values()) {
            item.getStat().tillFinish();
            item.addExp(EconomicSettings.win);
        }
    }

    public void winTech() {
        Bukkit.broadcastMessage(ChatColor.GOLD + name + " команда одержала техническую победу!");
        for (PPlayer item : this.players.values()) {
            item.getStat().tillFinish();
            item.addExp(EconomicSettings.technicalWin);
        }
    }

    public Location getSpawn() {
        return spawn;
    }

    public void minusPoint() {
        this.points--;
        PScoreboard.updateSidebarScoreboard();
    }

    public ChatColor getChatColor() {
        if (this.code.equals("red"))
            return ChatColor.RED;
        if (this.code.equals("blue"))
            return ChatColor.BLUE;
        return null;
    }

    public int getPoints() {
        return points;
    }
}
