package com.updg.paintball;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.updg.paintball.Models.PPlayer;
import com.updg.paintball.Models.PTeam;
import com.updg.paintball.Models.enums.GameStatus;
import com.updg.paintball.Utils.ConfigUtils;
import com.updg.paintball.Utils.PScoreboard;
import com.updg.paintball.Utils.PTeamSorterer;
import com.updg.paintball.Utils.Utils;

import com.updg.CR_API.MQ.senderStatsToCenter;
import com.updg.CR_API.MQ.senderUpdatesToCenter;
import com.updg.paintball.threads.TopBarThread;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.scoreboard.*;
import sun.security.krb5.Config;


public class Game{
    public int minPlayers = 10;
    public int killLimit = 20;
    public int vipPlayersFrom = 10;
    public int maxPlayers = 11;
    public int mapId = 0;

    public int tillGame = 60;
    public int tillGameDefault = 60;
    public int tillGameShedule = 0;

    public long timeStart = 0;
    public long timeEnd = 0;
    private boolean techWin = false;

    public static String pluginPrefix = "[" + ChatColor.GOLD + "Снежки" + ChatColor.WHITE + "] " + ChatColor.RESET;

    public Objective objective = null;
    public Scoreboard board = null;

    public HashMap<String, PPlayer> players = new HashMap<String, PPlayer>();
    private HashMap<String, PTeam> teams = new HashMap<String, PTeam>();

    private GameStatus status = GameStatus.RELOAD;

    public PTeam winner = null;
    private Location lobby;

    public Game(int minPlayers, int vipPlayersFrom, int maxPlayers, int killLimit, int mapId) {
        this.minPlayers = minPlayers;
        this.killLimit = killLimit;
        this.vipPlayersFrom = vipPlayersFrom;
        this.maxPlayers = maxPlayers;
        this.mapId = mapId;

        this.lobby = ConfigUtils.stringToLocation(PaintballPlugin.plugin.getConfig().getString("lobby"));

        this.teams.put("red", new PTeam(1, "red", "Красная", "Красной", PaintballPlugin.plugin.getConfig().getString("spawnRed")));
        this.teams.put("blue", new PTeam(2, "blue", "Синяя", "Синей", PaintballPlugin.plugin.getConfig().getString("spawnBlue")));
    }

    public void send() {
        String s = GameStatus.WAITING.toString();
        if (PaintballPlugin.game.maxPlayers <= PaintballPlugin.game.getActivePlayers())
            s = "IN_GAME";
        if (PaintballPlugin.game.getStatus() == GameStatus.WAITING) {
            if (PaintballPlugin.game.tillGame < PaintballPlugin.game.tillGameDefault)
                senderUpdatesToCenter.send(PaintballPlugin.plugin.serverId + ":" + s + ":" + "В ОЖИДАНИИ" + ":" + PaintballPlugin.game.getActivePlayers() + ":" + PaintballPlugin.game.maxPlayers + ":До игры " + PaintballPlugin.game.tillGame + " c.");
            else
                senderUpdatesToCenter.send(PaintballPlugin.plugin.serverId + ":" + s + ":" + "В ОЖИДАНИИ" + ":" + PaintballPlugin.game.getActivePlayers() + ":" + PaintballPlugin.game.maxPlayers + ":Набор игроков");
        } else if (PaintballPlugin.game.getStatus() == GameStatus.POSTGAME) {
            senderUpdatesToCenter.send(PaintballPlugin.plugin.serverId + ":IN_GAME:" + "ИГРА ОКОНЧЕНА" + ":" + PaintballPlugin.game.getActivePlayers() + ":" + PaintballPlugin.game.maxPlayers + ":Победил " + PaintballPlugin.game.winner.getName());
        } else if (PaintballPlugin.game.getStatus() == GameStatus.INGAME || PaintballPlugin.game.getStatus() == GameStatus.POSTGAME)
            senderUpdatesToCenter.send(PaintballPlugin.plugin.serverId + ":IN_GAME:" + "ИГРА" + ":" + PaintballPlugin.game.getActivePlayers() + ":" + PaintballPlugin.game.maxPlayers + ":БОЙ");
        else if (PaintballPlugin.game.getStatus() == GameStatus.RELOAD)
            senderUpdatesToCenter.send(PaintballPlugin.plugin.serverId + ":DISABLED:" + "ОФФЛАЙН" + ":0:0:");

    }

