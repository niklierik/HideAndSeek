package me.fiveship.hideandseek.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Settings {

    @JsonProperty("CanSelectSpawn")
    public Boolean canSelectSpawn = null;
    @JsonProperty("CanSelectBlock")
    public Boolean canSelectBlock = null;
    @JsonProperty("CanChangeBlock")
    public Boolean canChangeBlock = null;
    @JsonProperty("WaitingTime")
    public Integer waitingTime = null;
    @JsonProperty("StartingTime")
    public Integer startingTime = null;
    @JsonProperty("HideTime")
    public Integer hideTime = null;
    @JsonProperty("SeekTime")
    public Integer seekTime = null;
    @JsonProperty("EndTime")
    public Integer endTime = null;
    @JsonProperty("NoPlayersForStartAsWaiting")
    public Integer waiting_startNo = null;
    @JsonProperty("NoPlayersForStartAsStarting")
    public Integer start_startNo = null;
    @JsonProperty("TurningToBlockTime")
    public Integer turningToBlockTime = null;
    @JsonProperty("MoveAsPlayer")
    public Boolean moveAsPlayer = null;
    @JsonProperty("SeekerHealth")
    public Double seekerHP = null;
    @JsonProperty("HiderHealth")
    public Double hiderHP = null;
    @JsonProperty("MiningFatigueForSeeker")
    public Integer seekerMiningFatigue = null;
    @JsonProperty("MiningFatigueForHider")
    public Integer hiderMiningFatigue = null;
    @JsonProperty("DeadCanSeek")
    public Boolean deadCanSeek = null;
    @JsonProperty("MaxSeeker")
    public Integer maxSeeker = null;
    /**
     * Let n be this value. (def 5)<br>
     * Let h be the number of hiders.<br>
     * Let s be the number of seekers.<br>
     * Let p be the number of players. (p = s + h)<br>
     * s = (int)(p / n) + 1;<br>
     * So with default value (5)<br>
     * 2-4 players: 1 seeker<br>
     * 5-9 players: 2 seeker<br>
     * 10-14 players: 3 seeker<br>
     * ...
     */
    @JsonProperty("SeekersByPlayer")
    public Integer seekersByPlayer = 5;
    @JsonProperty("MaxPlayers")
    public Integer maxPlayers = -1;


    @JsonCreator
    public Settings() {

    }

    public void setDefault() {
        canSelectSpawn = true;
        canSelectBlock = true;
        canChangeBlock = false;
        waitingTime = 30;
        startingTime = 5;
        hideTime = 60;
        seekTime = 300;
        endTime = 10;
        waiting_startNo = 3;
        start_startNo = 15;
        turningToBlockTime = 3;
        moveAsPlayer = false;
        seekerHP = 5.0;
        hiderHP = 2.0;
        seekerMiningFatigue = 0;
        hiderMiningFatigue = 0;
        deadCanSeek = false;
        maxSeeker = -1;
    }

    public Settings(Settings s) {
        for (var field : getClass().getFields()) {
            try {
                field.set(s, field.get(this));
            } catch (Exception ignored) {
            }
        }
    }

    public Settings copy() {
        return new Settings(this);
    }

    public void keepNotNull(Settings global) {
        for (var field : getClass().getFields()) {
            try {
                var current = field.get(this);
                if (current == null) {
                    field.set(this, field.get(global));
                }
            } catch (Exception ignored) {
            }
        }
    }
}
