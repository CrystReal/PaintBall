package com.updg.paintball.stats;

import java.util.List;

/**
 * Created by Alex
 * Date: 12.11.13  21:17
 */
public class playerStats {
    private int _playerId;
    private int _deaths;
    private int _team;
    private List<playerStatsKills> _victims;
    private long _timeInGame;
    private boolean _tillFinish;

    public int getPlayerId() {
        return _playerId;
    }

    public void setPlayerId(int _playerId) {
        this._playerId = _playerId;
    }

    public int getDeaths() {
        return _deaths;
    }

    public void setDeaths(int _deaths) {
        this._deaths = _deaths;
    }

    public long getTimeInGame() {
        return _timeInGame;
    }

    public void setTimeInGame(long _timeInGame) {
        this._timeInGame = _timeInGame;
    }

    public List<playerStatsKills> getVictims() {
        return _victims;
    }

    public void setVictims(List<playerStatsKills> _victims) {
        this._victims = _victims;
    }

    public boolean isTillFinish() {
        return _tillFinish;
    }

    public void setTillFinish(boolean _tillFinish) {
        this._tillFinish = _tillFinish;
    }

    public int getTeam() {
        return _team;
    }

    public void setTeam(int _team) {
        this._team = _team;
    }
}