    public void getReady() {
        setStatus(GameStatus.WAITING);
        new TopBarThread().start();
        send();
    }

    public int getActivePlayers() {
        int activePlayers = 0;

        for (PPlayer player : players.values()) {
            if (!player.getStat().isExit()) {
                activePlayers++;
            }
        }

        return activePlayers;
    }

    public Collection<PPlayer> getActivePlayersArray() {
        Collection<PPlayer> o = new ArrayList<PPlayer>();
        for (PPlayer player : players.values()) {
            if (player.isActive()) {
                o.add(player);
            }
        }
        return o;
    }

    public Collection<PPlayer> getLeavedPlayersArray() {
        Collection<PPlayer> o = new ArrayList<PPlayer>();
        for (PPlayer player : players.values()) {
            if (player.getStat().isExit()) {
                o.add(player);
            }
        }
        return o;
    }

    public Collection<PPlayer> getSpectatorsArray() {
        Collection<PPlayer> o = new ArrayList<PPlayer>();
        for (PPlayer player : players.values()) {
            if (player.isSpectator() && !player.getStat().isExit()) {
                o.add(player);
            }
        }
        return o;
    }

    public boolean isAvailableToStart() {
        return this.getActivePlayers() >= this.minPlayers;
    }

    public void refreshTimer() {
        if (this.status != GameStatus.INGAME && this.status != GameStatus.RELOAD) {
            if (isAvailableToStart() && this.tillGame == this.tillGameDefault && this.tillGameShedule == 0) {
                this.tillGameShedule = Bukkit.getScheduler().scheduleSyncRepeatingTask(PaintballPlugin.plugin, new Runnable() {
                    public void run() {
                        updateTimer();
                    }
                }, 0, 20);
            }
        }
    }

    private void updateTimer() {
        if (isAvailableToStart()) {
            this.tillGame--;
            if (this.tillGame == 0) {
                Bukkit.getScheduler().cancelTask(this.tillGameShedule);
                for (PPlayer player : this.players.values()) {
                    if (!player.getStat().isExit())
                        player.playSound("random.levelup");
                }
                this.startGame();
            }
            if (this.tillGame <= 10 && this.tillGame > 0) {
                for (PPlayer player : this.players.values()) {
                    if (!player.getStat().isExit())
                        player.playSound("random.orb");
                }
            }
            send();
        } else {
            Bukkit.getScheduler().cancelTask(this.tillGameShedule);
            this.tillGameShedule = 0;
            this.tillGame = this.tillGameDefault;
            this.status = GameStatus.WAITING;
            send();
            Bukkit.broadcastMessage(pluginPrefix + "Отмена старта. Недостаточно игроков.");
        }
    }

    public void doWin(PTeam team) {
        for (PPlayer item : getActivePlayersArray()) {
            item.getStat().setInGameTime(System.currentTimeMillis() / 1000L - this.timeStart);
        }
        team.win();
        this.status = GameStatus.POSTGAME;
        this.winner = team;
        this.timeEnd = System.currentTimeMillis() / 1000L;
        send();
        PaintballPlugin.plugin.reloadServer();
    }

    private void doTechWin(PTeam team) {
        for (PPlayer item : getActivePlayersArray()) {
            item.getStat().setInGameTime(System.currentTimeMillis() / 1000L - this.timeStart);
        }
        team.winTech();
        this.status = GameStatus.POSTGAME;
        this.winner = team;
        this.techWin = true;
        this.timeEnd = System.currentTimeMillis() / 1000L;
        send();
        PaintballPlugin.plugin.reloadServer();
    }

    public void addPlayer(PPlayer p) {
        this.players.put(p.getName().toLowerCase(), p);
    }

