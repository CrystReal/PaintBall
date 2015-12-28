package com.updg.paintball;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.updg.CR_API.MQ.senderStatsToCenter;
import com.updg.paintball.Listeners.GameListener;
import com.updg.paintball.Listeners.PlayerListener;
import com.updg.paintball.Listeners.SystemListener;
import com.updg.paintball.Models.PPlayer;
import com.updg.paintball.Models.enums.GameStatus;
import com.updg.paintball.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.updg.paintball.stats.gameStats;
import com.updg.paintball.stats.playerStats;
import com.updg.paintball.stats.playerStatsKills;
import com.updg.paintball.threads.TimeAndWeather;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Alex
 * Date: 13.11.13  5:13
 */
public class PaintballPlugin extends JavaPlugin {
    public static PaintballPlugin plugin;
    public static Game game;
    public int serverId = 0;
    public int mapId = 0;

    @Override
    public void onEnable() {
        plugin = this;

        this.saveDefaultConfig();

        this.getServer().getPluginManager().registerEvents(new GameListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new SystemListener(), this);

        serverId = getConfig().getInt("serverId");
        mapId = getConfig().getInt("mapId", 1);

        game = new Game(getConfig().getInt("minPlayers", 10), getConfig().getInt("vipPlayersFrom", 10), getConfig().getInt("maxPlayers", 11), getConfig().getInt("killLimit", 20), getConfig().getInt("mapId", 0));
        Utils.resetScoreboard();

        World world = Bukkit.getWorlds().get(0);
        world.setThundering(false);
        world.setStorm(false);
        world.setWeatherDuration(1000000);
        world.setTime(0);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(PaintballPlugin.plugin, new TimeAndWeather(Bukkit.getWorlds().get(0)), 1000L, 1000L);

        //getCommand("startgame").setExecutor(game);

        game.getReady();
    }

    public void onDisable() {
        game.setStatus(GameStatus.RELOAD);
        PaintballPlugin.game.send();
    }

    public void reloadServer() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("Сервер перезагрузится через 15 секунд.");
        }
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage("Сервер перезагрузится через 10 секунд.");
                    }
                    Thread.sleep(5000);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage("Сервер перезагрузится через 5 секунд.");
                    }
                    Thread.sleep(5000);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage("Сервер перезагружается.");
                    }
                    game.setStatus(GameStatus.RELOAD);
                    game.send();
                    sendStats();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
    }

    public void sendStats() {
        gameStats game = new gameStats();
        game.setServerId(this.serverId);
        game.setWinner(PaintballPlugin.game.winner.getId());
        game.setWinType(PaintballPlugin.game.isTechWin() ? 1 : 0);
        game.setStart(PaintballPlugin.game.timeStart);
        game.setEnd(PaintballPlugin.game.timeEnd);
        game.setMap(mapId);
        List<playerStats> players = new ArrayList<playerStats>();
        List<playerStatsKills> tmpKillsA;
        playerStats tmpPlayer;
        playerStatsKills tmpKills;
        for (PPlayer p : PaintballPlugin.game.getActivePlayersArray()) {
            tmpPlayer = new playerStats();
            tmpPlayer.setPlayerId(p.getId());
            tmpPlayer.setDeaths(p.getStat().getDeaths());
            tmpPlayer.setTimeInGame(p.getStat().getInGameTime());
            tmpPlayer.setTillFinish(!p.getStat().isExit());
            if (p.team == null)
                tmpPlayer.setTeam(0);
            else tmpPlayer.setTeam(p.team.getId());
            tmpKillsA = new ArrayList<playerStatsKills>();
            for (Map.Entry entry : p.getStat().getKilled().entrySet()) {
                tmpKills = new playerStatsKills();
                tmpKills.setTime((Long) entry.getKey());
                tmpKills.setVictim(((PPlayer) entry.getValue()).getId());
                tmpKillsA.add(tmpKills);
            }
            tmpPlayer.setVictims(tmpKillsA);
            players.add(tmpPlayer);
        }
        for (PPlayer p : PaintballPlugin.game.getLeavedPlayersArray()) {
            tmpPlayer = new playerStats();
            tmpPlayer.setPlayerId(p.getId());
            tmpPlayer.setDeaths(p.getStat().getDeaths());
            tmpPlayer.setTimeInGame(p.getStat().getInGameTime());
            tmpPlayer.setTillFinish(!p.getStat().isExit());
            if (p.team == null)
                tmpPlayer.setTeam(0);
            else tmpPlayer.setTeam(p.team.getId());
            tmpKillsA = new ArrayList<playerStatsKills>();
            for (Map.Entry entry : p.getStat().getKilled().entrySet()) {
                tmpKills = new playerStatsKills();
                tmpKills.setTime((Long) entry.getKey());
                tmpKills.setVictim(((PPlayer) entry.getValue()).getId());
                tmpKillsA.add(tmpKills);
            }
            tmpPlayer.setVictims(tmpKillsA);
            players.add(tmpPlayer);
        }
        for (PPlayer p : PaintballPlugin.game.getSpectatorsArray()) {
            tmpPlayer = new playerStats();
            tmpPlayer.setPlayerId(p.getId());
            tmpPlayer.setDeaths(p.getStat().getDeaths());
            tmpPlayer.setTimeInGame(p.getStat().getInGameTime());
            tmpPlayer.setTillFinish(!p.getStat().isExit());
            if (p.team == null)
                tmpPlayer.setTeam(0);
            else tmpPlayer.setTeam(p.team.getId());
            tmpKillsA = new ArrayList<playerStatsKills>();
            for (Map.Entry entry : p.getStat().getKilled().entrySet()) {
                tmpKills = new playerStatsKills();
                tmpKills.setTime((Long) entry.getKey());
                tmpKills.setVictim(((PPlayer) entry.getValue()).getId());
                tmpKillsA.add(tmpKills);
            }
            tmpPlayer.setVictims(tmpKillsA);
            players.add(tmpPlayer);
        }
        game.setPlayers(players);
        try {
            String stat = new ObjectMapper().writeValueAsString(game);
            senderStatsToCenter.send("paintball", stat);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