    public void removePlayer(PPlayer p) {
        if (this.players.containsKey(p.getName().toLowerCase()))
            this.players.remove(p.getName().toLowerCase());
    }

    public void removePlayer(String player) {
        if (this.players.containsKey(player.toLowerCase()))
            this.players.remove(player.toLowerCase());
    }

    public void startGame() {
        this.checkPlayersAndSetToCommands();
        timeStart = System.currentTimeMillis() / 1000;
        setStatus(GameStatus.INGAME);
        for (PPlayer item : getActivePlayersArray()) {
            item.getBukkitPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            PScoreboard.updateSidebarScoreboard(item);
        }
        this.joinPlayersToMap();
        Bukkit.broadcastMessage(pluginPrefix + "Да прибудет с вами сила!");
    }

    public void checkPlayersAndSetToCommands() {
        HashMap<String, PPlayer> notInTeam = new HashMap<String, PPlayer>();
        for (PPlayer player : this.players.values()) {
            if (!player.isSpectator() && !this.isPlayerInAnyTeam(player)) {
                notInTeam.put(player.getName().toLowerCase(), player);
            }
        }
        PTeamSorterer.sort(notInTeam, teams);
    }

    public boolean addPlayerToTeam(PPlayer player, String Team) {
        this.removePlayerFromAllTeams(player);
        this.teams.get(Team).addPlayer(player);
        player.team = this.teams.get(Team);
        return false;
    }

    public void removePlayerFromAllTeams(PPlayer name) {
        for (Map.Entry<String, PTeam> stringDKTeamEntry : this.teams.entrySet()) {
            Map.Entry pairs = (Map.Entry) stringDKTeamEntry;
            PTeam team = (PTeam) pairs.getValue();
            team.removePlayer(name);
        }
    }

    public boolean isPlayerInAnyTeam(PPlayer name) {
        for (Map.Entry<String, PTeam> stringDKTeamEntry : this.teams.entrySet()) {
            Map.Entry pairs = (Map.Entry) stringDKTeamEntry;
            PTeam team = (PTeam) pairs.getValue();
            if (team.isPlayerInTeam(name))
                return true;
        }
        return false;
    }

    public void joinPlayersToMap() {
        for (Map.Entry<String, PTeam> stringDKTeamEntry : this.teams.entrySet()) {
            Map.Entry pairs = (Map.Entry) stringDKTeamEntry;
            PTeam team = (PTeam) pairs.getValue();
            team.joinPlayersToMap();
        }
    }

    public PPlayer getPPlayer(String player) {
        PPlayer qcp;
        if ((qcp = this.players.get(player.toLowerCase())) != null) {
            return qcp;
        }

        return null;
    }

    public void kickPlayer(Player pl) {
        if (this.players.containsKey(pl.getName().toLowerCase()) && this.isPlayerInAnyTeam(this.players.get(pl.getName().toLowerCase()))) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "Игрок " + pl.getName() + " покинул сервер.");
            if (this.getStatus() == GameStatus.INGAME)
                this.removePlayer(pl.getName());
            this.checkTeams();
        }
    }

    public void checkWin() {
        if (teams.get("red").getPoints() == 0) {
            doWin(teams.get("red"));
        } else if (teams.get("blue").getPoints() == 0) {
            doWin(teams.get("blue"));
        }
    }

    public void checkTeams() {
        int loose = 0;
        PTeam win = null;
        for (PTeam item : this.teams.values()) {
            if (item.getActivePlayers().size() == 0) {
                loose++;
            } else
                win = item;
        }
        if (win != null && loose == 1) {
            this.doTechWin(win);
        }
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void updateScoreboard() {
        for (PPlayer online : this.players.values()) {
            if (!online.getStat().isExit())
                online.getBukkitPlayer().setScoreboard(board);
        }
    }

    public boolean isTechWin() {
        return techWin;
    }

    public Location getLobby() {
        return lobby;
    }

    public HashMap<String, PTeam> getTeams() {
        return teams;
    }
}
